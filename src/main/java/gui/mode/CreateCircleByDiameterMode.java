package gui.mode;

import bodies.BodyType;
import builders.CircleByDiameterAngleBuilder;
import geom.Circle3d;
import geom.ExGeom;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Лютенков
 */
public class CreateCircleByDiameterMode extends CreateBodyBy2PointsMode {

  public CreateCircleByDiameterMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(ROT_ANGLE, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
  }

  @Override
  protected void nativeDraw1(Render ren) {

    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _numOfChosenAnchors; i++) {
      Drawer.drawPoint(ren, point(i));
    }
    if (_numOfChosenAnchors == 2) {
      try {
        Circle3d disk = Circle3d.CircleBy2PntsAngle(_anchor.get(0).getPoint(), _anchor.get(1).getPoint(), valueAsDouble(ROT_ANGLE));
        Drawer.drawCircle(ren, disk.radiusLength(), disk.center(), disk.normal(), TypeFigure.WIRE);
      } catch (ExGeom ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую вершину окружности{MOUSELEFT}",
            "Выберите вторую вершину окружности{MOUSELEFT}",
            "Выберите положение{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CIRCLE.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CIRCLE_BY_DIAM_PNTS.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CIRCLE_BY_DIAM_PNTS.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Окружность</strong><br>по двум точкам на концах диаметра";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_CIRCLE_BY_DIAMETER;
  }

  @Override
  protected void create() {
    CircleByDiameterAngleBuilder builder = new CircleByDiameterAngleBuilder(_name);
    builder.addA(id(0));
    builder.addB(id(1));
    builder.addAngle(valueAsDouble(ROT_ANGLE));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(ROT_ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    changeValue(ROT_ANGLE, e.getWheelRotation());
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    changeValue(ROT_ANGLE, e);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    if (isChosen(ROT_ANGLE)) {
      createWithNameValidation();
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
