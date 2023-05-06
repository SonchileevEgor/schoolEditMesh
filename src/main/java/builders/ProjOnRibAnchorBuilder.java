package builders;

import bodies.PointBody;
import bodies.RibBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class ProjOnRibAnchorBuilder extends BodyBuilder {
  static public final String ALIAS = "ProjOnRibAnchor";

  public ProjOnRibAnchorBuilder() {
  }

  public ProjOnRibAnchorBuilder(String id, String name) {
    super(id, name);
  }

  public ProjOnRibAnchorBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ProjOnRibAnchorBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
  }

  public ProjOnRibAnchorBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB, BuilderParam.RIB_ALIAS, BuilderParamType.ANCHOR, 61);
    addParam(BLDKEY_POINT, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR, 60);
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
      Vect3d point = anchors.get(getValueAsString(BLDKEY_POINT)).getPoint();
      if (rib.getRib().line().contains(point)) {
        PointBody result = new PointBody(_id, point);
        edt.addAnchor(result.point(), result, "P");
        edt.anchors().get(result.getAnchorID("P")).setMovable(true);
        _exists = true;
        return result;
      }
      String vertex = getValueAsString(BLDKEY_POINT);
      Vect3d proj = rib.getRib().line().projectOfPoint(point);
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
    result.add("rib", getValueAsString("rib"));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Перпендикуляр</strong> из точки %s к ребру %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
}
