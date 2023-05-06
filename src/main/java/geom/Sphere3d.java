package geom;

import static geom.Checker.eps;
import static geom.Vect3d.rNormalizedVector;
import static geom.Vect3d.sub;
import java.util.ArrayList;

/**
 * @author alexeev
 */
public class Sphere3d implements i_Geom {
  private Vect3d _center;
  private double _radius;

  public Sphere3d(Vect3d center, double radius) {
    _center = center;
    _radius = radius;
  }

  /**
   * Sphere constructed by 4 points
   * @param p1 1st point
   * @param p2 2nd point
   * @param p3 3d point
   * @param p4 4th point
   * @throws ExDegeneration
   */
  public Sphere3d(Vect3d p1, Vect3d p2, Vect3d p3, Vect3d p4) throws ExDegeneration {
    if (Checker.isFourPointsInOnePlane(p1, p2, p3, p4))
      throw new ExDegeneration("Точки сферы лежат в одной плоскости");

    //normal vectors of perpendicular bisector planes
    Vect3d n1 = Vect3d.sub(p2, p1);
    n1.inc_mul(0.5);
    Vect3d n2 = Vect3d.sub(p3, p1);
    n2.inc_mul(0.5);
    Vect3d n3 = Vect3d.sub(p4, p1);
    n3.inc_mul(0.5);

    Vect3d mid1 = new Vect3d(p1);
    mid1.inc_add(n1);
    Vect3d mid2 = new Vect3d(p1);
    mid2.inc_add(n2);
    Vect3d mid3 = new Vect3d(p1);
    mid3.inc_add(n3);

    n1.inc_div(n1.norm());
    n2.inc_div(n2.norm());
    n3.inc_div(n3.norm());

    //free terms of bisector planes
    double free_a = n1.x() * mid1.x() + n1.y() * mid1.y() + n1.z() * mid1.z();
    double free_b = n2.x() * mid2.x() + n2.y() * mid2.y() + n2.z() * mid2.z();
    double free_c = n3.x() * mid3.x() + n3.y() * mid3.y() + n3.z() * mid3.z();

    Vect3d col1 = new Vect3d(n1.x(), n2.x(), n3.x());
    Vect3d col2 = new Vect3d(n1.y(), n2.y(), n3.y());
    Vect3d col3 = new Vect3d(n1.z(), n2.z(), n3.z());
    Vect3d free = new Vect3d(free_a, free_b, free_c);

    double det = Vect3d.tripleProd(col1,col2,col3);
    double det1 = Vect3d.tripleProd(free, col2, col3);
    double det2 = Vect3d.tripleProd(col1, free, col3);
    double det3 = Vect3d.tripleProd(col1, col2, free);

    _center = new Vect3d(det1/det, det2/det, det3/det);
    _radius = Vect3d.dist(_center, p1);
  }

  /**
   * Sphere constructed by points
   * @param points
   *   point list:
   *   1st point - center,
   *   2nd point - a point on sphere
   */
  public Sphere3d(ArrayList<Vect3d> points) {
    _radius = Vect3d.sub(points.get(1), points.get(0)).norm();
    _center = points.get(0);
  }

  /**
   * Volume of sphere.
   * @return inner volume of the sphere
   */
  public double volume() {
    return 4/3 * Math.PI * Math.pow(_radius, 3);
  }

  /**
   * Area of sphere's surface.
   * @return surface area of the sphere
   */
  public double area() {
    return 4 * Math.PI * Math.pow(_radius, 2);
  }

  /**
   * Center of sphere.
   * Returns copy of Vect3d object.
   * @return center of the sphere
   */
  public Vect3d center() {
    return _center.duplicate();
  }

  /**
   * Radius of sphere.
   * @return radius of the sphere
   */
  public double radius() {
    return _radius;
  }

  /**
   * Проверка на эквивалентность двух сфер
   * @param sph1 1-ая сфера
   * @param sph2 2-ая сфера
   * @return boolean
   */
  static public boolean areTwoSpheresEqual(Sphere3d sph1, Sphere3d sph2){
    return Vect3d.equals(sph1.center(), sph2.center()) &&
            Math.abs(sph1.radius() - sph2.radius()) <= Checker.eps();
  }

  /**
   * Ищет точку касания сферы и плоскости, если они касаются
   * @param plane
   * @return точку касания сферы с плоскостью
   * @throws ExGeom
   */
  public Vect3d tangentPointSpherePlane(Plane3d plane) throws ExGeom {
    if( Math.abs(plane.distFromPoint(_center) - _radius) <= eps() ) {
      return plane.projectionOfPoint(_center);
    } else {
      throw new ExGeom("сфера и плоскость не касаются");
    }
  }

  /**
   * Ищет точку касания сферы и окружности, если они касаются.
   * Случай, когда окружность касается сферы
   * только границей под углом не в 90 градусов, не рассматривается.
   * @param circ
   * @return точку касания сферы с окружностью
   * @throws ExGeom
   */

  public Vect3d tangentPointSphereCircle(Circle3d circ) throws ExGeom {
    Vect3d tangPoint = tangentPointSpherePlane(circ.plane());
    if (circ.containsPoint(tangPoint)){
      return tangPoint;
    } else {
      throw new ExGeom("сфера и окружность не касаются");
    }
  }

  /**
   * @param poly
   * @return точку касания сферы с многоугольником
   * @throws ExGeom
   * @throws ExDegeneration
   */
  public Vect3d tangentPointSpherePoly(Polygon3d poly) throws ExGeom, ExDegeneration {
    Vect3d tangPoint = tangentPointSpherePlane(poly.plane());
    if (Checker.isPointOnClosePolygon(poly, tangPoint)) {
      return tangPoint;
    } else {
      throw new ExGeom("сфера и многоугольник не касаются");
    }
  }

 /**
   * @param line
   * @return точку касания сферы с прямой
   * @throws ExGeom
   */
  public Vect3d tangentPointSphereLine(Line3d line) throws ExGeom {
    if(Math.abs(line.distFromPoint(_center) - _radius) <= eps()) {
      return line.projectOfPoint(_center);
    } else {
      throw new ExGeom("сфера и прямая не касаются");
    }
  }

 /**
   * @param rib
   * @return точку касания сферы с отрезком
   * @throws ExGeom
   */
  public Vect3d tangentPointSphereRib(Rib3d rib) throws ExGeom {
    Vect3d tangPoint = tangentPointSphereLine(rib.line());
    if (Checker.isPointOnSegment(rib, tangPoint)){
      return tangPoint;
    } else {
      throw new ExGeom("сфера и отрезок не касаются");
    }
  }

/**
   * @param ray
   * @return точку касания сферы с лучом
   * @throws ExGeom
   */
  public Vect3d tangentPointSphereRay(Ray3d ray) throws ExGeom {
  Vect3d tangPoint = tangentPointSphereLine(ray.line());
    if (ray.containsPoint(tangPoint)) {
      return tangPoint;
    } else {
      throw new ExGeom("сфера и луч не касаются");
    }
  }

  /**
   * @param point
   * @return касательную плоскость в точке
   * @throws ExGeom
   */
  public Plane3d tangentPlaneInPoint(Vect3d point) throws ExGeom {
    if (Checker.isPointOnSphere(point, this)) {
      Vect3d normal = sub(point, _center);
      Plane3d tangPlane = new Plane3d(normal, point);
      return tangPlane;
    } else {
      throw new ExGeom ("точка не принадлежит сфере");
    }
  }

  /**
   * Get circle which is boundary of visible part of sphere.
   * presuming point is out of sphere.
   *
   * @param point
   * @return
   */
  public Circle3d getVisibleCircle(Vect3d point) {
    Vect3d dir = Vect3d.sub(point, _center);
    double l = dir.norm();
    double oh = Math.pow(_radius, 2) / l;
    double rad = Math.sqrt(Math.pow(_radius, 2) - Math.pow(oh, 2));
    return new Circle3d(rad, dir.getNormalized(),
            Vect3d.sum(_center, Vect3d.rNormalizedVector(dir, oh)));
  }

  /**
   * Get projection of sphere onto plane.
   *
   * @param plane
   * @return
   */
  public Circle3d projectOnPlane(Plane3d plane) {
    return new Circle3d(_radius, plane.n(), plane.projectionOfPoint(_center));
  }

  /**
   * Find section of sphere by line
   * @param line section line
   * @return section points
   */
  public ArrayList<Vect3d> intersect(Line3d line) {
    ArrayList<Vect3d> result = new ArrayList<>();
    // находим проекцию h центра сферы на прямую.
    Vect3d h = line.projectOfPoint(_center);
    double oh = Vect3d.dist(_center, h);
    if (Checker.isEqual(_radius, oh)) {
      // если h = радиусу, то h - точка касания
      result.add(h);
    } else if (oh < _radius) {
      // если h < радиуса, то
      // находим половину длины хорды, проходящей через h.
      double dist = Math.sqrt(_radius * _radius - oh * oh);
      Vect3d v = Vect3d.rNormalizedVector(line.l(), dist);
      result.add(Vect3d.sum(h, v));
      result.add(Vect3d.sub(h, v));
    }

    return result;
  }

  public ArrayList<Vect3d> intersect(Ray3d ray) {
    ArrayList<Vect3d> result = new ArrayList<>();
    try {
      ArrayList<Vect3d> points = intersect(ray.line());
      for (Vect3d point : points) {
        if (ray.containsPoint(point)) {
          result.add(point);
        }
      }
    } catch (ExGeom ex) {}
    return result;
  }

  /**
   * Find section of sphere by plane
   * @param pl section plane
   * @return circle section
   * @throws ExGeom
   */
  public Circle3d sectionByPlane(Plane3d pl) throws ExGeom {
    double d = pl.distFromPoint(_center);
    if (d > _radius)
      throw new ExGeom("плоскость и сфера не пересекаются");
    Vect3d c1 = pl.projectionOfPoint(_center);
    double rad = Math.sqrt(Math.pow(_radius,2) - Math.pow(d,2));
    Vect3d normal = rNormalizedVector(pl.n(), rad);
    return new Circle3d(normal, c1);
  }

  /**
   * Find intersection of two spheres.
   * @param sph second sphere
   * @return intersection circle
   * @throws ExGeom
   */
  public Circle3d intersectionWithSphere(Sphere3d sph) throws ExGeom {
    // Find radical plane, and than, intersection of plane with sphere.
    double dist = Vect3d.dist(_center, sph.center());
    if (dist > _radius + sph.radius() || dist < Math.abs(_radius - sph.radius()))
      throw new ExGeom("сферы не пересекаются");
    Vect3d center = Vect3d.conv_hull(_center, sph.center(),
            (dist * dist + _radius * _radius - sph.radius() * sph.radius()) / (2 * dist * dist));
    Plane3d radicalPlane = new Plane3d(Vect3d.sub(sph.center(), _center), center);
    return sectionByPlane(radicalPlane);
  }

  /**
    * В качестве основного набора точек - центр, точка на поверхности сферы
    * точка выбирается как одна из точек пересечения сферы с линией,
    * которая проходит через центр и точку _center
    * @return
    */
  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList<>();
    result.add(_center);
    Vect3d pnt = Vect3d.sum(_center, Vect3d.UZ.mul(2));
    Line3d line = null;
    try {
      line = Line3d.getLineByTwoPoints (_center, pnt);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
    }

    //!! TODO: обращение к line
    Vect3d vect = Vect3d.sub(line.pnt2(), _center);
    vect = vect.mul(_radius/vect.norm());
    vect = Vect3d.sum (vect, _center);
    result.add(vect);

    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    return new Sphere3d (points);
  }

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.SPHERE3D;
  }
}
