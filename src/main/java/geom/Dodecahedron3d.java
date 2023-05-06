package geom;

import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;

public class Dodecahedron3d implements i_Geom {

  private Vect3d _a, _b, _c, _d, _e, _f, _g, _h, _i, _j;
  private Vect3d _k, _l, _m, _n, _o, _p, _r, _s, _t, _u;
  private double _angle;

  /**
   * Constructor of dodecahedron by two vertices and angle
   * @param a 1st vertex
   * @param b 2nd vertex
   * @param angle rotate angle
   * @throws geom.ExGeom
   */
  public Dodecahedron3d(Vect3d a, Vect3d b, double angle) throws ExGeom {
    this._angle = angle;
    double r = radius(a, b);
    double R = 0;
    double h = 0; // расстояние от верхнего 5ника до след

    Polygon3d p1 = Polygon3d.regPolygonByTwoPoints(a, b, 5, angle);
    Circle3d circle1 = p1.outCircle();
    Vect3d center1 = circle1.center();
    ArrayList<Vect3d> list1 = p1.points(); // нижний 5ник
    ArrayList<Vect3d> list2 = new ArrayList<>(); // верхний 5ник
    ArrayList<Vect3d> List1 = new ArrayList<>(); // нижний большой 5ник
    ArrayList<Vect3d> List2 = new ArrayList<>(); // верхний большой 5ник
    ArrayList<Vect3d> listtmp = new ArrayList<>();

    for (int i = 0; i < list1.size() - 1; i++) {
      list2.add((Vect3d.sum(list1.get(i), list1.get(i + 1))).mul(0.5));
    }
    list2.add((Vect3d.sum(list1.get(4), list1.get(0))).mul(0.5));

    for (int i = 0; i < list2.size(); i++) {
      listtmp.add(center1.add((list2.get(i).sub(circle1.center()).getNormalized().mul(circle1.radiusLength()))));
    }

    list2.clear();
    for (int i = 0; i < listtmp.size(); i++) {
      list2.add(listtmp.get(i));
    }

    for (int i = 0; i < list2.size(); i++) {
      list2.get(i).inc_add(circle1.plane().n().mul(2 * r));
    }

    _a = list1.get(0);
    _b = list1.get(1);
    _c = list1.get(2);
    _d = list1.get(3);
    _e = list1.get(4);

    _f = list2.get(0);
    _g = list2.get(1);
    _h = list2.get(2);
    _i = list2.get(3);
    _j = list2.get(4);

    //------------------------------------------------
    R = Polygon3d.regPolygonByTwoPoints(_a, _c, 5, angle).outCircle().radiusLength(); // радиус большой окр
    h = Math.sqrt(Vect3d.dist(a, b) * Vect3d.dist(a, b) - (R - circle1.radiusLength()) * (R - circle1.radiusLength()));

    // расширяем поднимаем List1
    // нормализуем и умнoжаем
    for (int i = 0; i < list1.size(); i++) {
      List1.add(center1.add((list1.get(i).sub(circle1.center()).getNormalized().mul(R))));
    }
    // поднимаем
    for (int i = 0; i < List1.size(); i++) {
      List1.get(i).inc_add(circle1.plane().n().mul(h));
    }
    // добавляем
    _k = List1.get(0);
    _l = List1.get(1);
    _m = List1.get(2);
    _n = List1.get(3);
    _o = List1.get(4);

    // расширяем поднимаем List2
    // нормализуем и умнoжаем
    Polygon3d p2 = new Polygon3d(list2);
    Circle3d circle2 = p2.outCircle();
    for (int i = 0; i < list2.size(); i++) {
      List2.add(circle2.center().add((list2.get(i).sub(circle2.center()).getNormalized().mul(R))));
    }
    // поднимаем
    for (int i = 0; i < List2.size(); i++) {
      List2.get(i).inc_add(circle2.plane().n().mul(-h));
    }
    // добавляем
    _p = List2.get(0);
    _r = List2.get(1);
    _s = List2.get(2);
    _t = List2.get(3);
    _u = List2.get(4);

  }

   public Dodecahedron3d(ArrayList<Vect3d> points) {
    _a = points.get(0);
    _b = points.get(1);
    _c = points.get(2);
    _d = points.get(3);
    _e = points.get(4);

    _f = points.get(5);
    _g = points.get(6);
    _h = points.get(7);
    _i = points.get(8);
    _j = points.get(9);

    _k = points.get(10);
    _l = points.get(11);
    _m = points.get(12);
    _n = points.get(13);
    _o = points.get(14);

    _p = points.get(15);
    _r = points.get(16);
    _s = points.get(17);
    _t = points.get(18);
    _u = points.get(19);
    ArrayList<Vect3d> list = new ArrayList<>();
    list.add(points.get(0));
    list.add(points.get(1));
    list.add(points.get(2));
    list.add(points.get(3));
    list.add(points.get(4));
    Polygon3d p;
    try {
      p = new Polygon3d(list);
      _angle = Vect3d.getAngle(p.plane().n(), Vect3d.UZ);//тот ли угол?
    } catch (ExDegeneration ex) { }
  }

  public double radius(Vect3d a, Vect3d b) {
    return ((Vect3d.dist(a, b) / 4) * Math.sqrt(10 + (22 / Math.sqrt(5))));
  }

  /**
   * @return all faces of dodecahedron
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> f = new ArrayList<>();
    f.add(new Polygon3d(_a, _b, _c, _d, _e));
    f.add(new Polygon3d(_f, _g, _h, _i, _j));
    f.add(new Polygon3d(_a, _b, _l, _p, _k));
    f.add(new Polygon3d(_b, _c, _m, _r, _l));
    f.add(new Polygon3d(_c, _d, _n, _s, _m));
    f.add(new Polygon3d(_d, _e, _o, _t, _n));
    f.add(new Polygon3d(_e, _a, _k, _u, _o));
    f.add(new Polygon3d(_j, _f, _p, _k, _u));
    f.add(new Polygon3d(_f, _g, _r, _l, _p));
    f.add(new Polygon3d(_g, _h, _s, _m, _r));
    f.add(new Polygon3d(_h, _i, _t, _n, _s));
    f.add(new Polygon3d(_i, _j, _u, _o, _t));

    return f;
  }

  /**
   * @return all ribss of dodecahedron
   * @throws ExDegeneration
   */
  public ArrayList<Rib3d> ribs() throws ExDegeneration {
    ArrayList<Rib3d> r = new ArrayList<>();
    r.add(new Rib3d(_a, _b));
    r.add(new Rib3d(_b, _c));
    r.add(new Rib3d(_c, _d));
    r.add(new Rib3d(_d, _e));
    r.add(new Rib3d(_e, _a));
    r.add(new Rib3d(_f, _g));
    r.add(new Rib3d(_g, _h));
    r.add(new Rib3d(_h, _i));
    r.add(new Rib3d(_i, _j));
    r.add(new Rib3d(_j, _f));

    r.add(new Rib3d(_a, _k));
    r.add(new Rib3d(_b, _l));
    r.add(new Rib3d(_c, _m));
    r.add(new Rib3d(_d, _n));
    r.add(new Rib3d(_e, _o));
    r.add(new Rib3d(_f, _p));
    r.add(new Rib3d(_g, _r));
    r.add(new Rib3d(_h, _s));
    r.add(new Rib3d(_i, _t));
    r.add(new Rib3d(_j, _u));

    r.add(new Rib3d(_k, _p));
    r.add(new Rib3d(_p, _l));
    r.add(new Rib3d(_l, _r));
    r.add(new Rib3d(_r, _m));
    r.add(new Rib3d(_m, _s));
    r.add(new Rib3d(_s, _n));
    r.add(new Rib3d(_n, _t));
    r.add(new Rib3d(_t, _o));
    r.add(new Rib3d(_o, _u));
    r.add(new Rib3d(_u, _k));

    return r;
  }

  /**
   * @return all vertices of dodecahedron
   */
  public ArrayList<Vect3d> points() {
    ArrayList<Vect3d> p = new ArrayList<>();
    p.add(_a.duplicate());
    p.add(_b.duplicate());
    p.add(_c.duplicate());
    p.add(_d.duplicate());
    p.add(_e.duplicate());
    p.add(_f.duplicate());
    p.add(_g.duplicate());
    p.add(_h.duplicate());
    p.add(_i.duplicate());
    p.add(_j.duplicate());

    p.add(_k.duplicate());
    p.add(_l.duplicate());
    p.add(_m.duplicate());
    p.add(_n.duplicate());
    p.add(_o.duplicate());
    p.add(_p.duplicate());
    p.add(_r.duplicate());
    p.add(_s.duplicate());
    p.add(_t.duplicate());
    p.add(_u.duplicate());

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

  public Vect3d F() {
    return _f.duplicate();
  }

  public Vect3d G() {
    return _g.duplicate();
  }

  public Vect3d H() {
    return _h.duplicate();
  }

  public Vect3d I() {
    return _i.duplicate();
  }

  public Vect3d J() {
    return _j.duplicate();
  }

  public Vect3d K() {
    return _k.duplicate();
  }

  public Vect3d L() {
    return _l.duplicate();
  }

  public Vect3d M() {
    return _m.duplicate();
  }

  public Vect3d N() {
    return _n.duplicate();
  }

  public Vect3d O() {
    return _o.duplicate();
  }

  public Vect3d P() {
    return _p.duplicate();
  }

  public Vect3d Q() {
    return _r.duplicate();
  }

  public Vect3d R() {
    return _s.duplicate();
  }

  public Vect3d S() {
    return _t.duplicate();
  }

  public Vect3d T() {
    return _u.duplicate();
  }

  public double ribLength(){
    return Vect3d.sub(_a, _b).norm();
  }

  /**
   * Center of dodecahedron
   * @return Vect3d object
   * @throws ExGeom
   */
  public Vect3d center() throws ExGeom {
    Polygon3d poly1 = new Polygon3d(_a, _b, _c, _d, _e);
    Polygon3d poly2 = new Polygon3d(_f, _g, _h, _i, _j);
    Vect3d centerpoly1 = poly1.inCircle().center();
    Vect3d centerpoly2 = poly2.inCircle().center();
    return Vect3d.mul(Vect3d.sum(centerpoly1, centerpoly2), 0.5);
  }

  /**
   * Radius of dodecahedron incircle
   * @return double value
   */
  public double inRadius() {
    return this.ribLength() * Math.sqrt(10 + 22/Math.sqrt(5)) / 4;
  }

  /**
   * Radius of dodecahedron outcircle
   * @return double value
   */
  public double outRadius() {
    return this.ribLength() * Math.sqrt(3) * (1 + Math.sqrt(5)) / 4;
  }

  /**
   * @return dodecahedron insphere
   * @throws ExGeom
   */
  public Sphere3d inSphere() throws ExGeom{
    return new Sphere3d(center(), inRadius());
  }

  /**
   * @return dodecahedron outsphere
   * @throws ExGeom
   */
  public Sphere3d outSphere() throws ExGeom{
    return new Sphere3d(center(), outRadius());
  }

  /**
   * Face area of dodecahedron
   * @return area of regular 5-gon with side equals side of dodecahedron
   */
  public double faceArea() {
    return Math.sqrt(10 * Math.sqrt(5) + 25) * Math.pow(ribLength(), 2) / 4;
  }

  /**
   * Surface area of dodecahedron
   * @return area of 12 regular 5-gons with side equals side of dodecahedron
   */
  public double surfaceArea() {
    return 12 * faceArea();
  }

  /**
   * @return dodecahedron volume
   */
  public double volume() {
    return (15 + 7 * Math.sqrt(5)) * Math.pow(ribLength(), 3) / 4;
  }

  /**
   *
   * @param line
   * @return Список точек пересечения додекаэдра и прямой
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
   * Construct section of dodecahedron by plane
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
    return new Dodecahedron3d(points);
  }

  public ArrayList<Vect3d> intersect(Ray3d ray){
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
    return GeomType.DODECAHEDRON3D;
  }
}
