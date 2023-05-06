package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.SphereByTwoPointsBuilder;
import editor.AnchorType;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 * Sphere building scenario. User chooses center and point on sphere.
 *
 * @author alexeev
 */
public class CreateSphereByTwoPointsMode extends CreateBodyBy2PointsMode {

  public CreateSphereByTwoPointsMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.SPHERE_BY_2_POINTS.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.SPHERE_BY_2_POINTS.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 2);
  }

  @Override
  public String description() {
    return "<html><strong>Сфера</strong><br>по центру и точке на поверхности";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_SPHERE_BY_2_POINTS;
  }

  @Override
  protected void create() {
    SphereByTwoPointsBuilder builder = new SphereByTwoPointsBuilder(_name);
    builder.setValue(BodyBuilder.BLDKEY_CENTER, id(0));
    builder.setValue(BodyBuilder.BLDKEY_P, id(1));
    _ctrl.add(builder, checker, false);
    reset();
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
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите центр сферы{MOUSELEFT}", "Выберите точку на сфере{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.SPHERE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 1) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, _anchor.get(0).getPoint());
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
