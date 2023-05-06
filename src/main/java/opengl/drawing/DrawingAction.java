package opengl.drawing;

import opengl.Render;

/**
 *
 * @author alexeev
 */
public class DrawingAction implements i_DrawingAction {
  private int _priority;
  
  public DrawingAction(int priority) {
    _priority = priority;
  }

  public DrawingAction() {
    this(0);
  }

  @Override
  public void draw(Render ren) { }

  @Override
  public int priority() {
    return _priority;
  }
}