package geom;

import java.util.ArrayList;
import java.util.Collections;
import static geom.Checker.*;
import static geom.Vect3d.*;
import java.util.Iterator;


/**
 * Math object circle
 * @author ?
 */
public class Circle3d implements AbstractPolygon, i_Geom, i_OrientableGeom {
  private static final int NUM_POINTS_AS_ABSTRACT_POLYGON = 100;

  /**
   * Normal vector with length equals to radius.
   */
  private Vect3d _n;

  /**
   * Center of the circle.
   */
  private Vect3d _c;

  /**
   * Construct circle by three points.
   * @param a
   * @param b
   * @param c
   * @throws ExDegeneration
   */
  public Circle3d(Vect3d a, Vect3d b, Vect3d c) throws ExDegeneration {
    if (Checker.threePointsOnTheLine(a, b, c)) {
      throw new ExDegeneration("точки окружности лежат на одной прямой");
    }
    Plane3d pl = new Plane3d(a, b, c);
    Line3d l1 = midLinesArePerpendicularToTheSegment(a, b, pl);
    Line3d l2 = midLinesArePerpendicularToTheSegment(a, c, pl);
    Vect3d center = l1.intersect(l2).get(0);
    Vect3d normal = pl.n();
    normal = rNormalizedVector(normal, dist(a, center));
    _c = center;
    _n = normal;
  }

  /**
   * Constructor of circle by normal vector and center.
   * Length of normal vector is circle radius!
   * @param normal normal vector
   * @param center
   */
  public Circle3d(Vect3d normal, Vect3d center){
    _n = normal;
    _c = center;
  }

  /**
   * Constructor of circle by radius, normal vector and center
   * @param radius
   * @param normal
   * @param center
   */
  public Circle3d(double radius, Vect3d normal, Vect3d center){
    _n = Vect3d.rNormalizedVector(normal, radius);
    _c = center;
  }

  /**
   * Construct of circle by three points.
   * @param points three points on circle
   * @throws ExDegeneration
   */
  public Circle3d(ArrayList<Vect3d> points) throws ExDegeneration {
    this(points.get(0), points.get(1), points.get(2));
  }

  /**
   * Constructor of circle by by 2 points, width and angle
   * @param a 1st vertex
   * @param b 2nd vertex
   * @param angle angle between default plane and plane which contains circle
   * @return circle (Circle3d)
   * @throws geom.ExGeom
   */
  public static Circle3d CircleBy2PntsAngle (Vect3d a, Vect3d b,  double angle)
      throws ExGeom {
    Plane3d plane = Plane3d.planeByTwoPoints(a, b, angle);
    Vect3d normal = plane.n();
    Vect3d center = Vect3d.sum(a, b).mul(0.5);
    double radius = Vect3d.dist(a, center);
    return new Circle3d(radius, normal, center);

  }

  /**
   * @return normal vector of the plane
   */
  public Vect3d normal() {
    return _n.duplicate();
  }

  /**
   * radius of the circle
   * @return vector
   */
  public Vect3d radius(){
    return rot_x(_n, Math.PI);
  }

  /**
   * radius of the circle
   * @return double
   */
  public double radiusLength(){
    return _n.norm();
  }

  /**
   * @return center of the circle
   */
  public Vect3d center(){
    return _c.duplicate();
  }

  /**
   * @return plane which contains this circle
   */
  public Plane3d plane(){ return new Plane3d(_n, _c); }

  /**
   * Area of the circle.
   * @return
   */
  public double area() {
    return Math.PI * Math.pow(radiusLength(), 2);
  }

  /**
   * Length of the circle.
   * @return
   */
  public double length() {
    return 2 * Math.PI * radiusLength();
  }

  /**
   * Find intersection circle with line
   * @param line section line
   * @return point if section is point, two points if section is segment
   * @throws ExDegeneration
   * @deprecated
   */
  public ArrayList<Vect3d> intersectWithLine (Line3d line) throws ExDegeneration{
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    Vect3d O1 = line.projectOfPoint(_c);
    double dist = line.distFromPoint(_c);
    if(Checker.isPointOnCircle(O1, this)){
      points.add(O1);
      return points;
    }
    if(dist < _n.norm()){
      double length = Math.sqrt(Math.pow(Vect3d.norm(_n),2) - Math.pow(Vect3d.dist(_c, O1),2));
      if(Checker.isPointOnCircle(Vect3d.sum(O1, Vect3d.rNormalizedVector(line.l(),length)), this))
      points.add(Vect3d.sum(O1, Vect3d.rNormalizedVector(line.l(),length)));
       if(Checker.isPointOnCircle(Vect3d.sub(O1, Vect3d.rNormalizedVector(line.l(),length)), this))
      points.add(Vect3d.sub(O1, Vect3d.rNormalizedVector(line.l(),length)));
    }
     return points;
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
    } catch (ExDegeneration ex) {}
    return result;
  }
  
  /**
   * Пересечение с лучом (трансверсальное).
   * Если луч и эллипс в одной плоскости, то пересечения нет.
   * @param ray
   * @return 
   */
  public Vect3d intersectWithRayTransversal(Ray3d ray) {
    // Проверяем точку пересечения плоскости эллипса с лучом
    // на принадлежность области эллипса.
    try {
      Vect3d result = ray.intersectionWithPlane(plane());
      return containsPoint(result) ? result : null;
    } catch( ExDegeneration ex ){
      return null;
    }
  }
  
  /**
   *
   * @param line
   * @return
   * @throws ExDegeneration
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExDegeneration {
    return this.intersectWithLine(line);
  }

  public ArrayList<Vect3d> intersect(Rib3d rib) throws ExDegeneration {
    return rib.intersect(this);
  }

  //!! TODO: что делает этот метод? Почему бросается исключение?
  public ArrayList<Vect3d> intersect (Polygon3d poly) throws ExDegeneration {
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    for (int i = 0; i < poly.ribs().size(); i++){
      Vect3d.addWithoutDuplicates(points, poly.ribs().get(i).intersect(this));
    }
    if(points.isEmpty())
      throw new ExDegeneration("Многоугольник и окружность не пересекаются");
    return points;
  }

  public ArrayList<Vect3d> intersect (Circle3d circ) throws ExDegeneration {
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    Vect3d centerLine = Vect3d.sub(circ.center(), this.center());
    double dist = centerLine.norm();

    // Если неравенство треугольника для адиусов и расстояния между центрами не выполняется,
    // то точек пересечения точно нет.
    if( !Checker.holdTriangleInequality(this.radiusLength(), circ.radiusLength(), dist) )
      return points;

    if( Plane3d.equals(this.plane(), circ.plane()) ){
      // Окружности лежат в одной плоскости.

      if( Checker.isEqual(dist, 0) ){
        // Окружности совпадают, считаем, что пересечение пусто.
        return points;
      }

      // Находим угол, на который нужно повернуть вектор, соединяющий центры окружностей,
      // чтобы получить точки пересечения.
      // Поворачиваем в две стороны, нормируем на радиус первой окружности.
      double angle = Math.acos((dist * dist + Math.pow(this.radiusLength(), 2) - Math.pow(circ.radiusLength(), 2)) / (2 * dist * this.radiusLength()));
      points.add(Vect3d.sum(this.center(), Vect3d.rNormalizedVector(centerLine.rotate(angle, this.normal()), this.radiusLength())));
      Vect3d.addWithoutDuplicates(points, Vect3d.sum(this.center(), Vect3d.rNormalizedVector(centerLine.rotate(-angle, this.normal()), this.radiusLength())));
      return points;
    }

    if( Checker.isPlaneParallelPlane(this.plane(), circ.plane()) )
      return points;

    points = this.intersect(this.plane().intersectionWithPlane(circ.plane()));
    Iterator<Vect3d> iter = points.iterator();
    while( iter.hasNext() ){
      Vect3d s = iter.next();
      if(!Checker.isPointOnCircle(s, circ))
        iter.remove();
    }
    return points;
  }

  /**
   * Create copy of Circle3d object.
   * @return
   */
  public Circle3d duplicate() {
    return new Circle3d(_n, _c);
  }

  /**
   * Intersection of the <strong>disk</strong> (not circle) with line.
   * @param line
   * @return
   */
  public Vect3d intersectInteriorWithLine(Line3d line) {
    try {
      Vect3d v = line.intersectionWithPlane(plane());
      if (Vect3d.dist(v, _c) <= radiusLength())
        return v;
    } catch (ExDegeneration ex) { }

    return null;
  }
  
  /**
   * Intersection of the <strong>disk</strong> (not circle) with ray.
   * @param ray
   * @return
   */
  public Vect3d intersectInteriorWithRay(Ray3d ray) {
    try {
      Vect3d v = ray.intersectionWithPlane(plane());
      if (Vect3d.dist(v, _c) <= radiusLength())
        return v;
    } catch (ExDegeneration ex) { }

    return null;
  }

  /**
   * Проверка совпадения двух окружностей.
   * @param circ1
   * @param circ2
   */
  public static boolean equals(Circle3d circ1, Circle3d circ2){
    return Plane3d.equals(circ1.plane(), circ2.plane()) &&
           Vect3d.equals(circ1.center(), circ2.center()) &&
           Checker.isEqual(circ1.radiusLength(), circ2.radiusLength());
  }

  //Author Preobrazenskii

  /**
   * Строит касательную окружность  данным центром к данной окружности.
   * Подразумевается, что окружность и данная точка лежат в одной плоскости.
   * @param circ1
   * @param center2
   * @return касательная окружность
   * @throws ExDegeneration
   */
  public static Circle3d tangentCircleByPointAndCircle(Circle3d circ1, Vect3d center2)
          throws ExDegeneration {
    if( isCircleAndPointLieinOnePlane(circ1, center2) )
      throw new ExDegeneration("Point and circle is not lie in one plane");
    if( Checker.isPointInCircle(center2, circ1) ) {
      return new Circle3d(center2, Vect3d.rNormalizedVector(circ1.normal(),
              circ1.radiusLength() - Vect3d.dist(circ1.center(), center2)));
    } else {
      return new Circle3d(center2,  Vect3d.rNormalizedVector(circ1.normal(),
              Vect3d.dist(circ1.center(), center2) - circ1.radiusLength()));
    }
  }

  /**
   * Строим касательную из точки, не лежащей на окружности, на окружность.
   * @param point
   * @return точки касания
   * @throws geom.ExDegeneration
   */
  public ArrayList<Vect3d> tangentPoints(Vect3d point) throws ExDegeneration {
    ArrayList<Vect3d> tangentPoints = new ArrayList<Vect3d>();
    if(containsPoint(point)){
      tangentPoints.add(point);
      return tangentPoints;
    }
    Vect3d center = Vect3d.conv_hull(point, _c, 0.5);
    double radiusValue = Vect3d.sub(point, _c).norm() / 2;
    Vect3d radius = Vect3d.rNormalizedVector(normal(), radiusValue);
    Circle3d circ1 = new Circle3d(radius, center);
    return intersect(circ1);
  }

  /**
   * Касательная к окружности из точки на окружности в 2D
   * @param circ
   * @param point
   * @return касательная прямая
   * @throws ExDegeneration
   */
  public static Line3d tangentLineFromPointOnCircle2d(Circle3d circ, Vect3d point) throws ExDegeneration {
    Line3d radius = Line3d.getLineByTwoPoints(point, circ.center());
    Line3d result = Line3d.linePerpendicularLine2d(radius, point);
    return result;
  }

  /**
   * Точка на границе.
   * С её помощью выбирается направление оси при переходе в полярные координаты.
   * @return
   */
  public Vect3d getPointOnBoundary() {
    return plane().getSecondPoint(_n.norm());
  }

  /**
   * Точка по полярным координатам, определяемым окружностью.
   * @param phi
   * @param rho
   * @return
   */
  public Vect3d getPointByPolar(double phi, double rho) {
    // Умножаем вектор (0, radius) на rho,
    // поворачиваем на phi относительно центра.
    Vect3d radVect = Vect3d.sub(getPointOnBoundary(), _c);
    radVect.inc_mul(rho / radVect.norm());
    return Vect3d.sum(radVect.rotate(phi, _n), _c);
  }

  /**
   * Полярные координаты точки относительно окружности.
   * @param p
   * @return
   * @throws geom.ExDegeneration бросается, если точка не лежит в плоскости окружности
   */
  public double getPolarAngleByPoint(Vect3d p) throws ExDegeneration {
    return getPolarAngleByPointAndStartPointOnCircle(p, getPointOnBoundary());
  }

  /**
   * Полярные координаты точки относительно окружности с отмеченной точкой
   * @param p точка, чьи координаты определяем
   * @param startP точка на окружности, задающая ось полярных координат
   * (Ось проходит через центр окружности и эту точку)
   * @return величина угла
   * @throws ExDegeneration
   */
  public double getPolarAngleByPointAndStartPointOnCircle(Vect3d p, Vect3d startP) throws ExDegeneration {
    if (!Checker.isPlaneContainPoint(plane(), p))
      throw new ExDegeneration("Точка не лежит в плоскости окружности");
    double angle = Vect3d.getAngle(Vect3d.sub(startP, _c), Vect3d.sub(p, _c));
    if (Vect3d.tripleProd(Vect3d.sub(startP, _c), Vect3d.sub(p, _c), _n) > 0) {
      return angle;
    } else {
      return Math.PI * 2 - angle;
    }
  }

  /**
   * Find points of out tangent lines of circles
   * @param circ1 fist circle
   * @param circ2 second circle
   * @return list of points in order: circ1, circ2, circ1, circ2
   */
  public static ArrayList<Vect3d> pointsOfOutTangentLines(Circle3d circ1, Circle3d circ2)
  {
    ArrayList<Vect3d> result = new ArrayList<>();
    if(Checker.isTwoCirclesLieInOnePlane(circ1, circ2)) {
      // Difference between radii
      double dif = Math.abs(circ1.radiusLength() - circ2.radiusLength());
      // Normal of plane which contains circles
      Vect3d normal = circ1.normal();
      // Circles have difference radius
      if(dif > eps()) {
        Vect3d Cc;// vector from center of big circle to center of small circle
        Vect3d C, c;// centers of big and small circles
        double R, r;// radius of big and small circle
        if(circ1.radiusLength() > circ2.radiusLength() + eps()){
          Cc = sub(circ2.center(), circ1.center());
          C = circ1.center();
          c = circ2.center();
          R = circ1.radiusLength();
          r = circ2.radiusLength();
        } else {
          Cc = sub(circ1.center(), circ2.center());
          c = circ1.center();
          C = circ2.center();
          r = circ1.radiusLength();
          R = circ2.radiusLength();
        }
        // Angle beetwen radius to tangent point and Cc
        double ang = Math.acos(dif/Cc.norm());
        if(circ1.radiusLength() > circ2.radiusLength()) {
          result.add(sum(C, rNormalizedVector(Cc.rotate(ang, normal), R)));
          result.add(sum(c, rNormalizedVector(Cc.rotate(ang, normal), r)));
          result.add(sum(C, rNormalizedVector(Cc.rotate(-ang, normal), R)));
          result.add(sum(c, rNormalizedVector(Cc.rotate(-ang, normal), r)));
        } else {
          result.add(sum(c, rNormalizedVector(Cc.rotate(ang, normal), r)));
          result.add(sum(C, rNormalizedVector(Cc.rotate(ang, normal), R)));
          result.add(sum(c, rNormalizedVector(Cc.rotate(-ang, normal), r)));
          result.add(sum(C, rNormalizedVector(Cc.rotate(-ang, normal), R)));
        }
      // Circles have one radius and different centers
      } else if (!Vect3d.equals(circ1.center(), circ2.center())) {
          // vector from center of 1st circle to center of 2nd circle
          Vect3d c1c2 = sub(circ2.center(), circ1.center());
          double r = circ1.radiusLength();
          result.add(sum(circ1.center(), rNormalizedVector(c1c2.rotate(Math.PI/2, normal), r)));
          result.add(sum(circ2.center(), rNormalizedVector(c1c2.rotate(Math.PI/2, normal), r)));
          result.add(sum(circ1.center(), rNormalizedVector(c1c2.rotate(-Math.PI/2, normal), r)));
          result.add(sum(circ2.center(), rNormalizedVector(c1c2.rotate(-Math.PI/2, normal), r)));
      }
    }
    return result;
  }

  /**
   * Find points of inner tangent lines of circles.
   * @param circ1 first circle
   * @param circ2 second circle
   * @return list of points in order: circ1, circ1, circ2, circ2
   * @throws ExDegeneration
   */
  public static ArrayList<Vect3d> pointsOfInTangentLines(Circle3d circ1, Circle3d circ2) throws ExDegeneration {
    ArrayList<Vect3d> result = new ArrayList<>();
    if( (!equals(circ1,circ2)) && ( !isFirstCircleTouchSecondCircleInternally (circ1, circ2) )
      &&( !isOneCircleInSecondCircle( circ1, circ2) )
      && ( !isTwoCircleHaveTwoPointIntersect(circ1, circ2) )
      && Checker.isTwoCirclesLieInOnePlane(circ1, circ2)) {
      double r1 = circ1.radiusLength();
      double r2 = circ2.radiusLength();
      // отношение, в котором точка пересечения касательных делит линию, соединяющую центры:
      double k = r1 / ( r1+r2 );
      // координаты точки пересечения касательных
      double x = k * (circ2.center().x() - circ1.center().x());
      double y = k * (circ2.center().y() - circ1.center().y());
      double z = k * (circ2.center().z() - circ1.center().z());
      Vect3d M = new Vect3d(x, y, z);
      result = circ1.tangentPoints(M);
      result.addAll(circ2.tangentPoints(M));
    }
    return result;
  }

  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon() {
    return getAsAbstractPolygon(NUM_POINTS_AS_ABSTRACT_POLYGON);
  }

  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon(int numPoints) {
      ArrayList<Vect3d> res = new ArrayList<>();
      Vect3d anotherVect = Vect3d.rot_x(_n, Math.PI / 2);
      if (anotherVect.equals(_n)) // if _n is (any value, 0, 0)
          anotherVect = Vect3d.rot_y(_n, Math.PI / 2);
      Vect3d radVect = Vect3d.vector_mul(_n, anotherVect);
      radVect = Vect3d.rNormalizedVector(radVect, _n.norm());
      for (double p = 0; p < 1; p+=1.0/numPoints) {
          Vect3d rotRad = radVect.rotate(2 * Math.PI * p, _n);
          Vect3d point = Vect3d.sum(_c, rotRad);
          res.add(point);
      }
      return res;
  }

  public ArrayList<Polygon3d> faces() {
    ArrayList<Polygon3d> faces = new ArrayList<>();
    try {
      ArrayList<Vect3d> points = getAsAbstractPolygon();
      Vect3d a = new Vect3d(points.get(0));
      Vect3d b = new Vect3d(points.get(1));
      Vect3d c = new Vect3d(points.get(points.size() - 1));
      Orientation bypass = Orientation.getBodyOrientation(new Vect3d(), a, b, c);
      if (bypass == Orientation.LEFT)
          Collections.reverse(points);
      faces.add(new Polygon3d(new ArrayList<>(points)));
    }
    catch (ExGeom ex) { }
    return faces;
  }

  /**
   * Проверка, содержит ли круг точку.
   * @param v
   * @return
   */
  public boolean containsPoint(Vect3d v) {
    if (!plane().containsPoint(v)) return false;
    return Vect3d.dist(v, _c) <= radiusLength();
  }

  /**
   * В качестве основного набора точек - три точки на окружности.
   * @return
   */
  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList();
    Vect3d norm = _n;
    Vect3d rad = Vect3d.sub(getPointOnBoundary(), _c);
    result.add(getPointOnBoundary());
    rad = rad.rotate(Math.PI/2, norm);
    result.add(Vect3d.sum(rad, _c));
    rad = rad.rotate(Math.PI/2, norm);
    result.add(Vect3d.sum(rad, _c));

    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    Circle3d circ = null;
    try {
      circ = new Circle3d(points);
    } catch (ExDegeneration ex) {}
    return circ;
  }

  @Override
  public Vect3d getUpVect() {
    return _n.getNormalized();
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.CIRCLE3D;
  }
}