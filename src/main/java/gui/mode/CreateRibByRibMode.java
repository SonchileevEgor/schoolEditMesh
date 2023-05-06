package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.RibByRibBuilder;
import editor.AnchorType;
import editor.i_Anchor;
import editor.i_FocusChangeListener;
import java.util.Collection;
import gui.EdtController;
import gui.IconList;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Drawer;
import opengl.Render;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Лютенков
 */
public class CreateRibByRibMode extends CreateBodyMode implements i_FocusChangeListener {

  private ArrayList<BodyType> _types;
  BodyType _type;

  public CreateRibByRibMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void nativeDraw1(Render ren) {
    if (_numOfChosenAnchors == 2) {
      Drawer.setObjectColor(ren, ColorGL.RED);
      Drawer.drawPoint(ren, point(1));
    }
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<BodyType>();
    _types.add(BodyType.RIB);
    canvas().getHighlightAdapter().setTypes(_types);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
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
    if (_numOfChosenAnchors == 0) {
      i_Anchor a = getRibAnchor(id);
      chooseAnchor(a);
      canvas().getHighlightAdapter().setTypes(BodyType.POINT);
      canvas().getHighlightAdapter().setCreatePointMode(true);
    } else if (_numOfChosenAnchors == 1) {
      i_Anchor a = getPointAnchor(id);
      chooseAnchor(a);
    } else if (_numOfChosenAnchors == 2) {
      i_Anchor a = getPointAnchor(id);
      if (!checker.checkPointsDifferent(_anchorID, a.id())) {
        return;
      }
      chooseAnchor(a);
      createWithNameValidation();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите отрезок{MOUSELEFT}", "Выберите первую точку{MOUSELEFT}",
            "Выберите вторую точку{MOUSELEFT}");
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.RIB_BY_RIB.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.RIB_BY_RIB.getLargeIcon();
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_RIB_BY_RIB;
  }

  @Override
  protected void setName() {
    _name = BodyType.RIB.getName(_ctrl.getEditor());
  }

  @Override
  protected void create() {
    RibByRibBuilder builder = new RibByRibBuilder();
    builder.setValue(BodyBuilder.BLDKEY_NAME, _name);
    builder.setValue(BodyBuilder.BLDKEY_A, id(1));
    builder.setValue(BodyBuilder.BLDKEY_B, id(2));
    builder.setValue(BodyBuilder.BLDKEY_RIB, id(0));
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Отрезок</strong><br>равный данному";
  }

  @Override
  protected boolean isEnabled() {
    return (_ctrl.containsAnchors(AnchorType.ANC_RIB, 1)
            && _ctrl.containsAnchors(AnchorType.ANC_POINT, 1)
            && _ctrl.containsAnchors(AnchorType.ANC_POINT, 1));
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
