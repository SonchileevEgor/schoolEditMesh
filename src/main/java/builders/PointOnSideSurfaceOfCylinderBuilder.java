package builders;

import bodies.CylinderBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.behavior.BehaviorFactory;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Cylinder3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Построение точки по боковой поверхности цилиндра, на которой она находится
 * и паре сферических координат.
 *
 * @author gayduk
 */
public class PointOnSideSurfaceOfCylinderBuilder extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "PointOnSideSurfaceOfCylinder";
  private Cylinder3d _cylinder;

  public PointOnSideSurfaceOfCylinderBuilder() {
  }

  public PointOnSideSurfaceOfCylinderBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnSideSurfaceOfCylinderBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnSideSurfaceOfCylinderBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_CYLINDER, param.get(BLDKEY_CYLINDER).asString());
    setValue(BLDKEY_PHI, param.get(BLDKEY_PHI).asDouble());
    setValue(BLDKEY_RHO, param.get(BLDKEY_RHO).asDouble());
  }

  public PointOnSideSurfaceOfCylinderBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CYLINDER, "Цилиндр", BuilderParamType.BODY);
    addParam(BLDKEY_PHI, BuilderParam.ANGLE_ALIAS, BuilderParamType.DOUBLE, 41);
    addParam(BLDKEY_RHO, "Отношение расстояния от точки до нижнего основания к высоте цилиндра",
                                      BuilderParamType.DOUBLE, 40);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bd = edt.bd();
    try {
      CylinderBody cylinder = (CylinderBody)bd.get(getValueAsString(BLDKEY_CYLINDER));
      if (!cylinder.exists())
        throw new ExLostBody("цилиндр");
      _cylinder = cylinder.cylinder();
      double phi = getValueAsDouble(BLDKEY_PHI);
      double rho = getValueAsDouble(BLDKEY_RHO);
      Vect3d point = _cylinder.circ2().getPointByPolar(phi, _cylinder.circ2().radiusLength());
      point.inc_add(_cylinder.c0().sub(_cylinder.c1()).mul(rho));
      PointBody result = new PointBody(_id, point);
      edt.addAnchor(result.point(), result, "P", title());
      /**
       * Определяем поведение при перемещении точки.
       */
      edt.anchors().get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createCylinderBehavior(_cylinder));

      _exists = true;
      return result;
    } catch(ExBadRef ex){
      if (_exists)
        eh.showMessage("Точка на цилиндре не построена" + ex.getMessage(),error.Error.WARNING);
      _exists = false;
			return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CYLINDER, getValueAsString(BLDKEY_CYLINDER));
    result.add(BLDKEY_PHI, getValueAsDouble(BLDKEY_PHI));
    result.add(BLDKEY_RHO, getValueAsDouble(BLDKEY_RHO));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong> на цилиндре %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_CYLINDER)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    try {
      Vect3d p = _cylinder.basePlane1().projectionOfPoint(newPosition);
      double phi = _cylinder.circ2().getPolarAngleByPoint(p);
      double rho = _cylinder.basePlane1().distFromPoint(newPosition) / _cylinder.heightLength();
      setValue(BLDKEY_PHI, phi);
      setValue(BLDKEY_RHO, rho);
    } catch (ExDegeneration ex) { }
  }
}
