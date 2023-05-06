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
import geom.Plane3d;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class MiddlePerpendicularToRibBuilder extends BodyBuilder{
  static public final String ALIAS = "MiddlePerpendicularToRib";

  public MiddlePerpendicularToRibBuilder() {
  }

  public MiddlePerpendicularToRibBuilder(String name) {
    super(name);
  }

  public MiddlePerpendicularToRibBuilder(String id, String name) {
    super(id, name);
  }

  public MiddlePerpendicularToRibBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public MiddlePerpendicularToRibBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addRib(param.get(BLDKEY_RIB).asString());
  }

  public MiddlePerpendicularToRibBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public final void addRib(String id){
    setValue(BLDKEY_RIB, id);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB, "Отрезок", BuilderParamType.ANCHOR);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try{
      Rib3d rib = anchors.get(getValueAsString(BLDKEY_RIB)).getRib();

      LineBody result;
      Line3d line = Vect3d.midLinesArePerpendicularToTheSegment(rib.a(), rib.b(), Plane3d.oXY());
      result = new LineBody(_id, title(), line);
      _exists = true;
      return result;
    } catch(ExNoAnchor | ExDegeneration ex){
      if (_exists)
        eh.showMessage("Срединный перпендикуляр не построен: " + ex.getMessage(), error.Error.WARNING);
      _exists = false;
      return new LineBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Срединный перпендикуляр</strong> отрезка %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Срединный перпендикуляр</strong>";
    }
  }
}
