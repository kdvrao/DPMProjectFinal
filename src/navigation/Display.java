package navigation;

import java.text.DecimalFormat;

import odometer.*;
import lejos.hardware.lcd.TextLCD;

// TODO: Auto-generated Javadoc
/**
 * This class is used to display the content of the odometer variables (x, y, Theta).
 */
public class Display implements Runnable {

  /** The odo. */
  private Odometer odo;	
  
  /** The lcd. */
  private TextLCD lcd;
  
  /** The position. */
  private double[] position;
  
  /** The display period. */
  private final long DISPLAY_PERIOD = 25;
  
  /** The timeout. */
  private long timeout = Long.MAX_VALUE;

  /**
   * This is the class constructor.
   *
   * @param lcd the lcd
   * @throws OdometerExceptions the odometer exceptions
   */
  public Display(TextLCD lcd) throws OdometerExceptions {
    odo = Odometer.getOdometer();
    this.lcd = lcd;
  }

  /**
   * This is the overloaded class constructor.
   *
   * @param lcd the lcd
   * @param timeout the timeout
   * @throws OdometerExceptions the odometer exceptions
   */
  public Display(TextLCD lcd, long timeout) throws OdometerExceptions {
    odo = Odometer.getOdometer();
    this.timeout = timeout;
    this.lcd = lcd;
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public void run() {
    
    lcd.clear();
    
    long updateStart, updateEnd;

    long tStart = System.currentTimeMillis();
    do {
      updateStart = System.currentTimeMillis();

      // Retrieve x, y and Theta information
      position = odo.getXYT();
      
      // Print x,y, and theta information
      DecimalFormat numberFormat = new DecimalFormat("######0.00");
      lcd.drawString("X: " + numberFormat.format(position[0]), 0, 0);
      lcd.drawString("Y: " + numberFormat.format(position[1]), 0, 1);
      lcd.drawString("T: " + numberFormat.format(position[2]), 0, 2);
      
      // this ensures that the data is updated only once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < DISPLAY_PERIOD) {
        try {
          Thread.sleep(DISPLAY_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } while ((updateEnd - tStart) <= timeout);

  }

}