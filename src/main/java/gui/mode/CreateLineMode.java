package gui.mode;

import bodies.BodyType;
import builders.LineByTwoPointsBuilder;
import editor.ExNoAnchor;
import geom.ExDegeneration;
import gui.EdtController;
import gui.IconList;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 * Line building script. User chooses two points.
 *
 * @author alexeev, rita
 */
public class CreateLineMode extends CreateBodyBy2PointsMode {

  public CreateLineMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.LINE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.LINE.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Прямая</strong><br>по двум точкам";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_LINE;
  }

  @Override
  protected void create() {
    try {
      _name = _ctrl.getAnchorTitle(id(0)) + _ctrl.getAnchorTitle(id(1));
    } catch (ExNoAnchor ex) {
      _name = BodyType.LINE.getName(_ctrl.getEditor());
    }
    LineByTwoPointsBuilder builder = new LineByTwoPointsBuilder(_name);
    builder.addA(id(0));
    builder.addB(id(1));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void doIfTwoPointsChosen() {
    create();
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
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую точку{MOUSELEFT}", "Выберите вторую точку{MOUSELEFT}");
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, point(0));
      Drawer.drawPoint(ren, currPoint);
      Drawer.drawLine(ren, point(0), currPoint);
    }
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
