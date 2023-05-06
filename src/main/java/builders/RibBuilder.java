package builders;

import bodies.RibBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Segment by two points builder.
 *
 * @author
 */
public class RibBuilder extends BodyBuilder {
  static public final String ALIAS = "Rib";

  public RibBuilder() {
    super();
  }

  public RibBuilder(String name) {
    super(name);
  }

  public RibBuilder(String id, String name) {
    super(id, name);
  }

  public RibBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RibBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
  }

  public RibBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая вершина ребра", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "Вторая вершина ребра", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d b = anchors.getVect(getValueAsString(BLDKEY_B));
      Rib3d r = new Rib3d(a, b);
      RibBody result = new RibBody(_id, r);
      result.addAnchor(RibBody.BODY_KEY_A, getValueAsString(BLDKEY_A));
      result.addAnchor(RibBody.BODY_KEY_B, getValueAsString(BLDKEY_B));
      result.addRibs(edt);
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Отрезок не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new RibBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Отрезок</strong> %s%s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Отрезок</strong>";
    }
  }
};
