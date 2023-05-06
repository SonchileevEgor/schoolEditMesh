package geom;

import java.util.ArrayList;
import java.util.Collections;
import static geom.Checker.eps;
import static geom.Checker.isCollinear;
import static geom.Vect3d.*;
import static java.lang.Math.abs;

/**
 * Math object plane
 * @author
 */
public class Plane3d implements i_Geom, i_OrientableGeom {
  // Храним три точки плоскости, поскольку для точки на плоскости,
  // построенной по трем точкам, при её перемещении начинается ад.

  private Vect3d _n;    // вектор нормали
  private Vect3d _pnt;  // точка на плоскости
  private Vect3d _pnt2; // вторая точка плоскости
  private Vect3d _pnt3; // третья точка плоскости

  /**
   * Constructor of plane. The first parameter is normal, the second parameter is point
   * @param n normal
   * @param pnt point
   */
  public Plane3d(Vect3d n, Vect3d pnt){
    _n = Vect3d.getNormalizedVector(n);
    _pnt = pnt;

    Vect3d supply = (_n.x() == 0 && _n.y() == 0) ?
      new Vect3d(1, 0, 0) :
      new Vect3d(-_n.y(), _n.x(), 0);
    supply.inc_mul(1 / supply.norm());

    _pnt2 = Vect3d.sum(_pnt, supply);
    _pnt3 = Vect3d.sum(Vect3d.vector_mul(Vect3d.sub(_pnt2, _pnt), _n), _pnt);
  }

  /**
   * Constructs a plane by three points
   * @param a
   * @param b
   * @param c
   * @throws geom.ExDegeneration if a, b, c lies on the same line
   */
  public Plane3d(Vect3d a, Vect3d b, Vect3d c) throws ExDegeneration {
    Vect3d n = Vect3d.vector_mul(Vect3d.sub(b, a),Vect3d.sub(c, a));
    if(n.norm() == 0)
      throw new ExDegeneration("Points A, B, C lies on the same line");
    _n = Vect3d.getNormalizedVector(n);
    _pnt = a;
    _pnt2 = b;
    _pnt3 = c;
  }

  public Plane3d(ArrayList<Vect3d> points) throws ExDegeneration {
    this(points.get(0), points.get(1), points.get(2));
  }

  /**
   * Constructor of plane by line and point
   * @param line line
   * @param pnt point which doesn't belong line
   * @throws ExDegeneration
   */
  public Plane3d(Line3d line, Vect3d pnt) throws ExDegeneration {
    Plane3d plane = new Plane3d(line.pnt(), line.pnt2(), pnt);
    _n = plane.n();
    _pnt = plane.pnt();
  }

  public Plane3d duplicate() {
    return new Plane3d(_n, _pnt);
  }

  /**
   * OXY plane.
   * @return
   * @author alexeev
   */
  static public Plane3d oXY() {
    return new Plane3d(new Vect3d(0, 0, 1), new Vect3d(0, 0, 0));
  }

  /**
   * Construct plane by point, parallel given plane
   * @param pnt point of new plane
   * @param pln parallel plane
   * @return plane
   * @throws ExDegeneration
   */
  static public Plane3d planeByPointParalPlane(Vect3d pnt, Plane3d pln)
          throws ExDegeneration {
    return new Plane3d(pln.n(), pnt);
  }

  /**
   * Construct plane by point, parallel given line, by angle
   * Base normal: if angle is zero, normal is perpendicular from given point (pnt) on line.
   * @param pnt given point
   * @param line parallel line
   * @param angle angle between normal and default normal
   * @return plane
   * @throws ExDegeneration
   */
  static public Plane3d planeByPointParalLine(Vect3d pnt, Line3d line, double angle)
          throws ExDegeneration {
    if(!line.contains(pnt)){
      Vect3d normal = sub(pnt, line.projectOfPoint(pnt)).rotate(angle, line.l());
      return new Plane3d(normal, pnt);
    }else
      throw new ExDegeneration("Point must not belong line!");
  }

  /**
   * Construct plane through a line, parallel another given line
   * @param line1 line in plane
   * @param line2 parallel line
   * line1 and line2 are skew lines (not parallel, not equal and don't intersect)
   * @return plane
   * @throws ExDegeneration
   */
  static public Plane3d planeBySkewLines(Line3d line1, Line3d line2) throws ExGeom {
    if( !Checker.isTwoLinesIntersect(line1, line2) &&
            !Checker.isTwoLinesParallel(line1, line2) &&
            !Line3d.equals(line1, line2) ){
        Line3d line = Line3d.lineParallelLine (line2, line1.pnt());
        Vect3d normal = Vect3d.vector_mul(line1.l(), line.l());
      return new Plane3d(normal, line1.intersectionWithLine(line));
    } else
      throw new ExGeom("Прямые не скрещиваются");
  }

  /**
   * Construct plane by point, orthogonal given plane, by angle
   * @param pnt given point
   * @param plane orthogonal plane
   * @param angle between normal and base normal
   * @return plane
   * @throws ExDegeneration
   */
  static public Plane3d planeByPointOrthPlane(Vect3d pnt, Plane3d plane, double angle)
          throws ExDegeneration {
    Vect3d pntB = Vect3d.sum(pnt, plane.n()); // pntB is a second point on result plane
    Vect3d pntA = new Vect3d(0,0,0); // pntA is a third point on result plane
    if (Checker.threePointsOnTheLine(pnt, pntB, pntA)){ // if pntA is on the line pnt-pntB we should move it
      pntA.set_x(1);
      if (Checker.threePointsOnTheLine(pnt, pntB, pntA)){
         pntA.set_y(1); // (0,0,0) (1,0,0) (1,1,0) are not in one line so now pntA is not in one line with pntB and pnt
      }
    }
    Vect3d normal = Vect3d.vector_mul(Vect3d.sub(pnt, pntA), Vect3d.sub(pntB,pntA)).rotate(angle, plane.n());
    return new Plane3d(normal, pnt);
  }

     /**
   * Construct plane through a line, orthogonal given plane, that is parallel to the line
   * @param line line in plane
   * @param plane orthogonal plane, parallel to line
   * @return plane
   * @throws ExDegeneration
   */
  static public Plane3d planeByLineAndOrthPlane(Line3d line, Plane3d plane) throws ExDegeneration {
    if (Checker.isOrthogonal(plane.n(), line.l())) {
      Vect3d pnt = line.pnt();
      Line3d line1 = Line3d.line3dByTwoPoints(pnt, plane.projectionOfPoint(pnt));
      Vect3d normal = Vect3d.vector_mul(line1.l(), line.l());
      return new Plane3d(normal, line1.intersectionWithLine(line));
    } else {
      throw new ExDegeneration("Line and plane are not orthogonal");
    }
  }


  /**
   * Construct plane by 2 points and angle
   * @param a 1st point
   * @param b 2nd point
   * @param angle angle between normal and default normal
   * @return plane
   * @throws ExDegeneration
   */
  static public Plane3d planeByTwoPoints(Vect3d a, Vect3d b, double angle) throws ExDegeneration {
    if(!Vect3d.equals(a, b)){
      Vect3d ba = sub(a, b);
      Vect3d c = new Plane3d(ba, a).getSecondPoint(1);
      Vect3d ac = sub(c, a);
      Vect3d normal = vector_mul(ac, ba).rotate(angle, ba);
      return new Plane3d(normal, a);
    } else {
      throw new ExDegeneration("Points must not be equal!");
    }
  }

    /**
   * Construct plane through the given line at an angle to the given plane (line lies in given plane)
   * @param line
   * @param plane
   * @param angle angle beetwen result plane and given plane
   * @return plane
   */
  static public Plane3d planeByLineAndAngleBetweenPlanes(Line3d line, Plane3d plane, double angle) {
    Vect3d normal = plane._n.rotate( angle, line.l());
    return new Plane3d(normal, line.pnt());
  }


  /**
   * Construct plane by point, orthogonal line
   * @param pnt point of new plane
   * @param line orthogonal line
   * @return plane
   */
  static public Plane3d planeByPointOrthLine(Vect3d pnt, Line3d line){
    return new Plane3d(line.l(), pnt);
  }

  /**
   * @return normal of plane
   */
  public Vect3d n() { return _n.duplicate(); }

  /**
   * @return point of plane
   */
  public Vect3d pnt() {
    return _pnt.duplicate();
  }

  public Vect3d pnt2() {
    return _pnt2.duplicate();
  }

  public Vect3d pnt3() {
    return _pnt3.duplicate();
  }

  /**
   * computes a direct projection of point onto the plane
   * @param c point
   * @return projection, point on plane
   */
  public Vect3d projectionOfPoint(Vect3d c) {
    Vect3d v = sub(_pnt, c);
    double coef = inner_mul(_n, v) / inner_mul(_n, _n);
    double x1 = _n.x() * coef + c.x();
    double y1 = _n.y() * coef + c.y();
    double z1 = _n.z() * coef + c.z();
    return new Vect3d(x1, y1, z1);
  }

  /**
   * Find distance from point to plane
   * @param c point
   * @return distance
   */
  public double distFromPoint(Vect3d c){
    return Math.abs((inner_mul(_n, c) - inner_mul(_n, _pnt))/_n.norm());
  }

  /**
   * Check plane contains given point
   * @param p given point
   * @return true if plane contains point and false otherwise
   */
  public boolean containsPoint(Vect3d p){
    return abs(inner_mul(_n, p) - inner_mul(_n, _pnt)) < eps();
  }

  /**
   * Check plane contains given line
   * @param line given line
   * @return true if plane contains line and false otherwise
   */
  public boolean containsLine(Line3d line){
    return containsPoint(line.pnt()) && containsPoint(line.pnt2());
  }

   /**
   * Find projection of vector on plane
   * @param v vector
   * @return vector on plane
   */
  public Vect3d projectionOfVect( Vect3d v ){
    return sub(projectionOfPoint(sum(_pnt, v)), _pnt);
  }

  /**
   * Find projection of point along vector on plane
   * @param p point
   * @param v vector, direction of projection
   * @return point, projection on plane
   * @throws ExDegeneration
   */
  public Vect3d projectionOfPointAlongVect(Vect3d p, Vect3d v) throws ExDegeneration{
    if (!Checker.isOrthogonal(v, _n)){
      Line3d line = new Line3d(p, v);
      line.intersectionWithPlane(this);
      return line.intersectionWithPlane(this);
    } else
      throw new ExDegeneration("вектор проекции и плоскость коллинеарны");
  }

  /**
   * Возвращает координату z точки на плоскости по известным x и y
   * @param x абсцисса точки плоскости
   * @param y ордината точки плоскости
   * @return аппликата точки плоскости
   */
    public double z(double x, double y){
    if(_n.z()!=0) {
      return (inner_mul(_n, _pnt)-_n.x()*x-_n.y()*y)/_n.z();
    } else {
      return 0;
    }
  }

  /**
   * Check line and plane have intersection
   * @param plane plane
   * @return true if they have intersection false othewise
   */
  public boolean isIntersectsPlane(Plane3d plane){
    return (Checker.isCollinear(_n, plane.n()) && plane.containsPoint(_pnt)) ||
           !Checker.isCollinear(_n, plane.n());
  }

  /**
   * Cosine of angle between normals of 2 planes
   * @param pl1 1st plane
   * @param pl2 2nd plane
   * @return Cosine of angle
   */
  public static double cos(Plane3d pl1, Plane3d pl2) {
    return Vect3d.cos(pl1.n(), pl2.n());
  }

  /**
   * Find intersection line and plane
   * @param plane plane which intersect line
   * @return line of intersection
   * @throws ExDegeneration
   */
  public Line3d intersectionWithPlane(Plane3d plane) throws ExDegeneration {
    if(!Checker.isCollinear(_n, plane.n())){
      Vect3d l = vector_mul(_n, plane.n());
      Vect3d direction = this.projectionOfVect(plane.n());
      Vect3d tOfLine = new Line3d(_pnt, direction).intersectionWithPlane(plane);
      return new Line3d( tOfLine, l );
    }else
      throw new ExDegeneration("плоскости не пересекаются или совпадают");
  }

  public ArrayList<Polygon3d> faces(ArrayList<Vect3d> points) throws ExGeom {
      ArrayList<Polygon3d> faces = new ArrayList<>();
      ArrayList<Vect3d> face = new ArrayList<>(points);
      Vect3d a = new Vect3d(face.get(0));
      Vect3d b = new Vect3d(face.get(1));
      Vect3d c = new Vect3d(face.get(face.size()-1));
      // FIXME: временный костыль.
      // Пока с ним почему-то всё работает
      // (а именно при выводе файла для 3d-печати фигура будет всегда правильно ориентирована).
      // Необходимо разобраться в возникнувших странностях и разрешить проблему красиво и элегантно
      Vect3d o = new Vect3d(200000.0, 200000.0, 200000.0);
      Orientation bypass = Orientation.getBodyOrientation(o, a, b, c);
      if (bypass == Orientation.LEFT)
          Collections.reverse(face);
      faces.add(new Polygon3d(new ArrayList<>(face)));
      return faces;
  }

  /**
   * Check two planes are equal
   * @param pl1 1st plan
   * @param pl2 2nd plane
   * @return true if planes are equal, false otherwise
   */
  public static boolean equals(Plane3d pl1, Plane3d pl2){
    return pl1.containsPoint(pl2.pnt()) && isCollinear(pl1.n(), pl2.n());
  }

  /**
   * Computation of second point in plane
   * with given distance from first point.
   * @param distance
   * @return
   */
  public Vect3d getSecondPoint(double distance) {
    return Vect3d.sum(Vect3d.mul(Vect3d.sub(_pnt2, _pnt), distance), _pnt);
  }

  public ArrayList<Vect3d> intersect(Ray3d ray) {
    ArrayList<Vect3d> result = new ArrayList<Vect3d>();
    try {
      Vect3d point = ray.line().intersectionWithPlane(this);
      if (ray.containsPoint(point)) {
        result.add(point);
      }
    } catch (ExDegeneration ex) {}
    return result;
  }

  public ArrayList<Vect3d> intersectWithPoly(Polygon3d poly) throws ExDegeneration {
    ArrayList<Vect3d> intersect = new ArrayList<Vect3d>();
    if (Checker.isPlaneParallelPlane(this, poly.plane()) || Plane3d.equals(this, poly.plane()))
      return intersect;
    Line3d line = poly.plane().intersectionWithPlane(this);
    intersect = poly.intersectBoundaryWithLine(line);
    return intersect;
  }

  public ArrayList<Vect3d> intersectWithCircle(Circle3d circ) throws ExDegeneration {
    ArrayList<Vect3d> intersect = new ArrayList<Vect3d>();
    if (Checker.isPlaneParallelPlane(this, circ.plane()) || Plane3d.equals(this, circ.plane()))
      return intersect;
    Line3d line = circ.plane().intersectionWithPlane(this);
    intersect = circ.intersect(line);
    return intersect;
  }

  /**
   * Создает биссекторную плоскость 2ух плоскостей
   * Функция возвращает обе биссекторные плоскости, причем 1ой будет та плоскость,
   * нормаль которой лежит между нормалями переданных плоскостей
   * @param pl1 1ая плоскость
   * @param pl2 2ая плоскость
   * @return ArrayList<Plane3d>
   */
  public static ArrayList<Plane3d> bisectorPlaneOf2Planes(Plane3d pl1, Plane3d pl2) throws ExGeom {
    if (!Checker.isPlaneParallelPlane(pl1, pl2)){
      ArrayList<Plane3d> bispl = new ArrayList<Plane3d>();
      Line3d inters = pl1.intersectionWithPlane(pl2);
      Triang3d triang = new Triang3d(inters.pnt(), Vect3d.sum(inters.pnt(), pl1.n()),
              Vect3d.sum(inters.pnt(), pl2.n()));
      Line3d bis = new Line3d(triang.bisectrixA());
      bispl.add(new Plane3d(bis.l(), inters.pnt()));
      bispl.add(new Plane3d(inters.pnt(), Vect3d.sum(inters.pnt(), inters.l()), Vect3d.sum(inters.pnt(), bis.l())));
      return bispl;
    } else {
      throw new ExGeom("плоскости параллельны");
    }
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList<>();
    result.add(_pnt);
    result.add(_pnt2);
    result.add(_pnt3);
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      return new Plane3d(points);
    } catch (ExDegeneration ex) { }
    return null;
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public Vect3d getUpVect() {
    return n();
  }

  @Override
  public GeomType type() {
    return GeomType.PLANE3D;
  }
}