package geom;

import static geom.Checker.eps;
import static geom.Vect3d.inner_mul;
import static geom.Vect3d.sub;
import static geom.Vect3d.vector_mul;
import java.util.ArrayList;

/**
 * Класс, производящий вычисления с углами
 *
 * @author rita
 */
public class Angle3d implements i_Geom, i_OrientableGeom {
  private Vect3d _a, _b, _c; // b is vertex of angle
  private boolean _angleIsLessThanPI = true;

  /**
   * Constructor of angle by 3 vertices CAN NOT BE MORE THAN PI
   *
   * @param a point on first side
   * @param b vertex of angle
   * @param c point on second side
   */
  public Angle3d(Vect3d a, Vect3d b, Vect3d c) {
    _a = a;
    _b = b;
    _c = c;
  }

  public Angle3d() {
    this(new Vect3d(), new Vect3d(), new Vect3d());
  }

  /**
   * Constructor of angle by 3 vertices and eye CAN BE MORE THAN PI
   *
   * @param a point on first side
   * @param b vertex of angle
   * @param c point on second side
   * @param eye eye point
   */
  public Angle3d(Vect3d a, Vect3d b, Vect3d c, Vect3d eye) {
    this(a, b, c);
    _angleIsLessThanPI = isLessThanPI(eye);
  }

  /**
   * Constructor of angle by 3 vertices and flag
   *
   * @param a point on first side
   * @param b vertex of angle
   * @param c point on second side
   * @param isLessThanPI boolean flag which says angle is less or more than PI
   * true if angle is less than PI and false otherwise
   */
  public Angle3d(Vect3d a, Vect3d b, Vect3d c, boolean isLessThanPI) {
    this(a, b, c);
    _angleIsLessThanPI = isLessThanPI;
  }

  /**
   * Constructor of angle by points
   * @param points point list: (point on the first side; vertex; point on the second side)
   */
  public Angle3d(ArrayList<Vect3d> points) {
    this(points.get(0), points.get(1), points.get(2));
  }

  /**
   * Construct angle by 2 points and angle value
   *
   * @param a - vertex of the angle
   * @param b - point on side of the angle
   * @param angle - value of angle
   * @return angle on plane Oxy by vertex, point, value of angle
   */
  public static Angle3d angleByTwoPoints2D(Vect3d a, Vect3d b, double angle) {
    Vect3d n = new Vect3d(0, 0, 1);
    Vect3d vect_ab = Vect3d.sub(b, a);
    Vect3d point = Vect3d.sum(vect_ab.rotate(angle, n), a);
    Angle3d result = new Angle3d(b, a, point, n);
    return result;
  }

  /**
   * Check angle looks like less than PI from given eye
   *
   * @param eye point of eye
   * @return true if
   */
  public final boolean isLessThanPI(Vect3d eye) {
    /**
     * Проверяем, направлено ли векторное произведение сторон угла
     * в полупространство, где лежит глаз.
     * Если да, значит, угол меньше 180 градусов, если нет - больше.
     */
    Vect3d vectorMul = vector_mul(side1(), side2());
    return inner_mul(vectorMul, sub(eye, _b)) > eps();
  }

  /**
   * Is angle less than PI getter
   *
   * @return value of field _angleIsLessThanPI
   */
  public boolean isLessThanPI() {
    return _angleIsLessThanPI;
  }

  /**
   * Check angle is equal pi/2
   *
   * @return true if angle is right, false otherwise
   */
  public boolean isRight() {
    return Math.PI / 2 - eps() <= value() && value() <= Math.PI / 2 + eps();
  }

  /**
   * @return point on first side of angle
   */
  public Vect3d pointOnFirstSide() {
    return _a;
  }

  /**
   * @return angle vertex
   */
  public Vect3d vertex() {
    return _b;
  }

  /**
   * @return point on second side of angle
   */
  public Vect3d pointOnSecondSide() {
    return _c;
  }

  /**
   * first side of angle
   *
   * @return vector from angle vertex to 1st point
   */
  public Vect3d side1() {
    return sub(_a, _b);
  }

  /**
   * second side of angle
   *
   * @return vector from angle vertex to 2nd point
   */
  public Vect3d side2() {
    return sub(_c, _b);
  }

  /**
   * Angle normal.
   *
   * @return vector which is orthogonal plane of angle and has direction to eye
   * (with unit length)
   */
  public Vect3d normal() {
    Vect3d normal = vector_mul(side1(), side2()).getNormalized();
    return _angleIsLessThanPI ? normal : normal.mul(-1);
  }

  /**
   * Angle value
   *
   * @return double number from [0, 2*pi)
   */
  public double value() {
    try {
      return _angleIsLessThanPI ? arccos(cos(_a, _b, _c)) : 2 * Math.PI - arccos(cos(_a, _b, _c));
    } catch( ExDegeneration ex ){
      return 0;
    }
  }

  /**
   * Get the angle in degrees of angle in radians
   *
   * @param radians angle in radians
   * @return angle in degrees
   */
  static public double radians2Degree(double radians) {
    return radians * 180 / Math.PI;
  }

  /**
   * Get the angle in radians of angle in degrees
   *
   * @param degrees angle in degrees
   * @return angle in radians
   */
  static public double degree2Radians(double degrees) {
    return degrees / 180 * Math.PI;
  }

  /**
   * Find cosine of angle on 3 given points. Angle from [0, pi]
   *
   * @param a point on 1st side
   * @param b angle vertex
   * @param c point on 2nd side
   * @return cosine of angle from [0, pi]
   */
  public double cos(Vect3d a, Vect3d b, Vect3d c) {
    // Находим косинус угла из соответствующего треугольника по теореме косинусов
    double ab = Vect3d.dist(a, b);
    double bc = Vect3d.dist(b, c);
    double ac = Vect3d.dist(a, c);
    return (ab * ab + bc * bc - ac * ac) / (2 * ab * bc);
  }

  /**
   * Arcsine cover
   *
   * @param arg argument in [-1,1]
   * @return arcsine of argument
   * @throws ExDegeneration
   */
  static public double arcsin(double arg) throws ExDegeneration {
    if (arg > -1 + eps() && arg < 1 - eps()) {
      return Math.asin(arg);
    } else if (arg >= -1 - eps() && arg <= -1 + eps()) {
      return -Math.PI / 2;
    } else if (arg >= 1 - eps() && arg <= 1 + eps()) {
      return Math.PI / 2;
    } else {
      throw new ExDegeneration("Аргумент вне области определения арксинуса");
    }
  }

  /**
   * Arccosine cover
   *
   * @param arg argument in [-1,1]
   * @return arccosine of argument
   * @throws ExDegeneration
   */
  static public double arccos(double arg) throws ExDegeneration {
    if (arg > -1 + eps() && arg < 1 - eps()) {
      return Math.acos(arg);
    } else if (arg >= -1 - eps() && arg <= -1 + eps()) {
      return Math.PI;
    } else if (arg >= 1 - eps() && arg <= 1 + eps()) {
      return 0;
    } else {
      throw new ExDegeneration("Аргумент вне области определения арккосинуса");
    }
  }

  /**
   * Округлённое значение в радианах с заданной точностью.
   *
   * @param radians
   * @param presicion требуемая точность
   * @return
   */
  static public double getRoundedAngle(double radians, double presicion) {
    return ((double) Math.round(radians * 180 / presicion / Math.PI)) / 180 * presicion * Math.PI;
  }

  /**
   * Normalize angle to be in [-pi, pi) interval.
   * @param radians angular value (in radians).
   * @return
   */
  static public double toMinusPitoPiInterval(double radians) {
    double normAngle = radians % (2 * Math.PI);

    // force it to be the positive remainder, so that 0 <= angle < 360
    normAngle = (normAngle + (2 * Math.PI)) % (2 * Math.PI);

    // force into the minimum absolute value residue class, so that -180 < angle <= 180
    if (normAngle > Math.PI){
      normAngle -= (2 * Math.PI);
    }
    return normAngle;
  }

  /**
   * Normalize angle to be in (-180, 180] interval.
   * @param degrees angular value (in degrees).
   * @return
   */
  static public double toMinus180to180Interval(double degrees) {
    double normAngle = degrees % 360;

    // force it to be the positive remainder, so that 0 <= angle < 360
    normAngle = (normAngle + 360) % 360;

    // force into the minimum absolute value residue class, so that -180 < angle <= 180
    if (normAngle > 180){
      normAngle -= 360;
    }
    return normAngle;
  }

  /**
   * Bisectrix of angle.
   * @return
   * @throws ExDegeneration
   */
  public Line3d bisectrix() throws ExDegeneration {
    return Triang3d.bisectrix(_b, _a, _c).line();
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> result = new ArrayList();
    result.add(_a);
    result.add(_b);
    result.add(_c);
    return result;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    return new Angle3d(points);
  }

  public ArrayList<Vect3d> intersect(Ray3d ray) throws ExDegeneration {
    ArrayList<Vect3d> result = new ArrayList<>();
    ArrayList<Vect3d> intersectLine = new Rib3d(_a, _b).intersect(ray.line());
    intersectLine.addAll(new Rib3d(_c, _b).intersect(ray.line()));
    for (Vect3d pnt : intersectLine) {
      if (ray.containsPoint(pnt)) {
        result.add(pnt);
      }
    }
    return result;
  }

  public ArrayList<Vect3d> intersect(Circle3d circ) throws ExDegeneration {
    ArrayList<Vect3d> first = new Rib3d(_a, _b).intersect(circ);
    ArrayList<Vect3d> second = new Rib3d(_c, _b).intersect(circ);
    first.addAll(second);
    return first;
  }

  public ArrayList<Vect3d> intersect(Rib3d rib) throws ExGeom {
    ArrayList<Vect3d> first = new Rib3d(_a, _b).intersect(rib);
    ArrayList<Vect3d> second = new Rib3d(_c, _b).intersect(rib);
    first.addAll(second);
    return first;
  }

  public ArrayList<Vect3d> intersect(Line3d line) throws ExGeom {
    ArrayList<Vect3d> first = new Rib3d(_a, _b).intersect(line);
    ArrayList<Vect3d> second = new Rib3d(_c, _b).intersect(line);
    first.addAll(second);
    return first;
  }

  public ArrayList<Vect3d> intersect(Polygon3d poly) throws ExGeom {
    ArrayList<Vect3d> first = new Rib3d(_a, _b).intersect(poly);
    ArrayList<Vect3d> second = new Rib3d(_c, _b).intersect(poly);
    first.addAll(second);
    return first;
  }

  public ArrayList<Vect3d> intersect(Arc3d arc) throws ExGeom {
    ArrayList<Vect3d> first = arc.intersect(new Rib3d(_a, _b));
    ArrayList<Vect3d> second = arc.intersect(new Rib3d(_c, _b));
    first.addAll(second);
    return first;
  }

  public ArrayList<Vect3d> intersect(Triang3d tr) throws ExGeom {
    ArrayList<Vect3d> first = new Rib3d(_a, _b).intersect(tr);
    ArrayList<Vect3d> second = new Rib3d(_c, _b).intersect(tr);
    first.addAll(second);
    return first;
  }

  public ArrayList<Vect3d> intersect(Angle3d ang) throws ExGeom {
    ArrayList<Vect3d> first = new Rib3d(_a, _b).intersect(new Rib3d(ang._a, ang._b));

    ArrayList<Vect3d> second = new Rib3d(_a, _b).intersect(new Rib3d(ang._c, ang._b));
    first.addAll(second);

    second = new Rib3d(_c, _b).intersect(new Rib3d(ang._a, ang._b));
    first.addAll(second);

    second = new Rib3d(_c, _b).intersect(new Rib3d(ang._c, ang._b));
    first.addAll(second);

    return first;
  }

  @Override
  public Vect3d getUpVect() {
    return normal();
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.ANGLE3D;
  }
}
