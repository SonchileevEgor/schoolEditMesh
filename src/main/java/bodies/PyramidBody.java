package bodies;

import geom.AbstractPolygon;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.i_Geom;
import geom.*;

import java.util.ArrayList;
import java.util.List;
import util.Fatal;
import util.Util;

/**
 * Meta body - pyramid.
 *
 * Именование ключей:
 * "0", "1",... - вершины основания
 * "top" - вершина пирамиды.
 */
public class PyramidBody extends BodyAdapter {
  private Pyramid3d _pyramid; // math object pyramid

  /**
   * Constructor of pyramid by math pyramid
   * @param id
   * @param title
   * @param pyramid
   */
  public PyramidBody (String id, String title, Pyramid3d pyramid){
    super(id, title);
    _pyramid = pyramid;
    _alias = "пирамида";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public PyramidBody(String id, String title) {
    super(id, title);
    _alias = "пирамида";
    _exists = false;
  }

  public Pyramid3d pyramid() {
    return _pyramid;
  }

  /**
   * Construct pyramid section by plane
   * @param id
   * @param p section plane
   * @return pyramid section - polygon
   * @throws ExGeom
   */
  public PolygonBody section (String id, PlaneBody p)
    throws ExGeom {
    try {
      Polygon3d poly = _pyramid.sectionByPlane(new Plane3d(p.normal(), p.point()));
      if (poly.points().isEmpty())
        throw new ExGeom("нет точек пересечения");
      // return new polygon with params from PolygonBody3d
      return new PolygonBody(id, poly.points());
    } catch (NullPointerException ex) {
      throw new ExGeom("секущая плоскость не была построена");
    }
  }

  /**
   * List of anchors of base.
   * @return
   */
  public List<String> baseAnchorIDs() {
    ArrayList<String> result = new ArrayList<String>();
    for (int i = 0; i < _pyramid.vertNumber() - 1; i++) {
      result.add(getAnchorID(String.valueOf(i)));
    }
    return result;
  }

  /*
  * @return top anchor id
  */
  public String topAnchorID(){
    return getAnchorID(String.valueOf(_pyramid.top()));
  } 
  /**
   * constructs a circumscribed sphere of the pyramid if that's possible
   * otherwise throws an exception
   * @return
   * @throws ExGeom
   */
  public Sphere3d outSphere() throws ExGeom {
    return _pyramid.outSphere();
  }

  @Override
  public BodyType type() {
    return BodyType.PYRAMID;
  }

  // Getters for all vertexes of prism
  public Vect3d getBasePoint(int idx){
    if( idx > _pyramid.vertNumber() - 1){
      throw new IndexOutOfBoundsException("Index of point is not of base.");
    }
    return _pyramid.points().get(idx);
  }
  
  public Vect3d getTopPoint(){
    return _pyramid.points().get(_pyramid.vertNumber() - 1);
  }

  @Override
  public void addRibs(Editor edt){
    int baseSize = _pyramid.vertNumber() - 1;
    // add a ribs of base
    for(int i = 0; i < baseSize; i++){
      try {
        Vect3d a = getBasePoint(i);
        Vect3d b = getBasePoint((i + 1) % baseSize);
        Rib3d rib1 = new Rib3d(a, b);
        String Id1 = getAnchorID(String.valueOf(i));
        String Id2 = getAnchorID(String.valueOf((i + 1) % baseSize));
        edt.addAnchor(rib1, Id1, Id2, this, "rib_Base" + String.valueOf(i) + "_Base" + String.valueOf((i + 1) % baseSize));
      } catch (ExDegeneration ex) { }
    }

    // add a side ribs
    Vect3d top = _pyramid.top();
    String IdTop = getAnchorID("top");
    for(int i = 0; i < baseSize; i++){
      try {
        Vect3d a = getBasePoint(i);
        String Ida = getAnchorID(String.valueOf(i));
        Rib3d rib1 = new Rib3d(top, a);
        edt.addAnchor(rib1, IdTop, Ida, this, "rib_Top_Base" + String.valueOf(i));
      } catch (ExDegeneration ex) { }
    }
  }

  @Override
  public void addPlanes(Editor edt){
    int baseSize = _pyramid.vertNumber() - 1;
    try{
      // add a side planes
      Vect3d top = _pyramid.top();
      String IdTop = getAnchorID("top");
      for(int i = 0; i < baseSize; i++){
        Vect3d b = getBasePoint(i);
        Vect3d c = getBasePoint((i + 1) % baseSize);
        String Idb = getAnchorID(String.valueOf(i));
        String Idc = getAnchorID(String.valueOf((i + 1) % baseSize));

        Polygon3d poly1 = new Polygon3d(top, b, c);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(IdTop);
        pointIDs.add(Idb);
        pointIDs.add(Idc);
        edt.addAnchor(poly1, pointIDs, this, "plane_Top_Base" + String.valueOf(i) + "_Base" + String.valueOf((i + 1) % baseSize));
      }

      // add the base plane
      ArrayList<Vect3d> points = new ArrayList<Vect3d>();
      ArrayList<String> pointIDs = new ArrayList<String>();
      for(int i = 0; i < baseSize; i++){
        Vect3d a = getBasePoint(i);
        points.add(a);
        pointIDs.add(getAnchorID(String.valueOf(i)));
      }
      Polygon3d poly1 = new Polygon3d(points);
      edt.addAnchor(poly1, pointIDs, this, "plane_Base");
    } catch (ExGeom ex){
      util.Fatal.warning("Cannot create plane for prism: " + ex.getMessage());
    }
  }

  @Override
  public void glDrawFacets(Render ren){
    // рисуются только якоря
  }

   @Override
  public void glDrawCarcass(Render ren){
     // рисуются только якоря
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    ArrayList<Polygon3d> faces = new ArrayList<>();
    try {
      faces = _pyramid.faces();
    }
    catch (ExGeom e) {
      Fatal.error("Error @ PyramidBody.getAllFaces()");
    }
    return faces;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    try {
      return _pyramid.sectionByPlane(plane);
    } catch (ExGeom ex){
      return super.getIntersectionWithPlane(plane);
    }
  }

  @Override
  public i_Geom getGeom() {
    return _pyramid;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new PyramidBody (id, title, (Pyramid3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    PyramidBody pyr = (PyramidBody) result;
    edt.addAnchor(pyr.pyramid().points().get(pyr.pyramid().points().size()-1), result, "top");
    for (int i = 0; i < pyr.pyramid().points().size() - 1; i++) {
      edt.addAnchor(pyr.pyramid().points().get(i), result, String.valueOf(i));
    }
  }
    
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_pyramid.intersect(ray), ren.getCameraPosition().eye());
  }
}
