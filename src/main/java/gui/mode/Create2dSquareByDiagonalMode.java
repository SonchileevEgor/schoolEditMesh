package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.SquareByTwoPoints2dBuilder;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.Polygon3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 *
 * @author ???
 */
public class Create2dSquareByDiagonalMode extends CreateBodyBy2PointsMode {

  public Create2dSquareByDiagonalMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    if (_numOfChosenAnchors == 1) {
      Drawer.drawPoint(ren, point(0), anchor(0).getState().getFocusedPointThickness() * 2);
      Drawer.drawPoint(ren, currPoint);
      if (Vect3d.dist(currPoint, anchor(0).getPoint()) > 0) {
        try {
          Polygon3d poly = Polygon3d.square2dBy2PntsOfDiag(point(0), currPoint);
          Drawer.drawPolygon(ren, poly.points(), TypeFigure.WIRE);
        } catch (ExGeom ex) {
        }
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Отметьте первую точку диагонали{MOUSELEFT}",
            "Отметьте вторую точку диагонали{MOUSELEFT}");
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

  @Override
  protected void create() {
    SquareByTwoPoints2dBuilder builder = new SquareByTwoPoints2dBuilder(_name);
    builder.setValue(BodyBuilder.BLDKEY_A, id(0));
    builder.setValue(BodyBuilder.BLDKEY_C, id(1));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_SQUARE_BY_DIAGONAL_2D;
  }

  @Override
  protected void setName() {
    _name = BodyType.POLYGON.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.SQUARE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.SQUARE.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Квадрат</strong><br> по двум противоположным вершинам";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
