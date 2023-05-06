package builders;

import bodies.EmptyBody;
import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.SpaceTransformation;
import geom.Vect3d;
import geom.i_Geom;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Plane symmetry.
 *
 * @author Elena
 */
public class SymmetryPlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "SymmetryPlane";

  public SymmetryPlaneBuilder() {
  }

  public SymmetryPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public SymmetryPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SymmetryPlaneBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_BODY, param.get(BLDKEY_BODY).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }

  public SymmetryPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BODY, "Тело для симметрии", BuilderParamType.BODY, 61);
    addParam(BLDKEY_PLANE, "Плоскость симметрии", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bd = edt.bd();
    try {
      PlaneBody plane = (PlaneBody) bd.get(getValueAsString(BLDKEY_PLANE));
      i_Body body = bd.get(getValueAsString(BLDKEY_BODY));
      if (!plane.exists()) {
        throw new ExLostBody("Плоскость");
      }
      if (!body.exists()) {
        throw new ExLostBody(body.alias());
      }
      i_Body result;
      i_Geom geom_result = SpaceTransformation.objectSymUnderPlane(body.getGeom(), plane.plane());
      result = body.getBody(_id, title(), geom_result);
      //Anchors for points from deconstr()
      ArrayList<Vect3d> points = geom_result.deconstr();

      result.addAnchorsToBody(result, edt);
      result.addRibs(edt);
      result.addPlanes(edt);

      _exists = true;
      return result;
    } catch (ExNoBody ex) {
      if (_exists) {
        eh.showMessage("Тело не построено: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new EmptyBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_BODY, getValueAsString(BLDKEY_BODY));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Тело,</strong> симметричное телу %s<br> относительно плоскости %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_BODY)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Результат симметрии тела относительно плоскости</strong>";
    }
  }
}
