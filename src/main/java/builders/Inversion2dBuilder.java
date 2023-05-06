package builders;

import bodies.EmptyBody;
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
import geom.Circle3d;
import geom.SpaceTransformation;
import geom.Vect3d;
import geom.i_Geom;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class Inversion2dBuilder extends BodyBuilder {
  static public final String ALIAS = "Inversion";

  public Inversion2dBuilder() {
  }

  public Inversion2dBuilder(String name) {
    super(name);
  }
  
  public Inversion2dBuilder(String id, String name) {
    super(id, name);
  }

  public Inversion2dBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public Inversion2dBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_BODY, param.get(BLDKEY_BODY).asString());
    setValue(BLDKEY_CIRCLE, param.get(BLDKEY_CIRCLE).asString());
  }

  public Inversion2dBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BODY, "Тело", BuilderParamType.BODY);
    addParam(BLDKEY_CIRCLE, BuilderParam.CIRCLE_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bd = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try {
      Circle3d circ = anchors.get(getValueAsString(BLDKEY_CIRCLE)).getDisk();
      i_Body body = bd.get(getValueAsString(BLDKEY_BODY));
      if (!body.exists())
        throw new ExLostBody(body.alias());
      i_Body result;
      i_Geom geom_result = SpaceTransformation.inversion2dOfObject(body.getGeom(), circ);
      result = body.getBody(_id, title(), geom_result);

      ArrayList <Vect3d> points = geom_result.deconstr();

      result.addAnchorsToBody(result, edt);
      result.addRibs(edt);
      result.addPlanes(edt);

      _exists = true;
      return result;
    } catch( ExNoBody | ExNoAnchor ex ){
      if (_exists)
        eh.showMessage("Тело не построено: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new EmptyBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_BODY, getValueAsString(BLDKEY_BODY));
    result.add(BLDKEY_CIRCLE, getValueAsString(BLDKEY_CIRCLE));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Тело,</strong>, полученное в результате инверсии тела %s<br> относительно окружности %s<br>",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_BODY)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_CIRCLE)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Результат инверсии тела</strong>";
    }
  }
}
