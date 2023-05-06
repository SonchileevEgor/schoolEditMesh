package builders.param;

import java.util.Comparator;

/**
 * Parameter of i_BodyBuilder
 *
 * @author alexeev
 * @param <T> type of return value.
 */
public class BuilderParam<T> {

  /**
   * Frequently-used keys of builder parameters.
   */
  static public final String KEY_A = "A";
  static public final String KEY_B = "B";
  static public final String KEY_C = "C";
  static public final String KEY_D = "D";
  static public final String KEY_ANGLE = "angle";

  /**
   * Names of builder parameters for gui.
   */
  static public final String ANGLE_ALIAS = "Угол";
  static public final String ARC_ALIAS = "Дуга";
  static public final String CIRCLE_ALIAS = "Окружность";
  static public final String COEF_ALIAS = "Коэффициент";
  static public final String COEF_1_ALIAS = "Первый коэффициент";
  static public final String COEF_2_ALIAS = "Второй коэффициент";
  static public final String COORD_ALIAS = "Координаты точки";
  static public final String DISK_ALIAS = "Круг";
  static public final String DIST_TO_CENTER_ALIAS = "Расстояние от центра";
  static public final String LENGTH_OF_BASE_ALIAS = "Длина основания";
  static public final String LENGTH_OF_DIAG_ALIAS = "Длина диагонали";
  static public final String LINE_ALIAS = "Прямая";
  static public final String NAME_ALIAS = "Имя";
  static public final String PLANE_ALIAS = "Плоскость";
  static public final String POINT_ALIAS = "Точка";
  static public final String POINT_1_ALIAS = "Первая точка";
  static public final String POINT_2_ALIAS = "Вторая точка";
  static public final String POINT_3_ALIAS = "Третья точка";
  static public final String POINT_ON_1_SIDE_ALIAS = "Точка на первой стороне";
  static public final String POINT_ON_2_SIDE_ALIAS = "Точка на второй стороне";
  static public final String POLY_ALIAS = "Многоугольник";
  static public final String PRISM_ALIAS = "Призма";
  static public final String ROT_ANGLE_ALIAS = "Угол поворота";
  static public final String RIB_ALIAS = "Ребро";
  static public final String RAY_ALIAS = "Луч";
  static public final String SECTION_PLANE_ALIAS = "Секущая плоскость";
  static public final String TETRAHEDRON_ALIAS = "Тетраэдр";
  static public final String VERT_ALIAS = "Вершина";
  static public final String VERT_1_ALIAS = "Первая вершина";
  static public final String VERT_2_ALIAS = "Вторая вершина";
  static public final String VERT_3_ALIAS = "Третья вершина";
  static public final String VERT_4_ALIAS = "Четвёртая вершина";
  static public final String VERT_1_DIAG_ALIAS = "Первая вершина диагонали";
  static public final String VERT_2_DIAG_ALIAS = "Вторая вершина диагонали";
  static public final String VERT_1_DIAM_ALIAS = "Первая вершина диаметра";
  static public final String VERT_2_DIAM_ALIAS = "Вторая вершина диаметра";
  static public final String VERT_1_BASE_1_ALIAS = "1-я вершина 1-го основания";
  static public final String VERT_2_BASE_1_ALIAS = "2-я вершина 1-го основания";
  static public final String VERT_BASE_2_ALIAS = "Вершина 2-го основания";
  
  public static final Comparator<BuilderParam> PRIORITY_COMPARATOR = 
    new Comparator<BuilderParam>() {
      @Override
      public int compare(BuilderParam o1, BuilderParam o2) {
        return -Integer.compare(o1.getPriority(), o2.getPriority());
      }
    };
  
  /**
   * Key of parameter.
   */
  private String _key;
  
  /**
   * Human-readable name of parameter.
   */
  private String _name;

  /**
   * Parameter value.
   */
  private T _value;

  /**
   * Parameter type.
   */
  private BuilderParamType _type;

  /**
   * Parameter priority.
   * Used in builder params panel in BodySettingsDialog.
   */
  private int _priority;
  
  public BuilderParam( String key, String name, BuilderParamType type ) {
    _key = key;
    _name = name;
    _type = type;
    _priority = type.getDefaultPriority();
  }

  /**
   *
   * @param key   key  of the parameter
   * @param name  name of the parameter (how it displayed in GUI)
   * @param type  type of the parameter
   * @param value value of the parameter
   */
  @Deprecated
  public BuilderParam( String key, String name, BuilderParamType type, T value ) {
    this(key, name, type);
    _value = value;
    _priority = type.getDefaultPriority();
  }


  /**
   * Human-readable name of parameter.
   * Required for displaying in UI.
   * @return
   */
  public String name() {
    return _name;
  }
  
  /**
   * Key of the parameter;
   * @return 
   */
  public String getKey() {
    return _key;
  }

  /**
   * Get parameter value.
   * @return
   */
  public T getValue() {
    return _value;
  }

  /**
   * Set parameter value.
   * @param value
   */
  public void setValue(T value) {
    _value = value;
  }

  /**
   * Type of the parameter.
   * @return
   */
  public BuilderParamType type() {
    return _type;
  }

  public int getPriority() {
    return _priority;
  }
  
  public void setPriority(int priority) {
    _priority = priority;
  }
}
