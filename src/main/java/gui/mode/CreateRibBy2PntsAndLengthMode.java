package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.RibBy2PntsAndLengthBuilder;
import geom.ExDegeneration;
import geom.Rib3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.LENGTH;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Лютенков
 */
public class CreateRibBy2PntsAndLengthMode extends CreateBodyBy2PointsMode {

  BodyType _type;

  public CreateRibBy2PntsAndLengthMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(LENGTH, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(LENGTH);
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, point(0));
    }
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Rib3d result = null;
      try {
        result = Rib3d.RibBy2PntsAndLength(point(0), point(1), valueAsDouble(LENGTH));
      } catch (ExDegeneration ex) {
      }

      Drawer.drawPoint(ren, result.a());
      Drawer.drawPoint(ren, result.b());
      Drawer.drawSegment(ren, result.a(), result.b());
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую точку{MOUSELEFT}",
            "Выберите вторую точку{MOUSELEFT}",
            "Выберите длину отрезка{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RIB_BY_TWO_POINTS_AND_LENGTH.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RIB_BY_TWO_POINTS_AND_LENGTH.getLargeIcon();
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RIB_BY_TWO_POINTS_AND_LENGTH;
  }

  @Override
  protected void setName() {
    _name = BodyType.RIB.getName(_ctrl.getEditor());
  }

  @Override
  protected void create() {
    RibBy2PntsAndLengthBuilder builder = new RibBy2PntsAndLengthBuilder();
    builder.setValue(BodyBuilder.BLDKEY_NAME, _name);
    builder.setValue(BodyBuilder.BLDKEY_A, id(0));
    builder.setValue(BodyBuilder.BLDKEY_B, id(1));
    builder.setValue(RibBy2PntsAndLengthBuilder.BLDKEY_RIB_LENGTH, valueAsDouble(LENGTH));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Отрезок</strong><br>заданной длины";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(LENGTH);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    changeValue(LENGTH, e.getWheelRotation());
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(LENGTH);
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    changeValue(LENGTH, e);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(LENGTH);
    if (isChosen(LENGTH)) {
      create();
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
