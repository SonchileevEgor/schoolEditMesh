package geom;

import bodies.RhombicDodecahedronBody;
import editor.i_Body;
import static geom.SectionOfRibObject.sectionOfRibObject;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author e-son
 */
public class Mesh3d implements i_Geom, i_OrientableGeom {
  private ArrayList<RhombicDodecahedronBody> bds;

  public ArrayList<RhombicDodecahedronBody> getBds() {
    return bds;
  }

  public Mesh3d(ArrayList<RhombicDodecahedronBody> bds) {
      this.bds = bds;
  }

  /**
   * @return all vertices of mesh
   */
  public ArrayList<Vect3d> points(){
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    for (int i =0; i< bds.size(); i++) {
        for (int j=0; j< bds.get(i).getAllVertices().size(); j++) {
            points.add(bds.get(i).getAllVertices().get(j));
        }
    }
    return points;
  }
  
  /**
   * @return all ribs of mesh
   * @throws ExDegeneration
   */
  public ArrayList<Rib3d> ribs() throws ExDegeneration {
    ArrayList<Rib3d> ribs = new ArrayList<Rib3d>();
    for (int i =0; i< bds.size(); i++) {
        for (int j=0; j< bds.get(i).getAllEdges(null).size(); j++) {
            ribs.add(bds.get(i).getAllEdges(null).get(j));
        }
    }
    return ribs;
  }

  /**
   * @return all faces of mesh
   * @throws ExGeom
   */
  public ArrayList<Polygon3d> faces() throws ExGeom {
    ArrayList<Polygon3d> faces = new ArrayList<Polygon3d>();
    for (int i =0; i< bds.size(); i++) {
        for (int j=0; j< bds.get(i).getAllFaces(null).size(); j++) {
            faces.add(bds.get(i).getAllFaces(null).get(j));
        }
    }
    return faces;
  }


  /**
   * @param line
   * @return Список точек пересечения симплекса и прямой
   * @throws ExGeom
   */
  public ArrayList<Vect3d> intersect(Line3d line) throws ExGeom {
    ArrayList<Polygon3d> faces = this.faces();
    ArrayList<Vect3d> points = new ArrayList<Vect3d>();
    for(int i=0; i<faces.size(); i++ )
      for(int j=0; j<faces().get(i).intersectWithLine(line).size(); j++)
        if(!points.contains(faces().get(i).intersectWithLine(line).get(j)))
           points.add(faces().get(i).intersectWithLine(line).get(j));

    return points;
  }


  @Override
  public ArrayList<Vect3d> deconstr() {
    // в качестве основного набора точек - вершины
    return points();
  }

  @Override
  public i_Geom constr(ArrayList<Vect3d> points) {
    Mesh3d mesh = null;
//    try {
//      cube = new Mesh3d(points);
//    } catch (ExDegeneration ex) {
//      GeomErrorHandler.errorMessage(ex);
//    }
    return mesh;
  }

  @Override
  public Vect3d getUpVect() {
    return Vect3d.UZ;
  }

  @Override
  public boolean isOrientable() {
    return true;
  }

  @Override
  public GeomType type() {
    return GeomType.MESH3D;
  }
}