package geom;

import static geom.Checker.threePointsOnTheLine;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import util.Util;

/**
 * Simple 3d-vector.
 *
 * @author lt
 */
public class Vect3d implements AbstractPolygon, i_Geom {
  private double _x, _y, _z;

  /**
   * (0, 0, 0) vector.
   */
  public static final Vect3d O = new Vect3d(0, 0, 0);

  /**
   * X unit vector.
   */
  public static final Vect3d UX = new Vect3d(1, 0, 0);

  /**
   * Y unit vector.
   */
  public static final Vect3d UY = new Vect3d(0, 1, 0);

  /**
   * Z unit vector.
   */
  public static final Vect3d UZ = new Vect3d(0, 0, 1);

  /**
   * Vector with zero coordinates.
   */
  public Vect3d() {
    _x = _y = _z = 0.0;
  }

  /**
   * Vector (x, y, z).
   * @param x X coordinate
   * @param y Y coordinate
   * @param z Z coordinate
   */
  public Vect3d(double x, double y, double z){
    _x = x; _y = y; _z = z;
  }

  /**
   * Create copy of vector.
   * @param v
   */
  public Vect3d(Vect3d v) {
    this(v.x(), v.y(), v.z());
  }

  private Vect3d(ArrayList<Vect3d> points) {
    _x = points.get(0).x();
    _y = points.get(0).y();
    _z = points.get(0).z();
  }

  /**
   * Create copy of this.
   * @return
   */
  public Vect3d duplicate() {
    return new Vect3d(this);
  }

  /**
   * Copy all parameters from other vector.
   * @param vect3d Вектор из которого берутся параметры
   */
  public void setAs(Vect3d vect3d){
    set(vect3d.x(), vect3d.y(), vect3d.z());
  }

  public double x() { return _x; }
  public double y() { return _y; }
  public double z() { return _z; }

  public void set_x( double x ) { _x = x; }
  public void set_y( double y ) { _y = y; }
  public void set_z( double z ) { _z = z; }

  public void set( double x, double y, double z ) {
    _x = x; _y = y; _z = z;
  }

  /**
   * Норма (длина) вектора.
   * Вычисления "без переполнения" - хорошо, но очень долго.
   * Поэтому считаем по-быстрому.
   * @return
   */
  public double norm() {
    return Math.sqrt(_x * _x + _y * _y + _z * _z);
  }
  
  public Vect3d normalize() {
    double locLength = norm();
    double inv_length = (1 / locLength);
    return new Vect3d(_x*inv_length,_y*inv_length, _z*inv_length);
  }

  /**
   * Квадрат нормы вектора.
   * @return
   */
  public double mag() {
    return _x * _x + _y * _y + _z * _z;
  }

  /**
   * Rotate vector around axis by given angle in CCW direction.
   * @param angle
   * @param axis
   * @return
   */
  public Vect3d rotate(double angle, Vect3d axis) {
    Vect3d n = Vect3d.mul(axis, 1/axis.norm());
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);

    //матрица поворота
    Matrix3 m = new Matrix3(cos + (1 - cos) * n.x() * n.x(), (1 - cos) * n.x() * n.y() - sin * n.z(), (1 - cos) * n.x() * n.z() + sin * n.y(),
                            (1 - cos) * n.x() * n.y() + sin * n.z(), cos + (1 - cos) * n.y() * n.y(), (1 - cos) * n.y() * n.z() - sin * n.x(),
                            (1 - cos) * n.x() * n.z() - sin * n.y(), (1 - cos) * n.y() * n.z() + sin * n.x(), cos + (1 - cos) * n.z() * n.z());

    return m.mulOnVect(this);
  }

  public void rot_x( double al ){
    Vect2d yz = new Vect2d(_y, _z);
    yz.rot(al);
    _y = yz.x();
    _z = yz.y();
  }

  public void rot_x( SinCos sc ){
    Vect2d yz = new Vect2d(_y, _z);
    yz.rot(sc);
    _y = yz.x();
    _z = yz.y();
  }

  public void rot_y( double al ){
    Vect2d xz = new Vect2d(_x, _z);
    xz.rot(-al);
    _x = xz.x();
    _z = xz.y();
  }

  public void rot_y( SinCos sc ){
    Vect2d xz = new Vect2d(_x, _z);
    xz.rot(new SinCos(-sc.sin(), sc.cos()));
    _x = xz.x();
    _z = xz.y();
  }

  public void rot_z( double al ){
    Vect2d xy = new Vect2d(_x, _y);
    xy.rot(al);
    _x = xy.x();
    _y = xy.y();
  }

  public void rot_z( SinCos sc ){
    Vect2d xy = new Vect2d(_x, _y);
    xy.rot(sc);
    _x = xy.x();
    _y = xy.y();
  }

  public void rot_x( double al, Vect3d c3 ){
    Vect2d yz = new Vect2d(_y, _z);
    Vect2d c2 = new Vect2d(c3.y(), c3.z());
    yz.rot(al, c2);
    _y = yz.x();
    _z = yz.y();
  }

  public void rot_x( SinCos sc, Vect3d c3 ){
    Vect2d yz = new Vect2d(_y, _z);
    Vect2d c2 = new Vect2d(c3.y(), c3.z());
    yz.rot(sc, c2);
    _y = yz.x();
    _z = yz.y();
  }

  public void rot_y( double al, Vect3d c3 ){
    Vect2d xz = new Vect2d(_x, _z);
    Vect2d c2 = new Vect2d(c3.x(), c3.z());
    xz.rot(-al, c2);
    _x = xz.x();
    _z = xz.y();
  }

  public void rot_y( SinCos sc, Vect3d c3 ){
    Vect2d xz = new Vect2d(_x, _z);
    Vect2d c2 = new Vect2d(c3.x(), c3.z());
    xz.rot(new SinCos(-sc.sin(), sc.cos()), c2);
    _x = xz.x();
    _z = xz.y();
  }

  public void rot_z( double al, Vect3d c3 ){
    Vect2d xy = new Vect2d(_x, _y);
    Vect2d c2 = new Vect2d(c3.x(), c3.y());
    xy.rot(al, c2);
    _x = xy.x();
    _y = xy.y();
  }

  public void rot_z( SinCos sc, Vect3d c3 ){
    Vect2d xy = new Vect2d(_x, _y);
    Vect2d c2 = new Vect2d(c3.x(), c3.y());
    xy.rot(sc, c2);
    _x = xy.x();
    _y = xy.y();
  }

  // theta = angle(Pxy(v),Ox); phi = angle(v,Oz)
  public Vect2d project( double theta, double phi ){
    Vect3d res = this.duplicate();
    res.rot_z(Math.PI/2.0 - theta);
    res.rot_x(-(Math.PI - phi));
    return new Vect2d(-res.x(), res.y());
  }

  public Vect2d project( SinCos sc_theta, SinCos sc_phi ){
    Vect3d res = this.duplicate();
    res.rot_z(new SinCos(sc_theta.cos(), sc_theta.sin())); // M_PI/2.0 - theta
    res.rot_x(new SinCos(-sc_phi.sin(), -sc_phi.cos())); // -(M_PI - phi)
    return new Vect2d(-res.x(), res.y());
  }

  public Vect2d projectOnOXY(){
    return new Vect2d(_x, _y);
  }

  public void inc_add( Vect3d v ) { _x += v.x();  _y += v.y();  _z += v.z(); }
  public void inc_sub( Vect3d v ) { _x -= v.x();  _y -= v.y();  _z -= v.z(); }
  public void inc_mul( double k ) { _x *= k;  _y *= k;  _z *= k; }
  public void inc_div( double k ) { _x /= k;  _y /= k;  _z /= k; }

  public void mull_x( double k ) { _x *= k;}
  public void mull_y( double k ) { _y *= k;}
  public void mull_z( double k ) { _z *= k;}

  public static double norm( Vect3d v ) { return v.norm(); }
  public static double mag( Vect3d v ) { return v.mag(); }
  
  /**
   * Вычисления "без переполнения" - хорошо, но очень долго.
   * Поэтому считаем по-быстрому.
   * @param v1
   * @param v2
   * @return 
   */
  public static double dist( Vect3d v1, Vect3d v2 ) {
    return Math.sqrt((v1.x() - v2.x()) * (v1.x() - v2.x()) +
                     (v1.y() - v2.y()) * (v1.y() - v2.y()) +
                     (v1.z() - v2.z()) * (v1.z() - v2.z()));
  }

  public double[] getArray(){
    double[] res = new double[3];
    res[0] = _x;
    res[1] = _y;
    res[2] = _z;
    return res;
  }
  
  /**
   * Create Vect3d instance from string representation.
   * @param str
   * @return
   * @throws ParseException 
   */
  public static Vect3d fromString(String str) throws ParseException {
    String[] coords = str.split(";");
    // Ищем количество координат.
    switch( coords.length ){
      case 2:
        try {
          double x = Double.parseDouble(coords[0]);
          double y = Double.parseDouble(coords[1]);
          return new Vect3d(x, y, 0);
        } catch( NumberFormatException ex ){
          throw new ParseException(ex.getMessage(), -1);
        }
      case 3:
        try {
          double x = Double.parseDouble(coords[0]);
          double y = Double.parseDouble(coords[1]);
          double z = Double.parseDouble(coords[2]);
          return new Vect3d(x, y, z);
        } catch( NumberFormatException ex ){
          throw new ParseException(ex.getMessage(), -1);
        }
      default:
        throw new ParseException("Wrong coordinates number.", -1);
    }
  }

  public String toString(int precision, boolean is3d) {
    if( is3d ){
      return String.format("(%s; %s; %s)",
              Util.valueOf(_x, precision),
              Util.valueOf(_y, precision),
              Util.valueOf(_z, precision));
    } else {
      return String.format("(%s; %s)",
              Util.valueOf(_x, precision),
              Util.valueOf(_y, precision));
    }
  }

  // theta = angle(Pxy(v),Ox), phi = angle(v,Oz)
  public static Vect3d polar( double theta, double phi, double rho ){
    return new Vect3d(rho * Math.sin(phi) * Math.cos(theta),
                      rho * Math.sin(phi) * Math.sin(theta),
                      rho * Math.cos(phi));
  }

  public static Vect3d polar( SinCos sc_theta, SinCos sc_phi, double rho ){
    return new Vect3d(rho * sc_phi.sin() * sc_theta.cos(), rho * sc_phi.sin() * sc_theta.sin(), rho * sc_phi.cos());
  }

  public static Vect3d rot_x( Vect3d v, double al ){
    Vect3d res = v.duplicate();
    res.rot_x(al);
    return res;
  }

  public static Vect3d rot_x( Vect3d v, SinCos sc ){
    Vect3d res = v.duplicate();
    res.rot_x(sc);
    return res;
  }

  public static Vect3d rot_y( Vect3d v, double al ){
    Vect3d res = v.duplicate();
    res.rot_y(al);
    return res;
  }

  public static Vect3d rot_y( Vect3d v, SinCos sc ){
    Vect3d res = v.duplicate();
    res.rot_y(sc);
    return res;
  }

  public static Vect3d rot_z( Vect3d v, double al ){
    Vect3d res = v.duplicate();
    res.rot_z(al);
    return res;
  }

  public static Vect3d rot_z( Vect3d v, SinCos sc ){
    Vect3d res = v.duplicate();
    res.rot_z(sc);
    return res;
  }

  public static Vect3d rot_x( Vect3d v, double al, Vect3d c ){
    Vect3d res = v.duplicate();
    res.rot_x(al, c);
    return res;
  }

  public static Vect3d rot_x( Vect3d v, SinCos sc, Vect3d c ){
    Vect3d res = v.duplicate();
    res.rot_x(sc, c);
    return res;
  }

  public static Vect3d rot_y( Vect3d v, double al, Vect3d c ){
    Vect3d res = v.duplicate();
    res.rot_y(al, c);
    return res;
  }

  public static Vect3d rot_y( Vect3d v, SinCos sc, Vect3d c ){
    Vect3d res = v.duplicate();
    res.rot_y(sc, c);
    return res;
  }

  public static Vect3d rot_z( Vect3d v, double al, Vect3d c ){
    Vect3d res = v.duplicate();
    res.rot_z(al, c);
    return res;
  }

  public static Vect3d rot_z( Vect3d v, SinCos sc, Vect3d c ){
    Vect3d res = v.duplicate();
    res.rot_z(sc, c);
    return res;
  }

  /**
   * Inner (scalar) product of two vectors.
   * @param a
   * @param b
   * @return
   */
  public static double inner_mul( Vect3d a, Vect3d b ){
    return a.x() * b.x() + a.y() * b.y() + a.z() * b.z();
  }

  /**
   * Vector product of two vectors.
   * @param a
   * @param b
   * @return
   */
  public static Vect3d vector_mul( Vect3d a, Vect3d b ){
    return new Vect3d(a.y() * b.z() - a.z() * b.y(),
                      a.z() * b.x() - a.x() * b.z(),
                      a.x() * b.y() - a.y() * b.x());
  }

  /**
   * Computation of normalized vector.
   * @param v
   * @return
   */
  public static Vect3d getNormalizedVector(Vect3d v){
    return (v.norm() == 0) ? v.duplicate() : Vect3d.mul(v, 1 / v.norm());
  }

  /**
   * Get normalized version of current vector.
   * <br>This function not changes original vector.
   * @return Normalized version of current vector.
   */
  public Vect3d getNormalized(){
    return getNormalizedVector(this);
  }

  /**
   * Computation of co-directed vector with given distance.
   * @param v
   * @param r distance of result vector
   * @return
   */
  public static Vect3d rNormalizedVector(Vect3d v, double r){
    Vect3d res = getNormalizedVector(v);
    res.inc_mul(r);
    return res;
  }

  /**
   * Cosine of angle between two vectors.
   * @param v
   * @param w
   * @return
   */
  public static double cos(Vect3d v, Vect3d w){
    return Vect3d.inner_mul(v, w)/(v.norm()*w.norm());
  }

  /**
   * Linear combination of two points.
   * @param a first point
   * @param b second point
   * @param al multiplier
   * @return
   */
  public static Vect3d conv_hull( Vect3d a, Vect3d b, double al ){
    Vect3d d = Vect3d.sub(b, a);
    return new Vect3d(a.x() + al * d.x(), a.y() + al * d.y(), a.z() + al * d.z());
  }

  /**
   * Get sum of two vectors.
   * @param a first vector
   * @param b second vector
   * @return
   */
  public static Vect3d sum( Vect3d a, Vect3d b ){
    return new Vect3d(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
  }

  /**
   * Get difference of two vectors.
   * @param a first vector
   * @param b second vector
   * @return
   */
  public static Vect3d sub( Vect3d a, Vect3d b ){
    return new Vect3d(a.x() - b.x(), a.y() - b.y(), a.z() - b.z());
  }

  /**
   * Multiply vector by number.
   * @param a vector
   * @param k number
   * @return
   */
  public static Vect3d mul( Vect3d a, double k ){
    return new Vect3d(a.x() * k, a.y() * k, a.z() * k);
  }

  /**
   * Compute point from affine coordinates.
   * @param a First point of barycentric system.
   * @param b Second point of barycentric system.
   * @param c Third point of barycentric system.
   * @param alpha First multiplier.
   * @param beta Second multiplier.
   * @return
   */
  public static Vect3d getVectFromAffineCoords(Vect3d a, Vect3d b, Vect3d c, double alpha, double beta) {
    Vect3d result = Vect3d.mul(Vect3d.sub(b, a), alpha);
    result.inc_add(Vect3d.mul(Vect3d.sub(c, a), beta));
    result.inc_add(a);
    return result;
  }

  /**
   * Get sum of this vector and v2
   * @param v2 Vector for sum.
   * @return Sum of this vector and v2 as new vector.
   */
  public Vect3d add(Vect3d v2){
    return sum(this, v2);
  }

  /**
   * Get sum of this vector and v2
   * @param v2 Vector for sum.
   * @return Sum of this vector and v2 as new vector.
   */
  public Vect3d sum(Vect3d v2){
    return sum(this, v2);
  }

  /**
   * Get subtraction of this vector and v2
   * @param v2 Vector for subtraction
   * @return Subtraction of this vector and v2 as new vector.
   */
  public Vect3d sub(Vect3d v2){
    return sub(this, v2);
  }

  /**
   * Get the vector obtained by multiplying the original vector to the coefficient k.
   * @param k Coefficient for multiplying.
   * @return Vector obtained by multiplying the original vector to the coefficient k as new vector.
   */
  public Vect3d mul(double k){
    return mul(this, k);
  }

  /**
   * Get mixed product of three vectors.
   * @param v1
   * @param v2
   * @param v3
   * @return
   */
  public static double tripleProd( Vect3d v1, Vect3d v2, Vect3d v3 ){
    return Det23.calc(v1.x(), v1.y(), v1.z(), v2.x(), v2.y(), v2.z(), v3.x(), v3.y(), v3.z());
  }

  /**
   * Sorting of given _PLANAR_ convex set of points, which _DON'T_LIE_ON_ONE_LINE_.
   * If set isn't convex sorting isn't single valued!
   * @param points list of points
   * @throws ExZeroDivision
   * @throws geom.ExDegeneration
   * @author rita
   */
  //!! TODO: Consider case of all points lie on one line
  public static void sortPlanarConvexSet(ArrayList<Vect3d> points) throws ExZeroDivision, ExDegeneration {
    // Set need sorting only if it has more then 3 elements
    int n = points.size();// number of given points
    if(n > 3){
      // Choose polar center (point inside given set of points) and choose polar axis.
      // Sorting all points by angles between axis and corresponding vectors.
      int k = 0;//index
      // Choose 3 points which don't lie on one line.
      Vect3d vert1, vert2, vert3;
      while(k < n-2){
        if (!threePointsOnTheLine(points.get(k), points.get(k+1), points.get(k+2)))
          break;
        k++;
      }
      if (k == n - 2) {
        vert1 = points.get(0);
        vert2 = points.get(n / 3);
        vert3 = points.get(2 * n / 3);
      } else {
        vert1 = points.get(k);
        vert2 = points.get(k + 1);
        vert3 = points.get(k + 2);
      }
      // Find point which lie inside triangle with these vertices (centroid).
      // This point is center of polar coorditate.
      Vect3d polarCenter = Triang3d.centroid(vert1, vert2, vert3);
      // Get vector from center to some point as polar axis.
      Vect3d polarAxis = sub(vert1, polarCenter);
      Vect3d vectInPlane = sub(vert2, polarCenter);
      // Vector from plane where all points lie.
      NewSystemOfCoor polarSyst = new NewSystemOfCoor(polarCenter, polarAxis, vectInPlane);
      // Create list of angles with the same indexes as list of points
      ArrayList<Double> angles = new ArrayList<Double>();
      for(int i = 0; i < n; i++)
        angles.add(polarSyst.getPolarAngleInOxy(points.get(i)));
      // Sorting list of angles and synchronous sorting list of points
      for(int i = 0; i < n; i++)
        for(int j = 0; j < i; j++)
          if(angles.get(j) > angles.get(i)) {
            angles.add(j, angles.get(i));
            angles.remove(i + 1);
            points.add(j, points.get(i));
            points.remove(i + 1);
          }
    }
  }

  /**
   * Rotate list of points, start with known element
   * @param points list o points
   * @param startPoint begin element
   */
  public static void rotatePointsList(ArrayList<Vect3d> points, Vect3d startPoint) {
    int indOfBeginPoint = points.indexOf(startPoint);
    ArrayList<Vect3d> tail = new ArrayList<Vect3d>();
    for(int i = indOfBeginPoint; i < points.size(); i++)
      tail.add(points.get(i));
    points.removeAll(tail);
    points.addAll(0, tail);
  }

  /**
   * Find points of regular polygon by edge, plane and number of vertices.
   * the last and the first points are the same
   * @param a vertex of regular polygon
   * @param b vertex of regular polygon
   * @param pl plane
   * @param vertNum number of vertices
   * @return
   * @throws geom.ExGeom
   */
  public static ArrayList<Vect3d> getRegularPolygonPoints (
          Vect3d a, Vect3d b, Plane3d pl, int vertNum) throws ExGeom {
    if (a.equals(b)) {
      throw new ExGeom("вершины многоугольника должны быть различны!");
    }
    if (!pl.containsPoint(a) || !pl.containsPoint(b)) {
      throw new ExGeom("точки многоугольника должны лежать в одной плоскости!");
    }

    ArrayList<Vect3d> vert = new ArrayList<Vect3d>();
    vert.add(a);
    vert.add(b);

    Vect3d step = Vect3d.sub(b, a);
    double angle = Math.PI * 2 / vertNum;
    Vect3d curr = b;

    for (int i = 0; i < vertNum - 2; i++) {
      step = step.rotate(angle, pl.n());
      curr = Vect3d.sum(curr, step);
      vert.add(curr);
    }

    return vert;
  }

  /**
   * Строится срединный перпендикуляр к отрезку, лежащий в данной плоскости.
   * @param a
   * @param b
   * @param pl
   * @return направляющий вектор срединного перпендикуляра
   * @throws ExDegeneration
   */
  public static Vect3d midPerpendicularBisectorPlane(Vect3d a, Vect3d b, Plane3d pl)
          throws ExDegeneration {
    Vect3d ab = Vect3d.sub(b, a);
    Vect3d midAB = conv_hull(a, b, 0.5); //середина AB
    Plane3d pl1 = new Plane3d(ab, midAB);
    Line3d midPerpLine = pl.intersectionWithPlane(pl1); // получили серединный перпендикуляр
    Vect3d midPerp = midPerpLine.l();

    return midPerp;
  }

  /**
   * Строится срединный перпендикуляр к отрезку, лежащий в данной плоскости.
   * Возвращается прямая.
   * @param a
   * @param b
   * @param pl
   * @return прямая - срединный перпендикуляр.
   * @throws ExDegeneration
   */
  public static Line3d midLinesArePerpendicularToTheSegment(Vect3d a, Vect3d b, Plane3d pl)
          throws ExDegeneration {
    Vect3d ab = Vect3d.sub(b, a);
    Vect3d midAB = conv_hull(a, b, 0.5); //середина AB
    Plane3d pl1 = new Plane3d(ab, midAB);
    Line3d midPerpLine = pl.intersectionWithPlane(pl1); // получили серединный перпендикуляр

    return midPerpLine;
  }

  /**
   * Get the vector normal to the plane defined by three points.
   * Facet plane is considered the front if its vertices appear counterclockwise.
   * @param a 1st point
   * @param b 2nd point
   * @param c 3th point
   * @return Normalized normal vector
   */
  static public Vect3d getNormal3Points(Vect3d a, Vect3d b, Vect3d c){
    Vect3d ba = Vect3d.sub(a, b);
    Vect3d bc = Vect3d.sub(c, b);
    Vect3d norm = Vect3d.vector_mul(bc, ba);
    return norm;
  }

  /**
   * Check two vectors are equal with epsilon error.
   * @param o
   * @return true if they are equal and false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if( o == null )
      return false;
    
    if( o.getClass() != Vect3d.class )
      return false;
    
    Vect3d v = (Vect3d)o;
    return Checker.isEqual(_x, v.x()) &&
           Checker.isEqual(_y, v.y()) &&
           Checker.isEqual(_z, v.z());
  }

  @Override
  public int hashCode() {
    // auto-generated method
    int hash = 5;
    hash = 71 * hash + (int) (Double.doubleToLongBits(this._x) ^ (Double.doubleToLongBits(this._x) >>> 32));
    hash = 71 * hash + (int) (Double.doubleToLongBits(this._y) ^ (Double.doubleToLongBits(this._y) >>> 32));
    hash = 71 * hash + (int) (Double.doubleToLongBits(this._z) ^ (Double.doubleToLongBits(this._z) >>> 32));
    return hash;
  }

  /**
   * Check two vectors are equal.
   * @param v1
   * @param v2
   * @return true if they are equal and false otherwise
   */
  static public boolean equals(Vect3d v1, Vect3d v2){
    return v1.equals(v2);
  }

  /**
   * Check that two arrays of points are equal,
   * order is important.
   * @param v1
   * @param v2
   * @return
   */
  static public boolean equals(Vect3d[] v1, Vect3d[] v2){
    if (v1.length != v2.length)
      return false;
    for (int i = 0; i < v1.length; i++){
      if (!equals(v1[i], v2[i]))
        return false;
    }
    return true;
  }

  /**
   * Check that two lists of points are equal,
   * order is important.
   * @param v1
   * @param v2
   * @return
   */
  static public boolean equals(ArrayList<Vect3d> v1,  ArrayList<Vect3d> v2){
    if (v1.size() != v2.size())
      return false;
    for (int i = 0; i < v1.size(); i++){
      if (!v1.get(i).equals(v2.get(i)))
        return false;
    }
    return true;
  }

  /**
   * Метод сравнения точек с эпсилон-окрестностью
   * @param v1
   * @param v2
   * @param tolerance
   * @return
   */
  public static boolean equalsWithTolerance(Vect3d v1, Vect3d v2, double tolerance) {
        try {
            double diff = v1.x() - v2.x();
            if (Double.isNaN(diff)) {
                return false;
            }
            if ((diff < 0 ? -diff : diff) > tolerance) {
                return false;
            }
            diff = v1.y() - v2.y();
            if (Double.isNaN(diff)) {
                return false;
            }
            if ((diff < 0 ? -diff : diff) > tolerance) {
                return false;
            }
            diff = v1.z() - v2.z();
            if (Double.isNaN(diff)) {
                return false;
            }
            return (diff < 0 ? -diff : diff) <= tolerance;
        } catch (NullPointerException ex) {
            return false;
        }
    }

  /**
   * Проверяется являются ли одинаковыми списки векторов.
   * Векторы не обязательно идут в одном порядке.
   * Считается, что все векторы в массиве различны.
   * @param v1
   * @param v2
   * @return true, если два списка содержат один и тот же набор векторов,
   * идущих не обязательно в одном и том же порядке.
   * Считается, что все векторы в массиве различны.
   */
  static public boolean equalsAsSet(ArrayList<Vect3d> v1, ArrayList<Vect3d> v2){
    if (v1.size() != v2.size())
      return false;
    int numEqualPairs = 0;
    for (int i = 0; i < v1.size(); i++)
      for (int j = 0; j < v2.size(); j++)
        if (v1.get(i).equals(v2.get(j)))
          numEqualPairs++;
    return numEqualPairs == v1.size();
  }

  /**
   * Get the angle between the vectors
   * @param v1 1st vector
   * @param v2 2nd vector
   * @return angle between the vectors in radians (0, pi)
   */
  static public double getAngle(Vect3d v1, Vect3d v2){
    //!! TODO: везде ли подразумевается, что величина угла от 0 до PI, а не до 2PI?

    double cosAngle = Vect3d.inner_mul(v1, v2) / v1.norm() / v2.norm();
    // Due to errors in the calculation cosine of angle may be outside the range [-1, 1]
    if (cosAngle < -1)
      return Math.PI;
    if (cosAngle > 1)
      return 0;
    return Math.acos(cosAngle);
  }

  /**
   * Assuming that a, b, and p are collinear.
   * @param a
   * @param b
   * @param p
   * @return
   * @throws geom.ExDegeneration
   */
  static public double getMultiplierForHull2(Vect3d a, Vect3d b, Vect3d p)
    throws ExDegeneration {
    double ab = sub(a, b).norm();
    double bp = sub(b, p).norm();
    double ap = sub(a, p).norm();
    // Find a sign for multiplier
    if (Math.abs(ap + ab - bp) < Checker.eps()) {
      return - ap / ab;
    } else {
      return ap / ab;
    }
  }

  /**
   * Find affine homogeneous coordinates for q in system [a, b, c].
   * Assuming that a, b, c, and q are coplanar.
   * @param p1
   * @param p2
   * @param p3
   * @param q
   * @return
   * @throws geom.ExDegeneration
   */
  static public double[] getMultipliersForHull3(Vect3d p1, Vect3d p2, Vect3d p3, Vect3d q)
          throws ExDegeneration {
    {
      double det = Det23.calc(p2.x() - p1.x(), p3.x() - p1.x(), p2.y() - p1.y(), p3.y() - p1.y());
      if (det != 0) {
        double det1 = Det23.calc(q.x() - p1.x(), p3.x() - p1.x(), q.y() - p1.y(), p3.y() - p1.y());
        double det2 = Det23.calc(p2.x() - p1.x(), q.x() - p1.x(), p2.y() - p1.y(), q.y() - p1.y());
        return new double[] {det1 / det, det2 / det};
      }
    }
    {
      double det = Det23.calc(p2.x() - p1.x(), p3.x() - p1.x(), p2.z() - p1.z(), p3.z() - p1.z());
      if (det != 0) {
        double det1 = Det23.calc(q.x() - p1.x(), p3.x() - p1.x(), q.z() - p1.z(), p3.z() - p1.z());
        double det2 = Det23.calc(p2.x() - p1.x(), q.x() - p1.x(), p2.z() - p1.z(), q.z() - p1.z());
        return new double[] {det1 / det, det2 / det};
      }
    }
    {
      double det = Det23.calc(p2.y() - p1.y(), p3.y() - p1.y(), p2.z() - p1.z(), p3.z() - p1.z());
      if (det != 0) {
        double det1 = Det23.calc(q.y() - p1.y(), p3.y() - p1.y(), q.z() - p1.z(), p3.z() - p1.z());
        double det2 = Det23.calc(p2.y() - p1.y(), q.y() - p1.y(), p2.z() - p1.z(), q.z() - p1.z());
        return new double[] {det1 / det, det2 / det};
      }
    }
    throw new ExDegeneration("System is degenerate");
  }

  /**
   * Find the center of mass of the array of points (All the points have the same weight).
   * @param points An array of points to find the center of mass.
   * @return Center of mass.
   */
  static public Vect3d centerOfMass(final ArrayList<Vect3d> points){
    if (points.isEmpty())
      return null;
    if (points.size() == 1)
      return points.get(0).duplicate();
    Vect3d center = Vect3d.conv_hull(points.get(0), points.get(1), 0.5);
    for (int i = 2; i < points.size(); i++) {
      center = Vect3d.conv_hull(center, points.get(i), 1.0 / (i + 1));
    }
    return center;
  }

  /**
   * Find the center of mass of the array of points (All the points have the same weight).
   * @param args Points to find the center of mass.
   * @return Center of mass.
   */
  static public Vect3d centerOfMass(Vect3d ... args){
    ArrayList<Vect3d> points = new ArrayList<Vect3d>(Arrays.asList(args));
    return Vect3d.centerOfMass(points);
  }

  /**
   * Возвращает массив точек, получающийся из исходного массива удалением повторяющихся элементов
   * @param points
   * @return
   */
  public static ArrayList<Vect3d> withoutDuplicates(ArrayList<Vect3d> points){
    ArrayList<Vect3d> newPoints = new ArrayList<>();
    for( Vect3d p : points )
      addWithoutDuplicates(newPoints, p);
    return newPoints;
  }

  public static void addWithoutDuplicates(ArrayList<Vect3d> p1, Vect3d v){
    if(!p1.contains(v))
      p1.add(v);
  }

  public static void addWithoutDuplicates(ArrayList<Vect3d> p1, ArrayList<Vect3d> p2){
    for(int i = 0; i < p2.size(); i++){
      if(!p1.contains(p2.get(i)))
        p1.add(p2.get(i));
    }
  }

  @Override
  public String toString() {
    return toString(5, true);
  }

  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon() {
    ArrayList<Vect3d> res = new ArrayList<>();
    res.add(this);
    return res;
  }

  @Override
  public ArrayList<Vect3d> getAsAbstractPolygon(int numPoints) {
    return getAsAbstractPolygon();
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList<>();
    result.add(this);
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    return new Vect3d(points);
  }

  public ArrayList<Vect3d> intersect(Ray3d ray) {
    ArrayList<Vect3d> result = new ArrayList<>();
    if (ray.containsPoint(this)) {
      result.add(this.duplicate());
    }
    return result;
  }

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.VECT3D;
  }
}