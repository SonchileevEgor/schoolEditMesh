package geom;


import static geom.Checker.eps;
import static geom.Checker.isCollinear;
import static geom.Checker.isOrthogonal;
import static geom.Sphere3d.areTwoSpheresEqual;
import static geom.Vect3d.mul;
import static geom.Vect3d.sum;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Math object cylinder
 * @author rita
 */
public class Cylinder3d implements i_Geom, i_OrientableGeom {
  private Vect3d _c0, _c1;// centers of cylinder bases
  private double _r;// radius of base

  /**
   * Constructor of math cylinder
   * @param c1 center of cylinder top base
   * @param c2 center of cylinder bottom base
   * @param r radius of cylinder bottom base
   * @throws geom.ExDegeneration
   */
  public Cylinder3d(Vect3d c1, Vect3d c2, double r)
    throws ExDegeneration{
    if (c1.equals(c2)) {
      throw new ExDegeneration("Центры оснований цилиндра совпадают");
    }
    _c0 = c1; _c1 = c2; _r = r;
  }

  /**
   * Construct cylinder by points.
   * @param points
   *  1st point - center of base, 2nd point - center of another base, 3rd point -
   * @throws geom.ExDegeneration
   */
  public Cylinder3d(ArrayList<Vect3d> points) throws ExDegeneration {
    if (points.get(0).equals(points.get(1))) {
      throw new ExDegeneration("центры оснований цилиндра совпадают");
    }
    Vect3d p_c = points.get(2).sub(points.get(1));
    double r = p_c.norm();
    _c0 = points.get(0);
    _c1 = points.get(1);
    _r = r;
  }

  /**
   * @return center of 1st cylinder base
   */
  public Vect3d c0(){ return _c0.duplicate(); }

  /**
   * @return center of 2nd cylinder base
   */
  public Vect3d c1(){ return _c1.duplicate(); }

  /**
   * @return radius of cylinder base
   */
  public double r(){ return _r; }

  /**
   * @return height of cylinder, vector c0c1
   */
  public Vect3d h(){ return Vect3d.sub(_c1, _c0); }

  /**
   * @return 1st base circle of cylinder
   */
  public Circle3d circ1(){ return new Circle3d(r(), h(), c0()); }

  /**
   * @return 2nd base cyrcle of cylinder
   */
  public Circle3d circ2(){ return new Circle3d(r(), h(), c1()); }

  /**
   * @return 1st base plane
   */
  public Plane3d basePlane0(){ return new Plane3d(h(), c0()); }

  /**
   * @return 2nd base plane
   */
  public Plane3d basePlane1(){ return new Plane3d(h(), c1()); }

  /**
   * Длина высоты цилиндра.
   * @return
   * @author alexeev
   */
  public double heightLength() {
    return h().norm();
  }

  /**
   * Площадь боковой поверхности цилиндра.
   * @return
   * @author alexeev
   */
  public double sideSurfaceArea() {
    return 2 * Math.PI * _r * heightLength();
  }

  /**
   * Площадь полной поверхности цилиндра.
   * @return
   * @author alexeev
   */
  public double surfaceArea() {
    return 2 * Math.PI * _r * (heightLength() + _r);
  }

  /**
   * Объём цилиндра.
   * @return
   * @author alexeev
   */
  public double volume() {
    return Math.PI * _r * _r * heightLength();
  }

  /**
   * @return cylinder outsphere
   */
  public Sphere3d outSphere(){
    double outRad = sqrt(pow(h().norm(), 2) + 4 * pow(r(), 2))/2;
    Vect3d outCen = mul(sum(c0(), c1()), 0.5);
    return new Sphere3d(outCen, outRad);
  }

  /**
   * @return cylinder insphere
   * @throws ExDegeneration
   */
  public Sphere3d inSphere() throws ExDegeneration{
    if(abs(h().norm() - 2 * r()) <= eps()){
      Vect3d inCen = mul(sum(c0(), c1()), 0.5);
      return new Sphere3d(inCen, r());
    } else
      throw new ExDegeneration("сфера не может быть вписана");
  }

  /**
   * Определение общей окружности у цилиндра и вписанной сферы
     * @param sphere вписанная сфера
     * @return circle общая окружность
     * @throws geom.ExGeom
   */

  public Circle3d circleBySphereInCylinder (Sphere3d sphere) throws ExGeom{
    Sphere3d inSphere = inSphere();
    if(areTwoSpheresEqual(inSphere, sphere)){
      return new Circle3d(r(),h(),sphere.center());
    }
    else
      throw new ExGeom("сфера не вписана в цилиндр");
  }

  /**
   * Find points of intersection line with cylinder.
   * @param line section line
   * @return list of points may be empty
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExDegeneration {
    /* Прямая проецируется в плоскость основания цилиндра,
    ищутся точки пересечения полученной прямой с окружностью основания,
    эти точки проецируются вдоль вектора на прямую.*/
    ArrayList<Vect3d> result = new ArrayList<Vect3d>();
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();// подозрительные точки
    // Если прямая не параллельна оси цилиндра ищем пересечение с боковой поверхностью
    if(!isCollinear(line.l(), h())){
      ArrayList<Vect3d> basePoints = circ1().intersectWithLine(line.projectOnPlane(basePlane0()));
      for(int i = 0; i < basePoints.size(); i++)
        points.add(line.projectOfPointAlongVect(basePoints.get(i), h()));
    }
    // Если прямая не параллельна основаниям, ищем их точки пересечения
    if(!isOrthogonal(line.l(), h())){
      points.add(line.intersectionWithPlane(basePlane0()));
      points.add(line.intersectionWithPlane(basePlane1()));
    }
    // Проверяем принадлежность цилиндру подозрительных точек
    for(int i = 0; i < points.size(); i++)
      if(this.contains(points.get(i)) && result.contains(points.get(i)))
        result.add(points.get(i));
    return result;
  }

  /**
   * Check point belong cylinder surface.
   * @param pnt checked point
   * @return true if point belong cylinder surface, false otherwise
   */
  public boolean contains(Vect3d pnt) {
    try {
      NewSystemOfCoor sys = new NewSystemOfCoor(this);
      double x = sys.newCoor(pnt).x();
      double y = sys.newCoor(pnt).y();
      double z = sys.newCoor(pnt).z();
      return  (z > eps() && z < h().norm() - eps() && abs(x * x + y * y - r() * r()) <= eps()) ||
              ((abs(z) <= eps() || abs(z - h().norm()) <= eps()) && x * x + y * y <= r() * r() + eps());
    } catch( ExZeroDivision ex ){
      return false;
    }
  }

  public CylinderSection3d sectionByPlane(Plane3d plane) throws ExZeroDivision, ExGeom{
    CylinderSection3d cylSec = new CylinderSection3d(this, plane);
    if ("empty".equals(cylSec.sectionType()))
      throw new ExGeom("плоскость не пересекает цилиндр");
    return cylSec;
  }

  public ArrayList<Polygon3d> faces() {
      ArrayList<Polygon3d> faces = new ArrayList<>();
      ArrayList<Vect3d> bottom_base = circ1().getAsAbstractPolygon();
      ArrayList<Vect3d> top_base = circ2().getAsAbstractPolygon();
      Collections.reverse(top_base);
      Vect3d p1 = new Vect3d(bottom_base.get(0));
      Vect3d p2 = new Vect3d(bottom_base.get(1));
      Vect3d p3 = new Vect3d(bottom_base.get(bottom_base.size()-1));
      Vect3d p4 = new Vect3d(top_base.get(0));
      Orientation bypass = Orientation.getBodyOrientation(p1, p2, p3, p4);
      if (bypass == Orientation.LEFT) {
          Collections.reverse(top_base);
          Collections.reverse(bottom_base);
      }
      try {
          faces.add(new Polygon3d(new ArrayList<>(new ArrayList<>(top_base))));
          faces.add(new Polygon3d(new ArrayList<>(new ArrayList<>(bottom_base))));
          Collections.reverse(top_base);
          int last = bottom_base.size();
          for (int i=0; i<last; i++) {
              faces.add(new Polygon3d(bottom_base.get(i%last), top_base.get(i%last), bottom_base.get((i+1)%last)));
              faces.add(new Polygon3d(top_base.get(i%last), top_base.get((i+1)%last), bottom_base.get((i+1)%last)));
          }
      }
      catch (ExGeom ex) { }
      return faces;
  }

  public Prism3d getAbstractPrism() {
    ArrayList<Vect3d> bottom_base = circ1().getAsAbstractPolygon();
    ArrayList<Vect3d> top_base = circ2().getAsAbstractPolygon();
    Collections.reverse(top_base);
    try {
      Prism3d prism = new Prism3d(top_base.get(0), new Polygon3d(bottom_base));
      return prism;
    } catch (ExGeom ex) { }
    return null;
  }

  /**
   * В качестве основного набора точек - центры оснований,
   * точка на окружности-основании.
   * @return
   */
  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList();
    result.add(_c0);
    result.add(_c1);
    Circle3d circ = circ2();
    result.add(circ.getPointOnBoundary());
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      Vect3d p_c = points.get(2).sub(points.get(1));
      double r = p_c.norm();
      return new Cylinder3d(points.get(0), points.get(1), r);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
    }
    return null;
  }

  public static Cylinder3d constrByPoints (ArrayList<Vect3d> points) {
    try {
     return new Cylinder3d(points);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
    }
    return null;
  }

  /**
   * Find points of intersection ray with side surface of cylinder
   * @param ray section line
   * @return list of points may be empty
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  public ArrayList<Vect3d> intersectSideSurfaceWithRay (Vect3d point, Ray3d ray) throws ExDegeneration, ExZeroDivision {
    ArrayList<Vect3d> result = new ArrayList<Vect3d>();
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();

    //если луч не параллелен оси, ищем точки пересечения с боковой поверхностью
    if (!isCollinear(ray.line().l(), h())){
      ArrayList<Vect3d> basePoints = circ1().intersectWithLine(ray.line().projectOnPlane(basePlane0()));
      for (int i = 0; i < basePoints.size(); i++) {
        Vect3d p = ray.line().projectOfPointAlongVect(basePoints.get(i), h());
        if(this.contains(p) && points.contains(p))
          points.add(p);
      }
    }
    if (points.size() == 2)
      return points;

    //если луч не параллелен основаниям, ищем точки пересечения с основаниями
    if (!isOrthogonal(ray.line().l(), h())){
      Vect3d p0 = ray.line().intersectionWithPlane(basePlane0());
      Vect3d p1 = ray.line().intersectionWithPlane(basePlane1());
      if(this.contains(p0) && !points.contains(p0))
        points.add(p0);
      if(this.contains(p1) && !points.contains(p1))
        points.add(p1);
    }

    //определяем, какую точку нужно выбрать, если луч попадает и на боковую поверхность, и на основание
    if (points.size() > 0) {
      Vect3d p = points.get(0);
      if ((!this.basePlane0().containsPoint(p))&&(!this.basePlane1().containsPoint(p))){
        if ((Vect3d.dist(p, point)) < (Vect3d.dist(points.get(1), point)))
          result.add(p);
      }
    }
    return result;
  }

  public ArrayList<Vect3d> intersect (Ray3d ray){
    ArrayList<Vect3d> result = new ArrayList<>();
    try {
      ArrayList<Vect3d> points = intersect(ray.line());
      for (Vect3d point : points) {
        if (ray.containsPoint(point)) {
          result.add(point);
        }
      }
    } catch (ExDegeneration ex){}
    return result;
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public Vect3d getUpVect() {
    return h().getNormalized();
  }

  @Override
  public GeomType type() {
    return GeomType.CYLINDER3D;
  }
}
