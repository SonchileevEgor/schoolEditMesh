package bodies;

import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.ConicPivots;
import geom.Ray3d;
import geom.Vect3d;
import geom.i_Geom;


/**
 *
 * @author Ivan
 */
public class ConicBody extends BodyAdapter {

  private ConicPivots _conic; //math object conic

  /**
   * constructor for null-body
   *
   * @param id
   * @param title
   */
  public ConicBody(String id, String title) {
    super(id, title);
    _alias = "коника";
    _exists = false;
  }

  @Override
  public void glDrawCarcass(Render ren) {
  }

  @Override
  public BodyType type() {
    return BodyType.CONIC;
  }

  @Override
  public i_Geom getGeom() {
    return (i_Geom) _conic;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return null;
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
  }

  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
