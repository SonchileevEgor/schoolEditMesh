package builders;

import bodies.LineBody;
import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import geom.Angle3d;
import geom.Checker;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Vect3d;
import gui.EdtController;
import java.util.HashMap;
import minjson.JsonObject;
import util.Util;

public class PlaneByLineOrthogonalPlaneBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByLineOrthPlane";

  public PlaneByLineOrthogonalPlaneBuilder() {
  }

  public PlaneByLineOrthogonalPlaneBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByLineOrthogonalPlaneBuilder(String id, HashMap<String, BuilderParam>params) {
    super(id, params);
  }
  
  public PlaneByLineOrthogonalPlaneBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }

  public PlaneByLineOrthogonalPlaneBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_PLANE, param.get(BLDKEY_PLANE).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY, 61);
    addParam(BLDKEY_PLANE, "Ортогональная плоскость", BuilderParamType.BODY, 60);
    addParam(BLDKEY_ALPHA, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    try {
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      PlaneBody pl = (PlaneBody)bodies.get(getValueAsString(BLDKEY_PLANE));
      if (!pl.exists())
        throw new ExLostBody(BuilderParam.PLANE_ALIAS);
      if (!line.exists())
        throw new ExLostBody("прямая");
      // if line is orthogonal to plane
      if ( Checker.isCollinear(pl.plane().n(), line.l())) {
        f = true;
        double alpha = (double)getValue(BLDKEY_ALPHA);
        Plane3d plane = Plane3d.planeByPointOrthPlane(line.pnt(),pl.plane(), alpha);
        i_Body result= new PlaneBody(_id, title(), plane);
        _exists = true;
        return result;
      }
      Plane3d plane = null;
      if (Checker.isLineParallelPlane(line.line(), pl.plane())) {
          plane = Plane3d.planeByLineAndOrthPlane(line.line(), pl.plane());
      }
      else {
        Vect3d normal = Vect3d.vector_mul(line.l(), pl.normal());
        plane = new Plane3d(normal, line.pnt());
      }
      PlaneBody result = new PlaneBody(_id, title(), plane);
      _exists = true;
      return result;
    } catch(ExNoBody | ExDegeneration ex ){
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_PLANE, getValueAsString(BLDKEY_PLANE));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      if (!f) {
        return String.format("<html><strong>Плоскость, проходящая через прямую</strong> %s"
                + " и перпендикулярная плоскости %s",
                        ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
                        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)));
      } else {
        return String.format("<html><strong>Плоскость, проходящая через прямую</strong> %s"
                + " и перпендикулярная плоскости %s. Угол поворота %s \u00B0",
                        ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
                        ctrl.getBodyTitle(getValueAsString(BLDKEY_PLANE)),
                        Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ALPHA)), precision));
      }
    } catch ( ExNoBody ex ) {
      return "<html><strong>Прямая или плоскость</strong>";
    }
  }

  private boolean f = false;//show if line is orthogonal to plane (then we need angle of rotation)
}
