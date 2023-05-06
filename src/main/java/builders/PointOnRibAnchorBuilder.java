package builders;

import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.behavior.BehaviorFactory;
import editor.i_Anchor;
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
 *
 * @author alexeev
 */
public class PointOnRibAnchorBuilder extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "PointOnRibAnchor";
  private Vect3d _a;
  private Vect3d _b;

  public PointOnRibAnchorBuilder() {
  }

  public PointOnRibAnchorBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnRibAnchorBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnRibAnchorBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }

  public PointOnRibAnchorBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB, BuilderParam.RIB_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_ALPHA, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor anch = anchors.get(getValueAsString(BLDKEY_RIB));
      Rib3d rib = anch.getRib();
      _a = rib.a();
      _b = rib.b();
      Vect3d p = Vect3d.conv_hull(_a, _b, (double)getValue(BLDKEY_ALPHA));
      PointBody result = new PointBody(_id, p);
      edt.addAnchor(result.point(), result, "P", title());
      anchors.get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createRibBehavior(rib));
      _exists = true;
      return result;
    } catch(ExNoAnchor ex){
      if (_exists)
				eh.showMessage("Точка на отрезке не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
			return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong> на ребре %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Точка</strong>";
    }
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    try {
      double result = Vect3d.getMultiplierForHull2(_a, _b, newPosition);
      PointOnRibAnchorBuilder.this.setValue(BLDKEY_ALPHA, result);
    } catch (ExDegeneration ex) { }
  }
}
