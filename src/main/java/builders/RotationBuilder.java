package builders;

import bodies.EmptyBody;
import static builders.BodyBuilder.BLDKEY_ANGLE;
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
import geom.Angle3d;
import geom.ExDegeneration;
import geom.SpaceTransformation;
import geom.Vect3d;
import geom.i_Geom;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 *
 * @author Elena
 */
public class RotationBuilder extends BodyBuilder {
  static public final String ALIAS = "Rotation";
  public static final String BLDKEY_P1 = "p1";
  public static final String BLDKEY_P2 = "p2";

  public RotationBuilder() {
  }
  
  public RotationBuilder(String name) {
    super(name);
  }

  public RotationBuilder(String id, String name) {
    super(id, name);
  }

  public RotationBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public RotationBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_BODY, param.get(BLDKEY_BODY).asString());
    setValue(BLDKEY_P1, param.get(BLDKEY_P1).asString());
    setValue(BLDKEY_P2, param.get(BLDKEY_P2).asString());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
  }

  public RotationBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BODY, "Поворачиваеимое тело", BuilderParamType.BODY);
    addParam(BLDKEY_P1, "Первая точка на оси поворота", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_P2, "Вторая точка на оси поворота", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_VALUE);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_BodyContainer bd = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d p1 = anchors.getVect(getValueAsString(BLDKEY_P1));
      Vect3d p2 = anchors.getVect(getValueAsString(BLDKEY_P2));
      i_Body body = bd.get(getValueAsString(BLDKEY_BODY));
      if (!body.exists())
        throw new ExLostBody(body.alias());
      double alpha = getValueAsDouble(BLDKEY_ANGLE);
      i_Body result;
      i_Geom geom_result = SpaceTransformation.rotationOfObject(body.getGeom(), p1, p2, alpha);
      result = body.getBody(_id, title(), geom_result);

      result.addAnchorsToBody(result, edt);
      result.addRibs(edt);
      result.addPlanes(edt);

      _exists = true;
      return result;
    } catch( ExNoBody | ExNoAnchor | ExDegeneration ex ){
      if (_exists)
        eh.showMessage("Тело не построено: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new EmptyBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_BODY, getValueAsString(BLDKEY_BODY));
    result.add(BLDKEY_P1, getValueAsString(BLDKEY_P1));
    result.add(BLDKEY_P2, getValueAsString(BLDKEY_P2));
    result.add(BLDKEY_ANGLE, getValueAsDouble(BLDKEY_ANGLE));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Тело,</strong><br> полученное в результате поворота тела %s<br>"
              + "вокруг вектора с началом в %s и концом в %s<br>на угол %s\u00B0",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_BODY)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P1)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_P2)),
              Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ANGLE)), precision));
    } catch (ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Тело</strong>";
    }
  }
}