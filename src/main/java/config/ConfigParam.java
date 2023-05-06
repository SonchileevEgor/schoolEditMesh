package config;

import minjson.i_Jsonable;

/**
 * Configuration parameter.
 * 
 * @author alexeev
 * @param <T> type of the parameter.
 */
public abstract class ConfigParam<T> implements i_Jsonable {
  public final boolean mutable;
  public final String name;
  public final String alias;
  
  protected T _value;
  
  public ConfigParam(String name, String alias, boolean mutable, T defaultValue){
    this.name = name;
    this.alias = alias;
    this.mutable = mutable;
    _value = defaultValue;
  }
  
  public T value() {
    return _value;
  }
  
  public void setValue(T value) {
    _value = value;
  }
  
  /**
   * Apply option.
   * The most parameters not needed to apply.
   * For those parameters leave this method empty.
   */
  public void apply() {}
}
