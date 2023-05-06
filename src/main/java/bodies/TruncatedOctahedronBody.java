package bodies;

import static config.Config.LOG_LEVEL;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.AbstractPolygon;
import geom.ExGeom;
import geom.ExZeroDivision;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Ray3d;
import geom.Rib3d;
import geom.TruncatedOctahedron3d;
import geom.Vect3d;
import geom.i_Geom;
import java.util.ArrayList;
import util.Log;
import util.Util;

/**
 * Meta body - TruncatedOctahedron.
 *
 */
public class TruncatedOctahedronBody extends BodyAdapter {
  private TruncatedOctahedron3d _oct; // Math object polygon

  /**
   * Constructor of polygon by the geom object.
   *
   * @param id
   * @param title
   * @param oct
   */
  public TruncatedOctahedronBody(String id, String title, TruncatedOctahedron3d oct) {
    super(id, title);
    _oct = oct;
    _alias = "усечённый октаэдр";
    _exists = true;
  }

  /**
   * Constructor of TruncatedOctahedron
   *
   * @param id
   * @param title
   * @param a
   * @param b
   * @param angle
   * @throws geom.ExGeom
   */
  public TruncatedOctahedronBody(String id, String title, Vect3d a, Vect3d b, double angle) throws ExGeom {
    super(id, title);
    _oct = new TruncatedOctahedron3d(a, b, angle);
    _alias = "усечённый октаэдр";
    _exists = true;
  }

  /**
   * constructor for null-body
   *
   * @param id
   * @param title
   */
  public TruncatedOctahedronBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  /**
   * @return math object polygon
   */
  public TruncatedOctahedron3d truncatedOctahedron() {
    return _oct;
  }

  // Getters for all vertexs of truncatedOctahedron
  public Vect3d A() {
    return _oct.A();
  }

  public Vect3d B() {
    return _oct.B();
  }

  public Vect3d C() {
    return _oct.C();
  }

  public Vect3d D() {
    return _oct.D();
  }

  public Vect3d E() {
    return _oct.E();
  }

  public Vect3d F() {
    return _oct.F();
  }

  public Vect3d G() {
    return _oct.G();
  }

  public Vect3d H() {
    return _oct.H();
  }

  public Vect3d I() {
    return _oct.I();
  }

  public Vect3d J() {
    return _oct.J();
  }

  public Vect3d K() {
    return _oct.K();
  }

  public Vect3d L() {
    return _oct.L();
  }

  public Vect3d M() {
    return _oct.M();
  }

  public Vect3d N() {
    return _oct.N();
  }

  public Vect3d O() {
    return _oct.O();
  }

  public Vect3d P() {
    return _oct.P();
  }
  
  public Vect3d Q() {
    return _oct.Q();
  }

  public Vect3d R() {
    return _oct.R();
  }

  public Vect3d S() {
    return _oct.S();
  }

  public Vect3d T() {
    return _oct.T();
  }

  public Vect3d U() {
    return _oct.U();
  }

  public Vect3d V() {
    return _oct.V();
  }

  public Vect3d W() {
    return _oct.W();
  }

  public Vect3d X() {
    return _oct.X();
  }

  @Override
  public BodyType type() {
    return BodyType.TRUNCATED_OCTAHEDRON;
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
    Vect3d u = U();
    Vect3d v = V();
    Vect3d w = W();
    Vect3d x = X();

    try {
      
      Rib3d rib1 = new Rib3d(i, j);
      edt.addAnchor(rib1, getAnchorID("I"), getAnchorID("J"), this, "rib_IJ");
      Rib3d rib2 = new Rib3d(j, k);
      edt.addAnchor(rib2, getAnchorID("J"), getAnchorID("K"), this, "rib_JK");
      Rib3d rib3 = new Rib3d(k, l);
      edt.addAnchor(rib3, getAnchorID("K"), getAnchorID("L"), this, "rib_KL");
      Rib3d rib4 = new Rib3d(l, i);
      edt.addAnchor(rib4, getAnchorID("L"), getAnchorID("I"), this, "rib_LI");
      
      Rib3d rib5 = new Rib3d(i, t);
      edt.addAnchor(rib5, getAnchorID("I"), getAnchorID("T"), this, "rib_IT");
      Rib3d rib6 = new Rib3d(j, x);
      edt.addAnchor(rib6, getAnchorID("J"), getAnchorID("X"), this, "rib_JX");
      Rib3d rib7 = new Rib3d(k, r);
      edt.addAnchor(rib7, getAnchorID("K"), getAnchorID("R"), this, "rib_KR");
      Rib3d rib8 = new Rib3d(l, v);
      edt.addAnchor(rib8, getAnchorID("L"), getAnchorID("V"), this, "rib_LV");
      
      Rib3d rib9 = new Rib3d(t, b);
      edt.addAnchor(rib9, getAnchorID("T"), getAnchorID("B"), this, "rib_TB");
      Rib3d rib10 = new Rib3d(b, s);
      edt.addAnchor(rib10, getAnchorID("B"), getAnchorID("S"), this, "rib_BS");
      Rib3d rib11 = new Rib3d(s, c);
      edt.addAnchor(rib11, getAnchorID("S"), getAnchorID("C"), this, "rib_SC");
      Rib3d rib12 = new Rib3d(c, t);
      edt.addAnchor(rib12, getAnchorID("C"), getAnchorID("T"), this, "rib_CT");
      
      Rib3d rib13 = new Rib3d(x, d);
      edt.addAnchor(rib13, getAnchorID("X"), getAnchorID("D"), this, "rib_XD");
      Rib3d rib14 = new Rib3d(d, w);
      edt.addAnchor(rib14, getAnchorID("D"), getAnchorID("W"), this, "rib_DW");
      Rib3d rib15 = new Rib3d(w, e);
      edt.addAnchor(rib15, getAnchorID("W"), getAnchorID("E"), this, "rib_WE");
      Rib3d rib16 = new Rib3d(e, x);
      edt.addAnchor(rib16, getAnchorID("E"), getAnchorID("X"), this, "rib_EX");
      
      Rib3d rib17 = new Rib3d(r, f);
      edt.addAnchor(rib17, getAnchorID("R"), getAnchorID("F"), this, "rib_RF");
      Rib3d rib18 = new Rib3d(f, q);
      edt.addAnchor(rib18, getAnchorID("F"), getAnchorID("Q"), this, "rib_FQ");
      Rib3d rib19 = new Rib3d(q, g);
      edt.addAnchor(rib19, getAnchorID("Q"), getAnchorID("G"), this, "rib_QG");
      Rib3d rib20 = new Rib3d(g, r);
      edt.addAnchor(rib20, getAnchorID("G"), getAnchorID("R"), this, "rib_GR");

      Rib3d rib21 = new Rib3d(v, h);
      edt.addAnchor(rib21, getAnchorID("V"), getAnchorID("H"), this, "rib_VH");
      Rib3d rib22 = new Rib3d(h, u);
      edt.addAnchor(rib22, getAnchorID("H"), getAnchorID("U"), this, "rib_HU");
      Rib3d rib23 = new Rib3d(u, a);
      edt.addAnchor(rib23, getAnchorID("U"), getAnchorID("A"), this, "rib_UA");
      Rib3d rib24 = new Rib3d(a, v);
      edt.addAnchor(rib24, getAnchorID("A"), getAnchorID("V"), this, "rib_AV");
      
      Rib3d rib25 = new Rib3d(w, n);
      edt.addAnchor(rib25, getAnchorID("W"), getAnchorID("N"), this, "rib_WN");
      Rib3d rib26 = new Rib3d(s, m);
      edt.addAnchor(rib26, getAnchorID("S"), getAnchorID("M"), this, "rib_SM");
      Rib3d rib27 = new Rib3d(u, p);
      edt.addAnchor(rib27, getAnchorID("U"), getAnchorID("P"), this, "rib_UP");
      Rib3d rib28 = new Rib3d(q, o);
      edt.addAnchor(rib28, getAnchorID("Q"), getAnchorID("O"), this, "rib_QO");
      
      Rib3d rib29 = new Rib3d(o, p);
      edt.addAnchor(rib29, getAnchorID("O"), getAnchorID("P"), this, "rib_OP");
      Rib3d rib30 = new Rib3d(p, m);
      edt.addAnchor(rib30, getAnchorID("P"), getAnchorID("M"), this, "rib_PM");
      Rib3d rib31 = new Rib3d(m, n);
      edt.addAnchor(rib31, getAnchorID("M"), getAnchorID("N"), this, "rib_MN");
      Rib3d rib32 = new Rib3d(n, o);
      edt.addAnchor(rib32, getAnchorID("N"), getAnchorID("O"), this, "rib_NO");
      
      Rib3d rib33 = new Rib3d(a, b);
      edt.addAnchor(rib33, getAnchorID("A"), getAnchorID("B"), this, "rib_AB");
      Rib3d rib34 = new Rib3d(d, c);
      edt.addAnchor(rib34, getAnchorID("D"), getAnchorID("C"), this, "rib_DC");
      Rib3d rib35 = new Rib3d(e, f);
      edt.addAnchor(rib35, getAnchorID("E"), getAnchorID("F"), this, "rib_EF");
      Rib3d rib36 = new Rib3d(g, h);
      edt.addAnchor(rib36, getAnchorID("G"), getAnchorID("H"), this, "rib_GH");      
      
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add Plane of truncatedOctahedron: " + ex.getMessage());
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
    Vect3d v = U();
    Vect3d w = V();
    Vect3d x = W();
    Vect3d y = X();

    try {
      {
        Polygon3d poly = new Polygon3d(k, j, i, l);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("K"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("L"));
        edt.addAnchor(poly, pointsIDs, this, "plane_KJIL");
      }
      {
        Polygon3d poly = new Polygon3d(b, u, c, t);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("T"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("S"));
        edt.addAnchor(poly, pointsIDs, this, "plane_BTCS");
      }
      {
        Polygon3d poly = new Polygon3d(s, f, r, g);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("R"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("Q"));
        pointsIDs.add(getAnchorID("G"));
        edt.addAnchor(poly, pointsIDs, this, "plane_RFQG");
      }
      {
        Polygon3d poly = new Polygon3d(o, p, m, n);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("O"));
        pointsIDs.add(getAnchorID("P"));
        pointsIDs.add(getAnchorID("M"));
        pointsIDs.add(getAnchorID("N"));
        edt.addAnchor(poly, pointsIDs, this, "plane_OPMN");
      }
      {
        Polygon3d poly = new Polygon3d(h, w, a, v);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("V"));
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("U"));
        edt.addAnchor(poly, pointsIDs, this, "plane_HVAU");
      }
      {
        Polygon3d poly = new Polygon3d(y, e, x, d);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("X"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("W"));
        pointsIDs.add(getAnchorID("D"));
        edt.addAnchor(poly, pointsIDs, this, "plane_XEWD");
      }
      {
        Polygon3d poly = new Polygon3d(l, i, u, b, a, w);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("T"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("V"));
        edt.addAnchor(poly, pointsIDs, this, "plane_LITBAV");
      }
      {
        Polygon3d poly = new Polygon3d(l, k, s, g, h, w);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("K"));
        pointsIDs.add(getAnchorID("R"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("V"));
        edt.addAnchor(poly, pointsIDs, this, "plane_LKRGHV");
      }
      {
        Polygon3d poly = new Polygon3d(p, m, t, b, a, v);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("P"));
        pointsIDs.add(getAnchorID("M"));
        pointsIDs.add(getAnchorID("S"));
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("U"));
        edt.addAnchor(poly, pointsIDs, this, "plane_PMSBAU");
      }
      {
        Polygon3d poly = new Polygon3d(p, o, r, g, h, v);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("P"));
        pointsIDs.add(getAnchorID("O"));
        pointsIDs.add(getAnchorID("Q"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("U"));
        edt.addAnchor(poly, pointsIDs, this, "plane_POQGHU");
      }
      {
        Polygon3d poly = new Polygon3d(i, j, y, d, c, u);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("X"));
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("T"));
        edt.addAnchor(poly, pointsIDs, this, "plane_IJXDCT");
      }
      {
        Polygon3d poly = new Polygon3d(j, k, s, f, e, y);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("K"));
        pointsIDs.add(getAnchorID("R"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("X"));
        edt.addAnchor(poly, pointsIDs, this, "plane_JKRFEX");
      }
      {
        Polygon3d poly = new Polygon3d(n, m, t, c, d, x);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("N"));
        pointsIDs.add(getAnchorID("M"));
        pointsIDs.add(getAnchorID("S"));
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("W"));
        edt.addAnchor(poly, pointsIDs, this, "plane_NMSCDW");
      }
      {
        Polygon3d poly = new Polygon3d(n, o, r, f, e, x);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("N"));
        pointsIDs.add(getAnchorID("O"));
        pointsIDs.add(getAnchorID("Q"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("W"));
        edt.addAnchor(poly, pointsIDs, this, "plane_NOQFEW");
      }
     
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add Plane of icosahedron: " + ex.getMessage());
    }

  }
  
     /**
   * Construct truncated octahedron section by plane
   * @param id
   * @param p
   * @return
   * @throws ExGeom
   * @throws ExZeroDivision
   */
  public PolygonBody section(String id, PlaneBody p)
    throws ExGeom, ExZeroDivision {
    try {
      Polygon3d poly = _oct.sectionByPlane(new Plane3d(p.normal(), p.point()));
      if (poly.points().isEmpty())
        throw new ExGeom("усеченный октаэдр и плоскость не пересекаются");
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
      faces = _oct.faces();
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
    return _oct;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new TruncatedOctahedronBody(id, title, (TruncatedOctahedron3d) geom);
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
    edt.addAnchor(points.get(20), result, "U");
    edt.addAnchor(points.get(21), result, "V");
    edt.addAnchor(points.get(22), result, "W");
    edt.addAnchor(points.get(23), result, "X");
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    TruncatedOctahedronBody oct = (TruncatedOctahedronBody) result;
    edt.addAnchor(oct.A(), result, "A");
    edt.addAnchor(oct.B(), result, "B");
    edt.addAnchor(oct.C(), result, "C");
    edt.addAnchor(oct.D(), result, "D");
    edt.addAnchor(oct.E(), result, "E");
    edt.addAnchor(oct.F(), result, "F");
    edt.addAnchor(oct.G(), result, "G");
    edt.addAnchor(oct.H(), result, "H");
    edt.addAnchor(oct.I(), result, "I");
    edt.addAnchor(oct.J(), result, "J");
    edt.addAnchor(oct.K(), result, "K");
    edt.addAnchor(oct.L(), result, "L");
    edt.addAnchor(oct.M(), result, "M");
    edt.addAnchor(oct.N(), result, "N");
    edt.addAnchor(oct.O(), result, "O");
    edt.addAnchor(oct.P(), result, "P");
    edt.addAnchor(oct.Q(), result, "Q");
    edt.addAnchor(oct.R(), result, "R");
    edt.addAnchor(oct.S(), result, "S");
    edt.addAnchor(oct.T(), result, "T");
    edt.addAnchor(oct.U(), result, "U");
    edt.addAnchor(oct.V(), result, "V");
    edt.addAnchor(oct.W(), result, "W");
    edt.addAnchor(oct.X(), result, "X");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_oct.intersect(ray), ren.getCameraPosition().eye());
  }
};
