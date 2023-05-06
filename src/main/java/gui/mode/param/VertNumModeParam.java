package gui.mode.param;

/**
 * Vertices number parameter of the creation mode.
 *
 * @author alexeev
 */
public class VertNumModeParam extends ModeParam<Integer> {
  private static final int MAX_VALUE = 20;
  private static final int MIN_VALUE = 3;
  private int _value;

  public VertNumModeParam(int value) {
    _value = value;
  }

  public VertNumModeParam() {}

  @Override
  public CreateModeParamType type() {
    return CreateModeParamType.VERT_NUMBER;
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
  public int asInt() {
    return _value;
  }

  @Override
  String getFormattedString(int precision) {
    return String.format("%s: %d", CreateModeParamType.VERT_NUMBER.getTitle(), _value);
  }
  
  @Override
  String getFormattedValue(int precision) {
    return String.valueOf(_value);
  }

  @Override
  void setDefaultValue() {
    _value = 5;
  }
  
  @Override
  void setValue(Object value) {
    _value = (int)value;
  }
  
  private void safeChange(int stepNum) {
    int newValue = _value + stepNum;
    if( newValue <= MAX_VALUE && newValue >= MIN_VALUE ){
      _value = newValue;
    }
  }

  @Override
  public Integer getValue() {
    return _value;
  }
}
