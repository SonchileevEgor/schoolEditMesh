package gui.mode;

import gui.EdtController;
import static gui.mode.param.CreateModeParamType.WIDTH;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author rita
 */
public class Create2dRhombusByDiagonalMode extends CreateRhombusByDiagonalMode {

  public Create2dRhombusByDiagonalMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(WIDTH, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!isChosen(WIDTH)) {
      changeValue(WIDTH, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!isChosen(WIDTH)) {
      changeValue(WIDTH, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
    }
    if (isChosen(WIDTH)) {
      create();
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POLYGON_REGULAR_2D;
  }
}
