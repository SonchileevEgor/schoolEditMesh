package gui.mode;

import geom.Arc3d;
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

/**
 *
 * @author rita
 */
public class Create2dHalfCircleMode extends CreateHalfCircleMode {

  public Create2dHalfCircleMode(EdtController ctrl) {
    super(ctrl);
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

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      try {
        Drawer.setObjectColor(ren, ColorGL.RED);
        Drawer.drawPoint(ren, _anchor.get(0).getPoint());
        if (Vect3d.dist(currPoint, anchor(0).getPoint()) > 0) {
          Arc3d arc = Arc3d.halfCircleBy2PntsAngle(_anchor.get(0).getPoint(), currPoint, 0);
          Drawer.drawArc(ren, arc, TypeFigure.CURVE);
        }
      } catch (ExDegeneration ex) {
      }
    }
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
  public ModeList alias() {
    return ModeList.MODE_CREATE_HALF_CIRCLE_2D;
  }
}
