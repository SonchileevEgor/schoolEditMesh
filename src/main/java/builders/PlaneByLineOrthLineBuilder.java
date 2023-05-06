package builders;

import bodies.LineBody;
import bodies.PlaneBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Plane3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class PlaneByLineOrthLineBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByLineOrthLine";
  public static final String BLDKEY_LINE1 = "line1";
  public static final String BLDKEY_LINE2 = "line2";

  public PlaneByLineOrthLineBuilder() {
  }

  public PlaneByLineOrthLineBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByLineOrthLineBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PlaneByLineOrthLineBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE1, param.get(BLDKEY_LINE1).asString());
    setValue(BLDKEY_LINE2, param.get(BLDKEY_LINE2).asString());
  }

  public PlaneByLineOrthLineBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE1, "Первая прямая", BuilderParamType.BODY, 61);
    addParam(BLDKEY_LINE2, "Вторая прямая", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try{
      LineBody line1 = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE1));
      if (!line1.exists())
        throw new ExLostBody("Прямая1");
      LineBody line2 = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE2));
      if (!line2.exists())
        throw new ExLostBody("Прямая2");
      Plane3d plane = Plane3d.planeByPointOrthLine(line1.pnt(), line2.line());
      i_Body result = null;
      if (!plane.containsLine(line1.line()))
          throw new ExDegeneration("Невозможно построить такую плоскость");

      result = new PlaneBody(_id, title(), plane);
      _exists = true;
      return result;
    }catch (ExNoBody | ExDegeneration ex) {
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
     result.add(BLDKEY_LINE1, getValueAsString(BLDKEY_LINE1));
     result.add(BLDKEY_LINE2, getValueAsString(BLDKEY_LINE2));
     return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Плоскость</strong>, проходящая через прямую %s и перпендиклярная "
              + "прямой %s .",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_LINE1)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_LINE2)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Плоскость</strong>";
    }
  }
}
