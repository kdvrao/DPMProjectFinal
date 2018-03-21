package navigation;

import odometer.*;

// TODO: Auto-generated Javadoc
/**
 * The Class Navigation.
 */
public class Navigation extends Thread {
	
	/** The wheel radius. */
	double wheel_radius = DpmFinal.WHEEL_RAD;
	
	/** The width. */
	double width = DpmFinal.TRACK;
	
	/** The Constant FORWARD_SPEED. */
	private static final int FORWARD_SPEED = 250;
	
	/** The Constant ROTATE_SPEED. */
	private static final int ROTATE_SPEED = 150;
	
	/** The odo theta. */
	public double odo_x,odo_y, odo_theta;
	
	/** The theta dest. */
	public double x_dest, y_dest, theta_dest;
	
	/** The odometer. */
	public Odometer odometer;
	
	/**
	 * Instantiates a new navigation.
	 *
	 * @param odometer the odometer
	 */
	public Navigation(Odometer odometer){ //constructor
		this.odometer = odometer;
	}
	
	/**
	 * Runs the code needed for the robot to travel to the various coordinates.
	 *
	 * @return void
	 */
	public void run(){
		travelTo(0,60);
		travelTo(30,30);
		travelTo(60,60);
		travelTo(60,30);
		travelTo(30,0);
	}
	
	/**
	 * Takes the desired x and y coordinates and takes the robot to those
	 * specified coordinates.
	 *
	 * @author kiren&sarah
	 * @param x the x
	 * @param y            double
	 * @return void
	 */
	public void travelTo(double x, double y){
		
		double[] gettingValues=odometer.getXYT();// get the values of x,y,
		// and theta from the
		// odometer
	    double theta = gettingValues[2];
	    double xOdo = gettingValues[0];
	    double yOdo = gettingValues[1];
	    
	    
		odo_x = xOdo;// setting the global variables to the values obtained
		// from the odometer
		odo_y = yOdo;
		odo_theta = theta;
		
		x_dest = x;// seting the values of the desired coordinates taken from
		// the parameters
		y_dest = y;
		
		
		double delta_y = y_dest-odo_y;
		double delta_x = x_dest-odo_x;
		
		
		theta_dest = Math.toDegrees(Math.atan2(delta_x,delta_y));// calculating
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
		
		
		double travelDist = Math.hypot(delta_x,delta_y);// calculating the
		// hyponetus based
		// on the x and y
		// coordinates

		
		
		
		double theta_corr = theta_dest - odo_theta;// ensuring the theta angle
		// is corrected based on the
		// robots orientation
		
		 
		if(theta_corr < -180){ // ensuring the robot takes the smallest angle
			turnTo(theta_corr + 360);
		}
		else if(theta_corr > 180){
			turnTo(theta_corr - 360);
		}
		else{
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
	public void drive(double distance){
		
		DpmFinal.leftMotor.setSpeed(FORWARD_SPEED);
		DpmFinal.rightMotor.setSpeed(FORWARD_SPEED);
						
		DpmFinal.leftMotor.rotate(convertDistance(wheel_radius, distance), true);
		DpmFinal.rightMotor.rotate(convertDistance(wheel_radius, distance), false);
	}
	
	/**
	 * rotates the robot to the defined angle.
	 *
	 * @author kiren&sarah
	 * @param theta            double
	 * @return void
	 */
	public void turnTo(double theta){
		
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
	public boolean isNavigating(){
		
		return true;
	}
	
	/**
	 * Converts distance.
	 *
	 * @param radius            double
	 * @param distance            double
	 * @return int
	 */
	public static int convertDistance(double radius, double distance) {
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
	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}

