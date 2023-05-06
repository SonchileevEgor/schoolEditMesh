package opengl.scenegl;

import bodies.BodyType;
import static config.Config.STIPPLE_LINE_WIDTH;
import editor.Editor;
import editor.i_Anchor;
import java.util.List;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import opengl.Drawer;
import opengl.DrawingPolygon;
import opengl.SwitcherStateGL;
import opengl.TypeFigure;
import opengl.drawing.DrawingQueue;
import opengl.sceneparameters.CameraPosition;
import opengl.sceneparameters.ViewMode;

/**
 * Сцена для редактирования модели во внутреннем формате программы.
 * @author alexeev
 */
public abstract class SceneEdt extends SceneGL {

  public SceneEdt(Editor edt) {
    super(edt);
  }

  public SceneEdt(Editor edt, CameraPosition cameraPosition) {
    super(edt, cameraPosition);
  }

  public Editor getEditor() {
    return render.getEditor();
  }
  /**
   * Draw DrawingPolygons from list using saved parameters in DrawingPolygons.
   *
   * @param polygonsOnOnePlane DrawingPolygons for drawing.
   */
  public void drawOverlappedPolygons(List<DrawingPolygon> polygonsOnOnePlane) {
    for (DrawingPolygon polygon : polygonsOnOnePlane) {
      Drawer.setTransformMatrixForOpenGL(polygon.transformMatrix, render);
      Drawer.setObjectColor(render, polygon.color);
      Drawer.drawPolygon(render, polygon.points, TypeFigure.SOLID);
    }
  }

  /**
   * Draw all objects, using internal parameters
   */
  @Override
  public void drawObjects() {
    GL2 gl = render.getGL();
    switch (render.getViewMode()) {
      case LIGHT:
        gl.glEnable(GL2.GL_LIGHTING);
        break;
      case DOTTED_LINE:
      case PENCIL:
        gl.glDisable(GL2.GL_LIGHTING);
        break;
    }

    /**
     * Рисуем всю сцену прозрачным цветом (не рисуя), чтобы PlaneManager обнаружил полигоны, находящиеся в одной плоскости.
     */
    {
      gl.glEnable(GL2.GL_BLEND);
      gl.glDisable(GL2.GL_POLYGON_SMOOTH);
      gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE);

      Drawer.setPlaneManagerEnabled(true);
      Drawer.setPlaneManagerReadOnlyEnabled(false);
      if (render.getViewMode() != ViewMode.FACES_TRANSPARENT) {
        drawFaces();
      }
      // Переводим PlaneManager в режим "только для чтения",
      // чтобы далее перекрытые полигоны не рисовались в общей куче,
      // а рисовались отдельно.
      Drawer.setPlaneManagerReadOnlyEnabled(true);
      // Очищаем z-буфер, чтобы это не повлияло да дальнейшую работу.
      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    }

    if (render.isUseAntialiasing()) {
      gl.glEnable(GL2.GL_LINE_SMOOTH);
      gl.glEnable(GL2.GL_POLYGON_SMOOTH);
      gl.glEnable(GL2.GL_BLEND);
    } else {
      gl.glDisable(GL2.GL_LINE_SMOOTH);
      gl.glDisable(GL2.GL_POLYGON_SMOOTH);
      gl.glDisable(GL2.GL_BLEND);
    }

    gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ZERO);

    // Рисуем дополнительные элементы
    DrawingQueue.drawLowPriority(render);

    if (render.getViewMode() != ViewMode.FACES_TRANSPARENT) {
      // Рисуем грани кроме тех, что захватил PlaneManager
      drawFaces();

      // Выключаем PlaneManager, иначе перекрытые полигоны не будут рисоваться
      Drawer.setPlaneManagerEnabled(false);
      gl.glEnable(GL2.GL_BLEND);

      gl.glPushMatrix();
      for (List<DrawingPolygon> polygonsOnOnePlane : Drawer.getOverlappedPolygons()) {
        /**
         * Рисуем полигоны, находящиеся в одной плоскости в порядке их появления без записи в z-буфер, а затем
         * рисуем их прозрачным цветом, чтобы заполнить z-буфер.
         */
        gl.glDepthMask(false);
        gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ZERO);
        drawOverlappedPolygons(polygonsOnOnePlane);
        gl.glDepthMask(true);
        gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE);
        drawOverlappedPolygons(polygonsOnOnePlane);
      }
      gl.glPopMatrix();
    }
    gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ZERO);

    /**
     * Блок реализации прозрачности фигур, а именно получение эффекта изменения цвета части фигуры, если за ней
     * располагается другая фигура.
     * На данный момент цвет фигуры заднего плана не учитывается, а просто берется цвет фигуры переднего плана и
     * делается немного тусклее (ближе к черному).
     * Реализуется путем отрисовки всех граней на сцене с использованием обратной функции глубины (рисуем только те
     * полигоны, которые заслонены) и цветового смешения путем вычитания серого цвета из исходного цвета объекта.
     */
    {
      SwitcherStateGL.saveAndDisable(render, GL2.GL_LIGHTING); // Не учитываем освещенность загороженных объектов
      SwitcherStateGL.saveAndEnable(render, GL2.GL_BLEND);
      // На некоторых машинах из-за сглаживания краев полигонов появляется светлые линии на их стыках.
      SwitcherStateGL.saveAndDisable(render, GL2.GL_POLYGON_SMOOTH);
      // Рисуем только те полигоны, которые заслонены другими
      SwitcherStateGL.saveAndChangeDepthFunc(render, GL2.GL_GREATER);
      // Берем цвета как есть, параметр альфа не учитываем
      SwitcherStateGL.saveAndChangeBlendFunc(render, GL2.GL_ONE, GL2.GL_ONE);
      gl.glDepthMask(false); // На всякий случай делаем z-буффер только для чтения, чтобы не записывать туда
      // В теории должен быть GL_FUNC_SUBTRACT, но правильно работает GL_FUNC_REVERSE_SUBTRACT
      if (isBlendEquationAvailable) {
        gl.glBlendEquation(GL2.GL_FUNC_REVERSE_SUBTRACT);
      }
      // Отключаем рисование задних граней, чтобы объемные фигуры не смешивались дважды
//    SwitcherStateGL.saveAndEnable(render, GL2.GL_CULL_FACE);
//    gl.glCullFace(GL2.GL_FRONT);
      // Устанавливаем цвет всех фигур серым, чтобы исходный цвет просто становился тусклее
      Drawer.setAllObjectsForcedColor(true);
      Drawer.setPlaneManagerEnabled(true);
      Drawer.setPlaneManagerReadOnlyEnabled(true);

      if (render.getViewMode() != ViewMode.FACES_TRANSPARENT) {
        drawFaces();

        Drawer.setPlaneManagerEnabled(false);
        /**
         * К сожалению, проблема погрешности z-буфера появляется снова, т к полигоны одной плоскости частично
         * оказываются загорожены друг другом (от погрешностей) и рисуется рябь от заднего полигона.
         * По этой причине решил, что пусть такие полигоны будут рисоваться с другой функцией глубины,
         * чтобы они загораживали сами себя. Побочным эффектом будет то, что они будут темнее без видимой на то причины,
         * зато ряби нет.
         */
        gl.glDepthFunc(GL2.GL_GEQUAL);
        gl.glPushMatrix();
        for (List<DrawingPolygon> polygonsOnOnePlane : Drawer.getOverlappedPolygons()) {
          drawOverlappedPolygons(polygonsOnOnePlane);
        }
        gl.glPopMatrix();

      }

      SwitcherStateGL.restore(render, GL2.GL_LIGHTING);
      SwitcherStateGL.restore(render, GL2.GL_BLEND);
      SwitcherStateGL.restore(render, GL2.GL_POLYGON_SMOOTH);
      SwitcherStateGL.restoreDepthFunc(render);
      SwitcherStateGL.restoreBlendFunc(render);
      gl.glDepthMask(true);
      if (isBlendEquationAvailable) {
        gl.glBlendEquation(GL2.GL_FUNC_ADD);
      }
//    SwitcherStateGL.restore(render, GL2.GL_CULL_FACE);
      Drawer.setAllObjectsForcedColor(false);
      Drawer.setPlaneManagerEnabled(true);
    }

    if (isUseAntialiasing()){
      gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    }

    gl.glDepthFunc(GL2.GL_LEQUAL);
    drawCarcasses();

    gl.glDepthFunc(GL2.GL_GREATER);
    gl.glDepthMask(false);
    gl.glEnable(GL2.GL_LINE_STIPPLE);
    gl.glLineStipple(1, (short) 0x00ff);
    // Рисуем каркасы фигур (ребра и точки)
    drawCarcasses();
    gl.glDepthFunc(GL.GL_LESS);
    gl.glDisable(GL2.GL_LINE_STIPPLE);
    gl.glDepthMask(true);
  }

  public void drawCarcasses(){
    Editor editor = getEditor();
    // Рисуем каркасы фигур (ребра и точки)
    for (int j = 0; j < editor.bd().size(); j++) {
      Drawer.setLineWidth(render, STIPPLE_LINE_WIDTH.value());
      editor.bd().get(j).glDrawCarcass(render);
    }

    // рисуем каркасы якорей
    for (i_Anchor a : editor.anchors().getAnchors()) {
      a.drawCarcass(render);
    }
  }

  public void drawFaces(){
    Editor editor = getEditor();
    // рисуем грани
    // Сначала рисуем грани плоскостей, чтобы они не перекрывали полигоны других фигур
    for (int j = 0; j < editor.bd().size(); j++) {
      if (editor.bd().get(j).type() == BodyType.PLANE) {
        editor.bd().get(j).glDrawFacets(render);
      }
    }
    for (int j = 0; j < editor.bd().size(); j++) {
      if (editor.bd().get(j).type() != BodyType.PLANE)
        editor.bd().get(j).glDrawFacets(render);
    }

    // рисуем якоря-грани
    for (i_Anchor a : editor.anchors().getAnchors()) {
      a.drawSurface(render);
    }
  }

  /**
   * Drawing strokes for badges, medians, altitudes and angles put here.
   */
  public void drawAdditionalElements(){
    GL2 gl = render.getGL();
    gl.glDisable(GL2.GL_DEPTH_TEST);

    // Рисуем дополнительные элементы
    DrawingQueue.drawHighPriority(render);
    gl.glEnable(GL2.GL_DEPTH_TEST);
  }
}
