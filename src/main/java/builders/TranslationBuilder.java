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
import geom.ExDegeneration;
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
public class TranslationBuilder extends BodyBuilder {
  static public final String ALIAS = "Translation";
  public static final String BLDKEY_P1 = "p1";
  public static final String BLDKEY_P2 = "p2";

  public TranslationBuilder() {
  }

  public TranslationBuilder(String id, String name) {
    super(id, name);
  }

  public TranslationBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public TranslationBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_BODY, param.get(BLDKEY_BODY).asString());
    setValue(BLDKEY_P1, param.get(BLDKEY_P1).asString());
    setValue(BLDKEY_P2, param.get(BLDKEY_P2).asString());
  }

  public TranslationBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BODY, "Тело", BuilderParamType.BODY);
    addParam(BLDKEY_P1, "Начало вектора переноса", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_P2, "Конец вектора переноса", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bd = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d p1 = anchors.getVect(getValueAsString(BLDKEY_P1));
      Vect3d p2 = anchors.getVect(getValueAsString(BLDKEY_P2));
      i_Body body = bd.get(getValueAsString(BLDKEY_BODY));
      if (!body.exists())
        throw new ExLostBody(body.alias());
      i_Body result;
      i_Geom geom_result = SpaceTransformation.translationOfObject(body.getGeom(), p1, p2);
      result = body.getBody(_id, title(), geom_result);

      result.addAnchorsToBody(result, edt);
      result.addRibs(edt);
      result.addPlanes(edt);

      _exists = true;
      return result;
    } catch( ExNoBody | ExNoAnchor | ExDegeneration ex ){
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
    result.add(BLDKEY_P1, getValueAsString(BLDKEY_P1));
    result.add(BLDKEY_P2, getValueAsString(BLDKEY_P2));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Тело,</strong> полученное в результате<br>параллельного переноса тела %s на вектор %s%s<br>",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_BODY)),
         ctrl.getAnchorTitle(getValueAsString(BLDKEY_P1)),
          ctrl.getAnchorTitle(getValueAsString(BLDKEY_P2)));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Результат параллельного переноса тела</strong>";
    }
  }
}

