// Lab2.java

package navigation;

import odometer.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class Lab5.
 */
public class DpmFinal {

	/** The Constant leftMotor. */
	// Motor Objects, and Robot related parameters
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("A"));
	
	/** The Constant rightMotor. */
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("B"));
	
	/** The Constant lcd. */
	public static final TextLCD lcd = LocalEV3.get().getTextLCD();

	/** The Constant usPort. */
	private static final Port usPort = LocalEV3.get().getPort("S1");

	/** The ls port. */
	static Port lsPort = LocalEV3.get().getPort("S3");

	/** The ls. */
	static EV3ColorSensor LS = new EV3ColorSensor(lsPort);

	/** The Constant WHEEL_RAD. */
	public static final double WHEEL_RAD = 2.076;// wheel radius

	/** The Constant TRACK. */
	public static final double TRACK = 16;// 15.7
	
	
	
	/** The us sensor. */
	static SensorModes usSensor = new EV3UltrasonicSensor(usPort); 
	
	/** The us distance. */
	static SampleProvider usDistance = usSensor.getMode("Distance"); 
																
	/** The us data. */
	static float[] usData = new float[usDistance.sampleSize()];

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws OdometerExceptions the odometer exceptions
	 */
	public static void main(String[] args) throws OdometerExceptions {

		int buttonChoice;


		Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK,
				WHEEL_RAD); 

		UltrasonicLocalizer ultrasonicLocalizer = new UltrasonicLocalizer(
				leftMotor, rightMotor, odometer, usDistance, usData);
		
		LightLocalizer lightlocalizer = new LightLocalizer(leftMotor,
				 rightMotor, odometer,LS);
		
		Display odometryDisplay = new Display(lcd);
		
		
		// get wifi parameters
		
		double [] tunnelLL = {3, 5};
		double [] tunnelUR = {4, 7};
		double [] bridgeLL = {7, 5};
		double [] bridgeUR = {8, 7};

		do {
			// clear the display
			lcd.clear();

			// ask the user whether the motors should drive in a square or float
			lcd.drawString("< Left | Right  >", 0, 0);
			lcd.drawString(" for   |  for    ", 0, 1);
			lcd.drawString("Bridge | Tunnel  ", 0, 2);
			lcd.drawString(" then  |  then   ", 0, 3);
			lcd.drawString("Tunnel | Bridge  ", 0, 4);

			buttonChoice = Button.waitForAnyPress(); 
			
			// ASSUMING DEFAULT WHEEL BASE IS TUNNEL BASE
			
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {

			lcd.clear();
			
			// initialize odometer thread
			Thread odoThread = new Thread(odometer);
			odoThread.start();
			
			// initializer display thread
			Thread odoDisplayThread = new Thread(odometryDisplay);
			odoDisplayThread.start();
			
			// localize at corner
			ultrasonicLocalizer.turnFallingedge();
			lightlocalizer.localization(); 
			

			// change to bridge wheel base
			
			// traverse bridge from top to bottom
			lightlocalizer.travelTo(bridgeUR[0] - 0.5, bridgeUR[1] + 0.5);
			lightlocalizer.travelTo(bridgeLL[0] + 0.5, bridgeLL[1] - 0.5);
			
			// change back to tunnel wheel base
			
			// search algorithm
			
			// traverse tunnel from bottom to top
			lightlocalizer.travelTo(tunnelLL[0] + 0.5, tunnelLL[1] - 0.5);
			lightlocalizer.travelTo(tunnelUR[0] - 0.5, tunnelUR[1] + 0.5);

		} else {

			lcd.clear();

			// initialize odometer thread
			Thread odoThread = new Thread(odometer);
			odoThread.start();
			
			// initializer display thread
			Thread odoDisplayThread = new Thread(odometryDisplay);
			odoDisplayThread.start();

			// localize at corner
			ultrasonicLocalizer.turnFallingedge();
			lightlocalizer.localization(); 
			
			
			// traverse tunnel from bottom to top
			lightlocalizer.travelTo(tunnelLL[0] + 0.5, tunnelLL[1] - 0.5);
			lightlocalizer.travelTo(tunnelUR[0] - 0.5, tunnelUR[1] + 0.5);
			
			// search algorithm
			
			// change to bridge wheel base
			
			// traverse bridge from top to bottom
			lightlocalizer.travelTo(bridgeUR[0] - 0.5, bridgeUR[1] + 0.5);
			lightlocalizer.travelTo(bridgeLL[0] + 0.5, bridgeLL[1] - 0.5);
			
			// change back to tunnel wheel base
		}

		

		

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}
}