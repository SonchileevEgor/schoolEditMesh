package builders;

import bodies.StellaOctahedronBody;
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

public class StellaOctahedronBuilder extends BodyBuilder {
  static public final String ALIAS = "StellaOctahedron";  
  
  public StellaOctahedronBuilder() {
  }

  public StellaOctahedronBuilder(String name) {
    super(name);
  }

  public StellaOctahedronBuilder(String id, String name) {
    super(id, name);
  }

  public StellaOctahedronBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public StellaOctahedronBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
  }

  public StellaOctahedronBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, BuilderParam.VERT_2_ALIAS, BuilderParamType.ANCHOR, 100);
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

      StellaOctahedronBody result = new StellaOctahedronBody(_id, title(),
              anchors.getVect(getValueAsString(BLDKEY_A)),
              anchors.getVect(getValueAsString(BLDKEY_B)),
              (double) getValue(BLDKEY_ANGLE));

      result.addAnchor("A", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("B", anchors.get(getValueAsString(BLDKEY_B)).id());

      edt.addAnchor(result.C(), result, "C");
      edt.addAnchor(result.D(), result, "D");
      edt.addAnchor(result.E(), result, "E");
      edt.addAnchor(result.F(), result, "F");
      edt.addAnchor(result.G(), result, "G");
      edt.addAnchor(result.H(), result, "H");
      edt.addAnchor(result.I(), result, "I");
      edt.addAnchor(result.J(), result, "J");
      edt.addAnchor(result.K(), result, "K");
      edt.addAnchor(result.L(), result, "L");
      edt.addAnchor(result.M(), result, "M");
      edt.addAnchor(result.N(), result, "N");

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Звёздчатый октаэдр не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new StellaOctahedronBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_ANGLE, (double) getValue(BLDKEY_ANGLE));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Звёздчатый октаэдр</strong><br> со стороной основания %s%s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Звёздчатый октаэдр</strong>";
    }
  }
}
