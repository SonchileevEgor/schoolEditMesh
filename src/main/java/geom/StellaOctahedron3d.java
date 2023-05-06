
package geom;

import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;


public class StellaOctahedron3d implements i_Geom {
  private Vect3d _a, _b, _c, _d, _e, _f, _g, _h, _i, _j, _k, _l, _m, _n;
  
  public StellaOctahedron3d (Vect3d a, Vect3d b, double angle) throws ExGeom {
    Simplex3d tetr  = Simplex3d.regTetrahedronBy2PntsAngle(a, b, angle);
    double dist = Vect3d.dist(a, b) / 2;
    _a = tetr.a();
    _b = tetr.b();
    _c = tetr.c();
    _d = tetr.d();

    _f = _a.duplicate().add(_c.duplicate()).mul(0.5);
    _e = _a.duplicate().add(_d.duplicate()).mul(0.5);
    _j = _c.duplicate().add(_d.duplicate()).mul(0.5);
    _h = _d.duplicate().add(_b.duplicate()).mul(0.5);
    _g = _c.duplicate().add(_b.duplicate()).mul(0.5);
    _i = _a.duplicate().add(_b.duplicate()).mul(0.5);

    //найдем вершины tetr2
    Vect3d cb = Vect3d.sub(_c.duplicate(), _b.duplicate()).getNormalized();
    _k = Vect3d.sum(_e.duplicate(), cb.duplicate().mul(dist));
    _m = Vect3d.sum(_e.duplicate(), cb.duplicate().mul(-dist));

    Vect3d ca = Vect3d.sub(_c.duplicate(), _a.duplicate()).getNormalized();
    _l = Vect3d.sum(_h.duplicate(), ca.duplicate().mul(dist));

    Vect3d ad = Vect3d.sub(_a.duplicate(), _d.duplicate()).getNormalized();
    _n = Vect3d.sum(_g.duplicate(), ad.duplicate().mul(dist));
  }
    
    /**
   * @return all ribs of stella octahedron
   * @throws ExDegeneration
   */
  public ArrayList<Rib3d> ribs() throws ExDegeneration {
    ArrayList<Rib3d> r = new ArrayList<>();
    r.add(new Rib3d(_a, _e));
    r.add(new Rib3d(_a, _f));
    r.add(new Rib3d(_a, _i));
    r.add(new Rib3d(_e, _f));
    r.add(new Rib3d(_e, _i));
    r.add(new Rib3d(_f, _i));
    
    r.add(new Rib3d(_b, _i));
    r.add(new Rib3d(_b, _h));
    r.add(new Rib3d(_b, _g));
    r.add(new Rib3d(_i, _h));
    r.add(new Rib3d(_h, _g));
    r.add(new Rib3d(_g, _i));
    
    r.add(new Rib3d(_c, _f));
    r.add(new Rib3d(_c, _j));
    r.add(new Rib3d(_c, _g));
    r.add(new Rib3d(_j, _g));
    r.add(new Rib3d(_g, _f));
    r.add(new Rib3d(_f, _j));
    
    r.add(new Rib3d(_d, _e));
    r.add(new Rib3d(_d, _j));
    r.add(new Rib3d(_d, _h));
    r.add(new Rib3d(_j, _e));
    r.add(new Rib3d(_e, _h));
    r.add(new Rib3d(_h, _j));
    
    r.add(new Rib3d(_k, _e));
    r.add(new Rib3d(_k, _f));
    r.add(new Rib3d(_k, _j));
    
    r.add(new Rib3d(_l, _j));
    r.add(new Rib3d(_l, _g));
    r.add(new Rib3d(_l, _h));
    
    r.add(new Rib3d(_m, _i));
    r.add(new Rib3d(_m, _h));
    r.add(new Rib3d(_m, _e));
    
    r.add(new Rib3d(_n, _f));
    r.add(new Rib3d(_n, _i));
    r.add(new Rib3d(_n, _g));
    
    return r;
  }
  
  /**
   * @return all faces of stella octahedron
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> p = new ArrayList<>();
    p.add(new Polygon3d(_a, _e, _f));
    p.add(new Polygon3d(_a, _e, _i));
    p.add(new Polygon3d(_a, _f, _i));
    
    p.add(new Polygon3d(_b, _h, _i));
    p.add(new Polygon3d(_b, _h, _g));
    p.add(new Polygon3d(_b, _g, _i));
    
    p.add(new Polygon3d(_c, _j, _g));
    p.add(new Polygon3d(_c, _j, _f));
    p.add(new Polygon3d(_c, _f, _g));
    
    p.add(new Polygon3d(_d, _j, _h));
    p.add(new Polygon3d(_d, _j, _e));
    p.add(new Polygon3d(_d, _e, _h));
    
    p.add(new Polygon3d(_k, _e, _j));
    p.add(new Polygon3d(_k, _e, _f));
    p.add(new Polygon3d(_k, _f, _j));
    
    p.add(new Polygon3d(_l, _j, _g));
    p.add(new Polygon3d(_l, _j, _h));
    p.add(new Polygon3d(_l, _g, _h));
    
    p.add(new Polygon3d(_m, _i, _h));
    p.add(new Polygon3d(_m, _i, _e));
    p.add(new Polygon3d(_m, _e, _h));
    
    p.add(new Polygon3d(_n, _f, _g));
    p.add(new Polygon3d(_n, _f, _i));
    p.add(new Polygon3d(_n, _g, _i));

    return p;
  }

  /**
   * @return all vertices of stella octahedron
   */
  public ArrayList<Vect3d> points() {
    ArrayList<Vect3d> p = new ArrayList<>();
    p.add(_a);
    p.add(_b);
    p.add(_c);
    p.add(_d);
    p.add(_e);
    p.add(_f);
    p.add(_g);
    p.add(_h);
    p.add(_i);
    p.add(_j);
    p.add(_k);
    p.add(_l);
    p.add(_m);
    p.add(_n);

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
  
  public double surfaceArea() throws ExGeom {
    double surf_area = 0;
      for (int i = 0; i < this.faces().size(); i++)
        surf_area += this.faces().get(i).area();
    return surf_area;
  }
  
  public double ribLength(){
    return Vect3d.sub(_a, _b).norm();
  }
  
    /**
   *
   * @param line
   * @return Список точек пересечения звёздчатого октаэдра и прямой
   * @throws ExGeom
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExGeom {
    ArrayList<Polygon3d> faces = this.faces();
    ArrayList<Vect3d> points = new ArrayList<>();
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
   * Construct section of stella octahedron by plane
   * @param plane section plane
   * @return Polygon3d
   * @throws ExGeom
   * @throws geom.ExZeroDivision
   */
  public Polygon3d sectionByPlane(Plane3d plane) throws ExGeom, ExZeroDivision {
    Rib3d[] ribs = new Rib3d[this.ribs().size()];  
    for (int i = 0; i < this.ribs().size(); i++)
        ribs[i] = this.ribs().get(i);
    return sectionOfRibObject(ribs, plane);
  }
  
  @Override
  public boolean isOrientable() { return false; }

  @Override
  public ArrayList<Vect3d> deconstr() { return null; }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) { return null; }

  public ArrayList<Vect3d> intersect(Ray3d ray) { 
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
  public GeomType type() {
    return GeomType.STELLAR_OCTAHEDRON;
  }
}
