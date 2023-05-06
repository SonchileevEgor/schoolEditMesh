package gui.mode;

import builders.RibBuilder;
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
 * Segment building script. User chooses two endpoints.
 *
 * @author alexeev
 */
public class CreateRibMode extends CreateBodyBy2PointsMode {

  public CreateRibMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, point(0));
      Drawer.drawPoint(ren, currPoint);
      Drawer.drawSegment(ren, point(0), currPoint);
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую точку{MOUSELEFT}",
            "Выберите вторую точку{MOUSELEFT}");
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RIB.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RIB.getLargeIcon();
  }

  @Override
  public String description() {
    return "<html><strong>Отрезок</strong><br>по двум точкам";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RIB;
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void create() {
    RibBuilder builder = new RibBuilder(_anchor.get(0).getTitle() + _anchor.get(1).getTitle());
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
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
