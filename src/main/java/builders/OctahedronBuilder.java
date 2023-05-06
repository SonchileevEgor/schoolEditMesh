package builders;

import bodies.OctahedronBody;
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

public class OctahedronBuilder extends BodyBuilder {
  static public final String ALIAS = "Octahedron";
  
  public OctahedronBuilder() {
  }

  public OctahedronBuilder(String name) {
    super(name);
  }

  public OctahedronBuilder(String id, String name) {
    super(id, name);
  }

  public OctahedronBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public OctahedronBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
  }

  public OctahedronBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая вершина октаэдра", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "Вторая вершина октаэдра", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      OctahedronBody result = new OctahedronBody(_id, title(),
              anchors.getVect(getValueAsString(BLDKEY_A)),
              anchors.getVect(getValueAsString(BLDKEY_B)),
              (double) getValue(BLDKEY_ANGLE));
      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("B", anchors.get(getValueAsString(BLDKEY_B)).id());

      edt.addAnchor(result.C(), result, "C");
      edt.addAnchor(result.D(), result, "D");
      edt.addAnchor(result.topd(), result, "Topd");
      edt.addAnchor(result.topu(), result, "Topu");

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Октаэдр не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new OctahedronBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject() {
    };
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_ANGLE, getValueAsDouble(BLDKEY_ANGLE));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Октаэдр</strong><br> со стороной основания %s%s",
              ctrl.getAnchor(getValueAsString(BLDKEY_A)).getTitle(),
              ctrl.getAnchor(getValueAsString(BLDKEY_B)).getTitle());
    } catch (ExNoAnchor ex) {
      return "<html><strong>Октаэдр</strong>";
    }
  }
}
