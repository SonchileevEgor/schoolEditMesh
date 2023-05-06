package geom;

import static geom.Checker.isOrthogonal;
import static geom.Vect3d.sub;
import java.util.ArrayList;

/**
 * Section of rib bodies (cube, prism, pyramid, tetrahedron)
 * @author Vladislav, rita
 */

public class SectionOfRibObject {
  /**
   * Construct section of rib body by plane
   * @param ribs array of all ribs which belong body
   * @param plane section plane
   * @return Polygon3d
   * @throws ExGeom
   */
  public static Polygon3d sectionOfRibObject(Rib3d[] ribs, Plane3d plane) throws ExGeom {
    try {
      ArrayList<Vect3d> pointOfPolygon = ribsSection(ribs, plane);
      if(!pointOfPolygon.isEmpty()){
        Vect3d first = pointOfPolygon.get(0);
        Vect3d.sortPlanarConvexSet(pointOfPolygon);
        Vect3d.rotatePointsList(pointOfPolygon, first);
      }
      return new Polygon3d(pointOfPolygon);
    } catch( ExZeroDivision ex ){
      throw new ExGeom(ex.getMessage());
    }
  }

  /**
   * Return segments of polygon section.
   *
   * @param ribs
   * @param plane
   * @return
   * @throws ExDegeneration
   */
  private static ArrayList<Vect3d> ribsSection(Rib3d[] ribs, Plane3d plane) throws ExDegeneration {
    Vect3d point;
    // List of rib points which lie in plane (LIST).
    ArrayList<Vect3d> verticesOfPolygon = new ArrayList<Vect3d>();
    // Find points of intersection ribs and plane.
    // If some rib lie in plane add endpoints of this rib to LIST.
    for(int i = 0; i < ribs.length; i++){
      Vect3d l = sub(ribs[i].a(), ribs[i].b());// direction vector
      Line3d ribLine = new Line3d(ribs[i].a(), l);// line wnich contais rib sub i
      // Check line and plane have intersection
      if(!isOrthogonal(ribLine.l(), plane.n())){// Line intersect plane.
        point = ribLine.intersectionWithPlane(plane);
        // Check point of intersection of line and plane belongs rib.
        if((point.x() <= Math.max(ribs[i].b().x(), ribs[i].a().x())) && (point.x() >= Math.min(ribs[i].b().x(), ribs[i].a().x())) &&
           (point.y() <= Math.max(ribs[i].b().y(), ribs[i].a().y())) && (point.y() >= Math.min(ribs[i].b().y(), ribs[i].a().y())) &&
           (point.z() <= Math.max(ribs[i].b().z(), ribs[i].a().z())) && (point.z() >= Math.min(ribs[i].b().z(), ribs[i].a().z())) &&
        // Check point of intersection of line and plane don't belong LIST.
           !verticesOfPolygon.contains(point)
          )
          verticesOfPolygon.add(point);
      }else{// Line and plane have no intersection.
        // Check rib belongs plane.
        if(plane.containsPoint(ribs[i].a()) && plane.containsPoint(ribs[i].b())){
          // Check endpoints of rib don't belong LIST.
          if(!verticesOfPolygon.contains(ribs[i].a()))
            verticesOfPolygon.add(ribs[i].a());
          if(!verticesOfPolygon.contains(ribs[i].b()))
            verticesOfPolygon.add(ribs[i].b());
        }
      }
    }
    return verticesOfPolygon;
  }
}
