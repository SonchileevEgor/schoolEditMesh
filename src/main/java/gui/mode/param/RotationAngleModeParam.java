package gui.mode.param;

import static config.Config.ANGLE_STEP;
import geom.Angle3d;
import util.Util;

/**
 * Angle [0, 2PI) parameter of the creation mode.
 *
 * @author alexeev
 */
public class RotationAngleModeParam extends ModeParam<Double> {
  // value in radians
  private double _value;

  public RotationAngleModeParam(double value) {
    _value = value;
  }

  public RotationAngleModeParam() {}

  @Override
  public CreateModeParamType type() {
    return CreateModeParamType.ROT_ANGLE;
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
    return String.format("%s: %s\u00B0", CreateModeParamType.ROT_ANGLE.getTitle(),
            Util.valueOf(Angle3d.radians2Degree(_value), precision));
  }

  @Override
  public String getFormattedValue(int precision) {
    return Util.valueOf(Angle3d.radians2Degree(_value), precision);
  }

  @Override
  void setDefaultValue() {
    _value = 0.0;
  }
  
  @Override
  void setValue(Object value) {
    _value = (double)value;
  }

  @Override
  public Double getValue() {
    return _value;
  }
  
  private void safeChange(int stepNum) {
    _value = (_value - stepNum * ANGLE_STEP.value()) % (Math.PI * 2);
  }
}
