package builders;

import bodies.LineBody;
import bodies.PlaneBody;
import static builders.BodyBuilder.BLDKEY_ALPHA;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExLostBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import editor.i_AnchorContainer;
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

/**
 *
 * @author Elena
 */
public class PlaneByLineOrthogonalDiskBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByLineOrthDisk";
  private boolean f = false;//show if line is orthogonal to plane (then we need angle of rotation)

  public PlaneByLineOrthogonalDiskBuilder() {
  }

  public PlaneByLineOrthogonalDiskBuilder(String id, String name) {
    super(id, name);
  }


  public PlaneByLineOrthogonalDiskBuilder(String id, HashMap<String, BuilderParam>params) {
    super(id, params);
  }

  public PlaneByLineOrthogonalDiskBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_CIRCLE, param.get(BLDKEY_CIRCLE).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }
  public PlaneByLineOrthogonalDiskBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY);
    addParam(BLDKEY_CIRCLE, "Ортогональный круг", BuilderParamType.ANCHOR);
    addParam(BLDKEY_ALPHA, BuilderParam.ROT_ANGLE_ALIAS, BuilderParamType.ANGLE_ROTATION);
  }

  @Override
  public String alias() { return ALIAS; }

  @Override
  public i_Body create (Editor edt, i_ErrorHandler eh){
    i_BodyContainer bodies = edt.bd();
    i_AnchorContainer anchors = edt.anchors();
    try {
      LineBody line = (LineBody)bodies.get(getValueAsString(BLDKEY_LINE));
      i_Anchor circle = anchors.get(getValueAsString(BLDKEY_CIRCLE));
      if (!line.exists())
          throw new ExLostBody("прямая");
      if (Checker.isCollinear(circle.getDisk().plane().n(), line.l())) {
        f = true;
        double alpha = getValueAsDouble(BLDKEY_ALPHA);
        Plane3d plane = Plane3d.planeByPointOrthPlane(line.pnt(),circle.getDisk().plane(), alpha);
        i_Body result= new PlaneBody(_id, title(), plane);
        _exists = true;
        return result;
      }
      Plane3d plane = null;
    if (Checker.isLineParallelPlane(line.line(), circle.getDisk().plane())) {
      plane = Plane3d.planeByLineAndOrthPlane(line.line(), circle.getDisk().plane());
    }
    else {
      Vect3d normal = Vect3d.vector_mul(line.l(), circle.getDisk().plane().n());
      plane = new Plane3d(normal, line.pnt());
    }
      PlaneBody result = new PlaneBody(_id, title(), plane);
      _exists = true;
      return result;
    } catch(ExNoBody | ExNoAnchor | ExDegeneration ex ){
      if (_exists)
        eh.showMessage("Плоскость не построена: " + ex.getMessage(), error.Error.WARNING);
      _exists = false;
      return new PlaneBody(_id, title());
    }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_CIRCLE, getValueAsString(BLDKEY_CIRCLE));
    result.add(BLDKEY_ALPHA, getValueAsDouble(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      if (!f) {
        return String.format("<html><strong>Плоскость,</strong><br> проходящая через прямую %s"
                + " и перпендикулярная плоскости окружности %s",
                    ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
                    ctrl.getAnchorTitle(getValueAsString(BLDKEY_CIRCLE)));
      } else {
        return String.format("<html><strong>Плоскость,</strong><br> проходящая через прямую %s"
                + " и перпендикулярная плоскости окружности %s. Угол поворота %s\u00B0",
                    ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
                    ctrl.getAnchorTitle(getValueAsString(BLDKEY_CIRCLE)),
                    Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ALPHA)), precision));
      }
    } catch ( ExNoBody | ExNoAnchor ex) {
      return "<html><strong>Прямая или окружность</strong>";
    }
  }
}
