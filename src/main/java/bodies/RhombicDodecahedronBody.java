package bodies;

import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.AbstractPolygon;
import geom.ExGeom;
import geom.ExZeroDivision;
import geom.RhombicDodecahedron3d;
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
 * Meta body - rhombicDodecahedron.
 *
 */
public class RhombicDodecahedronBody extends BodyAdapter {
  private RhombicDodecahedron3d _dod; // Math object polygon

  /**
   * Constructor of polygon by the geom object.
   *
   * @param id
   * @param title
   * @param dod
   */
  public RhombicDodecahedronBody(String id, String title, RhombicDodecahedron3d dod) {
    super(id, title);
    _dod = dod;
    _alias = "ромбододекаэдр";
    _exists = true;
  }

  /**
   * Constructor of rhombic_dodecahedron
   *
   * @param id
   * @param title
   * @param a
   * @param b
   * @param angle
   * @throws geom.ExGeom
   */
  public RhombicDodecahedronBody(String id, String title, Vect3d a, Vect3d b, double angle) throws ExGeom {
    super(id, title);
    _dod = new RhombicDodecahedron3d(a, b, angle);
    _alias = "ромбододекаэдр";
    _exists = true;
  }

  /**
   * constructor for null-body
   *
   * @param id
   * @param title
   */
  public RhombicDodecahedronBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  /**
   * @return math object polygon
   */
  public RhombicDodecahedron3d rhombicDodecahedron() {
    return _dod;
  }

  // Getters for all vertexs of elongatedDodecahedron

  public Vect3d A() {
    return _dod.A();
  }

  public Vect3d B() {
    return _dod.B();
  }

  public Vect3d C() {
    return _dod.C();
  }

  public Vect3d D() {
    return _dod.D();
  }

  public Vect3d A1() {
    return _dod.A1();
  }

  public Vect3d B1() {
    return _dod.B1();
  }

  public Vect3d C1() {
    return _dod.C1();
  }

  public Vect3d D1() {
    return _dod.D1();
  }


  public Vect3d E() {
    return _dod.E();
  }

  public Vect3d F() {
    return _dod.F();
  }

  public Vect3d G() {
    return _dod.G();
  }

  public Vect3d H() {
    return _dod.H();
  }

  public Vect3d I() {
    return _dod.I();
  }

  public Vect3d J() {
    return _dod.J();
  }

  @Override
  public BodyType type() {
    return BodyType.RHOMBIC_DODECAHEDRON;
  }

  @Override
  public void addRibs(Editor edt) {
    Vect3d a = A();
    Vect3d b = B();
    Vect3d c = C();
    Vect3d d = D();
    Vect3d a1 = A1();
    Vect3d b1 = B1();
    Vect3d c1 = C1();
    Vect3d d1 = D1();
    Vect3d e = E();
    Vect3d f = F();
    Vect3d g = G();
    Vect3d h = H();
    Vect3d i = I();
    Vect3d j = J();

    try {
      Rib3d rib1 = new Rib3d(a, b);
      edt.addAnchor(rib1, getAnchorID("A"), getAnchorID("B"), this, "rib_AB");
      Rib3d rib2 = new Rib3d(b, c);
      edt.addAnchor(rib2, getAnchorID("B"), getAnchorID("C"), this, "rib_BC");
      Rib3d rib3 = new Rib3d(c, d);
      edt.addAnchor(rib3, getAnchorID("C"), getAnchorID("D"), this, "rib_CD");
      Rib3d rib4 = new Rib3d(d, a);
      edt.addAnchor(rib4, getAnchorID("D"), getAnchorID("A"), this, "rib_DA");
      Rib3d rib5 = new Rib3d(a1, b1);
      edt.addAnchor(rib5, getAnchorID("A1"), getAnchorID("B1"), this, "rib_A1B1");
      Rib3d rib6 = new Rib3d(b1, c1);
      edt.addAnchor(rib6, getAnchorID("B1"), getAnchorID("C1"), this, "rib_B1C1");
      Rib3d rib7 = new Rib3d(c1, d1);
      edt.addAnchor(rib7, getAnchorID("C1"), getAnchorID("D1"), this, "rib_C1D1");
      Rib3d rib8 = new Rib3d(d1, a1);
      edt.addAnchor(rib8, getAnchorID("D1"), getAnchorID("A1"), this, "rib_D1A1");
      
      Rib3d rib9 = new Rib3d(a, h);
      edt.addAnchor(rib9, getAnchorID("A"), getAnchorID("H"), this, "rib_AH");
      Rib3d rib10 = new Rib3d(a, i);
      edt.addAnchor(rib10, getAnchorID("A"), getAnchorID("I"), this, "rib_AI");
      Rib3d rib11 = new Rib3d(b, j);
      edt.addAnchor(rib11, getAnchorID("B"), getAnchorID("J"), this, "rib_BJ");
      Rib3d rib12 = new Rib3d(c, e);
      edt.addAnchor(rib12, getAnchorID("C"), getAnchorID("E"), this, "rib_CE");
      Rib3d rib13 = new Rib3d(c, f);
      edt.addAnchor(rib13, getAnchorID("C"), getAnchorID("F"), this, "rib_CF");
      Rib3d rib14 = new Rib3d(d, g);
      edt.addAnchor(rib14, getAnchorID("D"), getAnchorID("G"), this, "rib_DG");
      Rib3d rib15 = new Rib3d(a1, h);
      edt.addAnchor(rib15, getAnchorID("A1"), getAnchorID("H"), this, "rib_A1H");
      Rib3d rib16 = new Rib3d(a1, i);
      edt.addAnchor(rib16, getAnchorID("A1"), getAnchorID("I"), this, "rib_A1I");
      Rib3d rib17 = new Rib3d(b1, j);
      edt.addAnchor(rib17, getAnchorID("B1"), getAnchorID("J"), this, "rib_B1J");
      Rib3d rib18 = new Rib3d(c1, e);
      edt.addAnchor(rib18, getAnchorID("C1"), getAnchorID("E"), this, "rib_C1E");
      Rib3d rib19 = new Rib3d(c1, f);
      edt.addAnchor(rib19, getAnchorID("C1"), getAnchorID("F"), this, "rib_C1F");
      Rib3d rib20 = new Rib3d(d1, g);
      edt.addAnchor(rib20, getAnchorID("D1"), getAnchorID("G"), this, "rib_D1G");
      
      Rib3d rib21 = new Rib3d(g, f);
      edt.addAnchor(rib21, getAnchorID("G"), getAnchorID("F"), this, "rib_GF");
      Rib3d rib22 = new Rib3d(g, h);
      edt.addAnchor(rib22, getAnchorID("G"), getAnchorID("H"), this, "rib_GH");
      Rib3d rib23 = new Rib3d(j, i);
      edt.addAnchor(rib23, getAnchorID("J"), getAnchorID("I"), this, "rib_JI");
      Rib3d rib24 = new Rib3d(j, e);
      edt.addAnchor(rib24, getAnchorID("J"), getAnchorID("E"), this, "rib_JE");
      
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add plane of rhombicDodecahedron: " + ex.getMessage());
    }
  }

  @Override
  public void addPlanes(Editor edt) {
    Vect3d a = A();
    Vect3d b = B();
    Vect3d c = C();
    Vect3d d = D();
    Vect3d a1 = A1();
    Vect3d b1 = B1();
    Vect3d c1 = C1();
    Vect3d d1 = D1();
    Vect3d e = E();
    Vect3d f = F();
    Vect3d g = G();
    Vect3d h = H();
    Vect3d i = I();
    Vect3d j = J();

    try {
      {
        Polygon3d poly = new Polygon3d(a, b, c, d);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("D"));
        edt.addAnchor(poly, pointsIDs, this, "plane_ABCD");
      }
      {
        Polygon3d poly = new Polygon3d(a1, b1, c1, d1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A1"));
        pointsIDs.add(getAnchorID("B1"));
        pointsIDs.add(getAnchorID("C1"));
        pointsIDs.add(getAnchorID("D1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_A1B1C1D1");
      } 
      {
        Polygon3d poly = new Polygon3d(c, f, c1, e);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("C1"));
        pointsIDs.add(getAnchorID("E"));
        edt.addAnchor(poly, pointsIDs, this, "plane_CFC1E");
      }
      {
        Polygon3d poly = new Polygon3d(a, h, a1, i);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("A1"));
        pointsIDs.add(getAnchorID("I"));
        edt.addAnchor(poly, pointsIDs, this, "plane_AHA1I");
      }
      {
        Polygon3d poly = new Polygon3d(c1, d1, g, f);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C1"));
        pointsIDs.add(getAnchorID("D1"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("F"));
        edt.addAnchor(poly, pointsIDs, this, "plane_C1D1GF");
      } 
      {
        Polygon3d poly = new Polygon3d(c, d, g, f);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("F"));
        edt.addAnchor(poly, pointsIDs, this, "plane_CDGF");
      } 
      {
        Polygon3d poly = new Polygon3d(a, d, g, h);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("H"));
        edt.addAnchor(poly, pointsIDs, this, "plane_ADGH");
      }
      {
        Polygon3d poly = new Polygon3d(a1, d1, g, h);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A1"));
        pointsIDs.add(getAnchorID("D1"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("H"));
        edt.addAnchor(poly, pointsIDs, this, "plane_A1D1GH");
      }
      {
        Polygon3d poly = new Polygon3d(a1, b1, j, i);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A1"));
        pointsIDs.add(getAnchorID("B1"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("I"));
        edt.addAnchor(poly, pointsIDs, this, "plane_A1B1JI");
      } 
      {
        Polygon3d poly = new Polygon3d(a, b, j, i);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("I"));
        edt.addAnchor(poly, pointsIDs, this, "plane_");
      }
      {
        Polygon3d poly = new Polygon3d(c, b, j, e);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("E"));
        edt.addAnchor(poly, pointsIDs, this, "plane_CBJE");
      } 
      {
        Polygon3d poly = new Polygon3d(c1, b1, j, e);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C1"));
        pointsIDs.add(getAnchorID("B1"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("E"));
        edt.addAnchor(poly, pointsIDs, this, "plane_C1B1JE");
      } 
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add plane of rhombicDodecahedron: " + ex.getMessage());
    }

  }
  
   /**
   * Construct rhombic dodecahedron section by plane
   * @param id
   * @param p
   * @return
   * @throws ExGeom
   * @throws ExZeroDivision
   */
  public PolygonBody section(String id, PlaneBody p)
    throws ExGeom, ExZeroDivision {
    try {
      Polygon3d poly = _dod.sectionByPlane(new Plane3d(p.normal(), p.point()));
      if (poly.points().isEmpty())
        throw new ExGeom("ромбододекаэдр и плоскость не пересекаются");
      // возвращается новый полигон с параметрами из PolygonBody3d
      return new PolygonBody(id, poly.points()); 
    } catch (NullPointerException ex) {
      throw new ExGeom("плоскость не была построена");
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
      faces = _dod.faces();
    } catch (ExGeom e) {
      Log.out.println(e.getMessage());
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
    return _dod;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new RhombicDodecahedronBody(id, title, (RhombicDodecahedron3d) geom);
  }

  public void addPointAnchors(ArrayList<Vect3d> points, i_Body result, Editor edt) {
    edt.addAnchor(points.get(0), result, "A");
    edt.addAnchor(points.get(1), result, "B");
    edt.addAnchor(points.get(2), result, "C");
    edt.addAnchor(points.get(3), result, "D");
    edt.addAnchor(points.get(4), result, "A1");
    edt.addAnchor(points.get(5), result, "B1");
    edt.addAnchor(points.get(6), result, "C1");
    edt.addAnchor(points.get(7), result, "D1");
    edt.addAnchor(points.get(8), result, "E");
    edt.addAnchor(points.get(9), result, "F");
    edt.addAnchor(points.get(10), result, "G");
    edt.addAnchor(points.get(11), result, "H");
    edt.addAnchor(points.get(12), result, "I");
    edt.addAnchor(points.get(13), result, "J");
    
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    RhombicDodecahedronBody dod = (RhombicDodecahedronBody) result;
    
    edt.addAnchor(dod.A(), result, "A");
    edt.addAnchor(dod.B(), result, "B");
    edt.addAnchor(dod.C(), result, "C");
    edt.addAnchor(dod.D(), result, "D");
    edt.addAnchor(dod.A1(), result, "A1");
    edt.addAnchor(dod.B1(), result, "B1");
    edt.addAnchor(dod.C1(), result, "C1");
    edt.addAnchor(dod.D1(), result, "D1");
    edt.addAnchor(dod.E(), result, "E");
    edt.addAnchor(dod.F(), result, "F");
    edt.addAnchor(dod.G(), result, "G");
    edt.addAnchor(dod.H(), result, "H");
    edt.addAnchor(dod.I(), result, "I");
    edt.addAnchor(dod.J(), result, "J");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_dod.intersect(ray), ren.getCameraPosition().eye());
  }
};
