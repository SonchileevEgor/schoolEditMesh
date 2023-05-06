package builders;

import bodies.LineBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Rib3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class LineParallelRibBuilder extends BodyBuilder {
  static public final String ALIAS = "LineParallelRib";

  public LineParallelRibBuilder() {
  }

  public LineParallelRibBuilder(String id, String name) {
    super(id, name);
  }

  public LineParallelRibBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineParallelRibBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_P, param.get(BLDKEY_P).asString());
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
  }

  public LineParallelRibBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_P, BuilderParam.POINT_ALIAS, BuilderParamType.ANCHOR, 60);
    addParam(BLDKEY_RIB, "Параллельный отрезок", BuilderParamType.ANCHOR, 61);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      Rib3d rib = anchors.get(getValueAsString(BLDKEY_RIB)).getRib();
   //   if(rib.line().contains(anchors.getVect(getValueAsString(BLDKEY_P))))
   //     throw new ExDegeneration("точка лежит на прямой, содержащей отрезок");

      Line3d line = Line3d.lineParallelLine(rib.line(), anchors.getVect(getValueAsString(BLDKEY_P)));
      LineBody result = new LineBody(_id, title(), line);
      result.addAnchor("P", anchors.get(getValueAsString(BLDKEY_P)).id());
      _exists = true;
      return result;
    } catch (ExNoAnchor | ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Прямая не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
        return new LineBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_P, getValueAsString(BLDKEY_P));
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Прямая</strong>, проходящая через точку %s<br>параллельно отрезку %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_P)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Прямая</strong>";
    }
  }
}
