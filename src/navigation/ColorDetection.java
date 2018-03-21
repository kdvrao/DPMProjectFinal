package navigation;

import odometer.*;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.Sound;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

// TODO: Auto-generated Javadoc
/**
 * The Class colorDetection.
 */
public class ColorDetection extends Thread {
	
	/** The Constant RED_R_MEEN. */
	//TODO: get actual values
		private final static double RED_R_MEEN=0.12396416;
		
		/** The Constant RED_G_MEEN. */
		private final static double RED_G_MEEN=0.02064270;
		
		/** The Constant RED_B_MEEN. */
		private final static double RED_B_MEEN=0.01372543;
		
		/** The Constant RED_R_STD. */
		private final static double RED_R_STD=0.050752937;
		
		/** The Constant RED_G_STD. */
		private final static double RED_G_STD=0.007693734;
		
		/** The Constant RED_B_STD. */
		private final static double RED_B_STD=0.005220381;
		
		/** The Constant YELLOW_R_MEEN. */
		private final static double YELLOW_R_MEEN=0.215953659;
		
		/** The Constant YELLOW_G_MEEN. */
		private final static double YELLOW_G_MEEN=0.12932264;
		
		/** The Constant YELLOW_B_MEEN. */
		private final static double YELLOW_B_MEEN=0.022103387;
		
		/** The Constant YELLOW_R_STD. */
		private final static double YELLOW_R_STD=0.089106906;
		
		/** The Constant YELLOW_G_STD. */
		private final static double YELLOW_G_STD=0.060347026;
		
		/** The Constant YELLOW_B_STD. */
		private final static double YELLOW_B_STD=0.009508931;
		
		/** The Constant WHITE_R_MEEN. */
		private final static double WHITE_R_MEEN=0.18946119;
		
		/** The Constant WHITE_G_MEEN. */
		private final static double WHITE_G_MEEN=0.18917648;
		
		/** The Constant WHITE_B_MEEN. */
		private final static double WHITE_B_MEEN=0.12824939;
		
		/** The Constant WHITE_R_STD. */
		private final static double WHITE_R_STD=0.095847914;
		
		/** The Constant WHITE_G_STD. */
		private final static double WHITE_G_STD=0.089227586;
		
		/** The Constant WHITE_B_STD. */
		private final static double WHITE_B_STD=0.056484251;
		
		/** The Constant BLUE_R_MEEN. */
		private final static double BLUE_R_MEEN=0.02340686;
		
		/** The Constant BLUE_G_MEEN. */
		private final static double BLUE_G_MEEN=0.04534314;
		
		/** The Constant BLUE_B_MEEN. */
		private final static double BLUE_B_MEEN=0.05202206;
		
		/** The Constant BLUE_R_STD. */
		private final static double BLUE_R_STD=0.018837398;
		
		/** The Constant BLUE_G_STD. */
		private final static double BLUE_G_STD=0.016473312;
		
		/** The Constant BLUE_B_STD. */
		private final static double BLUE_B_STD=0.016708785;
		

	
	/** The Constant colorSensor. */
	private static final EV3ColorSensor colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S2"));
	
	/** The color value. */
	private SampleProvider colorValue;
	
	/** The us. */
	private SampleProvider us;
	
	/** The color data. */
	private float[] colorData;
	
	/** The us poller. */
	private UltrasonicPoller usPoller;
	
	/** The t. */
	private TextLCD t;
	
	/** The us data. */
	private float[] usData;
	
	/** The navigator. */
	private Navigation navigator;
	
	

	/**
	 * Instantiates a new color detection.
	 *
	 * @param us the us
	 * @param usData the us data
	 * @param t the t
	 * @return 
	 */
	public ColorDetection(SampleProvider us, float usData[], TextLCD t) { // when calling colorDetection only, pass in the front color sensor
		this.us = us;
		this.usData = usData;
		this.t = t;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		
		determineBlockColour();
		
	}
	
	/**
	 * Prints the color of the block .
	 *
	 * @return void
	 */
	public void determineBlockColour(){
		boolean blue;
		boolean red;
		boolean yellow;
		boolean white;
		boolean block;
		 
		SensorMode idColour;
	      idColour = colorSensor.getRGBMode();
	      //System.out.println("set mode");
	      
	      
	      float[] sample = new float[idColour.sampleSize()];
	      idColour.fetchSample(sample, 0);
		
	      //System.out.println("detect block");
	      long t1 = System.currentTimeMillis();
	      while(true){
	    	  
	    	  long t2 = System.currentTimeMillis(); 
	    	  
	    	  if ((t2-t1) > 10000)
	    		  break;
	    	  
	    	  block = detectBlock();
	    	  //add return to dip out
	    	  if(block){
				t.drawString("Encountered object", 0, 0);
				us.fetchSample(usData, 0);
				float distance = (float) (usData[0] * 100);
				if(distance < 5){ // check for the colors
					Sound.beep();
					blue = isBlue();
					red = isRed();
					yellow = isYellow();
					white = isWhite();
					
					
				
				
				if(white){					
					t.drawString("block is white", 0, 1);
					Sound.twoBeeps();
					break;
				} else if (red){
					t.drawString("block is red", 0, 1);
					Sound.twoBeeps();
					break;
					
				}else if (yellow){
					t.drawString("block is yellow", 0, 1);
					Sound.twoBeeps();
					break;
					
				} else if (blue){
					Sound.beep();
					t.drawString("block is blue", 0, 1);
					break;
					
					
				} else {
					t.drawString("nothing", 0, 1);
					Sound.twoBeeps();
					break;
				
				}
						
			}
		}
	      }
		
		
	}

	/**
	 * Checks for the presence of a block.
	 *
	 * @return boolean
	 */
	public boolean detectBlock() {
		us.fetchSample(usData, 0);
		float distance = (float) (usData[0] * 100);
		//System.out.println(distance);
		if(distance < 20){
			//Sound.beep();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks is block is blue.
	 *
	 * @return boolean
	 */
	public boolean isBlue() {
		 SensorMode idColour;
	      idColour = colorSensor.getRGBMode();
	      
	      
	      float[] sample = new float[idColour.sampleSize()];
	      idColour.fetchSample(sample, 0);
	      //aftergetting value
	      //calculate stddeviation
	     // System.out.println("red value is " + sample[0] * 1000 + "Green value is " + sample[1] * 1000 + "Blue value is " + sample[2] * 1000 );
	      
	     //0.001960 or 9.803922
		if(sample[0]< 0.01 && sample[1]< 0.01 && sample[2]>= 0.01){//change for blue
			return true;
		}
		return false;
	}
	
	/**
	 * Checks is block is red.
	 *
	 * @return boolean
	 */
	public boolean isRed() {
		SensorMode idColour;
	      idColour = colorSensor.getRGBMode();
		float[] sample = new float[idColour.sampleSize()];
	      idColour.fetchSample(sample, 0);
		
		if(sample[0] >= 0.010764 && sample[0]<=0.021568  && sample[1]>= 0.001960 && sample[1]<=0.003921 && sample[2]>= 0.003921 && sample[2] <= 0.006882){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks is block is yellow.
	 *
	 * @return boolean
	 */
	public boolean isYellow() {
		SensorMode idColour;
	      idColour = colorSensor.getRGBMode();
		float[] sample = new float[idColour.sampleSize()];
	      idColour.fetchSample(sample, 0);
		
		if(sample[0] >= 0.011645 && sample[0]<= 0.034313 && sample[1]>= 0.007823 && sample[1]<=0.21568 && sample[2]>= 0.002921 && sample[2] <= 0.006862){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks is block is white.
	 *
	 * @return boolean
	 */
	public boolean isWhite() {
		SensorMode idColour;
	      idColour = colorSensor.getRGBMode();
		float[] sample = new float[idColour.sampleSize()];
	      idColour.fetchSample(sample, 0);
		if(sample[0] >= 0.018607 && sample[0]<= 0.049019 && sample[1]>= 0.016666 && sample[1]<=0.038235 && sample[2]>= 0.027450 && sample[2] <= 0.051960){
			return true;
		}
		return false;
	}
}