package builders;

// Minkowski sum builder.
import bodies.MinkowskiBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

public class MinkowskiSumBuilder extends BodyBuilder {
  static public final String ALIAS = "Minkowski sum";

  public MinkowskiSumBuilder() {
  }

  public MinkowskiSumBuilder(String name) {
    super(name);
  }

  public MinkowskiSumBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public MinkowskiSumBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
  }

  public MinkowskiSumBuilder(HashMap<String, BuilderParam> params) {
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
    addParam(BLDKEY_A, "Первый многогранник", BuilderParamType.BODY, 102);
    addParam(BLDKEY_B, "Второй многогранник", BuilderParamType.BODY, 101);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer anchors = edt.bd();
    try {
      MinkowskiBody result = new MinkowskiBody(_id, title(),
              anchors.get(getValueAsString(BLDKEY_A)),
              anchors.get(getValueAsString(BLDKEY_B))
      );
      
      // Добавляем точки
      for (int i=0; i < result.points().size(); i++) {
        edt.addAnchor(result.points().get(i), result, String.valueOf(i));  
      }

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Сумма Минковского не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new MinkowskiBody(_id, title());
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
    return String.format("<html><strong>Сумма Минковского</strong> двух многогранников");
  }
};