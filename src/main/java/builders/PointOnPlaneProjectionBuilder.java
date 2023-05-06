package builders;

import bodies.PointBody;
import bodies.RibBody;
import bodies.i_PlainBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
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
 * Проекция точки на плоское тело.
 * @author alexeev
 */
public class PointOnPlaneProjectionBuilder extends BodyBuilder {
  static public final String ALIAS = "PointOnPlaneProjection";

  public PointOnPlaneProjectionBuilder() {
  }

  public PointOnPlaneProjectionBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnPlaneProjectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnPlaneProjectionBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
  }

  public PointOnPlaneProjectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_POINT, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      i_PlainBody pl = (i_PlainBody)bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!pl.exists())
        throw new ExLostBody(pl.alias());
      String anchorID = getValueAsString(BLDKEY_POINT);
      i_Anchor anchor = anchors.get(anchorID);
      try {
        Vect3d proj = pl.plane().projectionOfPoint(anchor.getPoint());
        RibBody result = new RibBody(_id, new Rib3d(anchor.getPoint(), proj));
        result.addAnchor(RibBody.BODY_KEY_A, anchorID);
        edt.addAnchor(proj, result, RibBody.BODY_KEY_B);
        result.addRibs(edt);
        _exists = true;
        return result;
      } catch( ExDegeneration ex ){
        PointBody result = new PointBody(_id, anchor.getPoint());
        edt.addAnchor(result.point(), result, "P");
        _exists = true;
        return result;
      }
    } catch( ExBadRef ex ){
      if (_exists) {
        eh.showMessage("Перпендикуляр не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Перпендикуляр</strong> из точки %s к %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
}
