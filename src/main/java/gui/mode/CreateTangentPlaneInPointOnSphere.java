package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.TangentPlaneInPointOnSphereBuilder;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_Body;
import java.util.Collection;
import editor.i_FocusChangeListener;
import java.util.Collection;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author gayduk
 */
public class CreateTangentPlaneInPointOnSphere extends CreateBodyMode implements i_FocusChangeListener {

  private String _pointID, _sphereID;

  public CreateTangentPlaneInPointOnSphere(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.SPHERE);
    setCursor(Cursor.getDefaultCursor());
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.TANGENT_PLN_TO_SPH.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.TANGENT_PLN_TO_SPH.getLargeIcon();
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.SPHERE, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Касательная плоскость</strong><br>к сфере в выбранной точке";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_TANGENT_PLANE_IN_POINT_ON_SPHERE;
  }

  @Override
  protected void create() {
    TangentPlaneInPointOnSphereBuilder builder = new TangentPlaneInPointOnSphereBuilder(_name);
    builder.setValue(BodyBuilder.BLDKEY_SPHERE, _sphereID);
    builder.setValue(BodyBuilder.BLDKEY_POINT, _pointID);
    _ctrl.add(builder, checker, false);
    reset();
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
      try {
        i_Body sph = _ctrl.getBody(id);
        if (sph.type() != BodyType.SPHERE) {
          return;
        }
        _sphereID = sph.id();
        _numOfChosenAnchors++;

        canvas().getHighlightAdapter().setTypes(BodyType.POINT);
        _ctrl.status().showMessage(_msg.current());
        _ctrl.redraw();
      } catch (ExNoBody ex) {
      }
    } else {
      i_Anchor a = getPointAnchor(id);
      if (a == null) {
        return;
      }
      _pointID = a.id();
      createWithNameValidation();
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите сферу{MOUSELEFT}", "Выберите точку на сфере{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.PLANE.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
