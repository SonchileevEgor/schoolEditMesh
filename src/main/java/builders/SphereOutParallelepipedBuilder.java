package builders;

import bodies.PrismBody;
import bodies.SphereBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.ExGeom;
import geom.ExZeroDivision;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author gayduk
 */
public class SphereOutParallelepipedBuilder extends BodyBuilder {
  static public final String ALIAS = "SphereOutParallelepiped";
  public static final String BLDKEY_PARALLELEPIPED = "par";

  public SphereOutParallelepipedBuilder() {
  }

  public SphereOutParallelepipedBuilder(String id, String name) {
    super(id, name);
  }

  public SphereOutParallelepipedBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public SphereOutParallelepipedBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_PARALLELEPIPED, param.get(BLDKEY_PARALLELEPIPED).asString());
  }

  public SphereOutParallelepipedBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PARALLELEPIPED, "Прямоугольный параллелепипед", BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      PrismBody par = (PrismBody) bodies.get(getValueAsString(BLDKEY_PARALLELEPIPED));
      if (!par.exists()) {
        throw new ExLostBody("прямоугольный параллелепипед");
      }

      SphereBody result = new SphereBody(_id, title(), par.outSphere());
      edt.addAnchor(result.sphere().center(), result, "center");
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef | ExZeroDivision ex) {
      if (_exists) {
        eh.showMessage("Сфера не построена: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new SphereBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_PARALLELEPIPED, getValueAsString(BLDKEY_PARALLELEPIPED));

    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сфера</strong><br>описанная около прямоугольного параллелепипеда %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PARALLELEPIPED)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сфера</strong>";
    }
  }
}
