package gui.mode;

import bodies.AngleBody;
import bodies.BodyType;
import builders.AngleByTwoPointsBuilder;
import builders.RibBuilder;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import geom.Angle3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import static gui.mode.param.CreateModeParamType.ANGLE;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 * Angle building scenario. User chooses vertex, point on one side and angle value.
 *
 * @author Elena
 */
public class Create2dAngleByTwoPointsMode extends CreateBodyBy2PointsMode {

  public Create2dAngleByTwoPointsMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(ANGLE, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    changeValue(ANGLE, e.getWheelRotation());
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    changeValue(ANGLE, e);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE);
    if (isChosen(ANGLE)) {
      createWithNameValidation();
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте вершину угла{MOUSELEFT}",
            "Отметьте точку на первой стороне угла{MOUSELEFT}",
            "Выберите величину угла{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.ANGLE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);

    if (_numOfChosenAnchors == 1) {
      Drawer.drawPoint(ren, point(0), anchor(0).getState().getFocusedPointThickness() * 2);
    }
    if (_numOfChosenAnchors == 2) {
      Drawer.drawPoint(ren, point(1), anchor(1).getState().getFocusedPointThickness() * 2);
      Drawer.drawSegment(ren, point(0), point(1));
      Angle3d angle = Angle3d.angleByTwoPoints2D(point(0), point(1), valueAsDouble(ANGLE));
      Drawer.drawPoint(ren, angle.pointOnSecondSide(), _anchor.get(0).getState().getFocusedPointThickness() * 2);
      Drawer.drawSegment(ren, angle.vertex(), angle.pointOnSecondSide());
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_ANGLE_2D;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ANGLE_BY_SIDE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ANGLE_BY_SIDE.getLargeIcon();
  }

  @Override
  protected void create() {
    // Если нет ребра AB, создаём его.
    if (_ctrl.getAnchorContainer().findEqual(id(0), id(1)) == null) {
      RibBuilder rb = new RibBuilder(_anchor.get(0).getTitle() + _anchor.get(1).getTitle());
      rb.addA(id(0));
      rb.addB(id(1));
      _ctrl.add(rb, checker, false);
    }

    AngleByTwoPointsBuilder builder = new AngleByTwoPointsBuilder(_name);
    builder.addPointOnFirstSide(id(0));
    builder.addVertex(id(1));
    builder.addAngle(valueAsDouble(ANGLE));
    _ctrl.add(builder, checker, false);

    // Создаём ребро BC.
    try {
      i_Anchor c = _ctrl.getAnchor(builder.id(), AngleBody.BODY_KEY_C);
      RibBuilder rb = new RibBuilder(_anchor.get(0).getTitle() + c.getTitle());
      rb.addA(id(0));
      rb.addB(c.id());
      _ctrl.add(rb, checker, false);
    } catch (ExNoBody | ExNoAnchor ex) {
    }

    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Угол</strong><br>по вершине и точке на стороне";
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
