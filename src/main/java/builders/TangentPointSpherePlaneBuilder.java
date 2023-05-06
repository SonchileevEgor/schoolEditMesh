package builders;

import bodies.PlaneBody;
import bodies.PointBody;
import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author kaznin
 */
public class TangentPointSpherePlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "TangentPointSpherePlane";

  public TangentPointSpherePlaneBuilder(String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_SPHERE, param.get(BLDKEY_SPHERE).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }

  public TangentPointSpherePlaneBuilder() {
  }

  public TangentPointSpherePlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public TangentPointSpherePlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_SPHERE, "Сфера", BuilderParamType.BODY, 61);
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      SphereBody sph = (SphereBody)bodies.get(getValueAsString(BLDKEY_SPHERE));
      PlaneBody pln = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE));

      if(!sph.exists())
        throw new ExLostBody("Сфера");
      if(!pln.exists())
        throw new ExLostBody("Плоскость");

      PointBody result = new PointBody(_id, sph.sphere().tangentPointSpherePlane(pln.plane()));
      edt.addAnchor(result.point(), result, "P");

      return result;
    } catch(ExBadRef | ExGeom ex) {
      if (_exists)
        eh.showMessage("Точка не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_SPHERE, getValueAsString(BLDKEY_SPHERE));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong> пересечения сферы %s и плоскости %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_SPHERE)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
