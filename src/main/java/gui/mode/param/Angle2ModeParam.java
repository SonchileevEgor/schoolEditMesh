package gui.mode.param;

import static config.Config.ANGLE_STEP;
import geom.Angle3d;
import geom.Checker;

/**
 * Angle which can be equal to 0, but not to 90 degrees.
 *
 * @author alexeev
 */
public class Angle2ModeParam extends ModeParam<Double> {
  private double _value;

  public Angle2ModeParam(double value) {
    _value = value;
  }

  public Angle2ModeParam() {}

  @Override
  public CreateModeParamType type() {
    return CreateModeParamType.ANGLE2;
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
    return String.format("%s: %s\u00B0", CreateModeParamType.ANGLE2.getTitle(),
            util.Util.valueOf(Angle3d.radians2Degree(_value), precision));
  }
  
  @Override
  public String getFormattedValue(int precision) {
    return util.Util.valueOf(Angle3d.radians2Degree(_value), precision);
  }

  @Override
  public void setDefaultValue() {
    _value = 0.0;
  }
   
  @Override
  void setValue(Object value) {
    _value = (double)value;
  }

  private void safeChange(int stepNum) {
    double newValue = (_value - stepNum * ANGLE_STEP.value()) % (Math.PI * 2);
    if (newValue <= Checker.eps()) {
      _value = 0;
    } else if (Math.abs(newValue - Math.PI/2) <= Checker.eps()) {
      safeChange(stepNum * 2);
    } else if (newValue >= Checker.eps() && newValue <= Math.PI - Checker.eps()) {
      _value = newValue;
    }
  }

  @Override
  public Double getValue() {
    return _value;
  }
}
