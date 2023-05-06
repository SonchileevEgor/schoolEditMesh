package gui.mode;

import bodies.BodyType;
import bodies.EllipsoidBody;
import bodies.SphereBody;
import builders.BodyBuilder;
import builders.ConeSectionBuilder;
import builders.CubeSectionBuilder;
import builders.CylinderSectionBuilder;
import builders.DodecahedronSectionBuilder;
import builders.EllipsoidSectionBuilder;
import builders.ElongatedDodecahedronSectionBuilder;
import builders.HexPrismSectionBuilder;
import builders.IcosahedronSectionBuilder;
import builders.OctahedronSectionBuilder;
import builders.PlaneXPlaneBuilder;
import builders.PrismSectionBuilder;
import builders.PyramidSectionBuilder;
import builders.RhombicDodecahedronSectionBuilder;
import builders.SphereSectionBuilder;
import builders.TetrahedronSectionBuilder;
import builders.TruncatedOctahedronSectionBuilder;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_FocusChangeListener;
import geom.Checker;
import geom.Cube3d;
import geom.Dodecahedron3d;
import geom.ElongatedDodecahedron3d;
import geom.ExDegeneration;
import geom.ExGeom;
import geom.Icosahedron3d;
import geom.Octahedron3d;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Prism3d;
import geom.Pyramid3d;
import geom.RhombicDodecahedron3d;
import geom.Simplex3d;
import geom.TruncatedOctahedron3d;
import geom.Vect3d;
import gui.EdtController;
import gui.IconList;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Icon;
import opengl.Render;

/**
 *
 * @author Elena
 */
public class CreatePlaneSectionMode extends CreateBodyMode implements i_FocusChangeListener {

  private BodyType _type;// type of object
  private i_Body _body;
  private ArrayList<BodyType> _types;
  private String _bodyID, _planeID;
  private Plane3d _plane;

  public CreatePlaneSectionMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void run() {
    super.run();
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _types = new ArrayList<>();
    _types.add(BodyType.SPHERE);
    _types.add(BodyType.PLANE);
    _types.add(BodyType.CUBE);
    _types.add(BodyType.CONE);
    _types.add(BodyType.PRISM);
    _types.add(BodyType.PYRAMID);
    _types.add(BodyType.TETRAHEDRON);
    _types.add(BodyType.CYLINDER);
    _types.add(BodyType.ICOSAHEDRON);
    _types.add(BodyType.OCTAHEDRON);
    _types.add(BodyType.DODECAHEDRON);
    _types.add(BodyType.ELONGATED_DODECAHEDRON);
    _types.add(BodyType.RHOMBIC_DODECAHEDRON);
    _types.add(BodyType.TRUNCATED_OCTAHEDRON);
    _types.add(BodyType.ELONGATED_DODECAHEDRON);
    _types.add(BodyType.RHOMBIC_DODECAHEDRON);
    _types.add(BodyType.TRUNCATED_OCTAHEDRON);
    _types.add(BodyType.PARALLELEPIPED);
    _types.add(BodyType.REG_TETRAHEDRON);
    _types.add(BodyType.HEXPRISM);
    _types.add(BodyType.ELLIPSOID);

    canvas().getHighlightAdapter().setTypes(BodyType.PLANE);
    _ctrl.redraw();
  }

  @Override
  public void dispose() {
    super.dispose();
    _ctrl.getFocusCtrl().removeFocusChangeListener(this);
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
  }

  @Override
  public void focusLost(String id, boolean isBody) {
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (id == null || !isBody) {
      return;
    }
    if (_numOfChosenAnchors == 0) {
      try {
        i_Body bd = _ctrl.getBody(id);
        _planeID = id;
        if (checker.checkBodyTypeIsPlane(bd.type())) {
          _plane = (Plane3d)bd.getGeom();
          _numOfChosenAnchors++;
          canvas().getHighlightAdapter().setTypes(_types);
          _ctrl.status().showMessage(_msg.current());
          _ctrl.redraw();
        }
      } catch (ExNoBody ex) {
      }
    } else if (_numOfChosenAnchors == 1) {
      try {
        i_Body bd = _ctrl.getBody(id);
        if (!_types.contains(bd.type())) {
          return;
        }
        _type = bd.type();
        _body = bd;
        _bodyID = id;
        _numOfChosenAnchors++;
        create();
      } catch (ExNoBody ex) {
      }
    }
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите секущую плоскость{MOUSELEFT}",
            "Выберите тело для пересечения{MOUSELEFT}");
  }

  @Override
  protected void setName() {
  }

  @Override
  protected void nativeDraw1(Render ren) {
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_PLANE_SECTION;
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.PLANE_SECTION.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.PLANE_SECTION.getLargeIcon();
  }

  @Override
  protected void create() {
    i_BodyBuilder builder = null;
    switch (_type) {
      case CUBE: {
        Cube3d cube = (Cube3d)_body.getGeom();
        try {
          Polygon3d poly = cube.sectionByPlane(_plane);
          builder = new CubeSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_CUBE, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case PYRAMID: {
        Pyramid3d pyr = (Pyramid3d)_body.getGeom();
        try {
          Polygon3d poly = pyr.sectionByPlane(_plane);
          builder = new PyramidSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_PYRAMID, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case PARALLELEPIPED:
      case PRISM: {
        Prism3d prism = (Prism3d)_body.getGeom();
        try {
          Polygon3d poly = prism.sectionByPlane(_plane);
          builder = new PrismSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_PRISM, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case REG_TETRAHEDRON:
      case TETRAHEDRON: {
        Simplex3d simplex = (Simplex3d)_body.getGeom();
        try {
          Polygon3d poly = simplex.sectionByPlane(_plane);
          builder = new TetrahedronSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_TETRAHEDRON, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case OCTAHEDRON: {
        Octahedron3d oct = (Octahedron3d)_body.getGeom();
        try {
          Polygon3d poly = oct.sectionByPlane(_plane);
          builder = new OctahedronSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_OCTAHEDRON, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case HEXPRISM: {
        Prism3d pr = (Prism3d)_body.getGeom();
        try {
          Polygon3d poly = pr.sectionByPlane(_plane);
          builder = new HexPrismSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_PRISM, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case DODECAHEDRON: {
        Dodecahedron3d d = (Dodecahedron3d)_body.getGeom();
        try {
          Polygon3d poly = d.sectionByPlane(_plane);
          builder = new DodecahedronSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_DODECAHEDRON, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case ICOSAHEDRON: {
        Icosahedron3d ic = (Icosahedron3d)_body.getGeom();
        try {
          Polygon3d poly = ic.sectionByPlane(_plane);
          builder = new IcosahedronSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_ICOSAHEDRON, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case ELONGATED_DODECAHEDRON: {
        ElongatedDodecahedron3d ed = (ElongatedDodecahedron3d)_body.getGeom();
        try {
          Polygon3d poly = ed.sectionByPlane(_plane);
          builder = new ElongatedDodecahedronSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_DODECAHEDRON, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case RHOMBIC_DODECAHEDRON: {
        RhombicDodecahedron3d rd = (RhombicDodecahedron3d)_body.getGeom();
        try {
          Polygon3d poly = rd.sectionByPlane(_plane);
          builder = new RhombicDodecahedronSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(RhombicDodecahedronSectionBuilder.BLDKEY_RHOMBICDODEC, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case TRUNCATED_OCTAHEDRON: {
        TruncatedOctahedron3d to = (TruncatedOctahedron3d)_body.getGeom();
        try {
          Polygon3d poly = to.sectionByPlane(_plane);
          builder = new TruncatedOctahedronSectionBuilder();
          if (poly.points().size() == 1) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else if (poly.points().size() == 2) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.RIB.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POLYGON.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_OCTAHEDRON, _bodyID);
        } catch (ExGeom ex) {
        }
        break;
      }
      case CYLINDER: {
        builder = new CylinderSectionBuilder();
        builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.CYLINDER_SECTION.getName(_ctrl.getEditor()));
        builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
        builder.setValue(BodyBuilder.BLDKEY_CYLINDER, _bodyID);
        break;
      }
      case CONE: {
        builder = new ConeSectionBuilder();
        builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.CONE_SECTION.getName(_ctrl.getEditor()));
        builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
        builder.setValue(BodyBuilder.BLDKEY_CONE, _bodyID);
        break;
      }
      case SPHERE: {
        SphereBody s = (SphereBody)_body;
        Vect3d c1 = _plane.projectionOfPoint(s.getCenter());
        builder = new SphereSectionBuilder();
        if (Checker.isPointOnSphere(c1, s.sphere())) {
          builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
        } else {
          builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.CIRCLE.getName(_ctrl.getEditor()));
        }
        builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
        builder.setValue(BodyBuilder.BLDKEY_SPHERE, _bodyID);
        break;
      }
      case ELLIPSOID: {
        try {
          EllipsoidBody el = (EllipsoidBody)_body;
          Vect3d c2 = el.convPlane(_plane).projectionOfPoint(el.convSphere().center());
          builder = new EllipsoidSectionBuilder();
          if (Checker.isPointOnSphere(c2, el.convSphere())) {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.POINT.getName(_ctrl.getEditor()));
          } else {
            builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.ELLIPSE.getName(_ctrl.getEditor()));
          }
          builder.setValue(BodyBuilder.BLDKEY_PLANE, _planeID);
          builder.setValue(BodyBuilder.BLDKEY_ELLIPSOID, _bodyID);
        } catch (ExDegeneration ex) {
        }
        break;
      }
      case PLANE: {
        builder = new PlaneXPlaneBuilder();
        builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.LINE.getName(_ctrl.getEditor()));
        builder.setValue(PlaneXPlaneBuilder.BLDKEY_PLANE1, _planeID);
        builder.setValue(PlaneXPlaneBuilder.BLDKEY_PLANE2, _bodyID);
        break;
      }
    }
    if (builder != null) {
      _ctrl.add(builder, checker, false);
    }
    reset();
  }

  @Override
  protected String description() {
    return "<html><strong>Сечение</strong><br>тела плоскостью";
  }

  @Override
  protected boolean isEnabled() {
    return _ctrl.containsBodies(BodyType.PLANE, 1);
  }

  @Override
  protected void nativeDraw0(Render ren) {
  }

  @Override
  public boolean isNameSelectable() {
    return false;
  }
}
