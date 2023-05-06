package builders;

import bodies.PlaneBody;
import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Gayduk
 */
public class TangentPlaneInPointOnSphereBuilder extends BodyBuilder {
  static public final String ALIAS = "TangentPlaneInPointOnSphere";

  public TangentPlaneInPointOnSphereBuilder() {
  }
  
  public TangentPlaneInPointOnSphereBuilder(String name) {
    super(name);
  }

  public TangentPlaneInPointOnSphereBuilder(String id, String name) {
    super(id, name);
  }

  public TangentPlaneInPointOnSphereBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public TangentPlaneInPointOnSphereBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_SPHERE, param.get(BLDKEY_SPHERE).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
  }

  public TangentPlaneInPointOnSphereBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_SPHERE, "Сфера", BuilderParamType.BODY);
    addParam(BLDKEY_POINT, "Точка на сфере", BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      SphereBody sp = (SphereBody)bodies.get(getValueAsString(BLDKEY_SPHERE));
      if (!sp.exists())
        throw new ExLostBody("сфера");
      Vect3d p = anchors.getVect(getValueAsString(BLDKEY_POINT));
      PlaneBody result = new PlaneBody(_id, title(), sp.sphere().tangentPlaneInPoint(p));
      _exists = true;
      return result;
    } catch( ExBadRef | ExGeom ex ){
      if (_exists){
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    result.add(BLDKEY_SPHERE, getValueAsString(BLDKEY_SPHERE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Касательная плоскость </strong>сферы %s в точке %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_SPHERE)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
