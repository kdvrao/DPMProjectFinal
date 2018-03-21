/**
 * This class is meant as a skeleton for the odometer class to be used.
 * 
 * @author Rodrigo Silva
 * @author Dirk Dubois
 * @author Derek Yu
 * @author Karim El-Baba
 * @author Michael Smith
 */

package odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

// TODO: Auto-generated Javadoc
/**
 * The Class Odometer.
 */
public class Odometer extends OdometerData implements Runnable {

  /** The odo data. */
  public OdometerData odoData;
  
  /** The odo. */
  private static Odometer odo = null; // Returned as singleton

  /** The left motor tacho count. */
  // Motors and related variables
  private int leftMotorTachoCount;
  
  /** The right motor tacho count. */
  private int rightMotorTachoCount;
  
  /** The previousleft motor tacho count. */
  private int previousleftMotorTachoCount;
  
  /** The previousright motor tacho count. */
  private int previousrightMotorTachoCount;
  
  /** The left motor. */
  private EV3LargeRegulatedMotor leftMotor;
  
  /** The right motor. */
  private EV3LargeRegulatedMotor rightMotor;
  
  /** The track. */
  private final double TRACK;
  
  /** The wheel rad. */
  private final double WHEEL_RAD;
  
  /** The rightdisplacement. */
  private double rightdisplacement;
  
  /** The leftdisplacement. */
  private double leftdisplacement;
  
  /** The Theta. */
  private double Theta;
  
  /** The x. */
  private double X;
  
  /** The y. */
  private double Y;

  
  

  /** The position. */
  private double[] position;


	/** The Constant ODOMETER_PERIOD. */
	private static final long ODOMETER_PERIOD = 25; // odometer update period in ms

  /**
   * This is the default constructor of this class. It initiates all motors and variables once.It
   * cannot be accessed externally.
   *
   * @param leftMotor the left motor
   * @param rightMotor the right motor
   * @param TRACK the track
   * @param WHEEL_RAD the wheel rad
   * @throws OdometerExceptions the odometer exceptions
   */
  private Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      final double TRACK, final double WHEEL_RAD) throws OdometerExceptions {
    odoData = OdometerData.getOdometerData(); // Allows access to x,y,z
                                              // manipulation methods
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;

    // Reset the values of x, y and z to 0
    odoData.setXYT(0, 0, 0);

    this.leftMotorTachoCount = 0;
    this.rightMotorTachoCount = 0;

    this.TRACK = TRACK;
    this.WHEEL_RAD = WHEEL_RAD;

  }

  /**
   * This method is meant to ensure only one instance of the odometer is used throughout the code.
   *
   * @param leftMotor the left motor
   * @param rightMotor the right motor
   * @param TRACK the track
   * @param WHEEL_RAD the wheel rad
   * @return new or existing Odometer Object
   * @throws OdometerExceptions the odometer exceptions
   */
  public synchronized static Odometer getOdometer(EV3LargeRegulatedMotor leftMotor,
      EV3LargeRegulatedMotor rightMotor, final double TRACK, final double WHEEL_RAD)
      throws OdometerExceptions {
    if (odo != null) { // Return existing object
      return odo;
    } else { // create object and return it
      odo = new Odometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
      return odo;
    }
  }

  /**
   * This class is meant to return the existing Odometer Object. It is meant to be used only if an
   * odometer object has been created
   *
   * @return error if no previous odometer exists
   * @throws OdometerExceptions the odometer exceptions
   */
  public synchronized static Odometer getOdometer() throws OdometerExceptions {

    if (odo == null) {
      throw new OdometerExceptions("No previous Odometer exits.");

    }
    return odo;
  }

  /**
   * This method is where the logic for the odometer will run. Use the methods provided from the
   * OdometerData class to implement the odometer.
   */
  // run method (required for Thread)
  public void run() {
    long updateStart, updateEnd;

    while (true) {
      updateStart = System.currentTimeMillis();
      leftMotorTachoCount = leftMotor.getTachoCount();
      rightMotorTachoCount = rightMotor.getTachoCount();
      Theta = getXYT()[2];
      
     
      
      
      
      rightMotorTachoCount = rightMotor.getTachoCount(); 
      leftdisplacement = 3.14159*WHEEL_RAD*(leftMotorTachoCount-previousleftMotorTachoCount)/180;// get the displacement traveled by left wheel     
      rightdisplacement = 3.14159*WHEEL_RAD*(rightMotorTachoCount-previousrightMotorTachoCount)/180;   //get the displacement traveled by right wheel
      
      double deltaD = 0.5*(leftdisplacement+rightdisplacement);  //total left + right displacement     
      double deltaT = (leftdisplacement-rightdisplacement)/TRACK;  //theta 
      Theta += deltaT;
      double dX = deltaD * Math.sin(Math.toRadians(Theta));// getting x component of distance traveled based on theta
      double dY = deltaD * Math.cos(Math.toRadians(Theta));  //getting y component of distance traveled based on theta
 

      
      // TODO Update odometer values with new calculated values
      //odo.update(0.5, 1.8, 20.1);
      odo.update(dX, dY, deltaT *(180/ Math.PI) );//added -ve here
      double[] position = odo.getXYT();
      previousleftMotorTachoCount=leftMotorTachoCount;  //update to get next distance for left     
      previousrightMotorTachoCount=rightMotorTachoCount; //update to get next distance for right
      // this ensures that the odometer only runs once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < ODOMETER_PERIOD) {
        try {
          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          // there is nothing to be done
        }
      }
    }
  }

}