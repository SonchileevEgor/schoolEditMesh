package geom;

import static geom.Vect3d.mul;
import static geom.Vect3d.rNormalizedVector;
import static geom.Vect3d.sub;
import static geom.Vect3d.sum;
import static geom.Vect3d.vector_mul;
import java.util.ArrayList;

/**
 * Arc
 * @author rita
 */
public class Arc3d implements i_Geom, i_OrientableGeom {
  private Vect3d _center;
  private Vect3d _normal;
  private Vect3d _vert1;
  private double _value;

  /**
   * Constructor of arc by center, normal, vertex, angle
   * @param center center of circle which contains the arc
   * @param normal circle normal
   * @param vert1 one of arc vertices
   * @param angle angle
   */
  public Arc3d(Vect3d center, Vect3d normal, Vect3d vert1, double angle) {
    _center = center;
    _normal = normal.getNormalized();
    _vert1 = vert1;
    _value = angle;
  }

  /**
   * Constructor of arc by angle and radius
   * @param angle angle object
   * @param r arc radius
   */
  public Arc3d(Angle3d angle, double r) {
    _center = angle.vertex();
    _normal = angle.normal();
    Vect3d side1 = rNormalizedVector(angle.side1(), r);
    _vert1 = sum(_center, side1);
    _value = angle.value();
  }

  /**
   * Constructor of arc by 3 points
   * @param v1 first end of arc
   * @param mV middle point of arc
   * @param v2 second end of arc
   * @throws geom.ExDegeneration
   */
  public Arc3d(Vect3d v1, Vect3d mV, Vect3d v2) throws ExDegeneration {
    /*
     * Дуга строится по трем точкам: 2 края и точка на дуге.
     * Центр находится как центр описанной оружности этих трех точек.
     * Нормаль - векторное произведение векторов, направленных из mV в v2 и в v1
     * Чтобы найти величину дуги, находим велиину центрального угла, опирающегося на эту дугу
     */
    if(Checker.threePointsOnTheLine(v1, v2, mV))
      throw new ExDegeneration("три точки на одной прямой");
    _vert1 = v1;
    _center = new Circle3d(v1, mV, v2).center();
    _normal = vector_mul(sub(v2, mV), sub(v1, mV)).getNormalized();
    Angle3d centralAng = new Angle3d(v1, _center, v2, sum(_center, _normal));
    _value = centralAng.value();
  }

  /**
   * Constructor of arc by points
   * @param points
   * @throws ExDegeneration
   */
   public Arc3d(ArrayList<Vect3d> points) throws ExDegeneration {
    this(points.get(0), points.get(1), points.get(2));
  }

  /**
   * Constructor of arc by circle and two points on circle
   * @param p1 first point on circle
   * @param p2 second point on circle
   * @param circ circle
   * @throws ExGeom
   */
  public Arc3d(Vect3d p1, Vect3d p2, Circle3d circ) throws ExGeom {
    // Проверка того, что точки принадлежат окружности. Если это не так, кидается исключение
    if (Math.abs(Vect3d.dist(circ.center(), p1) - circ.radiusLength()) > Checker.eps() ||
        Math.abs(Vect3d.dist(circ.center(), p2) - circ.radiusLength()) > Checker.eps())
      throw new ExGeom("точки не лежат на окружности");
    _value = circ.getPolarAngleByPointAndStartPointOnCircle(p2, p1);
    _normal = circ.normal().getNormalized();
    _vert1 = p1;
    _center = circ.center();
  }

  /**
   * Constructor of half circle by 2 points of diameter
   * @param a 1st point of diameter
   * @param b 2nd point of diameter
   * @param angle angle between default plane and plane which contains half circle
   * @return new arc
   * @throws ExDegeneration
   */
  public static Arc3d halfCircleBy2PntsAngle(Vect3d a, Vect3d b, double angle) throws ExDegeneration{
    Plane3d plane = Plane3d.planeByTwoPoints(a, b, angle);
    Vect3d diam = sub(b, a).rotate(Math.PI/2, plane.n());
    Vect3d c = mul(sum(sum(a, b), diam), 0.5);
    return new Arc3d(a, c, b);
  }

  /**
   * Point of arc by parameter.
   *
   * @param p parameter from [0, angle value of arc]
   * @return
   * @throws geom.ExDegeneration
   */
  public Vect3d getPoint(double p) throws ExDegeneration {
    Vect3d oldO = _center;
    Vect3d oldX = _vert1;
    Vect3d oldZ = sum(_center, _normal);
    Vect3d oldY = sum(_center, vector_mul(_normal, sub(_vert1, _center)));
    NewSystemOfCoor newSys = new NewSystemOfCoor(oldO, oldX, oldY, oldZ);
    Vect3d newCoorOfPoint = new Vect3d(r() * Math.cos(p), r() * Math.sin(p), 0);
    return newSys.oldCoor(newCoorOfPoint);
  }

  public double r() {
    return sub(_vert1, _center).norm();
  }

  public Vect3d normal() {
    return _normal;
  }

  public double value() {
    return _value;
  }

  public double length() {
    return _value * r();
  }

  /**
   * Central angle
   * @return central angle which corespondent to this arc
   * @throws ExDegeneration
   */
  public Angle3d centralAng() throws ExDegeneration {
    Vect3d eye = sum(_center, _normal);
    return new Angle3d(vert1(), _center, vert2(), eye);
  }

  /**
   * Find distance from given point to arc.
   * Presuming point belongs to arc plane.
   *
   * @param pnt point in arc plane
   * @return distance
   */
  public double dist(Vect3d pnt) {
    try {
      Ray3d ray = Ray3d.ray3dByTwoPoints(_center, pnt);
      ArrayList<Vect3d> rayXArc = this.intersect(ray);
      if (rayXArc.size() > 0) {
        return Vect3d.dist(rayXArc.get(0), pnt);
      }
    } catch (ExDegeneration ex) {
      // pnt coincided with arc center
    }

    return Math.min(Vect3d.dist(pnt, vert1()), Vect3d.dist(pnt, vert2()));
  }

  public Vect3d center() {
    return _center;
  }

  public Vect3d vert1() {
    return _vert1;
  }

  public Circle3d circle() {
    return new Circle3d(r(), _normal, _center);
  }

  public Vect3d vert2() {
    return getPointByPolar(_value);
  }

  public Vect3d midVert() {
    Vect3d radius1 = sub(_vert1, _center);
    Vect3d radius2 = radius1.rotate(_value / 2, _normal);
    return sum(_center, radius2);
  }

  public Plane3d plane() {
    return new Plane3d(_normal, _center);
  }

  /**
   * Point by polar angle
   * @param angle angle between radius vector to 1st point and radius vector to sought-for point
   * if angle more than arc value then return end of arc (2nd point)
   * @return point on arc
   */
  public Vect3d getPointByPolar(double angle) {
    Vect3d radius1 = sub(_vert1, _center);
    if (0 <= angle && angle <= _value){
      Vect3d radius2 = radius1.rotate(angle, _normal);
      return sum(_center, radius2);
    } else {
      return sum(_center, radius1);
    }
  }

  /**
   * Point on arc by ratio
   * @param ratio positive number less than 1
   * @return
   */
  public Vect3d getPointByRatio(double ratio) {
    double angle = _value * ratio;
    return getPointByPolar(angle);
  }

  /**
   * Reverse function to @link getPointByPolar
   * @param v
   * @return
   * @throws ExDegeneration
   */
  public double getPolarAngleByPoint(Vect3d v) throws ExDegeneration {
    double angle = circle().getPolarAngleByPointAndStartPointOnCircle(v, _vert1);
    if (angle <= _value) {
      return angle;
    } else if (_value < angle && angle <= _value / 2 + Math.PI) {
      return _value;
    } else {
      return 0;
    }
  }

  /**
   * Reverse function to @link getPointByRatio
   * @param v
   * @return
   * @throws ExDegeneration
   */
  public double getRatioByPoint(Vect3d v) throws ExDegeneration {
    double angle = getPolarAngleByPoint(v);
    if (_value > Checker.eps())
      return angle / _value;
    else
      return 0;
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    /* Разбираем дугу на 3 точки: края и точка на середине дуги */
    ArrayList<Vect3d> points = new ArrayList<>();
    points.add(vert1());
    points.add(midVert());
    points.add(vert2());
    return points;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      return new Arc3d(points);
    } catch (ExDegeneration ex) {
      return null;
    }
  }

  /**
   * Выбрать из списка точек только те, которые лежат на внутренности дуги.
   * @param circleIntersection
   * @return
   */
  ArrayList<Vect3d> checkIntersectPointOnArc(ArrayList<Vect3d> circleIntersection){
    ArrayList<Vect3d> result = new ArrayList<>();
    for (Vect3d pnt : circleIntersection){
      // Исключаем вершины дуги, рассматриваем только внутренность.
      if( Vect3d.equals(_vert1, pnt) || Vect3d.equals(vert2(), pnt))
        continue;

      try {
        double angle = circle().getPolarAngleByPointAndStartPointOnCircle(pnt, _vert1);
        if (0 <= angle && angle <= _value)
          result.add(pnt);
      } catch (ExDegeneration ex) {}
    }
    return result;
  }
  
  public ArrayList<Vect3d> intersect (Ray3d ray){
    ArrayList<Vect3d> circleIntersection = circle().intersect(ray);
    ArrayList<Vect3d> result = new ArrayList<>();
    for (Vect3d pnt : circleIntersection)
      try {
        double angle = circle().getPolarAngleByPointAndStartPointOnCircle(pnt, _vert1);
        if (0 <= angle && angle <= _value)
          result.add(pnt);
      } catch (ExDegeneration ex) {}
    return result;
  }

  public ArrayList<Vect3d> intersect(Rib3d rib) throws ExDegeneration {
    return checkIntersectPointOnArc(circle().intersect(rib));
  }

  public ArrayList<Vect3d> intersect(Line3d line) throws ExDegeneration {
    return checkIntersectPointOnArc(circle().intersect(line));
  }

  public ArrayList<Vect3d> intersect(Polygon3d poly) throws ExDegeneration {
    return checkIntersectPointOnArc(circle().intersect(poly));
  }

  public ArrayList<Vect3d> intersect(Circle3d circ) throws ExDegeneration {
    return checkIntersectPointOnArc(circle().intersect(circ));
  }

   public ArrayList<Vect3d> intersect(Triang3d tr) throws ExDegeneration {
    return checkIntersectPointOnArc(circle().intersect(tr));
  }

  public ArrayList<Vect3d> intersect(Arc3d arc) throws ExDegeneration {
    return arc.checkIntersectPointOnArc(checkIntersectPointOnArc(circle().intersect(arc.circle())));
  }

  @Override
  public Vect3d getUpVect() {
    return _normal.duplicate();
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.ARC3D;
  }
}
