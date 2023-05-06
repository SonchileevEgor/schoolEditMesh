package bodies;

import geom.AbstractPolygon;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.*;

import java.util.ArrayList;
import util.Fatal;
import util.Util;

/**
 * Meta body - cube.
 *
 */
public class CubeBody extends BodyAdapter {

  public CubeBody(String id, String title, Cube3d cube)
    throws ExGeom {
    super(id, title);
    _cube = cube;
    _alias = "куб";
    _exists = true;
  }

  /**
   * Constructor by 3 vertex. Order of points defines cube orientation. Requirement: points are vertex of isosceles right triangle.
   * @param id
   * @param title
   * @param a
   * @param b
   * @param d
   * @throws ExGeom
   */
  public CubeBody(String id, String title, Vect3d a, Vect3d b, Vect3d d)
    throws ExGeom {
    super(id, title);
    _cube = new Cube3d(a, b, d);
    _alias = "куб";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public CubeBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  public Cube3d cube() { return _cube; }

  // Getters for all vertexes of cube
  public Vect3d A1(){ return _cube.A1(); }
  public Vect3d B1(){ return _cube.B1(); }
  public Vect3d C1(){ return _cube.C1(); }
  public Vect3d D1(){ return _cube.D1(); }
  public Vect3d A2(){ return _cube.A2(); }
  public Vect3d B2(){ return _cube.B2(); }
  public Vect3d C2(){ return _cube.C2(); }
  public Vect3d D2(){ return _cube.D2(); }

  /**
   * @return center of the cube
   */
  public Vect3d center(){
    return _cube.center();
  }

  /**
   * @return length of edge
   */
  public double edgeLength(){
    return _cube.edgeLength();
  }

  /**
   * @return main diagonal length
   */
  public double diagonalLength(){
    return _cube.diagonalLength();
  }

  /**
   * @return volume of the cube
   */
  public double volume(){
    return _cube.volume();
  }

  /**
   * Construct cube section by plane
   * @param id
   * @param p
   * @return body cube section
   * @throws geom.ExGeom
   */
  public PolygonBody section(String id, PlaneBody p) throws ExGeom {
    Polygon3d poly = _cube.sectionByPlane(p.plane());
    if (poly.points().isEmpty())
      throw new ExGeom("куб и плоскость не пересекаются");
    //возвращается новый полигон с параметрами из PolygonBody3d
    return new PolygonBody(id, poly.points());
  }

  @Override
  public BodyType type() {
    return BodyType.CUBE;
  }

  @Override
  public void addRibs(Editor edt){
    Vect3d a1 = A1();
    Vect3d b1 = B1();
    Vect3d c1 = C1();
    Vect3d d1 = D1();
    Vect3d a2 = A2();
    Vect3d b2 = B2();
    Vect3d c2 = C2();
    Vect3d d2 = D2();

    // adding the ribs
    try {
      Rib3d rib1 = new Rib3d(a1, b1);
      edt.addAnchor(rib1, getAnchorID("A1"), getAnchorID("B1"), this, "rib_A1B1");
      Rib3d rib2 = new Rib3d(b1, c1);
      edt.addAnchor(rib2, getAnchorID("B1"), getAnchorID("C1"), this, "rib_B1C1");
      Rib3d rib3 = new Rib3d(c1, d1);
      edt.addAnchor(rib3, getAnchorID("C1"), getAnchorID("D1"), this, "rib_C1D1");
      Rib3d rib4 = new Rib3d(d1, a1);
      edt.addAnchor(rib4, getAnchorID("D1"), getAnchorID("A1"), this, "rib_D1A1");
      Rib3d rib5 = new Rib3d(a1, a2);
      edt.addAnchor(rib5, getAnchorID("A1"), getAnchorID("A2"), this, "rib_A1A2");
      Rib3d rib6 = new Rib3d(b1,b2);
      edt.addAnchor(rib6, getAnchorID("B1"), getAnchorID("B2"), this, "rib_B1B2");
      Rib3d rib7 = new Rib3d(c1, c2);
      edt.addAnchor(rib7, getAnchorID("C1"), getAnchorID("C2"), this, "rib_C1C2");
      Rib3d rib8 = new Rib3d(d1, d2);
      edt.addAnchor(rib8, getAnchorID("D1"), getAnchorID("D2"), this, "rib_D1D2");

      Rib3d rib9 = new Rib3d(a2, b2);
      edt.addAnchor(rib9, getAnchorID("A2"), getAnchorID("B2"), this, "rib_A2B2");
      Rib3d rib10 = new Rib3d(b2, c2);
      edt.addAnchor(rib10, getAnchorID("B2"), getAnchorID("C2"), this, "rib_B2C2");
      Rib3d rib11 = new Rib3d(c2, d2);
      edt.addAnchor(rib11, getAnchorID("C2"), getAnchorID("D2"), this, "rib_C2D2");
      Rib3d rib12 = new Rib3d(d2, a2);
      edt.addAnchor(rib12, getAnchorID("D2"), getAnchorID("A2"), this, "rib_D2A2");
    } catch( ExDegeneration ex ){
      util.Fatal.warning("Cannot add ribs of cube: " + ex.getMessage());
    }

  }

  @Override
  public void addPlanes(Editor edt){
    Vect3d a1 = A1();
    Vect3d b1 = B1();
    Vect3d c1 = C1();
    Vect3d d1 = D1();
    Vect3d a2 = A2();
    Vect3d b2 = B2();
    Vect3d c2 = C2();
    Vect3d d2 = D2();

    // adding the polys
    try{
      {
        Polygon3d poly1 = new Polygon3d(a1, b1, c1, d1);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(getAnchorID("A1"));
        pointIDs.add(getAnchorID("B1"));
        pointIDs.add(getAnchorID("C1"));
        pointIDs.add(getAnchorID("D1"));
        edt.addAnchor(poly1, pointIDs, this, "plane_A1B1C1D1");
      }
      {
        Polygon3d poly1 = new Polygon3d(a2, b2, c2, d2);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(getAnchorID("A2"));
        pointIDs.add(getAnchorID("B2"));
        pointIDs.add(getAnchorID("C2"));
        pointIDs.add(getAnchorID("D2"));
        edt.addAnchor(poly1, pointIDs, this, "plane_A2B2C2D2");
      }
      {
        Polygon3d poly1 = new Polygon3d(a1, b1, b2, a2);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(getAnchorID("A1"));
        pointIDs.add(getAnchorID("B1"));
        pointIDs.add(getAnchorID("B2"));
        pointIDs.add(getAnchorID("A2"));
        edt.addAnchor(poly1, pointIDs, this, "plane_A1B1B2A2");
      }
      {
        Polygon3d poly1 = new Polygon3d(b1, c1, c2, b2);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(getAnchorID("B1"));
        pointIDs.add(getAnchorID("C1"));
        pointIDs.add(getAnchorID("C2"));
        pointIDs.add(getAnchorID("B2"));
        edt.addAnchor(poly1, pointIDs, this, "plane_B1C1C2B2");
      }
      {
        Polygon3d poly1 = new Polygon3d(c1, d1, d2, c2);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(getAnchorID("C1"));
        pointIDs.add(getAnchorID("D1"));
        pointIDs.add(getAnchorID("D2"));
        pointIDs.add(getAnchorID("C2"));
        edt.addAnchor(poly1, pointIDs, this, "plane_C1D1D2C2");
      }
      {
        Polygon3d poly1 = new Polygon3d(a1, d1, d2, a2);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(getAnchorID("A1"));
        pointIDs.add(getAnchorID("D1"));
        pointIDs.add(getAnchorID("D2"));
        pointIDs.add(getAnchorID("A2"));
        edt.addAnchor(poly1, pointIDs, this, "plane_A1D1D2A2");
      }
    } catch(ExGeom ex){
      util.Fatal.warning("Cannot add facet of cube: " + ex.getMessage());
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
        faces = _cube.faces();
      } catch (ExGeom e) {
        Fatal.warning("Error @ CubeBody.getAllFaces");
      }
      return faces;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    try {
      return _cube.sectionByPlane(plane);
    } catch (ExGeom ex) {
      return super.getIntersectionWithPlane(plane);
    }
  }

  private Cube3d _cube; // math object cube

  @Override
  public i_Geom getGeom() {
    return _cube;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    try {
        return new CubeBody (id, title, (Cube3d) geom);
    } catch (ExGeom ex) { }
    return null;
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    CubeBody cube = (CubeBody) result;
    edt.addAnchor(cube.A1(), result, "A1");
    edt.addAnchor(cube.B1(), result, "B1");
    edt.addAnchor(cube.C1(), result, "C1");
    edt.addAnchor(cube.D1(), result, "D1");
    edt.addAnchor(cube.A2(), result, "A2");
    edt.addAnchor(cube.B2(), result, "B2");
    edt.addAnchor(cube.C2(), result, "C2");
    edt.addAnchor(cube.D2(), result, "D2");
  }

  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_cube.intersect(ray), ren.getCameraPosition().eye());
  }
};
