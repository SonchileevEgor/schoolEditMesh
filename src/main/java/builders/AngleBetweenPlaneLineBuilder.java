package builders;

import bodies.AngleBody;
import bodies.i_LinearBody;
import bodies.i_PlainBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.ExGeom;
import geom.ExOrthogonalException;
import geom.Line3d;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;

/**
 * Билдер угла как пересечения чего-то плоского и чего-то прямого.
 * @author alexeev
 */
public class AngleBetweenPlaneLineBuilder extends BodyBuilder {
  public static final String ALIAS = "AngleBetweenPlaneLine";
  public static final String BLDKEY_PLAIN = "plain";
  public static final String BLDKEY_LINEAR = "linear";
  public static final String BLDKEY_DIRECTION1 = "direction1";
  public static final String BLDKEY_DIRECTION2 = "direction2";

  public AngleBetweenPlaneLineBuilder() {
  }
   
  public AngleBetweenPlaneLineBuilder(String name) {
    super(name);
  }

  public AngleBetweenPlaneLineBuilder(String id, String name) {
    super(id, name);
  }

  public AngleBetweenPlaneLineBuilder(String id, HashMap<String, BuilderParam> params) {
    super(id, params);
  }

  public AngleBetweenPlaneLineBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public AngleBetweenPlaneLineBuilder(String id, String name, JsonObject param) {
    super(id, name);
    addPlane(param.get(BLDKEY_PLAIN).asString());
    addLine(param.get(BLDKEY_LINEAR).asString());
    addDirection1(param.get(BLDKEY_DIRECTION1).asBoolean());
    addDirection2(param.get(BLDKEY_DIRECTION2).asBoolean());
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_PLAIN, BuilderParam.PLANE_ALIAS, BuilderParamType.BODY, 102);
    addParam(BLDKEY_LINEAR, BuilderParam.LINE_ALIAS, BuilderParamType.BODY, 101);
    addParam(BLDKEY_DIRECTION1, "Направление на плоскости", BuilderParamType.BOOLEAN, 90);
    addParam(BLDKEY_DIRECTION2, "Направление на прямой", BuilderParamType.BOOLEAN, 80);
  }

  public final void addPlane(String id) {
    setValue(BLDKEY_PLAIN, id);
  }

  public final void addLine(String id) {
    setValue(BLDKEY_LINEAR, id);
  }

  public final void addDirection1(boolean direction) {
    setValue(BLDKEY_DIRECTION1, direction);
  }

  public final void addDirection2(boolean direction) {
    setValue(BLDKEY_DIRECTION2, direction);
  }

  @Override
  public i_Body create(Editor edt, i_ErrorHandler eh) {
    i_BodyContainer bd = edt.bd();
    try {
      i_LinearBody line = (i_LinearBody) bd.get(getValueAsString(BLDKEY_LINEAR));
      i_PlainBody plane = (i_PlainBody) bd.get(getValueAsString(BLDKEY_PLAIN));
      if (!line.exists())
        throw new ExLostBody(line.alias());
      if (!plane.exists())
        throw new ExLostBody(plane.alias());
      Plane3d p = plane.plane();
      Line3d ln = line.line();
      Vect3d center = ln.intersectionWithPlane(p);

      // выбираем один из четырёх углов
      Vect3d pt1 = getValueAsBoolean(BLDKEY_DIRECTION1) ?
              Vect3d.sum(center, ln.l()) :
              Vect3d.sub(center, ln.l());

      Vect3d pt2;
      try {
        Line3d proj = ln.projectOnPlane(p);

        pt2 = getValueAsBoolean(BLDKEY_DIRECTION2) ?
                Vect3d.sum(center, proj.l()) :
                Vect3d.sub(center, proj.l());
      } catch( ExOrthogonalException ex ){
        //!! TODO: угол прямой, надо повернуть его лицом к зрителю
        pt2 = p.pnt().equals(p.projectionOfPoint(pt1)) ?
                p.pnt() : p.pnt2();
      }

      Angle3d angle = new Angle3d(pt1, center, pt2, true);
      AngleBody result = new AngleBody(_id, getValueAsString(BLDKEY_NAME), angle);
      _exists = true;
      return result;
    } catch (ExNoBody | ExGeom ex) {
      if (_exists) {
        eh.showMessage("Угол не построен: " + ex.getMessage(), error.Error.WARNING);
        _exists = false;
      }
      return new AngleBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINEAR, getValueAsString(BLDKEY_LINEAR));
    result.add(BLDKEY_PLAIN, getValueAsString(BLDKEY_PLAIN));
    result.add(BLDKEY_DIRECTION1, getValueAsBoolean(BLDKEY_DIRECTION1));
    result.add(BLDKEY_DIRECTION2, getValueAsBoolean(BLDKEY_DIRECTION2));
    return result;
  }

  @Override
  public String alias() {
    return ALIAS;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      return String.format("<html><strong>Угол</strong><br> между %s и %s",
              ctrl.getBodyTitle(getValueAsString(BLDKEY_PLAIN)),
              ctrl.getBodyTitle(getValueAsString(BLDKEY_LINEAR)));
    } catch (ExNoBody ex) {
      return "<html><strong>Угол между прямыми</strong>";
    }
  }
}
