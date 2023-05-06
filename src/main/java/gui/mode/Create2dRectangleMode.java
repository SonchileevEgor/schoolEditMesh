package gui.mode;

import gui.EdtController;
import static gui.mode.param.CreateModeParamType.HEIGHT;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author rita
 */
public class Create2dRectangleMode extends CreateRectangleMode {

  public Create2dRectangleMode(EdtController ctrl) {
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
    if (!isChosen(HEIGHT)) {
      changeValue(HEIGHT, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(HEIGHT);
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!isChosen(HEIGHT)) {
      changeValue(HEIGHT, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(HEIGHT);
    }
    if (isChosen(HEIGHT)) {
      create();
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RECTANGLE_2D;
  }
}
