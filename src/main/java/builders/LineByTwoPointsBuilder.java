package builders;

import bodies.LineBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author
 */
public class LineByTwoPointsBuilder extends BodyBuilder {
  static public final String ALIAS = "LineTwoPoints";

  public LineByTwoPointsBuilder() {
  }

  public LineByTwoPointsBuilder(String name) {
    super(name);
  }

  public LineByTwoPointsBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineByTwoPointsBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public LineByTwoPointsBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addA(param.get(BLDKEY_A).asString());
    addB(param.get(BLDKEY_B).asString());
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
    addParam(BLDKEY_A, "Первая точка прямой", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "Вторая точка прямой", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      String aID = getValueAsString(BLDKEY_A);
      String bID = getValueAsString(BLDKEY_B);
      Vect3d aPoint = anchors.getVect(aID);
      Vect3d bPoint = anchors.getVect(bID);
      LineBody result = new LineBody(_id, title(), aPoint, bPoint);
      result.addAnchor(LineBody.BODY_KEY_A, aID);
      result.addAnchor(LineBody.BODY_KEY_B, bID);
      result.setHideSegment(true);
      result.setSegmentA(aPoint);
      result.setSegmentB(bPoint);
      result.addRibs(edt);

      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Прямая не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new LineBody(_id, title());
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
      return String.format("<html><strong>Прямая</strong>, построенная по точкам %s и %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Прямая</strong>";
    }
  }
}
