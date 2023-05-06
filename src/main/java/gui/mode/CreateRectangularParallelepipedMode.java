package gui.mode;

import bodies.BodyType;
import builders.RectangularParallelepipedBuilder;
import static config.Config.LOG_LEVEL;
import editor.ExNoBody;
import editor.i_Body;
import geom.ExGeom;
import geom.Polygon3d;
import geom.Prism3d;
import gui.EdtController;
import gui.IconList;
import gui.dialog.ChoosePointNamesDialog;
import static gui.mode.param.CreateModeParamType.HEIGHT;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import static gui.mode.param.CreateModeParamType.WIDTH;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import static java.lang.Math.PI;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;
import util.Log;
import util.Util;

/**
 * Построение параллелепипеда. Пользователь отмечает две вершины, затем стрелками или колесиком мыши
 * и нажатием клавиши ENTER последовательно выбирает ширину, высоту, угол поворота. После последнего
 * нажатия клавиши ENTER происходит построение параллелепипеда и переход в режим просмотра сцены
 *
 * @author alexeev, rita
 */
public class CreateRectangularParallelepipedMode extends CreateBodyBy2PointsMode {

  public CreateRectangularParallelepipedMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(HEIGHT, 1);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(HEIGHT);
    _param.setUsed(ROT_ANGLE, 2);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    _param.setUsed(WIDTH, 3);
    _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
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
        Polygon3d rectangle = Polygon3d.rectangleBy2PntsAngle(point(0), point(1), valueAsDouble(WIDTH), valueAsDouble(ROT_ANGLE));
        if (isChosen(WIDTH)) {
          Drawer.setObjectColor(ren, ColorGL.GREY);
        } else {
          Drawer.setObjectColor(ren, ColorGL.RED);
        }
        Drawer.drawPolygon(ren, rectangle.points(), TypeFigure.WIRE);
      } catch (ExGeom ex) {
      }

      try {
        Polygon3d rectangle = Polygon3d.rectangleBy2PntsAngle(point(0), point(1),
                valueAsDouble(HEIGHT), valueAsDouble(ROT_ANGLE) + PI / 2);
        if (isChosen(WIDTH)) {
          if (isChosen(HEIGHT)) {
            Drawer.setObjectColor(ren, ColorGL.GREY);
          } else {
            Drawer.setObjectColor(ren, ColorGL.RED);
          }
          Drawer.drawPolygon(ren, rectangle.points(), TypeFigure.WIRE);
        }
      } catch (ExGeom ex) {
      }

      try {
        if (isChosen(WIDTH) && isChosen(HEIGHT)) {
          Prism3d pr = Prism3d.recParallepBy2PntsAngle(anchor(0).getPoint(), anchor(1).getPoint(),
                  valueAsDouble(HEIGHT), valueAsDouble(WIDTH), valueAsDouble(ROT_ANGLE));
          Drawer.setObjectColor(ren, ColorGL.RED);
          Drawer.drawParallelepiped(ren, pr.points().get(0), pr.points().get(1), pr.points().get(3), pr.points().get(4), TypeFigure.WIRE);
        }
      } catch (ExGeom ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую вершину параллелепипеда{MOUSELEFT}",
            "Выберите вторую вершину параллелепипеда{MOUSELEFT}",
            "Выберите ширину{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите высоту{UP}{DOWN}{MOUSEWHEEL}{ENTER}",
            "Выберите угол поворота параллелепипеда{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PARALLELEPIPED.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PARALLEP.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PARALLEP.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Прямоугольный параллелепипед</strong><br>по двум вершинам основания";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RECTANGULAR_PARALLELEPIPED;
  }

  @Override
  protected void create() {
    _numOfChosenAnchors = 0; // prevent drawing

    String[] labels;
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

    RectangularParallelepipedBuilder builder = new RectangularParallelepipedBuilder(_name);
    builder.addA(id(0));
    builder.addB(id(1));
    builder.addAngle(valueAsDouble(ROT_ANGLE));
    builder.addHeight(valueAsDouble(HEIGHT));
    builder.addWidth(valueAsDouble(WIDTH));
    _ctrl.add(builder, checker, false);

    String[] ordTitles;

    boolean[] fixTitles = new boolean[]{ true, true, false, false, false, false, false, false };

    if (!Util.hasNumericSuffix(a) && !Util.hasNumericSuffix(b)) {
      ordTitles = new String[]{ a, b, c, d, a + "_1", b + "_1", c + "_1", d + "_1" };
      labels = new String[]{ Util.concat(defTitles), Util.concat(ordTitles) };
    } else {
      ordTitles = new String[8];
      labels = new String[]{ Util.concat(defTitles) };
    }

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
          for (int i = 2; i < 8; i++) {
            _ctrl.renameAnchorPoint(bd.getAnchorID(String.valueOf(i)), values[i]);
          }
          _ctrl.setUndo("Переименование точек параллелепипеда");
        } catch (ExNoBody ex) {
          if (LOG_LEVEL.value() >= 1) {
            Log.out.printf("Cannot rename parallelepiped points: %s", ex.getMessage());
          }
        }
        _ctrl.notifyEditorStateChange();
      }

      reset();
    } else {
      exit();
    }
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(WIDTH);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!isChosen(WIDTH)) {
      changeValue(WIDTH, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
    } else if (!isChosen(HEIGHT) && isChosen(WIDTH)) {
      changeValue(HEIGHT, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(HEIGHT);
    } else {
      changeValue(ROT_ANGLE, e.getWheelRotation());
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(ROT_ANGLE);
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!isChosen(WIDTH)) {
      changeValue(WIDTH, e);
      _ctrl.getMainCanvasCtrl().notifyModeParamChange(WIDTH);
      if (isChosen(WIDTH)) {
        showValue(HEIGHT);
      }
    } else if (!isChosen(HEIGHT) && isChosen(WIDTH)) {
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
      create();
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
