package builders;

// Cube builder.
import bodies.CubeBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

public class CubeBuilder extends BodyBuilder {
  static public final String ALIAS = "Cube";

  public CubeBuilder() {
  }

  public CubeBuilder(String name) {
    super(name);
  }

  public CubeBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public CubeBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addD(param.get(BLDKEY_D).asString());
  }

  public CubeBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addD(String id) {
    setValue(BLDKEY_D, id);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая вершина куба", BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, "Вторая вершина куба", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_D, "Третья вершина куба", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      CubeBody result = new CubeBody(_id, title(),
              anchors.getVect(getValueAsString(BLDKEY_A)),
              anchors.getVect(getValueAsString(BLDKEY_B)),
              anchors.getVect(getValueAsString(BLDKEY_D)));
      // Добавляем точки
      result.addAnchor("A1", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("B1", anchors.get(getValueAsString(BLDKEY_B)).id());
      result.addAnchor("D1", anchors.get(getValueAsString(BLDKEY_D)).id());
      edt.addAnchor(result.C1(), result, "C1");
      edt.addAnchor(result.A2(), result, "A2");
      edt.addAnchor(result.B2(), result, "B2");
      edt.addAnchor(result.C2(), result, "C2");
      edt.addAnchor(result.D2(), result, "D2");

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Куб не построен: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new CubeBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_D, getValueAsString(BLDKEY_D));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Куб</strong>, построенный по вершинам %s, %s и %s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_D)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Куб</strong>";
    }
  }
};
