package gui.mode.param;

import static config.Config.ANGLE_STEP;
import geom.Angle3d;
import geom.Checker;

/**
 * Angle parameter of the creation mode.
 *
 * @author alexeev
 */
public class AngleModeParam extends ModeParam<Double> {
  // value in radians
  private double _value;

  public AngleModeParam(double value) {
    _value = value;
  }

  public AngleModeParam() {}

  @Override
  public CreateModeParamType type() {
    return CreateModeParamType.ANGLE;
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
  public String getFormattedString(int precision) {
    return String.format("%s: %s\u00B0", CreateModeParamType.ANGLE.getTitle(),
            util.Util.valueOf(Angle3d.radians2Degree(_value), precision));
  }
  
  @Override
  public String getFormattedValue(int precision) {
    return util.Util.valueOf(Angle3d.radians2Degree(_value), precision);
  }

  @Override
  public void setDefaultValue() {
    _value = Math.PI / 6;
  }
  
  @Override
  void setValue(Object value) {
    _value = (double)value;
  }

  private void safeChange(int stepNum) {
    double newValue = (_value - stepNum * ANGLE_STEP.value()) % (Math.PI * 2);
    if( newValue >= Checker.eps() && newValue <= Math.PI - Checker.eps() ){
      _value = newValue;
    }
  }

  @Override
  public Double getValue() {
    return _value;
  }
}
