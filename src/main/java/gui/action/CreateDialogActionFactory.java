package gui.action;

import bodies.BodyType;
import editor.AnchorType;
import editor.ExNoBody;
import gui.EdtController;
import gui.IconList;
import gui.dialog.create.*;
import java.awt.event.ActionEvent;

/**
 * List of dialog show actions.
 *
 * @author alexeev
 */
public enum CreateDialogActionFactory implements i_ActionFactory {
  CIRCUMSCRIBE_SPHERE,
  CIRCUMSCRIBE_CIRCLE,
  CIRCLE_BY_3_POINTS,
  CIRCLE_BY_PLANE,
  CONE,
  CYLINDER,
  ESCRIBE_CIRCLE,
  HULL_2,
  HULL_3,
  INTERSECTION,
  PLANE_BY_POINT_AND_NORMAL,
  PLANE_BY_3_POINTS,
  POINT,
  ELEMENTARYFUNCTION,
  POLYGON,
  PRISM_BY_BASE_AND_VERTEX,
  PRISM,
  PYRAMID_BY_BASE_AND_VERTEX,
  PYRAMID,
  REGULAR_POLYGON,
  REGULAR_PRISM,
  REGULAR_PYRAMID,
  RIB,
  SPHERE_BY_4_POINTS,
  SPHERE,
  TETRAHEDRON,
  DISSECT_RIB,
  INSCRIBE_CIRCLE,
  INSCRIBE_SPHERE,
  PROJECT_POINT_ON_LINE,
  PROJECT_POINT_ON_PLANE,
  PLANE_SECTION,
  MEDIAN,
  HEIGHT,
  BISECTRIX,
  MIDDLE_PERPENDICULAR,
  LINE,
  OUT_TANGENT_FOR_TWO_CIRCLES,
  TANGENT_FROM_POINT_TO_CIRCLE,
  CIRCLE_X_CIRCLE,
  SPHERE_X_SPHERE,
  LINE_PARALLEL_LINE,
  LINE_PARALLEL_PLANE,
  LINE_ORTHOGONAL_LINE,
  LINE_ORTHOGONAL_PLANE,
  LINE_SECTION,
  RIB_SECTION,
  POLY_SECTION,
  PLANE_PROJECTION
  ;

  @Override
  public EdtAction getAction(final EdtController ctrl, final Object... args) {
    switch(this) {
      case BISECTRIX:
        return new EdtAction(ctrl,
        "Провести биссектрису",
        "Провести биссектрису",
        IconList.BISECTRIX.getMediumIcon(),
        IconList.BISECTRIX.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              showCreateDialog(ctrl, new CreateBisectrixDialog(ctrl, ctrl.error().getStatusStripHandler(), (String)args[0])); // args[0] is body ID
            } catch (ExNoBody ex) {}
          }
          @Override
          public void updateEditorState() {
            try {
              setEnabled(ctrl.getBody((String)args[0]).exists());
            } catch (ExNoBody ex) {}
          }
        };
      case CIRCLE_BY_3_POINTS:
        return new EdtAction(ctrl,
        "<html><strong>Окружность</strong><br>по трём точкам",
        "<html><strong>Окружность</strong><br>по трём точкам",
        IconList.CIRCLE_BY_3_POINTS.getLargeIcon(),
        IconList.CIRCLE_BY_3_POINTS.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            showCreateDialog(ctrl, new CreateCircleByThreePointsDialog(ctrl, ctrl.error().getStatusStripHandler()));
          }
          @Override
          public void updateEditorState() {
            setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 3));
          }
        };
       case CIRCLE_BY_PLANE:
        return new EdtAction(ctrl,
        "<html><strong>Окружность</strong><br>по плоскости, центру и точке",
        "<html><strong>Окружность</strong><br>по плоскости, центру и точке",
        IconList.CIRCLE_BY_PLANE.getLargeIcon(),
        IconList.CIRCLE_BY_PLANE.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            showCreateDialog(ctrl, new CreateCircleByPlaneDialog(ctrl, ctrl.error().getStatusStripHandler()));
          }
          @Override
          public void updateEditorState() {//plane!
            setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 2) ||
                       ctrl.containsBodies(BodyType.PLANE, 1));
          }
        };
      case CIRCUMSCRIBE_CIRCLE:
        return new EdtAction(ctrl,
          "<html><strong>Описать окружность</strong><br>около многоугольника",
          "<html><strong>Описать окружность</strong><br>около многоугольника",
          IconList.OUT_CIRCLE.getLargeIcon(),
          IconList.OUT_CIRCLE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CircumscribeCircleDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.POLYGON, 1) ||
                         ctrl.containsBodies(BodyType.TRIANGLE, 1) );
            }
          };
      case CIRCUMSCRIBE_SPHERE:
        return new EdtAction(ctrl,
        "Описать сферу",
        "Описать сферу",
        IconList.OUT_SPHERE.getLargeIcon(),
        IconList.OUT_SPHERE.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            showCreateDialog(ctrl, new CircumscribeSphereDialog(ctrl, ctrl.error().getStatusStripHandler()));
          }
          @Override
          public void updateEditorState() {
            setEnabled(ctrl.containsBodies(BodyType.CONE, 1)     ||
                       ctrl.containsBodies(BodyType.CUBE, 1)     ||
                       ctrl.containsBodies(BodyType.CYLINDER, 1) ||
                       ctrl.containsBodies(BodyType.PRISM, 1)    ||
                       ctrl.containsBodies(BodyType.PYRAMID, 1)  ||
                       ctrl.containsBodies(BodyType.TETRAHEDRON, 1));
          }
        };
      case CONE:
        return new EdtAction(ctrl,
          "<html><strong>Конус</strong><br>по вершине, центру и радиусу основания",
          "<html><strong>Конус</strong><br>по вершине, центру и радиусу основания",
          IconList.CONE.getLargeIcon(),
          IconList.CONE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateConeDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 2));
            }
          };
      case CYLINDER:
        return new EdtAction(ctrl,
          "<html><strong>Цилиндр</strong><br>по центрам оснований и радиусу",
          "<html><strong>Цилиндр</strong><br>по центрам оснований и радиусу",
          IconList.CYLINDER.getLargeIcon(),
          IconList.CYLINDER.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateCylinderDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 2));
            }
          };
      case DISSECT_RIB:
        return new EdtAction(ctrl,
          "Отметить середину отрезка",
          "Отметить середину отрезка",
          IconList.DISSECT_RIB.getMediumIcon(),
          IconList.DISSECT_RIB.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new DissectRibDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.RIB, 1));
            }
          };
      case ESCRIBE_CIRCLE:
        return new EdtAction(ctrl,
        "Построить вневписанную окружность",
        "Построить вневписанную окружность",
        IconList.CIRCLE_EX_TRIANGLE.getMediumIcon(),
        IconList.CIRCLE_EX_TRIANGLE.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              showCreateDialog(ctrl, new EscribeCircleDialog(ctrl, ctrl.error().getStatusStripHandler(), (String)args[0])); // args[0] is body ID
            } catch (ExNoBody ex) {}
          }
          @Override
          public void updateEditorState() {
            try {
              setEnabled(ctrl.getBody((String)args[0]).exists());
            } catch (ExNoBody ex) {
              setEnabled(false);
            }
          }
        };
      case HEIGHT:
        return new EdtAction(ctrl,
        "Провести высоту",
        "Провести высоту",
        IconList.HEIGHT.getMediumIcon(),
        IconList.HEIGHT.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              showCreateDialog(ctrl, new CreateHeightDialog(ctrl, ctrl.error().getStatusStripHandler(), (String)args[0])); // args[0] is body ID
            } catch (ExNoBody ex) {}
          }
          @Override
          public void updateEditorState() {
            try {
              setEnabled(ctrl.getBody((String)args[0]).exists());
            } catch (ExNoBody ex) {
              setEnabled(false);
            }
          }
        };
      case HULL_2:
        return new EdtAction(ctrl,
          "<html><strong>Линейная комбинация</strong><br>двух точек",
          "<html><strong>Линейная комбинация</strong><br>двух точек",
          IconList.HULL_2.getLargeIcon(),
          IconList.HULL_2.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateHull2Dialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 2));
            }
          };
      case HULL_3:
        return new EdtAction(ctrl,
          "<html><strong>Линейная комбинация</strong><br>трёх точек",
          "<html><strong>Линейная комбинация</strong><br>трёх точек",
          IconList.HULL_3.getLargeIcon(),
          IconList.HULL_3.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateHull3Dialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 3));
            }
          };
      case INSCRIBE_CIRCLE:
        return new EdtAction(ctrl,
          "<html><strong>Вписать окружность</strong><br>в многоугольник",
          "<html><strong>Вписать окружность</strong><br>в многоугольник",
          IconList.IN_CIRCLE.getLargeIcon(),
          IconList.IN_CIRCLE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new InscribeCircleDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.POLYGON, 1) ||
                         ctrl.containsBodies(BodyType.TRIANGLE, 1));
            }
          };
      case INSCRIBE_SPHERE:
        return new EdtAction(ctrl,
        "Вписать сферу",
        "Вписать сферу",
        IconList.IN_SPHERE.getLargeIcon(),
        IconList.IN_SPHERE.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            showCreateDialog(ctrl, new InscribeSphereDialog(ctrl, ctrl.error().getStatusStripHandler()));
          }
          @Override
          public void updateEditorState() {
            setEnabled(ctrl.containsBodies(BodyType.CONE, 1)     ||
                       ctrl.containsBodies(BodyType.CUBE, 1)     ||
                       ctrl.containsBodies(BodyType.CYLINDER, 1) ||
                       ctrl.containsBodies(BodyType.PRISM, 1)    ||
                       ctrl.containsBodies(BodyType.PYRAMID, 1)  ||
                       ctrl.containsBodies(BodyType.TETRAHEDRON, 1));
          }
        };
      case LINE:
        return new EdtAction(ctrl,
          "<html><strong>Прямая</strong>",
          "<html><strong>Прямая</strong>",
          IconList.LINE.getLargeIcon(),
          IconList.LINE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateLineByTwoPointDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 2));
            }
          };
      case MEDIAN:
        return new EdtAction(ctrl,
        "Провести медиану",
        "Провести медиану",
        IconList.MEDIAN.getMediumIcon(),
        IconList.MEDIAN.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              showCreateDialog(ctrl, new CreateMedianDialog(ctrl, ctrl.error().getStatusStripHandler(), (String)args[0])); // args[0] is body ID
            } catch (ExNoBody ex) {}
          }
          @Override
          public void updateEditorState() {
            try {
              setEnabled(ctrl.getBody((String)args[0]).exists());
            } catch (ExNoBody ex) {
              setEnabled(false);
            }
          }
        };
      case MIDDLE_PERPENDICULAR:
        return new EdtAction(ctrl,
        "Провести срединный перпендикуляр",
        "Провести срединный перпендикуляр",
        IconList.MIDDLE_PERPENDICULAR.getMediumIcon(),
        IconList.MIDDLE_PERPENDICULAR.getLargeIcon()){
          @Override
          public void actionPerformed(ActionEvent e) {
            try {
              showCreateDialog(ctrl, new CreateMiddlePerpendicularDialog(
                      ctrl, ctrl.error().getStatusStripHandler(), (String)args[0])); // args[0] is body ID
            } catch (ExNoBody ex) {}
          }
          @Override
          public void updateEditorState() {
            try {
              setEnabled(ctrl.getBody((String)args[0]).exists());
            } catch (ExNoBody ex) {
              setEnabled(false);
            }
          }
        };
      case PLANE_BY_3_POINTS:
        return new EdtAction(ctrl,
          "<html><strong>Плоскость</strong><br>по трём точкам",
          "<html><strong>Плоскость</strong><br>по трём точкам",
          IconList.PLANE_BY_3_POINTS.getLargeIcon(),
          IconList.PLANE_BY_3_POINTS.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreatePlaneByThreePointsDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 3));
            }
          };
      case PLANE_BY_POINT_AND_NORMAL:
        return new EdtAction(ctrl,
          "<html><strong>Плоскость</strong><br>по точке и вектору нормали",
          "<html><strong>Плоскость</strong><br>по точке и вектору нормали",
          IconList.PLANE_BY_PNT.getLargeIcon(),
          IconList.PLANE_BY_PNT.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreatePlaneByPointAndNormalDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 1));
            }
          };
      case POINT:
        return new EdtAction(ctrl,
          "<html><strong>Точка</strong><br>по набору координат",
          "<html><strong>Точка</strong><br>по набору координат",
          IconList.PNT_COOR.getLargeIcon(),
          IconList.PNT_COOR.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreatePointDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
          };
          
      case ELEMENTARYFUNCTION:
        return new EdtAction(ctrl,
          "<html><strong>Функция</strong><br>через задание формулой",
          "<html><strong>Функция</strong><br>через задание формулой",
          IconList.ELEMENTARYFUNCTION.getLargeIcon(),
          IconList.ELEMENTARYFUNCTION.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateElementaryFunctionDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
          };
      case POLYGON:
        return new EdtAction(ctrl,
          "<html><strong>Многоугольник</strong><br>по набору вершин",
          "<html><strong>Многоугольник</strong><br>по набору вершин",
          IconList.POLYGON.getLargeIcon(),
          IconList.POLYGON.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreatePolygonDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 3));
            }
          };
      case PRISM:
        return new EdtAction(ctrl,
          "<html><strong>Призма</strong><br>по точкам основания и вершине параллельного основания",
          "<html><strong>Призма</strong><br>по точкам основания и вершине параллельного основания",
          IconList.PRISM.getLargeIcon(),
          IconList.PRISM.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreatePrismDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 4));
            }
          };
      case PRISM_BY_BASE_AND_VERTEX:
        return new EdtAction(ctrl,
          "<html><strong>Призма</strong><br>по основанию и вершине параллельного основания",
          "<html><strong>Призма</strong><br>по основанию и вершине параллельного основания",
          IconList.PRISM_BY_BASE_AND_TOP.getLargeIcon(),
          IconList.PRISM_BY_BASE_AND_TOP.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreatePrismByBaseAndVertexDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 1) &&
                         ctrl.containsBodies(BodyType.POLYGON, 1));
            }
          };
      case PROJECT_POINT_ON_LINE:
        return new EdtAction(ctrl,
          "Перпендикуляр из точки на прямую",
          "Перпендикуляр из точки на прямую",
          IconList.PNT_PROJ_ON_LINE.getLargeIcon(),
          IconList.PNT_PROJ_ON_LINE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new ProjectPointOnLineDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 1) &&
                        (ctrl.containsBodies(BodyType.LINE, 1) || ctrl.containsBodies(BodyType.RIB, 1)));
            }
          };
      case PROJECT_POINT_ON_PLANE:
        return new EdtAction(ctrl,
          "Перпендикуляр из точки на плоскость",
          "Перпендикуляр из точки на плоскость",
          IconList.PNT_PROJ_ON_PLANE.getLargeIcon(),
          IconList.PNT_PROJ_ON_PLANE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new ProjectPointOnPlaneDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 1) &&
                         ctrl.containsBodies(BodyType.PLANE, 1));
            }
          };
      case PYRAMID:
        return new EdtAction(ctrl,
          "<html><strong>Пирамида</strong><br>по точкам основания и вершине",
          "<html><strong>Пирамида</strong><br>по точкам основания и вершине",
          IconList.PYRAMID.getLargeIcon(),
          IconList.PYRAMID.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreatePyramidDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 4));
            }
          };
      case PYRAMID_BY_BASE_AND_VERTEX:
        return new EdtAction(ctrl,
          "<html><strong>Призма</strong><br>по основанию и вершине",
          "<html><strong>Призма</strong><br>по основанию и вершине",
          IconList.PYRAMID_BY_BASE_AND_TOP.getLargeIcon(),
          IconList.PYRAMID_BY_BASE_AND_TOP.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreatePyramidByBaseAndVertexDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 1) &&
                         ctrl.containsBodies(BodyType.POLYGON, 1));
            }
          };
      case REGULAR_POLYGON:
        return new EdtAction(ctrl,
          "<html><strong>Правильный многоугольник</strong><br>по двум вершинам и плоскости",
          "<html><strong>Правильный многоугольник</strong><br>по двум вершинам и плоскости",
          IconList.REGULAR_POLYGON.getLargeIcon(),
          IconList.REGULAR_POLYGON.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateRegularPolygonDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 2) &&
                         ctrl.containsBodies(BodyType.PLANE, 1));
            }
          };
      case REGULAR_PRISM:
        return new EdtAction(ctrl,
          "<html><strong>Правильная призма</strong>",
          "<html><strong>Правильная призма</strong>",
          IconList.REGULAR_PRISM.getLargeIcon(),
          IconList.REGULAR_PRISM.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateRegularPrismDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 3) &&
                         ctrl.containsBodies(BodyType.PLANE, 1));
            }
          };
      case REGULAR_PYRAMID:
        return new EdtAction(ctrl,
          "<html><strong>Правильная пирамида</strong>",
          "<html><strong>Правильная пирамида</strong>",
          IconList.REGULAR_PYRAMID.getLargeIcon(),
          IconList.REGULAR_PYRAMID.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateRegularPyramidDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 3) &&
                         ctrl.containsBodies(BodyType.PLANE, 1));
            }
          };
      case RIB:
        return new EdtAction(ctrl,
          "Отрезок",
          "Отрезок",
          IconList.RIB.getLargeIcon(),
          IconList.RIB.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateRibDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 2));
            }
          };
      case PLANE_SECTION:
        return new EdtAction(ctrl,
          "<html><strong>Сечение</strong><br>тела плоскостью",
          "<html><strong>Сечение</strong><br>тела плоскостью",
          IconList.PLANE_SECTION.getLargeIcon(),
          IconList.PLANE_SECTION.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new PlaneSectionDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.PLANE, 1));
            }
          };
      case SPHERE:
        return new EdtAction(ctrl,
          "<html><strong>Сфера</strong><br>по точке и радиусу",
          "<html><strong>Сфера</strong><br>по точке и радиусу",
          IconList.SPHERE.getLargeIcon(),
          IconList.SPHERE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateSphereDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 1));
            }
          };
      case SPHERE_BY_4_POINTS:
        return new EdtAction(ctrl,
          "<html><strong>Сфера</strong><br>по четырём точкам",
          "<html><strong>Сфера</strong><br>по четырём точкам",
          IconList.SPHERE_BY_4_POINTS.getLargeIcon(),
          IconList.SPHERE_BY_4_POINTS.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateSphereByFourPointsDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 4));
            }
          };
      case TETRAHEDRON:
        return new EdtAction(ctrl,
          "<html><strong>Тетраэдр</strong><br>по четырём вершинам",
          "<html><strong>Тетраэдр</strong><br>по четырём вершинам",
          IconList.TETRAHEDRON.getLargeIcon(),
          IconList.TETRAHEDRON.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateTetrahedronDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 4));
            }
          };
      case TANGENT_FROM_POINT_TO_CIRCLE:
        return new EdtAction(ctrl,
          "<html><strong>Касательные</strong><br>из точки к окружности",
          "<html><strong>Касательные</strong><br>из точки к окружности",
          IconList.TANGENT1.getLargeIcon(),
          IconList.TANGENT1.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new TangentToCircleCreateDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POINT, 1) &&
                         ctrl.containsBodies(BodyType.CIRCLE, 1));
            }
          };
      case OUT_TANGENT_FOR_TWO_CIRCLES:
        return new EdtAction(ctrl,
          "<html><strong>Внешние касательные</strong><br>к двум окружностям",
          "<html><strong>Внешние касательные</strong><br>к двум окружностям",
          IconList.TANGENT2.getLargeIcon(),
          IconList.TANGENT2.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new OutTangentForTwoCirclesCreateDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.CIRCLE, 2));
            }
          };
      case CIRCLE_X_CIRCLE:
        return new EdtAction(ctrl,
          "Пересечение двух окружностей",
          "Пересечение двух окружностей",
          IconList.CIRCLE_BY_3_POINTS.getLargeIcon(),
          IconList.CIRCLE_BY_3_POINTS.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CircleXCircleCreateDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.CIRCLE, 2));
            }
          };
      case LINE_PARALLEL_LINE:
        return new EdtAction(ctrl,
          "<html><strong>Прямая</strong>,<br>проходящая через точку параллельно другой прямой",
          "<html><strong>Ппямая</strong>,<br>проходящая через точку параллельно другой прямой",
          IconList.LINE_PARAL_LINE.getLargeIcon(),
          IconList.LINE_PARAL_LINE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateLineParallelLineDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.LINE, 1) &&
                         ctrl.containsAnchors(AnchorType.ANC_POINT, 1));
            }
          };
      case LINE_PARALLEL_PLANE:
        return new EdtAction(ctrl,
          "<html><strong>Прямая</strong>,<br>проходящая через точку параллельно плоскости",
          "<html><strong>Прямая</strong>,<br>проходящая через точку параллельно плоскости",
          IconList.LINE_PARAL_PLANE.getLargeIcon(),
          IconList.LINE_PARAL_PLANE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateLineParallelPlaneDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.PLANE, 1) &&
                         ctrl.containsAnchors(AnchorType.ANC_POINT, 1));
            }
          };
      case LINE_ORTHOGONAL_LINE:
        return new EdtAction(ctrl,
          "<html><strong>Прямая</strong>,<br>проходящая через точку перпендикулярно другой прямой",
          "<html><strong>Прямая</strong>,<br>проходящая через точку перпендикулярно другой прямой",
          IconList.LINE_ORT_LINE.getLargeIcon(),
          IconList.LINE_ORT_LINE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateLineOrthogonalLineDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.LINE, 1) &&
                         ctrl.containsAnchors(AnchorType.ANC_POINT, 1));
            }
          };
      case LINE_ORTHOGONAL_PLANE:
        return new EdtAction(ctrl,
          "<html><strong>Прямая</strong>,<br>проходящая через точку перпендикулярно плоскости",
          "<html><strong>Прямая</strong>,<br>проходящая через точку перпендикулярно плоскости",
          IconList.LINE_ORT_PLANE.getLargeIcon(),
          IconList.LINE_ORT_PLANE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new CreateLineOrthogonalPlaneDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.PLANE, 1) &&
                         ctrl.containsAnchors(AnchorType.ANC_POINT, 1));
            }
          };
      case LINE_SECTION://!! TODO
        return new EdtAction(ctrl,
          "<html><strong>Пересечение</strong><br>тела с прямой",
          "<html><strong>Пересечение</strong><br>тела с прямой",
          IconList.LINE.getLargeIcon(),
          IconList.LINE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new LineSectionDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.LINE, 1) &&
                          (ctrl.containsBodies(BodyType.CYLINDER, 1) ||
                           ctrl.containsBodies(BodyType.LINE, 1)     ||
                           ctrl.containsBodies(BodyType.PLANE, 1)    ||
                           ctrl.containsBodies(BodyType.RIB, 1)));
            }
          };
      case PLANE_PROJECTION:
        return new EdtAction(ctrl,
          "<html><strong>Проекция</strong><br>тела на плоскость",
          "<html><strong>Проекция</strong><br>тела на плоскость",
          IconList.SPH_PROJ_ON_PLANE.getLargeIcon(),
          IconList.SPH_PROJ_ON_PLANE.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new PlaneProjectionDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.PLANE, 1) &&
                         ctrl.containsBodies(BodyType.SPHERE, 1));
            }
          };
      case INTERSECTION:
        return new EdtAction(ctrl,
          "<html><strong>Пересечение двух тел</strong>",
          "<html><strong>Пересечение двух тел</strong>",
          IconList.BODY_INTERSECT.getLargeIcon(),
          IconList.BODY_INTERSECT.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new IntersectionDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.PLANE, 1)    ||
                         ctrl.containsBodies(BodyType.RIB, 1) ||
                         ctrl.containsBodies(BodyType.CONE, 1) ||
                         ctrl.containsBodies(BodyType.CUBE, 1) ||
                         ctrl.containsBodies(BodyType.CYLINDER, 1) ||
                         ctrl.containsBodies(BodyType.LINE, 1) ||
                         ctrl.containsBodies(BodyType.PRISM, 1) ||
                         ctrl.containsBodies(BodyType.PYRAMID, 1) ||
                         ctrl.containsBodies(BodyType.SPHERE, 1) ||
                         ctrl.containsBodies(BodyType.TETRAHEDRON, 1));
            }
          };
      case SPHERE_X_SPHERE:
        return new EdtAction(ctrl,
          "<html><strong>Пересечение</strong><br>двух сфер",
          "<html><strong>Пересечение</strong><br>двух сфер",
          IconList.BODY_INTERSECT.getLargeIcon(),
          IconList.BODY_INTERSECT.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new SphereXSphereDialog(ctrl, ctrl.error().getStatusStripHandler()));
              }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsBodies(BodyType.SPHERE, 2));
            }
          };
      case RIB_SECTION:
        return new EdtAction(ctrl,
          "<html><strong>Пересечение</strong><br>отрезка с объектом на сцене",
          "<html><strong>Пересечение</strong><br>отрезка с объектом на сцене",
          IconList.CURVE_INTERSECT.getLargeIcon(),
          IconList.CURVE_INTERSECT.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new RibSectionDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_RIB, 1));
            }
          };
      case POLY_SECTION:
        return new EdtAction(ctrl,
          "<html><strong>Пересечение</strong><br>грани с объектом на сцене",
          "<html><strong>Пересечение</strong><br>грани с объектом на сцене",
          IconList.CURVE_INTERSECT.getLargeIcon(),
          IconList.CURVE_INTERSECT.getLargeIcon()){
            @Override
            public void actionPerformed(ActionEvent e) {
              showCreateDialog(ctrl, new PolySectionDialog(ctrl, ctrl.error().getStatusStripHandler()));
            }
            @Override
            public void updateEditorState() {
              setEnabled(ctrl.containsAnchors(AnchorType.ANC_POLY, 1));
            }
          };
      default:
        throw new AssertionError(this.name());
    }
  }

  /**
   * Shows and handle input value of a create body dialog.
   * @param ctrl
   * @param dialog
   */
  public static void showCreateDialog(final EdtController ctrl, final CreateBodyDialog dialog) {
    dialog.setVisible(true);
    if (dialog.accepted)
      ctrl.redraw();
  }
}
