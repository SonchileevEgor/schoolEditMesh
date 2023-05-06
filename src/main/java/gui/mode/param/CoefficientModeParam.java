package gui.mode.param;

import geom.Checker;

/**
 * Coefficient parameter of creation mode.
 * @author alexeev
 */
public class CoefficientModeParam extends ModeParam<Double> {
  double _value;

  public CoefficientModeParam(double value) {
    _value = value;
  }

  public CoefficientModeParam() {}

  @Override
  public CreateModeParamType type() {
    return CreateModeParamType.COEFFICIENT;
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
    return String.format("%s: %s", CreateModeParamType.COEFFICIENT.getTitle(),
            util.Util.valueOf(_value, precision));
  }
  
  @Override
  public String getFormattedValue(int precision) {
    return util.Util.valueOf(_value, precision);
  }

  @Override
  void setDefaultValue() {
    _value = 1.5;
  }

  private void safeChange(int stepNum) {
    double newValue = _value + 0.1 * stepNum;
    if (Math.abs(newValue) >= Checker.eps()) {
      _value = newValue;
    } else {
      safeChange(stepNum * 2);
    }
  }

  @Override
  void setValue(Object value) {
    _value = (double)value;
  }

  @Override
  public Double getValue() {
    return _value;
  }
}
