package builders;

import bodies.PyramidBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.Pyramid3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Kurgansky
 */
public class PyramidRectangBuilder extends BodyBuilder {
  static public final String ALIAS = "PyramidRectang";

  public PyramidRectangBuilder() {
  }

  public PyramidRectangBuilder(String id, String name) {
    super(id, name);
  }

  public PyramidRectangBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public PyramidRectangBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_BASE, param.get(BLDKEY_BASE).asString());
    setValue(BLDKEY_HEIGHT, param.get(BLDKEY_HEIGHT).asDouble());
    setValue(BLDKEY_POINT_NUM, param.get(BLDKEY_POINT_NUM).asInt());
  }

  public PyramidRectangBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_BASE, "Основание", BuilderParamType.ANCHOR);
    addParam(BLDKEY_HEIGHT, "Высота", BuilderParamType.DOUBLE_POSITIVE);
    addParam(BLDKEY_POINT_NUM, "Номер основания высоты", BuilderParamType.INT);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_AnchorContainer anchors = edt.anchors();
    try {
      i_Anchor base = anchors.get(getValueAsString(BLDKEY_BASE));
      double height = getValueAsDouble(BLDKEY_HEIGHT);
      int vertNum = getValueAsInt(BLDKEY_POINT_NUM);

      Pyramid3d p = Pyramid3d.rectangPyramidByPoly(base.getPoly(), vertNum, height);
      PyramidBody result = new PyramidBody(_id, title(), p);

      for (int i = 0; i < base.arrayIDs().size(); i++) {
        result.addAnchor(String.valueOf(i), base.arrayIDs().get(i));
      }
      //result.addAnchor(BLDKEY_TOP, result.topAnchorID());
      edt.addAnchor(result.pyramid().top(), result, BLDKEY_TOP);
      result.addRibs(edt);
      result.addPlanes(edt);
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Пирамида не построена: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new PyramidBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_BASE, getValueAsString(BLDKEY_BASE));
    result.add(BLDKEY_HEIGHT, getValueAsDouble(BLDKEY_HEIGHT));
    result.add(BLDKEY_POINT_NUM, getValueAsInt(BLDKEY_POINT_NUM));
    return result;
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
   try {
      return String.format("<html><strong>Прямоугольная пирамида</strong><br>c основанием %s",
        ctrl.getAnchorTitle(getValueAsString(BLDKEY_BASE)));
    } catch (ExNoAnchor ex) {
      return "<html><strong>Пирамида</strong>";
    }
  }

}
