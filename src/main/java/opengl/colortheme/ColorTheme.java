package opengl.colortheme;

import java.util.ArrayList;
import minjson.JsonObject;
import opengl.colorgl.ColorGL;

/**
 * Current enabled theme using in scene.
 * <br>If you want to get or set some parameters of current theme you should call {@link #getColorTheme()} and
 * use methods of {@link opengl.colortheme.ColorTheme} class.
 * @author MAXbrainRUS
 */
public class ColorTheme {
  // Name of the theme
  private String _name;
  // Color of carcass
  private ColorGL carcassFiguresColorGL;
  // Color of facets
  private ColorGL facetsFiguresColorGL;
  // Color of points
  private ColorGL pointsColorGL;
  // Font of point labels
  private ColorGL fontColorGL;
  // Colors of axes in order: x, y, z
  private ArrayList<ColorGL> colorsOfCoordinateAxes;

  /**
   * When creates, initializes some default theme.
   */
  public ColorTheme() {
    carcassFiguresColorGL = ColorGL.BLACK;
    facetsFiguresColorGL = ColorGL.WHITE;
    colorsOfCoordinateAxes = new ArrayList<ColorGL>();
    pointsColorGL = ColorGL.BLUE;
    fontColorGL = ColorGL.BLACK;
    initColorsOfCoordinateAxes();
  }

  /**
   * Construct theme by Json object.
   * @param theme
   */
  public ColorTheme(JsonObject theme) {
    _name = theme.get("name").asString();
    carcassFiguresColorGL = new ColorGL(theme.get("carcassFiguresColor").asObject());
    facetsFiguresColorGL = new ColorGL(theme.get("facetsFiguresColor").asObject());
    colorsOfCoordinateAxes = new ArrayList<ColorGL>();
    colorsOfCoordinateAxes.add(new ColorGL(theme.get("xAxis").asObject()));
    colorsOfCoordinateAxes.add(new ColorGL(theme.get("yAxis").asObject()));
    colorsOfCoordinateAxes.add(new ColorGL(theme.get("zAxis").asObject()));
    pointsColorGL = new ColorGL(theme.get("pointsColor").asObject());
    fontColorGL = new ColorGL(theme.get("fontColor").asObject());
  }

  public ColorGL getPointsColorGL() {
    return pointsColorGL;
  }

  public void setPointsColorGL(ColorGL pointsColorGL) {
    this.pointsColorGL = pointsColorGL;
  }

  private void initColorsOfCoordinateAxes() {
    for (int i = colorsOfCoordinateAxes.size(); i < 3; i++) {
      colorsOfCoordinateAxes.add(i, ColorGL.RED);
    }
    resetColorsOfCoordinateAxes();
  }

  private void resetColorsOfCoordinateAxes() {
    colorsOfCoordinateAxes.set(0, ColorGL.RED);
    colorsOfCoordinateAxes.set(1, ColorGL.GREEN);
    colorsOfCoordinateAxes.set(2, ColorGL.BLUE);
  }

  public ColorGL getCarcassFiguresColorGL() {
    return carcassFiguresColorGL;
  }

  public void setCarcassFiguresColorGL(ColorGL carcassFiguresColorGL) {
    this.carcassFiguresColorGL = carcassFiguresColorGL;
  }

  public ColorGL getFontColorGL() {
    return fontColorGL;
  }

  public ColorGL getFacetsFiguresColorGL() {
    return facetsFiguresColorGL;
  }

  public String getName() {
    return _name;
  }

  public void setFacetsFiguresColorGL(ColorGL facetsFiguresColorGL) {
    this.facetsFiguresColorGL = facetsFiguresColorGL;
  }

  public void setFontColorGL(ColorGL fontColorGL) {
    this.fontColorGL = fontColorGL;
  }

  public ArrayList<ColorGL> getColorsOfCoordinateAxes() {
    return new ArrayList(colorsOfCoordinateAxes);
  }

  public void setColorsOfCoordinateAxes(ArrayList<ColorGL> colorsOfCoordinateAxes) {
    this.colorsOfCoordinateAxes = colorsOfCoordinateAxes;
  }

  public void setColorsOfCoordinateAxesX(ColorGL colorGL) {
    colorsOfCoordinateAxes.set(0, colorGL);
  }

  public void setColorsOfCoordinateAxesY(ColorGL colorGL) {
    colorsOfCoordinateAxes.set(1, colorGL);
  }

  public void setColorsOfCoordinateAxesZ(ColorGL colorGL) {
    colorsOfCoordinateAxes.set(2, colorGL);
  }

  public void setColorTheme(ColorTheme colorTheme){
    setCarcassFiguresColorGL(colorTheme.getCarcassFiguresColorGL());
    setColorsOfCoordinateAxes(colorTheme.getColorsOfCoordinateAxes());
    setFacetsFiguresColorGL(colorTheme.getFacetsFiguresColorGL());
    setPointsColorGL(colorTheme.getPointsColorGL());
    setFontColorGL(colorTheme.getFontColorGL());
  }
}
