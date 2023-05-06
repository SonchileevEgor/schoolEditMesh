package opengl.textdrawinggl;

import opengl.CalculatorGL;
import opengl.Drawer;
import opengl.Render;
import com.jogamp.opengl.util.gl2.GLUT;
import geom.Vect3d;

import com.jogamp.opengl.GL2;

/**
 * Реализация отрисовки текста с помощью glutBitmapString.
 */
public class TextDrawerByBitmap implements SingleTextDrawer {
  @Override
  public void drawTextOnSegment(Render ren, String text, Vect3d pointSegmentStart, Vect3d pointSegmentFinish) {
    System.out.println("Warning: BIT_MAP not support drawing on segment");
    Vect3d mid = Vect3d.conv_hull(pointSegmentStart, pointSegmentFinish, 0.5);
    drawText(ren, text, mid, 0);
  }

  @Override
  public void drawText(Render ren, String str, Vect3d point, int indent) {
    if (!Drawer.isViewing(ren, point))
      return;
    GL2 gl = ren.getGL();
    GLUT glut = new GLUT();
    double winCoord[] = new double[2];
    CalculatorGL.getDisplayCoord(ren, point, winCoord);

    gl.glWindowPos2d(winCoord[0] + indent, winCoord[1] + indent);
    glut.glutBitmapString(GLUT.BITMAP_8_BY_13, str);
  }
}
