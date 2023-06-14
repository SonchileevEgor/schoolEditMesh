package gui.mode;

import editor.ExBadRef;
import gui.EdtController;
import gui.IconList;
import gui.action.EdtAction;
import gui.action.EdtModeAction;
import gui.action.i_ActionFactory;
import gui.elements.AngleStyle;
import java.awt.event.ActionEvent;

/**
 * List of mode switch actions.
 *
 * @author alexeev
 */
public enum ModeList implements i_ActionFactory {

  MODE_DEFAULT,
  MODE_COVER,
  MODE_CREATE_ANGLE,
  MODE_CREATE_ANGLE_2D,
  MODE_CREATE_ANGLE_2D_RAY,
  MODE_CREATE_CIRCLE_BY_3_POINTS,
  MODE_CREATE_CIRCLE_BY_2_POINTS_2D,
  MODE_CREATE_CONE,
  MODE_CREATE_CUBE,
  MODE_CREATE_CYLINDER,
  MODE_CREATE_LINE,
  MODE_CREATE_PLANE_BY_3_POINTS,
  MODE_CREATE_PLANE_BY_PNT,
  MODE_CREATE_PLANE_BY_POINT_AND_LINE,
  MODE_PLANE_BY_POINT_PARAL_PLANE_OR_POLY,
  MODE_PLANE_BY_LINE_PARAL_PLANE_OR_POLY,
  MODE_CREATE_PLANE_BY_LINE_ANGLE_AT_PLANE_OR_POLY,
  MODE_CREATE_PLANE_PAR_LINE_BY_POINT,
  MODE_CREATE_POLYGON,
  MODE_CREATE_POLYGON_REGULAR,
  MODE_CREATE_POLYGON_REGULAR_2D,
  MODE_SQUARE_BY_DIAGONAL_2D,
  MODE_CREATE_PRISM,
  MODE_CREATE_PRISM_BY_BASE_AND_TOP,
  MODE_CREATE_PRISM_REGULAR,
  MODE_CREATE_PRISM_HEXREGULAR,
  MODE_CREATE_PYRAMID_BY_BASE_AND_TOP,
  MODE_CREATE_PYRAMID,
  MODE_CREATE_PYRAMID_REGULAR,
  MODE_CREATE_PYRAMID_RECTANG,
  MODE_CREATE_RECTANGULAR_PARALLELEPIPED,
  MODE_CREATE_RIB,
  MODE_CREATE_RIB_BY_SKEW_LINES,
  MODE_CREATE_SPHERE,
  MODE_CREATE_SPHERE_BY_2_POINTS,
  MODE_CREATE_SPHERE_BY_4_POINTS,
  MODE_CREATE_TETRAHEDRON,
  MODE_MOVE_POINTS,
  MODE_MOVE_SCENE,
  MODE_CREATE_POINT,
  MODE_CREATE_POINT_ON_POLY_ANCHOR,
  MODE_CREATE_POINT_ON_RIB_ANCHOR,
  MODE_CREATE_POINT_ON_LINE,
  MODE_CREATE_POINT_ON_DISK,
  MODE_CREATE_POINT_ON_CIRCLE,
  MODE_CREATE_POINT_2D,
  MODE_INTERSECTION,
  MODE_PROJECT_POINT,
  MODE_ROTATE_PLANE,
  MODE_ROTATE_SCENE,
  MODE_VIEW,
  MODE_CREATE_RECTANGLE,
  MODE_CREATE_RECT_TRIANGLE,
  MODE_CREATE_RECTANGLE_2D,
  MODE_CREATE_RECT_TRIANGLE_2D,
  MODE_CREATE_PARALLELOGRAM,
  MODE_CREATE_TRAPEZE,
  MODE_CREATE_PLANE_BY_PNT_RIB,
  MODE_CREATE_RHOMBUS_BY_SIDE_ANGLE,
  MODE_CREATE_RHOMBUS_BY_SIDE_ANGLE_2D,
  MODE_CREATE_RHOMBUS_BY_DIAGONAL,
  MODE_CREATE_RHOMBUS_BY_DIAGONAL_2D,
  MODE_CREATE_LINE_OR_RIB_PROJ_ON_PLANE,
  MODE_CREATE_PLANE_ORTH_PLANE_OR_LINE,
  MODE_CREATE_PLANE_BY_SKEW_LINES,
  //MODE_CREATE_TANGENT_POINT_OF_CIRCLE_AND_LINE_OR_RIB,
  MODE_CREATE_LINE_ORTH_LINE_OR_RIB_OR_RAY,
  MODE_CREATE_LINE_PARALLEL_LINE_OR_RIB_OR_RAY,
  MODE_CREATE_TANGENT_TO_CIRCLE,
  MODE_CREATE_TANGENT_OUTER_TWO_CIRCLES,
  MODE_INSCRIBE_SPHERE,
  MODE_CIRCUMSCRIBE_SPHERE,
  MODE_CREATE_ARC,
  MODE_CREATE_ARC_ON_CIRCLE,
  MODE_CREATE_2D_MIDDLE_PERPENDICULAR,
  MODE_CREATE_INTERSECTION,
  MODE_CREATE_2D_ANGLE_BISECTRIX,
  MODE_CREATE_3D_SYM,
  MODE_CREATE_2D_SYM,
  MODE_CREATE_HOMOTHETY,
  MODE_CREATE_ROTATION,
  MODE_CREATE_2D_ROTATION,
  MODE_CREATE_INVERSION,
  MODE_CREATE_TRANSLATION,
  MODE_CREATE_2D_TRANSLATION,
  MODE_CREATE_2D_LINE_ORTH_LINE_OR_RIB_OR_RAY,
  MODE_CREATE_DODECAHEDRON,
  MODE_CREATE_TRUNCATEDOCTAHEDRON,
  MODE_CREATE_ELONGATEDDODECAHEDRON,
  MODE_CREATE_RHOMBICDODECAHEDRON,
  MODE_CREATE_ICOSAHEDRON,
  MODE_CREATE_HEXPRISM,
  MODE_CREATE_HYPERBOLE,
  MODE_CREATE_PARABOLA,
  MODE_CREATE_ELIPSE,
  MODE_CREATE_OCTAHEDRON,
  MODE_CREATE_STELLAOCTAHEDRON,
  MODE_CREATE_REGTETRAHEDRON,
  MODE_CREATE_POINT_ON_ARC,
  MODE_CREATE_HALF_CIRCLE,
  MODE_CREATE_HALF_CIRCLE_2D,
  MODE_CREATE_TANGENT_POINT_OF_SPHERE_AND_PLANE_OR_FACET,
  MODE_CREATE_TANGENT_PLANE_IN_POINT_ON_SPHERE,
  MODE_LINE_BY_POINT_ORTH_PLANE_OR_POLY,
  MODE_CREATE_TANGENT_RIB_2D,
  MODE_CIRCLE_BY_PLANE,
  MODE_CREATE_PLANE_SECTION,
  MODE_CREATE_LINE_SECTION,
  MODE_CREATE_RIB_SECTION,
  MODE_CREATE_POLY_SECTION,
  MODE_CREATE_SPHERE_X_SPHERE,
  MODE_CREATE_INSCRIBE_CIRCLE,
  MODE_CREATE_CIRCUMSCRIBE_CIRCLE,
  //MODE_CREATE_HULL_2,
  MODE_LINE_BY_POINT_PARAL_PLANE_OR_POLY,
  MODE_CREATE_BODY_PROJECTION,
  MODE_CREATE_BISECTOR_PLANE,
  MODE_CREATE_CIRCLE_BY_DIAMETER,
  MODE_CREATE_ISOSCELES_TRAPEZE,
  MODE_CREATE_CIRCLE_BY_DIAMETER_2D,
  MODE_CREATE_RAY_TWO_POINTS,
  MODE_CREATE_RIB_BY_RIB,
  MODE_CREATE_2D_RIB_BY_RIB,
  MODE_CREATE_2D_LINE_BY_POINT_ON_CIRCLE,
  MODE_CREATE_RAY_BY_POINT,
  MODE_CREATE_RIB_PROPORTIONALN_RIB,
  MODE_CREATE_RIB_BY_TWO_POINTS_AND_LENGTH,
  MODE_CREATE_HULL_2,
  MODE_CREATE_HULL_3,
  MODE_CREATE_CIRCLE_CENTER_RADIUS_2D,
  MODE_CREATE_CIRCLE_CENTER_RADIUS_NORMAL,
  MODE_CREATE_ELLIPTIC_PARABOLOID,
  MODE_CREATE_ELLIPTIC_PARABOLOID_BY_PARABOLA,
  MODE_CREATE_CONIC,
  MODE_CREATE_PAIR_OF_LINES,
  MODE_CREATE_ELLIPSOID,
  MODE_CREATE_HYPERBOLOID_OF_ONE_SHEET,
  MODE_CREATE_HYPERBOLOID_OF_TWO_SHEET,
  MODE_CIRCLE_IN_ANGLE,
  MODE_CREATE_POINT_ON_SPHERE;

  /**
   * Если нет аргументов, то режим устанавливается на главное полотно, если есть аргумент, то это -
   * полотно, на которое нужно установить режим.
   *
   * @param ctrl
   * @param args
   * @return
   */
  @Override
  public EdtAction getAction(final EdtController ctrl, final Object... args) {
    ScreenMode mode = null;
    switch (this) {
      case MODE_DEFAULT:
        return new EdtModeAction(ctrl,
                "<html><strong>Стандартный режим просмотра</strong>",
                "<html><strong>Стандартный режим просмотра</strong>",
                IconList.MOVE.getLargeIcon(), IconList.MOVE.getLargeIcon(), this) {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    ctrl.getMainCanvasCtrl().setDefaultMode();
                  }
                };
      case MODE_COVER:
        return new EdtModeAction(ctrl,
                "<html><strong>Режим просмотра моделей</strong>",
                "<html><strong>Режим просмотра моделей</strong>",
                IconList.MOVE.getLargeIcon(), IconList.MOVE.getLargeIcon(), this) {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                    ctrl.getMainCanvasCtrl().setMode(new CoverScreenMode(ctrl));
                  }
                };
      case MODE_CREATE_CIRCLE_BY_3_POINTS:
        mode = new CreateCircleByThreePointsMode(ctrl);
        break;
      case MODE_CREATE_CONE:
        mode = new CreateConeMode(ctrl);
        break;
      case MODE_CREATE_CUBE:
        mode = new CreateCubeMode(ctrl);
        break;
      case MODE_CREATE_CYLINDER:
        mode = new CreateCylinderMode(ctrl);
        break;
      case MODE_CREATE_LINE:
        mode = new CreateLineMode(ctrl);
        break;
      case MODE_CREATE_RAY_TWO_POINTS:
        mode = new CreateRayTwoPointsMode(ctrl);
        break;
      case MODE_CREATE_RAY_BY_POINT:
        mode = new CreateRayByPointMode(ctrl);
        break;
      case MODE_CREATE_PLANE_BY_PNT:
        mode = new CreatePlaneByPointMode(ctrl);
        break;
      case MODE_CREATE_PLANE_BY_3_POINTS:
        mode = new CreatePlaneMode(ctrl);
        break;
      case MODE_CREATE_PLANE_BY_POINT_AND_LINE:
        mode = new CreatePlaneByPointAndLineMode(ctrl);
        break;
      case MODE_PLANE_BY_POINT_PARAL_PLANE_OR_POLY:
        mode = new CreatePlaneByPointParallelPlaneOrPolygonMode(ctrl);
        break;
//      case MODE_PLANE_BY_LINE_PARAL_PLANE_OR_POLY:
//        mode = new CreatePlaneByLineParallelPlaneOrPolygonMode(ctrl); break;
      case MODE_CREATE_PLANE_ORTH_PLANE_OR_LINE:
        mode = new CreatePlaneOrthPlaneOrLineMode(ctrl);
        break;
      case MODE_CREATE_PLANE_BY_LINE_ANGLE_AT_PLANE_OR_POLY:
        mode = new CreatePlaneByLineAngleBetweenPlanesMode(ctrl);
        break;
      case MODE_CREATE_PLANE_PAR_LINE_BY_POINT:
        mode = new CreatePlaneParalLineByPointMode(ctrl);
        break;
      case MODE_CREATE_PLANE_BY_SKEW_LINES:
        mode = new CreatePlaneBySkewLinesMode(ctrl);
        break;
      case MODE_CREATE_POINT:
        mode = new CreatePointMode(ctrl);
        break;
      case MODE_CREATE_POINT_ON_POLY_ANCHOR:
        mode = new CreatePointOnPolyOrPlaneMode(ctrl);
        break;
      case MODE_CREATE_POLYGON:
        mode = new CreatePolygonMode(ctrl);
        break;
      case MODE_CREATE_PRISM:
        mode = new CreatePrismMode(ctrl);
        break;
      case MODE_CREATE_PYRAMID:
        mode = new CreatePyramidMode(ctrl);
        break;
      case MODE_CREATE_PYRAMID_RECTANG:
        mode = new CreatePyramidRectangMode(ctrl);
        break;
      case MODE_CREATE_RECTANGULAR_PARALLELEPIPED:
        mode = new CreateRectangularParallelepipedMode(ctrl);
        break;
      case MODE_CREATE_RIB:
        mode = new CreateRibMode(ctrl);
        break;
      case MODE_CREATE_RIB_BY_SKEW_LINES:
        mode = new CreateRibBetweenSkewLinesMode(ctrl);
        break;
      case MODE_CREATE_SPHERE:
        mode = new CreateSphereMode(ctrl);
        break;
      case MODE_CREATE_SPHERE_BY_4_POINTS:
        mode = new CreateSphereByFourPointsMode(ctrl);
        break;
      case MODE_CREATE_TETRAHEDRON:
        mode = new CreateTetrahedronMode(ctrl);
        break;
      case MODE_MOVE_POINTS:
        mode = new MovePointsScreenMode(ctrl);
        break;
      case MODE_MOVE_SCENE:
        mode = new MoveSceneMode(ctrl);
        break;
      case MODE_ROTATE_PLANE:
        try {
          mode = new RotatePlaneMode(ctrl, (String)args[0]);
        } catch (ExBadRef ex) {
        }
        break;
      case MODE_VIEW:
        mode = new ViewScreenMode(ctrl);
        break;
      case MODE_CREATE_SPHERE_BY_2_POINTS:
        mode = new CreateSphereByTwoPointsMode(ctrl);
        break;
      case MODE_CREATE_POINT_2D:
        mode = new Create2dPointMode(ctrl);
        break;
      case MODE_PROJECT_POINT:
        mode = new CreatePointProjectionMode(ctrl);
        break;
      case MODE_CREATE_LINE_OR_RIB_PROJ_ON_PLANE:
        mode = new CreateLineOrRibProjectionOnPlaneMode(ctrl);
        break;
      case MODE_CREATE_CIRCLE_BY_2_POINTS_2D:
        mode = new Create2dCircleByTwoPointsMode(ctrl);
        break;
      case MODE_CIRCLE_BY_PLANE:
        mode = new CreateCircleByPlaneMode(ctrl);
        break;
//      case MODE_CREATE_TANGENT_POINT_OF_CIRCLE_AND_LINE_OR_RIB:
//        mode = new CreateTangentPointOfCircleAndLineOrRibMode(ctrl); break;
      case MODE_CREATE_TANGENT_PLANE_IN_POINT_ON_SPHERE:
        mode = new CreateTangentPlaneInPointOnSphere(ctrl);
        break;
      case MODE_CREATE_PYRAMID_BY_BASE_AND_TOP:
        mode = new CreatePyramidByBaseAndTopMode(ctrl);
        break;
      case MODE_CREATE_PRISM_BY_BASE_AND_TOP:
        mode = new CreatePrismByBaseAndTopMode(ctrl);
        break;
      case MODE_CREATE_PYRAMID_REGULAR:
        mode = new CreateRegularPyramidMode(ctrl);
        break;
      case MODE_CREATE_PRISM_REGULAR:
        mode = new CreateRegularPrismMode(ctrl);
        break;
      case MODE_CREATE_PRISM_HEXREGULAR:
        mode = new CreateRegHexagonalPrismMode(ctrl);
        break;
      case MODE_CREATE_POLYGON_REGULAR:
        mode = new CreateRegularPolygonMode(ctrl);
        break;
      case MODE_CREATE_POLYGON_REGULAR_2D:
        mode = new Create2dRegularPolygonMode(ctrl);
        break;
      case MODE_SQUARE_BY_DIAGONAL_2D:
        mode = new Create2dSquareByDiagonalMode(ctrl);
        break;
      case MODE_CREATE_POINT_ON_RIB_ANCHOR:
        mode = new CreatePointOnRibAnchorMode(ctrl);
        break;
      case MODE_CREATE_POINT_ON_LINE:
        mode = new CreatePointOnLineMode(ctrl);
        break;
      case MODE_ROTATE_SCENE:
        mode = new RotateSceneMode(ctrl);
        break;
      case MODE_CREATE_POINT_ON_DISK:
        mode = new CreatePointOnDiskMode(ctrl);
        break;
      case MODE_CREATE_POINT_ON_CIRCLE:
        mode = new CreatePointOnCircleMode(ctrl);
        break;
      case MODE_CREATE_RECTANGLE:
        mode = new CreateRectangleMode(ctrl);
        break;
      case MODE_CREATE_RECT_TRIANGLE:
        mode = new CreateRectTriangleMode(ctrl);
        break;
      case MODE_CREATE_RECTANGLE_2D:
        mode = new Create2dRectangleMode(ctrl);
        break;
      case MODE_CREATE_RECT_TRIANGLE_2D:
        mode = new Create2dRectTriangleMode(ctrl);
        break;
      case MODE_CREATE_ANGLE:
        mode = new CreateAngleMode(ctrl, (AngleStyle)args[0]);
        break;
      case MODE_CREATE_ANGLE_2D:
        mode = new Create2dAngleByTwoPointsMode(ctrl);
        break;
      case MODE_CREATE_ANGLE_2D_RAY:
        mode = new Create2dAngleRayByTwoPointsMode(ctrl);
        break;
      case MODE_CREATE_PARALLELOGRAM:
        mode = new CreateParallelogramMode(ctrl);
        break;
      case MODE_CREATE_ISOSCELES_TRAPEZE:
        mode = new CreateIsoscelesTrapezeMode(ctrl);
        break;
      case MODE_CREATE_TRAPEZE:
        mode = new CreateTrapezeMode(ctrl);
        break;
      case MODE_CREATE_RHOMBUS_BY_SIDE_ANGLE:
        mode = new CreateRhombusBySideAngleMode(ctrl);
        break;
      case MODE_CREATE_RHOMBUS_BY_SIDE_ANGLE_2D:
        mode = new Create2dRhombusBySideAngleMode(ctrl);
        break;
      case MODE_CREATE_RHOMBUS_BY_DIAGONAL:
        mode = new CreateRhombusByDiagonalMode(ctrl);
        break;
      case MODE_CREATE_RHOMBUS_BY_DIAGONAL_2D:
        mode = new Create2dRhombusByDiagonalMode(ctrl);
        break;
      case MODE_CREATE_LINE_ORTH_LINE_OR_RIB_OR_RAY:
        mode = new CreateLineOrthogonalLineRibOrRayMode(ctrl);
        break;
      case MODE_CREATE_2D_LINE_ORTH_LINE_OR_RIB_OR_RAY:
        mode = new Create2dLineOrthLineRayRibMode(ctrl);
        break;
      case MODE_CREATE_LINE_PARALLEL_LINE_OR_RIB_OR_RAY:
        mode = new CreateLineParallelLineOrRibOrRayMode(ctrl);
        break;
      case MODE_LINE_BY_POINT_ORTH_PLANE_OR_POLY:
        mode = new CreateLineOrthPlaneMode(ctrl);
        break;
      case MODE_LINE_BY_POINT_PARAL_PLANE_OR_POLY:
        mode = new CreateLineParallelPlaneMode(ctrl);
        break;
      case MODE_INTERSECTION:
        break;
      case MODE_CREATE_TANGENT_TO_CIRCLE:
        mode = new CreateTangentLinesToCircleMode(ctrl);
        break;
      case MODE_CREATE_2D_LINE_BY_POINT_ON_CIRCLE:
        mode = new Create2dLineByPointOnCircleMode(ctrl);
        break;
      case MODE_CREATE_TANGENT_OUTER_TWO_CIRCLES:
        mode = new CreateOutTangentLinesForTwoCirclesMode(ctrl);
        break;
      case MODE_CREATE_INSCRIBE_CIRCLE:
        mode = new CreateInscribeCircleMode(ctrl);
        break;
      case MODE_CREATE_CIRCUMSCRIBE_CIRCLE:
        mode = new CreateCircumscribeCircleMode(ctrl);
        break;
      case MODE_CIRCLE_IN_ANGLE:
        mode = new CreateCircleInAngleMode(ctrl);
        break;
      case MODE_INSCRIBE_SPHERE:
        mode = new CreateInscribeSphereMode(ctrl);
        break;
      case MODE_CIRCUMSCRIBE_SPHERE:
        mode = new CreateCircumscribeSphereMode(ctrl);
        break;
      case MODE_CREATE_TANGENT_POINT_OF_SPHERE_AND_PLANE_OR_FACET:
        mode = new CreateTangentPointSphereAndFacetMode(ctrl);
        break;
      case MODE_CREATE_ARC:
        mode = new CreateArcMode(ctrl);
        break;
      case MODE_CREATE_2D_MIDDLE_PERPENDICULAR:
        mode = new Create2dMiddlePerpendicularMode(ctrl);
        break;
      case MODE_CREATE_2D_ANGLE_BISECTRIX:
        mode = new Create2dAngleBisectrixMode(ctrl);
        break;
      case MODE_CREATE_TANGENT_RIB_2D:
        mode = new Create2dTangentRibToCircleMode(ctrl);
        break;
      case MODE_CREATE_INTERSECTION:
        mode = new CreateIntersectionMode(ctrl);
        break;
      case MODE_CREATE_3D_SYM:
        mode = new CreatePlaneOrLineOrPointSymmetryMode(ctrl);
        break;
      case MODE_CREATE_2D_SYM:
        mode = new Create2dLineOrPointSymmetryMode(ctrl);
        break;
      case MODE_CREATE_HOMOTHETY:
        mode = new CreateHomothetyMode(ctrl);
        break;
      case MODE_CREATE_ROTATION:
        mode = new CreateRotationMode(ctrl);
        break;
      case MODE_CREATE_2D_ROTATION:
        mode = new Create2dRotationMode(ctrl);
        break;
      case MODE_CREATE_INVERSION:
        mode = new Create2dInversionMode(ctrl);
        break;
      case MODE_CREATE_TRANSLATION:
        mode = new CreateParallelTranslationMode(ctrl);
        break;
      case MODE_CREATE_2D_TRANSLATION:
        mode = new Create2dParTranslationMode(ctrl);
        break;
      case MODE_CREATE_DODECAHEDRON:
        mode = new CreateDodecahedronMode(ctrl);
        break;
      case MODE_CREATE_TRUNCATEDOCTAHEDRON:
        mode = new CreateTruncatedOctahedronMode(ctrl);
        break;
      case MODE_CREATE_ELONGATEDDODECAHEDRON:
        mode = new CreateElongatedDodecahedronMode(ctrl);
        break;
      case MODE_CREATE_RHOMBICDODECAHEDRON:
        mode = new CreateRhombicDodecahedronMode(ctrl);
        break;
      case MODE_CREATE_ICOSAHEDRON:
        mode = new CreateIcosahedronMode(ctrl);
        break;
      case MODE_CREATE_OCTAHEDRON:
        mode = new CreateOctahedronMode(ctrl);
        break;
      case MODE_CREATE_STELLAOCTAHEDRON:
        mode = new CreateStellaOctahedronMode(ctrl);
        break;
      case MODE_CREATE_HEXPRISM:
        mode = new CreateHexagonalPrismMode(ctrl);
        break;
      case MODE_CREATE_ELIPSE:
        mode = new CreateEllipseMainMode(ctrl);
        break;
      case MODE_CREATE_HYPERBOLE:
        mode = new CreateHyperboleMode(ctrl);
        break;
      case MODE_CREATE_PARABOLA:
        mode = new CreateParabolaMode(ctrl);
        break;
      case MODE_CREATE_REGTETRAHEDRON:
        mode = new CreateRegTetrahedronMode(ctrl);
        break;
      case MODE_CREATE_POINT_ON_ARC:
        mode = new CreatePointOnArcMode(ctrl);
        break;
      case MODE_CREATE_POINT_ON_SPHERE:
        mode = new CreatePointOnSphereMode(ctrl);
        break;
      case MODE_CREATE_ARC_ON_CIRCLE:
        mode = new CreateArcOnCircleMode(ctrl);
        break;
      case MODE_CREATE_HALF_CIRCLE:
        mode = new CreateHalfCircleMode(ctrl);
        break;
      case MODE_CREATE_HALF_CIRCLE_2D:
        mode = new Create2dHalfCircleMode(ctrl);
        break;
      case MODE_CREATE_PLANE_SECTION:
        mode = new CreatePlaneSectionMode(ctrl);
        break;
      case MODE_CREATE_LINE_SECTION:
        mode = new CreateLineSectionMode(ctrl);
        break;
      case MODE_CREATE_RIB_SECTION:
        mode = new CreateRibSectionMode(ctrl);
        break;
      case MODE_CREATE_POLY_SECTION:
        mode = new CreatePolySectionMode(ctrl);
        break;
      case MODE_CREATE_SPHERE_X_SPHERE:
        mode = new CreateSphereXSphereMode(ctrl);
        break;
      case MODE_CREATE_BODY_PROJECTION:
        mode = new CreateBodyProjectionMode(ctrl);
        break;
      case MODE_CREATE_BISECTOR_PLANE:
        mode = new CreateBisectorPlaneMode(ctrl);
        break;
      //    case MODE_CREATE_HULL_2:
//        mode = new CreateHull2Mode(ctrl); break;
      case MODE_CREATE_CIRCLE_BY_DIAMETER:
        mode = new CreateCircleByDiameterMode(ctrl);
        break;
      case MODE_CREATE_CIRCLE_BY_DIAMETER_2D:
        mode = new Create2dCircleByDiameterMode(ctrl);
        break;
      case MODE_CREATE_RIB_BY_RIB:
        mode = new CreateRibByRibMode(ctrl);
        break;
      case MODE_CREATE_RIB_PROPORTIONALN_RIB:
        mode = new CreateRibProportionalRibMode(ctrl);
        break;
      case MODE_CREATE_RIB_BY_TWO_POINTS_AND_LENGTH:
        mode = new CreateRibBy2PntsAndLengthMode(ctrl);
        break;
      case MODE_CREATE_HULL_2:
        mode = new CreateHull2Mode(ctrl);
        break;
      case MODE_CREATE_HULL_3:
        mode = new CreateHull3Mode(ctrl);
        break;
      case MODE_CREATE_CIRCLE_CENTER_RADIUS_2D:
        mode = new Create2dCircleByCenterRadiusMode(ctrl);
        break;
      case MODE_CREATE_CIRCLE_CENTER_RADIUS_NORMAL:
        mode = new CreateCircleByCenterRadiusNormalMode(ctrl);
        break;
      case MODE_CREATE_ELLIPTIC_PARABOLOID:
        mode = new CreateEllipticParaboloidMode(ctrl);
        break;
      case MODE_CREATE_ELLIPSOID:
        mode = new CreateEllipsoidMode(ctrl);
        break;
      case MODE_CREATE_HYPERBOLOID_OF_ONE_SHEET:
        mode = new CreateHyperboloidOfOneSheetMode(ctrl);
        break;
      case MODE_CREATE_HYPERBOLOID_OF_TWO_SHEET:
        mode = new CreateHyperboloidOfTwoSheetMode(ctrl);
        break;
      case MODE_CREATE_ELLIPTIC_PARABOLOID_BY_PARABOLA:
        mode = new CreateEllipticParaboloidByParabolaMode(ctrl);
        break;
      case MODE_CREATE_CONIC:
        mode = new CreateConicByFivePointsMode(ctrl);
        break;
      case MODE_CREATE_PAIR_OF_LINES:
        mode = new CreatePairOfLinesMode(ctrl);
        break;
      default:
        throw new AssertionError(this.name());
    }
    if (mode != null) {
      final ScreenMode screenMode = mode;
      return new EdtModeAction(ctrl, mode.description(), mode.description(), mode.getSmallIcon(), mode.getLargeIcon(), this) {
        @Override
        public void actionPerformed(ActionEvent e) {
          ctrl.getMainCanvasCtrl().setMode(screenMode);
        }

        @Override
        public void updateEditorState() {
          setEnabled(screenMode.isEnabled());
        }
      };
    } else {
      return new EdtAction();
    }
  }
}
