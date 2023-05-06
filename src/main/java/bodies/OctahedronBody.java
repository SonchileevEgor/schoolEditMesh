package bodies;

import static config.Config.LOG_LEVEL;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.i_Geom;
import geom.AbstractPolygon;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.ExZeroDivision;
import geom.Octahedron3d;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Ray3d;
import geom.Rib3d;
import geom.Vect3d;
import java.util.ArrayList;
import util.Log;
import util.Util;

/**
 * Meta body - octahedron.
 *
 */
public class OctahedronBody extends BodyAdapter {
  private Octahedron3d _octahedron; // Math object polygon

  /**
   * Constructor of polygon by the geom object.
   *
   * @param id
   * @param title
   * @param okt
   */
  public OctahedronBody(String id, String title, Octahedron3d okt) {
    super(id, title);
    _octahedron = okt;
    _exists = true;
  }

  /**
   * Constructor of polygon by list of points
   *
   * @param id
   * @param title
   * @param a
   * @param b
   * @param angle
   * @throws geom.ExGeom
   */
  public OctahedronBody(String id, String title, Vect3d a, Vect3d b, double angle) throws ExGeom {
    super(id, title);
    _octahedron = new Octahedron3d(a, b, angle);
    _alias = "октаэдр";
    _exists = true;
  }

  /**
   * constructor for null-body
   *
   * @param id
   * @param title
   */
  public OctahedronBody(String id, String title) {
    super(id, title);
    _alias = "октаэдр";
    _exists = false;
  }

  /**
   * @return math object polygon
   */
  public Octahedron3d octahedron() {
    return _octahedron;
  }

  // Getters for all vertex of octahedron
  public Vect3d A() {
    return _octahedron.A();
  }

  public Vect3d B() {
    return _octahedron.B();
  }

  public Vect3d C() {
    return _octahedron.C();
  }

  public Vect3d D() {
    return _octahedron.D();
  }

  public Vect3d topu() {
    return _octahedron.topu();
  }

  public Vect3d topd() {
    return _octahedron.topd();
  }

  @Override
  public BodyType type() {
    return BodyType.OCTAHEDRON;
  }

  /**
   * Construct octahedron section by plane
   * @param id
   * @param p
   * @return
   * @throws ExGeom
   * @throws ExZeroDivision
   */
  public PolygonBody section(String id, PlaneBody p)
    throws ExGeom, ExZeroDivision {
    try {
      Polygon3d poly = _octahedron.sectionByPlane(new Plane3d(p.normal(), p.point()));
      if (poly.points().isEmpty())
        throw new ExGeom("октаэдр и плоскость не пересекаются");
      // возвращается новый полигон с параметрами из PolygonBody3d
      return new PolygonBody(id, poly.points()); 
    } catch (NullPointerException ex) {
      throw new ExGeom("плоскость не была построена");
    }
  }

  @Override
  public void addRibs(Editor edt) {
    Vect3d a = A();
    Vect3d b = B();
    Vect3d c = C();
    Vect3d d = D();
    Vect3d topu = topu();
    Vect3d topd = topd();

    // adding the ribs
    try {
      Rib3d rib1 = new Rib3d(topu, a);
      edt.addAnchor(rib1, getAnchorID("Topu"), getAnchorID("A"), this, "rib_TopuA");
      Rib3d rib2 = new Rib3d(topu, b);
      edt.addAnchor(rib2, getAnchorID("Topu"), getAnchorID("B"), this, "rib_TopuB");
      Rib3d rib3 = new Rib3d(topu, c);
      edt.addAnchor(rib3, getAnchorID("Topu"), getAnchorID("C"), this, "rib_TopuC");
      Rib3d rib4 = new Rib3d(topu, d);
      edt.addAnchor(rib4, getAnchorID("Topu"), getAnchorID("D"), this, "rib_TopuD");
      Rib3d rib5 = new Rib3d(topd, a);
      edt.addAnchor(rib5, getAnchorID("Topd"), getAnchorID("A"), this, "rib_TopdA");
      Rib3d rib6 = new Rib3d(topd, b);
      edt.addAnchor(rib6, getAnchorID("Topd"), getAnchorID("B"), this, "rib_TopdB");
      Rib3d rib7 = new Rib3d(topd, c);
      edt.addAnchor(rib7, getAnchorID("Topd"), getAnchorID("C"), this, "rib_TopdC");
      Rib3d rib8 = new Rib3d(topd, d);
      edt.addAnchor(rib8, getAnchorID("Topd"), getAnchorID("D"), this, "rib_TopdD");
      Rib3d rib9 = new Rib3d(a, b);
      edt.addAnchor(rib9, getAnchorID("A"), getAnchorID("B"), this, "rib_AB");
      Rib3d rib10 = new Rib3d(b, c);
      edt.addAnchor(rib10, getAnchorID("B"), getAnchorID("C"), this, "rib_BC");
      Rib3d rib11 = new Rib3d(c, d);
      edt.addAnchor(rib11, getAnchorID("C"), getAnchorID("D"), this, "rib_CD");
      Rib3d rib12 = new Rib3d(d, a);
      edt.addAnchor(rib12, getAnchorID("D"), getAnchorID("A"), this, "rib_DA");
    } catch (ExDegeneration ex) {
      util.Fatal.warning("Cannot add ribs of octahedron: " + ex.getMessage());
    }
  }

  @Override
  public void addPlanes(Editor edt) {
    Vect3d a = A();
    Vect3d b = B();
    Vect3d c = C();
    Vect3d d = D();
    Vect3d topu = topu();
    Vect3d topd = topd();

    // adding the polys
    try {
      {
        Polygon3d poly = new Polygon3d(topu, a, b);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("Topu"));
        pointIDs.add(getAnchorID("A"));
        pointIDs.add(getAnchorID("B"));
        edt.addAnchor(poly, pointIDs, this, "plane_TopuAB");
      }
      {
        Polygon3d poly = new Polygon3d(topu, b, c);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("Topu"));
        pointIDs.add(getAnchorID("B"));
        pointIDs.add(getAnchorID("C"));
        edt.addAnchor(poly, pointIDs, this, "plane_TopuBC");
      }
      {
        Polygon3d poly = new Polygon3d(topu, c, d);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("Topu"));
        pointIDs.add(getAnchorID("C"));
        pointIDs.add(getAnchorID("D"));
        edt.addAnchor(poly, pointIDs, this, "plane_TopuCD");
      }
      {
        Polygon3d poly = new Polygon3d(topu, d, a);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("Topu"));
        pointIDs.add(getAnchorID("D"));
        pointIDs.add(getAnchorID("A"));
        edt.addAnchor(poly, pointIDs, this, "plane_TopuDA");
      }
      {
        Polygon3d poly = new Polygon3d(topd, a, b);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("Topd"));
        pointIDs.add(getAnchorID("A"));
        pointIDs.add(getAnchorID("B"));
        edt.addAnchor(poly, pointIDs, this, "plane_TopdAB");
      }
      {
        Polygon3d poly = new Polygon3d(topd, b, c);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("Topd"));
        pointIDs.add(getAnchorID("B"));
        pointIDs.add(getAnchorID("C"));
        edt.addAnchor(poly, pointIDs, this, "plane_TopdBC");
      }
      {
        Polygon3d poly = new Polygon3d(topd, c, d);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("Topd"));
        pointIDs.add(getAnchorID("C"));
        pointIDs.add(getAnchorID("D"));
        edt.addAnchor(poly, pointIDs, this, "plane_TopdCD");
      }
      {
        Polygon3d poly = new Polygon3d(topd, d, a);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("Topd"));
        pointIDs.add(getAnchorID("D"));
        pointIDs.add(getAnchorID("A"));
        edt.addAnchor(poly, pointIDs, this, "plane_TopdDA");
      }
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add Plane of octahedron: " + ex.getMessage());
    }
  }

  @Override
  public void glDrawFacets(Render ren) {
    // рисуются только якоря
  }

  @Override
  public void glDrawCarcass(Render ren) {
    // рисуются только якоря
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    ArrayList<Polygon3d> faces = new ArrayList<>();
    try {
      faces = _octahedron.faces();
    } catch (ExGeom e) {
      if (LOG_LEVEL.value() >= 2) {
        Log.out.println(e.getMessage());
      }
    }
    return faces;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    //TODO: add intersection with plane and return as abstract polygon.
    return super.getIntersectionWithPlane(plane);
  }

  @Override
  public i_Geom getGeom() {
    return _octahedron;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new OctahedronBody(id, title, (Octahedron3d) geom);
  }

  public void addPointAnchors(ArrayList<Vect3d> points, i_Body result, Editor edt) {
    edt.addAnchor(points.get(0), result, "A");
    edt.addAnchor(points.get(1), result, "B");
    edt.addAnchor(points.get(2), result, "C");
    edt.addAnchor(points.get(3), result, "D");
    edt.addAnchor(points.get(4), result, "Topu");
    edt.addAnchor(points.get(5), result, "Topd");
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    OctahedronBody oct = (OctahedronBody)result;
    edt.addAnchor(oct.A(), result, "A");
    edt.addAnchor(oct.B(), result, "B");
    edt.addAnchor(oct.C(), result, "C");
    edt.addAnchor(oct.D(), result, "D");
    edt.addAnchor(oct.topd(), result, "Topd");
    edt.addAnchor(oct.topu(), result, "Topu");
  }

  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_octahedron.intersect(ray), ren.getCameraPosition().eye());
  }
}
