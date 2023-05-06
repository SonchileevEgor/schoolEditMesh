package gui.mode.param;

import geom.Checker;
import opengl.i_ScaleChangeListener;
import util.Util;

/**
 * Distance parameter of the creation mode.
 * May be length, width, height, or radius.
 *
 * @author alexeev
 */
public class DistanceModeParam extends ModeParam<Double> implements i_ScaleChangeListener {
  private static final double MAX_VALUE = 50000;
  public static double MIN_VALUE = 0;
  CreateModeParamType _type;
  double _defaultValue;
  double _value;

  public DistanceModeParam(double value, CreateModeParamType type) {
    _value = value;
    _type = type;
  }

  public DistanceModeParam(CreateModeParamType type) {
    _type = type;
  };

  @Override
  public CreateModeParamType type() {
    return _type;
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
    return String.format("%s: %s", type().getTitle(), Util.valueOf(_value, precision));
  }
  
  @Override
  public String getFormattedValue(int precision) {
    return Util.valueOf(_value, precision);
  }

  @Override
  public void setDefaultValue() {
    _value = _defaultValue;
  }

  @Override
  void setValue(Object value) {
    _value = (double)value;
  }
  
  @Override
  public void scaleChanged(double distance, double meshSize) {
    _defaultValue = meshSize;
    MIN_VALUE = meshSize * 0.4;
  }
  
  @Override
  public Double getValue() {
    return _value;
  }

  private void safeChange(int stepNum) {
    double result = _value + stepNum * MIN_VALUE * 0.5;
    if( result >= MIN_VALUE - Checker.eps() && result <= MAX_VALUE + Checker.eps() ){
      _value = result;
    }
  }
}
