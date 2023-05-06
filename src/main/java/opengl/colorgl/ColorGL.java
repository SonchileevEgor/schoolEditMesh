package opengl.colorgl;

import minjson.JsonObject;
import minjson.JsonValue;

import java.awt.*;
import java.text.MessageFormat;

/**
 * Color in RGBA mode. All parameters have values ​​from 0 to 1.
 * If alpha is not specified, it is set to 1
 */
public class ColorGL {
  public static final ColorGL BLACK = new ColorGL(0, 0, 0);
  public static final ColorGL WHITE = new ColorGL(1, 1, 1);
  public static final ColorGL RED = new ColorGL(1, 0, 0);
  public static final ColorGL GREEN = new ColorGL(0, 1, 0);
  public static final ColorGL BLUE = new ColorGL(0, 0, 1);
  public static final ColorGL YELLOW = new ColorGL(1, 1, 0);
  public static final ColorGL CYAN = new ColorGL(0, 1, 1);
  public static final ColorGL MAGENTA = new ColorGL(1, 0, 1);
  public static final ColorGL GREY = new ColorGL(0.5, 0.5, 0.5);
  public static final ColorGL LIGHT_GREY = new ColorGL(0.1, 0.1, 0.1);
  private static final int RGB_COEF = 255;
  private double _color[] = new double[4];

  public ColorGL() {
    setColor(0.0, 0.0, 0.0, 1);
  }
  public ColorGL(double red, double green, double blue){
    setColor(red, green, blue, 1);
  }

  public ColorGL(double red, double green, double blue, double alpha){
    setColor(red, green, blue, alpha);
  }
  public ColorGL(double[] color, int numElements){
    setColor(color, numElements);
  }
  public ColorGL(Color c){
    float[] compArray = new float[4];
    c.getComponents(compArray);
    setColor(compArray[0], compArray[1], compArray[2], compArray[3]);
  }
  public ColorGL(JsonObject jobj) {
    if (jobj.get("A") == null) {
      setColor(jobj.get("R").asFloat(),
               jobj.get("G").asFloat(),
               jobj.get("B").asFloat());
    } else {
      setColor(jobj.get("R").asFloat(),
               jobj.get("G").asFloat(),
               jobj.get("B").asFloat(),
               jobj.get("A").asFloat());
    }
  }

  public ColorGL duplicate() {
    return new ColorGL(_color[0], _color[1], _color[2], _color[3]);
  }

  public final void setColor(double red, double green, double blue, double alpha){
    _color[0] = red;
    _color[1] = green;
    _color[2] = blue;
    _color[3] = alpha;
  }
  public final void setColor(double red, double green, double blue){
    setColor(red, green, blue, 1);
  }
  public final void setColor(double[] color, int numElements){
    if (numElements < 0 || numElements > 4 ){
      throw new RuntimeException("numElements must be less or equal then 4 and more then 0");
    }
    for (int i = 0; i < numElements; i++){
      _color[i] = color[i];
    }
    for (int i = numElements; i < 4; i++){
      _color[i] = 1;
    }
  }

  /**
   * Get color as double array [r, g, b, a]
   * @return color as double array
   */
  public double[] getArray(){
    return _color;
  }

  @Override
  public String toString() {
    return MessageFormat.format("R:{0} G:{1} B:{2} A:{3}", _color[0], _color[1], _color[2], _color[3]);
  }

  /**
   * Get color as float array [r, g, b, a]
   * @return color as float array
   */
  public float[] getArrayFloat(){
    float[] res = new float[4];
    for (int i = 0; i < 4; i++){
      res[i] = (float)_color[i];
    }
    return res;
  }
  public double getRed(){
    return _color[0];
  }
  public double getGreen(){
    return _color[1];
  }
  public double getBlue(){
    return _color[2];
  }
  public double getAlpha(){
    return _color[3];
  }

  /**
   * Emphasize color.
   * Option used when we choose object.
   *
   * @return
   */
  public ColorGL emphasize(){
    double[] newColor = new double[3];
    /* if color is light, darken it;
       otherwise - enlight it */
    if (_color[0] + _color[1] + _color[2] >= 1.5) {
      for(int i = 0; i < 3; i++) {
        newColor[i] = Math.max(_color[i] - 0.25, 0);
      }
    } else {
      for(int i = 0; i < 3; i++) {
        newColor[i] = Math.min(_color[i] + 0.25, 1);
      }
    }

    return new ColorGL(newColor[0], newColor[1], newColor[2], _color[3]);
  }

  public Color toRGBAColor() {
    return new Color((float)(_color[0]),
                     (float)(_color[1]),
                     (float)(_color[2]),
                     (float)(_color[3]));
  }

  public int[] getRGBComponents() {
    int[] components = new int[3];
    Double red = getRed()*RGB_COEF;
    Double green = getGreen()*RGB_COEF;
    Double blue = getBlue()*RGB_COEF;
    components[0] = red.intValue();
    components[1] = green.intValue();
    components[2] = blue.intValue();
    return components;
  }

  public JsonObject toJson() {
    JsonObject result = new JsonObject();
    result.add("R", JsonValue.valueOf(getRed()));
    result.add("G", JsonValue.valueOf(getGreen()));
    result.add("B", JsonValue.valueOf(getBlue()));
    result.add("A", JsonValue.valueOf(getAlpha()));
    return result;
  }
}
