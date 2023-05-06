package bodies;

import static config.Config.GUI_EPS;
import geom.AbstractPolygon;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.i_Geom;
import geom.*;
import opengl.CalculatorGL;

/**
 * Meta body - 3d rib.
 *
 */
public class RibBody extends BodyAdapter implements i_LinearBody {
  public static final String BODY_KEY_A = "A";
  public static final String BODY_KEY_B = "B";
  public static final String BODY_KEY_RIB = "rib";
  private Rib3d _rib; // math object segment

  /**
   * Constructor of segment by math segment.
   * @param id
   * @param rib math object Rib3d
   */
  public RibBody (String id, Rib3d rib){
    super(id, "");
    _alias = "отрезок";
    _rib = rib;
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   */
  public RibBody(String id) {
    super(id, "");
    _alias = "отрезок";
    _exists = false;
  }

  // Getters for all vertexes of edge
  public Vect3d A(){ return _rib.a(); }
  public Vect3d B(){ return _rib.b(); }

  public Rib3d rib() {
    return _rib;
  }
  
  @Override
  public String getTitle() {
//    return "%" + getAnchorID(BODY_KEY_A) + "%%" + getAnchorID(BODY_KEY_B) + "%";
    return _exists ? "%" + getAnchorID(BODY_KEY_RIB) + "%" : "?";
  }

  @Override
  public Line3d line() throws ExDegeneration {
    return _rib.line();
  }

  @Override
  public BodyType type() {
    return BodyType.RIB;
  }

  @Override
  public void addRibs(Editor edt){
    String idA = getAnchorID(BODY_KEY_A);
    String idB = getAnchorID(BODY_KEY_B);

    // adding the ribs
    Rib3d rib1 = _rib.duplicate();
    edt.addAnchor(rib1, idA, idB, this, BODY_KEY_RIB);
  }

  @Override
  public void glDrawCarcass(Render ren){
    // отрисовка через якоря
  }

  @Override
  public void glDrawFacets(Render ren){
    // Rib hasn't facets
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    try {
      return _rib.intersectWithPlane(plane);
    } catch (ExGeom ex) {
      return super.getIntersectionWithPlane(plane);
    }
  }

  @Override
  public i_Geom getGeom() {
    return _rib;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new RibBody (id, (Rib3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    RibBody rib = (RibBody) result;
    edt.addAnchor(rib.A(), result, "A");
    edt.addAnchor(rib.B(), result, "B");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    double[] coord1 = new double[2];
    double[] coord2 = new double[2];
    // проецируем отрезок на экран
    CalculatorGL.getDisplayCoord(ren, _rib.a(), coord1);
    CalculatorGL.getDisplayCoord(ren, _rib.b(), coord2);
    Rib2d ribOnScreen = new Rib2d(new Vect2d(coord1[0], coord1[1]),
                                      new Vect2d(coord2[0], coord2[1]));
    // вычисляем расстояние до прямой на экране в пикселях
    if( ribOnScreen.distFromPoint(new Vect2d(x, ren.getHeight() - y))
            <= GUI_EPS.value()) {
      // Ищем точку на прямой, ближайшую к лучу взгляда.
      try {
        Plane3d pl = new Plane3d(_rib.a(), _rib.b(), Vect3d.sum(_rib.b(), ray.l()));
        
        //!! TODO: возвращать вершину, если точка за пределами ребра.
        return _rib.line().intersectionWithLine(ray.line().projectOnPlane(pl));
      } catch (ExDegeneration ex) {}
    }
    return null;
  }
};
