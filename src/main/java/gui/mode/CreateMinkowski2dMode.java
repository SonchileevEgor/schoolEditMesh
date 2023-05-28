package gui.mode;

import bodies.BodyType;
import bodies.TriangleBody;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_B;
import builders.MinkowskiSumBuilder;
import static config.Config.LOG_LEVEL;
import editor.ExNoBody;
import editor.i_Body;
import geom.ExDegeneration;
import geom.Minkowski2d;
import geom.ColoredVector3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import gui.dialog.ChoosePointNamesDialog;
import static gui.mode.param.CreateModeParamType.VERT_NUMBER;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeArrow;
import opengl.colorgl.ColorGL;
import util.Log;
import util.Util;

/**
 *
 * @author e-son
 */
public class CreateMinkowski2dMode extends CreateBodyMode {

  public CreateMinkowski2dMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Это первый шаг алгоритма {ENTER}");
  }

  @Override
  protected void setName() {
    _name = BodyType.POLYGON.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
//    if (_numOfChosenAnchors == 1) {
//        MinkowskiSumBuilder builder = new MinkowskiSumBuilder("ТЕСТ123");
//        builder.setValue(BLDKEY_A, _ctrl.getFocusCtrl().getFocusedBodies().get(0));
//        builder.setValue(BLDKEY_B, _ctrl.getFocusCtrl().getFocusedBodies().get(1));
        Minkowski2d sumRes = null;
        try {
            sumRes = new Minkowski2d(
                    _ctrl.getBody(_ctrl.getFocusCtrl().getFocusedBodies().get(0)), 
                    _ctrl.getBody(_ctrl.getFocusCtrl().getFocusedBodies().get(1)),
                    _ctrl.getEditor().getBodySurfaceColor(_ctrl.getFocusCtrl().getFocusedBodies().get(0)),
                    _ctrl.getEditor().getBodySurfaceColor(_ctrl.getFocusCtrl().getFocusedBodies().get(1)));
        }catch (ExNoBody ex) {
            Logger.getLogger(CreateMinkowski2dMode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExDegeneration ex) {
          Logger.getLogger(CreateMinkowski2dMode.class.getName()).log(Level.SEVERE, null, ex);
      }
//        MinkowskiBody bd = (MinkowskiBody) builder.create(_ctrl.getEditor(), null);
      
//        MinkowskiBody bd = (MinkowskiBody) builder.create(_ctrl.getEditor(), null);

        Drawer.setLineWidth(ren, 2.0);
        ArrayList<ColoredVector3d> vects = sumRes.getVectors();
        for (int i = 0; i < vects.size(); i++) {
            Drawer.setObjectColor(ren, vects.get(i).getColor());
            Drawer.draw2DArrow(ren, vects.get(i).start, vects.get(i).end, TypeArrow.ONE_END);
//            Drawer.drawLine(ren, vects.get(i).a(), vects.get(i).b());
        }
//    }
        
//        _numOfChosenAnchors++;
//
//    _ctrl.add(builder, null, false);
//    }
//    _numOfChosenAnchors++;
  }

  @Override
  protected void create() {
//    _numOfChosenAnchors = 0; // prevent drawing

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

    MinkowskiSumBuilder builder = new MinkowskiSumBuilder("ТЕСТ123");
    builder.setValue(BLDKEY_A, _ctrl.getFocusCtrl().getFocusedBodies().get(0));
    builder.setValue(BLDKEY_B, _ctrl.getFocusCtrl().getFocusedBodies().get(1));

    _ctrl.add(builder, null, false);

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
    return ModeList.MODE_CREATE_MINKOWSKI_SUM_2D;
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
    return "<html><strong>Сумма Минковского</strong><br>двух многоугольников";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }

  @Override
  protected boolean isEnabled() {
      return true;
  }
}