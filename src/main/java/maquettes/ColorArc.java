package maquettes;

import geom.Arc3d;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Kurgansky
 */
public class ColorArc {
  private Arc3d _arc;
  private ColorGL _color;
  
  public ColorArc(Arc3d rb, ColorGL clr) {
    _arc = rb;
    _color = clr;
  }

  public Arc3d getArc() {
    return _arc;
  }

  public ColorGL getColor() {
    return _color;
  }
}
