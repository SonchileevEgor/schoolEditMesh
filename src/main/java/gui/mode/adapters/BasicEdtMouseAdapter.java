package gui.mode.adapters;

import gui.BasicEdtCanvasController;
import gui.EdtController;
import java.awt.event.MouseAdapter;

/**
 * Base class for mouse adapters working on canvas.
 * Might work without link to EdtController.
 *
 * @author alexeev
 */
public abstract class BasicEdtMouseAdapter extends MouseAdapter {
  protected final BasicEdtCanvasController _canvas;
  protected boolean _block;

  protected BasicEdtMouseAdapter(BasicEdtCanvasController canvas) {
    _canvas = canvas;
  }

  public void reset() {
    _block = false;
  }

  /**
   * Block some functions of adapter.
   * @param block
   */
  public void setBlocked(boolean block) {
    _block = block;
  }

  protected BasicEdtCanvasController canvas() {
    return _canvas;
  }
}
