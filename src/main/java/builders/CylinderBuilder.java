package builders;

import bodies.CylinderBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.Cylinder3d;
import geom.ExDegeneration;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

/**
 * Constructs cylinder.
 * By its center of the top base
 * center and radius of the bottom base
 * @author alexeev
 */
public class CylinderBuilder extends BodyBuilder {
  static public final String ALIAS = "Cylinder";
  
  public static final String BLDKEY_CENTER_1 = "c1";
  public static final String BLDKEY_CENTER_2 = "c2";
   
  public CylinderBuilder() {
  }

  public CylinderBuilder(String id, String name) {
    super(id, name);
  }
  
  public CylinderBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }
  
  public CylinderBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_CENTER_1, param.get(BLDKEY_CENTER_1).asString());
    setValue(BLDKEY_CENTER_2, param.get(BLDKEY_CENTER_2).asString());
    setValue(BLDKEY_RADIUS, param.get(BLDKEY_RADIUS).asDouble());
  }
  
  public CylinderBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_CENTER_1, "Центр первого основания", BuilderParamType.ANCHOR, 101);
    addParam(BLDKEY_CENTER_2, "Центр второго основания", BuilderParamType.ANCHOR, 100);
    addParam(BLDKEY_RADIUS, "Радиус основания", BuilderParamType.DOUBLE_POSITIVE);
  }

  @Override
  public String alias() { return ALIAS; }
  
  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh){
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor c1 = anchors.get(getValueAsString(BLDKEY_CENTER_1));
      i_Anchor c2 = anchors.get(getValueAsString(BLDKEY_CENTER_2));
      Cylinder3d cylinder = new Cylinder3d(c1.getPoint(), c2.getPoint(), getValueAsDouble(BLDKEY_RADIUS));
      CylinderBody result = new CylinderBody(_id, title(), cylinder);
      result.addAnchor(BLDKEY_CENTER_1, c1.id());
      result.addAnchor(BLDKEY_CENTER_2, c2.id());
      edt.addAnchor(result.cylinder().circ1(), c1.id(), result, "disk1");
      edt.addAnchor(result.cylinder().circ2(), c2.id(), result, "disk2");
      _exists = true;
      return result;
    } catch(ExNoAnchor | ExDegeneration ex){
      if (_exists)
				eh.showMessage("Цилиндр не построен: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
			return new CylinderBody(_id, title());
    }
  }
  
  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_CENTER_1, getValueAsString(BLDKEY_CENTER_1));
    result.add(BLDKEY_CENTER_2, getValueAsString(BLDKEY_CENTER_2));
    result.add(BLDKEY_RADIUS, getValueAsDouble(BLDKEY_RADIUS));
    
    return result;
  }
  
  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Цилиндр</strong> с радиусом основания %s,<br>центрами оснований %s и %s",
        Util.valueOf(getValueAsDouble(BLDKEY_RADIUS), precision),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_CENTER_1)),
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_CENTER_2)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Цилиндр</strong>";
    }
  }
}
