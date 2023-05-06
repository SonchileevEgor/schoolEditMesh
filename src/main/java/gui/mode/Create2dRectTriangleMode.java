package gui.mode;

import gui.EdtController;
import static gui.mode.param.CreateModeParamType.HEIGHT;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author rita
 */
public class Create2dRectTriangleMode extends CreateRectTriangleMode {

  public Create2dRectTriangleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(HEIGHT, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(HEIGHT);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!_param.isChosen(HEIGHT)) {
      _param.change(HEIGHT, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(HEIGHT);
      _param.showValue(HEIGHT);
      _ctrl.redraw();
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!_param.isChosen(HEIGHT)) {
      changeValue(HEIGHT, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(HEIGHT);
    }
    if (_param.isChosen(HEIGHT)) {
      create();
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RECT_TRIANGLE_2D;
  }
}
