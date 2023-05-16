package bodies;

import geom.AbstractPolygon;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Fatal;
import util.Util;

/**
 * Meta body - cube.
 *
 */
public class MinkowskiBody extends BodyAdapter {

  public MinkowskiBody(String id, String title, Minkowski2d minkowski)
    throws ExGeom {
    super(id, title);
    _minkowskiResult = minkowski;
    _alias = "результат суммы Минковского";
    _exists = true;
  }

  /**
   * Constructor by 3 vertex. Order of points defines cube orientation. Requirement: points are vertex of isosceles right triangle.
   * @param id
   * @param title
   * @param a
   * @param b
   * @throws ExGeom
   */
  public MinkowskiBody(String id, String title, i_Body a, i_Body b)
    throws ExGeom {
    super(id, title);
    _minkowskiResult = new Minkowski2d(a, b);
    _alias = "результат суммы Минковского";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public MinkowskiBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  public Minkowski2d minkowskiResult() { return _minkowskiResult; }

  @Override
  public BodyType type() {
    return BodyType.POLYGON;
  }

  @Override
  public void addRibs(Editor edt){
    
    // adding the ribs
    for (int i = 0; i < points().size() - 1; i++) {
        try {
            Rib3d rib = new Rib3d(points().get(i), points().get(i+1));
            edt.addAnchor(rib, getAnchorID(String.valueOf(i)), getAnchorID(String.valueOf(i+1)), this, "rib_"+i+""+i+1);
        } catch( ExDegeneration ex ){
            util.Fatal.warning("Cannot add ribs of cube: " + ex.getMessage());
        }
    }
    
//      try {
//          Rib3d rib = new Rib3d(points().get(0), points().get(points().size() - 1));
//          edt.addAnchor(rib, getAnchorID(String.valueOf(0)), getAnchorID(String.valueOf(points().size())), this, "rib_"+0+""+points().size());
//      } catch (ExDegeneration ex) {
//          Logger.getLogger(MinkowskiBody.class.getName()).log(Level.SEVERE, null, ex);
//      }
  }

  @Override
  public void addPlanes(Editor edt){
    
    try {
        ArrayList<Vect3d> points = points();
        points.remove(points.size() - 1);
        Polygon3d poly1 = new Polygon3d(points);
        String name = "plane_";
        ArrayList<String> pointIDs = new ArrayList<String>();
          
        for (int i = 0; i < points.size(); i++) {
            pointIDs.add(getAnchorID(String.valueOf(i)));
            name += String.valueOf(i);
        }
          
        edt.addAnchor(poly1, pointIDs, this, name);
    } catch (ExDegeneration ex) {
        Logger.getLogger(MinkowskiBody.class.getName()).log(Level.SEVERE, null, ex);
    }
          
//    // adding the polys
//    try{
//      {
//        Polygon3d poly1 = new Polygon3d(a1, b1, c1, d1);
//        ArrayList<String> pointIDs = new ArrayList<String>();
//        pointIDs.add(getAnchorID("A1"));
//        pointIDs.add(getAnchorID("B1"));
//        pointIDs.add(getAnchorID("C1"));
//        pointIDs.add(getAnchorID("D1"));
//        edt.addAnchor(poly1, pointIDs, this, "plane_A1B1C1D1");
//      }
//      {
//        Polygon3d poly1 = new Polygon3d(a2, b2, c2, d2);
//        ArrayList<String> pointIDs = new ArrayList<String>();
//        pointIDs.add(getAnchorID("A2"));
//        pointIDs.add(getAnchorID("B2"));
//        pointIDs.add(getAnchorID("C2"));
//        pointIDs.add(getAnchorID("D2"));
//        edt.addAnchor(poly1, pointIDs, this, "plane_A2B2C2D2");
//      }
//      {
//        Polygon3d poly1 = new Polygon3d(a1, b1, b2, a2);
//        ArrayList<String> pointIDs = new ArrayList<String>();
//        pointIDs.add(getAnchorID("A1"));
//        pointIDs.add(getAnchorID("B1"));
//        pointIDs.add(getAnchorID("B2"));
//        pointIDs.add(getAnchorID("A2"));
//        edt.addAnchor(poly1, pointIDs, this, "plane_A1B1B2A2");
//      }
//      {
//        Polygon3d poly1 = new Polygon3d(b1, c1, c2, b2);
//        ArrayList<String> pointIDs = new ArrayList<String>();
//        pointIDs.add(getAnchorID("B1"));
//        pointIDs.add(getAnchorID("C1"));
//        pointIDs.add(getAnchorID("C2"));
//        pointIDs.add(getAnchorID("B2"));
//        edt.addAnchor(poly1, pointIDs, this, "plane_B1C1C2B2");
//      }
//      {
//        Polygon3d poly1 = new Polygon3d(c1, d1, d2, c2);
//        ArrayList<String> pointIDs = new ArrayList<String>();
//        pointIDs.add(getAnchorID("C1"));
//        pointIDs.add(getAnchorID("D1"));
//        pointIDs.add(getAnchorID("D2"));
//        pointIDs.add(getAnchorID("C2"));
//        edt.addAnchor(poly1, pointIDs, this, "plane_C1D1D2C2");
//      }
//      {
//        Polygon3d poly1 = new Polygon3d(a1, d1, d2, a2);
//        ArrayList<String> pointIDs = new ArrayList<String>();
//        pointIDs.add(getAnchorID("A1"));
//        pointIDs.add(getAnchorID("D1"));
//        pointIDs.add(getAnchorID("D2"));
//        pointIDs.add(getAnchorID("A2"));
//        edt.addAnchor(poly1, pointIDs, this, "plane_A1D1D2A2");
//      }
//    } catch(ExGeom ex){
//      util.Fatal.warning("Cannot add facet of cube: " + ex.getMessage());
//    }
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
        faces = _minkowskiResult.faces();
      } catch (ExGeom e) {
        Fatal.warning("Error @ MinkowskiBody.getAllFaces");
      }
      return faces;
  }

  private Minkowski2d _minkowskiResult; // math object minkowski sum result

  @Override
  public i_Geom getGeom() {
    return _minkowskiResult;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    try {
        return new MinkowskiBody (id, title, (Minkowski2d) geom);
    } catch (ExGeom ex) { }
    return null;
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    MinkowskiBody minkowski = (MinkowskiBody) result;
    for (int i=0; i < minkowski.points().size(); i++) {
        edt.addAnchor(minkowski.points().get(i), minkowski, String.valueOf(i));  
      }
  }

  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    return Util.getClosestPointToCamera(_minkowskiResult.intersect(ray), ren.getCameraPosition().eye());
  }
  
  public ArrayList<Vect3d> points () {
      return _minkowskiResult.points();
  }
};
