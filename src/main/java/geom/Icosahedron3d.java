package geom;

import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;

public class Icosahedron3d implements i_Geom {

  private Vect3d _a, _b, _c, _d, _e, _topu, _a1, _b1, _c1, _d1, _e1, _topd;
  private double _angle;

  /**
  * Constructor of icosahedron by two vertices and angle
  * @param a 1st vertex
  * @param b 2nd vertex
  * @param angle rotate angle
  * @throws ExGeom
  */
  public Icosahedron3d(Vect3d a, Vect3d b, double angle) throws ExGeom {
    _angle = angle;
    double h = 0; // высота от 5уголника до верхней вершины
    double r = radius(a, b); // радиус опис шара
    Polygon3d p1 = Polygon3d.regPolygonByTwoPoints(a, b, 5, angle);
    Circle3d circle1 = p1.outCircle();
    ArrayList<Vect3d> list1 = p1.points(); // нижний 5ник
    ArrayList<Vect3d> list2 = new ArrayList<>(); // верхний 5ник
    ArrayList<Vect3d> listtmp = new ArrayList<>();

    for (int i = 0; i < list1.size() - 1; i++) {
      list2.add((Vect3d.sum(list1.get(i), list1.get(i + 1))).mul(0.5));
    }
    list2.add((Vect3d.sum(list1.get(4), list1.get(0))).mul(0.5));

    for (int i = 0; i < list2.size(); i++) {
      listtmp.add(circle1.center().add((list2.get(i).sub(circle1.center()).getNormalized().mul(circle1.radiusLength()))));
    }

    list2.clear();
    for (int i = 0; i < listtmp.size(); i++) {
      list2.add(listtmp.get(i));
    }

    h = Math.sqrt(Vect3d.dist(a, b) * Vect3d.dist(a, b) - circle1.radiusLength() * circle1.radiusLength());
    _topu = circle1.center().add(circle1.plane().n().mul(h));

    _a = list1.get(0);
    _b = list1.get(1);
    _c = list1.get(2);
    _d = list1.get(3);
    _e = list1.get(4);

    // опускаем
    for (int i = 0; i < list2.size(); i++) {
      list2.get(i).inc_add(circle1.plane().n().mul(-2 * (r - h)));
    }

    _a1 = list2.get(0);
    _b1 = list2.get(1);
    _c1 = list2.get(2);
    _d1 = list2.get(3);
    _e1 = list2.get(4);

    Polygon3d p2 = new Polygon3d(list2);

    _topd = p2.outCircle().center().add(p2.plane().n().mul(-h));
  }

  /**
  * Constructor of icosahedron by points
  */
  public Icosahedron3d(ArrayList<Vect3d> points) {
    _topu = points.get(0);
    _a = points.get(1);
    _b = points.get(2);
    _c = points.get(3);
    _d = points.get(4);
    _e = points.get(5);

    _a1 = points.get(6);
    _b1 = points.get(7);
    _c1 = points.get(8);
    _d1 = points.get(9);
    _e1 = points.get(10);
    _topd = points.get(11);

    ArrayList<Vect3d> list = new ArrayList<>();
    list.add(points.get(1));
    list.add(points.get(2));
    list.add(points.get(3));
    list.add(points.get(4));
    list.add(points.get(5));
    Polygon3d p;
    try {
      p = new Polygon3d(list);
      _angle = Vect3d.getAngle(p.plane().n(), Vect3d.UZ);//тот ли угол?
    } catch (ExDegeneration ex) { }
  }

  public double radius(Vect3d a, Vect3d b) {
    return ((3 * Math.sqrt(5) * Vect3d.dist(a, b)) / (4 * Math.sqrt(3)));
  }

  /**
   * @return all ribs of icosahedron
   * @throws ExDegeneration
   */
  public ArrayList<Rib3d> ribs() throws ExDegeneration {
    ArrayList<Rib3d> r = new ArrayList<>();
    r.add(new Rib3d(_a, _b));
    r.add(new Rib3d(_b, _c));
    r.add(new Rib3d(_c, _d));
    r.add(new Rib3d(_d, _e));
    r.add(new Rib3d(_e, _a));
    r.add(new Rib3d(_topu, _a));
    r.add(new Rib3d(_topu, _b));
    r.add(new Rib3d(_topu, _c));
    r.add(new Rib3d(_topu, _d));
    r.add(new Rib3d(_topu, _e));

    r.add(new Rib3d(_a1, _b1));
    r.add(new Rib3d(_b1, _c1));
    r.add(new Rib3d(_c1, _d1));
    r.add(new Rib3d(_d1, _e1));
    r.add(new Rib3d(_e1, _a1));
    r.add(new Rib3d(_topd, _a1));
    r.add(new Rib3d(_topd, _b1));
    r.add(new Rib3d(_topd, _c1));
    r.add(new Rib3d(_topd, _d1));
    r.add(new Rib3d(_topd, _e1));

    r.add(new Rib3d(_a, _a1));
    r.add(new Rib3d(_a1, _b));
    r.add(new Rib3d(_b, _b1));
    r.add(new Rib3d(_b1, _c));
    r.add(new Rib3d(_c, _c1));
    r.add(new Rib3d(_c1, _d));
    r.add(new Rib3d(_d, _d1));
    r.add(new Rib3d(_d1, _e));
    r.add(new Rib3d(_e, _e1));
    r.add(new Rib3d(_e1, _a));

    return r;
  }

  /**
   * @return all faces of icosahedron
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> p = new ArrayList<>();
    p.add(new Polygon3d(_topu, _a, _b));
    p.add(new Polygon3d(_topu, _b, _c));
    p.add(new Polygon3d(_topu, _c, _d));
    p.add(new Polygon3d(_topu, _d, _e));
    p.add(new Polygon3d(_topu, _e, _a));

    p.add(new Polygon3d(_topd, _a1, _b1));
    p.add(new Polygon3d(_topd, _b1, _c1));
    p.add(new Polygon3d(_topd, _c1, _d1));
    p.add(new Polygon3d(_topd, _d1, _e1));
    p.add(new Polygon3d(_topd, _e1, _a1));

    p.add(new Polygon3d(_a, _a1, _b));
    p.add(new Polygon3d(_a1, _b, _b1));
    p.add(new Polygon3d(_b, _b1, _c));
    p.add(new Polygon3d(_b1, _c, _c1));
    p.add(new Polygon3d(_c, _c1, _d));
    p.add(new Polygon3d(_c1, _d, _d1));
    p.add(new Polygon3d(_d, _d1, _e));
    p.add(new Polygon3d(_d1, _e, _e1));
    p.add(new Polygon3d(_e, _e1, _a));
    p.add(new Polygon3d(_e1, _a, _a1));

    return p;
  }

  /**
   * @return all vertices of icosahedron
   */
  public ArrayList<Vect3d> points() {
    ArrayList<Vect3d> p = new ArrayList<>();
    p.add(_topu);
    p.add(_a);
    p.add(_b);
    p.add(_c);
    p.add(_d);
    p.add(_e);
    p.add(_a1);
    p.add(_b1);
    p.add(_c1);
    p.add(_d1);
    p.add(_e1);
    p.add(_topd);

    return p;
  }

  public Vect3d A() {
    return _a.duplicate();
  }

  public Vect3d B() {
    return _b.duplicate();
  }

  public Vect3d C() {
    return _c.duplicate();
  }

  public Vect3d D() {
    return _d.duplicate();
  }

  public Vect3d E() {
    return _e.duplicate();
  }

  public Vect3d A1() {
    return _a1.duplicate();
  }

  public Vect3d B1() {
    return _b1.duplicate();
  }

  public Vect3d C1() {
    return _c1.duplicate();
  }

  public Vect3d D1() {
    return _d1.duplicate();
  }

  public Vect3d E1() {
    return _e1.duplicate();
  }

  public Vect3d topu() {
    return _topu.duplicate();
  }

  public Vect3d topd() {
    return _topd.duplicate();
  }

  public double ribLength(){
    return Vect3d.sub(_a, _b).norm();
  }

  /**
   * Center of icosahedron
   * @return Vect3d object
   * @throws ExGeom
   */
  public Vect3d center() throws ExGeom {
    return Vect3d.mul(Vect3d.sum(_topd, _topu), 0.5);
  }
  /**
   * Radius of icosahedron incircle
   * @return double value
   */
  public double inRadius() {
    return this.ribLength() * (3 + Math.sqrt(5)) / 4 / Math.sqrt(3);
  }

  /**
   * Radius of icosahedron outcircle
   * @return double value
   */
  public double outRadius() {
    return this.ribLength() * Math.sqrt(10 + 2 * Math.sqrt(5)) / 4;
  }

  /**
   * @return icosahedron insphere
   * @throws ExGeom
   */
  public Sphere3d inSphere() throws ExGeom {
    return new Sphere3d(center(), inRadius());
  }

  /**
   * @return icosahedron outsphere
   * @throws ExGeom
   */
  public Sphere3d outSphere() throws ExGeom{
    return new Sphere3d(center(), outRadius());
  }

  /**
   * Face area of icosahedron
   * @return area of regular triangle with side equals side of icosahedron
   */
  public double faceArea() {
    return Math.pow(ribLength(), 2) * Math.sqrt(3) / 4;
  }

  /**
   * Surface area of icosahedron
   * @return area of 20 regular triangles with side equals side of icosahedron
   */
  public double surfaceArea() {
    return 5 * Math.pow(ribLength(), 2) * Math.sqrt(3);
  }

  /**
   * @return icosahedron volume
   */
  public double volume() {
    return (15 + 5 * Math.sqrt(5)) * Math.pow(ribLength(), 3) / 12;
  }

  /**
   *
   * @param line
   * @return Список точек пересечения икосаэдра и прямой
   * @throws ExGeom
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExGeom {
    ArrayList<Polygon3d> faces = this.faces();
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    for(int i = 0; i < faces.size(); i++) {
      for(int j = 0; j < faces().get(i).intersectWithLine(line).size(); j++) {
        if(!points.contains(faces().get(i).intersectWithLine(line).get(j))) {
          points.add(faces().get(i).intersectWithLine(line).get(j));
        }
      }
    }
    return points;
  }
  
    /**
   * Construct section of icosahedron by plane
   * @param plane section plane
   * @return Polygon3d
   * @throws ExGeom
   */
  public Polygon3d sectionByPlane(Plane3d plane) throws ExGeom {
    Rib3d[] ribs = new Rib3d[this.ribs().size()];  
    for (int i = 0; i < this.ribs().size(); i++)
        ribs[i] = this.ribs().get(i);
    return sectionOfRibObject(ribs, plane);
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    return this.points();
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    return new Icosahedron3d(points);
  }

  public ArrayList<Vect3d> intersect (Ray3d ray){
    ArrayList<Vect3d> result = new ArrayList<Vect3d>();
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

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.ICOSAHEDRON3D;
  }
}
