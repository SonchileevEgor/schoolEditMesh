package gui.mode;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_A;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import static builders.BodyBuilder.BLDKEY_B;
import static builders.BodyBuilder.BLDKEY_HEIGHT;
import static builders.BodyBuilder.BLDKEY_POINT_NUM;
import builders.PrismRegularBuilder;
import editor.AnchorType;
import geom.ExGeom;
import geom.Prism3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.CreateBodyMode._param;
import static gui.mode.ScreenMode.checker;
import static gui.mode.param.CreateModeParamType.HEIGHT;
import static gui.mode.param.CreateModeParamType.ROT_ANGLE;
import static gui.mode.param.CreateModeParamType.WIDTH;
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
 * Regular pyramid building scenario. User chooses two base points, number of vertices, height and
 * rotation angle.
 *
 * @deprecated
 */
public class CreateRegHexagonalPrismMode extends CreateBodyBy2PointsMode {

  /**
   * 0 - choosing the first vertex 1 - choosing the second vertex 2 - choosing number of vertices 3
   * - choosing height 4 - choosing angle of rotation
   *
   * @param ctrl
   */

  public CreateRegHexagonalPrismMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _param.setUsed(ROT_ANGLE, 1);
    _param.setUsed(HEIGHT, 1);
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.HEXREGULAR_PRISM.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.HEXREGULAR_PRISM.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 2);
  }

  @Override
  public String description() {
    return "<html><strong>Шестиугольная призма</strong><br>по двум вершинам основания";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PRISM_REGULAR;
  }

  protected void create() {
    PrismRegularBuilder builder = new PrismRegularBuilder(BodyType.PRISM.getName(_ctrl.getEditor()));
    builder.setValue(BLDKEY_A, id(0));
    builder.setValue(BLDKEY_B, id(1));
    builder.setValue(BLDKEY_HEIGHT, valueAsDouble(HEIGHT));
    builder.setValue(BLDKEY_ANGLE, valueAsDouble(ROT_ANGLE));
    builder.setValue(BLDKEY_POINT_NUM, 6);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void doIfTwoPointsChosen() {
    showValue(HEIGHT);
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
    if (!isChosen(HEIGHT)) {
      changeValue(HEIGHT, e.getWheelRotation());
      canvas().notifyModeParamChange(HEIGHT);
    } else {
      changeValue(ROT_ANGLE, e.getWheelRotation());
      canvas().notifyModeParamChange(ROT_ANGLE);
    }
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
    if (!isChosen(HEIGHT)) {
      changeValue(HEIGHT, e);
      canvas().notifyModeParamChange(WIDTH);
      if (isChosen(HEIGHT)) {
        showValue(ROT_ANGLE);
      }
    } else {
      changeValue(ROT_ANGLE, e);
      canvas().notifyModeParamChange(ROT_ANGLE);
      if (isChosen(ROT_ANGLE)) {
        create();
      }
    }
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте вершину основания призмы{MOUSELEFT}");
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
                _anchor.get(0).getPoint(), _anchor.get(1).getPoint(), 6,
                valueAsDouble(HEIGHT), valueAsDouble(ROT_ANGLE));
        ArrayList<Vect3d> points0 = pr.base1().points();
        ArrayList<Vect3d> points1 = pr.base2().points();
        ArrayList<Vect3d> points = pr.points();
        if (_numOfChosenAnchors == 2) {
          Drawer.drawPolygon(ren, points0, TypeFigure.WIRE);
        } else {
          for (int i = 0; i < points.size() / 2; i++) {
            Drawer.drawSegment(ren, points.get(i), points.get(i + points.size() / 2));
          }
          Drawer.setObjectColor(ren, (_numOfChosenAnchors == 3) ? ColorGL.RED : ColorGL.GREY);
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
    return false;
  }
}
