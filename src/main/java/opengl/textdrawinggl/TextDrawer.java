package opengl.textdrawinggl;

import opengl.CalculatorGL;
import opengl.Render;
import geom.Vect3d;

import java.util.EnumMap;
import java.util.Map;

/**
 * Класс, содержащий функции для отрисовки текста на сцене.
 * <br>Данный класс агрегирует в себе несколько отрисовщиков(технологий отрисовки) текста.
 */
public class TextDrawer {
  static Map<TextDrawingType, SingleTextDrawer> textDrawers = null;
  /**
   * Default indent in pixels for
   * <br>{@link #drawText(opengl.Render, String, geom.Vect3d, TextDrawingType)} and
   * <br>{@link #drawText(opengl.Render, String, geom.Vect3d)}
   */
  static private int defaultIndent = 4;

  /**
   * Lazy initialization of textDrawers.
   * <br>The second and subsequent calls do nothing.
   */
  static private void checkInitialization(){
    if (textDrawers == null){
      textDrawers = new EnumMap<>(TextDrawingType.class);
      textDrawers.put(TextDrawingType.BIT_MAP, new TextDrawerByBitmap());
      textDrawers.put(TextDrawingType.TEXT_RENDERER, new TextDrawerByTextRenderer());
      textDrawers.put(TextDrawingType.TEXTURE, new TextDrawerLatexTexture());
    }
  }

  /**
   * Add label in cache for fast drawing call.
   * <br>Working only for TEXTURE type
   * @param ren Object for drawing in OpenGL canvas
   * @param label Label for caching.
   */
  static public void cacheLabel(Render ren, String label){
    checkInitialization();
    ((TextDrawerLatexTexture)textDrawers.get(TextDrawingType.TEXTURE)).cacheLabel(ren, label);
  }

  /**
   * Draw text on point.
   * <br>The text is located above the point and is directed at the camera.
   * <br>The direction of the text is parallel to the lines of window of application.
   * @param ren Object for drawing in OpenGL canvas
   * @param text String value of text
   * @param point Point, near which the text will be drawn
   * @param indent Spacing between the selected point and the beginning of the text(in pixels)
   * @param type The type of method used for drawing
   */
  static public void drawText(Render ren, String text, Vect3d point, int indent, TextDrawingType type) {
    checkInitialization();
    try {
      textDrawers.get(type).drawText(ren, text, point, indent);
    }
    catch (Exception ex){
      textDrawers.get(TextDrawingType.TEXT_RENDERER).drawText(ren, text, point, indent);
    }
  }

  /**
   * The same as {@link #drawText(opengl.Render, String, geom.Vect3d, int, TextDrawingType)}
   * with default parameters.
   */
  static public void drawText(Render ren, String str, Vect3d point, TextDrawingType type){
    drawText(ren, str, point, defaultIndent, type);
  }

  /**
   * The same as {@link #drawText(opengl.Render, String, geom.Vect3d, int, TextDrawingType)}
   * with default parameters.
   */
  static public void drawText(Render ren, String str, Vect3d point){
    drawText(ren, str, point, TextDrawingType.BIT_MAP);
  }

  /**
   * Draw text on the route of segment (from the starting point to the end point).
   * <br>The text is located above the segment center and is directed at the camera.
   * <br>The direction of the text is parallel to the segment direction.
   * <br>Order of the start and end points do not matter.
   * <br>That is
   * <pre><code>
   *   drawTextOnSegment(render, "text", point1, point2);
   * </code></pre>
   * the same that
   * <pre><code>
   *   drawTextOnSegment(render, "text", point2, point1);
   * </code></pre>
   * @param ren Object for drawing in OpenGL canvas
   * @param text string value of text
   * @param pointSegmentStart Start point of direction segment
   * @param pointSegmentFinish Finish point of direction segment
   * @param type The type of method used for drawing
   */

  static public void drawTextOnSegment(Render ren, String text, Vect3d pointSegmentStart, Vect3d pointSegmentFinish,
                                       TextDrawingType type) {
    try {
      drawTextOnSegmentCheckOrientation(ren, text, pointSegmentStart, pointSegmentFinish, type);
    }
    catch (Exception ex){
      drawTextOnSegmentCheckOrientation(ren, text, pointSegmentStart, pointSegmentFinish, TextDrawingType.TEXT_RENDERER);
    }
  }

  static public void drawTextOnSegmentCheckOrientation(Render ren, String text,
          Vect3d pointSegmentStart, Vect3d pointSegmentFinish, TextDrawingType type){
    checkInitialization();
    double[] coordStartPoint = new double[2];
    double[] coordFinishPoint = new double[2];
    CalculatorGL.getDisplayCoord(ren, pointSegmentStart, coordStartPoint);
    CalculatorGL.getDisplayCoord(ren, pointSegmentFinish, coordFinishPoint);

    // Protection against inverted text
    if (coordStartPoint[0] < coordFinishPoint[0])
      textDrawers.get(type).drawTextOnSegment(ren, text, pointSegmentStart, pointSegmentFinish);
    else
      textDrawers.get(type).drawTextOnSegment(ren, text, pointSegmentFinish, pointSegmentStart);
  }

  /**
   * The same as {@link #drawTextOnSegment(opengl.Render, String, geom.Vect3d, geom.Vect3d, TextDrawingType)} with
   * default type of method used for drawing.
   */
  static public void drawTextOnSegment(Render ren, String text, Vect3d pointSegmentStart, Vect3d pointSegmentFinish){
    drawTextOnSegment(ren, text, pointSegmentStart, pointSegmentFinish, TextDrawingType.TEXT_RENDERER);
  }


}
