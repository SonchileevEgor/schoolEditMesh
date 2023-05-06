package geom;

import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author alexeev
 */
public class Cube3d implements i_Geom, i_OrientableGeom {
  private Vect3d _a, _b, _c, _d;
  private Vect3d _at, _bt, _ct, _dt;

  /**
   * Constructor of cube by 3 points:
   * vertices of isosceles right triangle (ab=ac, a=pi/2).
   * @param a vertex of right angle
   * @param b vertex at the base
   * @param d vertex at the base
   * @throws ExDegeneration
   */
  public Cube3d(Vect3d a, Vect3d b, Vect3d d) throws ExDegeneration {
    Vect3d vab = Vect3d.sub(b, a);
    Vect3d vac = Vect3d.sub(d, a);
    if( !Checker.isOrthogonal(vab, vac) || !Checker.isEqualNorm(vab, vac) )
      throw new ExDegeneration("Куб");

    _a = a; _b = b; _d = d;
    _c = new Vect3d(b.x() + vac.x(), b.y() + vac.y(), b.z() + vac.z());

    double c = vab.norm();
    Vect3d ort = Vect3d.vector_mul(vab, vac);
    ort.inc_mul(c / ort.norm());

    _at = new Vect3d(_a.x() + ort.x(), _a.y() + ort.y(), _a.z() + ort.z());
    _bt = new Vect3d(_b.x() + ort.x(), _b.y() + ort.y(), _b.z() + ort.z());
    _dt = new Vect3d(_d.x() + ort.x(), _d.y() + ort.y(), _d.z() + ort.z());
    _ct = new Vect3d(_c.x() + ort.x(), _c.y() + ort.y(), _c.z() + ort.z());
  }

  /**
   * Constructor of cube by points:
   */
  public Cube3d(ArrayList<Vect3d> points) throws ExDegeneration {
      this(points.get(0), points.get(1), points.get(3));
  }

  /**
   * Cube by two points and rotation angle.
   *
   * @param a
   * @param b
   * @param angle
   * @return
   * @throws geom.ExDegeneration
   */
  public static Cube3d cube3dByTwoPointsAndAngle(Vect3d a, Vect3d b, double angle)
          throws ExDegeneration {
    Vect3d d = new Plane3d(Vect3d.sub(b, a), a).getSecondPoint(Vect3d.sub(b, a).norm());
    d = Vect3d.sum(a, Vect3d.sub(d, a).rotate(angle, Vect3d.sub(b, a)));
    return new Cube3d(a, b, d);
  }

  //Getters for vertices
  public Vect3d A1() { return _a.duplicate(); }
  public Vect3d B1() { return _b.duplicate(); }
  public Vect3d C1() { return _c.duplicate(); }
  public Vect3d D1() { return _d.duplicate(); }
  public Vect3d A2() { return _at.duplicate(); }
  public Vect3d B2() { return _bt.duplicate(); }
  public Vect3d C2() { return _ct.duplicate(); }
  public Vect3d D2() { return _dt.duplicate(); }

  /**
   * @return all vertices of cube
   */
  public ArrayList<Vect3d> points(){
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    points.add(_a);
    points.add(_b);
    points.add(_c);
    points.add(_d);
    points.add(_at);
    points.add(_bt);
    points.add(_ct);
    points.add(_dt);
    return points;
  }

  /**
   * @return all faces of cube
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> faces = new ArrayList<Polygon3d>();
    ArrayList<Vect3d> face = new ArrayList<Vect3d>();
    // bottom
    for(int i = 0; i < 4; i++)
      face.add(points().get(i));
    ArrayList<Vect3d> bottom = new ArrayList<>(face);
    face.clear();
    // top
    for(int i = 4; i < 8; i++)
      face.add(points().get(i));
    ArrayList<Vect3d> top = new ArrayList<>(face);
    face.clear();
    Vect3d p1 = new Vect3d(bottom.get(0));
    Vect3d p2 = new Vect3d(bottom.get(1));
    Vect3d p3 = new Vect3d(bottom.get(bottom.size()-1));
    Vect3d p4 = new Vect3d(top.get(0));
    Orientation bypass = Orientation.getBodyOrientation(p1, p2, p3, p4);
    if (bypass == Orientation.RIGHT)
        Collections.reverse(top);
    faces.add(new Polygon3d(new ArrayList<>(bottom)));
    faces.add(new Polygon3d(new ArrayList<>(top)));
    // 3 walls
    for(int i = 0; i < 3; i++){
      face.add(points().get(i));
      face.add(points().get(i + 1));
      face.add(points().get(i + 5));
      face.add(points().get(i + 4));
      if (bypass == Orientation.RIGHT)
          Collections.reverse(face);
      faces.add(new Polygon3d(new ArrayList<>(face)));
      face.clear();
    }
    // 4th wall
    face.add(points().get(4));
    face.add(points().get(7));
    face.add(points().get(3));
    face.add(points().get(0));
    if (bypass == Orientation.RIGHT)
        Collections.reverse(face);
    faces.add(new Polygon3d(new ArrayList<>(face)));
    return faces;
  }


  /**
   * @param line
   * @return Список точек пересечения симплекса и прямой
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
   * @return center of cube
   */
  public Vect3d center() {
    return Vect3d.conv_hull(_a, _ct, 0.5);
  }

  /**
   * @return length of edge
   */
  public double edgeLength() {
    return Vect3d.sub(_a, _b).norm();
  }

  /**
   * @return length of main diagonal
   */
  public double diagonalLength() {
    return Vect3d.sub(_a, _ct).norm();
  }

  /**
   * Area of surface.
   * @return
   * @author alexeev
   */
  public double surfaceArea() {
    return 6 * Math.pow(edgeLength(), 2);
  }

  /**
   * Volume of cube.
   * @return
   */
  public double volume(){
    return Math.pow(edgeLength(), 3);
  }

  /**
   * Construct section of cube by plane
   * @param plane section plane
   * @return Polygon3d
   * @throws ExGeom
   */
  public Polygon3d sectionByPlane(Plane3d plane) throws ExGeom {
    Rib3d[] ribs = new Rib3d[12];
    ribs[0] = new Rib3d(_b, _a);   //A1 B1
    ribs[1] = new Rib3d(_a, _d);   //A1 D1
    ribs[2] = new Rib3d(_d, _c);   //C1 D1
    ribs[3] = new Rib3d(_c, _b);   //C1 B1
    ribs[4] = new Rib3d(_b, _bt);  //A1 A2
    ribs[5] = new Rib3d(_a, _at);  //B1 B2
    ribs[6] = new Rib3d(_c, _ct);  //C1 C2
    ribs[7] = new Rib3d(_d, _dt);  //D1 D2
    ribs[8] = new Rib3d(_bt, _at); //A2 B2
    ribs[9] = new Rib3d(_at, _dt); //A2 D2
    ribs[10] = new Rib3d(_dt, _ct);//B2 C2
    ribs[11] = new Rib3d(_ct, _bt);//C2 B2
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
  public ArrayList<Vect3d> deconstr() {
    // в качестве основного набора точек - вершины
    return points();
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    Cube3d cube = null;
    try {
      cube = new Cube3d(points);
    } catch (ExDegeneration ex) {
      GeomErrorHandler.errorMessage(ex);
    }
    return cube;
  }

  @Override
  public Vect3d getUpVect() {
    return Vect3d.sub(_at, _a).getNormalized();
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.CUBE3D;
  }
}