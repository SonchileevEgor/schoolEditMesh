package geom;

import static geom.SectionOfRibObject.sectionOfRibObject;
import static geom.Vect3d.sum;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Math object prism
 * @author Knyazev Vladislav
 */
public class Prism3d implements i_Geom, i_OrientableGeom {
  /**
   * Vertices of prism in the following order:
   * base vertices (in the order of boundary of base)
   * another base first vertex
   * top vertex is correspondent with first vertex.
   */
  private ArrayList<Vect3d> _points;

  /**
   * Constructor of prism by 1st base points and point from 2nd  base point
   * @param points vertices of prism in the following order:
   * base vertices (in the order of boundary of base)
   * another base first vertex
   * top vertex is correspondent with first vertex
   * @throws ExDegeneration
   */
  public Prism3d(ArrayList<Vect3d> points)
          throws ExDegeneration {
    Vect3d top = points.get(points.size() - 1);
    points.remove(points.size() - 1);

    if (points.size() > 3){
      if (Checker.atLeast3PointsOneline(points))
        throw new ExDegeneration("точки основания лежат на одной прямой");
      if (!Checker.isCoplanar(points))
        throw new ExDegeneration("точки основания не лежат в одной плоскости");
    }
    //убедились, что точки основания не лежат на одной прямой, что лежат в одной плоскости

    //проверим, что основание является выпуклым
    if (!Checker.isConvexPolygon(points))
      throw new ExDegeneration("основание призмы не выпукло");

    //проверим, что вершина не лежит в плоскости основания
    ArrayList<Vect3d> CheckPoints = new ArrayList<Vect3d>();
    CheckPoints.add(top);
    CheckPoints.add(points.get(0));
    CheckPoints.add(points.get(1));
    CheckPoints.add(points.get(2));

    if (Checker.isCoplanar(CheckPoints))
      throw new ExDegeneration("вершина призмы лежит в плоскости основания");

    //добавим точки верхней плоскости
    Vect3d offset = Vect3d.sub(top, points.get(0));
    int s = points.size();
    for(int i=0; i<s;i++){
      points.add(Vect3d.sum(offset, points.get(i)));
    }
    _points = points;
  }

  /**
   * Construct regular prism by
   * @param v1 1st point
   * @param v2 2nd point
   * @param vertNum vertices number of base
   * @param h heigh
   * @param a angle
   * @return regular prism
   * @throws ExGeom
   */
  public static Prism3d regPrismBy2PntsHeightAngle(Vect3d v1, Vect3d v2, int vertNum, double h, double a)
          throws ExGeom {
    Polygon3d base = Polygon3d.regPolygonByTwoPoints(v1, v2, vertNum, a);
    Plane3d basePlane = new Plane3d(base.points().get(0), base.points().get(1), base.points().get(2));
    Vect3d height = Vect3d.rNormalizedVector(basePlane.n(), -h);
    Vect3d top = sum(base.points().get(0), height);
    return new Prism3d(top, base);
  }
  
    /**
   * Construct regular prism by
   * @param v1 1st point
   * @param v2 2nd point
   * @param a angle
   * @return regular prism
   * @throws ExGeom
   */
  public static Prism3d hexagonalPrismBy2PntsAngle(Vect3d v1, Vect3d v2, double a)
          throws ExGeom {
    Polygon3d base = Polygon3d.regPolygonByTwoPoints(v1, v2, 6, a);
    Plane3d basePlane = new Plane3d(base.points().get(0), base.points().get(1), base.points().get(2));
    Vect3d height = Vect3d.rNormalizedVector(basePlane.n(), -Vect3d.dist(v1, v2));
    Vect3d top = sum(base.points().get(0), height);
    return new Prism3d(top, base);
  }

  /**
   * Constructor of prism by 1st base polygon and point from 2nd pbase point
   * @param top point from 2nd base
   * @param base 1st base
   * @throws ExGeom
   */
  public Prism3d(Vect3d top, Polygon3d base) throws ExGeom {
    this(top, base, 0);
  }

  /**
   * Constructor of prism by 1st base polygon,
   * index of point of this polygon,
   * and the correspondent point from 2nd base.
   * @param top point from 2nd base
   * @param base 1st base
   * @param index
   * @throws ExGeom
   */
  public Prism3d(Vect3d top, Polygon3d base, int index)
    throws ExGeom {
    assert (index <= base.vertNumber() - 1) && (index >= 0);

    ArrayList<Vect3d> points = new ArrayList<Vect3d>();

    if (!base.isConvex())
      throw new ExGeom("Основание призмы невыпукло");
    if (base.plane().distFromPoint(top) < Checker.eps())
      throw new ExDegeneration("Вершина призмы лежит на основании");

    for (int i = index; i < base.vertNumber() + index; i++)
      points.add(base.points().get(i % base.vertNumber()));

    Vect3d offset = Vect3d.sub(top, points.get(0));

    int s = points.size();

    for (int i = 0; i < s; i++)
      points.add(Vect3d.sum(offset, points.get(i)));

    _points = points;
  }

  /**
   * Construct rectangle parallepiped by
   * @param v1 1st vertex
   * @param v2 2nd vertex
   * @param h height
   * @param w width
   * @param ang angle
   * @return Prism3d
   * @throws ExGeom
   * @throws ExDegeneration
   */
  public static Prism3d recParallepBy2PntsAngle(Vect3d v1, Vect3d v2, double h, double w, double ang)
          throws ExGeom {
    Polygon3d base = Polygon3d.rectangleBy2PntsAngle(v1, v2, w, ang);
    Plane3d basePlane = new Plane3d(base.points().get(0), base.points().get(1), base.points().get(2));
    Vect3d heigh = Vect3d.rNormalizedVector(basePlane.n(), -h);
    Vect3d top = sum(base.points().get(0), heigh);
    return new Prism3d(top, base);
  }

  /**
   * all vertices of prism in the following order:
   * base vertices (in the order of boundary of base)
   * another base first vertex
   * top vertex is correspondent with first vertex and other points of second base
   * @return all points of prism
   */
  public ArrayList<Vect3d> points(){ return _points; }

  /**
   * @return all faces of pyramid in the following order:
   * bases of pyramid, then lateral faces.
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> faces = new ArrayList<Polygon3d>();
    ArrayList<Vect3d> bottom_base = base1().points();
    ArrayList<Vect3d> top_base = base2().points();
    Collections.reverse(bottom_base);
    Vect3d p1 = new Vect3d(bottom_base.get(0));
    Vect3d p2 = new Vect3d(bottom_base.get(1));
    Vect3d p3 = new Vect3d(bottom_base.get(bottom_base.size()-1));
    Vect3d p4 = new Vect3d(top_base.get(0));
    Orientation bypass = Orientation.getBodyOrientation(p1, p2, p3, p4);
    if (bypass == Orientation.LEFT)
        Collections.reverse(bottom_base);
    if (bypass == Orientation.LEFT)
        Collections.reverse(top_base);
    Collections.reverse(top_base);
    faces.add(new Polygon3d(new ArrayList<>(top_base)));
    faces.add(new Polygon3d(new ArrayList<>(bottom_base)));
    Collections.reverse(bottom_base);
    int n = base1().points().size(); // number of vertices of base polygon
    for(int i = 0; i < n; i++){
      ArrayList<Vect3d> face = new ArrayList<Vect3d>();
      face.add(bottom_base.get((i + 1) % n));
      face.add(top_base.get((i + 1) % n));
      face.add(top_base.get(i % n));
      face.add(bottom_base.get(i % n));
      faces.add(new Polygon3d(new ArrayList<>(face)));
    }
    return faces;
  }

  /**
   * @param line
   * @return Список точек пересечения призмы и прямой
   * @throws ExGeom
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExGeom {
    ArrayList<Polygon3d> faces = this.faces();
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    for(int i=0; i<faces.size(); i++ )
      for(int j=0; j<faces().get(i).intersectWithLine(line).size(); j++)
        if(!points.contains(faces().get(i).intersectWithLine(line).get(j)))
           points.add(faces().get(i).intersectWithLine(line).get(j));

    return points;
  }

  /**
   * Return two bases of prism
   * @return array[2]
   * @throws ExGeom
   */
  public Polygon3d[] base() throws ExGeom {
    Polygon3d[] result = new Polygon3d[2];
    result[0] = base1();
    result[1] = base2();
    return result;
  }

  public Polygon3d base1() throws ExDegeneration {
    ArrayList<Vect3d> basePts = new ArrayList<>(_points.subList(0, _points.size() / 2));
    Collections.reverse(basePts);
    return new Polygon3d(basePts);
  }

  public Polygon3d base2() throws ExDegeneration {
    return new Polygon3d(new ArrayList<Vect3d>(_points.subList(_points.size() / 2, _points.size())));
  }

  /**
   * Construct section of pyramid by plane
   * @param plane section plane
   * @return Polygon3d
   * @throws ExGeom
   */
  //!! TODO: Как формируется массив ribs?
  public Polygon3d sectionByPlane(Plane3d plane) throws ExGeom {
    Rib3d[] ribs = new Rib3d[3 * _points.size() / 2];
    int j = 0;
    for(int i = 0; i < _points.size() / 2 - 1; i++){ //добавляем ребра основания
      ribs[i] = new Rib3d(_points.get(i), _points.get(i + 1));
      j++;
    }
    ribs[j] = new Rib3d(_points.get(0), _points.get(_points.size() / 2 - 1));
    j++;
    for(int i = _points.size() / 2 ;i < _points.size() - 1; i++){ //добавляем ребра верхней грани
      ribs[i]=new Rib3d(_points.get(i), _points.get(i+1));
      j++;
    }
    ribs[j]=new Rib3d(_points.get(_points.size() / 2), _points.get(_points.size() - 1));
    j++;
    for(int i = 0; i < _points.size() / 2; i++){ //добавляем боковые ребра
      ribs[j]=new Rib3d(_points.get(i), _points.get(i + _points.size()/2));
      j++;
    }
    return sectionOfRibObject(ribs, plane);
  }

   /**
   * constructs a circumscribed sphere of the prism if that's possible
   * otherwise throws an exception
   * @return
   * @throws ExGeom
   */
  public Sphere3d outSphere() throws ExGeom {
    if ((Checker.isOrthogonal(this.faces().get(0).plane().n(), this.faces().get(2).plane().n()))&&
            (Checker.isCircleOutPolygon(this.base1())))
    {
      Circle3d circle = this.base1().outCircle();
      Vect3d center = Vect3d.sum(circle.center(),this.base2().outCircle().center());
      center.inc_div(2);
      double rad = Vect3d.sub(center, this.points().get(0)).norm();
      Sphere3d result = new Sphere3d(center, rad);
      return result;
    }
    throw new ExGeom("невозможно описать сферу");
  }

  public Sphere3d inSphere() throws ExGeom{
    Plane3d base = new Plane3d(_points.get(0), _points.get(1), _points.get(2));
    Plane3d latpl = new Plane3d(_points.get(0), _points.get(_points.size()/2), _points.get(1));
    Plane3d bis1 = Plane3d.bisectorPlaneOf2Planes(base, latpl).get(1);
    latpl = new Plane3d(_points.get(1), _points.get(_points.size()/2+1), _points.get(2));
    Plane3d bis2 = Plane3d.bisectorPlaneOf2Planes(base, latpl).get(1);
    Line3d inters12 = bis1.intersectionWithPlane(bis2);
    latpl = new Plane3d(_points.get(0), _points.get(_points.size()/2-1), _points.get(_points.size()/2));
    Plane3d bis = Plane3d.bisectorPlaneOf2Planes(base, latpl).get(1);
    Vect3d center = inters12.intersectionWithPlane(bis);
    double radius = base.distFromPoint(center);
    ArrayList<Polygon3d> faces = this.faces();
    for (int i = 0; i < faces.size(); i++) {
      if (Math.abs(faces.get(i).plane().distFromPoint(center)-radius) > Checker.eps()){
        throw new ExGeom("невозможно вписать сферу");
      }
    }
    return new Sphere3d(center, radius);
  }

  /**
   * points are vertices of prism in the following order:
   * base vertices (in the order of boundary of base)
   * another base first vertex
   * top vertex is correspondent with first vertex
   */
  @Override
  public ArrayList<Vect3d> deconstr() {
    ArrayList<Vect3d> pts = new ArrayList<>();
    for(int i = 0; i < points().size()/2 + 1; i++)
      pts.add(points().get(i));
    return pts;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      return new Prism3d (points);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
    }
    return null;
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

  /**
   * Length of the height.
   * If prism is degenerate, return 0.
   *
   * @return
   * @author alexeev
   */
  public double heightLength() {
    try {
      return new Plane3d(_points.get(0), _points.get(1),
          _points.get(2)).distFromPoint(_points.get(_points.size() - 1));
    } catch (ExDegeneration ex) {
      return 0;
    }
  }

  /**
   * Full surface area.
   * If prism is degenerate, return 0.
   *
   * @return
   * @author alexeev
   */
  public double lateralSurfaceArea() {
    try {
      return base1().perimeter() * heightLength();
    } catch (ExGeom ex) {
      return 0;
    }
  }

  /**
   * Full surface area.
   * If prism is degenerate, return 0.
   *
   * @return
   * @author alexeev
   */
  public double surfaceArea() {
    try {
      return base1().area() * 2 + lateralSurfaceArea();
    } catch (ExGeom ex) {
      return 0;
    }
  }

  /**
   * Volume of prism.
   * If prism is degenerate, return 0.
   *
   * @return
   * @author alexeev
   */
  public double volume() {
    try {
      return base1().area() * heightLength();
    } catch (ExDegeneration ex) {
      return 0;
    }
  }

  @Override
  public Vect3d getUpVect() {
    try {
      return base1().getUpVect();
    } catch (ExDegeneration ex) {
      return new Vect3d(0, 0, 1);
    }
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.PRISM3D;
  }
}