package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_HEIGHT;
import static builders.BodyBuilder.BLDKEY_POINT_NUM;
import builders.PyramidRegularBuilder;
import static config.Config.LOG_LEVEL;
import editor.AnchorType;
import editor.ExNoBody;
import editor.i_Body;
import geom.ExGeom;
import geom.Polygon3d;
import geom.Pyramid3d;
import gui.EdtController;
import gui.IconList;
import gui.dialog.ChoosePointNamesDialog;
import static gui.mode.CreateBodyMode._param;
import static gui.mode.param.CreateModeParamType.HEIGHT;
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
 * Regular pyramid building scenario. User chooses two base points, number of vertices, heightLength
 * and rotation angle.
 *
 * @author alexeev
 */
public class CreateRegularPyramidMode extends CreateBodyBy2PointsMode {

  public CreateRegularPyramidMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(VERT_NUMBER, 1);
    _param.setUsed(HEIGHT, 1);
    _param.setUsed(ROT_ANGLE, 1);
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.REGULAR_PYRAMID.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.REGULAR_PYRAMID.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 2);
  }

  @Override
  public String description() {
    return "<html><strong>Правильная пирамида</strong><br>по двум вершинам основания";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PYRAMID_REGULAR;
  }

  @Override
  protected void create() {
    _numOfChosenAnchors = 0; // prevent drawing
    int vertNum = valueAsInt(VERT_NUMBER);

    String[] defaultPointTitles = new String[vertNum + 1];
    String[] orderedPointTitles = new String[vertNum + 1];

    defaultPointTitles[0] = _anchor.get(0).getTitle();
    defaultPointTitles[1] = _anchor.get(1).getTitle();
    String firstVertTitle = Util.removeSuffix(defaultPointTitles[0]);
    String secondVertTitle = Util.removeSuffix(defaultPointTitles[1]);
    for (int i = 2; i <= vertNum; i++) {
      defaultPointTitles[i] = BodyType.POINT.getName(_ctrl.getEditor(), i - 1);
    }
    for (int i = 0; i < vertNum; i++) {
      orderedPointTitles[i] = firstVertTitle + "_" + String.valueOf(i + 1);
    }
    orderedPointTitles[vertNum] = secondVertTitle;
    boolean[] fixTitles = new boolean[vertNum + 1];
    fixTitles[0] = true;
    fixTitles[1] = true;
    for (int i = 2; i <= vertNum; i++) {
      fixTitles[i] = false;
    }

    PyramidRegularBuilder builder = new PyramidRegularBuilder(_name);
    builder.setValue(BLDKEY_A, id(0));
    builder.setValue(BLDKEY_B, id(1));
    builder.setValue(BLDKEY_HEIGHT, valueAsDouble(HEIGHT));
    builder.setValue(BLDKEY_POINT_NUM, valueAsInt(VERT_NUMBER));
    builder.setValue(BLDKEY_ANGLE, valueAsDouble(ROT_ANGLE));
    _ctrl.add(builder, checker, false);

    // Show dialog for vertices renaming.
    // Show dialog with three options:
    // Choose default alphabetical naming, like ABCDEF...;
    // Choose default indexed naming, like A_1A_2A_3A_4A_5...B;
    // Choose point names by user.
    ChoosePointNamesDialog cpnd = new ChoosePointNamesDialog(_ctrl,
            new String[]{ Util.concat(defaultPointTitles),
              Util.concat(orderedPointTitles) }, defaultPointTitles, fixTitles);
    cpnd.setVisible(true);

    if (cpnd.accepted) {
      if (cpnd.getChoice() != 0) {
        String[] values;
        switch (cpnd.getChoice()) {
          case 1:
            values = orderedPointTitles; // use ordered naming 1
            break;
          default:
            values = cpnd.getValues(); // use user-defined naming
        }
        try {
          i_Body bd = _ctrl.getBody(builder.id());
          for (int i = 0; i < vertNum; i++) {
            _ctrl.renameAnchorPoint(bd.getAnchorID(String.valueOf(i)), values[i]);
          }
          _ctrl.renameAnchorPoint(bd.getAnchorID("top"), values[vertNum]);
          _ctrl.setUndo("Переименование точек пирамиды");
        } catch (ExNoBody ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.printf("Cannot rename pyramid points: %s", ex.getMessage());
          }
        }
        _ctrl.notifyEditorStateChange();
      }
    }

    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первую вершину основания пирамиды{MOUSELEFT}",
            "Отметьте вторую вершину основания пирамиды{MOUSELEFT}",
            "Выберите количество вершин{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите высоту{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите положение пирамиды{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PYRAMID.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      try {
        Drawer.setObjectColor(ren, ColorGL.RED);
        Drawer.drawPoint(ren, _anchor.get(1).getPoint());
        Polygon3d poly = Polygon3d.regPolygonByTwoPoints(
                _anchor.get(0).getPoint(),
                _anchor.get(1).getPoint(),
                valueAsInt(VERT_NUMBER),
                valueAsDouble(ROT_ANGLE));
        Drawer.setObjectColor(ren, isChosen(VERT_NUMBER) ? ColorGL.GREY : ColorGL.RED);
        Drawer.drawPolygon(ren, poly.points(), TypeFigure.WIRE);

        if (isChosen(VERT_NUMBER)) {
          Drawer.setObjectColor(ren, ColorGL.RED);
          Pyramid3d pyr = Pyramid3d.regPyramidBy2PntsHeightAngle(
                  _anchor.get(0).getPoint(),
                  _anchor.get(1).getPoint(),
                  valueAsInt(VERT_NUMBER),
                  valueAsDouble(HEIGHT),
                  valueAsDouble(ROT_ANGLE));
          for (int i = 0; i < pyr.vertNumber() - 1; i++) {
            Drawer.drawSegment(ren, pyr.top(), pyr.base().points().get(i));
          }
        }
      } catch (ExGeom ex) {
      }
    }
    if (_numOfChosenAnchors >= 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
    }
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(VERT_NUMBER);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!isChosen(VERT_NUMBER)) {
      changeValue(VERT_NUMBER, e.getWheelRotation());
      canvas().notifyModeParamChange(VERT_NUMBER);
    } else if (!isChosen(HEIGHT)) {
      changeValue(HEIGHT, e.getWheelRotation());
      canvas().notifyModeParamChange(HEIGHT);
    } else {
      changeValue(ROT_ANGLE, e.getWheelRotation());
      canvas().notifyModeParamChange(ROT_ANGLE);
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!isChosen(VERT_NUMBER)) {
      changeValue(VERT_NUMBER, e);
      canvas().notifyModeParamChange(VERT_NUMBER);
      if (isChosen(VERT_NUMBER)) {
        showValue(HEIGHT);
      }
    } else if (!isChosen(HEIGHT)) {
      changeValue(HEIGHT, e);
      canvas().notifyModeParamChange(HEIGHT);
      if (isChosen(HEIGHT)) {
        showValue(ROT_ANGLE);
      }
    } else {
      changeValue(ROT_ANGLE, e);
      canvas().notifyModeParamChange(ROT_ANGLE);
    }
    if (isChosen(ROT_ANGLE)) {
      create();
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }
}
