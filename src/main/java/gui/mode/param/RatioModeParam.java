package gui.mode.param;

/**
 * Ratio parameter of creation mode.
 * @author alexeev
 */
public class RatioModeParam extends ModeParam<Double> {
  double _value;

  public RatioModeParam(double value) {
    _value = value;
  }

  public RatioModeParam() {}

  @Override
  public CreateModeParamType type() {
    return CreateModeParamType.RATIO;
  }

  @Override
  public void change(int stepNum) {
    safeChange(stepNum);
  }

  @Override
  public void inc() {
    safeChange(1);
  }

  @Override
  public void dec() {
    safeChange(-1);
  }

  @Override
  public double asDouble() {
    return _value;
  }

  @Override
  String getFormattedString(int precision) {
    return String.format("%s: %s", CreateModeParamType.RATIO.getTitle(),
            util.Util.valueOf(_value, precision));
  }
  
  @Override
  public String getFormattedValue(int precision) {
    return util.Util.valueOf(_value, precision);
  }

  @Override
  void setDefaultValue() {
    _value = 0.5;
  }
   
  @Override
  void setValue(Object value) {
    _value = (double)value;
  }

  private void safeChange(int stepNum) {
    double newValue = _value - stepNum * 0.05;
    if (newValue > 0 && newValue <= 1.05)
      _value = newValue;
  }

  @Override
  public Double getValue() {
    return _value;
  }
}
