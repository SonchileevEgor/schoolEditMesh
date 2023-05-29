package bodies;

import geom.AbstractPolygon;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import maquettes.EarClipping;
import util.Fatal;
import util.Util;

public class MeshBody extends BodyAdapter {

  public MeshBody(String id, String title, Mesh3d mesh)
    throws ExGeom {
    super(id, title);
    _mesh = mesh;
    _alias = "сетка";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public MeshBody(String id, String title) {
    super(id, title);
    _exists = false;
  }

  public Mesh3d mesh() { return _mesh; }

  @Override
  public BodyType type() {
    return BodyType.MESH3D;
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
  public HashMap<String, String> getAnchors() {
    return _anchors;
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
      ArrayList<Polygon3d> faces = new ArrayList<>();
      try {
        faces = _mesh.faces();
      } catch (ExGeom e) {
        Fatal.warning("Error @ MeshBody.getAllFaces");
      }
      return faces;
  }

  private Mesh3d _mesh; // math object mesh

  @Override
  public i_Geom getGeom() {
    return _mesh;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    try {
        return new MeshBody (id, title, (Mesh3d) geom);
    } catch (ExGeom ex) { }
    return null;
  }
  
  @Override
  public ArrayList<Vect3d> getAllVertices() {
    return _mesh.points();
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    MeshBody mesh = (MeshBody) result;
    for (int i=0; i< this.getAllVertices().size(); i++) {
        edt.addAnchor(this.getAllVertices().get(i), result, Integer.toString(i));
    }
  }

  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
      return new Vect3d();
//    return Util.getClosestPointToCamera(_cube.intersect(ray), ren.getCameraPosition().eye());
  }
  
  public void print3d() {
    EarClipping triangulatedBody = new EarClipping(getAllVertices());
  }
};
