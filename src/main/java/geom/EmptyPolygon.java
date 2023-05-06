package geom;

import java.util.ArrayList;

/**
 * Polygon witch has no points.
 */
public class EmptyPolygon implements AbstractPolygon {
  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon() {
    return new ArrayList<>();
  }

  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon(int numPoints) {
    return getAsAbstractPolygon();
  }
}
