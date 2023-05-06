package gui.mode;

import bodies.BodyType;
import builders.MiddlePerpendicularToRibBuilder;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 * Middle perpendicular building scenario. User chooses rib on scene.
 *
 * @author evk
 */
public class Create2dMiddlePerpendicularMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _id1;

  public Create2dMiddlePerpendicularMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.RIB);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите отрезок{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.LINE.getName(_ctrl.getEditor());
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.MID_PERP.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.MID_PERP.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return _ctrl.containsAnchors(AnchorType.ANC_RIB, 1);
  }

  public String shortDescription() {
    return "<html><strong>Срединный перпендикуляр отрезка";
  }

  public String longDescription() {
    return "<html><strong>Срединный перпендикуляр отрезка";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_2D_MIDDLE_PERPENDICULAR;
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) {
      return;
    }
    i_Anchor a = getRibAnchor(id);
    if (a == null) {
      return;
    }
    _id1 = a.id();
    createWithNameValidation();
  }

  @Override
  protected void create() {
    MiddlePerpendicularToRibBuilder perp_builder = new MiddlePerpendicularToRibBuilder(_name);
    perp_builder.addRib(_id1);
    _ctrl.add(perp_builder, checker, false);

    //!! TODO: добавить прямые углы
    reset();
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  protected String description() {
    return "<html><strong>Срединный перпендикуляр отрезка";
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
