package builders;

import bodies.IntersectionBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import static geom.Intersect2d.intersect2d;
import geom.Vect3d;
import geom.i_Geom;
import gui.EdtController;
import java.util.ArrayList;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Elena
 */
public class IntersectionBuilder extends BodyBuilder {
  static public final String ALIAS = "Intersection";
  static public final String BLDKEY_BODY_1 = "body1";
  static public final String BLDKEY_BODY_2 = "body2";

  public IntersectionBuilder() {
  }

  public IntersectionBuilder(String name) {
    super(name);
  }

  public IntersectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public IntersectionBuilder(String id, String name, JsonObject param ){
    super(id, name);
    addBody1(param.get(BLDKEY_BODY_1).asString());
    addBody2(param.get(BLDKEY_BODY_2).asString());
  }

  public final void addBody1(String id) {
    setValue(BLDKEY_BODY_1, id);
  }

  public final void addBody2(String id) {
    setValue(BLDKEY_BODY_2, id);
  }

  public IntersectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BODY_1, "Первая кривая", BuilderParamType.BODY, 61);
    addParam(BLDKEY_BODY_2, "Вторая кривая", BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bd = edt.bd();
    try {
      i_Body body1 = bd.get(getValueAsString(BLDKEY_BODY_1));
      i_Body body2 = bd.get(getValueAsString(BLDKEY_BODY_2));
      if (!body1.exists())
        throw new ExLostBody("тело");
      if (!body2.exists())
        throw new ExLostBody("тело");

      i_Geom geom1 = body1.getGeom();
      i_Geom geom2 = body2.getGeom();
      ArrayList<Vect3d> intersectionPoints = intersect2d(geom1, geom2);

      if (intersectionPoints.isEmpty())
        throw new ExDegeneration("Кривые не пересекаются");
      IntersectionBody result = new IntersectionBody(_id, title(), intersectionPoints);
      for (int j = 0; j < intersectionPoints.size(); j++){        
        edt.addAnchor(intersectionPoints.get(j), result, String.valueOf(j));
      }
      _exists = true;
      return result;
    } catch( ExNoBody | ExDegeneration ex ){
      if (_exists)
        eh.showMessage("Пересечение не построено: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new IntersectionBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_BODY_1, getValueAsString(BLDKEY_BODY_1));
    result.add(BLDKEY_BODY_2, getValueAsString(BLDKEY_BODY_2));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Пересечение</strong> кривой %s<br> и кривой %s",
        ctrl.getBodyTitle(getValueAsString(BLDKEY_BODY_1)),
        ctrl.getBodyTitle(getValueAsString(BLDKEY_BODY_2)));
    } catch (ExNoBody ex) {
      return "<html><strong>Тело</strong>";
    }
  }
};
