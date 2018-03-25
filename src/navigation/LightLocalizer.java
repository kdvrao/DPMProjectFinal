package navigation;
import odometer.*;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
// TODO: Auto-generated Javadoc
/**
 * The Class LightLocalizer.
 */
public class LightLocalizer {
 /** The odometer. */
 private static Odometer odometer;
 /** The ls. */
 private EV3ColorSensor LS;
 /** The left motor. */
 private static EV3LargeRegulatedMotor leftMotor;
 
 /** The right motor. */
 private static EV3LargeRegulatedMotor rightMotor;
 /** The Constant OFFSET. */
 private static final double OFFSET = -14;
 /** The Rotationspeed. */
 private static int Rotationspeed = 70;
 /** The forwardspeed. */
 private static int forwardspeed = 100;
 /**
  * Instantiates a new light localizer.
  *
  * @param leftMotor the left motor
  * @param rightMotor the right motor
  * @param odometer the odometer
  * @param ls the ls
  * @throws OdometerExceptions the odometer exceptions
  */
 public LightLocalizer(EV3LargeRegulatedMotor leftMotor,
   EV3LargeRegulatedMotor rightMotor, Odometer odometer,
   EV3ColorSensor ls) throws OdometerExceptions {
  this.leftMotor = leftMotor;
  this.rightMotor = rightMotor;
  this.LS = ls;
  this.odometer = odometer;
 }
 /**
  * Localization.
  */
 public void localization() {
 
  double Xplus, Yplus, Xminus, Yminus, thetax, thetay, dx, dy, dtheta;
  double[] angle = new double[4];
  turnTo(45);
  // move 15 cm near the 0,0
  leftMotor.rotate(convertDistance(DpmFinal.WHEEL_RAD, 15), true);
  rightMotor.rotate(convertDistance(DpmFinal.WHEEL_RAD, 15), false);
  leftMotor.setSpeed(Rotationspeed);
  rightMotor.setSpeed(Rotationspeed);
  // turn right continuously
  leftMotor.forward();
  rightMotor.backward();
  int i = 0;


  LS.setCurrentMode("Red"); //set Sensor to use red light alone
  
  float[] currentSample = new float[1];

  while (i < 4) {
   // see the black line

	  LS.fetchSample(currentSample, 0);
	  
       if (currentSample[0] < 0.27) {
    	   Sound.beepSequence();
    	   angle[i] = odometer.getXYT()[2];
	    try {
	    	Thread.sleep(400);
	    	} catch (InterruptedException e) {
	    }
	    
	    i++;
       }
  }
  
  Xminus = angle[0];
  Yplus = angle[1];
  Xplus = angle[2];
  Yminus = angle[3];
  thetax = (Xminus - Xplus) / 2;
  thetay = (Yplus - Yminus) / 2;
  dx = OFFSET * Math.cos(Math.toRadians(thetax));
  dy = OFFSET * Math.cos(Math.toRadians(thetay));
  odometer.setX(dx);
  odometer.setY(dy);
  travelTo(0, 0);
  Sound.beep();
   turnTo(0);
  //turnTo(19);
  
  
  
  //leftMotor.rotate(convertDistance(Lab4.WHEEL_RAD, 5), true);
  //rightMotor.rotate(convertDistance(Lab4.WHEEL_RAD, 5), false);
 }
 
 
 /**
  * This function let the robot travel to the inputted position.
  *
  * @param x  X-coordinate of the destination
  *
  * 
  * @param y  y-coordinate of the destination
  */
 
 
 
 public void travelTo(double x, double y) {
  double initialX = odometer.getXYT()[0];
  double initialY = odometer.getXYT()[1];
  double deltaX = (x*30.41) - initialX;// distance need to be travelled in the x
          // direction
  double deltaY = (y*30.41) - initialY;// distance need to be travelled in the y
          // direction
  double destYheta;
  if (deltaY > 0) { // forward -90deg to 90deg
   destYheta = (Math.atan(deltaX / deltaY) * 180 / Math.PI);
  }
  else { // backward
   destYheta = (Math.atan(deltaX / deltaY) * 180 / Math.PI) + 180;
  }
  double distance = Math.sqrt(Math.pow((deltaX), 2)
    + Math.pow((deltaY), 2));
  // double newTheta = Math.atan2(deltaX, deltaY) - initialTheta;
  turnTo(destYheta);
  leftMotor.setSpeed(forwardspeed);
  rightMotor.setSpeed(forwardspeed);
  leftMotor.rotate(convertDistance(DpmFinal.WHEEL_RAD, distance), true);
  rightMotor.rotate(convertDistance(DpmFinal.WHEEL_RAD, distance), false);
 }
 
 
 
 /**
  * This function turns to the theta as inputted, It always rotate with the minimal angle .
  *
  * @param theta   Desired theta to be rotated at in degrees.
  */
 public static void turnTo(double theta) {
  double currentTheta = odometer.getXYT()[2];
  double dTheta = theta - currentTheta;
  if (dTheta < 0)
   dTheta = 360 + dTheta;
  if (dTheta <= 180) {
   leftMotor.setSpeed(Rotationspeed);
   rightMotor.setSpeed(Rotationspeed);
   leftMotor.rotate(
     convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, dTheta), true);
   rightMotor.rotate(
     -convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK, dTheta),
     false);
  }
  else {
   // Find minimal angle
   dTheta = 360 - dTheta;
   leftMotor.setSpeed(Rotationspeed);
   rightMotor.setSpeed(Rotationspeed);
   leftMotor
     .rotate(-convertAngle(DpmFinal.WHEEL_RAD, DpmFinal.TRACK,
       dTheta), true);
   rightMotor
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