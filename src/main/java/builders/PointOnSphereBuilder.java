package builders;

import bodies.PointBody;
import bodies.SphereBody;
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
import geom.SpherCoord;
import geom.Sphere3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Построение точки по сфере, на которой она находится
 * и паре сферических координат.
 *
 * @author alexeev
 */
public class PointOnSphereBuilder extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "PointOnSphere";
  private Sphere3d _sphere;

  public PointOnSphereBuilder() {
  }

  public PointOnSphereBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnSphereBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnSphereBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_SPHERE, param.get(BLDKEY_SPHERE).asString());
    setValue(BLDKEY_PHI, param.get(BLDKEY_PHI).asDouble());
    setValue(BLDKEY_THETA, param.get(BLDKEY_THETA).asDouble());
  }

  public PointOnSphereBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_SPHERE, "Сфера", BuilderParamType.BODY);
    addParam(BLDKEY_PHI, "Азимут", BuilderParamType.DOUBLE, 41);
    addParam(BLDKEY_THETA, "Зенит", BuilderParamType.DOUBLE, 40);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bd = edt.bd();
    try {
      SphereBody sphere = (SphereBody)bd.get(getValueAsString(BLDKEY_SPHERE));
      if (!sphere.exists())
        throw new ExLostBody("сфера");
      _sphere = sphere.sphere();
      double phi = (double)getValue(BLDKEY_PHI);
      double theta = (double)getValue(BLDKEY_THETA);
      SpherCoord coords = new SpherCoord(phi, theta, sphere.getRadius(), sphere.getCenter());
      PointBody result = new PointBody(_id, coords.toCartesian());
      edt.addAnchor(result.point(), result, "P", title());
      /**
       * Определяем поведение при перемещении точки.
       */
      edt.anchors().get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createSphereBehavior(_sphere));

      _exists = true;
      return result;
    } catch(ExBadRef ex){
      if (_exists)
        eh.showMessage("Точка на сфере не построена" + ex.getMessage(),error.Error.WARNING);
      _exists = false;
			return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_SPHERE, getValueAsString(BLDKEY_SPHERE));
    result.add(BLDKEY_THETA, (double)getValue(BLDKEY_THETA));
    result.add(BLDKEY_PHI, (double)getValue(BLDKEY_PHI));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong> на сфере %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_SPHERE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    SpherCoord coords = new SpherCoord(newPosition, _sphere.center());
    setValue(BLDKEY_THETA, coords.getTheta());
    setValue(BLDKEY_PHI, coords.getPhi());
  }
}
