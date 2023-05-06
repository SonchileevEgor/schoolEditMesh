package builders;

import bodies.RibBody;
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
import geom.ExDegeneration;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs a projection of point onto a line
 *
 * @author alexeev
 */
public class PointOnRibProjectionBuilder extends BodyBuilder {
  static public final String ALIAS = "PointOnRibProjection";

  public PointOnRibProjectionBuilder() {
  }

  public PointOnRibProjectionBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnRibProjectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnRibProjectionBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
  }

  public PointOnRibProjectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB, "Отрезок", BuilderParamType.BODY);
    addParam(BLDKEY_POINT, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      RibBody rib = (RibBody) bodies.get(getValueAsString(BLDKEY_RIB));
      if (!rib.exists()) {
        throw new ExLostBody("Отрезок");
      }

      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_POINT));
      String vertex = getValueAsString(BLDKEY_POINT);
      Vect3d proj = rib.rib().line().projectOfPoint(point);
      RibBody result = new RibBody(_id, new Rib3d(point, proj));
      result.addAnchor("A", anchors.get(vertex).id());
      edt.addAnchor(proj, result, "B", title());
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExBadRef | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Проекция точки не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Проекция</strong> точки %s на прямую %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
}
