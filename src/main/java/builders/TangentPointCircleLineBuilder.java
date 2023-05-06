package builders;

import bodies.LineBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Circle3d;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class TangentPointCircleLineBuilder extends BodyBuilder {
  static public final String ALIAS = "TangentPointCircleLine";

  public TangentPointCircleLineBuilder() {
  }

  public TangentPointCircleLineBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_DISK, param.get(BLDKEY_DISK).asString());
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
  }

  public TangentPointCircleLineBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public TangentPointCircleLineBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_DISK, BuilderParam.CIRCLE_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_LINE, BuilderParam.LINE_ALIAS, BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      Circle3d circle = anchors.get(getValueAsString(BLDKEY_DISK)).getDisk();
      LineBody line = (LineBody) bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists()) {
        throw new ExLostBody("Прямая");
      }
      ArrayList<Vect3d> intersect = circle.intersectWithLine(line.line());
      if (intersect.size() == 1) {
        // line is tangent to circle
        PointBody result = new PointBody(_id, intersect.get(0));
        edt.addAnchor(result.point(), result, "P");
        _exists = true;
        return result;
      } else {
        throw new ExDegeneration("Прямая и окружность не касаются");
      }
    } catch (ExBadRef | ExDegeneration ex) {
      if (_exists) {
        eh.showMessage("Точка не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_DISK, getValueAsString(BLDKEY_DISK));
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong> касания окружности %s и прямой %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_DISK)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
