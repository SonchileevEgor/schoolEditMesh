package bodies;

import static config.Config.LOG_LEVEL;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.AbstractPolygon;
import geom.ExGeom;
import geom.ElongatedDodecahedron3d;
import geom.ExZeroDivision;
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
 * Meta body - elongatedDodecahedron.
 *
 */
public class ElongatedDodecahedronBody extends BodyAdapter {
  private ElongatedDodecahedron3d _dod; // Math object polygon

  /**
   * Constructor of polygon by the geom object.
   *
   * @param id
   * @param title
   * @param dod
   */
  public ElongatedDodecahedronBody(String id, String title, ElongatedDodecahedron3d dod) {
    super(id, title);
    _dod = dod;
    _alias = "удлинённый додекаэдр";
    _exists = true;
  }

  /**
   * Constructor of elongated_dodecahedron
   *
   * @param id
   * @param title
   * @param a
   * @param b
   * @param angle
   * @throws geom.ExGeom
   */
  public ElongatedDodecahedronBody(String id, String title, Vect3d a, Vect3d b, double angle) throws ExGeom {
    super(id, title);
    _dod = new ElongatedDodecahedron3d(a, b, angle);
    _alias = "удлинённый додекаэдр";
    _exists = true;
  }

  /**
   * constructor for null-body
   *
   * @param id
   * @param title
   */
  public ElongatedDodecahedronBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  /**
   * @return math object polygon
   */
  public ElongatedDodecahedron3d elongatedDodecahedron() {
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

  public Vect3d E() {
    return _dod.E();
  }

  public Vect3d F() {
    return _dod.F();
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

  public Vect3d E1() {
    return _dod.E1();
  }

  public Vect3d F1() {
    return _dod.F1();
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

  public Vect3d K() {
    return _dod.K();
  }

  public Vect3d L() {
    return _dod.L();
  }

  @Override
  public BodyType type() {
    return BodyType.ELONGATED_DODECAHEDRON;
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
    Vect3d h = H();
    Vect3d i = I();
    Vect3d j = J();
    Vect3d k = K();
    Vect3d l = L();
    Vect3d g = G();

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
      

      Rib3d rib13 = new Rib3d(a, g);   
      edt.addAnchor(rib13, getAnchorID("A"), getAnchorID("G"), this, "rib_AG");
      Rib3d rib14 = new Rib3d(g, a1);
      edt.addAnchor(rib14, getAnchorID("G"), getAnchorID("A1"), this, "rib_GA1");
      Rib3d rib15 = new Rib3d(b, h);
      edt.addAnchor(rib15, getAnchorID("B"), getAnchorID("H"), this, "rib_BH");
      Rib3d rib16 = new Rib3d(h, b1);
      edt.addAnchor(rib16, getAnchorID("H"), getAnchorID("B1"), this, "rib_HB1");
      Rib3d rib17 = new Rib3d(d, j);
      edt.addAnchor(rib17, getAnchorID("D"), getAnchorID("J"), this, "rib_DJ");
      Rib3d rib18 = new Rib3d(j, d1);
      edt.addAnchor(rib18, getAnchorID("J"), getAnchorID("D1"), this, "rib_JD1");
      Rib3d rib19 = new Rib3d(e, k);
      edt.addAnchor(rib19, getAnchorID("E"), getAnchorID("K"), this, "rib_EK");
      Rib3d rib20 = new Rib3d(k, e1);
      edt.addAnchor(rib20, getAnchorID("K"), getAnchorID("E1"), this, "rib_KE1");
      Rib3d rib21 = new Rib3d(c, i);
      edt.addAnchor(rib21, getAnchorID("C"), getAnchorID("I"), this, "rib_CI");
      Rib3d rib22 = new Rib3d(h, i);
      edt.addAnchor(rib22, getAnchorID("H"), getAnchorID("I"), this, "rib_HI");
      Rib3d rib23 = new Rib3d(j, i);
      edt.addAnchor(rib23, getAnchorID("J"), getAnchorID("I"), this, "rib_JI");
      Rib3d rib24 = new Rib3d(c1, i);
      edt.addAnchor(rib24, getAnchorID("C1"), getAnchorID("I"), this, "rib_C1I");
      Rib3d rib25 = new Rib3d(f, l);
      edt.addAnchor(rib25, getAnchorID("F"), getAnchorID("L"), this, "rib_FL");
      Rib3d rib26 = new Rib3d(g, l);
      edt.addAnchor(rib26, getAnchorID("G"), getAnchorID("L"), this, "rib_GL");
      Rib3d rib27 = new Rib3d(f1, l);
      edt.addAnchor(rib27, getAnchorID("F1"), getAnchorID("L"), this, "rib_F1L");
      Rib3d rib28 = new Rib3d(k, l);
      edt.addAnchor(rib28, getAnchorID("K"), getAnchorID("L"), this, "rib_KL");
      
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add Plane of elongatedDodecahedron: " + ex.getMessage());
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
    Vect3d h = H();
    Vect3d i = I();
    Vect3d j = J();
    Vect3d k = K();
    Vect3d l = L();
    Vect3d g = G();

    try {
      {
        Polygon3d poly = new Polygon3d(a, b, c, d, e, f);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("F"));
        edt.addAnchor(poly, pointsIDs, this, "plane_ABCDEF");
      }
      {
        Polygon3d poly = new Polygon3d(a1, b1, c1, d1, e1, f1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A1"));
        pointsIDs.add(getAnchorID("B1"));
        pointsIDs.add(getAnchorID("C1"));
        pointsIDs.add(getAnchorID("D1"));
        pointsIDs.add(getAnchorID("E1"));
        pointsIDs.add(getAnchorID("F1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_A1B1C1D1E1F1");
      }
      {
        Polygon3d poly = new Polygon3d(a, b, h, b1, a1, g);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("B1"));
        pointsIDs.add(getAnchorID("A1"));
        pointsIDs.add(getAnchorID("G"));
        edt.addAnchor(poly, pointsIDs, this, "plane_ABHB1A1G");
      }
      {
        Polygon3d poly = new Polygon3d(e, d, j, d1, e1, k);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("D1"));
        pointsIDs.add(getAnchorID("E1"));
        pointsIDs.add(getAnchorID("K"));
        edt.addAnchor(poly, pointsIDs, this, "plane_EDJD1E1K");
      }
      {
        Polygon3d poly = new Polygon3d(b, c, i, h);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("H"));
        edt.addAnchor(poly, pointsIDs, this, "plane_BCIH");
      }
      {
        Polygon3d poly = new Polygon3d(b1, h, i, c1);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("B1"));
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("C1"));
        edt.addAnchor(poly, pointsIDs, this, "plane_B1HIC1");
      }
      {
        Polygon3d poly = new Polygon3d(d, c, i, j);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("J"));
        edt.addAnchor(poly, pointsIDs, this, "plane_DCIJ");
      }
      {
        Polygon3d poly = new Polygon3d(c1, d1, j, i);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C1"));
        pointsIDs.add(getAnchorID("D1"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("I"));
        edt.addAnchor(poly, pointsIDs, this, "plane_C1, D1, J, I");
      }
      {
        Polygon3d poly = new Polygon3d(a, f, l, g);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("G"));
        edt.addAnchor(poly, pointsIDs, this, "plane_AFLG");
      }
      {
        Polygon3d poly = new Polygon3d(a1, f1, l, g);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A1"));
        pointsIDs.add(getAnchorID("F1"));
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("G"));
        edt.addAnchor(poly, pointsIDs, this, "plane_A1F1LG");
      }
      {
        Polygon3d poly = new Polygon3d(e1, f1, l, k);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("E1"));
        pointsIDs.add(getAnchorID("F1"));
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("K"));
        edt.addAnchor(poly, pointsIDs, this, "plane_E1F1LK");
      }
      {
        Polygon3d poly = new Polygon3d(e, f, l, k);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("K"));
        edt.addAnchor(poly, pointsIDs, this, "plane_EFLK");
      }
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add Plane of elongatedDodecahedron: " + ex.getMessage());
    }

  }
  
   /**
   * Construct elongated dodecahedron section by plane
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
        throw new ExGeom("удлиненный додекаэдр и плоскость не пересекаются");
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
    return _dod;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new ElongatedDodecahedronBody(id, title, (ElongatedDodecahedron3d) geom);
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
    edt.addAnchor(points.get(12), result, "H");
    edt.addAnchor(points.get(13), result, "I");
    edt.addAnchor(points.get(14), result, "J");
    edt.addAnchor(points.get(15), result, "K");
    edt.addAnchor(points.get(16), result, "L");
    edt.addAnchor(points.get(17), result, "G");
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    ElongatedDodecahedronBody dod = (ElongatedDodecahedronBody) result;
    
    edt.addAnchor(dod.A(), result, "A");
    edt.addAnchor(dod.B(), result, "B");
    edt.addAnchor(dod.C(), result, "C");
    edt.addAnchor(dod.D(), result, "D");
    edt.addAnchor(dod.E(), result, "E");
    edt.addAnchor(dod.F(), result, "F");
    edt.addAnchor(dod.A1(), result, "A1");
    edt.addAnchor(dod.B1(), result, "B1");
    edt.addAnchor(dod.C1(), result, "C1");
    edt.addAnchor(dod.D1(), result, "D1");
    edt.addAnchor(dod.E1(), result, "E1");
    edt.addAnchor(dod.F1(), result, "F1");
    edt.addAnchor(dod.H(), result, "H");
    edt.addAnchor(dod.I(), result, "I");
    edt.addAnchor(dod.J(), result, "J");
    edt.addAnchor(dod.K(), result, "K");
    edt.addAnchor(dod.L(), result, "L");
    edt.addAnchor(dod.G(), result, "G");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_dod.intersect(ray), ren.getCameraPosition().eye());
  }
};
