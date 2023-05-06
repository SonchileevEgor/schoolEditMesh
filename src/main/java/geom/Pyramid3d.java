package geom;


import static geom.SectionOfRibObject.sectionOfRibObject;
import static geom.Vect3d.sum;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Math object pyramid.
 *
 * @author Knyazev Vladislav
 */
public class Pyramid3d implements i_Geom, i_OrientableGeom {
  /**
   * Vertices of pyramid in the following order:
   * base vertices (in the order of boundary of base), top vertex
   * top vertex is the last item of list.
   */
  private ArrayList<Vect3d> _points;

  /**
   * Constructor of pyramid by all vertices
   * @param points vertices of pyramid in the following order:
   * base vertices (in the order of boundary of base), top vertex
   * top vertex is the last item of list
   * @throws ExDegeneration
   */
  public Pyramid3d (ArrayList<Vect3d> points) throws ExDegeneration {
    Vect3d top = points.get(points.size() - 1);
    points.remove(points.size() - 1);

    if(points.size() > 3){
      if(geom.Checker.atLeast3PointsOneline(points))
        throw new ExDegeneration("вершины основания лежат на одной прямой");
      if(!Checker.isCoplanar(points))
        throw new ExDegeneration("вершины основания не лежат в одной плоскости");
    }
    // убедились, что точки основания не лежат на одной прямой, что лежат в одной плоскости

    // проверим, что основание является выпуклым
    if(!Checker.isConvexPolygon(points))
      throw new ExDegeneration("основание призмы не выпукло");

    // проверим, что вершина не лежит в проскости основания
    ArrayList<Vect3d> checkPoints = new ArrayList<>();
    checkPoints.add(top);
    checkPoints.add(points.get(0));
    checkPoints.add(points.get(1));
    checkPoints.add(points.get(2));

    if(Checker.isCoplanar(checkPoints))
      throw new ExDegeneration("вершина лежит в плоскости основания");

    points.add(top);
    _points = points;
  }

  public Pyramid3d(Vect3d a, Vect3d b, Vect3d c, Plane3d pl, int n) throws ExGeom {
    if(Math.abs(pl.distFromPoint(c)) < Checker.eps())
      throw new ExDegeneration("вершина лежит в плоскости основания");
    ArrayList<Vect3d> points = Vect3d.getRegularPolygonPoints(a, b, pl, n);
    points.add(c);
    _points = points;
  }

  /**
   * Constructor of pyramid by top vertex and base polygon
   * @param top top vertex
   * @param base base polygon
   * @throws ExGeom
   */
  public Pyramid3d(Vect3d top, Polygon3d base) throws ExGeom {
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();

    if(!base.isConvex())
      throw new ExGeom("основание невыпукло");
    if(base.plane().distFromPoint(top) < Checker.eps())
      throw new ExDegeneration("вершина пирамиды лежит в плоскости основания");
    for(int i = 0; i < base.vertNumber(); i++)
      points.add(base.points().get(i));

    points.add(top);
    _points = points;
  }

  /**
   * Construct regular pyramid by
   * @param v1 1st point
   * @param v2 2nd point
   * @param vertNum vertices number of base
   * @param h heigh
   * @param a angle
   * @return regular pyramid
   * @throws ExGeom
   */
  public static Pyramid3d regPyramidBy2PntsHeightAngle(Vect3d v1, Vect3d v2, int vertNum, double h, double a)
          throws ExGeom {
      Polygon3d base = Polygon3d.regPolygonByTwoPoints(v1, v2, vertNum, a);
      Vect3d baseCenter = base.outCircle().center();
      Plane3d basePlane = new Plane3d(base.points().get(0), base.points().get(1), base.points().get(2));
      Vect3d heigh = Vect3d.rNormalizedVector(basePlane.n(), -h);
      Vect3d top = sum(baseCenter, heigh);
      return new Pyramid3d(top, base);
  }

  
    /**
   * Construct rectangular pyramid by
   * @param poly - base pyramid
   * @param vertNum number of point - base heigh
   * @param h heigh
   * @return rectangle pyramid
   * @throws ExGeom
   */
  public static Pyramid3d rectangPyramidByPoly(Polygon3d poly, int vertNum, double h)
          throws ExGeom {
      Vect3d baseCenter = poly.points().get(vertNum);
      Plane3d basePlane = new Plane3d(poly.points().get(0), poly.points().get(1), poly.points().get(2));
      
      Vect3d heigh = Vect3d.rNormalizedVector(basePlane.n(), -h);
      Vect3d top = sum(baseCenter, heigh);
      
      return new Pyramid3d(top, poly);
  }
  /**
   * Vertices of pyramid in the following order:
   * base vertices (in the order of boundary of base), top vertex
   * top vertex is the last item of list
   * @return list of all points
   */
  public ArrayList<Vect3d> points(){return _points;}

  /**
   * @return all faces of pyramid in the following order:
   * base of pyramyd, then triangles.
   * First vertex of every triangle is pyramid vertex.
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> faces = new ArrayList<Polygon3d>();
    ArrayList<Vect3d> base = new ArrayList<>(base().points());
    Vect3d p1 = new Vect3d(base.get(0));
    Vect3d p2 = new Vect3d(base.get(1));
    Vect3d p3 = new Vect3d(base.get(base.size() - 1));
    Vect3d p4 = new Vect3d(top());
    Orientation bypass = Orientation.getBodyOrientation(p1, p2, p3, p4);
    if (bypass == Orientation.LEFT)
        Collections.reverse(base);
    faces.add(new Polygon3d(new ArrayList<>(base)));
    int n = base.size();// number of vertices of base polygon
    for(int i = 0; i < n; i++){
      ArrayList<Vect3d> face = new ArrayList<Vect3d>();
        face.add(top());
      face.add(base.get((i+1) % n));
      face.add(base.get(i % n));

      faces.add(new Polygon3d(new ArrayList<>(face)));
    }
    return faces;
  }

  /**
   *
   * @param line
   * @return Список точек пересечения пирамиды и прямой
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
   * @return base polygon
   * @throws geom.ExDegeneration
   */
  public Polygon3d base() throws ExDegeneration {
    ArrayList<Vect3d> base = new ArrayList<Vect3d>();
    for(int i = 0; i < _points.size() - 1; i++) {
      base.add(_points.get(i));
    }
    return new Polygon3d(base);
  }

  /**
   * @return top vertices
   */
  public Vect3d top() {
    return _points.get(_points.size() - 1);
  }

  /**
   * @return number of all vertices
   */
  public int vertNumber() {
    return _points.size();
  }

  /**
   * Height of the pyramid.
   * @return heightLength length, or 0 (if got errors)
   * @author alexeev
   */
  public double heightLength() {
    try {
      return base().plane().distFromPoint(top());
    } catch (ExGeom ex) {
      return 0;
    }
  }

  /**
   * Volume of the pyramid.
   * @return volume, or 0 (if got errors)
   * @author alexeev.
   */
  public double volume() {
    try {
      return base().area() * heightLength() / 3;
    } catch (ExGeom ex) {
      return 0;
    }
  }

  /**
   * Area of the lateral surface.
   *
   * @return
   * @author alexeev
   */
  public double lateralSurfaceArea() {
    double result = 0;
    int numBaseVert = vertNumber() - 1;
    Vect3d top = top();
    for (int i = 0; i < numBaseVert; i++) {
      result += Vect3d.vector_mul(Vect3d.sub(top, _points.get(i)),
          Vect3d.sub(top, _points.get((i + 1) % numBaseVert))).norm();
    }
    return result / 2;
  }

  /**
   * Full surface area.
   * If degenerate, returns 0.
   *
   * @return
   * @author alexeev
   */
  public double surfaceArea() {
    try {
      return base().area() + lateralSurfaceArea();
    } catch (ExGeom ex) {
      return 0;
    }
  }

  /**
   * Construct section of pyramid by plane
   * @param plane section plane
   * @return Polygon3d
   * @throws ExGeom
   */
  //!! TODO: Как формируется массив ribs?
  public Polygon3d sectionByPlane(Plane3d plane) throws ExGeom {
    Rib3d[] ribs = new Rib3d[2*(_points.size()-1)];
    int j = 0;
    for(int i = 0; i < _points.size() - 2; i++){ //добавляем ребра основания
      ribs[i] = new Rib3d(_points.get(i), _points.get(i+1));
      j++;
    }
    ribs[j] = new Rib3d(_points.get(0), _points.get(_points.size() - 2));
    j++;
    for(int i = 0; i < _points.size() - 1; i++){ //добавляем боковые ребра
      ribs[j] = new Rib3d(_points.get(i), _points.get(_points.size() - 1));
      j++;
    }
    return sectionOfRibObject(ribs, plane);
  }

  /**
   * constructs a circumscribed sphere of the pyramid if that's possible
   * otherwise throws an exception
   * @return
   * @throws ExGeom
   */
  public Sphere3d outSphere() throws ExGeom {
    Sphere3d result = new Sphere3d(_points.get(0), _points.get(1), _points.get(2), top());
    for(int i = 3; i < _points.size(); i++) {
      if(Math.abs(result.radius() - Vect3d.dist(result.center(), _points.get(i))) > Checker.eps()) {
        throw new ExGeom("невозможно описать сферу");
      }
    }
    return result;
  }

  /**
   * constructs a inscribed sphere of the pyramid if that's possible
   * otherwise throws an exception
   * @return sphere
   * @throws ExGeom
   */
  public Sphere3d inSphere() throws ExGeom {
    Plane3d base = new Plane3d(_points.get(0), _points.get(1), _points.get(2));
    Plane3d latpl = new Plane3d(_points.get(0), _points.get(_points.size()-1), _points.get(1));
    Plane3d bis1 = Plane3d.bisectorPlaneOf2Planes(base, latpl).get(1);
    latpl = new Plane3d(_points.get(1), _points.get(_points.size()-1), _points.get(2));
    Plane3d bis2 = Plane3d.bisectorPlaneOf2Planes(base, latpl).get(1);
    Line3d inters12 = bis1.intersectionWithPlane(bis2);
    latpl = new Plane3d(_points.get(0), _points.get(_points.size()-2), _points.get(_points.size()-1));
    Plane3d bis = Plane3d.bisectorPlaneOf2Planes(base, latpl).get(1);
    Vect3d center = inters12.intersectionWithPlane(bis);
    double radius = base.distFromPoint(center);
    for (int i = 3; i < _points.size()-1; i++) {
      latpl = new Plane3d(_points.get(i), _points.get(i-1), _points.get(_points.size()-1));
      if (Math.abs(latpl.distFromPoint(center)-radius) > Checker.eps()){
        throw new ExGeom("невозможно вписать сферу");
      }
    }
    return new Sphere3d(center, radius);
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    return _points;
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    Pyramid3d pyr = null;
    try {
      pyr = new Pyramid3d(points);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
    }
    return pyr;
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
  public Vect3d getUpVect() {
    try {
      return base().getUpVect();
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
    return GeomType.PYRAMID3D;
  }
}