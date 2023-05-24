package gui.inspector.bodycontext;

import bodies.BodyType;
import editor.ExNoBody;
import gui.EdtController;
import java.awt.Component;

/**
 * Launcher of context menus.
 *
 * @author alexeev
 */
public class ContextMenuLauncher {
  static public void launch(EdtController ctrl, Component invoker, String bodyID, int scrX, int scrY)
          throws ExNoBody {
    BodyContextMenu menu;
    BodyType type = ctrl.getBody(bodyID).type();
    if( type == BodyType.TRIANGLE ){
      menu = new TriangleContextMenu(ctrl, bodyID);
    } else if( type.isPolygon() ){
      menu = new PolygonContextMenu(ctrl, bodyID);
    } else {
      switch (type) {
        case POINT:
          menu = new PointContextMenu(ctrl, bodyID); break;
        case PLANE:
          menu = new PlaneContextMenu(ctrl, bodyID); break;
        case RIB:
          menu = new RibContextMenu(ctrl, bodyID); break;
        case LINE:
          menu = new LineContextMenu(ctrl, bodyID); break;
        case CUBE:
          menu = new CubeContextMenu(ctrl, bodyID); break;
        case ICOSAHEDRON:
          menu = new IcosahedronContextMenu(ctrl, bodyID); break;
        case DODECAHEDRON:
          menu = new DodecahedronContextMenu(ctrl, bodyID); break;
        case OCTAHEDRON:
          menu = new OctahedronContextMenu(ctrl, bodyID); break;
        case REG_TETRAHEDRON:
          menu = new RegularPolyhedronContextMenu(ctrl, bodyID); break;
        case ELONGATED_DODECAHEDRON: 
          menu = new ElongatedDodecahedronContextMenu(ctrl, bodyID); break;
        case TRUNCATED_OCTAHEDRON:
          menu = new TruncatedOctahedronContextMenu(ctrl, bodyID); break;
        case RHOMBIC_DODECAHEDRON:
          menu = new RhombicDodecahedronContextMenu(ctrl, bodyID); break;
        case STELLAR_OCTAHEDRON:
          menu = new StellaOctahedronContextMenu(ctrl, bodyID); break;
        case PRISM:
        case PARALLELEPIPED:
          menu = new PrismContextMenu(ctrl, bodyID); break;
        case PYRAMID:
          menu = new PyramidContextMenu(ctrl, bodyID); break;
        case TETRAHEDRON:
          menu = new TetrahedronContextMenu(ctrl, bodyID); break;
        case CONE:
        case ELLIPSOID:
          menu = new ConeContextMenu(ctrl, bodyID); break;
        case CYLINDER:
          menu = new CylinderContextMenu(ctrl, bodyID); break;
        case CIRCLE:
          menu = new CircleContextMenu(ctrl, bodyID); break;
        case SPHERE:
        case HYPERBOLOID_OF_ONE_SHEET:
        case HYPERBOLOID_OF_TWO_SHEET:
        case ELLIPTIC_PARABOLOID:
          menu = new SphereContextMenu(ctrl, bodyID); break;
        case CONE_SECTION:
        case CYLINDER_SECTION:
          menu = new SectionContextMenu(ctrl, bodyID); break;
        case ANGLE:
          menu = new AngleContextMenu(ctrl, bodyID); break;
        case ARC:
        case PARABOLA:
        case HYPERBOLE:
        case ELLIPSE:
          menu = new ArcContextMenu(ctrl, bodyID); break;
        case MESH3D:
          menu = new Mesh3DContextMenu(ctrl, bodyID); break;
        default:
          menu = new DefaultBodyContextMenu(ctrl, bodyID);
      }
    }
    menu.show(invoker, scrX, scrY);
  }
}
