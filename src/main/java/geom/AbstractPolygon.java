package geom;

import java.util.ArrayList;

/**
 * A plane figure which can be represented (possibly approximately) as a polygon
 */
public interface AbstractPolygon {
  /**
   * Get figure as array of points (which creates polygon)
   * <br>Can contain 0, 1, 2 or more points
   * @return Figure as polygon
   */
  ArrayList<Vect3d> getAsAbstractPolygon();

  /**
   * Get abstract polygon with specified amount of points
   * @param numPoints specified amount of points
   * @return
  */
  ArrayList<Vect3d> getAsAbstractPolygon(int numPoints);
}