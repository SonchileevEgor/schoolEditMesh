package builders;

import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Checker;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class RibXPolyBuilder extends BodyBuilder {
  static public final String ALIAS = "RibXPoly";

  public RibXPolyBuilder() {
  }

  public RibXPolyBuilder(String id, String name) {
    super(id, name);
  }

  public RibXPolyBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RibXPolyBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
    setValue(BLDKEY_POLYGON, param.get(BLDKEY_POLYGON).asString());
  }

  public RibXPolyBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB, "Отрезок", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_POLYGON, BuilderParam.POLY_ALIAS, BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor rib = anchors.get(getValueAsString(BLDKEY_RIB));
      i_Anchor poly = anchors.get(getValueAsString(BLDKEY_POLYGON));

      Vect3d intersect = rib.getRib().intersectWithPlane(poly.getPoly().plane());
      if (!Checker.isPointOnClosePolygon(poly.getPoly(), intersect)) {
        throw new ExGeom("отрезок и грань не пересекаются");
      }

      PointBody result = new PointBody(_id, intersect);
      edt.addAnchor(result.point(), result, "P");
      _exists = true;
      return result;
    } catch (ExBadRef | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Пересечение отрезка и плоскости не построено: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
    result.add(BLDKEY_POLYGON, getValueAsString(BLDKEY_POLYGON));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка пересечения</strong> отрезка %s и грани %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
