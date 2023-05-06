package bodies;


import static config.Config.GUI_EPS;
import geom.AbstractPolygon;
import opengl.Drawer;
import opengl.Render;
import editor.state.DisplayParam;
import editor.Editor;
import editor.ExNoAnchor;
import editor.i_Anchor;
import editor.i_Body;
import geom.i_Geom;
import geom.*;

import java.util.ArrayList;
import geom.Ray3d;
import geom.Vect3d;
import opengl.CalculatorGL;

/**
 *
 * @author Igor
 */
public class RayBody extends BodyAdapter implements i_LinearBody {
  private Ray3d _ray; //math object ray
  public static final String KEY_A = "A";
  public static final String KEY_B = "B";
  public static final String KEY_RIB = "rib";

  public RayBody (String id, String title, Ray3d ray){
    super(id, title);
    _ray = ray;
    _exists = true;
    _alias = "луч";
  }

  public RayBody(String id, String title) {
    super(id, title);
    _exists = false;
    _alias = "луч";
  }

  public Ray3d ray(){return _ray;}
  public Vect3d pnt(){return _ray.pnt();}
  public Vect3d l(){return _ray.l();}
  public Vect3d pnt2(){return _ray.pnt2();}

  @Override
  public Line3d line() throws ExDegeneration {
    return _ray.line();
  }

  @Override
  public BodyType type() {
    return BodyType.RAY;
  }

  @Override
  public void glDrawCarcass(Render ren){
    if (!_exists || !_state.isVisible()) return;
    Drawer.setObjectCarcassColor(ren, _state);
    if (_state.isChosen()) {
      Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS) * 1.5);
    } else {
      Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
    }
    Drawer.drawRayByPoints(ren, _ray.pnt(), _ray.pnt2());
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    //TODO: Return intersection with plane and return as AbstractPolygon
    return super.getIntersectionWithPlane(plane);
  }

  @Override
  public void addRibs(Editor edt){
      String idA = getAnchorID(KEY_A);
      String idB = getAnchorID(KEY_B);

      try {
      i_Anchor a = edt.anchors().get(idA);
      i_Anchor b = edt.anchors().get(idB);

      // adding the ribs
      Rib3d rib = new Rib3d(a.getPoint(), b.getPoint());
      edt.addAnchor(rib, idA, idB, this, KEY_RIB);
    } catch (ExNoAnchor | ExDegeneration ex) {}
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    ArrayList<Polygon3d> faces = new ArrayList<>();
    return faces;
  }

  @Override
  public i_Geom getGeom() {
    return _ray;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new RayBody(id, title, (Ray3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    RayBody ray = (RayBody) result;
    edt.addAnchor(ray.pnt(), result, KEY_A);
    edt.addAnchor(ray.pnt2(), result, KEY_B);
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    double[] coord1 = new double[2];
    double[] coord2 = new double[2];
    // проецируем отрезок на экран
    CalculatorGL.getDisplayCoord(ren, _ray.pnt(), coord1);
    CalculatorGL.getDisplayCoord(ren, _ray.pnt2(), coord2);
    Rib2d ribOnScreen = new Rib2d(new Vect2d(coord1[0], coord1[1]),
                                      new Vect2d(coord2[0], coord2[1]));
    // вычисляем расстояние до прямой на экране в пикселях
    //!! Считать расстояние до луга, а не до прямой.
    if( ribOnScreen.line().distFromPoint(new Vect2d(x, ren.getHeight() - y))
            <= GUI_EPS.value()) {
      // Ищем точку на прямой, ближайшую к лучу взгляда.
      try {
        Plane3d pl = new Plane3d(_ray.pnt(), _ray.pnt2(), Vect3d.sum(_ray.pnt2(), ray.l()));
        return _ray.line().intersectionWithLine(ray.line().projectOnPlane(pl));
      } catch (ExDegeneration ex) {}
    }
    return null;
  }
  
}