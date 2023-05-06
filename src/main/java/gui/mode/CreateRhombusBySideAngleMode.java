package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.RhombusBySideAngleBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.ExGeom;
import geom.Polygon3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.ANGLE;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 *
 * @author rita
 */
public class CreateRhombusBySideAngleMode extends CreateBodyBy2PointsMode {

  public CreateRhombusBySideAngleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(ANGLE, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE);
    _param.setUsed(ROT_ANGLE, 2);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!isChosen(ANGLE)) {
      changeValue(ANGLE, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE);
    } else {
      changeValue(ROT_ANGLE, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!isChosen(ANGLE)) {
      changeValue(ANGLE, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ANGLE);
      if (isChosen(ANGLE)) {
        showValue(ROT_ANGLE);
      }
    } else {
      changeValue(ROT_ANGLE, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    }
    if (isChosen(ROT_ANGLE)) {
      createWithNameValidation();
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте отметьте первую точку на стороне ромба{MOUSELEFT}",
            "Отметьте отметьте вторую точку на стороне ромба{MOUSELEFT}",
            "Выберите угол ромба{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите угол поворота{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.RHOMBUS.getName(_ctrl.getEditor());
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RHOMBUS_BY_DIAGONAL;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RHOMBUS_BY_SIDE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RHOMBUS_BY_SIDE.getLargeIcon();
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_A, new BuilderParam(BLDKEY_A, "Первая вершина ромба", BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_B, new BuilderParam(BLDKEY_B, "Вторая вершина ромба", BuilderParamType.ANCHOR, id(1)));
    params.put(RhombusBySideAngleBuilder.BLDKEY_ANGLE1,
            new BuilderParam(RhombusBySideAngleBuilder.BLDKEY_ANGLE1, "Угол между сторонами", BuilderParamType.ANGLE_VALUE, valueAsDouble(ANGLE)));
    params.put(RhombusBySideAngleBuilder.BLDKEY_ANGLE2,
            new BuilderParam(RhombusBySideAngleBuilder.BLDKEY_ANGLE2, "Угол поворота", BuilderParamType.ANGLE_ROTATION, valueAsDouble(ROT_ANGLE)));
    RhombusBySideAngleBuilder builder = new RhombusBySideAngleBuilder(params);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Ромб</strong><br>по двум вершинам одной стороны и углу";
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    if (_numOfChosenAnchors == 1) {
      Drawer.drawPoint(ren, point(0), anchor(0).getState().getFocusedPointThickness() * 2);
    }
    if (_numOfChosenAnchors == 2) {
      Drawer.drawPoint(ren, point(0), anchor(0).getState().getFocusedPointThickness() * 2);
      Drawer.drawPoint(ren, point(1), anchor(1).getState().getFocusedPointThickness() * 2);
      try {
        Polygon3d rho = Polygon3d.rhombusBy2PntsAngAng(point(0), point(1), valueAsDouble(ANGLE), valueAsDouble(ROT_ANGLE));
        Drawer.drawPolygon(ren, rho.points(), TypeFigure.WIRE);
      } catch (ExGeom ex) {
      }
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
