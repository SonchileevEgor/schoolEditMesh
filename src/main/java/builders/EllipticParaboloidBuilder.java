package builders;

import bodies.EllipticParaboloidBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.EllipticParaboloid3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Vitaliy
 */
public class EllipticParaboloidBuilder extends BodyBuilder {
  static public final String ALIAS = "EllipticParaboloid";
  public static final String BLDKEY_POINT_ON_LINE_1 = "pOnLine1";
  public static final String BLDKEY_POINT_ON_LINE_2 = "pOnLine2";

  public EllipticParaboloidBuilder() {
  }

  public EllipticParaboloidBuilder(String id, String name) {
    super(id, name);
  }

  public EllipticParaboloidBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public EllipticParaboloidBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public EllipticParaboloidBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_POINT_ON_LINE_1, param.get(BLDKEY_POINT_ON_LINE_1).asString());
    setValue(BLDKEY_POINT_ON_LINE_2, param.get(BLDKEY_POINT_ON_LINE_2).asString());
    setValue(BLDKEY_FOCUS, param.get(BLDKEY_FOCUS).asString());
  }

  public void setPointOnLine1(String anchorID){
    setValue(BLDKEY_POINT_ON_LINE_1, anchorID);
  }

  public void setPointOnLine2(String anchorID){
    setValue(BLDKEY_POINT_ON_LINE_2, anchorID);
  }

  public void setFocus(String anchorID){
    setValue(BLDKEY_FOCUS, anchorID);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_POINT_ON_LINE_1, "Точка на директрисе", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_POINT_ON_LINE_2, "Вторая точка на директрисе", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_FOCUS, "Фокус", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d pOnLine1 = anchors.getVect(getValueAsString(BLDKEY_POINT_ON_LINE_1));
      Vect3d pOnLine2 = anchors.getVect(getValueAsString(BLDKEY_POINT_ON_LINE_2));
      Vect3d f = anchors.getVect(getValueAsString(BLDKEY_FOCUS));
      EllipticParaboloid3d paraboloid = new EllipticParaboloid3d(pOnLine1, pOnLine2, f);
      EllipticParaboloidBody result = new EllipticParaboloidBody(_id, title(), paraboloid);
      result.addAnchor(BLDKEY_POINT_ON_LINE_1, anchors.get(getValueAsString(BLDKEY_POINT_ON_LINE_1)).id());
      result.addAnchor(BLDKEY_POINT_ON_LINE_2, anchors.get(getValueAsString(BLDKEY_POINT_ON_LINE_2)).id());
      result.addAnchor(BLDKEY_FOCUS, anchors.get(getValueAsString(BLDKEY_FOCUS)).id());
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Параболоид не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new EllipticParaboloidBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_POINT_ON_LINE_1, getValueAsString(BLDKEY_POINT_ON_LINE_1));
    result.add(BLDKEY_POINT_ON_LINE_2, getValueAsString(BLDKEY_POINT_ON_LINE_2));
    result.add(BLDKEY_FOCUS, getValueAsString(BLDKEY_FOCUS));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Эллиптический параболоид</strong> с директрисой %s%s <br> и фокусом %s ",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT_ON_LINE_1)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_POINT_ON_LINE_2)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_FOCUS)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Эллиптический параболоид</strong>";
    }
  }

}
