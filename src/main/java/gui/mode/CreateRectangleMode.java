package gui.mode;

import bodies.BodyType;
import builders.AngleByThreePointsBuilder;
import builders.RectangleBuilder;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_BodyBuilder;
import geom.ExGeom;
import geom.Polygon3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.param.CreateModeParamType.HEIGHT;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 *
 * @author rita
 */
public class CreateRectangleMode extends CreateBodyBy2PointsMode {

  public CreateRectangleMode(EdtController ctrl) {
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
        Polygon3d poly = Polygon3d.rectangleBy2PntsAngle(point(0), point(1),
                valueAsDouble(HEIGHT), valueAsDouble(ROT_ANGLE));
        Drawer.drawPolygon(ren, poly.points(), TypeFigure.WIRE);
      } catch (ExGeom ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первую вершину прямоугольника{MOUSELEFT}",
            "Отметьте вторую вершину прямоугольника{MOUSELEFT}",
            "Выберите высоту{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите угол поворота прямоугольника{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
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
      createWithNameValidation();// создаём параллелепипед
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  protected void setName() {
    _name = BodyType.RECTANGLE.getName(_ctrl.getEditor());
  }

  @Override
  protected void create() {
    RectangleBuilder builder = new RectangleBuilder(_name);
    builder.addA(id(0));
    builder.addB(id(1));
    builder.addHeight(valueAsDouble(HEIGHT));
    builder.addRotationAngle(valueAsDouble(ROT_ANGLE));
    _ctrl.add(builder, checker, false);

    // добавляем отметки прямых углов
    try {
      ArrayList<i_BodyBuilder> builders = new ArrayList<>();
      // находим вершины прямоугольника
      String[] vertexIDs = new String[]{
        _ctrl.getAnchorID(builder.id(), "0"),
        _ctrl.getAnchorID(builder.id(), "1"),
        _ctrl.getAnchorID(builder.id(), "2"),
        _ctrl.getAnchorID(builder.id(), "3"), };
      String[] titles = new String[]{
        _ctrl.getAnchorTitle(builder.id(), "0"),
        _ctrl.getAnchorTitle(builder.id(), "1"),
        _ctrl.getAnchorTitle(builder.id(), "2"),
        _ctrl.getAnchorTitle(builder.id(), "3")
      };
      for (int i = 0; i < 4; i++) {
        AngleByThreePointsBuilder angleBuilder = new AngleByThreePointsBuilder(
                titles[i] + titles[(i + 1) % 4] + titles[(i + 2) % 4]);
        angleBuilder.addPointOnFirstSide(vertexIDs[i]);
        angleBuilder.addVertex(vertexIDs[(i + 1) % 4]);
        angleBuilder.addPointOnSecondSide(vertexIDs[(i + 2) % 4]);
        angleBuilder.addLessThanPiParam(true);
        builders.add(angleBuilder);
      }
      _ctrl.addBodies(builders, checker, false);
    } catch (ExNoAnchor | ExNoBody ex) {
    }

    reset();
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RECTANGLE;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RECTANGLE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RECTANGLE.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Прямоугольник</strong><br>по двум вершинам и высоте";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
