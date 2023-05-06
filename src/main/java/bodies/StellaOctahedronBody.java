package bodies;

import static config.Config.LOG_LEVEL;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.AbstractPolygon;
import geom.ExGeom;
import geom.ExZeroDivision;
import geom.StellaOctahedron3d;
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
public class StellaOctahedronBody extends BodyAdapter {
  private StellaOctahedron3d _stellaOctahedron; // Math object polygon

  /**
   * Constructor of polygon by the geom object.
   *
   * @param id
   * @param title
   * @param so
   */
  public StellaOctahedronBody(String id, String title, StellaOctahedron3d so) {
    super(id, title);
    _stellaOctahedron = so;
    _alias = "звёздчатый октаэдр";
    _exists = true;
  }

  /**
   * Constructor of stella octahedron
   *
   * @param id
   * @param title
   * @param a
   * @param b
   * @param angle
   * @throws geom.ExGeom
   */
  public StellaOctahedronBody(String id, String title, Vect3d a, Vect3d b, double angle) throws ExGeom {
    super(id, title);
    _stellaOctahedron = new StellaOctahedron3d(a, b, angle);
    _alias = "звёздчатый октаэдр";
    _exists = true;
  }

  /**
   * constructor for null-body
   *
   * @param id
   * @param title
   */
  public StellaOctahedronBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  /**
   * @return math object polygon
   */
  public StellaOctahedron3d stellaOctahedron() {
    return _stellaOctahedron;
  }

  // Getters for all vertexs of icosahedron
  public Vect3d A() {
    return _stellaOctahedron.A();
  }

  public Vect3d B() {
    return _stellaOctahedron.B();
  }

  public Vect3d C() {
    return _stellaOctahedron.C();
  }

  public Vect3d D() {
    return _stellaOctahedron.D();
  }

  public Vect3d E() {
    return _stellaOctahedron.E();
  }

  public Vect3d F() {
    return _stellaOctahedron.F();
  }

  public Vect3d G() {
    return _stellaOctahedron.G();
  }

  public Vect3d H() {
    return _stellaOctahedron.H();
  }

  public Vect3d I() {
    return _stellaOctahedron.I();
  }

  public Vect3d J() {
    return _stellaOctahedron.J();
  }

  public Vect3d K() {
    return _stellaOctahedron.K();
  }

  public Vect3d L() {
    return _stellaOctahedron.L();
  }

  public Vect3d M() {
    return _stellaOctahedron.M();
  }

  public Vect3d N() {
    return _stellaOctahedron.N();
  }

  @Override
  public BodyType type() {
    return BodyType.STELLAR_OCTAHEDRON;
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

    try {
      Rib3d rib1 = new Rib3d(a, e);
      edt.addAnchor(rib1, getAnchorID("A"), getAnchorID("E"), this, "rib_AE");
      Rib3d rib2 = new Rib3d(a, f);
      edt.addAnchor(rib2, getAnchorID("A"), getAnchorID("F"), this, "rib_AF");
      Rib3d rib3 = new Rib3d(a, i);
      edt.addAnchor(rib3, getAnchorID("A"), getAnchorID("I"), this, "rib_AI");
      Rib3d rib4 = new Rib3d(e, f);
      edt.addAnchor(rib4, getAnchorID("E"), getAnchorID("F"), this, "rib_EF");
      Rib3d rib5 = new Rib3d(e, i);
      edt.addAnchor(rib5, getAnchorID("E"), getAnchorID("I"), this, "rib_EI");
      Rib3d rib6 = new Rib3d(f, i);
      edt.addAnchor(rib6, getAnchorID("F"), getAnchorID("I"), this, "rib_FI");

      Rib3d rib7 = new Rib3d(b, i);
      edt.addAnchor(rib7, getAnchorID("B"), getAnchorID("I"), this, "rib_BI");
      Rib3d rib8 = new Rib3d(b, h);
      edt.addAnchor(rib8, getAnchorID("B"), getAnchorID("H"), this, "rib_BH");
      Rib3d rib9 = new Rib3d(b, g);
      edt.addAnchor(rib9, getAnchorID("B"), getAnchorID("G"), this, "rib_BG");
      Rib3d rib10 = new Rib3d(i, h);
      edt.addAnchor(rib10, getAnchorID("I"), getAnchorID("H"), this, "rib_IH");
      Rib3d rib11 = new Rib3d(h, g);
      edt.addAnchor(rib11, getAnchorID("H"), getAnchorID("G"), this, "rib_HG");
      Rib3d rib12 = new Rib3d(g, i);
      edt.addAnchor(rib12, getAnchorID("G"), getAnchorID("I"), this, "rib_GI");

      Rib3d rib13 = new Rib3d(c, f);
      edt.addAnchor(rib13, getAnchorID("C"), getAnchorID("F"), this, "rib_CF");
      Rib3d rib14 = new Rib3d(c, j);
      edt.addAnchor(rib14, getAnchorID("C"), getAnchorID("J"), this, "rib_CJ");
      Rib3d rib15 = new Rib3d(c, g);
      edt.addAnchor(rib15, getAnchorID("C"), getAnchorID("G"), this, "rib_CG");
      Rib3d rib16 = new Rib3d(j, g);
      edt.addAnchor(rib16, getAnchorID("J"), getAnchorID("G"), this, "rib_JG");
      Rib3d rib17 = new Rib3d(g, f);
      edt.addAnchor(rib17, getAnchorID("G"), getAnchorID("F"), this, "rib_GF");
      Rib3d rib18 = new Rib3d(f, j);
      edt.addAnchor(rib18, getAnchorID("F"), getAnchorID("J"), this, "rib_FJ");

      Rib3d rib19 = new Rib3d(d, e);
      edt.addAnchor(rib19, getAnchorID("D"), getAnchorID("E"), this, "rib_DE");
      Rib3d rib20 = new Rib3d(d, j);
      edt.addAnchor(rib20, getAnchorID("D"), getAnchorID("J"), this, "rib_DJ");
      Rib3d rib21 = new Rib3d(d, h);
      edt.addAnchor(rib21, getAnchorID("D"), getAnchorID("H"), this, "rib_DH");
      Rib3d rib22 = new Rib3d(j, e);
      edt.addAnchor(rib22, getAnchorID("J"), getAnchorID("E"), this, "rib_JE");
      Rib3d rib23 = new Rib3d(e, h);
      edt.addAnchor(rib23, getAnchorID("E"), getAnchorID("H"), this, "rib_EH");
      Rib3d rib24 = new Rib3d(h, j);
      edt.addAnchor(rib24, getAnchorID("H"), getAnchorID("J"), this, "rib_HJ");

      Rib3d rib25 = new Rib3d(k, e);
      edt.addAnchor(rib25, getAnchorID("K"), getAnchorID("E"), this, "rib_KE");
      Rib3d rib26 = new Rib3d(k, f);
      edt.addAnchor(rib26, getAnchorID("K"), getAnchorID("F"), this, "rib_KF");
      Rib3d rib27 = new Rib3d(k, j);
      edt.addAnchor(rib27, getAnchorID("K"), getAnchorID("J"), this, "rib_KJ");

      Rib3d rib28 = new Rib3d(l, j);
      edt.addAnchor(rib28, getAnchorID("L"), getAnchorID("J"), this, "rib_LJ");
      Rib3d rib29 = new Rib3d(l, g);
      edt.addAnchor(rib29, getAnchorID("L"), getAnchorID("G"), this, "rib_LG");
      Rib3d rib30 = new Rib3d(l, h);
      edt.addAnchor(rib30, getAnchorID("L"), getAnchorID("H"), this, "rib_LH");

      Rib3d rib31 = new Rib3d(m, i);
      edt.addAnchor(rib31, getAnchorID("M"), getAnchorID("I"), this, "rib_MI");
      Rib3d rib32 = new Rib3d(m, h);
      edt.addAnchor(rib32, getAnchorID("M"), getAnchorID("H"), this, "rib_MH");
      Rib3d rib33 = new Rib3d(m, e);
      edt.addAnchor(rib33, getAnchorID("M"), getAnchorID("E"), this, "rib_ME");

      Rib3d rib34 = new Rib3d(n, f);
      edt.addAnchor(rib34, getAnchorID("N"), getAnchorID("F"), this, "rib_NF");
      Rib3d rib35 = new Rib3d(n, i);
      edt.addAnchor(rib35, getAnchorID("N"), getAnchorID("I"), this, "rib_NI");
      Rib3d rib36 = new Rib3d(n, g);
      edt.addAnchor(rib36, getAnchorID("N"), getAnchorID("G"), this, "rib_NG");

      
    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add Plane of stella octahedron: " + ex.getMessage());
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

    try {
      {
        Polygon3d poly = new Polygon3d(a, e, f);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("F"));
        edt.addAnchor(poly, pointsIDs, this, "plane_AEF");
      }
     {
        Polygon3d poly = new Polygon3d(a, e, i);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("I"));
        edt.addAnchor(poly, pointsIDs, this, "plane_AEI");
      }
      {
        Polygon3d poly = new Polygon3d(a, f, i);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("A"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("I"));
        edt.addAnchor(poly, pointsIDs, this, "plane_AFI");
      }
      {
        Polygon3d poly = new Polygon3d(b, h, i);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("I"));
        edt.addAnchor(poly, pointsIDs, this, "plane_BHI");
      }
      {
        Polygon3d poly = new Polygon3d(b, h, g);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("H"));
        pointsIDs.add(getAnchorID("G"));
        edt.addAnchor(poly, pointsIDs, this, "plane_BHG");
      }
      {
        Polygon3d poly = new Polygon3d(b, g, i);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("B"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("I"));
        edt.addAnchor(poly, pointsIDs, this, "plane_BGI");
      }
      {
        Polygon3d poly = new Polygon3d(c, j, g);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("G"));
        edt.addAnchor(poly, pointsIDs, this, "plane_CJG");
      }
      {
        Polygon3d poly = new Polygon3d(c, j, f);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("F"));
        edt.addAnchor(poly, pointsIDs, this, "plane_CJF");
      }
      {
        Polygon3d poly = new Polygon3d(c, f, g);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("C"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("G"));
        edt.addAnchor(poly, pointsIDs, this, "plane_CFG");
      }
      {
        Polygon3d poly = new Polygon3d(d, j, h);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("H"));
        edt.addAnchor(poly, pointsIDs, this, "plane_DJH");
      }
      {
        Polygon3d poly = new Polygon3d(d, j, e);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("E"));
        edt.addAnchor(poly, pointsIDs, this, "plane_DJE");
      }
      {
        Polygon3d poly = new Polygon3d(d, e, h);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("D"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("H"));
        edt.addAnchor(poly, pointsIDs, this, "plane_DEH");
      }
      {
        Polygon3d poly = new Polygon3d(k, e, j);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("K"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("J"));
        edt.addAnchor(poly, pointsIDs, this, "plane_KEJ");
      }
      {
        Polygon3d poly = new Polygon3d(k, e, f);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("K"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("F"));
        edt.addAnchor(poly, pointsIDs, this, "plane_KEF");
      }
      {
        Polygon3d poly = new Polygon3d(k, f, j);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("K"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("J"));
        edt.addAnchor(poly, pointsIDs, this, "plane_KFJ");
      }
      {
        Polygon3d poly = new Polygon3d(l, j, g);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("G"));
        edt.addAnchor(poly, pointsIDs, this, "plane_LJG");
      }
      {
        Polygon3d poly = new Polygon3d(l, j, h);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("J"));
        pointsIDs.add(getAnchorID("H"));
        edt.addAnchor(poly, pointsIDs, this, "plane_LJH");
      }
      {
        Polygon3d poly = new Polygon3d(l, g, h);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("L"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("H"));
        edt.addAnchor(poly, pointsIDs, this, "plane_LGH");
      }
      {
        Polygon3d poly = new Polygon3d(m, i, h);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("M"));
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("H"));
        edt.addAnchor(poly, pointsIDs, this, "plane_MIH");
      }
      {
        Polygon3d poly = new Polygon3d(m, i, e);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("M"));
        pointsIDs.add(getAnchorID("I"));
        pointsIDs.add(getAnchorID("E"));
        edt.addAnchor(poly, pointsIDs, this, "plane_MIE");
      }
      {
        Polygon3d poly = new Polygon3d(m, e, h);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("M"));
        pointsIDs.add(getAnchorID("E"));
        pointsIDs.add(getAnchorID("H"));
        edt.addAnchor(poly, pointsIDs, this, "plane_MEH");
      }
      {
        Polygon3d poly = new Polygon3d(n, f, g);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("N"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("G"));
        edt.addAnchor(poly, pointsIDs, this, "plane_NFG");
      }
      {
        Polygon3d poly = new Polygon3d(n, f, i);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("N"));
        pointsIDs.add(getAnchorID("F"));
        pointsIDs.add(getAnchorID("I"));
        edt.addAnchor(poly, pointsIDs, this, "plane_NFI");
      }
      {
        Polygon3d poly = new Polygon3d(n, g, i);
        ArrayList<String> pointsIDs = new ArrayList<String>();
        pointsIDs.add(getAnchorID("N"));
        pointsIDs.add(getAnchorID("G"));
        pointsIDs.add(getAnchorID("I"));
        edt.addAnchor(poly, pointsIDs, this, "plane_NGI");
      }


    } catch (ExGeom ex) {
      util.Fatal.warning("Cannot add Plane of stella octahedron: " + ex.getMessage());
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
      faces = _stellaOctahedron.faces();
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
   * Construct stellar octahedron section by plane
   * @param id
   * @param p
   * @return
   * @throws ExGeom
   * @throws ExZeroDivision
   */
  public PolygonBody section(String id, PlaneBody p)
    throws ExGeom, ExZeroDivision {
    try {
      Polygon3d poly = _stellaOctahedron.sectionByPlane(new Plane3d(p.normal(), p.point()));
      if (poly.points().isEmpty())
        throw new ExGeom("звёздчатый октаэдр и плоскость не пересекаются");
      // возвращается новый полигон с параметрами из PolygonBody3d
      return new PolygonBody(id, poly.points()); 
    } catch (NullPointerException ex) {
      throw new ExGeom("плоскость не была построена");
    }
  }

  @Override
  public i_Geom getGeom() {
    return _stellaOctahedron;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new StellaOctahedronBody(id, title, (StellaOctahedron3d) geom);
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
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    StellaOctahedronBody so = (StellaOctahedronBody) result;
    edt.addAnchor(so.A(), result, "A");
    edt.addAnchor(so.B(), result, "B");
    edt.addAnchor(so.C(), result, "C");
    edt.addAnchor(so.D(), result, "D");
    edt.addAnchor(so.E(), result, "E");
    edt.addAnchor(so.F(), result, "F");
    edt.addAnchor(so.G(), result, "G");
    edt.addAnchor(so.H(), result, "H");
    edt.addAnchor(so.I(), result, "I");
    edt.addAnchor(so.J(), result, "J");
    edt.addAnchor(so.K(), result, "K");
    edt.addAnchor(so.L(), result, "L");
    edt.addAnchor(so.M(), result, "M");
    edt.addAnchor(so.N(), result, "N");
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_stellaOctahedron.intersect(ray), ren.getCameraPosition().eye());
  }
};
