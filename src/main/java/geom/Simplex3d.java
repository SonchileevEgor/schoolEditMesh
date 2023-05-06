package geom;

import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Math object tetrahedron.
 *
 * @author alexeev
 */
public class Simplex3d implements i_Geom {

  private Vect3d _a, _b, _c, _d;

  /**
   * Constructor of tetrahedron by 4 vertices.
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @throws geom.ExDegeneration
   */
  public Simplex3d(Vect3d a, Vect3d b, Vect3d c, Vect3d d) throws ExDegeneration {
    if (!Checker.isFourPointsInOnePlane(a, b, c, d)) {
      _a = a;
      _b = b;
      _c = c;
      _d = d;
    } else {
      throw new ExDegeneration("Четыре точки лежат в одной плоскости!");
    }
  }

  private Simplex3d(ArrayList<Vect3d> points) throws ExDegeneration {
    if (!Checker.isFourPointsInOnePlane(points.get(0), points.get(1), points.get(2), points.get(3))) {
      _a = points.get(0);
      _b = points.get(1);
      _c = points.get(2);
      _d = points.get(3);
    } else {
      throw new ExDegeneration("Четыре точки лежат в одной плоскости!");
    }
  }

  /**
   * Construct regular tetrahedron by 2 points and angle
   *
   * @param a 1st vertex of an edge
   * @param b 2nd vertex the edge
   * @param angle rotate angle
   * @return regular tetrahedron (object of Simplex3d)
   * @throws ExDegeneration
   * @throws ExGeom
   */
  public static Simplex3d regTetrahedronBy2PntsAngle(Vect3d a, Vect3d b, double angle) throws ExDegeneration, ExGeom {
    double d = Vect3d.dist(a, b);
    // создаем правильный треугольник по двум точкам и углу поворота
    Polygon3d base = Polygon3d.regPolygonByTwoPoints(a, b, 3, angle);
    Vect3d centerBase = base.inCircle().center();
    // находим вершину тетраэдра
    Plane3d pl = base.plane(); // из полигона делаем плоскость
    d = 2 * d / Math.sqrt(6); // расстояние от центра до топ
    Vect3d top = Vect3d.sum(pl.n().mul(d), centerBase);
    ArrayList<Vect3d> al = base.points();
    al.add(top);
    return new Simplex3d(al.get(0), al.get(1), al.get(2), al.get(3));
  }

  /**
   * @return vertex <strong>A</strong> of the simplex
   */
  public Vect3d a() {
    return _a.duplicate();
  }

  /**
   * @return vertex <strong>B</strong> of the simplex
   */
  public Vect3d b() {
    return _b.duplicate();
  }

  /**
   * @return vertex <strong>C</strong> of the simplex
   */
  public Vect3d c() {
    return _c.duplicate();
  }

  /**
   * @return vertex <strong>D</strong> of the simplex
   */
  public Vect3d d() {
    return _d.duplicate();
  }

  /**
   * @return all vertices
   */
  public ArrayList<Vect3d> points() {
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    points.add(_a);
    points.add(_b);
    points.add(_c);
    points.add(_d);
    return points;
  }

  /**
   * @return all faces (objects Polygon3d)
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> faces = new ArrayList<Polygon3d>();
    ArrayList<Vect3d> face = new ArrayList<Vect3d>();
    Orientation bypass = Orientation.getBodyOrientation(_a, _b, _c, _d);
    face.add(_a);
    face.add(_b);
    face.add(_c);
    if (bypass == Orientation.LEFT) {
      Collections.reverse(face);
    }
    faces.add(new Polygon3d(new ArrayList<>(face)));
    face.clear();
    face.add(_d);
    face.add(_b);
    face.add(_a);
    if (bypass == Orientation.LEFT) {
      Collections.reverse(face);
    }
    faces.add(new Polygon3d(new ArrayList<>(face)));
    face.clear();
    face.add(_a);
    face.add(_c);
    face.add(_d);
    if (bypass == Orientation.LEFT) {
      Collections.reverse(face);
    }
    faces.add(new Polygon3d(new ArrayList<>(face)));
    face.clear();
    face.add(_d);
    face.add(_c);
    face.add(_b);
    if (bypass == Orientation.LEFT) {
      Collections.reverse(face);
    }
    faces.add(new Polygon3d(new ArrayList<>(face)));
    return faces;
  }

  /**
   * Center of icosahedron
   * @return Vect3d object
   * @throws ExGeom
   */
  public Vect3d inCenter() throws ExGeom {
    Vect3d center = new Vect3d();
    Vect3d v1 = new Vect3d(_a);
    v1.inc_mul(faceArea(0));
    Vect3d v2 = new Vect3d(_b);
    v2.inc_mul(faceArea(1));
    Vect3d v3 = new Vect3d(_c);
    v3.inc_mul(faceArea(2));
    Vect3d v4 = new Vect3d(_d);
    v4.inc_mul(faceArea(3));
    center.inc_add(v1);
    center.inc_add(v2);
    center.inc_add(v3);
    center.inc_add(v4);
    center.inc_mul(inRadius() / (3 * volume()));
    return center;
  }

    /**
   * Center of icosahedron
   * @return Vect3d object
   * @throws ExGeom
   */
  public Vect3d outCenter() throws ExGeom {
    return outSphere().center();
  }

  /**
   * Radius of icosahedron incircle
   * @return double value
   */
  public double inRadius() {
    return 3 * volume() / surfaceArea();
  }

  /**
   * Radius of icosahedron outcircle
   * @return double value
   */
  public double outRadius() {
    return outSphere().radius();
  }

  /**
   * @return inscribed sphere of the simplex
   */
  public Sphere3d inSphere() throws ExGeom {
    return new Sphere3d(inCenter(), inRadius());
  }

  /**
   * @return circumscribed sphere of the simplex
   */
  public Sphere3d outSphere() {
    try {
      return new Sphere3d(_a, _b, _c, _d);
    } catch (ExDegeneration ex) {
      return null;
    }
  }

  /**
   * @param index
   * @return area of a specified face of tetrahedron<br>
   * <strong>0</strong>: area of the face 123<br>
   * <strong>1</strong>: area of the face 023 etc. (2, 3)<br>
   * otherwise, returns 0
   */
  public double faceArea(int index) {
    switch (index) {
      case 0:
        return 0.5 * Vect3d.vector_mul(Vect3d.sub(_d, _b), Vect3d.sub(_d, _c)).norm();
      case 1:
        return 0.5 * Vect3d.vector_mul(Vect3d.sub(_d, _a), Vect3d.sub(_d, _c)).norm();
      case 2:
        return 0.5 * Vect3d.vector_mul(Vect3d.sub(_d, _a), Vect3d.sub(_d, _b)).norm();
      case 3:
        return 0.5 * Vect3d.vector_mul(Vect3d.sub(_c, _a), Vect3d.sub(_c, _b)).norm();
      default:
        return 0;
    }
  }

  /**
   * @return full area of simplex surface
   */
  public double surfaceArea() {
    return faceArea(0) + faceArea(1) + faceArea(2) + faceArea(3);
  }

  /**
   * @return volume of the simplex
   */
  public double volume() {
    Vect3d v1 = Vect3d.sub(_b, _a);
    Vect3d v2 = Vect3d.sub(_c, _a);
    Vect3d v3 = Vect3d.sub(_d, _a);
    return Math.abs(Vect3d.tripleProd(v1, v2, v3)) / 6;
  }

  /**
   * Construct section of tetrahedron by plane
   *
   * @param plane section plane
   * @return Polygon3d
   * @throws ExGeom
   */
  public Polygon3d sectionByPlane(Plane3d plane) throws ExGeom {
    Rib3d[] ribs = new Rib3d[6];
    ribs[0] = new Rib3d(_a, _b);
    ribs[1] = new Rib3d(_b, _c);
    ribs[2] = new Rib3d(_c, _a);
    ribs[3] = new Rib3d(_a, _d);
    ribs[4] = new Rib3d(_b, _d);
    ribs[5] = new Rib3d(_c, _d);
    return sectionOfRibObject(ribs, plane);
  }

  /**
   *
   * @param line
   * @return Список точек пересечения симплекса и прямой
   * @throws ExGeom
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExGeom {
    ArrayList<Polygon3d> faces = this.faces();
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    for (int i = 0; i < faces.size(); i++) {
      for (int j = 0; j < faces().get(i).intersectWithLine(line).size(); j++) {
        if (points.contains(faces().get(i).intersectWithLine(line).get(j))) {
          points.add(faces().get(i).intersectWithLine(line).get(j));
        }
      }
    }
    return points;
  }

  @Override
  public ArrayList<Vect3d> deconstr() {
    return points();
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    try {
      return new Simplex3d(points);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
    }
    return null;
  }

  public ArrayList<Vect3d> intersect(Ray3d ray) {
    ArrayList<Vect3d> result = new ArrayList<>();
    try {
      ArrayList<Vect3d> points = intersect(ray.line());
      for (Vect3d point : points) {
        if (ray.containsPoint(point)) {
          result.add(point);
        }
      }
    } catch (ExGeom ex) {
    }
    return result;
  }

  @Override
  public boolean isOrientable() {
    return false;
  }

  @Override
  public GeomType type() {
    return GeomType.SIMPLEX3D;
  }
}
