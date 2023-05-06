package gui.mode;

import bodies.BodyType;
import builders.CircleByThreePointsBuilder;
import geom.Circle3d;
import geom.ExDegeneration;
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
 * Режим построения окружности по трём точкам. Пользователь выбирает три различных якоря, через
 * которые проводится окружность.
 *
 * @author alexeev, rita
 */
public class CreateCircleByThreePointsMode extends CreateBodyBy3PointsMode {

  public CreateCircleByThreePointsMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.CIRCLE_BY_3_POINTS.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.CIRCLE_BY_3_POINTS.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Окружность</strong><br> по трём точкам";
  }

  @Override
  protected void doWhenTwoPointsChosen(MouseEvent e) {
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
  public ModeList alias() {
    return ModeList.MODE_CREATE_CIRCLE_BY_3_POINTS;
  }

  @Override
  protected void create() {
    CircleByThreePointsBuilder builder = new CircleByThreePointsBuilder(_name);
    builder.addA(id(0));
    builder.addB(id(1));
    builder.addC(id(2));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void doIfThreePointsChosen() {
    createWithNameValidation();
  }

  @Override
  protected void doIfThreePointsChosen(MouseWheelEvent e) {
  }

  @Override
  protected void doIfThreePointsChosen(KeyEvent e) {
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую точку",
            "Выберите вторую точку",
            "Выберите третью точку");
  }

  @Override
  protected void setName() {
    _name = BodyType.CIRCLE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _anchor.size(); i++) {
      Drawer.drawPoint(ren, _anchor.get(i).getPoint());
    }
    if (_numOfChosenAnchors == 2 && Vect3d.dist(currPoint, anchor(1).getPoint()) > 0) {
      Drawer.drawPoint(ren, currPoint);
      try {
        Circle3d circle = new Circle3d(anchor(0).getPoint(), anchor(1).getPoint(), currPoint);
        Drawer.drawCircle(ren, circle.radiusLength(), circle.center(), circle.normal(), TypeFigure.WIRE);
      } catch (ExDegeneration ex) {
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
}
