package gui.mode;

import bodies.BodyType;
import builders.SphereXSphereBuilder;
import editor.ExNoBody;
import editor.i_Body;
import java.util.Collection;
import editor.i_FocusChangeListener;
import java.util.Collection;
import gui.EdtController;
import gui.IconList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author Elena
 */
public class CreateSphereXSphereMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _sph1ID, _sph2ID;

  public CreateSphereXSphereMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    canvas().getHighlightAdapter().setTypes(BodyType.SPHERE);
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
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() != BodyType.SPHERE) {
          return;
        }
        _sph1ID = id;
        _numOfChosenAnchors++;
        _ctrl.status().showMessage(_msg.current());
        _ctrl.redraw();
      } catch (ExNoBody ex) {
      }
    } else {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (bd.type() != BodyType.SPHERE) {
          return;
        }
        _sph2ID = id;
        createWithNameValidation();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.BODY_INTERSECT.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.BODY_INTERSECT.getLargeIcon();
  }

  @Override
  public boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.SPHERE, 2));
  }

  @Override
  public String description() {
    return "<html><strong>Пересечение</strong><br>двух сфер";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_SPHERE_X_SPHERE;
  }

  @Override
  protected void create() {
    SphereXSphereBuilder builder = new SphereXSphereBuilder(_name);
    builder.setValue(SphereXSphereBuilder.BLDKEY_SPHERE1, _sph1ID);
    builder.setValue(SphereXSphereBuilder.BLDKEY_SPHERE2, _sph2ID);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите первую сферу{MOUSELEFT}",
            "Выберите вторую сферу{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.CIRCLE.getName(_ctrl.getEditor());
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
