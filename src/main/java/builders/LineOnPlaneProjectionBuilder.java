package builders;

import bodies.LineBody;
import bodies.PlaneBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Checker;
import geom.ExDegeneration;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class LineOnPlaneProjectionBuilder extends BodyBuilder {
  static public final String ALIAS = "LineOnPlaneProjection";

  public LineOnPlaneProjectionBuilder() {
  }

  public LineOnPlaneProjectionBuilder(String id, String name) {
    super(id, name);
  }

  public LineOnPlaneProjectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public LineOnPlaneProjectionBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
  }

  public LineOnPlaneProjectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PLANE, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY, 61);
    addParam(BLDKEY_LINE, "Проецируемая прямая", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      PlaneBody pl = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!pl.exists())
        throw new ExLostBody("плоскость");

      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      if (!line.exists())
        throw new ExLostBody("прямая");

      i_Body result;
      if (pl.plane().containsLine(line.line())) {
        throw new ExDegeneration("Прямая лежит в плоскости");
      } else if (Checker.isCollinear(pl.plane().n(), line.l())) { // line is orthogonal to plane
        Vect3d intersect = line.line().intersectionWithPlane(pl.plane());
        result = new PointBody(_id, intersect);
        edt.addAnchor(intersect, result, "P");
      } else {
        result = new LineBody(_id, title(), line.line().projectOnPlane(pl.plane()));
      }

      _exists = true;
      return result;
    } catch( ExNoBody | ExDegeneration ex ){
      if (_exists)
	eh.showMessage("Проекция не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new LineBody(_id, title());
    }
  }



  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    return result;
  }

    @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Проекция</strong> прямой %s на плоскость %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Точка</strong>";
    }
  }
}
