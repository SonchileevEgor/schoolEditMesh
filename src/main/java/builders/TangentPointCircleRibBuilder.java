package builders;

import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Checker;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class TangentPointCircleRibBuilder extends BodyBuilder {
  static public final String ALIAS = "TangentPointCircleRib";

  public TangentPointCircleRibBuilder() {
  }

  public TangentPointCircleRibBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_DISK, param.get(BLDKEY_DISK).asString());
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
  }

  public TangentPointCircleRibBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public TangentPointCircleRibBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_DISK, BuilderParam.CIRCLE_ALIAS, BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_RIB, "Отрезок", BuilderParamType.ANCHOR, 100);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      Circle3d circle = anchors.get(getValueAsString(BLDKEY_DISK)).getDisk();
      Rib3d rib = anchors.get(getValueAsString(BLDKEY_RIB)).getRib();
      ArrayList<Vect3d> intersect = circle.intersectWithLine(rib.line());
      if (intersect.isEmpty())
        throw new ExDegeneration("Окружность и прямая не пересекаются");

      Vect3d tangentPoint = intersect.get(0);

      if( intersect.size() == 1 && Checker.isPointOnSegment(rib, tangentPoint) ){
        // line of rib is tangent to circle
        PointBody result = new PointBody(_id, tangentPoint);
        edt.addAnchor(result.point(), result, "P");
        _exists = true;
        return result;
      } else {
        throw new ExDegeneration("окружность и отрезок не касаются, либо касаются в одном из концов отрезка");
      }
    } catch (ExNoAnchor | ExDegeneration ex) {
        if (_exists) {
          eh.showMessage("Точка не построена: " + ex.getMessage(),error.Error.WARNING);
          _exists = false;
        }
        return new PointBody(_id);
      }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_DISK, getValueAsString(BLDKEY_DISK));
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong> касания окружности %s и отрезка %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_DISK)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
