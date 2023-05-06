package builders;

import bodies.PointBody;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import static config.Config.PRECISION;
import editor.Editor;
import editor.ExNoAnchor;
import editor.behavior.BehaviorFactory;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExDegeneration;
import geom.Line3d;
import geom.Rib3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Point builder.
 * Point divides rib in given relation.
 * @author alexeev
 */
public class DivideRibInRelationBuilder extends BodyBuilder {
  static public final String ALIAS = "DivideRibInRelation";

  public DivideRibInRelationBuilder() {
  }
  
  public DivideRibInRelationBuilder(String id, String name) {
    super(id, name);
  }

  public DivideRibInRelationBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public DivideRibInRelationBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_RIB, param.get(BLDKEY_RIB).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
    setValue(BLDKEY_BETA, param.get(BLDKEY_BETA).asDouble());
  }

  public DivideRibInRelationBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_RIB, BuilderParam.RIB_ALIAS, BuilderParamType.ANCHOR);
    addParam(BLDKEY_ALPHA, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE, 41);
    addParam(BLDKEY_BETA, BuilderParam.COEF_ALIAS, BuilderParamType.DOUBLE, 40);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    double alpha = getValueAsDouble(BLDKEY_ALPHA);
    double beta = getValueAsDouble(BLDKEY_BETA);
    try {
      if( getValueAsDouble(BLDKEY_ALPHA) <= 0 || getValueAsDouble(BLDKEY_BETA) <= 0){
        throw new ExDegeneration(String.format("нельзя разбить ребро в отношении %s : %s", 
                Util.valueOf(alpha, PRECISION.value()),
                Util.valueOf(beta, PRECISION.value())));
      }
      i_Anchor a = anchors.get(getValueAsString(BLDKEY_RIB));
      Rib3d rib = a.getRib();
      Vect3d p = rib.innerPoint(alpha / (alpha + beta));
      PointBody result = new PointBody(_id, p);
      edt.addAnchor(result.point(), result, "P", title());
      _exists = true;
      return result;
    } catch(ExNoAnchor | ExDegeneration ex){
      if (_exists)
        eh.showMessage("Не удалось разбить ребро в заданном отношении: " +
                ex.getMessage(), error.Error.WARNING);
      _exists = false;
      return new PointBody(_id);
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_RIB, getValueAsString(BLDKEY_RIB));
    result.add(BLDKEY_ALPHA, getValueAsDouble(BLDKEY_ALPHA));
    result.add(BLDKEY_BETA, getValueAsDouble(BLDKEY_BETA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Точка</strong>,<br> разбивающая ребро %s<br>"
              + " в отношении %s : %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_RIB)),
        Util.valueOf(getValueAsDouble(BLDKEY_ALPHA), precision),
        Util.valueOf(getValueAsDouble(BLDKEY_BETA), precision));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Точка</strong>";
    }
  }
};
