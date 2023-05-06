package gui.mode.param;


/**
 * Types of create body parameters.
 * @author rita
 */
public enum CreateModeParamType {

  /**
   * Rotation angle.
   */
  ROT_ANGLE("Угол поворота"),

  /**
   * Angle.
   */
  ANGLE("Угол"),

  /**
   * Angle which can be equal to 0, but not to 90 degrees.
   */
  ANGLE2("Угол"),

  LENGTH("Длина"),
  RADIUS("Радиус"),
  WIDTH("Ширина"),
  HEIGHT("Высота"),

  /**
   * Real coefficient.
   */
  COEFFICIENT("Коэффициент"),
  COEFFICIENT2("Коэффициент"),

  /**
   * Ratio of two values.
   */
  RATIO("Отношение"),

  /**
   * Number of vertices.
   */
  VERT_NUMBER("Количество вершин");

  private String _title;

  CreateModeParamType(String title) {
    _title = title;
  }

  public String getTitle() {
    return _title;
  }
}
