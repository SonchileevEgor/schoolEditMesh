package opengl.drawing;

import opengl.Render;
import java.util.ArrayList;

/**
 * Queue of drawing actions.
 * These drawing operations will be drawn after scene drawing
 * @author alexeev
 */
public class DrawingQueue {
  private static ArrayList<i_DrawingAction> _queue = new ArrayList<>();

  private DrawingQueue() { };

  /**
   * Draw actions from queue with low priority.
   * (with priority = 0)
   * @param ren
   */
  public static void drawLowPriority(Render ren) {
    for (i_DrawingAction a : _queue) {
      if (a.priority() == 0)
        a.draw(ren);
    }
  }

  /**
   * Draw actions from queue with high priority.
   * (with priority = 1)
   * @param ren
   */
  public static void drawHighPriority(Render ren) {
    for (i_DrawingAction a : _queue) {
      if (a.priority() == 1)
        a.draw(ren);
    }
  }

  public static void clear() {
    _queue.clear();
  }

  public static void add(i_DrawingAction action) {
    _queue.add(action);
  }

  public static void remove(i_DrawingAction action) {
    _queue.remove(action);
  }
}