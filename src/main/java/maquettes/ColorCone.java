package maquettes;

import geom.Arc3d;
import geom.Cone3d;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Vladimir
 */
public class ColorCone {
    private Cone3d _cone;
    private ColorGL _color;

    public ColorCone(Cone3d cone, ColorGL clr) {
      _cone = cone;
      _color = clr;
    }

    public Cone3d getCone() {
      return _cone;
    }

    public ColorGL getColor() {
      return _color;
    }
}
