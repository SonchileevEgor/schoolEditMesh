package gui.mode;

import bodies.BodyType;
import builders.CubeByTwoPointsAndAngleBuilder;
import static config.Config.LOG_LEVEL;
import editor.ExNoBody;
import editor.i_Body;
import geom.Cube3d;
import geom.ExDegeneration;
import gui.EdtController;
import gui.IconList;
import gui.dialog.ChoosePointNamesDialog;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
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
 * Построение куба по двум вершинам. Пользователь отмечает две вершины, после чего нажатием клавиш
 * ВВЕРХ и ВНИЗ определяет положение куба по нажатию клавиши ENTER происходит переход в режим
 * просмотра сцены
 *
 * @author alexeev
 */
public class CreateCubeMode extends CreateBodyBy2PointsMode {

  public CreateCubeMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(ROT_ANGLE, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    _ctrl.redraw();
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      try {
        Cube3d cube = Cube3d.cube3dByTwoPointsAndAngle(_anchor.get(0).getPoint(),
                _anchor.get(1).getPoint(), valueAsDouble(ROT_ANGLE));
        Drawer.drawParallelepiped(ren, cube.A1(), cube.B1(), cube.D1(), cube.A2(), TypeFigure.WIRE);
      } catch (ExDegeneration ex) {
      }
      Drawer.drawPoint(ren, _anchor.get(1).getPoint(), _anchor.get(1).getState().getFocusedPointThickness() * 2);
    }
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint(), _anchor.get(0).getState().getFocusedPointThickness() * 2);
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую вершину куба{MOUSELEFT}",
            "Выберите вторую вершину куба{MOUSELEFT}",
            "Выберите положение куба{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CUBE.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CUBE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CUBE.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Куб</strong><br>по двум вершинам основания";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_CUBE;
  }

  @Override
  protected void create() {
    _numOfChosenAnchors = 0; // prevent drawing

    String a = _anchor.get(0).getTitle();
    String b = _anchor.get(1).getTitle();
    String c = BodyType.POINT.getName(_ctrl.getEditor());
    String d = BodyType.POINT.getName(_ctrl.getEditor(), 2);

    String[] defTitles = new String[]{ a, b, c, d,
      BodyType.POINT.getName(_ctrl.getEditor(), 3),
      BodyType.POINT.getName(_ctrl.getEditor(), 4),
      BodyType.POINT.getName(_ctrl.getEditor(), 5),
      BodyType.POINT.getName(_ctrl.getEditor(), 6)
    };

    CubeByTwoPointsAndAngleBuilder builder = new CubeByTwoPointsAndAngleBuilder(_name);
    builder.addA(id(0));
    builder.addB(id(1));
    builder.addAngle(valueAsDouble(ROT_ANGLE));
    _ctrl.add(builder, checker, false);

    String[] labels;
    String[] ordTitles;

    boolean[] fixTitles = new boolean[]{ true, true, false, false, false, false, false, false };

    if (!Util.hasNumericSuffix(a) && !Util.hasNumericSuffix(b)) {
      ordTitles = new String[]{ a, b, c, d, a + "_1", b + "_1", c + "_1", d + "_1" };
      labels = new String[]{ Util.concat(defTitles), Util.concat(ordTitles) };
    } else {
      ordTitles = new String[8];
      labels = new String[]{ Util.concat(defTitles) };
    }

    // Show dialog for vertices renaming.
    // Show dialog with three options:
    // Choose default alphabetical naming, like ABCDEFGH;
    // Choose default indexed naming, like ABCDA_1B_1C_1D_1;
    // Choose point names by user.
    ChoosePointNamesDialog cpnd = new ChoosePointNamesDialog(_ctrl, labels, defTitles, fixTitles);
    cpnd.setVisible(true);

    if (cpnd.accepted) {
      if (cpnd.getChoice() != 0) {
        String[] values;
        switch (cpnd.getChoice()) {
          case 1:
            values = ordTitles; // use ordered naming
            break;
          default:
            values = cpnd.getValues(); // use user-defined naming
        }
        try {
          i_Body bd = _ctrl.getBody(builder.id());
          _ctrl.renameAnchorPoint(bd.getAnchorID("C1"), values[2]);
          _ctrl.renameAnchorPoint(bd.getAnchorID("D1"), values[3]);
          _ctrl.renameAnchorPoint(bd.getAnchorID("A2"), values[4]);
          _ctrl.renameAnchorPoint(bd.getAnchorID("B2"), values[5]);
          _ctrl.renameAnchorPoint(bd.getAnchorID("C2"), values[6]);
          _ctrl.renameAnchorPoint(bd.getAnchorID("D2"), values[7]);
          _ctrl.setUndo("Переименование точек куба");
        } catch (ExNoBody ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.printf("Cannot rename cube points: %s", ex.getMessage());
          }
        }
        _ctrl.notifyEditorStateChange();
      }
    }
    exit();
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
      create();// создаём куб
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
    return false;
  }
}
