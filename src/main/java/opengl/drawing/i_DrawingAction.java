package opengl.drawing;

import opengl.Render;

/**
 * Drawing operation.
 * This action will be called after scene drawing
 * if queued into DrawingQueue
 * @author alexeev
 */
public interface i_DrawingAction {
  public void draw(Render ren);

  /**
   * priority of drawing action
   * 0 - action will be drawn <strong>before</strong> scene drawing
   * 1 - action will be drawn <strong>after</strong> scene drawing
   * @return
   */
  public int priority();
}