package builders;

import bodies.LineBody;
import bodies.PointBody;
import bodies.RibBody;
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
 * Constructs a projection of point onto a line
 * @author alexeev
 */
public class PointOnLineProjectionBuilder extends BodyBuilder {
  static public final String ALIAS = "PointOnLineProjection";

  public PointOnLineProjectionBuilder() {
  }

  public PointOnLineProjectionBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnLineProjectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnLineProjectionBuilder (String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
  }

  public PointOnLineProjectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_POINT, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists())
        throw new ExLostBody("прямая");

      String anchorID = getValueAsString(BLDKEY_POINT);
      i_Anchor anchor = anchors.get(anchorID);
      if (line.line().contains(anchor.getPoint())) {
        PointBody result = new PointBody(_id, anchor.getPoint());
        edt.addAnchor(result.point(), result, "P");
        edt.anchors().get(result.getAnchorID("P")).setMovable(true);
        _exists = true;
        return result;
      }
      Vect3d proj = line.line().projectOfPoint(anchor.getPoint());
      RibBody result = new RibBody(_id, new Rib3d(anchor.getPoint(), proj));
      result.addAnchor("A", anchorID);
      edt.addAnchor(proj, result, "B", title());
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch( ExBadRef | ExDegeneration ex ){
      if (_exists)
        eh.showMessage("Отрезок не построен: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><<strong>Перпендикуляр</strong> из точки %s к прямой %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
}
