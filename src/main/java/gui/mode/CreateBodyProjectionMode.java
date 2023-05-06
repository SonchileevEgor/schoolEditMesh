package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.SphereOnPlaneProjectionBuilder;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 * Пока только для сферы.
 *
 * @author Elena
 */
public class CreateBodyProjectionMode extends CreateBodyMode implements i_FocusChangeListener {

  private String _planeID, _bodyID;
  private BodyType _type;
  private ArrayList<BodyType> _types;

  public CreateBodyProjectionMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.add(BodyType.SPHERE);

    canvas().getHighlightAdapter().setTypes(_types);
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
    return IconList.SPH_PROJ_ON_PLANE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.SPH_PROJ_ON_PLANE.getLargeIcon();
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.SPHERE, 1) && _ctrl.containsBodies(BodyType.PLANE, 1);
  }

  @Override
  public String description() {
    return "<html><strong>Проекция сферы на плоскость</strong>";
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_BODY_PROJECTION;
  }

  @Override
  protected void create() {
    i_BodyBuilder builder = null;
    switch (_type) {
      case SPHERE:
        builder = new SphereOnPlaneProjectionBuilder(_name);
        builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
        builder.setValue(BodyBuilder.BLDKEY_SPHERE, _bodyID);
        break;
    }

    if (builder != null) {
      _ctrl.add(builder, checker, false);
      reset();
    }
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
        _type = bd.type();
        if (checker.checkBodyTypeIsSphere(bd.type())) {
          _bodyID = id;
          _numOfChosenAnchors++;
          canvas().getHighlightAdapter().setTypes(BodyType.PLANE);
          _ctrl.status().showMessage(_msg.current());
          _ctrl.redraw();
        }
      } catch (ExNoBody ex) {
      }
    } else if (_numOfChosenAnchors == 1) {
      try {
        i_Body pl = _ctrl.getBody(id);
        if (pl.type() != BodyType.PLANE) {
          return;
        }
        _planeID = pl.id();
        create();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите сферу{MOUSELEFT}", "Выберите плоскость{MOUSELEFT}");
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
