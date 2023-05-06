package gui.mode;

import bodies.BodyType;
import builders.AllBuildersManager;
import builders.PlaneThreePointsBuilder;
import editor.AnchorType;
import editor.i_BodyBuilder;
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
 * Plane building scenario. User chooses three points on scene.
 *
 * @author alexeev
 */
public class CreatePlaneMode extends CreateBodyBy3PointsMode {

  public CreatePlaneMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void doWhenTwoPointsChosen(MouseEvent e) {
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PLANE_BY_3_POINTS.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PLANE_BY_3_POINTS.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_POINT, 3);
  }

  @Override
  public String description() {
    return "<html><strong>Плоскость</strong><br>по трём точкам";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PLANE_BY_3_POINTS;
  }

  @Override
  protected void create() {
    i_BodyBuilder builder = AllBuildersManager.create(PlaneThreePointsBuilder.ALIAS,
            _name, id(0), id(1), id(2));
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
    _msg.setMessage("Выберите первую точку{MOUSELEFT}",
            "Выберите вторую точку{MOUSELEFT}",
            "Выберите третью точку{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PLANE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
    Drawer.setObjectColor(ren, ColorGL.RED);
    for (int i = 0; i < _numOfChosenAnchors; i++) {
      Drawer.drawPoint(ren, _anchor.get(i).getPoint());
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
