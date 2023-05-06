package builders;

import bodies.EllipseMainBody;
import bodies.EllipsoidBody;
import bodies.PlaneBody;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExBadRef;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Checker;
import geom.ExGeom;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 *
 * @author Vitaliy
 */
public class EllipsoidSectionBuilder extends BodyBuilder {
  static public final String ALIAS = "EllipsoidSection";

  public EllipsoidSectionBuilder() {
  }

  public EllipsoidSectionBuilder(String id, String name) {
    super(id, name);
  }

  public EllipsoidSectionBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public EllipsoidSectionBuilder(String id, String name, JsonObject param) {
    super(id, name);
    setValue(BLDKEY_ELLIPSOID, param.get(BLDKEY_ELLIPSOID).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
  }

  public EllipsoidSectionBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_ELLIPSOID, "Эллипсоид", BuilderParamType.BODY, 61);
    addParam(BLDKEY_PLANE, BuilderParam.SECTION_PLANE_ALIAS, BuilderParamType.BODY, 60);
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bodies = edt.bd();
    try {
      PlaneBody p = (PlaneBody) bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!p.exists()) {
        throw new ExLostBody("плоскость");
      }

      EllipsoidBody el = (EllipsoidBody) bodies.get(getValueAsString(BLDKEY_ELLIPSOID));
      if (!el.exists()) {
        throw new ExLostBody("эллипсоид");
      }
      // проверка осуществляется, используя сферу (сжатый эллипсоид) и преобразованную плоскость
      Vect3d c1 = el.convPlane(p.plane()).projectionOfPoint(el.convSphere().center());
      if (Checker.isPointOnSphere(c1, el.convSphere())) {

        PointBody result = new PointBody(_id, p.plane().projectionOfPoint(el.convSphere().center()));
        edt.addAnchor(result.point(), result, "P");
        _exists = true;
        return result;
      } else if (!Checker.isPointInSphere(c1, el.convSphere())) {
        throw new ExGeom("плоскость и эллипсоид не пересекаются");
      }

      EllipseMainBody result = el.section(_id, title(), p);
      edt.addAnchor(result.f1(), result, "F1");
      edt.addAnchor(result.f2(), result, "F2");
      edt.addAnchor(result.pOnBound(), result, "P");
      _exists = true;
      return result;
    } catch (ExGeom | ExBadRef ex) {
      if (_exists) {
        eh.showMessage("Сечение эллипсоида не построено: " + ex.getMessage(), error.Error.WARNING);
      }
      _exists = false;
      return new EllipseMainBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_ELLIPSOID, getValueAsString(BLDKEY_ELLIPSOID));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Сечение</strong> эллипсоида %s плоскостью %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_ELLIPSOID)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
    } catch (ExNoBody ex) {
      return "<html><strong>Сечение эллипсоида</strong>";
    }
  }
}
