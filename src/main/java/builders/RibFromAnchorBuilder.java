package builders;

import bodies.RibBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev-laptop
 */
public class RibFromAnchorBuilder extends BodyBuilder {

  static public final String ALIAS = "RibFromAnchor";

  public RibFromAnchorBuilder() {
  }

  public RibFromAnchorBuilder(String id, String name) {
    super(id, name);
  }

  public RibFromAnchorBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RibFromAnchorBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
  }

  public RibFromAnchorBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB, BuilderParam.RIB_ALIAS, BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor a = anchors.get(getValueAsString(BLDKEY_RIB));
      RibBody result = new RibBody(_id, a.getRib());
      // Добавляем точки
      ArrayList<String> pointIDs = a.arrayIDs();
      result.addAnchor("A", pointIDs.get(0));
      result.addAnchor("B", pointIDs.get(1));
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExNoAnchor ex) {
      if (_exists) {
        eh.showMessage("Отрезок не построен: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Отрезок</strong>, построенный по ребру %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
}
