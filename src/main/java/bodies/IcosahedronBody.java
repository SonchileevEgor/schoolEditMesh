package bodies;

import static config.Config.LOG_LEVEL;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.AbstractPolygon;
import geom.ExGeom;
import geom.ExZeroDivision;
import geom.Icosahedron3d;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Ray3d;
import geom.Rib3d;
import geom.Vect3d;
import geom.i_Geom;
import java.util.ArrayList;
import util.Log;
import util.Util;

/**
 * Meta body - icosahedron.
 *
 */
public class IcosahedronBody extends BodyAdapter {
  private Icosahedron3d _icosahedron; // Math object polygon

  /**
   * Constructor of polygon by the geom object.
   *
   * @param id
   * @param title
   * @param ic
   */
  public IcosahedronBody(String id, String title, Icosahedron3d ic) {
    super(id, title);
    _icosahedron = ic;
    _alias = "икосаэдр";
    _exists = true;
  }

  /**
   * Constructor of icosahedron
   *
   * @param id
   * @param title
   * @param a
   * @param b
   * @param angle
   * @throws geom.ExGeom
   */
  public IcosahedronBody(String id, String title, Vect3d a, Vect3d b, double angle) throws ExGeom {
    super(id, title);
    _icosahedron = new Icosahedron3d(a, b, angle);
    _alias = "икосаэдр";
    _exists = true;
  }

  /**
   * constructor for null-body
   *
   * @param id
   * @param title
   */
  public IcosahedronBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  /**
   * @return math object polygon
   */
  public Icosahedron3d icosahedron() {
    return _icosahedron;
  }

  // Getters for all vertexs of icosahedron
  public Vect3d A() {
    return _icosahedron.A();
  }

  public Vect3d B() {
    return _icosahedron.B();
  }

  public Vect3d C() {
    return _icosahedron.C();
  }

  public Vect3d D() {
    return _icosahedron.D();
  }

  public Vect3d E() {
    return _icosahedron.E();
  }

  public Vect3d A1() {
    return _icosahedron.A1();
  }

  public Vect3d B1() {
    return _icosahedron.B1();
  }

  public Vect3d C1() {
    return _icosahedron.C1();
  }

  public Vect3d D1() {
    return _icosahedron.D1();
  }

  public Vect3d E1() {
    return _icosahedron.E1();
  }

  public Vect3d topu() {
    return _icosahedron.topu();
  }

  public Vect3d topd() {
    return _icosahedron.topd();
  }

  @Override
  public BodyType type() {
    return BodyType.ICOSAHEDRON;
  }

  @Override
  public void addRibs(Editor edt) {
    Vect3d a = A();
    Vect3d b = B();
    Vect3d c = C();
    Vect3d d = D();
    Vect3d e = E();
    Vect3d a1 = A1();
    Vect3d b1 = B1();
    Vect3d c1 = C1();
    Vect3d d1 = D1();
    Vect3d e1 = E1();
    Vect3d topu = topu();
    Vect3d topd = topd();

    try {
      Rib3d rib1 = new Rib3d(a, b);
      edt.addAnchor(rib1, getAnchorID("A"), getAnchorID("B"), this, "rib_AB");
      Rib3d rib2 = new Rib3d(b, c);
      edt.addAnchor(rib2, getAnchorID("B"), getAnchorID("C"), this, "rib_BC");
      Rib3d rib3 = new Rib3d(c, d);
      edt.addAnchor(rib3, getAnchorID("C"), getAnchorID("D"), this, "rib_CD");
      Rib3d rib4 = new Rib3d(d, e);
      edt.addAnchor(rib4, getAnchorID("D"), getAnchorID("E"), this, "rib_DE");
      Rib3d rib5 = new Rib3d(e, a);
      edt.addAnchor(rib5, getAnchorID("E"), getAnchorID("A"), this, "rib_EA");

      Rib3d rib6 = new Rib3d(topu, a);
      edt.addAnchor(rib6, getAnchorID("Topu"), getAnchorID("A"), this, "rib_TopuA");
      Rib3d rib7 = new Rib3d(topu, b);
      edt.addAnchor(rib7, getAnchorID("Topu"), getAnchorID("B"), this, "rib_TopuB");
      Rib3d rib8 = new Rib3d(topu, c);
      edt.addAnchor(rib8, getAnchorID("Topu"), getAnchorID("C"), this, "rib_TopuC");
      Rib3d rib9 = new Rib3d(topu, d);
      edt.addAnchor(rib9, getAnchorID("Topu"), getAnchorID("D"), this, "rib_TopuD");
      Rib3d rib10 = new Rib3d(topu, e);
      edt.addAnchor(rib10, getAnchorID("Topu"), getAnchorID("E"), this, "rib_TopuE");

      Rib3d rib11 = new Rib3d(a1, b1);
      edt.addAnchor(rib11, getAnchorID("A1"), getAnchorID("B1"), this, "rib_A1B1");
      Rib3d rib12 = new Rib3d(b1, c1);
      edt.addAnchor(rib12, getAnchorID("B1"), getAnchorID("C1"), this, "rib_B1C1");
      Rib3d rib13 = new Rib3d(c1, d1);
      edt.addAnchor(rib13, getAnchorID("C1"), getAnchorID("D1"), this, "rib_C1D1");
      Rib3d rib14 = new Rib3d(d1, e1);
      edt.addAnchor(rib14, getAnchorID("D1"), getAnchorID("E1"), this, "rib_D1E1");
      Rib3d rib15 = new Rib3d(e1, a1);
      edt.addAnchor(rib15, getAnchorID("E1"), getAnchorID("A1"), this, "rib_E1A1");

      Rib3d rib16 = new Rib3d(topd, a1);
      edt.addAnchor(rib16, getAnchorID("Topd"), getAnchorID("A1"), this, "rib_TopdA1");
      Rib3d rib17 = new Rib3d(topd, b1);
      edt.addAnchor(rib17, getAnchorID("Topd"), getAnchorID("B1"), this, "rib_TopdB1");
      Rib3d rib18 = new Rib3d(topd, c1);
      edt.addAnchor(rib18, getAnchorID("Topd"), getAnchorID("C1"), this, "rib_TopdC1");
      Rib3d rib19 = new Rib3d(topd, d1);
      edt.addAnchor(rib19, getAnchorID("Topd"), getAnchorID("D1"), this, "rib_TopdD1");
      Rib3d rib20 = new Rib3d(topd, e1);
      edt.addAnchor(rib20, getAnchorID("Topd"), getAnchorID("E1"), this, "rib_TopdE1");

      Rib3d rib21 = new Rib3d(a, a1);
      edt.addAnchor(rib21, getAnchorID("A"), getAnchorID("A1"), this, "rib_AA1");
      Rib3d rib22 = new Rib3d(a1, b);
      edt.addAnchor(rib22, getAnchorID("A1"), getAnchorID("B"), this, "rib_A1B");
      Rib3d rib23 = new Rib3d(b, b1);
      edt.addAnchor(rib23, getAnchorID("B"), getAnchorID("B1"), this, "rib_BB1");
      Rib3d rib24 = new Rib3d(b1, c);
      edt.addAnchor(rib24, getAnchorID("B1"), getAnchorID("C"), this, "rib_B1C");
      Rib3d rib25 = new Rib3d(c, c1);
      edt.addAnchor(rib25, getAnchorID("C"), getAnchorID("C1"), this, "rib_CC1");
      Rib3d rib26 = new Rib3d(c1, d);
      edt.addAnchor(rib26, getAnchorID("C1"), getAnchorID("D"), this, "rib_C1D");
      Rib3d rib27 = new Rib3d(d, d1);
      edt.addAnchor(rib27, getAnchorID("D"), getAnchorID("D1"), this, "rib_DD1");
      Rib3d rib28 = new Rib3d(d1, e);
      edt.addAnchor(rib28, getAnchorID("D1"), getAnchorID("E"), this, "rib_D1E");
      Rib3d rib29 = new Rib3d(e, e1);
      edt.addAnchor(rib29, getAnchorID("E"), getAnchorID("E1"), this, "rib_EE1");
      Rib3d rib30 = new Rib3d(e1, a);
      edt.addAnchor(rib30, getAnchorID("E1"), getAnchorID("A"), this, "rib_E1A");
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add Plane of icosahedron: " + ex.getMessage());
    }
  }

  @Override
  public void addPlanes(Editor edt) {
    Vect3d a = A();
    Vect3d b = B();
    Vect3d c = C();
    Vect3d d = D();
    Vect3d e = E();
    Vect3d a1 = A1();
    Vect3d b1 = B1();
    Vect3d c1 = C1();
    Vect3d d1 = D1();
    Vect3d e1 = E1();
    Vect3d topu = topu();
    Vect3d topd = topd();

    try {
      {
        Polygon3d poly = new Polygon3d(topu, a, b);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("Topu"));
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("B"));
        edt.addAnchor(poly, pointsIDs, this, "plane_TopuAB");
      }
      {
        Polygon3d poly = new Polygon3d(topu, b, c);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("Topu"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("C"));
        edt.addAnchor(poly, pointsIDs, this, "plane_TopuBC");
      }
      {
        Polygon3d poly = new Polygon3d(topu, c, d);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("Topu"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("D"));
        edt.addAnchor(poly, pointsIDs, this, "plane_TopuCD");
      }
      {
        Polygon3d poly = new Polygon3d(topu, d, e);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("Topu"));
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("E"));
        edt.addAnchor(poly, pointsIDs, this, "plane_TopuDE");
      }
      {
        Polygon3d poly = new Polygon3d(topu, e, a);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("Topu"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("A"));
        edt.addAnchor(poly, pointsIDs, this, "plane_TopuEA");
      }
      {
        Polygon3d poly = new Polygon3d(topd, a1, b1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("Topd"));
        pointsIDs.add(getAnchorID("A1"));
        pointsIDs.add(getAnchorID("B1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_TopdA1B1");
      }
      {
        Polygon3d poly = new Polygon3d(topd, b1, c1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("Topd"));
        pointsIDs.add(getAnchorID("B1"));
        pointsIDs.add(getAnchorID("C1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_TopdB1C1");
      }
      {
        Polygon3d poly = new Polygon3d(topd, c1, d1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("Topd"));
        pointsIDs.add(getAnchorID("C1"));
        pointsIDs.add(getAnchorID("D1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_TopdAB");
      }
      {
        Polygon3d poly = new Polygon3d(topd, d1, e1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("Topd"));
        pointsIDs.add(getAnchorID("D1"));
        pointsIDs.add(getAnchorID("E1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_TopdD1E1");
      }
      {
        Polygon3d poly = new Polygon3d(topd, e1, a1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("Topd"));
        pointsIDs.add(getAnchorID("E1"));
        pointsIDs.add(getAnchorID("A1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_TopdE1A1");
      }
      {
        Polygon3d poly = new Polygon3d(a, a1, b);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("A1"));
        pointsIDs.add(getAnchorID("B"));
        edt.addAnchor(poly, pointsIDs, this, "plane_AA1B");
      }
      {
        Polygon3d poly = new Polygon3d(a1, b, b1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A1"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("B1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_A1BB1");
      }
      {
        Polygon3d poly = new Polygon3d(b, b1, c);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("B1"));
        pointsIDs.add(getAnchorID("C"));
        edt.addAnchor(poly, pointsIDs, this, "plane_BB1C");
      }
      {
        Polygon3d poly = new Polygon3d(b1, c, c1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("B1"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("C1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_B1CC1");
      }
      {
        Polygon3d poly = new Polygon3d(c, c1, d);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("C1"));
        pointsIDs.add(getAnchorID("D"));
        edt.addAnchor(poly, pointsIDs, this, "plane_CC1D");
      }
      {
        Polygon3d poly = new Polygon3d(c1, d, d1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C1"));
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("D1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_C1DD1");
      }
      {
        Polygon3d poly = new Polygon3d(d, d1, e);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("D1"));
        pointsIDs.add(getAnchorID("E"));
        edt.addAnchor(poly, pointsIDs, this, "plane_DD1E");
      }
      {
        Polygon3d poly = new Polygon3d(d1, e, e1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("D1"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("E1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_D1EE1");
      }
      {
        Polygon3d poly = new Polygon3d(e, e1, a);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("E1"));
        pointsIDs.add(getAnchorID("A"));
        edt.addAnchor(poly, pointsIDs, this, "plane_EE1A");
      }
      {
        Polygon3d poly = new Polygon3d(e1, a, a1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("E1"));
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("A1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_E1AA1");
      }
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add plane of icosahedron: " + ex.getMessage());
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
      faces = _icosahedron.faces();
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
  
   /**
   * Construct icosahedron section by plane
   * @param id
   * @param p
   * @return
   * @throws ExGeom
   * @throws ExZeroDivision
   */
  public PolygonBody section(String id, PlaneBody p)
    throws ExGeom, ExZeroDivision {
    try {
      Polygon3d poly = _icosahedron.sectionByPlane(new Plane3d(p.normal(), p.point()));
      if (poly.points().isEmpty())
        throw new ExGeom("икосаэдр и плоскость не пересекаются");
      // возвращается новый полигон с параметрами из PolygonBody3d
      return new PolygonBody(id, poly.points()); 
    } catch (NullPointerException ex) {
      throw new ExGeom("плоскость не была построена");
    }
  }

  @Override
  public i_Geom getGeom() {
    return _icosahedron;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new IcosahedronBody(id, title, (Icosahedron3d) geom);
  }

  public void addPointAnchors(ArrayList<Vect3d> points, i_Body result, Editor edt) {
    edt.addAnchor(points.get(0), result, "Topu");
    edt.addAnchor(points.get(1), result, "A");
    edt.addAnchor(points.get(2), result, "B");
    edt.addAnchor(points.get(3), result, "C");
    edt.addAnchor(points.get(4), result, "D");
    edt.addAnchor(points.get(5), result, "E");
    edt.addAnchor(points.get(6), result, "A1");
    edt.addAnchor(points.get(7), result, "B1");
    edt.addAnchor(points.get(8), result, "C1");
    edt.addAnchor(points.get(9), result, "D1");
    edt.addAnchor(points.get(10), result, "E1");
    edt.addAnchor(points.get(11), result, "Topd");
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    IcosahedronBody ics = (IcosahedronBody) result;
    edt.addAnchor(ics.A(), result, "A");
    edt.addAnchor(ics.B(), result, "B");
    edt.addAnchor(ics.C(), result, "C");
    edt.addAnchor(ics.D(), result, "D");
    edt.addAnchor(ics.E(), result, "E");
    edt.addAnchor(ics.A1(), result, "A1");
    edt.addAnchor(ics.B1(), result, "B1");
    edt.addAnchor(ics.C1(), result, "C1");
    edt.addAnchor(ics.D1(), result, "D1");
    edt.addAnchor(ics.E1(), result, "E1");
    edt.addAnchor(ics.topu(), result, "Topu");
    edt.addAnchor(ics.topd(), result, "Topd");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_icosahedron.intersect(ray), ren.getCameraPosition().eye());
  }
};
