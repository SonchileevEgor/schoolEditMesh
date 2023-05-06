package builders;

import bodies.PrismBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Prism3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Constructs a regular prism by the two points of base polygon,
 * plane and first vertex of another base
 */
public class PrismRegularBuilder extends BodyBuilder {
  static public final String ALIAS = "RegularPrism";
  
  /**
   * Максимальное количество вершин.
   */
  public static final int MAX_VERT = 20;
  
  public PrismRegularBuilder() {
  }

  public PrismRegularBuilder(String name) {
    super(name);
  }

  public PrismRegularBuilder(String id, String name) {
    super(id, name);
  }

  public PrismRegularBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PrismRegularBuilder ( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_A, param.get(BLDKEY_A).asString());
    setValue(BLDKEY_B, param.get(BLDKEY_B).asString());
    setValue(BLDKEY_HEIGHT, param.get(BLDKEY_HEIGHT).asDouble());
    setValue(BLDKEY_ANGLE, param.get(BLDKEY_ANGLE).asDouble());
    setValue(BLDKEY_POINT_NUM, param.get(BLDKEY_POINT_NUM).asInt());

    for( int i = 2; i < MAX_VERT * 2; i++ ){
      String paramName = "name" + i;
      if( param.get(paramName) != null) {
        setValue(paramName, param.get(paramName).asString());
      }
    }
  }

  public PrismRegularBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_A, "1-я точка основания", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_B, "2-я точка основания", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_HEIGHT, "Высота", BuilderParamType.DOUBLE_POSITIVE);
    addParam(BLDKEY_ANGLE, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
    addParam(BLDKEY_POINT_NUM, "Количество вершин", BuilderParamType.INT);
    for( int i = 2; i < MAX_VERT * 2; i++ ){
      addParam("name" + i, "Точка " + i, BuilderParamType.NAME);
    }
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      Vect3d a = anchors.getVect(getValueAsString(BLDKEY_A));
      Vect3d b = anchors.getVect(getValueAsString(BLDKEY_B));
      double height = (double)getValue(BLDKEY_HEIGHT);
      double angle = (double)getValue(BLDKEY_ANGLE);
      int vertNum = (int)getValue(BLDKEY_POINT_NUM);
      Prism3d pri = Prism3d.regPrismBy2PntsHeightAngle(a, b, vertNum, height, angle);
      PrismBody result = new PrismBody(_id, title(), pri);
      result.addAnchor("0", anchors.get(getValueAsString(BLDKEY_A)).id());
      result.addAnchor("1", anchors.get(getValueAsString(BLDKEY_B)).id());
      for (int j = 2; j < result.prism().points().size() / 2; j++){
        edt.addAnchor(result.prism().points().get(j), result, String.valueOf(j),
            hasParam("name" + j) ? getValueAsString("name" + j) : null);
      }
      for (int j = result.prism().points().size() / 2 ; j < result.prism().points().size(); j++){
        edt.addAnchor(result.prism().points().get(j), result, String.valueOf(j),
            hasParam("name" + j) ? getValueAsString("name" + j) : null);
      }

      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch( ExGeom | ExBadRef ex ){
      if (_exists) {
				eh.showMessage("Призма не построена: " + ex.getMessage(),error.Error.WARNING);
        _exists = false;
      }
			return new PrismBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_A, getValueAsString(BLDKEY_A));
    result.add(BLDKEY_B, getValueAsString(BLDKEY_B));
    result.add(BLDKEY_HEIGHT, (double)getValue(BLDKEY_HEIGHT));
    result.add(BLDKEY_ANGLE, (double)getValue(BLDKEY_ANGLE));
    result.add(BLDKEY_POINT_NUM, (int)getValue(BLDKEY_POINT_NUM));

    for( int i = 3; i <= (int)getValue(BLDKEY_POINT_NUM) * 2; i++ ){
      String paramName = "name" + i;
      if( hasParam(paramName) ) {
        result.add(paramName, getValueAsString(paramName));
      }
    }
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Правильная %s-угольная призма</strong><br>со стороной основания %s%s<br> и длиной высоты %s",
              getValue(BLDKEY_POINT_NUM),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_A)),
              ctrl.getAnchorTitle(getValueAsString(BLDKEY_B)),
              getValue(BLDKEY_HEIGHT));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Правильная призма</strong>";
    }
  }
}
