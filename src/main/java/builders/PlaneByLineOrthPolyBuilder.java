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
public class PlaneByLineOrthPolyBuilder extends BodyBuilder {
  static public final String ALIAS = "PlaneByLineOrthPoly";
  
  //show if line is orthogonal to plane (then we need angle of rotation)
  private boolean f = false;

  public PlaneByLineOrthPolyBuilder() {}

  public PlaneByLineOrthPolyBuilder(String id, String name) {
    super(id, name);
  }

  public PlaneByLineOrthPolyBuilder(String id, HashMap<String, BuilderParam>params) {
    super(id, params);
  }

  public PlaneByLineOrthPolyBuilder( String id, String name, JsonObject param ){
    super(id, name);
    setValue(BLDKEY_LINE, param.get(BLDKEY_LINE).asString());
    setValue(BLDKEY_POLYGON, param.get(BLDKEY_POLYGON).asString());
    setValue(BLDKEY_ALPHA, param.get(BLDKEY_ALPHA).asDouble());
  }
  
  public PlaneByLineOrthPolyBuilder(HashMap<String, BuilderParam> params) {
    super(params);
  }
  
  @Override
  public void initParams(){
    super.initParams();
    addParam(BLDKEY_LINE, "Прямая в плоскости", BuilderParamType.BODY);
    addParam(BLDKEY_POLYGON, "Ортогональный многоугольник", BuilderParamType.ANCHOR);
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
        i_Anchor poly = anchors.get(getValueAsString(BLDKEY_POLYGON));
        if (!line.exists())
          throw new ExLostBody("прямая");
        if ( Checker.isCollinear(poly.getPoly().plane().n(), line.l())) {
          f = true;
          double alpha = (double)getValue(BLDKEY_ALPHA);
          Plane3d plane = Plane3d.planeByPointOrthPlane(line.pnt(),poly.getPoly().plane(), alpha);
          i_Body result= new PlaneBody(_id, title(), plane);
          _exists = true;
          return result;
        }
        Plane3d plane = null;
      if (Checker.isLineParallelPlane(line.line(), poly.getPoly().plane())) {
        plane = Plane3d.planeByLineAndOrthPlane(line.line(), poly.getPoly().plane());
      }
      else {
        Vect3d normal = Vect3d.vector_mul(line.l(), poly.getPoly().plane().n());
        plane = new Plane3d(normal, line.pnt());
      }
        PlaneBody result = new PlaneBody(_id, title(), plane);
        _exists = true;
        return result;
      } catch(ExNoBody | ExNoAnchor | ExDegeneration ex ){
        if (_exists) {
          eh.showMessage("Плоскость не построена: " + ex.getMessage(),error.Error.WARNING);
        }
        _exists = false;
        return new PlaneBody(_id, title());
      }
  }

  @Override
  public JsonObject getJSONParams() {
    JsonObject result = new JsonObject();
    result.add(BLDKEY_LINE, getValueAsString(BLDKEY_LINE));
    result.add(BLDKEY_POLYGON, getValueAsString(BLDKEY_POLYGON));
    result.add(BLDKEY_ALPHA, (double)getValue(BLDKEY_ALPHA));
    return result;
  }

  @Override
  public String description(EdtController ctrl, int precision) {
    try {
      if (!f) {
        return String.format("<html><strong>Плоскость, проходящая через прямую</strong> %s "
                        + "и перпендикулярная плоскости многоугольника %s",
                        ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
                        ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)));
      } else {
        return String.format("<html><strong>Плоскость, проходящая через прямую</strong> %s "
                + "и перпендикулярная плоскости многоугольника %s. Угол поворота %s\u00B0",
                ctrl.getBodyTitle(getValueAsString(BLDKEY_LINE)),
                ctrl.getAnchorTitle(getValueAsString(BLDKEY_POLYGON)),
                Util.valueOf(Angle3d.radians2Degree(getValueAsDouble(BLDKEY_ALPHA)), precision));
      }
    } catch ( ExNoBody | ExNoAnchor ex) {
        return "<html><strong>Прямая или многоугольник</strong>";
    }
  }
}
