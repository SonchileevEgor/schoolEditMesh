package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.TangentPointSphereFacetBuilder;
import builders.TangentPointSpherePlaneBuilder;
import editor.ExNoBody;
import editor.i_Body;
import java.util.Collection;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import java.util.Collection;
import gui.EdtController;
import gui.IconList;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author Elena
 */
public class CreateTangentPointSphereAndFacetMode extends CreateBodyMode implements i_FocusChangeListener {

  private BodyType _type; //plane, poly, disk, line, ray, rib
  private String _sphereID, _bodyID;
  private ArrayList<BodyType> _types;

  public CreateTangentPointSphereAndFacetMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);

    _types = new ArrayList<>();
    _types.addAll(BodyType.getPlainBodies());
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

        canvas().getHighlightAdapter().setTypes(_types);
        _ctrl.status().showMessage(_msg.current());
        _ctrl.redraw();
      } catch (ExNoBody ex) {
      }
    } else {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (!_types.contains(bd.type())) {
          return;
        }
        _bodyID = id;
        _type = bd.type();
        createWithNameValidation();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите сферу{MOUSELEFT}",
            "Выберите тело{MOUSELEFT}");
  }

  @Override
  protected void setName() {
    _name = BodyType.POINT.getName(_ctrl.getEditor());
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_TANGENT_POINT_OF_SPHERE_AND_PLANE_OR_FACET;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.TANGENT_PNT_TO_SPH.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.TANGENT_PNT_TO_SPH.getLargeIcon();
  }

  @Override
  protected void create() {
    i_BodyBuilder builder;
    if (_type != BodyType.PLANE) {
      builder = new TangentPointSpherePlaneBuilder();
      builder.setValue(TangentPointSphereFacetBuilder.BLDKEY_FACET, _bodyID);
    } else {
      builder = new TangentPointSpherePlaneBuilder();
      builder.setValue(TangentPointSpherePlaneBuilder.BLDKEY_PLANE, _bodyID);
    }
    builder.setValue(TangentPointSphereFacetBuilder.BLDKEY_SPHERE, _sphereID);
    builder.setValue(BodyBuilder.BLDKEY_NAME, _name);
    _ctrl.add(builder, checker, false);
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Точка касания</strong><br>сферы и плоскости";
  }

  @Override
  protected boolean isEnabled() {
    return (_ctrl.containsBodies(BodyType.SPHERE, 1)
            && (_ctrl.containsBodies(BodyType.PLANE, 1)
            || _ctrl.containsBodies(BodyType.getPolygonTypes(), 1)));
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return true;
  }
}
