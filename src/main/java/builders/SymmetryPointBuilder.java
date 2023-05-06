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
import geom.SpaceTransformation;
import geom.Vect3d;
import geom.i_Geom;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class SymmetryPointBuilder extends BodyBuilder {
  static public final String ALIAS = "SymmetryPoint";

  public SymmetryPointBuilder() {
  }

  public SymmetryPointBuilder(String id, String name) {
    super(id, name);
  }

  public SymmetryPointBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SymmetryPointBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_BODY, param.get(BLDKEY_BODY).asString());
    setValue(BLDKEY_CENTER, param.get(BLDKEY_CENTER).asString());
  }

  public SymmetryPointBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BODY, "Тело для симметрии", BuilderParamType.BODY);
    addParam(BLDKEY_CENTER, "Центр симметрии", BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bd = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try {

      Vect3d center = anchors.get(getValueAsString(BLDKEY_CENTER)).getPoint();
      i_Body body = bd.get(getValueAsString(BLDKEY_BODY));
      if (!body.exists())
        throw new ExLostBody(body.alias());
      i_Body result;
      i_Geom geom_result = SpaceTransformation.objectSymUnderPoint (body.getGeom(), center);
      result = body.getBody(_id, title(), geom_result);

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
    result.add(BLDKEY_CENTER, getValueAsString(BLDKEY_CENTER));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Тело,</strong> симметричное телу %s<br> относительно точки %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_BODY)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_CENTER)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Результат симметрии тела относительно точки</strong>";
    }
  }
}