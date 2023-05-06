package opengl;

import geom.Vect3d;

import java.util.ArrayList;
import java.util.List;
import opengl.colorgl.ColorGL;
import opengl.sceneparameters.CameraPosition;
import opengl.textdrawinggl.TextDrawer;
import opengl.textdrawinggl.TextDrawingType;

/**
 * Draftsman of the coordinate axes and scales on it
 */
public class AxesDrawer {
  /**
   * If cosine of angle between sight and axis is greater than that value,
   * we don't draw labels on axis to avoid overlapping.
   */
  private static final double MAX_COS_DRAW_LABELS = 0.99;

  private static ArrayList<Double> stepTable;

  static {
    stepTable = new ArrayList<>();
    stepTable.add(0.05);
    for (int i = 0; i < 20; i++){
      double multiplier;
      if (i % 3 == 1)
        multiplier = 2.5;
      else
        multiplier = 2;
      stepTable.add(multiplier * stepTable.get(stepTable.size() - 1));
    }
  }

  private AxesDrawer(){}

  /**
   * Draw axis from zero in vect direction.
   * @param ren object for drawing in OpenGL canvas
   * @param vect direction to draw
   */
  static private void drawAxis(Render ren, Vect3d vect, double size){
    Drawer.drawSegment(ren, Vect3d.O, Vect3d.mul(vect, size));
  }

  /**
   * Draw scale marks.
   * @param ren object for drawing in OpenGL canvas
   * @param vec Direction of coordinate axis
   */
  static private void drawScale(Render ren, Vect3d vec){
    CameraPosition cam = ren.getCameraPosition();
    // Max value which can be markup
    double maxValue =  cam.distance() * 0.5;
    Vect3d normVec = Vect3d.getNormalizedVector(vec);
    double step = calcMarkupIndent(ren);
    // Drawing markups
    double markScale = step;

    // Если направление взгляда близко к направлению оси, то не рисуем надписи.
    boolean drawMarks = Math.abs((Vect3d.cos(
        Vect3d.sub(ren.getCameraPosition().eye(), ren.getCameraPosition().center()
        ), vec))) < MAX_COS_DRAW_LABELS;

    while (markScale < maxValue){
      String title;
      if (step < 5) {
        title = String.format("%.2f", markScale);
      } else {
        title = String.format("%.0f", markScale);
      }
      Vect3d scaledMark = Vect3d.mul(normVec, markScale);
      // Only point in the volume of visibility to be displayed
      if (ren.getViewVolume().inViewingCube(scaledMark)) {
        if (drawMarks) {
          TextDrawer.drawText(ren, title, scaledMark, TextDrawingType.TEXT_RENDERER);
        }
        Drawer.drawHatch(ren, scaledMark, normVec);
      }

      markScale += step;
    }
  }

  /**
   * Draw coordinate axes
   * @param ren Object for drawing in OpenGL canvas
   * @param colors
   * @param size
   */
  static public void drawCoordinateAxes(Render ren, List<ColorGL> colors, double size){
    Vect3d xAxe = new Vect3d(1, 0, 0);
    Vect3d yAxe = new Vect3d(0, 1, 0);
    Vect3d zAxe = new Vect3d(0, 0, 1);

    Drawer.setObjectColor(ren, colors.get(0));
    ren.getTextRenderer().setColor(colors.get(0).toRGBAColor());
    drawScale(ren, xAxe);
    drawAxis(ren, xAxe, size);

    Drawer.setObjectColor(ren, colors.get(1));
    ren.getTextRenderer().setColor(colors.get(1).toRGBAColor());
    drawScale(ren, yAxe);
    drawAxis(ren, yAxe, size);

    Drawer.setObjectColor(ren, colors.get(2));
    ren.getTextRenderer().setColor(colors.get(2).toRGBAColor());
    drawScale(ren, zAxe);
    drawAxis(ren, zAxe, size);
  }

  static public void drawLabelAtOrigin(Render ren){
    TextDrawer.drawText(ren, "0", Vect3d.O, TextDrawingType.TEXT_RENDERER);
  }

  static public void drawCoordinateAxesXY(Render ren, List<ColorGL> colors, double size){
    Vect3d xAxe = new Vect3d(1, 0, 0);
    Vect3d yAxe = new Vect3d(0, 1, 0);

    Drawer.setObjectColor(ren, colors.get(0));
    ren.getTextRenderer().setColor(colors.get(0).toRGBAColor());
    drawLabelAtOrigin(ren);
    drawScale(ren, xAxe);
    drawLongAxis(ren, xAxe, size, 1);

    Drawer.setObjectColor(ren, colors.get(1));
    ren.getTextRenderer().setColor(colors.get(1).toRGBAColor());
    drawScale(ren, yAxe);
    drawLongAxis(ren, yAxe, size, 1);
  }

  /**
   * Draw coordinate axes
   * @param ren Object for drawing in OpenGL canvas
   * @param colors
   */
  static public void drawCoordinateAxes(Render ren, List<ColorGL> colors){
    drawCoordinateAxes(ren, colors, ren.getInitialPlane().getSizeInitPlane());
  }
  static public void drawCoordinateAxesXY(Render ren, List<ColorGL> colors){
    drawCoordinateAxesXY(ren, colors, ren.getInitialPlane().getSizeInitPlane());
  }

   /**
   * Draw axis from zero in vect direction
   * @param ren object for drawing in OpenGL canvas
   * @param vect direction to draw
   */
  static private void drawLongAxis(Render ren, Vect3d vect, double size, double lineWidth){
    Drawer.drawSegment(ren, Vect3d.O, Vect3d.mul(vect, size), lineWidth);
    Drawer.drawSegment(ren, Vect3d.O, Vect3d.mul(vect, -size), lineWidth);
  }

  static private double calcMarkupIndent(Render ren){
    CameraPosition cam = ren.getCameraPosition();
    // Max value which can be markup
    double maxValue =  cam.distance() * 0.5;
    double optimalStep = maxValue / 4;
    // Search optimal step from stepTable
    double bestFloorStep = stepTable.get(0);
    for (Double el : stepTable){
      if (Math.abs(el - optimalStep) < Math.abs(bestFloorStep - optimalStep))
        bestFloorStep = el;
    }
    return bestFloorStep;
  }

  /**
   * Get distance from origin to first mark in scene coordinates.
   * (same as distance between marks,as marks are the same distance from each other)
   * @return Distance between marks
   */
  static public double getMarkupSize(Render ren) {
    return calcMarkupIndent(ren);
  }
}
