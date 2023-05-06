package opengl.textdrawinggl;

import opengl.*;
import com.jogamp.opengl.util.texture.Texture;
import geom.ExDegeneration;
import geom.Vect3d;
import latex.ImageGenerator;

import com.jogamp.opengl.GL2;
import opengl.colorgl.ColorGL;
import opengl.sceneparameters.CameraPosition;

/**
 * Реализация отрисовки текста(математической формулы в латеховской нотации) с помощью текстурирования.
 */
public class TextDrawerLatexTexture implements SingleTextDrawer {

  static private double TEXT_MULTIPLIER_TEXTURE = 0.001;
  private LinearInterpolator screenHeightToTextMultiplierInterpolator = new LinearInterpolator();

  TextDrawerLatexTexture(){
    screenHeightToTextMultiplierInterpolator.setX0(1397);
    screenHeightToTextMultiplierInterpolator.setY0(0.0004);
    screenHeightToTextMultiplierInterpolator.setX1(415);
    screenHeightToTextMultiplierInterpolator.setY1(0.0011);
  }

  /**
   * Add label in cache for fast drawing call.
   * @param ren Object for drawing in OpenGL canvas
   * @param label Label for caching.
   */
  public void cacheLabel(Render ren, String label){
    try {
      // Генератор текстур сам кэширует созданные им текстуры.
      Texture texture = ImageGenerator.getTextureWithLatexText(ren, label);
    }
    catch (Exception ex){
      System.out.println("Cache fail");
      return;
    }
  }

  @Override
  public void drawText(Render ren, String text, Vect3d point, int indent) {
    Vect3d pointWithIndent;
    try {
      pointWithIndent = CalculatorGL.addIndentForPoint(ren, point, indent);
    }
    catch (ExDegeneration ex){
      pointWithIndent = point;
    }
    Texture texture;
    try {
      texture = ImageGenerator.getTextureWithLatexText(ren, text);
    }
    catch (Exception ex){
      System.out.println(ex.getMessage());
      return;
    }

    int textHeight = texture.getImageHeight();
    int textWidth = texture.getImageWidth();

    TEXT_MULTIPLIER_TEXTURE = screenHeightToTextMultiplierInterpolator.getInterpolationValue(ScreenSizeManager.getScreenHeight());
    // Size must be different in depending of camera distance (text should be visible always )
    double textSizeMultiplier = ren.getCameraPosition().distance() * TEXT_MULTIPLIER_TEXTURE;
    double drawingTextWidth = textWidth * textSizeMultiplier;
    double drawingTextHeight = textHeight * textSizeMultiplier;

    CameraPosition cam = ren.getCameraPosition();

    // Вектор, направленный из точки в глаз наблюдателя ()
    Vect3d p2e;
    if (ren.isSceneIn3d() == false) {
      p2e = Vect3d.sub(cam.eye(), new Vect3d(0, 0, 0));
    }
    else {
      p2e = Vect3d.sub(cam.eye(), pointWithIndent);
    }
    Vect3d upIndent = cam.getUpPerpendicularVect().getNormalized().mul(drawingTextHeight);
    Vect3d rightIndent = Vect3d.vector_mul(upIndent, p2e).getNormalized().mul(drawingTextWidth);

    drawLatexText(ren, text,
        pointWithIndent.add(upIndent),
        pointWithIndent.add(upIndent).add(rightIndent),
        pointWithIndent,
        pointWithIndent.add(rightIndent)
    );
  }

  @Override
  public void drawTextOnSegment(Render ren, String text, Vect3d pointSegmentStart, Vect3d pointSegmentFinish) {
    Texture texture;
    try {
      texture = ImageGenerator.getTextureWithLatexText(ren, text);
    }
    catch (Exception ex){
      System.out.println(ex.getMessage());
      return;
    }

    int textHeight = texture.getImageHeight();
    int textWidth = texture.getImageWidth();

    Vect3d eyeDef = ren.getCameraPosition().eye();

    // Size must be different in depending of camera distance (text should be visible always )
    double textSizeMultiplier = ren.getCameraPosition().distance() * TEXT_MULTIPLIER_TEXTURE;
    double drawingTextWidth = textWidth * textSizeMultiplier;
    double drawingTextHeight = textHeight * textSizeMultiplier;

    // Vector from start point to finish point
    Vect3d segment = Vect3d.sub(pointSegmentFinish, pointSegmentStart);
    // Indent in segment coordinates to start draw text
    // Text drawing in center of segment.
    double indent = (drawingTextWidth) / 2.0;
    Vect3d segmentHalf = Vect3d.mul(segment, 1.0 / 2);
    Vect3d pointSegmentCenter = Vect3d.sum(pointSegmentStart, segmentHalf);

    Vect3d center2eye;
    if (ren.isSceneIn3d() == false) {
      center2eye = Vect3d.sub(eyeDef, new Vect3d(0, 0, 0));
    }
    else {
      center2eye = Vect3d.sub(eyeDef, pointSegmentCenter);
    }

    // В плоскости надписи указывает вверх
    Vect3d leftUp = Vect3d.vector_mul(center2eye, segment);

    Vect3d upIndent = Vect3d.getNormalizedVector(leftUp);
    upIndent.inc_mul(drawingTextHeight);

    Vect3d indentVector = Vect3d.getNormalizedVector(segment);
    indentVector.inc_mul(indent);

    drawLatexText(ren,
        text,
        pointSegmentCenter.sub(indentVector).sum(upIndent),
        pointSegmentCenter.sum(indentVector).sum(upIndent),
        pointSegmentCenter.sub(indentVector),
        pointSegmentCenter.sum(indentVector)
    );
  }

  /**
   * Draw a logical mathematical formula in latex notation.
   * <br>All points must be in a same plane.
   * @param ren Object for drawing in OpenGL canvas.
   * @param text Drawing text (a logical mathematical formula in latex notation).
   * @param ul Upper left point.
   * @param ur Upper right point.
   * @param bl Bottom left point.
   * @param br Bottom right point.
   */
  static public void drawLatexText(Render ren, String text, Vect3d ul, Vect3d ur, Vect3d bl, Vect3d br){
    /**
     * Realization: Generate texture with specified text and drawing a rectangle with this texture.
     * <br>По тексту генерируется картинка и загружается в OpenGL как текстура.
     * Затем на месте расположения надписи создается прямоугольный полигон и на него натягивается данная текстура.
     */
    Texture texture;
    try {
      texture = ImageGenerator.getTextureWithLatexText(ren, text);
    }
    catch (Exception ex){
      System.out.println(ex.getMessage());
      return;
    }
    GL2 gl = ren.getGL();
    // Color filter of texture. For white color texture not changes.
    gl.glColor3dv(ColorGL.WHITE.getArray(), 0);
    texture.bind(gl);

    // Since the square texture is drawn as two triangles, then smoothing polygons creates a line on the border of their contact
    SwitcherStateGL.saveAndDisable(ren, GL2.GL_POLYGON_SMOOTH);
    // Сolor of texture should not depend on the light.
    SwitcherStateGL.saveAndDisable(ren, GL2.GL_LIGHTING);
    // Надпись должна быть видна, даже если ее закрывает какой-нибудь полигон.
    SwitcherStateGL.saveAndDisable(ren, GL2.GL_DEPTH_TEST);
    // Включаем режим текстурирования
    SwitcherStateGL.saveAndEnable(ren, GL2.GL_TEXTURE_2D);

    // Без цветового смешивания надписи рисоваться не будут, а будет просто одноцветный прямоугольник
    SwitcherStateGL.saveAndEnable(ren, GL2.GL_BLEND);
    SwitcherStateGL.saveAndChangeBlendFunc(ren, gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);

    gl.glBegin(GL2.GL_QUADS);
    gl.glNormal3f(0.0f, 0.0f, -1.0f);

    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex3dv(bl.getArray(), 0);

    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex3dv(br.getArray(), 0);

    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex3dv(ur.getArray(), 0);

    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex3dv(ul.getArray(), 0);

    gl.glEnd();

    // Возвращаем исходное состояние машины состояний OpenGL
    SwitcherStateGL.restore(ren, GL2.GL_POLYGON_SMOOTH);
    SwitcherStateGL.restore(ren, GL2.GL_LIGHTING);
    SwitcherStateGL.restore(ren, GL2.GL_TEXTURE_2D);
    SwitcherStateGL.restore(ren, GL2.GL_DEPTH_TEST);

    SwitcherStateGL.restore(ren, GL2.GL_BLEND);
    SwitcherStateGL.restoreBlendFunc(ren);
  }

}
