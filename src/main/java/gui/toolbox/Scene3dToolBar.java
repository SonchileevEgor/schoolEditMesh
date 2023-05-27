package gui.toolbox;

import gui.elements.AngleStyle;
import gui.EdtController;
import gui.IconList;
import gui.action.ActionFactory;
import gui.action.CreateDialogActionFactory;
import gui.action.EdtAction;
import gui.extensions.Extensions;
import gui.i_AppSettingsChangeListener;
import gui.mode.ModeList;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

/**
 * Функции для работы со сценой.
 *
 * @author alexeev
 */
public class Scene3dToolBar extends EdtToolBar implements i_AppSettingsChangeListener {
  private ArrayList<EdtAction> _pointActionList;
  private ArrayList<EdtAction> _lineActionList;
  private ArrayList<EdtAction> _planeActionList;
  private ArrayList<EdtAction> _polygonActionList;
  private ArrayList<EdtAction> _angleActionList;
  private ArrayList<EdtAction> _circleActionList;
  private ArrayList<EdtAction> _arcActionList;
  private ArrayList<EdtAction> _polyhedraActionList;
  private ArrayList<EdtAction> _pyramidActionList;
  private ArrayList<EdtAction> _prismActionList;
  private ArrayList<EdtAction> _coneActionList;
  private ArrayList<EdtAction> _cylinderActionList;
  private ArrayList<EdtAction> _sphereActionList;
  private ArrayList<EdtAction> _projectionActionList;
  private ArrayList<EdtAction> _sectionActionList;
  private ArrayList<EdtAction> _transformActionList;
  private ArrayList<EdtAction> _curvesOf2ndOrderList;
  private ArrayList<EdtAction> _surfacesOf2ndOrderList;
  private ArrayList<EdtAction> _parallelohedraActionList;
  
  private EnumMap<Extensions, EdtToolButton> _extButtons;
  
  public Scene3dToolBar(EdtController ctrl) {
    super(ctrl);

    _pointActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_POINT.getAction(_ctrl));
      add(CreateDialogActionFactory.POINT.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HULL_2.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HULL_3.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_RIB_ANCHOR.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_LINE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_ARC.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_DISK.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_POLY_ANCHOR.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_SPHERE.getAction(_ctrl));
    }};

    _lineActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_RIB.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RIB_BY_RIB.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RIB_PROPORTIONALN_RIB.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RIB_BY_TWO_POINTS_AND_LENGTH.getAction(_ctrl));
      add(ModeList.MODE_CREATE_LINE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RAY_TWO_POINTS.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RIB_BY_SKEW_LINES.getAction(_ctrl));
      add(ModeList.MODE_CREATE_LINE_PARALLEL_LINE_OR_RIB_OR_RAY.getAction(_ctrl));
      add(ModeList.MODE_LINE_BY_POINT_PARAL_PLANE_OR_POLY.getAction(_ctrl));
      add(ModeList.MODE_CREATE_LINE_ORTH_LINE_OR_RIB_OR_RAY.getAction(_ctrl));
      add(ModeList.MODE_LINE_BY_POINT_ORTH_PLANE_OR_POLY.getAction(_ctrl));
    }};

    _planeActionList = new ArrayList<EdtAction>(){{
      add(ActionFactory.CREATE_DEFAULT_PLANE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PLANE_BY_3_POINTS.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PLANE_BY_PNT.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PLANE_BY_POINT_AND_LINE.getAction(_ctrl));
      add(ModeList.MODE_PLANE_BY_POINT_PARAL_PLANE_OR_POLY.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PLANE_ORTH_PLANE_OR_LINE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PLANE_BY_LINE_ANGLE_AT_PLANE_OR_POLY.getAction(_ctrl));
      add(ModeList.MODE_CREATE_BISECTOR_PLANE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PLANE_PAR_LINE_BY_POINT.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PLANE_BY_SKEW_LINES.getAction(_ctrl));
    }};

    _polygonActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_POLYGON.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POLYGON_REGULAR.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RECTANGLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RECT_TRIANGLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PARALLELOGRAM.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TRAPEZE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_ISOSCELES_TRAPEZE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RHOMBUS_BY_DIAGONAL.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RHOMBUS_BY_SIDE_ANGLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_MINKOWSKI_SUM_2D.getAction(_ctrl));
    }};

    _angleActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_ANGLE.getAction(_ctrl, AngleStyle.SINGLE));
      add(ModeList.MODE_CIRCLE_IN_ANGLE.getAction(_ctrl));
    }};

    _circleActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_CIRCLE_BY_3_POINTS.getAction(_ctrl));
      add(ModeList.MODE_CIRCLE_BY_PLANE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_INSCRIBE_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_CIRCUMSCRIBE_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TANGENT_TO_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TANGENT_OUTER_TWO_CIRCLES.getAction(_ctrl));
      add(ModeList.MODE_CREATE_CIRCLE_BY_DIAMETER.getAction(_ctrl));
      add(ModeList.MODE_CREATE_CIRCLE_CENTER_RADIUS_NORMAL.getAction(_ctrl));
    }};

    _arcActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_ARC.getAction(_ctrl));
      add(ModeList.MODE_CREATE_ARC_ON_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HALF_CIRCLE.getAction(_ctrl));
    }};

    _polyhedraActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_CUBE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_REGTETRAHEDRON.getAction(_ctrl));
      add(ModeList.MODE_CREATE_OCTAHEDRON.getAction(_ctrl));
      add(ModeList.MODE_CREATE_ICOSAHEDRON.getAction(_ctrl));
      add(ModeList.MODE_CREATE_DODECAHEDRON.getAction(_ctrl));
      add(ModeList.MODE_CREATE_CUBOCTAHEDRON.getAction(_ctrl));
    }};

    _pyramidActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_TETRAHEDRON.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PYRAMID.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PYRAMID_BY_BASE_AND_TOP.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PYRAMID_REGULAR.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PYRAMID_RECTANG.getAction(_ctrl));
    }};

    _prismActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_RECTANGULAR_PARALLELEPIPED.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PRISM.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PRISM_BY_BASE_AND_TOP.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PRISM_REGULAR.getAction(_ctrl));
    }};

    _coneActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_CONE.getAction(_ctrl));
    }};

    _cylinderActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_CYLINDER.getAction(_ctrl));
    }};

    _sphereActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_SPHERE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_SPHERE_BY_2_POINTS.getAction(_ctrl));
      add(ModeList.MODE_CREATE_SPHERE_BY_4_POINTS.getAction(_ctrl));
      add(ModeList.MODE_INSCRIBE_SPHERE.getAction(_ctrl));
      add(ModeList.MODE_CIRCUMSCRIBE_SPHERE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TANGENT_POINT_OF_SPHERE_AND_PLANE_OR_FACET.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TANGENT_PLANE_IN_POINT_ON_SPHERE.getAction(_ctrl));
    }};

    _projectionActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_PROJECT_POINT.getAction(_ctrl));
      add(ModeList.MODE_CREATE_LINE_OR_RIB_PROJ_ON_PLANE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_BODY_PROJECTION.getAction(_ctrl));
    }};

    _sectionActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_PLANE_SECTION.getAction(_ctrl));
      add(ModeList.MODE_CREATE_LINE_SECTION.getAction(_ctrl));
      add(ModeList.MODE_CREATE_INTERSECTION.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RIB_SECTION.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POLY_SECTION.getAction(_ctrl));
      add(ModeList.MODE_CREATE_SPHERE_X_SPHERE.getAction(_ctrl));
    }};

    _transformActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_3D_SYM.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HOMOTHETY.getAction(_ctrl));
      add(ModeList.MODE_CREATE_ROTATION.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TRANSLATION.getAction(_ctrl));
    }};

    _curvesOf2ndOrderList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_ELIPSE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HYPERBOLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PARABOLA.getAction(_ctrl));
      add(ModeList.MODE_CREATE_CONIC.getAction(_ctrl));
    }};

    _surfacesOf2ndOrderList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_ELLIPSOID.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HYPERBOLOID_OF_ONE_SHEET.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HYPERBOLOID_OF_TWO_SHEET.getAction(_ctrl));
      add(ModeList.MODE_CREATE_ELLIPTIC_PARABOLOID.getAction(_ctrl));
      add(ModeList.MODE_CREATE_ELLIPTIC_PARABOLOID_BY_PARABOLA.getAction(_ctrl));
    }};

    _parallelohedraActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_CUBE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HEXPRISM.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RHOMBICDODECAHEDRON.getAction(_ctrl));
      add(ModeList.MODE_CREATE_ELONGATEDDODECAHEDRON.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TRUNCATEDOCTAHEDRON.getAction(_ctrl));
      add(ModeList.MODE_CREATE_STELLAOCTAHEDRON.getAction(_ctrl));
    }};
    
    _extButtons = new EnumMap<>(Extensions.class);
    
    build();
    rebuild();
  }
  
  private void build() {
    add(new EdtToolButton(_ctrl, this, IconList.POINT.getLargeIcon(), _pointActionList));
    add(new EdtToolButton(_ctrl, this, IconList.LINE.getLargeIcon(), _lineActionList));
    add(new EdtToolButton(_ctrl, this, IconList.POLYGON.getLargeIcon(), _polygonActionList));
    add(new EdtToolButton(_ctrl, this, IconList.ANGLE.getLargeIcon(), _angleActionList));
    add(new EdtToolButton(_ctrl, this, IconList.CIRCLE.getLargeIcon(), _circleActionList));
    add(new EdtToolButton(_ctrl, this, IconList.ARC.getLargeIcon(), _arcActionList));
    
    EdtToolButton secondOrderCurvesButton = new EdtToolButton(_ctrl,
            this, IconList.ELLIPSE.getLargeIcon(), _curvesOf2ndOrderList);
    _extButtons.put(Extensions.SECOND_ORDER_CURVES, secondOrderCurvesButton);
    add(secondOrderCurvesButton);
    
    EdtToolButton secondOrderSurfacesButton = new EdtToolButton(_ctrl,
            this, IconList.ELLIPSOID.getLargeIcon(), _surfacesOf2ndOrderList);
    _extButtons.put(Extensions.SECOND_ORDER_SURFACES, secondOrderSurfacesButton);
    add(secondOrderSurfacesButton);
    
    add(new EdtToolButton(_ctrl, this, IconList.PLANE.getLargeIcon(), _planeActionList));
    
    EdtToolButton polyhedraToolButton = new EdtToolButton(_ctrl,
            this, IconList.OCTAHEDRON.getLargeIcon(), _polyhedraActionList);
    _extButtons.put(Extensions.PLATONIC_BODIES, polyhedraToolButton);
    add(polyhedraToolButton);
    
    add(new EdtToolButton(_ctrl, this, IconList.PYRAMID.getLargeIcon(), _pyramidActionList));
    add(new EdtToolButton(_ctrl, this, IconList.PRISM.getLargeIcon(), _prismActionList));
    add(new EdtToolButton(_ctrl, this, IconList.CONE.getLargeIcon(), _coneActionList));
    add(new EdtToolButton(_ctrl, this, IconList.CYLINDER.getLargeIcon(), _cylinderActionList));
    add(new EdtToolButton(_ctrl, this, IconList.SPHERE.getLargeIcon(), _sphereActionList));
    
    EdtToolButton spaceTransformButton = new EdtToolButton(_ctrl,
            this, IconList.SYMMETRY.getLargeIcon(), _transformActionList);
    _extButtons.put(Extensions.SPACE_TRANSFORM, spaceTransformButton);
    add(spaceTransformButton);
    
    add(new EdtToolButton(_ctrl, this, IconList.PNT_PROJ_ON_LINE.getLargeIcon(), _projectionActionList));
    add(new EdtToolButton(_ctrl, this, IconList.PLANE_SECTION.getLargeIcon(), _sectionActionList));
    
    EdtToolButton parallelohedraButton = new EdtToolButton(_ctrl,
            this, IconList.EMPTY.getLargeIcon(), _parallelohedraActionList);
    _extButtons.put(Extensions.PARALLELOHEDRA, parallelohedraButton);
    add(parallelohedraButton);
  }
  
  private void rebuild() {
    for( Map.Entry<Extensions, EdtToolButton> e : _extButtons.entrySet() ){
      e.getValue().setVisible(_ctrl.getExtensionMgr().getStatus(e.getKey()));
    }
  }

  @Override
  public void settingsChanged() {
    rebuild();
    revalidate();
    repaint();
  }
}