package navigation;

import lejos.robotics.SampleProvider;

// TODO: Auto-generated Javadoc
/**
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20mS, and that the thread sleeps for 50 mS at the end of each loop, then
 * one cycle through the loop is approximately 70 mS. This corresponds to a sampling rate of 1/70mS
 * or about 14 Hz.
 */
public class UltrasonicPoller extends Thread {
  
  /** The us. */
  private SampleProvider us;
  
  /** The cont. */
  private UltrasonicController cont;
  
  /** The us data. */
  private float[] usData;

  /**
   * Instantiates a new ultrasonic poller.
   *
   * @param us the us
   * @param usData the us data
   * @param cont the cont
   */
  public UltrasonicPoller(SampleProvider us, float[] usData, UltrasonicController cont) {
    this.us = us;
    this.cont = cont;
    this.usData = usData;
  }

  /*
   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer
   * [0,255] (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    int distance;
    while (true) {
      us.fetchSample(usData, 0); // acquire data
      distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
      cont.processUSData(distance); // now take action depending on value
      try {
        Thread.sleep(50);
      } catch (Exception e) {
      } // Poor man's timed sampling
    }
  }
  
  /**
	 * The implemetation of a simple filter that only returns values larger than
	 * 50 units.
	 * 
	 * @author kiren&sarah
	 * 
	 * @return float
	 */
	public float getFilteredData() {
		us.fetchSample(usData, 0);
		float distance = usData[0];
		distance = (int) (usData[0] * 100.0);
		// Rudimentary filter
		if (distance > 50) {
			distance = 255;
		}
		return distance;
	}

}
