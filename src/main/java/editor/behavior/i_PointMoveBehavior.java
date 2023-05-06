package editor.behavior;

import geom.Vect3d;
import gui.MainEdtCanvasController;

/**
 * Behavior of moving anchor on scene.
 *
 * @author alexeev.
 */
public interface i_PointMoveBehavior {
  /**
   * Get the position of anchor after moving to point
   * <code>(newX, newY)</code> on screen.
   * @param canvas
   * @param point
   * @param startX
   * @param startY
   * @param newX
   * @param newY
   * @return
   */
  public Vect3d position(MainEdtCanvasController canvas, Vect3d point, double startX, double startY, double newX, double newY);

  /**
   * Type of behavior.
   * @return
   */
  public Behavior type();
}
