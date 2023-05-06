package builders;

import geom.Vect3d;

/**
 * Interface for builder of movable point.
 * @author alexeev.
 */
public interface i_MovablePointBuilder {
  /**
   * Change builder params so result point moves into v.
   * @param newPosition 
   */
  void movePoint(Vect3d newPosition);
}
