package bodies;

import static config.Config.LOG_LEVEL;
import geom.AbstractPolygon;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.i_Geom;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.ExZeroDivision;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Ray3d;
import geom.Rib3d;
import geom.Simplex3d;
import geom.Sphere3d;
import geom.Vect3d;
import java.util.ArrayList;
import util.Log;
import util.Util;

/**
 * Meta body - tetrahedron.
 *
 */
public class TetrahedronBody extends BodyAdapter {
  private Simplex3d _simplex; // math object simplex
  private boolean _isRegular = false;

  public static final String BODY_KEY_A = "A";
  public static final String BODY_KEY_B = "B";
  public static final String BODY_KEY_C = "C";
  public static final String BODY_KEY_D = "D";

  /**
   * Constructor of tetrahedron by math tetrahedron body
   * @param id
   * @param title
   * @param simplex
   * @throws geom.ExGeom
   */
  public TetrahedronBody (String id, String title, Simplex3d simplex) throws ExGeom {
    super(id, title);
    _simplex = simplex;
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public TetrahedronBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  public void setRegular(boolean isRegular) {
    _isRegular = isRegular;
  }

  public Simplex3d tetrahedron() { return _simplex; }

  // Getters for all vertexes of tetrahedron
  public Vect3d a(){ return _simplex.a(); }
  public Vect3d b(){ return _simplex.b(); }
  public Vect3d c(){ return _simplex.c(); }
  public Vect3d d(){ return _simplex.d(); }

  /**
   * Construct tetrahedron section by plane
   * @param id
   * @param p
   * @return
   * @throws ExGeom
   * @throws ExZeroDivision
   */
  public PolygonBody section(String id, PlaneBody p)
    throws ExGeom, ExZeroDivision {
    try {
      Polygon3d poly = _simplex.sectionByPlane(new Plane3d(p.normal(), p.point()));
      if (poly.points().isEmpty())
        throw new ExGeom("тетраэдр и плоскость не пересекаются");
      // возвращается новый полигон с параметрами из PolygonBody3d
      return new PolygonBody(id, poly.points());
    } catch (NullPointerException ex) {
      throw new ExGeom("плоскость не была построена");
    }
  }

  /**
   * @return volume of the tetrahedron
   */
  public double volume(){
    return _simplex.volume();
  }

  /**
   * @return the circumscribed sphere
   */
  public Sphere3d outSphere(){
    return _simplex.outSphere();
  }

    /**
   * @return center of the circumscribed sphere
   */
  public Vect3d outCenter(){
    return outSphere().center();
  }

  /**
   * @return radius of the circumscribed sphere
   */
  public double outRadius(){
    return outSphere().radius();
  }

  /**
   * @return the inscribed sphere
   */
  public Sphere3d inSphere() throws ExGeom{
    return _simplex.inSphere();
  }

  /**
   * @return center of the inscribed sphere
   */
  public Vect3d inCenter() throws ExGeom{
    return inSphere().center();
  }

  /**
   * @return radius of the inscribed sphere
   */
  public double inRadius() throws ExGeom{
    return inSphere().radius();
  }

  /**
   * @param index face index.
   * @return area of a specified face of tetrahedron<br>
   * <strong>0</strong>: area of the face BCD<br>
   * <strong>1</strong>: area of the face ACD<br>
   * <strong>2</strong>: area of the face ABD<br>
   * <strong>3</strong>: area of the face ABC<br>
   * otherwise, returns 0
   */
  public double faceArea (int index){
    return _simplex.faceArea(index);
  }

  /**
   * @return full area of tetrahedron surface
   */
  public double fullArea(){
    return _simplex.surfaceArea();
  }

  @Override
  public BodyType type() {
    if (_isRegular) {
      return BodyType.REG_TETRAHEDRON;
    } else {
      return BodyType.TETRAHEDRON;
    }
  }

  @Override
  public void addRibs(Editor edt){
    try {
      Vect3d a = _simplex.a();
      Vect3d b = _simplex.b();
      Vect3d c = _simplex.c();
      Vect3d d = _simplex.d();

      String idA = getAnchorID(BODY_KEY_A);
      String idB = getAnchorID(BODY_KEY_B);
      String idC = getAnchorID(BODY_KEY_C);
      String idD = getAnchorID(BODY_KEY_D);

      // adding the ribs
      Rib3d rib1 = new Rib3d(a, b);
      edt.addAnchor(rib1, idA, idB, this, "rib_AB");
      Rib3d rib2 = new Rib3d(a, c);
      edt.addAnchor(rib2, idA, idC, this, "rib_AC");
      Rib3d rib3 = new Rib3d(a, d);
      edt.addAnchor(rib3, idA, idD, this, "rib_AD");
      Rib3d rib4 = new Rib3d(b, c);
      edt.addAnchor(rib4, idB, idC, this, "rib_BC");
      Rib3d rib5 = new Rib3d(b, d);
      edt.addAnchor(rib5, idB, idD, this, "rib_BD");
      Rib3d rib6 = new Rib3d(c, d);
      edt.addAnchor(rib6, idC, idD, this, "rib_CD");
    } catch (ExDegeneration ex) { }
  }

  @Override
  public String alias() {
    if (_isRegular) {
      return "правильный тетраэдр";
    } else {
      return "тетраэдр";
    }
  }

  @Override
  public void addPlanes(Editor edt){
    Vect3d a = _simplex.a();
    Vect3d b = _simplex.b();
    Vect3d c = _simplex.c();
    Vect3d d = _simplex.d();

    String idA = getAnchorID(BODY_KEY_A);
    String idB = getAnchorID(BODY_KEY_B);
    String idC = getAnchorID(BODY_KEY_C);
    String idD = getAnchorID(BODY_KEY_D);

    // adding the polys
    try{
      {
        Polygon3d poly1 = new Polygon3d(a, b, c);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(idA);
        pointIDs.add(idB);
        pointIDs.add(idC);
        edt.addAnchor(poly1, pointIDs, this, "plane_ABC");
      }
      {
        Polygon3d poly1 = new Polygon3d(a, b, d);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(idA);
        pointIDs.add(idB);
        pointIDs.add(idD);
        edt.addAnchor(poly1, pointIDs, this, "plane_ABD");
      }
      {
        Polygon3d poly1 = new Polygon3d(a, c, d);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(idA);
        pointIDs.add(idC);
        pointIDs.add(idD);
        edt.addAnchor(poly1, pointIDs, this, "plane_ACD");
      }
      {
        Polygon3d poly1 = new Polygon3d(b, c, d);
        ArrayList<String> pointIDs = new ArrayList<>();
        pointIDs.add(idB);
        pointIDs.add(idC);
        pointIDs.add(idD);
        edt.addAnchor(poly1, pointIDs, this, "plane_BCD");
      }
    } catch(ExGeom ex){
      util.Fatal.warning("Cannot create Polygon3d: " + ex.getMessage());
    }

  }
  @Override
  public void glDrawCarcass(Render ren){
    // рисуются только якоря
  }

  @Override
  public void glDrawFacets(Render ren){
    // рисуются только якоря
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
      ArrayList<Polygon3d> faces = new ArrayList<>();
      try {
          faces = _simplex.faces();
      } catch (ExGeom e) {
        if (LOG_LEVEL.value() >= 2) {
          Log.out.println(e.getMessage());
        }
      }
      return faces;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    try{
      return _simplex.sectionByPlane(plane);
    } catch (ExGeom ex) {
      return super.getIntersectionWithPlane(plane);
    }
  }

  @Override
  public i_Geom getGeom() {
    return _simplex;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    try {
      return new TetrahedronBody (id, title, (Simplex3d) geom);
    } catch (ExGeom ex) { }
    return null;
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    TetrahedronBody tetrahedron = (TetrahedronBody) result;
    edt.addAnchor(tetrahedron.a(), result, BODY_KEY_A);
    edt.addAnchor(tetrahedron.b(), result, BODY_KEY_B);
    edt.addAnchor(tetrahedron.c(), result, BODY_KEY_C);
    edt.addAnchor(tetrahedron.d(), result, BODY_KEY_D);
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_simplex.intersect(ray), ren.getCameraPosition().eye());
  }
};
