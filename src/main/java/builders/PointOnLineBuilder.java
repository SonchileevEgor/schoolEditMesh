package builders;

import bodies.LineBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.behavior.BehaviorFactory;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class PointOnLineBuilder extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "PointOnLine";
  private Line3d _line;

  public PointOnLineBuilder() {
  }

  public PointOnLineBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnLineBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnLineBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }

  public PointOnLineBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_ALPHA, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists())
        throw new ExLostBody("прямая");
      _line = line.line();
      Vect3d p = Vect3d.conv_hull(_line.pnt(), _line.pnt2(), (double)getValue(BLDKEY_ALPHA));
      PointBody result = new PointBody(_id, p);
      edt.addAnchor(result.point(), result, "P", title());
      edt.anchors().get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createLineBehavior(_line));
      _exists = true;
      return result;
    } catch(ExNoBody | ExNoAnchor ex){
      if (_exists)
        eh.showMessage("Точка на прямой не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong> на прямой %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    try {
      double result = Vect3d.getMultiplierForHull2(_line.pnt(), _line.pnt2(), newPosition);
      setValue(BLDKEY_ALPHA, result);
    } catch (ExDegeneration ex) { }
  }
}
