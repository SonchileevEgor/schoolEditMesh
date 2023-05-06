
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
import geom.Polygon3d;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class ProjOnPolyAnchorBuilder extends BodyBuilder {
  static public final String ALIAS = "ProjOnPolyAnchor";

  public ProjOnPolyAnchorBuilder() {
  }

  public ProjOnPolyAnchorBuilder(String id, String name) {
    super(id, name);
  }

  public ProjOnPolyAnchorBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ProjOnPolyAnchorBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_POLYGON, param.get(BLDKEY_POLYGON).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
  }

  public ProjOnPolyAnchorBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POLYGON, BuilderParam.POLY_ALIAS, BuilderParamType.ANCHOR, 61);
    addParam(BLDKEY_POINT, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR, 60);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      Polygon3d poly = anchors.get(getValueAsString(BLDKEY_POLYGON)).getPoly();
      i_Anchor point = anchors.get(getValueAsString(BLDKEY_POINT));
      if (poly.plane().containsPoint(point.getPoint())) {
        PointBody result = new PointBody(_id, point.getPoint());
        edt.addAnchor(result.point(), result, "P");
        edt.anchors().get(result.getAnchorID("P")).setMovable(true);
        _exists = true;
        return result;
      }
      Vect3d proj = poly.plane().projectionOfPoint(point.getPoint());
      RibBody result = new RibBody(_id, new Rib3d(point.getPoint(), proj));
      result.addAnchor("A", point.id());
      edt.addAnchor(proj, result, "B", title());
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch( ExBadRef | ExDegeneration ex ){
      if (_exists)
        eh.showMessage("Проекция точки не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      String ribTitle = title();
//      try {
//        // если точка, из которой опускается перпендикуляр, была построена,
//        // мы знаем имя отрезка;
//        // если нет - то заменяем название первой точки знаком вопроса.
//        ribTitle = edt.anchors().get(getValueAsString(BLDKEY_POINT)).getTitle() + title();
//      } catch (ExNoAnchor ex2) {
//        ribTitle = "?" + ribTitle;
//      }
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    result.add(BLDKEY_POLYGON, getValueAsString(BLDKEY_POLYGON));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Перпендикуляр</strong> из точки %s к многоугольнику %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Проекция точки на многоугольник</strong>";
    }
  }
}
