package navigation;



import odometer.*;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.Sound;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class searchForBlock.
 */
public class SearchForBlock extends Thread  {
	
	
	/** The us. */
	private UltrasonicPoller US;
	
	/** The odo. */
	private Odometer odo;
	
	/** The count. */
	public static int cnt;
	
	/**
	 * Instantiates a new search for block.
	 *
	 * @param odo the odo
	 */
	public SearchForBlock (Odometer odo){

	}
	
	
/**
 * 	Searches for blocks in a specified location.
 *
 * @param LLC coordinates
 * @param URC coordinate
 * @throws OdometerExceptions the odometer exceptions
 */
public void run (int [] LLC, int [] URC) throws OdometerExceptions {

	
	int LLCXcord = LLC[0];
	int LLCYcord = LLC[1];
	int URCXcord = URC[0];
	int URCYcord = URC[1];
	
	
	//assuming that we are at the LLC of search area do this
	
	
	//generate array separate function
	int [][] map = generate2DArray(LLC, URC);
	
	
	
	//go through all the squares and update the 2d array accordingly
	Odometer odometer = Odometer.getOdometer(DpmFinal.leftMotor, DpmFinal.rightMotor, DpmFinal.TRACK, DpmFinal.WHEEL_RAD);
	NavigatorAvoid navigatorAvoid = new NavigatorAvoid(odometer, US);
	
	int blockSize =30;
	
		for (int x =LLCXcord; x<=URCXcord ; x++){ //go along the x direction
		for (int y =LLCYcord; y<=URCYcord; y++ ){// go along the y direction
				
				cnt = 0; // counter intitialized to make sure that the robot does not try to reach the same point more than twice
				
				navigatorAvoid.travelTo(x*blockSize, y*blockSize); //avoids blocks and detects blocks 
				
		}
	}
	
	
}

/**
 * Creates a 2D map based on the lower left hand corner and upper right hand corner.
 *
 * @param LLC the llc
 * @param URC the urc
 * @return int [][]
 */
public int[][] generate2DArray(int [] LLC, int [] URC){
	
	int width = URC[0] - LLC[0];
	int height = URC[1] - LLC[1];
	
	int [][] map = new int [width] [height];
	return map;
	
}



	
}