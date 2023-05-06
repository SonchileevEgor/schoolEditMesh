package maquettes;

import opengl.colorgl.ColorGL;
import geom.Vect3d;

/**
 * @author Leonid Ivanovsky
 */

public class ColorPoint {

  private Vect3d _point;
  private ColorGL _color;
  private double _coverRadius; // TODO: parameter needs for temporary decision of building cover around sphere
  private String _id;

  public ColorPoint(Vect3d pnt, ColorGL clr, String id) {
    setPoint(pnt);
    setColor(clr);
    setCoverRadius(0.0);
    _id = id;
  }

  public ColorPoint(Vect3d pnt, ColorGL clr, double cover_rad, String id) {
    setPoint(pnt);
    setColor(clr);
    setCoverRadius(cover_rad);
    _id = id;
  }

  public void setPoint(Vect3d pnt) {
      _point = pnt;
  }

  public Vect3d getPoint() {
    return _point;
  }

  public void setColor(ColorGL clr) {
      _color = clr;
  }

  public ColorGL getColor() {
    return _color;
  }

  public void setCoverRadius(double cover_rad) {
      _coverRadius = cover_rad;
  }

  public double getCoverRadius() {
      return _coverRadius;
  }

  public String getID() {
      return _id;
  }
}