package gui.mode.param;

import geom.Checker;

/**
 *
 * @author Лютенков
 */
public class Coefficient2ModeParam extends ModeParam<Double> {
  double _value;

  public Coefficient2ModeParam(double value) {
    _value = value;
  }

  public Coefficient2ModeParam() {}

  @Override
  public CreateModeParamType type() {
    return CreateModeParamType.COEFFICIENT2;
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
    return String.format("%s: %s", CreateModeParamType.COEFFICIENT2.getTitle(),
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
  @Override
  void setValue(Object value) {
    _value = (double)value;
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
  public Double getValue() {
    return _value;
  }
}
