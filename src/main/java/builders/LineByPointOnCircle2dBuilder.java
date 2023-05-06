package builders;

import bodies.CircleBody;
import bodies.LineBody;
import bodies.RibBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Checker;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Tangent line from point to circle.
 *
 * @author alexeev
 */
public class LineByPointOnCircle2dBuilder extends BodyBuilder {
  static public final String ALIAS = "LineByPointOnCircle";

  public LineByPointOnCircle2dBuilder() {
  }
  
  public LineByPointOnCircle2dBuilder(String name) {
    super(name);
  }

  public LineByPointOnCircle2dBuilder(String id, String name) {
    super(id, name);
  }

  public LineByPointOnCircle2dBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineByPointOnCircle2dBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_CIRCLE, param.get(BLDKEY_CIRCLE).asString());
    setValue(BLDKEY_POINT, param.get(BLDKEY_POINT).asString());
  }

  public LineByPointOnCircle2dBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CIRCLE, BuilderParam.CIRCLE_ALIAS, BuilderParamType.BODY);
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
      CircleBody circ = (CircleBody) bodies.get(getValueAsString(BLDKEY_CIRCLE));
      if (!circ.exists()) {
        throw new ExLostBody("окружность");
      }

      Vect3d point = anchors.getVect(getValueAsString(BLDKEY_POINT));

      if (Checker.isPointOnCircle(point, circ.circle())) {
        LineBody result = new LineBody(_id, title(), Circle3d.tangentLineFromPointOnCircle2d(circ.circle(), point));
        result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_POINT)).id());
        _exists = true;
        return result;
      } else {
        throw new ExDegeneration("Точка не лежит на окружности!");
      }

    } catch (ExNoBody | ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Прямая не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POINT, getValueAsString(BLDKEY_POINT));
    result.add(BLDKEY_CIRCLE, getValueAsString(BLDKEY_CIRCLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Прямая,</strong>касающаяся окружности %s в точке %s, принадлежащей окружности",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_CIRCLE)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Прямая</strong>";
    }
  }
}
