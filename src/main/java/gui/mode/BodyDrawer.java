package gui.mode;

import geom.Angle3d;
import geom.Arc3d;
import geom.Circle3d;
import geom.Cone3d;
import geom.ConeSection3d;
import geom.Cube3d;
import geom.Cylinder3d;
import geom.CylinderSection3d;
import geom.Dodecahedron3d;
import geom.EllipseMain3d;
import geom.ExDegeneration;
import geom.Hyperbole3d;
import geom.Icosahedron3d;
import geom.Line3d;
import geom.Octahedron3d;
import geom.PairOfLines;
import geom.Parabola3d;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Prism3d;
import geom.Pyramid3d;
import geom.Ray3d;
import geom.Rib3d;
import geom.Simplex3d;
import geom.Sphere3d;
import geom.Vect3d;
import geom.i_Geom;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import opengl.colorgl.ColorGL;

/**
 * Draws temporary bodies.
 *
 * @author alexeev
 */
public class BodyDrawer {

  private BodyDrawer() {
  }

  ;

  public static void draw(Render ren, i_Geom body) {
    switch (body.type()) {
      case ANGLE3D:
        Angle3d angle = (Angle3d)body;
        Drawer.drawPoint(ren, angle.vertex());
        Drawer.drawSegment(ren, angle.vertex(), angle.pointOnFirstSide());
        Drawer.drawSegment(ren, angle.vertex(), angle.pointOnSecondSide());
        break;
      case ARC3D:
        Arc3d arc = (Arc3d)body;
        try {
          Drawer.drawArc(ren, arc, TypeFigure.CURVE);
          Drawer.drawPoint(ren, arc.center());
          Drawer.drawPoint(ren, arc.vert1());
          Drawer.drawPoint(ren, arc.vert2());
        } catch (ExDegeneration ex) {
        }
        break;
      case CIRCLE3D:
        Circle3d circ = (Circle3d)body;
        Drawer.drawPoint(ren, circ.center());
        Drawer.drawCircle(ren, circ.radiusLength(), circ.center(), circ.normal(), TypeFigure.WIRE);
        break;
      case CONE3D:
        Cone3d cone = (Cone3d)body;
        try {
          Drawer.drawConeCarcass(ren, cone);
        } catch (ExDegeneration ex) {
        }
        Drawer.drawPoint(ren, cone.c());
        break;
      case CONE_SECTION3D:
        ConeSection3d coneSec = (ConeSection3d)body;
        try {
          Drawer.drawConeSection(ren, coneSec, TypeFigure.WIRE);
        } catch (ExDegeneration ex) {
        }
        break;
      case CUBE3D:
        Cube3d cube = (Cube3d)body;
        Drawer.drawParallelepiped(ren, cube.A1(), cube.B1(), cube.D1(), cube.A2(), TypeFigure.WIRE);
        break;
      case CYLINDER3D:
        Cylinder3d cyl = (Cylinder3d)body;
        Drawer.drawCylinder(ren, cyl.r(), cyl.c0(), cyl.c1(), TypeFigure.WIRE, 2);
        break;
      case CYLINDER_SECTION3D:
        CylinderSection3d cylSec = (CylinderSection3d)body;
        try {
          Drawer.drawCylinderSection(ren, cylSec, TypeFigure.WIRE);
        } catch (ExDegeneration ex) {
        }
        break;
      case DODECAHEDRON3D:
        Dodecahedron3d dod = (Dodecahedron3d)body;
        try {
          Drawer.drawDodecahedron(ren, dod, TypeFigure.WIRE);
        } catch (ExDegeneration ex) {
        }
        break;
      case ELLIPSE3D:
        EllipseMain3d ell = (EllipseMain3d)body;
        Drawer.drawEllipse(ren, ell, TypeFigure.WIRE);
        break;
      case DODECAHEDRON_ELONGATED3D:
        break;
      case HYPERBOLE3D:
        Hyperbole3d hyp = (Hyperbole3d)body;
        Drawer.drawHyperbole(ren, hyp, TypeFigure.WIRE);
        break;
      case ICOSAHEDRON3D:
        Icosahedron3d ics = (Icosahedron3d)body;
        try {
          Drawer.drawIcosahedron(ren, ics, TypeFigure.WIRE);
        } catch (ExDegeneration ex) {
        }
        break;
      case LINE3D:
        Line3d line = (Line3d)body;
        Drawer.drawLine(ren, line.pnt(), line.pnt2());
        break;
      case MENGER:
        break;
      case OCTAHEDRON3D:
        Octahedron3d oct = (Octahedron3d)body;
        try {
          Drawer.drawOctahedron(ren, oct, TypeFigure.WIRE);
        } catch (ExDegeneration ex) {
        }
        break;
      case PARABOLA3D:
        Parabola3d par = (Parabola3d)body;
        Drawer.drawParabola(ren, par, TypeFigure.WIRE);
        break;
      case PAIROFLINES3D:
        PairOfLines pairOfLines = (PairOfLines)body;
        Drawer.drawPairOfLines(ren, pairOfLines.pnt1(), pairOfLines.pnt11(), pairOfLines.pnt2(), pairOfLines.pnt21());
        break;
      case PLANE3D:
        Plane3d plane = (Plane3d)body;
        Drawer.drawPlane(ren, plane);
        break;
      case POLYGON3D:
      case TRIANG3D:
        Polygon3d poly = (Polygon3d)body;
        Drawer.drawPolygon(ren, poly.points(), TypeFigure.WIRE);
        break;
      case PRISM3D:
        Prism3d prism = (Prism3d)body;
        Drawer.drawPrism(ren, prism, TypeFigure.WIRE);
        break;
      case PYRAMID3D:
        Pyramid3d pyr = (Pyramid3d)body;
        Drawer.drawPyramid(ren, pyr, TypeFigure.WIRE);
        break;
      case RAY3D:
        Ray3d ray = (Ray3d)body;
        Drawer.drawPoint(ren, ray.pnt());
        Drawer.drawRay(ren, ray.pnt(), ray.l());
        break;
      case DODECAHEDRON_ROMBIC3D:
        break;
      case RIB3D:
        Rib3d rib = (Rib3d)body;
        Drawer.drawPoint(ren, rib.a());
        Drawer.drawPoint(ren, rib.b());
        Drawer.drawSegment(ren, rib.a(), rib.b());
        break;
      case SIMPLEX3D:
        Simplex3d tet = (Simplex3d)body;
        Drawer.drawTetrahedron(ren, ColorGL.RED, null, TypeFigure.WIRE, tet.a(), tet.b(), tet.c(), tet.d());
        break;
      case SPHERE3D:
        Sphere3d sph = (Sphere3d)body;
        Drawer.drawPoint(ren, sph.center());
        Drawer.drawSphereCircles(ren, sph.center(), sph.radius());
        break;
      case OCTAHEDRON_TRUNCATED3D:
        break;
      case VECT3D:
        Vect3d point = (Vect3d)body;
        Drawer.drawPoint(ren, point);
        break;
    }
  }
}
