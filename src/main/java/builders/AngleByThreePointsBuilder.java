package builders;

import bodies.AngleBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Otec666
 */
public class AngleByThreePointsBuilder extends BodyBuilder {
  static public final String ALIAS = "Angle";
  static public final String BLDKEY_IS_LESS_THAN_PI = "isLessThanPI";

  public AngleByThreePointsBuilder() {
  }

  public AngleByThreePointsBuilder(String name) {
    super(name);
  }

  public AngleByThreePointsBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public AngleByThreePointsBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addPointOnFirstSide(param.get(BLDKEY_A).asString());
    addVertex(param.get(BLDKEY_B).asString());
    addPointOnSecondSide(param.get(BLDKEY_C).asString());
    addLessThanPiParam(param.get(BLDKEY_IS_LESS_THAN_PI).asBoolean());
  }

  public AngleByThreePointsBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, BuilderParam.POINT_ON_1_SIDE_ALIAS, BuilderParamType.ANCHOR, 102);
    addParam(BLDKEY_B, BuilderParam.VERT_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_C, BuilderParam.POINT_ON_2_SIDE_ALIAS, BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_IS_LESS_THAN_PI, "Меньше PI", BuilderParamType.BOOLEAN);
  }

  public final void addPointOnFirstSide(String id) {
    setValue(BLDKEY_A, id);
  }

  public final void addVertex(String id) {
    setValue(BLDKEY_B, id);
  }

  public final void addPointOnSecondSide(String id) {
    setValue(BLDKEY_C, id);
  }

  public final void addLessThanPiParam(boolean isLessThanPi) {
    setValue(BLDKEY_IS_LESS_THAN_PI, isLessThanPi);
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
      boolean isLessThanPI = getValueAsBoolean(BLDKEY_IS_LESS_THAN_PI);

      Vect3d a = anchors.getVect(idA);
      Vect3d b = anchors.getVect(idB);
      Vect3d c = anchors.getVect(idC);
      Angle3d ang = new Angle3d(a, b, c, isLessThanPI);

      AngleBody result = new AngleBody(_id, title(), ang);

      result.addAnchor(AngleBody.BODY_KEY_A, idA);
      result.addAnchor(AngleBody.BODY_KEY_B, idB);
      result.addAnchor(AngleBody.BODY_KEY_C, idC);

      result.addRibs(edt);

      _exists = true;
      return result;
    } catch (ExNoAnchor ex) {
      if (_exists) {
        eh.showMessage("Угол не построен: отсутствует вершина", error.Error.WARNING);
        _exists = false;
      }
      return new AngleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_C, getValueAsString(BLDKEY_C));
    result.add(BLDKEY_IS_LESS_THAN_PI, getValueAsBoolean(BLDKEY_IS_LESS_THAN_PI));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Угол </strong>%s%s%s",
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_C)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Угол</strong>";
    }
  }
}