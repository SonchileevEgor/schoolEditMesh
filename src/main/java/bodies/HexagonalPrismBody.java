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
import geom.Prism3d;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Ray3d;
import geom.Rib3d;
import geom.Vect3d;
import java.util.ArrayList;
import util.Log;
import util.Util;

/**
 * Meta body - hexagonal prism.
 *
 */
public class HexagonalPrismBody extends BodyAdapter {
  private Prism3d _hprism; // Math object polygon

  /**
   * Constructor of polygon by the geom object.
   *
   * @param id
   * @param title
   * @param pr
   */
  public HexagonalPrismBody(String id, String title, Prism3d pr) {
    super(id, title);
    _hprism = pr;
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
  public HexagonalPrismBody(String id, String title, Vect3d a, Vect3d b, double angle) throws ExGeom {
    super(id, title);
    _hprism = Prism3d.hexagonalPrismBy2PntsAngle(a, b, angle);
    _alias = "шестиугольная призма";
    _exists = true;
  }

  /**
   * constructor for null-body
   *
   * @param id
   * @param title
   */
  public HexagonalPrismBody(String id, String title) {
    super(id, title);
    _alias = "шестиугольная призма";
    _exists = false;
  }

  /**
   * @return math object polygon
   */
  public Prism3d hexagonalprism() {
    return _hprism;
  }

  // Getters for all vertex of hexagonal prism
  public Vect3d A() {
    return _hprism.points().get(0);
  }

  public Vect3d B() {
    return _hprism.points().get(1);
  }

  public Vect3d C() {
    return _hprism.points().get(2);
  }

  public Vect3d D() {
    return _hprism.points().get(3);
  }

  public Vect3d E() {
    return _hprism.points().get(4);
  }

  public Vect3d F() {
    return _hprism.points().get(5);
  }

  public Vect3d A1() {
    return _hprism.points().get(6);
  }

  public Vect3d B1() {
    return _hprism.points().get(7);
  }

  public Vect3d C1() {
    return _hprism.points().get(8);
  }

  public Vect3d D1() {
    return _hprism.points().get(9);
  }

  public Vect3d E1() {
    return _hprism.points().get(10);
  }

  public Vect3d F1() {
    return _hprism.points().get(11);
  }

  @Override
  public BodyType type() {
    return BodyType.HEXPRISM;
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
      Polygon3d poly = _hprism.sectionByPlane(new Plane3d(p.normal(), p.point()));
      if (poly.points().isEmpty())
        throw new ExGeom("Шестиугольная призма и плоскость не пересекаются");
      return new PolygonBody(id, poly.points()); //возвращается новый полигон с параметрами из PolygonBody3d
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
    Vect3d e = E();
    Vect3d f = F();
    Vect3d a1 = A1();
    Vect3d b1 = B1();
    Vect3d c1 = C1();
    Vect3d d1 = D1();
    Vect3d e1 = E1();
    Vect3d f1 = F1();

    // adding the ribs
    try {
      Rib3d rib1 = new Rib3d(a, b);
      edt.addAnchor(rib1, getAnchorID("A"), getAnchorID("B"), this, "rib_AB");
      Rib3d rib2 = new Rib3d(b, c);
      edt.addAnchor(rib2, getAnchorID("B"), getAnchorID("C"), this, "rib_BC");
      Rib3d rib3 = new Rib3d(c, d);
      edt.addAnchor(rib3, getAnchorID("C"), getAnchorID("D"), this, "rib_CD");
      Rib3d rib4 = new Rib3d(d, e);
      edt.addAnchor(rib4, getAnchorID("D"), getAnchorID("E"), this, "rib_DE");
      Rib3d rib5 = new Rib3d(e, f);
      edt.addAnchor(rib5, getAnchorID("E"), getAnchorID("F"), this, "rib_EF");
      Rib3d rib6 = new Rib3d(f, a);
      edt.addAnchor(rib6, getAnchorID("F"), getAnchorID("A"), this, "rib_FA");
      Rib3d rib7 = new Rib3d(a1, b1);
      edt.addAnchor(rib7, getAnchorID("A1"), getAnchorID("B1"), this, "rib_A1B1");
      Rib3d rib8 = new Rib3d(b1, c1);
      edt.addAnchor(rib8, getAnchorID("B1"), getAnchorID("C1"), this, "rib_B1C1");
      Rib3d rib9 = new Rib3d(c1, d1);
      edt.addAnchor(rib9, getAnchorID("C1"), getAnchorID("D1"), this, "rib_C1D1");
      Rib3d rib10 = new Rib3d(d1, e1);
      edt.addAnchor(rib10, getAnchorID("D1"), getAnchorID("E1"), this, "rib_D1E1");
      Rib3d rib11 = new Rib3d(e1, f1);
      edt.addAnchor(rib11, getAnchorID("E1"), getAnchorID("F1"), this, "rib_E1F1");
      Rib3d rib12 = new Rib3d(f1, a1);
      edt.addAnchor(rib12, getAnchorID("F1"), getAnchorID("A1"), this, "rib_F1A1");

      Rib3d rib13 = new Rib3d(a, a1);
      edt.addAnchor(rib13, getAnchorID("A"), getAnchorID("A1"), this, "rib_AA1");
      Rib3d rib14 = new Rib3d(b, b1);
      edt.addAnchor(rib14, getAnchorID("B"), getAnchorID("B1"), this, "rib_BB1");
      Rib3d rib15 = new Rib3d(c, c1);
      edt.addAnchor(rib15, getAnchorID("C"), getAnchorID("C1"), this, "rib_CC1");
      Rib3d rib16 = new Rib3d(d, d1);
      edt.addAnchor(rib16, getAnchorID("D"), getAnchorID("D1"), this, "rib_DD1");
      Rib3d rib17 = new Rib3d(e, e1);
      edt.addAnchor(rib17, getAnchorID("E"), getAnchorID("E1"), this, "rib_EE1");
      Rib3d rib18 = new Rib3d(f, f1);
      edt.addAnchor(rib18, getAnchorID("F"), getAnchorID("F1"), this, "rib_FF1");
    } catch (ExDegeneration ex) {
      util.Fatal.warning("Cannot add ribs of hexagonal prism: " + ex.getMessage());
    }
  }

  @Override
  public void addPlanes(Editor edt) {
    Vect3d a = A();
    Vect3d b = B();
    Vect3d c = C();
    Vect3d d = D();
    Vect3d e = E();
    Vect3d f = F();
    Vect3d a1 = A1();
    Vect3d b1 = B1();
    Vect3d c1 = C1();
    Vect3d d1 = D1();
    Vect3d e1 = E1();
    Vect3d f1 = F1();

    // adding the polys
    try {
      {
        Polygon3d poly = new Polygon3d(a, b, b1, a1);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("A"));
        pointIDs.add(getAnchorID("B"));
        pointIDs.add(getAnchorID("B1"));
        pointIDs.add(getAnchorID("A1"));
        edt.addAnchor(poly, pointIDs, this, "plane_ABB1A1");
      }
      {
        Polygon3d poly = new Polygon3d(b, c, c1, b1);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("B"));
        pointIDs.add(getAnchorID("C"));
        pointIDs.add(getAnchorID("C1"));
        pointIDs.add(getAnchorID("B1"));
        edt.addAnchor(poly, pointIDs, this, "plane_BCC1B1");
      }
      {
        Polygon3d poly = new Polygon3d(c, d, d1, c1);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("C"));
        pointIDs.add(getAnchorID("D"));
        pointIDs.add(getAnchorID("D1"));
        pointIDs.add(getAnchorID("C1"));
        edt.addAnchor(poly, pointIDs, this, "plane_CDD1C1");
      }
      {
        Polygon3d poly = new Polygon3d(d, e, e1, d1);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("D"));
        pointIDs.add(getAnchorID("E"));
        pointIDs.add(getAnchorID("E1"));
        pointIDs.add(getAnchorID("D1"));
        edt.addAnchor(poly, pointIDs, this, "plane_DEE1D1");
      }
      {
        Polygon3d poly = new Polygon3d(e, f, f1, e1);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("E"));
        pointIDs.add(getAnchorID("F"));
        pointIDs.add(getAnchorID("F1"));
        pointIDs.add(getAnchorID("E1"));
        edt.addAnchor(poly, pointIDs, this, "plane_EFF1E1");
      }
      {
        Polygon3d poly = new Polygon3d(f, a, a1, f1);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("F"));
        pointIDs.add(getAnchorID("A"));
        pointIDs.add(getAnchorID("A1"));
        pointIDs.add(getAnchorID("F1"));
        edt.addAnchor(poly, pointIDs, this, "plane_FAA1F1");
      }
      {
        Polygon3d poly = new Polygon3d(a, b, c, d, e, f);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("A"));
        pointIDs.add(getAnchorID("B"));
        pointIDs.add(getAnchorID("C"));
        pointIDs.add(getAnchorID("D"));
        pointIDs.add(getAnchorID("E"));
        pointIDs.add(getAnchorID("F"));
        edt.addAnchor(poly, pointIDs, this, "plane_ABCDEF");
      }
      {
        Polygon3d poly = new Polygon3d(a1, b1, c1, d1, e1, f1);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(getAnchorID("A1"));
        pointIDs.add(getAnchorID("B1"));
        pointIDs.add(getAnchorID("C1"));
        pointIDs.add(getAnchorID("D1"));
        pointIDs.add(getAnchorID("E1"));
        pointIDs.add(getAnchorID("F1"));
        edt.addAnchor(poly, pointIDs, this, "plane_A1B1C1D1E1F1");
      }
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add plane of hexagonal prism: " + ex.getMessage());
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
      faces = _hprism.faces();
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
    return _hprism;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new HexagonalPrismBody(id, title, (Prism3d) geom);
  }

  public void addPointAnchors(ArrayList<Vect3d> points, i_Body result, Editor edt) {
    edt.addAnchor(points.get(0), result, "A");
    edt.addAnchor(points.get(1), result, "B");
    edt.addAnchor(points.get(2), result, "C");
    edt.addAnchor(points.get(3), result, "D");
    edt.addAnchor(points.get(4), result, "E");
    edt.addAnchor(points.get(5), result, "F");
    edt.addAnchor(points.get(6), result, "A1");
    edt.addAnchor(points.get(7), result, "B1");
    edt.addAnchor(points.get(8), result, "C1");
    edt.addAnchor(points.get(9), result, "D1");
    edt.addAnchor(points.get(10), result, "E1");
    edt.addAnchor(points.get(11), result, "F1");
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    HexagonalPrismBody hp = (HexagonalPrismBody)result;
    edt.addAnchor(hp.A(), result, "A");
    edt.addAnchor(hp.B(), result, "B");
    edt.addAnchor(hp.C(), result, "C");
    edt.addAnchor(hp.D(), result, "D");
    edt.addAnchor(hp.E(), result, "E");
    edt.addAnchor(hp.F(), result, "F");
    edt.addAnchor(hp.A1(), result, "A1");
    edt.addAnchor(hp.B1(), result, "B1");
    edt.addAnchor(hp.C1(), result, "C1");
    edt.addAnchor(hp.D1(), result, "D1");
    edt.addAnchor(hp.E1(), result, "E1");
    edt.addAnchor(hp.F1(), result, "F1");
  }

  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_hprism.intersect(ray), ren.getCameraPosition().eye());
  }
}
