package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.CircleCenterPointBuilder;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;
import opengl.scenegl.Scene2d;

/**
 * Circle building scenario. User chooses center of circle and point on circle.
 *
 * @author alexeev
 */
public class Create2dCircleByTwoPointsMode extends CreateBodyBy2PointsMode {

  private Vect3d _viewPlaneNormal;

  @Override
  public void run() {
    super.run();
    _viewPlaneNormal = ((Scene2d)canvas().getScene()).getViewPlane().n();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, point(0));
      Drawer.drawCircle(ren, Vect3d.dist(currPoint, anchor(0).getPoint()),
              anchor(0).getPoint(), _viewPlaneNormal, TypeFigure.WIRE);
    }
  }

  public Create2dCircleByTwoPointsMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CIRCLE_BY_2_POINTS.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CIRCLE_BY_2_POINTS.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Окружность</strong><<br> по центру и точке на окружности";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_CIRCLE_BY_2_POINTS_2D;
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите центр окружности{MOUSELEFT}",
            "Выберите точку на окружности{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CIRCLE.getName(_ctrl.getEditor());
  }

  @Override
  protected void create() {
    CircleCenterPointBuilder builder = new CircleCenterPointBuilder(_name);
    builder.setValue(BodyBuilder.BLDKEY_NORMAL, _viewPlaneNormal);
    builder.setValue(BodyBuilder.BLDKEY_CENTER, id(0));
    builder.setValue(BodyBuilder.BLDKEY_POINT, id(1));
    _ctrl.add(builder, checker, false);
    reset();
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
    createWithNameValidation();
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
    if (!is3d) {
      try {
        currPoint = canvas().getHighlightAdapter().getVect(e.getX(), e.getY());
      } catch (ExDegeneration ex) {
        currPoint = anchor(0).getPoint();
      }
      _ctrl.redraw();
    }
  }
}
