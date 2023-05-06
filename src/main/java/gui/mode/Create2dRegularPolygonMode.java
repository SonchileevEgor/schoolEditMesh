package gui.mode;

import gui.EdtController;
import static gui.mode.param.CreateModeParamType.VERT_NUMBER;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Creation regular polygon in 2d mode
 *
 * @author rita
 */
public class Create2dRegularPolygonMode extends CreateRegularPolygonMode {

  public Create2dRegularPolygonMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(VERT_NUMBER, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(VERT_NUMBER);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!_param.isChosen(VERT_NUMBER)) {
      _param.change(VERT_NUMBER, e.getWheelRotation());
      _param.showValue(VERT_NUMBER);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(VERT_NUMBER);
      _ctrl.redraw();
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!_param.isChosen(VERT_NUMBER)) {
      changeValue(VERT_NUMBER, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(VERT_NUMBER);
    }
    if (_param.isChosen(VERT_NUMBER)) {
      create();
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POLYGON_REGULAR_2D;
  }

}
