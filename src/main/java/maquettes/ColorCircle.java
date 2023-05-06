package maquettes;

import geom.Circle3d;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Kurgansky
 */
public class ColorCircle {
  private Circle3d _circle;
  private ColorGL _color;
  
  public ColorCircle(Circle3d rb, ColorGL clr) {
    _circle = rb;
    _color = clr;
  }

  public Circle3d getCircle() {
    return _circle;
  }

  public ColorGL getColor() {
    return _color;
  }
}