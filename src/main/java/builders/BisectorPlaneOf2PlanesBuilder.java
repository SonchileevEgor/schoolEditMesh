package builders;

import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.Plane3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author gayduk
 */
public class BisectorPlaneOf2PlanesBuilder extends BodyBuilder {
  static public final String ALIAS = "BisectorPlaneOf2Planes";
  public static final String BLDKEY_PLANE1 = "plane1";
  public static final String BLDKEY_PLANE2 = "plane2";

  public BisectorPlaneOf2PlanesBuilder() {
  }

  public BisectorPlaneOf2PlanesBuilder(String id, String name) {
    super(id, name);
  }

  public BisectorPlaneOf2PlanesBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public BisectorPlaneOf2PlanesBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_PLANE1, param.get(BLDKEY_PLANE1).asString());
    setValue(BLDKEY_PLANE2, param.get(BLDKEY_PLANE2).asString());
    setValue(BLDKEY_DIRECTION, param.get(BLDKEY_DIRECTION).asBoolean());
  }

  public BisectorPlaneOf2PlanesBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PLANE1, "1-я плоскость", BuilderParamType.BODY, 91);
    addParam(BLDKEY_PLANE2, "2-я плоскость", BuilderParamType.BODY, 90);
    addParam(BLDKEY_DIRECTION, "Направление", BuilderParamType.BOOLEAN);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();

    try {
      PlaneBody pl1 = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE1));
      PlaneBody pl2 = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE2));
      if ((!pl1.exists())||(!pl2.exists()))
        throw new ExLostBody("плоскость");

      ArrayList<Plane3d> bisPlanes = Plane3d.bisectorPlaneOf2Planes(pl1.plane(), pl2.plane());
      Plane3d bisPlane;
      if (getValueAsBoolean(BLDKEY_DIRECTION) == true) {
        bisPlane = bisPlanes.get(1);
      } else if (!bisPlanes.isEmpty()) {
        bisPlane = bisPlanes.get(0);
      } else {
        throw new ExDegeneration("биссекторные плоскости не построить");
      }

      PlaneBody result = new PlaneBody(_id, title(), bisPlane);
      _exists = true;
      return result;
    } catch (ExNoBody | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Плоскость не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_PLANE1, getValueAsString(BLDKEY_PLANE2));
    result.add(BLDKEY_PLANE2, getValueAsString(BLDKEY_PLANE2));
    result.add(BLDKEY_DIRECTION, getValueAsBoolean(BLDKEY_DIRECTION));
    return result;
   }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Биссекторная плоскость</strong><br>плоскости %s и плоскости %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE1)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE2)));
    } catch (ExNoBody ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
