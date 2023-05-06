package opengl;

/**
 * Listener of display change.
 * @author alexeev
 */
public interface i_ScaleChangeListener {
  /**
   * Handler of scene scale change.
   * @param distance  distance from the camera.
   * @param meshSize  size of a mesh cell.
   */
  public void scaleChanged(double distance, double meshSize);
}
