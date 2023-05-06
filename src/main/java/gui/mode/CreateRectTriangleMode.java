package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_HEIGHT;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.RectTriangleBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.ExDegeneration;
import geom.Triang3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.HEIGHT;
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
public class CreateRectTriangleMode extends CreateBodyBy2PointsMode {

  public CreateRectTriangleMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(HEIGHT, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(HEIGHT);
    _param.setUsed(ROT_ANGLE, 2);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, point(0), anchor(0).getState().getFocusedPointThickness() * 2);
    }
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, point(1), anchor(1).getState().getFocusedPointThickness() * 2);
      try {
        Triang3d poly = Triang3d.rectTriangleBy2PntsAngle(point(0), point(1),
                valueAsDouble(HEIGHT), valueAsDouble(ROT_ANGLE));
        Drawer.drawPolygon(ren, poly.points(), TypeFigure.WIRE);
      } catch (ExDegeneration ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первую вершину прямоугольного треугольника{MOUSELEFT}",
            "Отметьте вторую вершину прямоугольного треугольника{MOUSELEFT}",
            "Выберите высоту{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите угол поворота{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(HEIGHT);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!isChosen(HEIGHT)) {
      changeValue(HEIGHT, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(HEIGHT);
    } else {
      changeValue(ROT_ANGLE, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!isChosen(HEIGHT)) {
      changeValue(HEIGHT, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(HEIGHT);
      if (isChosen(HEIGHT)) {
        showValue(ROT_ANGLE);
      }
    } else {
      changeValue(ROT_ANGLE, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    }
    if (isChosen(ROT_ANGLE)) {
      createWithNameValidation();// создаём прямоугольный треугольник
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  protected void setName() {
    _name = BodyType.RECT_TRIANGLE.getName(_ctrl.getEditor());
  }

  @Override
  protected void create() {
    HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
    params.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _name));
    params.put(BLDKEY_A, new BuilderParam(BLDKEY_A, "Вершина острого угла", BuilderParamType.ANCHOR, id(0)));
    params.put(BLDKEY_B, new BuilderParam(BLDKEY_B, "Вершина прямого угла", BuilderParamType.ANCHOR, id(1)));
    params.put(BLDKEY_HEIGHT, new BuilderParam(BLDKEY_HEIGHT, "Длина катета", BuilderParamType.DOUBLE_POSITIVE, valueAsDouble(HEIGHT)));
    params.put(BLDKEY_ANGLE, new BuilderParam(BLDKEY_ANGLE, "Угол поворота", BuilderParamType.ANGLE_ROTATION, valueAsDouble(ROT_ANGLE)));
    RectTriangleBuilder builder = new RectTriangleBuilder(params);

    //!! TODO: добавить прямые углы
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RECT_TRIANGLE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RECT_TRIANGLE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RECT_TRIANGLE.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Прямоугольный треугольник</strong><br>по точкам катета и высоте";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
