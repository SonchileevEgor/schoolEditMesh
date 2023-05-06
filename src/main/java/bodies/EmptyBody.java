package bodies;

import editor.Editor;
import editor.i_Body;
import geom.Ray3d;
import geom.Vect3d;
import geom.i_Geom;
import opengl.Render;
import util.Util;

/**
 * Empty body (without type).
 *
 * @author alexeev
 */
public class EmptyBody extends BodyAdapter {

  /**
   * Empty body doesn't exists.
   * @param id
   * @param title
   */
  public EmptyBody(String id, String title) {
    super(id, title);
    _alias = "тело";
    _exists = false;
  }

  @Override
  public i_Geom getGeom() {
    return null;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new EmptyBody(id, title);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) { }

  @Override
  public BodyType type() {
    return BodyType.EMPTY;
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return null;
  }
}