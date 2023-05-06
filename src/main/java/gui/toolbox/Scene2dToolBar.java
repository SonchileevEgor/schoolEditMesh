package gui.toolbox;

import gui.elements.AngleStyle;
import gui.EdtController;
import gui.IconList;
import gui.action.CreateDialogActionFactory;
import gui.action.EdtAction;
import gui.extensions.Extensions;
import gui.i_AppSettingsChangeListener;
import gui.mode.ModeList;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author alexeev
 */
public class Scene2dToolBar extends EdtToolBar implements i_AppSettingsChangeListener {
  private ArrayList<EdtAction> _pointActionList;
  private ArrayList<EdtAction> _lineActionList;
  private ArrayList<EdtAction> _projectionActionList;
  private ArrayList<EdtAction> _angleActionList;
  private ArrayList<EdtAction> _polygonActionList;
  private ArrayList<EdtAction> _circleActionList;
  private ArrayList<EdtAction> _arcActionList;
  private ArrayList<EdtAction> _curvesOf2ndOrderList;
  private ArrayList<EdtAction> _sectionActionList;
  private ArrayList<EdtAction> _transformActionList;
  private ArrayList<EdtAction> _functionActionList;
  
  private EnumMap<Extensions, EdtToolButton> _extButtons;
  
  public Scene2dToolBar(EdtController ctrl) {
    super(ctrl);

    _pointActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_POINT_2D.getAction(_ctrl));
      add(CreateDialogActionFactory.POINT.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HULL_2.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HULL_3.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_RIB_ANCHOR.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_LINE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_ARC.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_DISK.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POINT_ON_POLY_ANCHOR.getAction(_ctrl));
    }};

    _lineActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_LINE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RIB.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RIB_BY_RIB.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RIB_PROPORTIONALN_RIB.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RIB_BY_TWO_POINTS_AND_LENGTH.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RAY_TWO_POINTS.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RAY_BY_POINT.getAction(_ctrl));
      add(ModeList.MODE_CREATE_2D_MIDDLE_PERPENDICULAR.getAction(_ctrl));
      add(ModeList.MODE_CREATE_LINE_PARALLEL_LINE_OR_RIB_OR_RAY.getAction(_ctrl));
      add(ModeList.MODE_CREATE_2D_LINE_ORTH_LINE_OR_RIB_OR_RAY.getAction(_ctrl));
    }};

    _projectionActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_PROJECT_POINT.getAction(_ctrl));
    }};

    _angleActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_ANGLE.getAction(_ctrl, AngleStyle.SINGLE));
      add(ModeList.MODE_CREATE_ANGLE_2D.getAction(_ctrl));
      add(ModeList.MODE_CREATE_2D_ANGLE_BISECTRIX.getAction(_ctrl));
      add(ModeList.MODE_CIRCLE_IN_ANGLE.getAction(_ctrl));
    }};

    _polygonActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_POLYGON.getAction(_ctrl));
      add(ModeList.MODE_CREATE_POLYGON_REGULAR_2D.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RECTANGLE_2D.getAction(_ctrl));
      add(ModeList.MODE_SQUARE_BY_DIAGONAL_2D.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RECT_TRIANGLE_2D.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PARALLELOGRAM.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TRAPEZE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_ISOSCELES_TRAPEZE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RHOMBUS_BY_DIAGONAL_2D.getAction(_ctrl));
      add(ModeList.MODE_CREATE_RHOMBUS_BY_SIDE_ANGLE_2D.getAction(_ctrl));
    }};

    _circleActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_CIRCLE_CENTER_RADIUS_2D.getAction(_ctrl));
      add(ModeList.MODE_CREATE_CIRCLE_BY_2_POINTS_2D.getAction(_ctrl));
      add(ModeList.MODE_CREATE_CIRCLE_BY_3_POINTS.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TANGENT_TO_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TANGENT_RIB_2D.getAction(_ctrl));
      add(ModeList.MODE_CREATE_2D_LINE_BY_POINT_ON_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_TANGENT_OUTER_TWO_CIRCLES.getAction(_ctrl));
      add(ModeList.MODE_CREATE_INSCRIBE_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_CIRCUMSCRIBE_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_CIRCLE_BY_DIAMETER_2D.getAction(_ctrl));
    }};

    _curvesOf2ndOrderList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_ELIPSE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HYPERBOLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_PARABOLA.getAction(_ctrl));
      add(ModeList.MODE_CREATE_CONIC.getAction(_ctrl));
    }};

    _arcActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_ARC.getAction(_ctrl));
      add(ModeList.MODE_CREATE_ARC_ON_CIRCLE.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HALF_CIRCLE_2D.getAction(_ctrl));
    }};

    _sectionActionList = new ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_INTERSECTION.getAction(_ctrl));
    }};

    _transformActionList = new  ArrayList<EdtAction>(){{
      add(ModeList.MODE_CREATE_2D_SYM.getAction(_ctrl));
      add(ModeList.MODE_CREATE_HOMOTHETY.getAction(_ctrl));
      add(ModeList.MODE_CREATE_2D_ROTATION.getAction(_ctrl));
      add(ModeList.MODE_CREATE_2D_TRANSLATION.getAction(_ctrl));
    }};
    
    _functionActionList = new ArrayList<EdtAction>(){{
        add(CreateDialogActionFactory.ELEMENTARYFUNCTION.getAction(_ctrl));
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
    add(new EdtToolButton(_ctrl, this, IconList.CIRCLE_BY_3_POINTS.getLargeIcon(), _circleActionList));
    add(new EdtToolButton(_ctrl, this, IconList.ARC.getLargeIcon(), _arcActionList));
    
    EdtToolButton secondOrderCurvesButton = new EdtToolButton(_ctrl,
            this, IconList.ELLIPSE.getLargeIcon(), _curvesOf2ndOrderList);
    _extButtons.put(Extensions.SECOND_ORDER_CURVES, secondOrderCurvesButton);
    add(secondOrderCurvesButton);
    
    EdtToolButton spaceTransformButton = new EdtToolButton(_ctrl,
            this, IconList.SYMMETRY.getLargeIcon(), _transformActionList);
    _extButtons.put(Extensions.SPACE_TRANSFORM, spaceTransformButton);
    add(spaceTransformButton);
    
    add(new EdtToolButton(_ctrl, this, IconList.PNT_PROJ_ON_LINE.getLargeIcon(), _projectionActionList));
    add(new EdtToolButton(_ctrl, this, IconList.CURVE_INTERSECT.getLargeIcon(), _sectionActionList));
    add(new EdtToolButton(_ctrl, this, IconList.FUNCTION.getLargeIcon(), _functionActionList));
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
