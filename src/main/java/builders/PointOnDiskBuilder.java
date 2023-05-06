package builders;

import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.behavior.BehaviorFactory;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Построение точки по кругу, на котором она находится
 * и координатам.
 *
 * @author alexeev
 */
public class PointOnDiskBuilder extends BodyBuilder implements i_MovablePointBuilder {
  static public final String ALIAS = "PointOnDisk";
  private Circle3d _disk;

  public PointOnDiskBuilder() {
  }

  public PointOnDiskBuilder(String id, String name) {
    super(id, name);
  }

  public PointOnDiskBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PointOnDiskBuilder(String id, String name, JsonObject param){
    super(id, name);
    setValue(BLDKEY_DISK, param.get(BLDKEY_DISK).asString());
    setValue(BLDKEY_RHO, param.get(BLDKEY_RHO).asDouble());
    setValue(BLDKEY_PHI, param.get(BLDKEY_PHI).asDouble());
  }

  public PointOnDiskBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_DISK, BuilderParam.DISK_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_RHO, BuilderParam.DIST_TO_CENTER_ALIAS, BuilderParamType.DOUBLE);
    addParam(BLDKEY_PHI, BuilderParam.ANGLE_ALIAS, BuilderParamType.ANGLE_VALUE);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      _disk = anchors.get(getValueAsString(BLDKEY_DISK)).getDisk();
      double rho = (double)getValue(BLDKEY_RHO) * _disk.radiusLength();
      double phi = (double)getValue(BLDKEY_PHI);
      if (rho < 0) {
        throw new ExGeom("Точка должна лежать внутри круга");
      }
      PointBody result = new PointBody(_id, _disk.getPointByPolar(phi, rho));
      edt.addAnchor(result.point(), result, "P", title());

      /**
       * Определяем поведение при перемещении точки.
       */
      edt.anchors().get(result.getAnchorID("P")).setMovable(true); //!!TODO: избвавиться от дублирования опции
      result.setBehavior(BehaviorFactory.createDiskBehavior(_disk));
      _exists = true;
      return result;
    } catch(ExBadRef | ExGeom ex){
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_DISK, getValueAsString(BLDKEY_DISK));
    result.add(BLDKEY_RHO, (double)getValue(BLDKEY_RHO));
    result.add(BLDKEY_PHI, (double)getValue(BLDKEY_PHI));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong>, на круге %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_DISK)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Точка</strong>";
    }
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    try {
      double rho = Vect3d.dist(_disk.center(), newPosition) / _disk.radiusLength();
      double phi = _disk.getPolarAngleByPoint(newPosition);
      setValue(BLDKEY_RHO, rho);
      setValue(BLDKEY_PHI, phi);
    } catch (ExDegeneration ex) { }
  }
}
