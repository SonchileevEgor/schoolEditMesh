package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.OctahedronBuilder;
import static config.Config.LOG_LEVEL;
import editor.ExNoBody;
import editor.i_Body;
import geom.ExGeom;
import geom.Octahedron3d;
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
 * Create octahedron by two points and angle.
 *
 * @author kaznin
 */
public class CreateOctahedronMode extends CreateBodyBy2PointsMode {

  public CreateOctahedronMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(ROT_ANGLE, 1);
    canvas().notifyModeParamChange(ROT_ANGLE);
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      try {
        Octahedron3d okt = new Octahedron3d(_anchor.get(0).getPoint(),
                _anchor.get(1).getPoint(), valueAsDouble(ROT_ANGLE));
        Drawer.drawOctahedron(ren, okt, TypeFigure.WIRE);
      } catch (ExGeom ex) {
      }
      Drawer.drawPoint(ren, _anchor.get(1).getPoint(),
              _anchor.get(1).getState().getFocusedPointThickness() * 2);
    }
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint(),
              _anchor.get(0).getState().getFocusedPointThickness() * 2);
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую вершину октаэдра{MOUSELEFT}",
            "Выберите вторую вершину октаэдра{MOUSELEFT}",
            "Выберите положение октаэдра{UP}{DOWN}{MOUSEWHEEL}{ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.OCTAHEDRON.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.OCTAHEDRON.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.OCTAHEDRON.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Октаэдр</strong><br>по двум вершинам основания";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_OCTAHEDRON;
  }

  @Override
  protected void create() {
    _numOfChosenAnchors = 0; // prevent drawing

    String[] defaultPointTitles = new String[6];
    String[] orderedPointTitles1 = new String[6];
    String[] orderedPointTitles2 = new String[6];
    defaultPointTitles[0] = _anchor.get(0).getTitle();
    defaultPointTitles[1] = _anchor.get(1).getTitle();
    String firstVertTitle = Util.removeSuffix(defaultPointTitles[0]);
    String secondVertTitle = Util.removeSuffix(defaultPointTitles[1]);
    for (int i = 2; i < 6; i++) {
      defaultPointTitles[i] = BodyType.POINT.getName(_ctrl.getEditor(), i - 1);
    }
    for (int i = 0; i < 6; i++) {
      orderedPointTitles1[i] = firstVertTitle + "_" + String.valueOf(i + 1);
    }
    for (int i = 0; i < 6; i++) {
      orderedPointTitles2[i] = secondVertTitle + "_" + String.valueOf(i + 1);
    }
    boolean[] fixTitles = new boolean[6];
    fixTitles[0] = true;
    fixTitles[1] = true;
    for (int i = 2; i < 6; i++) {
      fixTitles[i] = false;
    }

    OctahedronBuilder builder = new OctahedronBuilder();
    builder.setValue(BodyBuilder.BLDKEY_NAME, _name);
    builder.setValue(BodyBuilder.BLDKEY_A, _anchor.get(0).id());
    builder.setValue(BodyBuilder.BLDKEY_B, _anchor.get(1).id());
    builder.setValue(BodyBuilder.BLDKEY_ANGLE, valueAsDouble(ROT_ANGLE));
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
          _ctrl.renameAnchorPoint(bd.getAnchorID("A"), values[0]);
          _ctrl.renameAnchorPoint(bd.getAnchorID("B"), values[1]);
          _ctrl.renameAnchorPoint(bd.getAnchorID("C"), values[2]);
          _ctrl.renameAnchorPoint(bd.getAnchorID("D"), values[3]);
          _ctrl.renameAnchorPoint(bd.getAnchorID("Topd"), values[4]);
          _ctrl.renameAnchorPoint(bd.getAnchorID("Topu"), values[5]);

          _ctrl.setUndo("Переименование точек октаэдра");
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
  protected void doIfTwoPointsChosen() {
    showValue(ROT_ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    changeValue(ROT_ANGLE, e.getWheelRotation());
    canvas().notifyModeParamChange(ROT_ANGLE);
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    changeValue(ROT_ANGLE, e);
    canvas().notifyModeParamChange(ROT_ANGLE);
    if (isChosen(ROT_ANGLE)) {
      createWithNameValidation();// создаём Октаэдр
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
