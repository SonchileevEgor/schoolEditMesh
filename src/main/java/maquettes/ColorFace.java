package maquettes;

import geom.Polygon3d;
import opengl.colorgl.ColorGL;

/**
 * @author Leonid Ivanovsky
 */

public class ColorFace {

  private Polygon3d _face;
  private ColorGL _color;
  private String _id;

    /**
     *
     * @param fc the value of fc
     * @param clr the value of clr
     */
    public ColorFace(Polygon3d fc, ColorGL clr, String id) {
    _face = fc;
    _color = clr;
    _id = id;
  }

  public Polygon3d getFace() {
    return _face;
  }

  public ColorGL getColor() {
    return _color;
  }
  
  public String getID() {
      return _id;
  }
}