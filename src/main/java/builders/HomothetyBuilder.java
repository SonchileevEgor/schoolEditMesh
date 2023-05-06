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
public class HomothetyBuilder extends BodyBuilder {
  static public final String ALIAS = "Homothety";

  public HomothetyBuilder() {
  }

  public HomothetyBuilder(String id, String name) {
    super(id, name);
  }

  public HomothetyBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public HomothetyBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_BODY, param.get(BLDKEY_BODY).asString());
    setValue(BLDKEY_CENTER, param.get(BLDKEY_CENTER).asString());
    setValue(BLDKEY_COEFFICIENT, param.get(BLDKEY_COEFFICIENT).asDouble());
  }

  public HomothetyBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BODY, "Тело", BuilderParamType.BODY);
    addParam(BLDKEY_CENTER, "Центр гомотетии", BuilderParamType.ANCHOR);
    addParam(BLDKEY_COEFFICIENT, "Коэффициент гомотетии", BuilderParamType.DOUBLE);
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
      double k = (double)getValue(BLDKEY_COEFFICIENT);
      i_Body result;
      i_Geom geom_result = SpaceTransformation.homothetyOfObject(body.getGeom(), center, k);
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
    result.add(BLDKEY_COEFFICIENT, (double)getValue(BLDKEY_COEFFICIENT));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Образ гомотетии</strong> тела %s<br> с центром %s и коэффициентом %.3f",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_BODY)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_CENTER)),
        getValue(BLDKEY_COEFFICIENT));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Образ гомотетии тела</strong>";
    }
  }
}