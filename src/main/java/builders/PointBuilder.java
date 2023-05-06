package builders;

import bodies.PointBody;
import static bodies.PointBody.BODY_KEY_POINT;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.behavior.BehaviorFactory;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * 3D point builder.
 * @author alexeev
 */
public class PointBuilder extends BodyBuilder implements i_MovablePointBuilder {
  public static final String ALIAS = "Point";
  
  public PointBuilder() {
  }
  
  public PointBuilder(String name) {
    super(name);
  }

  public PointBuilder(String id, String name) {
    super(id, name);
  }

  public PointBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_P, new Vect3d(param.get("x").asDouble(),
                                  param.get("y").asDouble(),
                                  param.get("z").asDouble()));
  }

  public PointBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams() {
    super.initParams();
    addParam(BLDKEY_P, BuilderParam.COORD_ALIAS, BuilderParamType.POINT);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    PointBody result = new PointBody(_id, getValueAsVect(BLDKEY_P));
    edt.addAnchor(result.point(), result, BODY_KEY_POINT, title());
    try {
      edt.anchors().get(result.getAnchorID(BODY_KEY_POINT)).setMovable(true);
    } catch (ExNoAnchor ex) {}

    result.setBehavior(BehaviorFactory.createFreeBehavior());
    return result;
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add("x", getValueAsVect(BLDKEY_P).x());
    result.add("y", getValueAsVect(BLDKEY_P).y());
    result.add("z", getValueAsVect(BLDKEY_P).z());

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    return String.format("<html><strong>Точка</strong> %s",
            getValueAsVect(BLDKEY_P).toString(precision, ctrl.getScene().is3d()));
  }

  @Override
  public void movePoint(Vect3d newPosition) {
    setValue(BLDKEY_P, newPosition);
  }
};
