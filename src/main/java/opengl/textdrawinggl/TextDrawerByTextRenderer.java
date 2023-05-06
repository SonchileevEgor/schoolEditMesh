package opengl.textdrawinggl;

import opengl.CalculatorGL;
import opengl.Drawer;
import opengl.Render;
import com.jogamp.opengl.util.awt.TextRenderer;
import geom.*;

import com.jogamp.opengl.GL2;
import java.awt.geom.Rectangle2D;

/**
 * Draw text using OpenGL text renderer.
 */
public class TextDrawerByTextRenderer implements SingleTextDrawer {

  static private double TEXT_MULTIPLIER_TEXT_RENDERER = 0.0025;

  @Override
  public void drawText(Render ren, String str, Vect3d point, int indent) {
    double winCoord[] = new double[2];
    CalculatorGL.getDisplayCoord(ren, point, winCoord);

    // Text not drawing if clipping planes enabled
    ren.getViewVolume().disableClippingPlanes(ren);

    TextRenderer textRenderer = ren.getTextRenderer();
    textRenderer.beginRendering(ren.getWidth(), ren.getHeight());
    textRenderer.draw(str, (int)winCoord[0] + indent, (int)winCoord[1] + indent);

    textRenderer.endRendering();

    // Get back clipping planes enabled
    ren.getViewVolume().enableClippingPlanes(ren);
  }

  @Override
  public void drawTextOnSegment(Render ren, String text, Vect3d pointSegmentStart, Vect3d pointSegmentFinish) {
    //!! TODO: сделать с этой функцией что-нибудь, а то она очень большая
    TextRenderer textRenderer = ren.getTextRenderer();
    GL2 gl = ren.getGL();
    // By default text (in 3d text renderer) drawing in Oxy plane with face direction on Oz
    Vect3d upDef = new Vect3d(0, 0, 1); // Face direction
    Vect3d botDef = new Vect3d(1, 0, 0); // Bottom direction of text from the first letter to the last
    Vect3d eyeDef = ren.getCameraPosition().eye();

    // Vector from start point to finish point
    Vect3d segment = Vect3d.sub(pointSegmentFinish, pointSegmentStart);

    Rectangle2D rectangle2D = textRenderer.getBounds(text);
    double textWidth = rectangle2D.getWidth(); // Width of text in coordinates of scene.
    // Size must be different in depending of camera distance (text should be visible always )
    double textSizeMultiplier = ren.getCameraPosition().distance() * TEXT_MULTIPLIER_TEXT_RENDERER;
    textWidth *= textSizeMultiplier;
    // Indent in segment coordinates to start draw text
    // Text drawing in center of segment.
    double indent = (segment.norm() - textWidth) / 2;
    Vect3d startDrawingIndentVector = Vect3d.mul(Vect3d.getNormalizedVector(segment), indent);
    // The starting point for drawing text
    Vect3d drawingStartPoint = Vect3d.sum(pointSegmentStart, startDrawingIndentVector);
    Vect3d center2eye = Vect3d.sub(eyeDef, drawingStartPoint);

    // Промежуточный вектор. В плоскости надписи указывает вверх
    Vect3d leftUp = Vect3d.vector_mul(center2eye, segment);
    // Вектор, куда будет смотреть надпись
    Vect3d realUp = Vect3d.vector_mul(segment, leftUp);

    // Not drawing text outside viewing cube
    if (!Drawer.inViewingCube(ren, drawingStartPoint))
      return;

    // Text not drawing if clipping planes enabled
    ren.getViewVolume().disableClippingPlanes(ren);
    gl.glPushMatrix();

    textRenderer.begin3DRendering();
    gl.glTranslated(drawingStartPoint.x(), drawingStartPoint.y(), drawingStartPoint.z());
    gl.glScaled(textSizeMultiplier, textSizeMultiplier, textSizeMultiplier);
    Drawer.rotate(gl, upDef, drawingStartPoint, realUp.add(drawingStartPoint)); // Повернуть надпись в нужную плоскость

    // Вычисляем угол, на который необходимо докрутить надпись так, чтобы она была параллельна отрезку
    // (т к после функции rotate надпись находится в плоскости отрезка, но не параллельна ему)
    Matrix3 mat = Drawer.getRotateMatrix(upDef, drawingStartPoint, realUp.add(drawingStartPoint));
    Vect3d newBegin = mat.mulOnVect(botDef); // Направление надписи после преобразования
    double addAngle = Vect3d.getAngle(newBegin, segment); // угол между получившимся направлением надписи и необходимым

    // Выясняем в каком направлении подкручивать надпись (по часовой или против часовой стрелки)
    Vect3d checkNorm = Vect3d.vector_mul(newBegin, segment);
    if ( Vect3d.getAngle(realUp, checkNorm) > Math.PI / 2)
      addAngle *= -1;

    // Подкручиваем надпись, чтобы она была параллельна отрезку
    gl.glRotated(Angle3d.radians2Degree(addAngle), upDef.x(), upDef.y(), upDef.z());

    textRenderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
    // Drawing in point (0, 0, 0) because we already call glTranslated
    // (already translate origin to drawingStartPoint).
    textRenderer.draw3D(text, 0, 0, 0, 1);
    textRenderer.end3DRendering();

    gl.glPopMatrix();

    ren.getViewVolume().enableClippingPlanes(ren);
  }
}
