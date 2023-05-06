package geom;

import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;

public class Octahedron3d implements i_Geom {
  private Vect3d _a, _b, _c, _d, _topu, _topd;
  private double _angle;

  /**
   * Constructor of octahedron by two vertices and angle
   * @param a 1st vertex
   * @param b 2nd vertex
   * @param angle rotate angle
   * @throws geom.ExGeom
   */
  public Octahedron3d(Vect3d a, Vect3d b, double angle) throws ExGeom {
    this._angle = angle;
    double d = Vect3d.dist(a, b); // расстояние между точками
    Polygon3d base = Polygon3d.regPolygonByTwoPoints(a, b, 4, angle); // основа октаэдра 4х угольник
    Vect3d center = base.inCircle().center();
    Plane3d plane = base.plane();
    Vect3d norm = plane.n();

    Vect3d topu = Vect3d.sum(Vect3d.mul(norm, d / Math.sqrt(2)), center);
    Vect3d topd = Vect3d.sum(Vect3d.mul(norm, -d / Math.sqrt(2)), center);

    ArrayList<Vect3d> arl = base.points();
    arl.add(topu);
    arl.add(topd);

    _a = arl.get(0);
    _b = arl.get(1);
    _c = arl.get(2);
    _d = arl.get(3);
    _topu = topu;
    _topd = topd;
  }

  /**
  * Constructor of octahedron by points
  */
  public Octahedron3d(ArrayList<Vect3d> points) {
    _a = points.get(0);
    _b = points.get(1);
    _c = points.get(2);
    _d = points.get(3);
    _topu = points.get(4);
    _topd = points.get(5);
    ArrayList<Vect3d> list = new ArrayList<>();
    list.add(points.get(0));
    list.add(points.get(1));
    list.add(points.get(2));
    list.add(points.get(3));
    Polygon3d p;
    try {
      p = new Polygon3d(list);
      _angle = Vect3d.getAngle(p.plane().n(), Vect3d.UZ);//тот ли угол?
    } catch (ExDegeneration ex) { }
  }

  /**
   * @return all faces of oktahedron
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> pol = new ArrayList<>();
    pol.add(new Polygon3d(_topu, _a, _b));
    pol.add(new Polygon3d(_topu, _b, _c));
    pol.add(new Polygon3d(_topu, _c, _d));
    pol.add(new Polygon3d(_topu, _d, _a));
    pol.add(new Polygon3d(_topd, _a, _b));
    pol.add(new Polygon3d(_topd, _b, _c));
    pol.add(new Polygon3d(_topd, _c, _d));
    pol.add(new Polygon3d(_topd, _d, _a));
    return pol;
  }

  /**
   * @return all ribs of octahedron
   * @throws ExDegeneration
   */
  public ArrayList<Rib3d> ribs() throws ExDegeneration {
    ArrayList<Rib3d> r = new ArrayList<>();
    r.add(new Rib3d(_topu, _a));
    r.add(new Rib3d(_topu, _b));
    r.add(new Rib3d(_topu, _c));
    r.add(new Rib3d(_topu, _d));
    r.add(new Rib3d(_topd, _a));
    r.add(new Rib3d(_topd, _b));
    r.add(new Rib3d(_topd, _c));
    r.add(new Rib3d(_topd, _d));
    r.add(new Rib3d(_a, _b));
    r.add(new Rib3d(_b, _c));
    r.add(new Rib3d(_c, _d));
    r.add(new Rib3d(_d, _a));
    return r;
  }

  /**
   * @return all vertices of octahedron
   */
  public ArrayList<Vect3d> points() {
    ArrayList<Vect3d> p = new ArrayList<>();
    p.add(_a);
    p.add(_b);
    p.add(_c);
    p.add(_d);
    p.add(_topu);
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
   * Center of octahedron
   * @return Vect3d object
   * @throws ExGeom
   */
  public Vect3d center() throws ExGeom {
    return Vect3d.mul(Vect3d.sum(_topd, _topu), 0.5);
  }

  /**
   * Radius of octahedron incircle
   * @return double value
   */
  public double inRadius() {
    return this.ribLength() * Math.sqrt(6) / 6;
  }

  /**
   * Radius of octahedron outcircle
   * @return double value
   */
  public double outRadius() {
    return this.ribLength() * Math.sqrt(2) / 2;
  }

  /**
   * @return octahedron insphere
   * @throws ExGeom
   */
  public Sphere3d inSphere() throws ExGeom{
    return new Sphere3d(center(), inRadius());
  }

  /**
   * @return octahedron outsphere
   * @throws ExGeom
   */
  public Sphere3d outSphere() throws ExGeom{
    return new Sphere3d(center(), outRadius());
  }

  /**
   * Face area of octahedron
   * @return area of regular triangle with side equals side of octahedron
   */
  public double faceArea() {
    return Math.pow(ribLength(), 2) * Math.sqrt(3) / 4;
  }

  /**
   * Surface area of octahedron
   * @return area of 8 regular triangles with side equals side of octahedron
   */
  public double surfaceArea() {
    return 2 * Math.pow(ribLength(), 2) * Math.sqrt(3);
  }

  /**
   * @return octahedron volume
   */
  public double volume() {
    return Math.sqrt(2) * Math.pow(ribLength(), 3) / 3;
  }

  /**
   *
   * @param line
   * @return Список точек пересечения октаэдра и прямой
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

  @Override
  public ArrayList<Vect3d> deconstr() {
    return this.points();
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    return new Octahedron3d(points);
 }

  /**
   * Construct section of octahedron by plane
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

  public ArrayList<Vect3d> intersect (Ray3d ray){
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

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.OCTAHEDRON3D;
  }
}
