package bodies;

import geom.AbstractPolygon;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.i_Geom;
import geom.*;

import java.util.ArrayList;
import java.util.List;
import util.Util;

/**
 * Meta body - prism.
 *
 */
public class PrismBody extends BodyAdapter {
  private Prism3d _prism; // math object prism
  private boolean _isParallelepiped = false;

  /**
   * Constructor of prism by math prism
   * @param id
   * @param title
   * @param prismBody math object Prism3d
   */
  public PrismBody(String id, String title, Prism3d prismBody){
    super(id, title);
    _prism = prismBody;
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public PrismBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  /**
   * Prism section by plane
   * @param id
   * @param p section plane
   * @return polygon
   * @throws geom.ExGeom
   * @throws geom.ExZeroDivision
   */
  public PolygonBody section(String id, PlaneBody p) throws ExGeom, ExZeroDivision {
    try {
      Polygon3d poly = _prism.sectionByPlane(new Plane3d(p.normal(), p.point()));
      if (poly.points().isEmpty())
        throw new ExGeom("нет точек пересечения");
      // return new polygon with params from PolygonBody3d
      return new PolygonBody(id, poly.points());
    } catch (NullPointerException ex) {
      throw new ExGeom("секущая плоскость не была построена");
    }
  }

  public void setParallelepiped(boolean isParallelepiped) {
    _isParallelepiped = isParallelepiped;
  }

  public Prism3d prism() {
    return _prism;
  }

  /**
   * constructs a circumscribed sphere of the prism if that's possible
   * otherwise throws an exception
   * @return
   * @throws ExGeom
   * @throws ExZeroDivision
   */
  public Sphere3d outSphere() throws ExGeom, ExZeroDivision{
    return _prism.outSphere();
  }

  @Override
  public String alias() {
    if (_isParallelepiped) {
      return "прямоугольный параллелепипед";
    } else {
      return "призма";
    }
  }
  
  @Override
  public BodyType type() {
    if (_isParallelepiped) {
      return BodyType.PARALLELEPIPED;
    } else {
      return BodyType.PRISM;
    }
  }

  /**
   * List of anchors in first base.
   * @return
   */
  public List<String> baseAnchorIDs() {
    ArrayList<String> result = new ArrayList<String>();
    for (int i = 0; i < ptBaseNum(); i++) {
      result.add(getAnchorID(String.valueOf(i)));
    }
    return result;
  }

  /**
   *
   * @return Number of points on base.
   */
  public final int ptBaseNum(){ return _prism.points().size() / 2; }

  // Getters for all vertexes of prism
  public Vect3d getBasePoint(int idx){
    if( idx > _prism.points().size() / 2){
      throw new IndexOutOfBoundsException("Index of point is not of base.");
    }
    return _prism.points().get(idx);
  }

  public Vect3d getUpBasePoint(int idx){
    int baseSize = _prism.points().size() / 2;
    if(idx > _prism.points().size() / 2){
      throw new IndexOutOfBoundsException("Index of point is not of up base.");
    }
    return _prism.points().get(baseSize + idx);
  }

  @Override
  public void addRibs(Editor edt){
    int baseSize = _prism.points().size() / 2;
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

    for(int i = 0; i < baseSize; i++){
      try {
        Vect3d a = getUpBasePoint(i);
        Vect3d b = getUpBasePoint((i + 1) % baseSize);
        Rib3d rib1 = new Rib3d(a, b);
        String Id1 = getAnchorID(String.valueOf(i + baseSize));
        String Id2 = getAnchorID(String.valueOf((i + 1) % baseSize + baseSize));
        edt.addAnchor(rib1, Id1, Id2, this, "rib_Top" + String.valueOf(i) + "_Top" + String.valueOf( (i + 1) % baseSize));
      } catch (ExDegeneration ex) { }
    }

    for(int i = 0; i < baseSize; i++){
      try {
        Vect3d a = getBasePoint(i);
        Vect3d b = getUpBasePoint(i);
        Rib3d rib1 = new Rib3d(a, b);
        String Id1 = getAnchorID(String.valueOf(i));
        String Id2 = getAnchorID(String.valueOf(i + baseSize));
        edt.addAnchor(rib1, Id1, Id2, this, "rib_Base" + String.valueOf(i) + "_Top" + String.valueOf(i));
      } catch (ExDegeneration ex) { }
    }
  }

  @Override
  public void addPlanes(Editor edt){
    int baseSize = _prism.points().size() / 2;
    try{
      // добавляем боковые грани
      for(int i = 0; i < baseSize; i++){
        Vect3d a = getBasePoint(i);
        Vect3d b = getUpBasePoint(i);
        Vect3d c = getUpBasePoint((i + 1) % baseSize);
        Vect3d d = getBasePoint((i + 1) % baseSize);
        String Id1 = getAnchorID(String.valueOf(i));
        String Id2 = getAnchorID(String.valueOf(i + baseSize));
        String Id3 = getAnchorID(String.valueOf((i + 1) % baseSize + baseSize));
        String Id4 = getAnchorID(String.valueOf((i + 1) % baseSize));

        Polygon3d poly1 = new Polygon3d(a, b, c, d);
        ArrayList<String> pointIDs = new ArrayList<String>();
        pointIDs.add(Id1);
        pointIDs.add(Id2);
        pointIDs.add(Id3);
        pointIDs.add(Id4);
        edt.addAnchor(poly1, pointIDs, this, "plane_Base" + String.valueOf(i) + "_Top" + String.valueOf(i) + "_Top" + String.valueOf((i + 1) % baseSize) + "_Base" + String.valueOf((i + 1) % baseSize));
      }

      ArrayList<Vect3d> points = new ArrayList<Vect3d>();
      ArrayList<String> pointIDs = new ArrayList<String>();
      for(int i = 0; i < baseSize; i++){
        Vect3d a = getBasePoint(i);
        points.add(a);
        pointIDs.add(getAnchorID(String.valueOf(i)));
      }
      Polygon3d poly1 = new Polygon3d(points);
      edt.addAnchor(poly1, pointIDs, this, "plane_Base");

      ArrayList<Vect3d> points2 = new ArrayList<Vect3d>();
      ArrayList<String> pointIDs2 = new ArrayList<String>();
      for(int i = 0; i < baseSize; i++){
        Vect3d a = getUpBasePoint(i);
        points2.add(a);
        pointIDs2.add(getAnchorID(String.valueOf(i + baseSize)));
      }
      Polygon3d poly2 = new Polygon3d(points2);
      edt.addAnchor(poly2, pointIDs2, this, "plane_Top");

    } catch(ExGeom ex){
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
      faces = _prism.faces();
    } catch (ExGeom e) {
      util.Fatal.warning(e.getMessage());
    }
    return faces;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    try {
      return _prism.sectionByPlane(plane);
    } catch (ExGeom ex) {
      return super.getIntersectionWithPlane(plane);
    }
  }

  @Override
  public i_Geom getGeom() {
    return _prism;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new PrismBody (id, title, (Prism3d) geom);
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
      PrismBody prism = (PrismBody) result;
     for (int i = 0; i < prism.prism().points().size(); i++) {
      edt.addAnchor(prism.prism().points().get(i), result, String.valueOf(i));
    }
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_prism.intersect(ray), ren.getCameraPosition().eye());
  }
}
