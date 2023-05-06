package gui.mode.param;

/**
 * Parameter of the creation mode.
 * @author alexeev
 * @param <T>
 */
public abstract class ModeParam<T> {
  public static int MIN_INT = 3;
  public static int MAX_INT = 20;
  public static double MAX_DOUBLE = 50000;

  /**
   * Change value of parameter.
   * @param stepNum
   */
  abstract void change(int stepNum);

  /**
   * Increase value by one step.
   */
  abstract void inc();

  /**
   * Decrease value by one step.
   */
  abstract void dec();

  /**
   * Get type of parameter.
   * @return
   */
  abstract CreateModeParamType type();

  /**
   * Output formatted string.
   * @return
   */
  abstract String getFormattedString(int precision);
  
  /**
   * Output formatted value.
   * @return
   */
  abstract String getFormattedValue(int precision);

  /**
   * Set default value of the parameter.
   */
  abstract void setDefaultValue();
  
  /**
   * Set value of the parameter. 
   */
  abstract void setValue(Object value);
  
  /**
   * Value of the parameter.
   * @return 
   */
  public abstract T getValue();

  public int asInt() {
    return 0;
  }

  public double asDouble() {
    return 0.0;
  }

  public String asString() {
    return "";
  }
}