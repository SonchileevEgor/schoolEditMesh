package opengl.textdrawinggl;

import opengl.Render;
import geom.Vect3d;

/**
 * Интерфейс для одной реализации рисовальщика текста на сцене.
 * <br>Каждая реализация рисовальщика должна уметь отрисовывать текст над точкой
 * {@link #drawText(opengl.Render, String, geom.Vect3d, int)}
 * и над отрезком
 * {@link #drawTextOnSegment(opengl.Render, String, geom.Vect3d, geom.Vect3d)}.
 */
public interface SingleTextDrawer {

  /**
   * Draw text on point.
   * <br>The text is located above the point and is directed at the camera.
   * <br>The direction of the text is parallel to the lines of window of application.
   * @param ren object for drawing in OpenGL canvas
   * @param str string value of text
   * @param point left bottom point of text
   * @param indent indent from point in pixels
   */
  abstract public void drawText(Render ren, String str, Vect3d point, int indent);

  /**
   * Draw text on the route of segment (from the starting point to the end point).
   * <br>The text is located above the segment center and is directed at the camera.
   * <br>The direction of the text is parallel to the segment direction.
   * @param ren Object for drawing in OpenGL canvas
   * @param text string value of text
   * @param pointSegmentStart Start point of direction segment
   * @param pointSegmentFinish Finish point of direction segment
   */
  abstract public void drawTextOnSegment(Render ren, String text, Vect3d pointSegmentStart, Vect3d pointSegmentFinish);

}
