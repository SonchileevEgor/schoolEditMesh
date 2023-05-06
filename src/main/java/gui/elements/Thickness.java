package gui.elements;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 * List of default line thickness values.
 * 
 * @author alexeev-laptop
 */
public enum Thickness {
  XS(1f),
  S (2f),
  M (3f),
  L (4f),
  XL(5f);
  
  private Thickness(float value) {
    _thickness = value;
  }
  
  public float getValue() {
    return _thickness;
  }
  
  public Stroke getStroke() {
    return new BasicStroke(_thickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1f, new float[] { _thickness }, 1);
  }
  
  public static Thickness get(float value) {
    if (value < 1.5)
      return XS;
    if (value < 2.5)
      return S;
    if (value < 3.5)
      return M;
    if (value < 4.5)
      return L;
    return XL;
  }
  
  private float _thickness;
}
