package gui.mode;

import bodies.BodyType;
import bodies.TriangleBody;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_POINT_NUM;
import builders.RegularPolygonBuilder;
import static config.Config.LOG_LEVEL;
import editor.ExNoBody;
import editor.i_Body;
import geom.ExGeom;
import geom.Polygon3d;
import gui.EdtController;
import gui.IconList;
import gui.dialog.ChoosePointNamesDialog;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import static gui.mode.param.CreateModeParamType.VERT_NUMBER;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;
import util.Log;
import util.Util;

/**
 *
 * @author alexeev
 */
public class CreateRegularPolygonMode extends CreateBodyBy2PointsMode {

  public CreateRegularPolygonMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(VERT_NUMBER, 2);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(VERT_NUMBER);
    _param.setUsed(ROT_ANGLE, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую вершину{MOUSELEFT}",
            "Выберите вторую вершину{MOUSELEFT}",
            "Выберите количество вершин{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите угол поворота{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(VERT_NUMBER);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!isChosen(VERT_NUMBER)) {
      changeValue(VERT_NUMBER, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(VERT_NUMBER);
    } else if (!isChosen(ROT_ANGLE)) {
      changeValue(ROT_ANGLE, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!isChosen(VERT_NUMBER)) {
      changeValue(VERT_NUMBER, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(VERT_NUMBER);
      if (isChosen(VERT_NUMBER)) {
        showValue(ROT_ANGLE);
      }
    } else if (!isChosen(ROT_ANGLE)) {
      changeValue(ROT_ANGLE, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    }
    if (isChosen(ROT_ANGLE)) {
      create();// создаём полигон
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  protected void setName() {
    _name = BodyType.POLYGON.getName(_ctrl.getEditor());
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
        Polygon3d poly = Polygon3d.regPolygonByTwoPoints(point(0), point(1),
                valueAsInt(VERT_NUMBER),
                valueAsDouble(ROT_ANGLE));
        Drawer.drawPolygon(ren, poly.points(), TypeFigure.WIRE);
      } catch (ExGeom ex) {
      }
    }
  }

  @Override
  protected void create() {
    _numOfChosenAnchors = 0; // prevent drawing
    int numPoints = valueAsInt(VERT_NUMBER);

    String[] defaultPointTitles = new String[numPoints];
    String[] orderedPointTitles1 = new String[numPoints];
    String[] orderedPointTitles2 = new String[numPoints];
    defaultPointTitles[0] = _anchor.get(0).getTitle();
    defaultPointTitles[1] = _anchor.get(1).getTitle();
    String firstVertTitle = Util.removeSuffix(defaultPointTitles[0]);
    String secondVertTitle = Util.removeSuffix(defaultPointTitles[1]);
    for (int i = 2; i < numPoints; i++) {
      defaultPointTitles[i] = BodyType.POINT.getName(_ctrl.getEditor(), i - 1);
    }
    for (int i = 0; i < numPoints; i++) {
      orderedPointTitles1[i] = firstVertTitle + "_" + String.valueOf(i + 1);
    }
    for (int i = 0; i < numPoints; i++) {
      orderedPointTitles2[i] = secondVertTitle + "_" + String.valueOf(i + 1);
    }
    boolean[] fixTitles = new boolean[numPoints];
    fixTitles[0] = true;
    fixTitles[1] = true;
    for (int i = 2; i < numPoints; i++) {
      fixTitles[i] = false;
    }

    RegularPolygonBuilder builder = new RegularPolygonBuilder();
    builder.setValue(BLDKEY_A, id(0));
    builder.setValue(BLDKEY_B, id(1));
    builder.setValue(BLDKEY_ANGLE, valueAsDouble(ROT_ANGLE));
    builder.setValue(BLDKEY_POINT_NUM, valueAsInt(VERT_NUMBER));
    _ctrl.add(builder, checker, false);

    // Show dialog for vertices renaming.
    // Show dialog with three options:
    // Choose default alphabetical naming, like ABCDEFGHIJ;
    // Choose default indexed naming, like A_1A_2A_3A_4A_5;
    // Choose point names by user.
    ChoosePointNamesDialog cpnd = new ChoosePointNamesDialog(_ctrl,
            new String[]{ Util.concat(defaultPointTitles),
              Util.concat(orderedPointTitles1),
              Util.concat(orderedPointTitles2) }, defaultPointTitles, fixTitles);
    cpnd.setVisible(true);

    if (cpnd.accepted) {
      if (cpnd.getChoice() != 0) {
        String[] values;
        switch (cpnd.getChoice()) {
          case 1:
            values = orderedPointTitles1; // use ordered naming 1
            break;
          case 2:
            values = orderedPointTitles2; // use ordered naming 2
            break;
          default:
            values = cpnd.getValues(); // use user-defined naming
        }
        try {
          i_Body bd = _ctrl.getBody(builder.id());
          if (numPoints > 3) {
            for (int i = 0; i < numPoints; i++) {
              _ctrl.renameAnchorPoint(bd.getAnchorID(String.valueOf(i)), values[i]);
            }
          } else {
            _ctrl.renameAnchorPoint(TriangleBody.BODY_KEY_A, values[0]);
            _ctrl.renameAnchorPoint(TriangleBody.BODY_KEY_B, values[1]);
            _ctrl.renameAnchorPoint(TriangleBody.BODY_KEY_C, values[2]);
          }
          _ctrl.setUndo("Переименование точек многоугольника");
        } catch (ExNoBody ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.printf("Cannot rename polygon points: %s", ex.getMessage());
          }
        }
        _ctrl.notifyEditorStateChange();
      }
    }

    reset();
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_POLYGON_REGULAR;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.REGULAR_POLYGON.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.REGULAR_POLYGON.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Правильный многоугольник</strong><br>по двум точкам";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
