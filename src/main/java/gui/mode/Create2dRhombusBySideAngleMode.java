package gui.mode;

import gui.EdtController;
import static gui.mode.param.CreateModeParamType.ANGLE;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author rita
 */
public class Create2dRhombusBySideAngleMode extends CreateRhombusBySideAngleMode {

  public Create2dRhombusBySideAngleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(ANGLE, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!isChosen(ANGLE)) {
      changeValue(ANGLE, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE);
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!isChosen(ANGLE)) {
      changeValue(ANGLE, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE);
    }
    if (isChosen(ANGLE)) {
      create();
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POLYGON_REGULAR_2D;
  }

}
