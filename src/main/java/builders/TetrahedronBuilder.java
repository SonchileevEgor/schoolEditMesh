package builders;

import bodies.TetrahedronBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Simplex3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Tetrahedron builder.
 *
 * @author alexeev
 */
public class TetrahedronBuilder extends BodyBuilder {
  static public final String ALIAS = "Tetrahedron";

  public TetrahedronBuilder() {
  }

  public TetrahedronBuilder(String name) {
    super(name);
  }

  public TetrahedronBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public TetrahedronBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
    addC(param.get(BLDKEY_C).asString());
    addD(param.get(BLDKEY_D).asString());
  }

  public TetrahedronBuilder(HashMap<String, BuilderParam> params) {
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

  public final void addD(String id) {
    setValue(BLDKEY_D, id);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.VERT_1_ALIAS, BuilderParamType.ANCHOR, 103);
    addParam(BLDKEY_B, BuilderParam.VERT_2_ALIAS, BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_C, BuilderParam.VERT_3_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_D, BuilderParam.VERT_4_ALIAS, BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      String aID = getValueAsString(BLDKEY_A);
      String bID = getValueAsString(BLDKEY_B);
      String cID = getValueAsString(BLDKEY_C);
      String dID = getValueAsString(BLDKEY_D);

      Vect3d a = anchors.getVect(aID);
      Vect3d b = anchors.getVect(bID);
      Vect3d c = anchors.getVect(cID);
      Vect3d d = anchors.getVect(dID);

      TetrahedronBody result = new TetrahedronBody(_id, title(), new Simplex3d(a, b, c, d));

      result.addAnchor(TetrahedronBody.BODY_KEY_A, aID);
      result.addAnchor(TetrahedronBody.BODY_KEY_B, bID);
      result.addAnchor(TetrahedronBody.BODY_KEY_C, cID);
      result.addAnchor(TetrahedronBody.BODY_KEY_D, dID);

      result.addRibs(edt);
      result.addPlanes(edt);
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
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_C, getValueAsString(BLDKEY_C));
    result.add(BLDKEY_D, getValueAsString(BLDKEY_D));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Тетраэдр</strong> %s%s%s%s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_D)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Тетраэдр</strong>";
    }
  }
};
