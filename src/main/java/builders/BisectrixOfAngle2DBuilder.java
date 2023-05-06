package builders;

import bodies.AngleBody;
import bodies.LineBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Line3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Bisectrix of 2D-angle builder.
 * @author Elena
 */
public class BisectrixOfAngle2DBuilder extends BodyBuilder {
  static public final String ALIAS = "BisectrixOfAngle2D";

  public BisectrixOfAngle2DBuilder() {
  }

  public BisectrixOfAngle2DBuilder(String name) {
    super(name);
  }

  public BisectrixOfAngle2DBuilder(String id, String name) {
    super(id, name);
  }

  public BisectrixOfAngle2DBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public BisectrixOfAngle2DBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asString());
  }

  public BisectrixOfAngle2DBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_ANGLE, BuilderParam.ANGLE_ALIAS, BuilderParamType.BODY);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try{
      AngleBody angle = (AngleBody)bodies.get(getValueAsString(BLDKEY_ANGLE));
      if (!angle.exists())
        throw new ExLostBody("угол" + angle.getTitle());
      Line3d line = angle.angle().bisectrix();
      LineBody result = new LineBody(_id, title(), line);
      _exists = true;
      return result;
    } catch (ExNoBody | ExDegeneration ex){
      if (_exists)
        eh.showMessage("Биссектриса не построена: " + ex.getMessage(), error.Error.WARNING);
      _exists = false;
      return new LineBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_ANGLE, getValueAsString(BLDKEY_ANGLE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Биссектриса</strong> угла %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_ANGLE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Биссектриса</strong>";
    }
  }
}
