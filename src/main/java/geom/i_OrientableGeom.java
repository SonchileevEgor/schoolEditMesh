/*
 */
package geom;

/**
 * Orientable geom objects.
 * (We can specify the view from above)
 *
 * @author alexeev
 */
public interface i_OrientableGeom {
  /**
   * Vector that specifies view from above for geometry object.
   * @return
   */
  public Vect3d getUpVect();
}
