package builders;

import bodies.PlaneBody;
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
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author alexeev
 */
public class PointOnPlaneBuilder extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "PointOnPlane";
  private Vect3d _a;
  private Vect3d _b;
  private Vect3d _c;

  public PointOnPlaneBuilder() {
  }

  public PointOnPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnPlaneBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnPlaneBuilder(String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
    setValue(BLDKEY_BETA, param.get(BLDKEY_BETA).asDouble());
  }

  public PointOnPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY);
    addParam(BLDKEY_ALPHA, BuilderParam.COEF_1_ALIAS, BuilderParamType.DOUBLE, 41);
    addParam(BLDKEY_BETA, BuilderParam.COEF_2_ALIAS, BuilderParamType.DOUBLE, 40);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bd = edt.bd();
    try {
      PlaneBody plane = (PlaneBody)bd.get(getValueAsString(BLDKEY_PLANE));
      if (!plane.exists())
        throw new ExLostBody("плоскость");
      Plane3d pl = plane.plane();
      _a = pl.pnt();
      _b = pl.pnt2();
      _c = pl.pnt3();
      Vect3d p = Vect3d.getVectFromAffineCoords(_a, _b, _c,
              (double)getValue(BLDKEY_ALPHA), (double)getValue(BLDKEY_BETA));
      PointBody result = new PointBody(_id, p);
      edt.addAnchor(result.point(), result, "P", title());
      edt.anchors().get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createPlaneBehavior(pl));
      _exists = true;
      return result;
    } catch(ExNoBody | ExNoAnchor ex){
      if (_exists)
        eh.showMessage("Точка на плоскости не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    result.add(BLDKEY_BETA, (double)getValue(BLDKEY_BETA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong>, на плоскости %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    try {
      double[] coefs = Vect3d.getMultipliersForHull3(_a, _b, _c, newPosition);
      setValue(BLDKEY_ALPHA, coefs[0]);
      setValue(BLDKEY_BETA, coefs[1]);
    } catch (ExDegeneration ex) { }
  }
}
