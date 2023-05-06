package builders;

import bodies.ArcBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Arc3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author rita
 */
public class ArcBuilder extends BodyBuilder {
  static public final String ALIAS = "Arc";

  public ArcBuilder() {
  }

  public ArcBuilder(String id, String name) {
    super(id, name);
  }

  public ArcBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public ArcBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addC(param.get(BLDKEY_C).asString());
  }

  public ArcBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addC(String id) {
    setValue(BLDKEY_C, id);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первый край дуги", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, "Точка на дуге", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_C, "Второй край дуги", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      String idA = getValueAsString(BLDKEY_A);
      String idB = getValueAsString(BLDKEY_B);
      String idC = getValueAsString(BLDKEY_C);
      Vect3d a = anchors.getVect(idA);
      Vect3d b = anchors.getVect(idB);
      Vect3d c = anchors.getVect(idC);

      Arc3d arc = new Arc3d(a, b, c);
      ArcBody result = new ArcBody(_id, title(), arc);

      result.addAnchor(ArcBody.BODY_KEY_A, idA);
      result.addAnchor(ArcBody.BODY_KEY_B, idB);
      result.addAnchor(ArcBody.BODY_KEY_C, idC);

      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Дуга не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new ArcBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_C, getValueAsString(BLDKEY_C));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Дуга </strong>%s%s%s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Дуга</strong>";
    }
  }
}