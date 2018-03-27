package navigation;

import lejos.hardware.port.Port;
import odometer.*;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.*;


// TODO: Auto-generated Javadoc
/**
 * The Class UltrasonicLocalizer.
 */
public class UltrasonicLocalizer extends Thread /* implements Runnable */{

	/** The odometer. */
	private Odometer odometer;
	
	/** The data. */
	private OdometerData data;

	/** The left motor. */
	private EV3LargeRegulatedMotor leftMotor;
	
	/** The right motor. */
	private EV3LargeRegulatedMotor rightMotor;

	/** The us. */
	private SampleProvider us;
	
	/** The us data. */
	private float usData[];

	/** The filter control. */
	private int filterControl = 0;
	
	/** The offset. */
	private static int offset = 40;

	/** The max. */
	private static int MAX = 65;

	/** The backwall. */
	private boolean backwall = false;
	
	/** The leftwall. */
	private boolean leftwall = false;

	/** The error. */
	private int error = 3;

	/** The a speed. */
	private static int A_SPEED = 100;

	/** The Rotationspeed. */
	private static int Rotationspeed = 150;

	/** The d. */
	private int d = 40; 
	
	/** The k. */
	private int k = 1; // +/- error in the d value above in cm.



	

	
	
	/**
	 * Instantiates a new ultrasonic localizer.
	 *
	 * @param leftMotor the left motor
	 * @param rightMotor the right motor
	 * @param odo the odo
	 * @param us the us
	 * @param usData the us data
	 */
	public UltrasonicLocalizer(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, Odometer odo,
			SampleProvider us, float usData[]) {

		this.odometer = odo;

		this.leftMotor = leftMotor;

		this.rightMotor = rightMotor;

		this.us = us;

		this.usData = usData;
		

	}



	/**
	 * Gets the distance.
	 *
	 * @return the distance
	 */
	public double getdistance() {

		us.fetchSample(usData, 0);
		float kk = (float) (usData[0] * 100);

		return kk;

	}

	
	/**
	 * Turn fallingedge.
	 */
	public void turnFallingedge() {  // far to near

		// turn left all the way round && don'twait for finish turn
		
		
		DpmFinal.leftMotor.setSpeed(Rotationspeed);
		DpmFinal.rightMotor.setSpeed(Rotationspeed);
		
		leftMotor.rotate( -convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, 720),true);
		rightMotor.rotate( convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, 720),true);

 		  

		while (getdistance() > d - k) {

			// keep turning

		}

		Sound.beep();// see first wall

		
		leftMotor.stop(true);
		rightMotor.stop(true);

		odometer.setTheta(0);

		
		
		//adjustment to ensure  escaping the falling edge
		leftMotor.rotate( convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, 60),true);
		rightMotor.rotate( -convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, 60),false);

		// turn left all the way round && don'twait for finish turn
		leftMotor.rotate( convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, 720),true);
		rightMotor.rotate( -convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, 720),true);
		

		while (getdistance() > d - k) {

			// let robot keep Turing right if the 

		}

		Sound.beep();// see second wall.

		// stop if sees the wall
		leftMotor.stop(true);
		rightMotor.stop(true);
		
		double heading = odometer.getXYT()[2] / 2;

		odometer.setTheta(50 + heading);

		turnTo(0, true);

	}
	
	
	
	
	  /**
  	 * This function turns to the theta as inputted, It always rotate with the minimal angle .
  	 *
  	 * @param theta   Desired theta to be rotated at in degrees.
  	 */



	public void turnTo(double theta, boolean usLocalize) {
		

		double currentTheta = odometer.getXYT()[2];

		
		double dTheta = theta - currentTheta;
		
		
		DpmFinal.leftMotor.setSpeed(Rotationspeed);
		DpmFinal.rightMotor.setSpeed(Rotationspeed);

		
		if (dTheta < 0)
			dTheta = 360 + dTheta;

	
		if (dTheta <= 180) {
			
			//we want minimal angle
		
			
			DpmFinal.leftMotor.rotate(
					convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, dTheta), true);
			
			DpmFinal.rightMotor.rotate(
					-convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, dTheta),
					false);

		}
		
		else {
			//if it is maximum angle, Find minimal angle
			dTheta = 360 - dTheta;

		
			
			DpmFinal.leftMotor
					.rotate(-convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK,
							dTheta), true);
			
			DpmFinal.rightMotor
					.rotate(convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, dTheta),
							false);
		}

	}  
	
	

	
	



	/**
	 * Function that calculates how many degrees the wheels must rotate to cover
	 * a particular distance.
	 * 
	 * @param wheelRad
	 *            Radius of the wheel in cm.
	 * @param distance
	 *            Distance to be covered in cm.
	 * @return Returns the amount of degrees the wheel must turn to in order to
	 *         cover the inputted distance.
	 */

	private static int convertDistance(double wheelRad, double distance) {

		return (int) ((180.0 * distance) / (Math.PI * wheelRad));
	}

	/**
	 * Function that calculates the amount of degrees of rotation needed to turn
	 * a particular degree.
	 * 
	 * @param radius
	 *            Radius of the wheels in cm.
	 * @param width
	 *            Largeness of the robot in cm. Also known as the Track value.
	 * @param angle
	 *            Desired amount of angle to be turned in deg.
	 * @return Returns the amount of degree the wheels must turn in order for
	 *         the robot to rotate the inputted amount of degrees.
	 */

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}