package geom;

import java.util.ArrayList;
import static geom.Vect3d.*;
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

/**
 * Triangle in 3d.
 */
public class Triang3d extends Polygon3d {
  /**
   * Constructor of triangle by 3 vertexes.
   * @param a 1st vertex of triangle
   * @param b 2nd vertex of triangle
   * @param c 3rd vertex of triangle
   * @throws ExDegeneration
   *    if points lay on one line.
   */
  public Triang3d(Vect3d a, Vect3d b, Vect3d c) throws ExDegeneration {
    super(a, b, c);
  }

  /**
    * Constructor of triangle by 3 vertexes.
    * @param points vertexes of a triangle.
    * @throws ExDegeneration
    *    if points lay on one line.
    */
  public Triang3d(ArrayList<Vect3d> points) throws ExDegeneration {
    super(points);
  }

  /**
   * Construct rectangle triangle by 2 points, cathetus length and angle.
   * @param a 1st vertex
   * @param b vertex of right angle
   * @param cathLength length other side (not ab)
   * @param angle angle between default plane and plane which contains rectangle
   * @return rectangle (Polygon3d)
   * @throws ExDegeneration
   */
  public static Triang3d rectTriangleBy2PntsAngle(Vect3d a, Vect3d b, double cathLength, double angle) throws ExDegeneration {
    Plane3d plane = Plane3d.planeByTwoPoints(a, b, angle);
    Vect3d ba = sub(a, b);
    Vect3d cath = rNormalizedVector(ba.rotate(PI/2, plane.n()), cathLength);
    Vect3d c = sub(b, cath);
    return new Triang3d(a, b, c);
  }

  // getters for vertices
  public Vect3d a() { return _points.get(0); }
  public Vect3d b() { return _points.get(1); }
  public Vect3d c() { return _points.get(2); }

  // getters for sides
  public Rib3d ab() throws ExDegeneration { return new Rib3d(this.a(), this.b()); }
  public Rib3d bc() throws ExDegeneration { return new Rib3d(this.b(), this.c()); }
  public Rib3d ca() throws ExDegeneration { return new Rib3d(this.c(), this.a()); }

  public ArrayList<Rib3d> getSides() throws ExDegeneration {
    ArrayList<Rib3d> sides = new ArrayList<>();
    sides.add(ab());
    sides.add(bc());
    sides.add(ca());
    return sides;
  }

  @Override
  public ArrayList<Vect3d> points(){
    return new ArrayList<>(_points);
  }

  /**
   * Area of the triangle.
   * @return double
   * @author alexeev
   */
  @Override
  public double area() {
    double s_1 = Det23.calc(1, a().y(), a().z(), 1, b().y(), b().z(), 1, c().y(), c().z());
    double s_2 = Det23.calc(a().x(), 1, a().z(), b().x(), 1, b().z(), c().x(), 1, c().z());
    double s_3 = Det23.calc(a().x(), a().y(), 1, b().x(), b().y(), 1, c().x(), c().y(), 1);
    return 0.5 * sqrt(s_1 * s_1 + s_2 * s_2 + s_3 * s_3);
  }

  /**
   * @return plane of this triangle
   * @throws ExDegeneration
   */
  @Override
  public Plane3d plane() throws ExDegeneration{
    return new Plane3d(a(), b(), c());
  }

  /**
   * Find median from vertex A
   * @return Rib3d
   */
  public Rib3d medianeA() throws ExDegeneration{ return mediane(this.a(), this.b(), this.c()); }

  /**
   * Find median from vertex A
   * @return Rib3d
   */
  public Rib3d medianeB() throws ExDegeneration{ return mediane(this.b(), this.c(), this.a()); }

  /**
   * Find median from vertex A
   * @return Rib3d
   */
  public Rib3d medianeC() throws ExDegeneration{ return mediane(this.c(), this.a(), this.b()); }

  /**
   * Find bisectrix from vertex A
   * @return Rib3d
   */
  public Rib3d bisectrixA() throws ExDegeneration{ return bisectrix(this.a(), this.b(), this.c()); }

  /**
   * Find bisectrix from vertex B
   * @return Rib3d
   */
  public Rib3d bisectrixB() throws ExDegeneration{ return bisectrix(this.b(), this.c(), this.a()); }

  /**
   * Find bisectrix from vertex C
   * @return Rib3d
   */
  public Rib3d bisectrixC() throws ExDegeneration{ return bisectrix(this.c(), this.a(), this.b()); }

  /**
   * Find altitude from vertex A
   * @return Rib3d
   */
  public Rib3d heightA() throws ExDegeneration{ return height(this.a(), this.b(), this.c()); }

  /**
   * Find altitude from vertex b
   * @return Rib3d
   */
  public Rib3d heightB() throws ExDegeneration{ return height(this.b(), this.c(), this.a()); }

  /**
   * Find altitude from vertex C
   * @return Rib3d
   */
  public Rib3d heightC() throws ExDegeneration{ return height(this.c(), this.a(), this.b()); }

  /**
   * Find middle perpendicular of side AB
   * @return Line3d
   * @throws ExDegeneration
   */
  public Line3d midPerpAB() throws ExDegeneration{ return midPerp(this.a(), this.b(), this.c()); }

  /**
   * Find middle perpendicular of side BC
   * @return Line3d
   * @throws ExDegeneration
   */
  public Line3d midPerpBC() throws ExDegeneration{ return midPerp(this.b(), this.c(), this.a()); }

  /**
   * Find middle perpendicular of side CA
   * @return Line3d
   * @throws ExDegeneration
   */
  public Line3d midPerpCA() throws ExDegeneration{ return midPerp(this.c(), this.a(), this.b()); }

  /**
   * Find middle perpendicular of side AB
   * @return rib with vertices outcenter and middle of AB
   * @throws ExDegeneration
   */
  public Rib3d midPerpABRib() throws ExDegeneration{
    return new Rib3d(ab().innerPoint(0.5), outCenter());
  }

  /**
   * Find middle perpendicular of side BC
   * @return rib with vertices outcenter and middle of BC
   * @throws ExDegeneration
   */
  public Rib3d midPerpBCRib() throws ExDegeneration {
    return new Rib3d(bc().innerPoint(0.5), outCenter());
  }

  /**
   * Find middle perpendicular of side CA
   * @return rib with vertices outcenter and middle of CA
   * @throws ExDegeneration
   */
  public Rib3d midPerpCARib() throws ExDegeneration {
    return new Rib3d(ca().innerPoint(0.5), outCenter());
  }

  /**
   * @author rita
   * @return centroid (point of medians intersection)
   */
  public Vect3d centroid(){
    double centroidX = (this.a().x() + this.b().x() + this.c().x()) / 3;
    double centroidY = (this.a().y() + this.b().y() + this.c().y()) / 3;
    double centroidZ = (this.a().z() + this.b().z() + this.c().z()) / 3;
    return new Vect3d(centroidX, centroidY, centroidZ);
  }

  /**
   * @author rita
   * @param a vertex of triangle
   * @param b vertex of triangle
   * @param c vertex of triangle
   * @return centroid (point of medians intersection) by 3 points
   */
  public static Vect3d centroid(Vect3d a, Vect3d b, Vect3d c){
    double centroidX = (a.x() + b.x() + c.x()) / 3;
    double centroidY = (a.y() + b.y() + c.y()) / 3;
    double centroidZ = (a.z() + b.z() + c.z()) / 3;
    return new Vect3d(centroidX, centroidY, centroidZ);
  }

  /**
   * @return point of intersection of bisectrixes (incenter)
   * @throws ExDegeneration
   */
  public Vect3d inCenter() throws ExDegeneration {
    Line3d line1 = new Line3d(bisectrixA());
    Line3d line2 = new Line3d(bisectrixB());
    return line1.intersect(line2).get(0);
  }

  /**
   * @return point outcenter
   * @throws ExDegeneration
   */
  public Vect3d outCenter() throws ExDegeneration {
    return outCircle().center();
  }

  /**
   * @return point of intersection of altitudes (orthocenter)
   * @throws geom.ExDegeneration
   */
  public Vect3d ortoCenter() throws ExDegeneration {
    Line3d line1 = new Line3d(heightA());
    Line3d line2 = new Line3d(heightB());
    return line1.intersect(line2).get(0);
  }

  /**
   * @return center of escribed circle which touches bc
   * @throws ExDegeneration
   */
  public Vect3d exCenterA() throws ExDegeneration {
    return similarTriangForExCircle(this.a(), this.b(), this.c()).inCenter();
  }

    /**
   * @return center of escribed circle which touches ac
   * @throws ExDegeneration
   */
  public Vect3d exCenterB() throws ExDegeneration {
    return similarTriangForExCircle(this.b(), this.c(), this.a()).inCenter();
  }

    /**
   * @return center of escribed circle which touches ab
   * @throws ExDegeneration
   */
  public Vect3d exCenterC() throws ExDegeneration {
    return similarTriangForExCircle(this.c(), this.a(), this.b()).inCenter();
  }

  /**
   * Construct circumcircle for triangle
   * @return Circle3d
   * @throws ExDegeneration
   */
  @Override
  public Circle3d outCircle() throws ExDegeneration {
    return new Circle3d(this.a(), this.b(), this.c());
  }

  /**
   * Construct inscribed circle of the triangle.
   *
   * @return inscribed circle of the triangle.
   * @throws ExDegeneration if triangle is degenerate.
   */
  @Override
  public Circle3d inCircle() throws ExDegeneration {
    double ab = ab().length();
    double bc = bc().length();
    double ac = ca().length();
    Vect3d a1 = new Rib3d(c(), b()).innerPoint((ab + bc - ac) / bc / 2);
    Vect3d b1 = new Rib3d(a(), c()).innerPoint((bc + ac - ab) / ac / 2);
    Vect3d c1 = new Rib3d(b(), a()).innerPoint((ab + ac - bc) / ab / 2);
    return new Circle3d(a1, b1, c1);
  }

  /**
   * @return exCircle which touches bc
   * @throws ExDegeneration
   */
  public Circle3d exCircleA() throws ExDegeneration {
    return similarTriangForExCircle(this.a(), this.b(), this.c()).inCircle();
  }

  /**
   * @return exCircle which touches ac
   * @throws ExDegeneration
   */
  public Circle3d exCircleB() throws ExDegeneration {
    return similarTriangForExCircle(this.b(), this.c(), this.a()).inCircle();
  }

  /**
   * @return exCircle which touches ab
   * @throws ExDegeneration
   */
  public Circle3d exCircleC() throws ExDegeneration {
    return similarTriangForExCircle(this.c(), this.a(), this.b()).inCircle();
  }

  /**
   * @param line
   * @return
   * @throws ExDegeneration
   */
  @Override
  public ArrayList<Vect3d> intersectWithLine (Line3d line) throws ExDegeneration {
    ArrayList<Vect3d> points = new ArrayList<>();
    if(Checker.isLineParallelPlane(line, this.plane()) ||
            Checker.isLineLiesInPlane(line, this.plane()))
      return points;
    else {
      Vect3d p = line.intersectionWithPlane(this.plane());
      Polygon3d poly = new Polygon3d(this.points());
      if(Checker.isPointOnClosePolygon(poly, p)){
        points.add(p);
        return points;
      }
    }
    return points;
  }

  /**
   * @return perimeter of the triangle, the same as sideLength(0) calling
   */
  @Override
  public double perimeter(){ return sideLength(0); }

  /**
   * @param n 1 for side AB, 2 for side BC, 3 for side AC, 0 for perimeter
   * @return length of the corresponding side.
   */
  public double sideLength(int n) {
    switch (n) {
      case 0: return Vect3d.sub(a(), b()).norm() +
           Vect3d.sub(b(), c()).norm() +
           Vect3d.sub(a(), c()).norm();
      case 1: return Vect3d.sub(a(), b()).norm();
      case 2: return Vect3d.sub(b(), c()).norm();
      case 3: return Vect3d.sub(a(), c()).norm();
    }
    return 0;
  }

  /**
   * Check point belongs triangle
   * @param pnt point
   * @return true if point lie inside or on triangle boundary
   */
  public boolean contains(Vect3d pnt) {
    if (pnt.equals(a()) || pnt.equals(b()) || pnt.equals(c()))
      return true;
    try {
      Triang3d tr1 = new Triang3d(a(), b(), pnt);
      Triang3d tr2 = new Triang3d(a(), c(), pnt);
      Triang3d tr3 = new Triang3d(b(), c(), pnt);
      return Checker.isEqual(tr1.area() + tr2.area() + tr3.area(), this.area());
    } catch( ExDegeneration e ){
      return false;
    }
  }

  /**
   * Bisectrix from vertex a to side bc
   */
  static Rib3d bisectrix(Vect3d a, Vect3d b, Vect3d c) throws ExDegeneration {
    Rib3d ca = new Rib3d(c, a);
    Rib3d ab = new Rib3d(a, b);
    Rib3d bc = new Rib3d(b, c);
    double coef = ca.length() / (ca.length() + ab.length());
    Vect3d base = bc.innerPoint(coef); // base of bisectrix
    return new Rib3d(a, base);
  }

  // Median from vertex a to side bc
  private Rib3d mediane(Vect3d a, Vect3d b, Vect3d c) throws ExDegeneration {
    return new Rib3d(a, mul(sum(b, c), 0.5));
  }

  // Hieght from vertex a to side bc
  private Rib3d height(Vect3d a, Vect3d b, Vect3d c) throws ExDegeneration {
    Line3d lineBC = Line3d.getLineByTwoPoints(b, c);
    return new Rib3d(a, lineBC.projectOfPoint(a));
  }

  // Midperpendicular of side ab
  private Line3d midPerp(Vect3d a, Vect3d b, Vect3d c) throws ExDegeneration{
    Plane3d plane = new Plane3d(a, b, c);
    return midLinesArePerpendicularToTheSegment(a, b, plane);
  }

  private Triang3d similarTriangForExCircle(Vect3d a, Vect3d b, Vect3d c) throws ExDegeneration{
    double p = perimeter() / 2;
    double bc = Vect3d.dist(b, c);
    double coef = p / (p - bc);
    Vect3d b1 = sum(a, mul(sub(b, a), coef));
    Vect3d c1 = sum(a, mul(sub(c, a), coef));
    return new Triang3d(a, b1, c1);
  }

  public Vect3d centerOfMass(double m_a, double m_b, double m_c) {
    double sum_mass = m_a + m_b + m_c;
    double x_coor = (m_a*this.a().x() + m_b*this.b().x() + m_c*this.c().x())/sum_mass;
    double y_coor = (m_a*this.a().y() + m_b*this.b().y() + m_c*this.c().y())/sum_mass;
    double z_coor = (m_a*this.a().z() + m_b*this.b().z() + m_c*this.c().z())/sum_mass;
    return new Vect3d(x_coor, y_coor, z_coor);
  }

  @Override
  public boolean equals(Object o) {
    if( o == null )
      return false;
    
    if( getClass() != o.getClass() )
      return false;
      
    Triang3d tr = (Triang3d) o;
    return (tr.a().equals(a()) && (tr.b().equals(b())) && (tr.c().equals(c()))) ||
        (tr.b().equals(a()) && (tr.c().equals(b())) && (tr.a().equals(c()))) ||
        (tr.c().equals(a()) && (tr.a().equals(b())) && (tr.b().equals(c())));
  }

  @Override
  public int hashCode() {
    // auto-generated method
    int hash = 3;
    hash = 79 * hash + a().hashCode();
    hash = 79 * hash + b().hashCode();
    hash = 79 * hash + c().hashCode();
    return hash;
  }

  @Override
  public ArrayList<Polygon3d> faces() throws ExGeom {
      ArrayList<Polygon3d> faces = new ArrayList<>();
      ArrayList<Vect3d> face = new ArrayList<>();
      // FIXME: убрать это органичение.
      Vect3d o = new Vect3d(200000.0, 200000.0, 200000.0);
      Orientation bypass = Orientation.getBodyOrientation(o, this.a(), this.b(), this.c());
      if (bypass == Orientation.RIGHT) {
          face.add(this.a());
          face.add(this.b());
          face.add(this.c());
      }
      else {
          face.add(this.a());
          face.add(this.c());
          face.add(this.b());
      }
      faces.add(new Polygon3d(face));
      return faces;
  }

  @Override
  public Vect3d getUpVect() {
    try {
      return plane().n();
    } catch (ExDegeneration ex) {
      return new Vect3d(0, 0, 1);
    }
  }
}