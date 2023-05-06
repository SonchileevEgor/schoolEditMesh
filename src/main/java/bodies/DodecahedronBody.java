package bodies;

import static config.Config.LOG_LEVEL;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.AbstractPolygon;
import geom.Dodecahedron3d;
import geom.ExGeom;
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
 * Meta body - dodecahedron.
 *
 */
public class DodecahedronBody extends BodyAdapter {
  private Dodecahedron3d _dodecahedron; // Math object polygon
  
  /**
   * Constructor of dodecahedron by the geom object.
   *
   * @param id
   * @param title
   * @param dod
   */
  public DodecahedronBody(String id, String title, Dodecahedron3d dod) {
    super(id, title);
    _dodecahedron = dod;
    _alias = "додекаэдр";
    _exists = true;
  }

  /**
   * Constructor of dodecahedron by list of points
   *
   * @param id
   * @param title
   * @param a
   * @param b
   * @param angle
   * @throws geom.ExGeom
   */
  public DodecahedronBody(String id, String title, Vect3d a, Vect3d b, double angle) throws ExGeom {
    super(id, title);
    _dodecahedron = new Dodecahedron3d(a, b, angle);
    _alias = "додекаэдр";
    _exists = true;
  }

  /**
   * constructor for null-body
   *
   * @param id
   * @param title
   */
  public DodecahedronBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  /**
   * @return math object dodecahedron
   */
  public Dodecahedron3d dodecahedron() {
    return _dodecahedron;
  }

  public Vect3d A() {
    return _dodecahedron.A();
  }

  public Vect3d B() {
    return _dodecahedron.B();
  }

  public Vect3d C() {
    return _dodecahedron.C();
  }

  public Vect3d D() {
    return _dodecahedron.D();
  }

  public Vect3d E() {
    return _dodecahedron.E();
  }

  public Vect3d F() {
    return _dodecahedron.F();
  }

  public Vect3d G() {
    return _dodecahedron.G();
  }

  public Vect3d H() {
    return _dodecahedron.H();
  }

  public Vect3d I() {
    return _dodecahedron.I();
  }

  public Vect3d J() {
    return _dodecahedron.J();
  }

  public Vect3d K() {
    return _dodecahedron.K();
  }

  public Vect3d L() {
    return _dodecahedron.L();
  }

  public Vect3d M() {
    return _dodecahedron.M();
  }

  public Vect3d N() {
    return _dodecahedron.N();
  }

  public Vect3d O() {
    return _dodecahedron.O();
  }

  public Vect3d P() {
    return _dodecahedron.P();
  }

  public Vect3d Q() {
    return _dodecahedron.Q();
  }

  public Vect3d R() {
    return _dodecahedron.R();
  }

  public Vect3d S() {
    return _dodecahedron.S();
  }

  public Vect3d T() {
    return _dodecahedron.T();
  }

  @Override
  public BodyType type() {
    return BodyType.DODECAHEDRON;
  }

  @Override
  public void addRibs(Editor edt) {
    Vect3d a = A();
    Vect3d b = B();
    Vect3d c = C();
    Vect3d d = D();
    Vect3d e = E();
    Vect3d f = F();
    Vect3d g = G();
    Vect3d h = H();
    Vect3d i = I();
    Vect3d j = J();

    Vect3d k = K();
    Vect3d l = L();
    Vect3d m = M();
    Vect3d n = N();
    Vect3d o = O();
    Vect3d p = P();
    Vect3d q = Q();
    Vect3d r = R();
    Vect3d s = S();
    Vect3d t = T();

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
      Rib3d rib6 = new Rib3d(f, g);
      edt.addAnchor(rib6, getAnchorID("F"), getAnchorID("G"), this, "rib_FG");
      Rib3d rib7 = new Rib3d(g, h);
      edt.addAnchor(rib7, getAnchorID("G"), getAnchorID("H"), this, "rib_GH");
      Rib3d rib8 = new Rib3d(h, i);
      edt.addAnchor(rib8, getAnchorID("H"), getAnchorID("I"), this, "rib_HI");
      Rib3d rib9 = new Rib3d(i, j);
      edt.addAnchor(rib9, getAnchorID("I"), getAnchorID("J"), this, "rib_IJ");
      Rib3d rib10 = new Rib3d(j, f);
      edt.addAnchor(rib10, getAnchorID("J"), getAnchorID("F"), this, "rib_JF");

      Rib3d rib11 = new Rib3d(a, k);
      edt.addAnchor(rib11, getAnchorID("A"), getAnchorID("K"), this, "rib_AK");
      Rib3d rib12 = new Rib3d(b, l);
      edt.addAnchor(rib12, getAnchorID("B"), getAnchorID("L"), this, "rib_BL");
      Rib3d rib13 = new Rib3d(c, m);
      edt.addAnchor(rib13, getAnchorID("C"), getAnchorID("M"), this, "rib_CM");
      Rib3d rib14 = new Rib3d(d, n);
      edt.addAnchor(rib14, getAnchorID("D"), getAnchorID("N"), this, "rib_DN");
      Rib3d rib15 = new Rib3d(e, o);
      edt.addAnchor(rib15, getAnchorID("E"), getAnchorID("O"), this, "rib_EO");
      Rib3d rib16 = new Rib3d(f, p);
      edt.addAnchor(rib16, getAnchorID("F"), getAnchorID("P"), this, "rib_FP");
      Rib3d rib17 = new Rib3d(g, q);
      edt.addAnchor(rib17, getAnchorID("G"), getAnchorID("Q"), this, "rib_GQ");
      Rib3d rib18 = new Rib3d(h, r);
      edt.addAnchor(rib18, getAnchorID("H"), getAnchorID("R"), this, "rib_HR");
      Rib3d rib19 = new Rib3d(i, s);
      edt.addAnchor(rib19, getAnchorID("I"), getAnchorID("S"), this, "rib_IS");
      Rib3d rib20 = new Rib3d(j, t);
      edt.addAnchor(rib20, getAnchorID("J"), getAnchorID("T"), this, "rib_JT");

      Rib3d rib21 = new Rib3d(k, p);
      edt.addAnchor(rib21, getAnchorID("K"), getAnchorID("P"), this, "rib_KP");
      Rib3d rib22 = new Rib3d(p, l);
      edt.addAnchor(rib22, getAnchorID("P"), getAnchorID("L"), this, "rib_PL");
      Rib3d rib23 = new Rib3d(l, q);
      edt.addAnchor(rib23, getAnchorID("L"), getAnchorID("Q"), this, "rib_LQ");
      Rib3d rib24 = new Rib3d(q, m);
      edt.addAnchor(rib24, getAnchorID("Q"), getAnchorID("M"), this, "rib_QM");
      Rib3d rib25 = new Rib3d(m, r);
      edt.addAnchor(rib25, getAnchorID("M"), getAnchorID("R"), this, "rib_MR");
      Rib3d rib26 = new Rib3d(r, n);
      edt.addAnchor(rib26, getAnchorID("R"), getAnchorID("N"), this, "rib_RN");
      Rib3d rib27 = new Rib3d(n, s);
      edt.addAnchor(rib27, getAnchorID("N"), getAnchorID("S"), this, "rib_NS");
      Rib3d rib28 = new Rib3d(s, o);
      edt.addAnchor(rib28, getAnchorID("S"), getAnchorID("O"), this, "rib_SO");
      Rib3d rib29 = new Rib3d(o, t);
      edt.addAnchor(rib29, getAnchorID("O"), getAnchorID("T"), this, "rib_OT");
      Rib3d rib30 = new Rib3d(t, k);
      edt.addAnchor(rib30, getAnchorID("T"), getAnchorID("K"), this, "rib_TK");

    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add plane of dodecahedron: " + ex.getMessage());
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
    Vect3d g = G();
    Vect3d h = H();
    Vect3d i = I();
    Vect3d j = J();

    Vect3d k = K();
    Vect3d l = L();
    Vect3d m = M();
    Vect3d n = N();
    Vect3d o = O();
    Vect3d p = P();
    Vect3d r = Q();
    Vect3d s = R();
    Vect3d t = S();
    Vect3d u = T();

    try {
      {
        Polygon3d poly = new Polygon3d(a, b, c, d, e);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("E"));
        edt.addAnchor(poly, pointsIDs, this, "plane_ABCDE");
      }
      {
        Polygon3d poly = new Polygon3d(f, g, h, i, j);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("J"));
        edt.addAnchor(poly, pointsIDs, this, "plane_FGHIJ");
      }
      {
        Polygon3d poly = new Polygon3d(a, b, l, p, k);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("P"));
        pointsIDs.add(getAnchorID("K"));
        edt.addAnchor(poly, pointsIDs, this, "plane_ABLPK");
      }
      {
        Polygon3d poly = new Polygon3d(b, c, m, r, l);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("M"));
        pointsIDs.add(getAnchorID("Q"));
        pointsIDs.add(getAnchorID("L"));
        edt.addAnchor(poly, pointsIDs, this, "plane_BCMQL");
      }
      {
        Polygon3d poly = new Polygon3d(c, d, n, s, m);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("N"));
        pointsIDs.add(getAnchorID("R"));
        pointsIDs.add(getAnchorID("M"));
        edt.addAnchor(poly, pointsIDs, this, "plane_CDNRM");
      }
      {
        Polygon3d poly = new Polygon3d(d, e, o, t, n);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("O"));
        pointsIDs.add(getAnchorID("S"));
        pointsIDs.add(getAnchorID("N"));
        edt.addAnchor(poly, pointsIDs, this, "plane_DEOSN");
      }
      {
        Polygon3d poly = new Polygon3d(e, a, k, u, o);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("K"));
        pointsIDs.add(getAnchorID("T"));
        pointsIDs.add(getAnchorID("O"));
        edt.addAnchor(poly, pointsIDs, this, "plane_EAKTO");
      }
      {
        Polygon3d poly = new Polygon3d(j, f, p, k, u);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("P"));
        pointsIDs.add(getAnchorID("K"));
        pointsIDs.add(getAnchorID("T"));
        edt.addAnchor(poly, pointsIDs, this, "plane_JFPKT");
      }
      {
        Polygon3d poly = new Polygon3d(f, g, r, l, p);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("Q"));
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("P"));
        edt.addAnchor(poly, pointsIDs, this, "plane_FGQLP");
      }
      {
        Polygon3d poly = new Polygon3d(g, h, s, m, r);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("R"));
        pointsIDs.add(getAnchorID("M"));
        pointsIDs.add(getAnchorID("Q"));
        edt.addAnchor(poly, pointsIDs, this, "plane_GHRMQ");
      }
      {
        Polygon3d poly = new Polygon3d(h, i, t, n, s);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("S"));
        pointsIDs.add(getAnchorID("N"));
        pointsIDs.add(getAnchorID("R"));
        edt.addAnchor(poly, pointsIDs, this, "plane_HISNR");
      }
      {
        Polygon3d poly = new Polygon3d(i, j, u, o, t);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("T"));
        pointsIDs.add(getAnchorID("O"));
        pointsIDs.add(getAnchorID("S"));
        edt.addAnchor(poly, pointsIDs, this, "plane_IJOTS");
      }
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add Plane of dodecahedron: " + ex.getMessage());
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
      faces = _dodecahedron.faces();
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
   * Construct dodecahedron section by plane
   * @param id
   * @param p
   * @return
   * @throws ExGeom
   * @throws ExZeroDivision
   */
  public PolygonBody section(String id, PlaneBody p)
    throws ExGeom, ExZeroDivision {
    try {
      Polygon3d poly = _dodecahedron.sectionByPlane(new Plane3d(p.normal(), p.point()));
      if (poly.points().isEmpty())
        throw new ExGeom("додекаэдр и плоскость не пересекаются");
      // возвращается новый полигон с параметрами из PolygonBody3d
      return new PolygonBody(id, poly.points()); 
    } catch (NullPointerException ex) {
      throw new ExGeom("плоскость не была построена");
    }
  }

  @Override
  public i_Geom getGeom() {
    return (i_Geom) _dodecahedron;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new DodecahedronBody(id, title, (Dodecahedron3d) geom);
  }

  public void addPointAnchors(ArrayList<Vect3d> points, i_Body result, Editor edt) {
    edt.addAnchor(points.get(0), result, "A");
    edt.addAnchor(points.get(1), result, "B");
    edt.addAnchor(points.get(2), result, "C");
    edt.addAnchor(points.get(3), result, "D");
    edt.addAnchor(points.get(4), result, "E");
    edt.addAnchor(points.get(5), result, "F");
    edt.addAnchor(points.get(6), result, "G");
    edt.addAnchor(points.get(7), result, "H");
    edt.addAnchor(points.get(8), result, "I");
    edt.addAnchor(points.get(9), result, "J");
    edt.addAnchor(points.get(10), result, "K");
    edt.addAnchor(points.get(11), result, "L");
    edt.addAnchor(points.get(12), result, "M");
    edt.addAnchor(points.get(13), result, "N");
    edt.addAnchor(points.get(14), result, "O");
    edt.addAnchor(points.get(15), result, "P");
    edt.addAnchor(points.get(16), result, "Q");
    edt.addAnchor(points.get(17), result, "R");
    edt.addAnchor(points.get(18), result, "S");
    edt.addAnchor(points.get(19), result, "T");
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    DodecahedronBody dod = (DodecahedronBody) result;

    edt.addAnchor(dod.C(), result, "A");
    edt.addAnchor(dod.D(), result, "B");  
    edt.addAnchor(dod.C(), result, "C");
    edt.addAnchor(dod.D(), result, "D");
    edt.addAnchor(dod.E(), result, "E");
    edt.addAnchor(dod.F(), result, "F");
    edt.addAnchor(dod.G(), result, "G");
    edt.addAnchor(dod.H(), result, "H");
    edt.addAnchor(dod.I(), result, "I");
    edt.addAnchor(dod.J(), result, "J");
    edt.addAnchor(dod.K(), result, "K");
    edt.addAnchor(dod.L(), result, "L");
    edt.addAnchor(dod.M(), result, "M");
    edt.addAnchor(dod.N(), result, "N");
    edt.addAnchor(dod.O(), result, "O");
    edt.addAnchor(dod.P(), result, "P");
    edt.addAnchor(dod.Q(), result, "Q");
    edt.addAnchor(dod.R(), result, "R");
    edt.addAnchor(dod.S(), result, "S");
    edt.addAnchor(dod.T(), result, "T");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_dodecahedron.intersect(ray), ren.getCameraPosition().eye());
  }
};