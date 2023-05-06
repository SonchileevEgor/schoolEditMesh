package builders;

import bodies.EmptyBody;
import bodies.LineBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
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
public class SymmetryLineBuilder extends BodyBuilder {

  public SymmetryLineBuilder() {
  }

  public SymmetryLineBuilder(String id, String name) {
    super(id, name);
  }
  static public final String ALIAS = "SymmetryLine";

  public SymmetryLineBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SymmetryLineBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_BODY, param.get(BLDKEY_BODY).asString());
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
  }

  public SymmetryLineBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BODY, "Тело для симметрии", BuilderParamType.BODY, 61);
    addParam(BLDKEY_LINE, "Ось симметрии", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bd = edt.bd();
    try {

      LineBody line = (LineBody) bd.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists()) {
        throw new ExLostBody("Прямая");
      }
      i_Body body = bd.get(getValueAsString(BLDKEY_BODY));
      if (!body.exists()) {
        throw new ExLostBody(body.alias());
      }
      i_Body result;
      i_Geom geom_result = SpaceTransformation.objectSymUnderLine(body.getGeom(), line.line());
      result = body.getBody(_id, title(), geom_result);
      //Anchors for points from deconstr()
      ArrayList<Vect3d> points = geom_result.deconstr();
      result.addAnchorsToBody(result, edt);
      result.addRibs(edt);
      result.addPlanes(edt);

      _exists = true;
      return result;
    } catch (ExNoBody ex) {
      if (_exists) {
        eh.showMessage("Тело не построено: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new EmptyBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_BODY, getValueAsString(BLDKEY_BODY));
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Тело,</strong> симметричное телу %s<br> относительно прямой %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_BODY)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Результат симметрии тела относительно прямой</strong>";
    }
  }
}
