package builders;

import bodies.LineBody;
import bodies.RayBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Line3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class LineOrthogonalRay2dBuilder extends BodyBuilder {
  static public final String ALIAS = "LineOrthogonalRay2d";

  public LineOrthogonalRay2dBuilder() {
  }

  public LineOrthogonalRay2dBuilder(String id, String name) {
    super(id, name);
  }

  public LineOrthogonalRay2dBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineOrthogonalRay2dBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_P, param.get(BLDKEY_P).asString());
    setValue(BLDKEY_RAY, param.get(BLDKEY_RAY).asString());
  }

  public LineOrthogonalRay2dBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_RAY, "Ортогональный луч", BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    i_BodyContainer bodies = edt.bd();
    try {
      RayBody ray = (RayBody)bodies.get(getValueAsString(BLDKEY_RAY));
      if (!ray.exists())
        throw new ExLostBody("Луч");
      Line3d line = Line3d.linePerpendicularLine2d(ray.ray().line(), anchors.getVect(getValueAsString(BLDKEY_P)));
      LineBody result = new LineBody(_id, title(), line);
      result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      if (ray.ray().containsPoint(line.intersectionWithLine(ray.ray().line())))
        _exists = true;
      return result;
    } catch (ExNoAnchor | ExNoBody | ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Прямая не построена: " + ex.getMessage(), error.Error.WARNING);
      _exists = false;
      return new LineBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_P, getValueAsString(BLDKEY_P));
    result.add(BLDKEY_RAY, getValueAsString(BLDKEY_RAY));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Прямая</strong>, проходящая через точку %s<br>перпендикулярная лучу %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_RAY)));
    } catch (ExNoAnchor | ExNoBody ex) {
      return "<html><strong>Прямая</strong>";
    }
  }
}

