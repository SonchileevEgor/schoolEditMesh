package maquettes;

import opengl.colorgl.ColorGL;
import geom.Rib3d;

/**
 * @author Leonid Ivanovsky
 */

public class ColorEdge {

  private Rib3d _edge;
  private ColorGL _color;
  private String _id;

  public ColorEdge(Rib3d rb, ColorGL clr, String id) {
    _edge = rb;
    _color = clr;
    _id = id;
  }

  public Rib3d getEdge() {
    return _edge;
  }

  public ColorGL getColor() {
    return _color;
  }

  public String getID() {
      return _id;
  }
}