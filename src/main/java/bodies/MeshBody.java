package bodies;

import geom.AbstractPolygon;
import opengl.Render;
import editor.Editor;
import editor.i_Body;
import geom.*;

import java.util.ArrayList;
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
    return BodyType.CUBE;
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
  public void addAnchorsToBody(i_Body result, Editor edt) {
//    MeshBody cube = (MeshBody) result;
//    edt.addAnchor(cube.A1(), result, "A1");
//    edt.addAnchor(cube.B1(), result, "B1");
//    edt.addAnchor(cube.C1(), result, "C1");
//    edt.addAnchor(cube.D1(), result, "D1");
//    edt.addAnchor(cube.A2(), result, "A2");
//    edt.addAnchor(cube.B2(), result, "B2");
//    edt.addAnchor(cube.C2(), result, "C2");
//    edt.addAnchor(cube.D2(), result, "D2");
  }

  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
      return new Vect3d();
//    return Util.getClosestPointToCamera(_cube.intersect(ray), ren.getCameraPosition().eye());
  }
};
