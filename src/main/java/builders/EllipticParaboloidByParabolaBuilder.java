package builders;

import bodies.EllipticParaboloidBody;
import bodies.ParabolaBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.EllipticParaboloid3d;
import geom.ExDegeneration;
import geom.Parabola3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Ivan
 */
public class EllipticParaboloidByParabolaBuilder extends BodyBuilder {
  static public final String ALIAS = "EllipticParaboloidByParabola";
  public static final String BLDKEY_PARABOLA = "parabola";

  public EllipticParaboloidByParabolaBuilder() {
    super();
  }

  public EllipticParaboloidByParabolaBuilder(String id, String name) {
    super(id, name);
  }

  public EllipticParaboloidByParabolaBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public EllipticParaboloidByParabolaBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public EllipticParaboloidByParabolaBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_PARABOLA, param.get(BLDKEY_PARABOLA).asString());
  }

  @Override
  public void initParams() {
    super.initParams();
    addParam(BLDKEY_PARABOLA, "Парабола", BuilderParamType.BODY);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      ParabolaBody parabola = (ParabolaBody) bodies.get(getValueAsString(BLDKEY_PARABOLA));
      if (!parabola.exists())
        throw new ExLostBody("парабола");

      EllipticParaboloidBody result = new EllipticParaboloidBody(
              _id, title(), new EllipticParaboloid3d(parabola.parabola()));
      _exists = true;
      return result;
    } catch (ExNoBody | ExDegeneration ex) {
      if (_exists) {
       eh.showMessage("Параболоид не построен: " + ex.getMessage(), error.Error.WARNING);
    }
      _exists = false;
      return new EllipticParaboloidBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_PARABOLA, getValueAsString(BLDKEY_PARABOLA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Параболоид</strong> по параболе %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PARABOLA)));
    } catch (ExNoBody ex) {
      return "<html><strong>Параболоид</strong>";
    }
  }
}
