package geom;

 import java.util.ArrayList;

/**
 *
 * @author elena
 */
public interface i_Geom {
  /**
   * Is geometry object orientable or not.
   * (implements {@link i_OrientableGeom})
   * @return
   */
  public boolean isOrientable();

  public GeomType type();

  /**
   * Return basic points of geom object, by which it can be reconstructed.
   * @return
   */
  public ArrayList<Vect3d> deconstr();

  /**
   * Return geom object by its basic points.
   * @param points
   * @return
   */
  public i_Geom constr (ArrayList<Vect3d> points);
}
