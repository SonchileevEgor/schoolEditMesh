package builders;

// Tetrahedron builder.
import bodies.TetrahedronBody;
import static builders.BodyBuilder.BLDKEY_ANGLE;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.ExGeom;
import geom.Simplex3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

public class RegTetrahedronBuilder extends BodyBuilder {
  static public final String ALIAS = "RegTetrahedron";

  public RegTetrahedronBuilder() {
  }

  public RegTetrahedronBuilder(String name) {
    super(name);
  }

  public RegTetrahedronBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RegTetrahedronBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addAngle(param.get(BLDKEY_ANGLE).asDouble());
  }

  public RegTetrahedronBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addA(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addB(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addAngle(double angle) {
    setValue(BLDKEY_ANGLE, angle);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "Первая вершина тетраэдра", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "Вторая вершина тетраэдра", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();

    String aID = getValueAsString(BLDKEY_A);
    String bID = getValueAsString(BLDKEY_B);
    double angle = (double)getValue(BLDKEY_ANGLE);
    try {
      Simplex3d rt = Simplex3d.regTetrahedronBy2PntsAngle(
              anchors.getVect(aID),
              anchors.getVect(bID),
              angle);
      TetrahedronBody result = new TetrahedronBody(_id, title(), rt);
      result.addAnchor(TetrahedronBody.BODY_KEY_A, aID);
      result.addAnchor(TetrahedronBody.BODY_KEY_B, bID);

      edt.addAnchor(result.c(), result, TetrahedronBody.BODY_KEY_C);
      edt.addAnchor(result.d(), result, TetrahedronBody.BODY_KEY_D);

      result.addRibs(edt);
      result.addPlanes(edt);
      result.setRegular(true);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Тетраэдр не построен: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new TetrahedronBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject() {
    };
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_ANGLE, (double) getValue(BLDKEY_ANGLE));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Правильный тетраэдр,</strong><br>"
              + "построенный по вершинам %s, %s<br> и углу поворота %s\u00B0",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ANGLE)), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Тетраэдр</strong>";
    }
  }
};
