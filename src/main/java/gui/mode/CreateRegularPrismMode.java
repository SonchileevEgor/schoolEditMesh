package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_HEIGHT;
import static builders.BodyBuilder.BLDKEY_POINT_NUM;
import builders.PrismRegularBuilder;
import static config.Config.LOG_LEVEL;
import editor.AnchorType;
import editor.ExNoBody;
import editor.i_Body;
import geom.ExGeom;
import geom.Prism3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import gui.dialog.ChoosePointNamesDialog;
import static gui.mode.param.CreateModeParamType.HEIGHT;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import static gui.mode.param.CreateModeParamType.VERT_NUMBER;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;
import util.Log;
import util.Util;

/**
 * Regular prism building scenario. User chooses two base points, number of vertices, height and
 * rotation angle.
 *
 * @author alexeev
 */
public class CreateRegularPrismMode extends CreateBodyBy2PointsMode {

  public CreateRegularPrismMode(EdtController ctrl) {
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
    return IconList.REGULAR_PRISM.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.REGULAR_PRISM.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 2);
  }

  @Override
  public String description() {
    return "<html><strong>Правильная призма</strong><br>по двум вершинам основания";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PRISM_REGULAR;
  }

  @Override
  protected void create() {
    _numOfChosenAnchors = 0; // prevent drawing
    int vertNum = valueAsInt(VERT_NUMBER);

    String[] defaultPointTitles = new String[vertNum * 2];
    String[] orderedPointTitles = new String[vertNum * 2];

    defaultPointTitles[0] = _anchor.get(0).getTitle();
    defaultPointTitles[1] = _anchor.get(1).getTitle();
    String firstVertTitle = Util.removeSuffix(defaultPointTitles[0]);
    String secondVertTitle = Util.removeSuffix(defaultPointTitles[1]);
    for (int i = 2; i < vertNum * 2; i++) {
      defaultPointTitles[i] = BodyType.POINT.getName(_ctrl.getEditor(), i - 1);
    }
    for (int i = 0; i < vertNum; i++) {
      orderedPointTitles[i] = firstVertTitle + "_" + String.valueOf(i + 1);
      orderedPointTitles[i + vertNum] = secondVertTitle + "_" + String.valueOf(i + 1);
    }
    boolean[] fixTitles = new boolean[vertNum * 2];
    fixTitles[0] = true;
    fixTitles[1] = true;
    for (int i = 2; i < vertNum * 2; i++) {
      fixTitles[i] = false;
    }

    PrismRegularBuilder builder = new PrismRegularBuilder(_name);
    builder.setValue(BLDKEY_A, id(0));
    builder.setValue(BLDKEY_B, id(1));
    builder.setValue(BLDKEY_HEIGHT, valueAsDouble(HEIGHT));
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
          for (int i = 0; i < vertNum * 2; i++) {
            _ctrl.renameAnchorPoint(bd.getAnchorID(String.valueOf(i)), values[i]);
          }
          _ctrl.setUndo("Переименование точек призмы");
        } catch (ExNoBody ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.printf("Cannot rename prism points: %s", ex.getMessage());
          }
        }
        _ctrl.notifyEditorStateChange();
      }
    }

    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первую вершину основания призмы{MOUSELEFT}",
            "Отметьте вторую вершину основания призмы{MOUSELEFT}",
            "Выберите количество вершин{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите высоту{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите положение призмы{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PRISM.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors >= 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
    }
    if (_numOfChosenAnchors >= 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(1).getPoint());
      try {
        Prism3d pr = Prism3d.regPrismBy2PntsHeightAngle(
                _anchor.get(0).getPoint(),
                _anchor.get(1).getPoint(),
                valueAsInt(VERT_NUMBER),
                valueAsDouble(HEIGHT),
                valueAsDouble(ROT_ANGLE));
        ArrayList<Vect3d> points0 = pr.base1().points();
        ArrayList<Vect3d> points1 = pr.base2().points();
        ArrayList<Vect3d> points = pr.points();
        if (!isChosen(VERT_NUMBER)) {
          Drawer.drawPolygon(ren, points0, TypeFigure.WIRE);
        } else {
          for (int i = 0; i < points.size() / 2; i++) {
            Drawer.drawSegment(ren, points.get(i), points.get(i + points.size() / 2));
          }
          Drawer.setObjectColor(ren, isChosen(HEIGHT) ? ColorGL.RED : ColorGL.GREY);
          Drawer.drawPolygon(ren, points1, TypeFigure.WIRE);
          Drawer.drawPolygon(ren, points0, TypeFigure.WIRE);
        }
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
