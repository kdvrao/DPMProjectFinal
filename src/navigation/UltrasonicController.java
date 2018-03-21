package navigation;

// TODO: Auto-generated Javadoc
/**
 * The Interface UltrasonicController.
 */
public interface UltrasonicController {
	
	/**
	 * Process US data.
	 *
	 * @param distance the distance
	 */
	public void processUSData(int distance);

	  /**
  	 * Read US distance.
  	 *
  	 * @return the int
  	 */
  	public int readUSDistance();
	}