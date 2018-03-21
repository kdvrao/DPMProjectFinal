package odometer;

// TODO: Auto-generated Javadoc
/**
 * This class is used to handle errors regarding the singleton pattern used for the odometer and
 * odometerData.
 */
@SuppressWarnings("serial")
public class OdometerExceptions extends Exception {

  /**
   * Instantiates a new odometer exceptions.
   *
   * @param Error the error
   */
  public OdometerExceptions(String Error) {
    super(Error);
  }

}