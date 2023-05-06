package gui.mode.adapters;

import gui.BasicEdtCanvasController;
import gui.EdtController;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author alexeev
 */
public class ScalingMouseAdapter extends BasicEdtMouseAdapter {

  public ScalingMouseAdapter(BasicEdtCanvasController canvas) {
    super(canvas);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (!_block) {
      _canvas.getScene().changeCameraDistance(0.25 * e.getWheelRotation());
    }
  }
}
