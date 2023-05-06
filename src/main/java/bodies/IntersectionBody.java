package bodies;

import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.Ray3d;
import geom.i_Geom;
import geom.Vect3d;
import java.util.ArrayList;
import util.Util;

/**
 *
 * @author Elena
 */
public class IntersectionBody extends BodyAdapter {
  private ArrayList<Vect3d> _points;

  /**
   * Constructor of circle by math object circle
   * @param id
   * @param title
   * @param points
   */
  public IntersectionBody(String id, String title, ArrayList<Vect3d> points){
    super(id, title);
    _points = points;
    _alias = "точки пересечения";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public IntersectionBody(String id, String title) {
    super(id, title);
    _alias = "точки пересечения";
    _exists = false;
  }

  public ArrayList<Vect3d> points() { return _points; }

  @Override
  public BodyType type() {
    return BodyType.INTERSECT_BODY;
  }

  @Override
  public void glDrawCarcass(Render ren){}

  @Override
  public void glDrawFacets(Render ren){}

  @Override
  public i_Geom getGeom() {
    return null;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return null;
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) { }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return null;
  }
};
