package geom;

import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;

/**
 *
 * @author kaznin
 */
public class TruncatedOctahedron3d implements i_Geom {
  Vect3d _a, _b, _c, _d, _e, _f,
         _g, _h, _i, _j, _k, _l,
         _m, _n, _o, _p, _q, _r,
         _s, _t, _u, _v, _w, _x;

  /**
  * Constructor of TruncatedOctahedron by two vertices and angle
  * @param a 1st vertex
  * @param b 2nd vertex
  * @param angle rotate angle
  * @throws ExGeom
  */
  public TruncatedOctahedron3d(Vect3d a, Vect3d b, double angle) throws ExGeom {
    // построим октаэдр
    Vect3d tmpVect = Vect3d.sub(a, b).getNormalized();
    double dist = Vect3d.dist(a, b);
    Vect3d a1 = Vect3d.sum(a, tmpVect.mul(dist));
    Vect3d b1 = Vect3d.sum(b, tmpVect.mul(-dist));
    Octahedron3d oct = new Octahedron3d(a1, b1, angle);
    
    // будем его усекать
    Vect3d AB = Vect3d.sub(oct.A(), oct.B()).getNormalized();
    _b = Vect3d.sum(oct.B(), AB.mul(dist));
    _a = Vect3d.sum(oct.B(), AB.mul(2 * dist));

    Vect3d BC = Vect3d.sub(oct.B(), oct.C()).getNormalized();
    _d = Vect3d.sum(oct.C(), BC.mul(dist));
    _c = Vect3d.sum(oct.C(), BC.mul(2 * dist));

    Vect3d CD = Vect3d.sub(oct.C(), oct.D()).getNormalized();
    _f = Vect3d.sum(oct.D(), CD.mul(dist));
    _e = Vect3d.sum(oct.D(), CD.mul(2 * dist));

    Vect3d DA = Vect3d.sub(oct.D(), oct.A()).getNormalized();
    _h = Vect3d.sum(oct.A(), DA.mul(dist));
    _g = Vect3d.sum(oct.A(), DA.mul(2 * dist));

    // u = up, d = down
    Vect3d UA = Vect3d.sub(oct.topu(), oct.A()).getNormalized();
    _v = Vect3d.sum(oct.A(), UA.mul(dist));
    _l = Vect3d.sum(oct.A(), UA.mul(2 * dist));

     Vect3d UB = Vect3d.sub(oct.topu(), oct.B()).getNormalized();
    _t = Vect3d.sum(oct.B(), UB.mul(dist));
    _i = Vect3d.sum(oct.B(), UB.mul(2 * dist));

     Vect3d UC = Vect3d.sub(oct.topu(), oct.C()).getNormalized();
    _x = Vect3d.sum(oct.C(), UC.mul(dist));
    _j = Vect3d.sum(oct.C(), UC.mul(2 * dist));

     Vect3d UD = Vect3d.sub(oct.topu(), oct.D()).getNormalized();
    _r = Vect3d.sum(oct.D(), UD.mul(dist));
    _k = Vect3d.sum(oct.D(), UD.mul(2 * dist));


    Vect3d DA1 = Vect3d.sub(oct.topd(), oct.A()).getNormalized();
    _u = Vect3d.sum(oct.A(), DA1.mul(dist));
    _p = Vect3d.sum(oct.A(), DA1.mul(2 * dist));

     Vect3d DB = Vect3d.sub(oct.topd(), oct.B()).getNormalized();
    _s = Vect3d.sum(oct.B(), DB.mul(dist));
    _m = Vect3d.sum(oct.B(), DB.mul(2 * dist));

     Vect3d DC = Vect3d.sub(oct.topd(), oct.C()).getNormalized();
    _w = Vect3d.sum(oct.C(), DC.mul(dist));
    _n = Vect3d.sum(oct.C(), DC.mul(2 * dist));

     Vect3d DD = Vect3d.sub(oct.topd(), oct.D()).getNormalized();
    _q = Vect3d.sum(oct.D(), DD.mul(dist));
    _o = Vect3d.sum(oct.D(), DD.mul(2 * dist));
  }

  /**
   * @return all vertices of TruncatedOctahedron
   */
  public ArrayList<Vect3d> points() {
    ArrayList<Vect3d> p = new ArrayList<Vect3d>(){{
      add(_a); add(_b); add(_c); add(_d); add(_e); add(_f); add(_g); add(_h); add(_i);
      add(_j); add(_k); add(_l); add(_m); add(_n); add(_o); add(_p); add(_q); add(_r);
      add(_s); add(_t); add(_u); add(_v); add(_w); add(_x);
    }};
    
    return p;
  }

  /**
   * @return all faces of TruncatedOctahedron
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> p = new ArrayList<>();
    p.add(new Polygon3d(_k, _j, _i, _l));
    p.add(new Polygon3d(_b, _t, _c, _s));
    p.add(new Polygon3d(_r, _f, _q, _g));
    p.add(new Polygon3d(_o, _p, _m, _n));
    p.add(new Polygon3d(_v, _h, _u, _a));
    p.add(new Polygon3d(_x, _e, _w, _d));

    p.add(new Polygon3d(_l, _i, _t, _b, _a, _v));
    p.add(new Polygon3d(_l, _k, _r, _g, _h, _v));
    p.add(new Polygon3d(_p, _m, _s, _b, _a, _u));
    p.add(new Polygon3d(_p, _o, _q, _g, _h, _u));

    p.add(new Polygon3d(_i, _j, _x, _d, _c, _t));
    p.add(new Polygon3d(_j, _k, _r, _f, _e, _x));
    p.add(new Polygon3d(_n, _m, _s, _c, _d, _w));
    p.add(new Polygon3d(_m, _o, _q, _f, _e, _w));

    return p;
  }

  /**
   * @return all ribs of TruncatedOctahedron
   * @throws ExDegeneration
   */
  public ArrayList<Rib3d> ribs() throws ExDegeneration {
    ArrayList<Rib3d> r = new ArrayList<>();
    r.add(new Rib3d(_i, _j));
    r.add(new Rib3d(_j, _k));
    r.add(new Rib3d(_k, _l));
    r.add(new Rib3d(_l, _i));

    r.add(new Rib3d(_i, _t));
    r.add(new Rib3d(_j, _x));
    r.add(new Rib3d(_k, _r));
    r.add(new Rib3d(_l, _v));

    r.add(new Rib3d(_t, _b));
    r.add(new Rib3d(_b, _s));
    r.add(new Rib3d(_s, _c));
    r.add(new Rib3d(_c, _t));

    r.add(new Rib3d(_x, _d));
    r.add(new Rib3d(_d, _w));
    r.add(new Rib3d(_w, _e));
    r.add(new Rib3d(_e, _x));

    r.add(new Rib3d(_r, _f));
    r.add(new Rib3d(_f, _q));
    r.add(new Rib3d(_q, _g));
    r.add(new Rib3d(_g, _r));

    r.add(new Rib3d(_v, _h));
    r.add(new Rib3d(_h, _u));
    r.add(new Rib3d(_u, _a));
    r.add(new Rib3d(_a, _v));

    r.add(new Rib3d(_w, _n));
    r.add(new Rib3d(_s, _m));
    r.add(new Rib3d(_u, _p));
    r.add(new Rib3d(_q, _o));

    r.add(new Rib3d(_o, _p));
    r.add(new Rib3d(_p, _m));
    r.add(new Rib3d(_m, _n));
    r.add(new Rib3d(_n, _o));

    r.add(new Rib3d(_a, _b));
    r.add(new Rib3d(_d, _c));
    r.add(new Rib3d(_e, _f));
    r.add(new Rib3d(_g, _h));

    return r;
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
    return _q.duplicate();
  }
  
  public Vect3d R() {
    return _r.duplicate();
  }

  public Vect3d S() {
    return _s.duplicate();
  }

  public Vect3d T() {
    return _t.duplicate();
  }

  public Vect3d U() {
    return _u.duplicate();
  }

  public Vect3d V() {
    return _v.duplicate();
  }

  public Vect3d W() {
    return _w.duplicate();
  }

  public Vect3d X() {
    return _x.duplicate();
  }

  public double surfaceArea() {
    return 6 * Math.pow(this.ribLength(), 2) * (1 + 2 * Math.sqrt(3));
  }

  public double ribLength(){
    return Vect3d.sub(_a, _b).norm();
  }

  public double volume() {
    return 8 * Math.sqrt(2) * Math.pow(this.ribLength(), 3);
  }
    
  public ArrayList<Vect3d> intersect( Ray3d ray ){
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
   *
   * @param line
   * @return Список точек пересечения удл. додекаэдра и прямой
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
   * Construct section of truncated octahedron by plane
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
  public boolean isOrientable() { return false; }

  @Override
  public ArrayList<Vect3d> deconstr() {
    return points();
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    return null;
  }

  @Override
  public GeomType type() {
    return GeomType.OCTAHEDRON_TRUNCATED3D;
  }
}
