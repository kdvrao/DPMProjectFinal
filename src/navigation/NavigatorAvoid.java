package navigation;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.motor.Motor;
import lejos.robotics.SampleProvider;
import odometer.*;

// TODO: Auto-generated Javadoc
/**
 * The Class NavigatorAvoid.
 */
public class NavigatorAvoid extends Thread {
	
	/** The wheel radius. */
	double wheel_radius = DpmFinal.WHEEL_RAD;
	
	/** The width. */
	double width = DpmFinal.TRACK;
	
	/** The us. */
	private UltrasonicPoller US;
	
	/** The sensor motor. */
	private EV3LargeRegulatedMotor sensorMotor;
	
	/** The Constant FORWARD_SPEED. */
	private static final int FORWARD_SPEED = 250;
	
	/** The Constant ROTATE_SPEED. */
	private static final int ROTATE_SPEED = 170;
	
	/** The Constant ERROR_DIST. */
	private static final int ERROR_DIST = 1;
	
	/** The Constant FILTER_DIST. */
	private static final int FILTER_DIST = 20;
	
	/** The Constant FILTER. */
	private static final int FILTER = 10;
	
	/** The Constant CHEAT_DISTANCE. */
	private static final double CHEAT_DISTANCE = 30;
	
	/** The Constant TURN_ANGLE. */
	private static final double TURN_ANGLE = 90;
	
	/** The firstpath complete. */
	int firstpath_complete = 0;
	
	/** The odometer. */
	// ODOMETER CONSTANTS:
	private Odometer odometer;
	
	/** The odo theta. */
	public double odo_x, odo_y, odo_theta;
	
	/** The theta dest. */
	public double x_dest, y_dest, theta_dest;
	
	/** The navigation. */
	Navigation navigation = new Navigation(odometer);

	/**
	 * Inititalizes the odometer for use in the rest on the class.
	 *
	 * @author Kiren&Sarah
	 * @param odometer            Odometer
	 * @param US            UltrasonicPoller
	 */

	public NavigatorAvoid(Odometer odometer, UltrasonicPoller US) {
		this.odometer = odometer;
	}

	/**
	 * Runs the code needed for the robot to travel to the various coordinates.
	 *
	 * @return void
	 */

	@Override
	public void run() {
		travelTo(0, 60);
		travelTo(30, 30);
		travelTo(60, 60);
		travelTo(60, 30);
		travelTo(30, 0);
	}

	/**
	 * Takes the desired x and y coordinates and takes the robot to those
	 * specified coordinates. If Block is found while travelling it will also determine the color of the block
	 *
	 * @author kiren&sarah
	 * @param x the x
	 * @param y            double
	 * @return void
	 */
	public void travelTo(double x, double y) {
		
		
		if (SearchForBlock.cnt > 1) {
			return;
		}
		

		double[] gettingValues = odometer.getXYT(); // get the values of x,y,
													// and theta from the
													// odometer
		double theta = gettingValues[2];
		double odox = gettingValues[0];
		double odoy = gettingValues[1];

		odo_x = odox; // setting the global variables to the values obtained
						// from the odometer
		odo_y = odoy;
		odo_theta = theta;

		x_dest = x; // seting the values of the desired coordinates taken from
					// the parameters
		y_dest = y;

		double delta_y = y_dest - odo_y;
		double delta_x = x_dest - odo_x;

		double theta_dest = Math.toDegrees(Math.atan2(delta_x, delta_y)); // calculating
																			// the
																			// proper
																			// orientation
																			// of
																			// the
																			// robot
																			// by
																			// calculating
																			// the
																			// arctan
																			// of
																			// the
																			// x
																			// and
																			// y
																			// components
		double travelDist = Math.hypot(delta_x, delta_y); // calculating the
															// hyponetus based
															// on the x and y
															// coordinates

		double theta_corr = theta_dest - odo_theta; // ensuring the theta angle
													// is corrected based on the
													// robots orientation

		if (theta_corr < -180) { // ensuring the robot takes the smallest angle
			turnTo(theta_corr + 360);
		} else if (theta_corr > 180) {

			turnTo(theta_corr - 360);
		} else {
			turnTo(theta_corr);
		}
		drive(travelDist);

	}

	/**
	 * takes the distance passed as the parameter and moves the robot in that
	 * direction.
	 *
	 * @author kiren&sarah
	 * @param distance            double
	 * @return void
	 */
	public void drive(double distance) {
		double[] gettingValues = odometer.getXYT();
		double theta = gettingValues[2];
		double xOdo = gettingValues[0];
		double yOdo = gettingValues[1];
		
		DpmFinal.leftMotor.setSpeed(FORWARD_SPEED);
		DpmFinal.rightMotor.setSpeed(FORWARD_SPEED);

		DpmFinal.leftMotor.rotate(convertDistance(wheel_radius, distance), true);
		DpmFinal.rightMotor.rotate(convertDistance(wheel_radius, distance), true);

		DpmFinal.usSensor.fetchSample(DpmFinal.usData, 0);
		double wall_dist = DpmFinal.usData[0] * 100; // taking the distance from the
													// ultrasonic sensor

		while (wall_dist > FILTER_DIST && DpmFinal.leftMotor.isMoving() && DpmFinal.rightMotor.isMoving()) {// ensure
																									// that
																									// robot
																									// is
																									// not
																									// close
																									// to
																									// a
																									// wall
																									// and
																									// the
																									// robot
																									// is
																									// still
																									// moving
																									// block
			DpmFinal.usSensor.fetchSample(DpmFinal.usData, 0);// keep getting values to
														// check
			wall_dist = DpmFinal.usData[0] * 100;
		}
		if (wall_dist < FILTER_DIST) {
			navigateAwayFromBlock();
		}

		
		

	}

	/**
	 * rotates the robot to the defined angle.
	 *
	 * @author kiren&sarah
	 * @param theta            double
	 * @return void
	 */
	public void turnTo(double theta) {
		DpmFinal.leftMotor.setSpeed(ROTATE_SPEED);
		DpmFinal.rightMotor.setSpeed(ROTATE_SPEED);
		DpmFinal.leftMotor.rotate(convertAngle(wheel_radius, width, theta), true);
		DpmFinal.rightMotor.rotate(-convertAngle(wheel_radius, width, theta), false);
	}

	/**
	 * (DEPRECATED) Boolean flag to check if robot is moving.
	 *
	 * @return boolean
	 */
	public boolean isNavigating() {
		return true;
	}

	/**
	 * Converts distance.
	 *
	 * @param radius            double
	 * @param distance            double
	 * @return int
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * Converts the angle from radians to degrees.
	 *
	 * @param radius            double
	 * @param width            double
	 * @param angle            double
	 * @return the int
	 */
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	/**
	 * starts the sequence to go around the block.
	 *
	 * @author kiren&sarah
	 * @return void
	 */
	public void navigateAwayFromBlock() {

		//TODO: add the logic for detection here
		//wince detetected go forward a little bit 
		//then determine color 
		//then reverse back to ensure good distance and then continue traveling
		
		DpmFinal.usSensor.fetchSample(DpmFinal.usData, 0);
		double wall_dist = DpmFinal.usData[0] * 100; // taking the distance from the
													// ultrasonic sensor
		
		//go forward a little bit
		
		DpmFinal.leftMotor.rotate(convertDistance(wheel_radius, 15), true); 
		DpmFinal.rightMotor.rotate(convertDistance(wheel_radius, 15), false);
		
		//check the color
		ColorDetection identify = new ColorDetection(DpmFinal.usDistance, DpmFinal.usData, DpmFinal.lcd);
		Thread colorclass = new Thread(identify);
		colorclass.start();
		identify.run();
		
		
		
		//move back to FILTER_DIST
		DpmFinal.leftMotor.rotate(convertDistance(wheel_radius, -15), true); 
		DpmFinal.rightMotor.rotate(convertDistance(wheel_radius, -15), false);
		
		//continue the navigation away from it
		
		
		
		
		Sound.beepSequenceUp();
		DpmFinal.leftMotor.setSpeed(ROTATE_SPEED);
		DpmFinal.rightMotor.setSpeed(ROTATE_SPEED);

		DpmFinal.leftMotor.rotate(-convertAngle(wheel_radius, width, TURN_ANGLE), true); // rotate
																					// the
																					// robot
																					// to
																					// the
																					// right
		DpmFinal.rightMotor.rotate(convertAngle(wheel_radius, width, TURN_ANGLE), false);

		DpmFinal.leftMotor.rotate(convertDistance(wheel_radius, CHEAT_DISTANCE), true); // goes
																					// straight
																					// to
																					// go
																					// passed
																					// block
		DpmFinal.rightMotor.rotate(convertDistance(wheel_radius, CHEAT_DISTANCE), false);

		DpmFinal.leftMotor.rotate(convertAngle(wheel_radius, width, TURN_ANGLE), true); // turn
																						// back
																						// to
																						// original
																						// direction
																						// before
																						// facing
																						// the
																						// block
		DpmFinal.rightMotor.rotate(-convertAngle(wheel_radius, width, TURN_ANGLE), false);
		
		
		DpmFinal.leftMotor.rotate(convertDistance(wheel_radius, CHEAT_DISTANCE), true);
		DpmFinal.rightMotor.rotate(convertDistance(wheel_radius, CHEAT_DISTANCE), false);
		
	
		
		SearchForBlock.cnt++;
		travelTo(x_dest, y_dest); //if the robot does not get to the point before it reaches the block it will constantly keep going to the same block

	}
}
