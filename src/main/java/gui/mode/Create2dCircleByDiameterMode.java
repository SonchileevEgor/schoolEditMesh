package gui.mode;

import geom.Circle3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;
import opengl.scenegl.Scene2d;

/**
 *
 * @author Лютенков
 */
public class Create2dCircleByDiameterMode extends CreateCircleByDiameterMode {

  private Vect3d _viewPlaneNormal;

  public Create2dCircleByDiameterMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _viewPlaneNormal = ((Scene2d)canvas().getScene()).getViewPlane().n();
  }

  @Override
  protected void doIfTwoPointsChosen(MouseWheelEvent e) {
  }

  @Override
  protected void doIfTwoPointsChosen(KeyEvent e) {
  }

  @Override
  protected void doIfTwoPointsChosen() {
    createWithNameValidation();
  }

  @Override
  protected void doWhenOnePointChosen(MouseEvent e) {
    try {
      currPoint = canvas().getHighlightAdapter().getVect(e.getX(), e.getY());
    } catch (ExDegeneration ex) {
      currPoint = anchor(0).getPoint();
    }
    _ctrl.redraw();
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _numOfChosenAnchors; i++) {
      Drawer.drawPoint(ren, point(i));
    }
    if (_numOfChosenAnchors == 1) {
      Circle3d circ = new Circle3d(Vect3d.dist(anchor(0).getPoint(), currPoint) * 0.5,
              _viewPlaneNormal, Vect3d.conv_hull(currPoint, anchor(0).getPoint(), 0.5));
      Drawer.drawCircle(ren, circ.radiusLength(), circ.center(), circ.normal(), TypeFigure.WIRE);
    }
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_CIRCLE_BY_DIAMETER_2D;
  }
}
