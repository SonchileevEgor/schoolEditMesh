package builders;

import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Sphere3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs a sphere by center and point on sphere surface.
 *
 * @author alexeev-laptop
 */
public class SphereByTwoPointsBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereByTwoPoints";

  public SphereByTwoPointsBuilder() {
  }
  
  public SphereByTwoPointsBuilder(String name) {
    super(name);
  }

  public SphereByTwoPointsBuilder(String id, String name) {
    super(id, name);
  }

  public SphereByTwoPointsBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereByTwoPointsBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addCenter(param.get(BLDKEY_CENTER).asString());
    addPointOnSphere(param.get(BLDKEY_P).asString());
  }

  public SphereByTwoPointsBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addCenter(String id) {
    setValue(BLDKEY_CENTER, id);
  }

  public final void addPointOnSphere(String id) {
    setValue(BLDKEY_P, id);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CENTER, "Центр", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_P, "Точка на сфере", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d center = anchors.getVect(getValueAsString(BLDKEY_CENTER));
      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_P));
      Sphere3d sph = new Sphere3d(center, Vect3d.dist(center, point));
      SphereBody result = new SphereBody(_id, title(), sph);
      result.addAnchor(SphereBody.BODY_KEY_CENTER, getValueAsString(BLDKEY_CENTER));
      _exists = true;
      return result;
    } catch (ExNoAnchor ex) {
      if (_exists) {
        eh.showMessage("Сфера не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new SphereBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CENTER, getValueAsString(BLDKEY_CENTER));
    result.add(BLDKEY_P, getValueAsString(BLDKEY_P));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong><br> с центром %s и точкой %s на поверхности",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_CENTER)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
