package geom;

import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;

/**
 *
 * @author ???
 */
public class ElongatedDodecahedron3d implements i_Geom {
  private Vect3d _a, _b, _c, _d, _e, _f,
                 _a1, _b1, _c1, _d1, _e1, _f1,
                 _h, _i, _j, _k, _l, _g;
  private double _angle;
  
  /**
  * Constructor of ElongatedDodecahedron by two vertices and angle
  * @param a 1st vertex
  * @param b 2nd vertex
  * @param angle rotate angle
  * @throws ExGeom
  */
  public ElongatedDodecahedron3d(Vect3d a, Vect3d b, double angle) throws ExGeom {
    _angle = angle;
    Polygon3d bottomBase = Polygon3d.regPolygonByTwoPoints(a, b, 6, angle);
    ArrayList<Vect3d> bottomPoints = bottomBase.points();
    Vect3d tmpNormal = bottomBase.inCircle().normal().getNormalized(); // 'временная' нормаль для окр. впис. в основание
    double baseDiametrIn = bottomBase.inCircle().radiusLength() * 2; // диаметр этой окружности
    double baseDiametrOut = bottomBase.outCircle().radiusLength() * 2;
    ArrayList<Vect3d> topPoints = new ArrayList<>(); // точки вверхнего основания
    // поднимаем точки нижнего основания выше
    for (int i = 0; i < bottomPoints.size(); i++)
        topPoints.add(Vect3d.sum(bottomPoints.get(i), tmpNormal.mul(-baseDiametrIn)));
   // точки вверхнего и нижнего оснований
    _a = bottomPoints.get(0);
    _b = bottomPoints.get(1);
    _c = bottomPoints.get(2);
    _d = bottomPoints.get(3);
    _e = bottomPoints.get(4);
    _f = bottomPoints.get(5);

    _a1 = topPoints.get(0);
    _b1 = topPoints.get(1);
    _c1 = topPoints.get(2);
    _d1 = topPoints.get(3);
    _e1 = topPoints.get(4);
    _f1 = topPoints.get(5);

    // это делается для того чтобы найди центр боковой грани (явл. 6угольником)
    Vect3d tmpPoint1 = Vect3d.sum(_a, _b).mul(0.5);
    Vect3d tmpPoint2 = Vect3d.sum(_a1, _b1).mul(0.5);
    Vect3d tmpPoint3 = Vect3d.sum(tmpPoint1, tmpPoint2).mul(0.5); // центр
    tmpNormal = Vect3d.sub(_b, _a).getNormalized(); // нормаль для сдвига
    // сдвигаем влево вправо от найденного центра на радиус опис. окр.
    double tmp = baseDiametrOut / 2;
    _h = Vect3d.sum(tmpPoint3, tmpNormal.mul(tmp));
    _g = Vect3d.sum(tmpPoint3, tmpNormal.mul(-tmp));

    // проделываем тоже самое только для парал. грани
    tmpPoint1 = Vect3d.sum(_d, _e).mul(0.5);
    tmpPoint2 = Vect3d.sum(_d1, _e1).mul(0.5);
    tmpPoint3 = Vect3d.sum(tmpPoint1, tmpPoint2).mul(0.5); // центр
    tmpNormal = Vect3d.sub(_d, _e).getNormalized(); // нормаль для сдвига
    // сдвигаем влево вправо от найденного центра на радиус опис. окр.
    _j = Vect3d.sum(tmpPoint3, tmpNormal.mul(tmp));
    _k = Vect3d.sum(tmpPoint3, tmpNormal.mul(-tmp));

    // поиск координат концов вытянутого додекаэдра
    tmpPoint1 = Vect3d.sum(_f1, _g).mul(0.5);
    double tmpDist = Vect3d.dist(_a1, tmpPoint1) * 2; // расстояние от центра ромба до его вершины
    tmpNormal = Vect3d.sub(tmpPoint1, _a1).getNormalized();
    _l = Vect3d.sum(_a1, tmpNormal.mul(tmpDist));
    // и другого
    tmpPoint1 = Vect3d.sum(_h, _c1).mul(0.5);
    tmpNormal = Vect3d.sub(tmpPoint1, _b1).getNormalized();
    _i = Vect3d.sum(_b1, tmpNormal.mul(tmpDist));
  }

  /**
   * @return all ribs of ElonagtedDodecahedron
   * @throws ExDegeneration
   */
  public ArrayList<Rib3d> ribs() throws ExDegeneration {
    ArrayList<Rib3d> r = new ArrayList<>();
    r.add(new Rib3d(_a, _b));
    r.add(new Rib3d(_b, _c));
    r.add(new Rib3d(_c, _d));
    r.add(new Rib3d(_d, _e));
    r.add(new Rib3d(_e, _f));
    r.add(new Rib3d(_f, _a));
    r.add(new Rib3d(_a1, _b1));
    r.add(new Rib3d(_b1, _c1));
    r.add(new Rib3d(_c1, _d1));
    r.add(new Rib3d(_d1, _e1));
    r.add(new Rib3d(_e1, _f1));
    r.add(new Rib3d(_f1, _a1));

    r.add(new Rib3d(_a, _g));
    r.add(new Rib3d(_g, _a1));
    r.add(new Rib3d(_b, _h));
    r.add(new Rib3d(_h, _b1));
    r.add(new Rib3d(_d, _j));
    r.add(new Rib3d(_j, _d1));
    r.add(new Rib3d(_e, _k));
    r.add(new Rib3d(_k, _e1));

    r.add(new Rib3d(_c, _i));
    r.add(new Rib3d(_h, _i));
    r.add(new Rib3d(_j, _i));
    r.add(new Rib3d(_c1, _i));

    r.add(new Rib3d(_f, _l));
    r.add(new Rib3d(_g, _l));
    r.add(new Rib3d(_f1, _l));
    r.add(new Rib3d(_k, _l));

    return r;
  }

 /**
 * @return all vertices of ElongatedDodecahedron
 */
  public ArrayList<Vect3d> points() {
    ArrayList<Vect3d> p = new ArrayList<>();
    p.add(_a);
    p.add(_b);
    p.add(_c);
    p.add(_d);
    p.add(_e);
    p.add(_f);
    p.add(_a1);
    p.add(_b1);
    p.add(_c1);
    p.add(_d1);
    p.add(_e);
    p.add(_f);

    p.add(_g);
    p.add(_h);
    p.add(_i);
    p.add(_j);
    p.add(_k);
    p.add(_l);

    return p;
  }

 /**
 * @return all faces of ElonagetedDodecahedron
 * @throws ExGeom
 */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> p = new ArrayList<>();
    p.add(new Polygon3d(_a, _b, _c, _d, _e, _f));
    p.add(new Polygon3d(_a1, _b1, _c1, _d1, _e1, _f1));
    p.add(new Polygon3d(_a, _b, _h, _b1, _a1, _g));
    p.add(new Polygon3d(_e, _d, _j, _d1, _e1, _k));
    p.add(new Polygon3d(_b, _c, _i, _h));
    p.add(new Polygon3d(_b1, _h, _i, _c1));
    p.add(new Polygon3d(_d, _c, _i, _j));
    p.add(new Polygon3d(_c1, _d1, _j, _i));
    p.add(new Polygon3d(_a, _f, _l, _g));
    p.add(new Polygon3d(_a1, _f1, _l, _g));
    p.add(new Polygon3d(_e1, _f1, _l, _k));
    p.add(new Polygon3d(_e, _f, _l, _k));

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

  public Vect3d F1() {
    return _f1.duplicate();
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

  public ArrayList<Vect3d> intersect(Ray3d ray) {
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
   * Construct section of elongate dodecahedron by plane
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
  
  public double surfaceArea() throws ExGeom {
      double surf_area = 0;
        for (int i = 0; i < this.faces().size(); i++)
            surf_area += this.faces().get(i).area();
      return surf_area;
  }
  
  public double ribLength(){
    return Vect3d.sub(_a, _b).norm();
  }

  @Override
  public boolean isOrientable() { return false; }

  @Override
  public ArrayList<Vect3d> deconstr() { return null; }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) { return null; }

  @Override
  public GeomType type() {
    return GeomType.DODECAHEDRON_ELONGATED3D;
  }
}