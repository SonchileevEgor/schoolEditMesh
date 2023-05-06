package config;

import java.awt.Dimension;
import java.awt.Font;
import minjson.JsonObject;
import minjson.JsonValue;

/**
 * Factory of configuration parameters of different types.
 * @author alexeev
 */
public class ConfigParamFactory {
  public static ConfigParam<String> getStringParam(
          String name, String alias, boolean mutable, String defaultValue){
    return new ConfigParam<String>(name, alias, mutable, defaultValue) {
      @Override
      public void fromJson(JsonValue v) {
        _value = v.asString();
      }

      @Override
      public JsonValue toJson() {
        return JsonValue.valueOf(_value);
      }
    };
  }
  
  public static ConfigParam<Double> getDoubleParam(
          String name, String alias, boolean mutable, double defaultValue){
    return new ConfigParam<Double>(name, alias, mutable, defaultValue) {
      @Override
      public void fromJson(JsonValue v) {
        _value = v.asDouble();
      }
      
      @Override
      public JsonValue toJson() {
        return JsonValue.valueOf(_value);
      }
    };
  }
  
  public static ConfigParam<Float> getFloatParam(
          String name, String alias, boolean mutable, float defaultValue){
    return new ConfigParam<Float>(name, alias, mutable, defaultValue) {
      @Override
      public void fromJson(JsonValue v) {
        _value = v.asFloat();
      }
      
      @Override
      public JsonValue toJson() {
        return JsonValue.valueOf(_value);
      }
    };
  }
  
  public static ConfigParam<Font> getFontParam(
          String name, String alias, boolean mutable, Font defaultValue){
    return new ConfigParam<Font>(name, alias, mutable, defaultValue) {
      @Override
      public void fromJson(JsonValue v) {
        _value = new Font(v.asString(), Font.PLAIN, 10);
      }
      
      @Override
      public JsonValue toJson() {
        return JsonValue.valueOf(_value.getFontName());
      }
    };
  }
  
  public static ConfigParam<Integer> getDecimalParam(
          String name, String alias, boolean mutable, int defaultValue){
    return new ConfigParam<Integer>(name, alias, mutable, defaultValue) {
      @Override
      public void fromJson(JsonValue v) {
        _value = v.asInt();
      }
      
      @Override
      public JsonValue toJson() {
        return JsonValue.valueOf(_value);
      }
    };
  }
  
  public static ConfigParam<Boolean> getBooleanParam(
          String name, String alias, boolean mutable, boolean defaultValue){
    return new ConfigParam<Boolean>(name, alias, mutable, defaultValue) {
      @Override
      public void fromJson(JsonValue v) {
        _value = v.asBoolean();
      }
      
      @Override
      public JsonValue toJson() {
        return JsonValue.valueOf(_value);
      }
    };
  }
  
  public static ConfigParam<Dimension> getDimensionParam(
          String name, String alias, boolean mutable, Dimension defaultValue){
    return new ConfigParam<Dimension>(name, alias, mutable, defaultValue) {
      @Override
      public void fromJson(JsonValue v) {
        _value = new Dimension(v.asObject().get("width").asInt(),
                               v.asObject().get("height").asInt());
      }
      
      @Override
      public JsonValue toJson() {
        JsonObject js = new JsonObject();
        js.add("width", _value.width);
        js.add("height", _value.height);
        return js;
      }
    };
  }
  
  public static ConfigParam<JsonValue> getJsonParam(
          String name, String alias, boolean mutable, JsonValue defaultValue){
    return new ConfigParam<JsonValue>(name, alias, mutable, defaultValue) {
      @Override
      public void fromJson(JsonValue v) {
        _value = v;
      }
      
      @Override
      public JsonValue toJson() {
        return _value;
      }
    };
  }
}
