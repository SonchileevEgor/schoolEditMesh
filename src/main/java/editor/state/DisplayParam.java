package editor.state;

import static config.Config.LINE_WIDTH;
import static config.Config.POINT_TITLE_VISIBLE;
import static config.Config.POINT_WIDTH;
import gui.elements.AngleSideType;
import gui.elements.AngleStyle;
import gui.elements.SidesSizeType;
import opengl.colorgl.ColorGL;
import gui.elements.LineType;
import minjson.JsonValue;
import opengl.LineMark;
import opengl.StyleArrow;
import opengl.colortheme.CurrentTheme;

/**
 * List of display param types.
 *
 * @author alexeev
 */
public enum DisplayParam {
  /**
   * Visibility.
   * boolean value.
   */
  VISIBLE("visible", true),

  /**
   * Visibility of title.
   * boolean value.
   */
  TITLE_VISIBLE("title_visible", true),

  /**
   * Is entity (anchor / body) chosen or not.
   * boolean value.
   */
  CHOSEN("chosen", false),

  /**
   * Is anchor movable or not.
   * boolean value.
   */
  MOVABLE("movable", false),

  /**
   * Соотношение длин сторон угла.
   * Требуется для корректного отображения отметки угла.
   * SidesSizeType value.
   */
  SIDES_SIZE_TYPE("sides_type", false),

  /**
   * Point color.
   * ColorGL value.
   */
  POINT_COLOR("point_color", true),

  /**
   * Fill color (only for 2- or 3-dimensional bodies).
   * ColorGL value.
   */
  FILL_COLOR("facet_color", true),

  /**
   * Filling visibility.
   * boolean value.
   */
  FILL_VISIBLE("facet_visible", true),

  /**
   * Mark on segment.
   * LineMark value.
   */
  LINE_MARK("line_mark", true),

  /**
   * Style of carcass lines.
   * LineType value.
   */
  CARCASS_STYLE("carcass_style", true),

  /**
   * Thickness of carcass lines.
   * float value.
   */
  CARCASS_THICKNESS("carcass_thickness", true),

  /**
   * Color of carcass lines.
   * ColorGL value.
   */
  CARCASS_COLOR("carcass_color", true),

  /**
   * Thickness of points.
   * float value.
   */
  POINT_THICKNESS("point_thickness", true),

  /**
   * How much to increase the size of the plane.
   * double value.
   */
  PLANE_INDENT("plane_indent", true),

  /**
   * Label.
   * String value.
   */
  LABEL("label", true),

  /**
   * Angle style.
   * AngleStyle value.
   */
  ANGLE_STYLE("angle_style", true),

  /**
   * Type of arrow begin.
   * StyleArrow value.
   */
  ARROW_BEGIN("arrow_begin", true),

  /**
   * Type of arrow end.
   * StyleArrow value.
   */
  ARROW_END("arrow_end", true),

  /**
   * Visibility of asymptotes.
   * Boolean value.
   */
  DRAW_ASYMPTOTES("asymp", true),

  /**
   * Отображать первую сторону угла пунктиром.
   * Boolean value.
   */
  DRAW_FIRST_ANGLE_SIDE("angle_side_first", false),

  /**
   * Отображать вторую сторону угла пунктиром.
   * Boolean value.
   */
  DRAW_SECOND_ANGLE_SIDE("angle_side_second", false);

  private String _key;
  private boolean _mutable; // может ли параметр изменяться пользователем.

  DisplayParam(String key, boolean mutable) {
    _key = key;
    _mutable = mutable;
  }

  /**
   * Get DisplayParam by key.
   * If there's no param with given key, return null.
   * @param key
   * @return
   */
  public static DisplayParam get(String key) {
    for (DisplayParam p : DisplayParam.values()) {
      if (p._key.equals(key)) {
        return p;
      }
    }
    return null;
  }

  public String getKey() {
    return _key;
  }

  public Object getDefaultValue() {
    switch(this) {
      case VISIBLE: return true;
      case TITLE_VISIBLE: return POINT_TITLE_VISIBLE.value();
      case CHOSEN: return false;
      case MOVABLE: return false;
      case FILL_COLOR: return CurrentTheme.getColorTheme().getFacetsFiguresColorGL();
      case FILL_VISIBLE: return true;
      case LINE_MARK: return LineMark.NONE;
      case CARCASS_STYLE: return LineType.PLAIN;
      case CARCASS_THICKNESS: return LINE_WIDTH.value();
      case CARCASS_COLOR: return CurrentTheme.getColorTheme().getCarcassFiguresColorGL();
      case PLANE_INDENT: return 1.0;
      case LABEL: return "";
      case ANGLE_STYLE: return AngleStyle.SINGLE;
      case POINT_THICKNESS: return POINT_WIDTH.value();
      case POINT_COLOR: return CurrentTheme.getColorTheme().getPointsColorGL();
      case ARROW_BEGIN: return StyleArrow.NONE;
      case ARROW_END: return StyleArrow.NONE;
      case DRAW_ASYMPTOTES: return false;
      case SIDES_SIZE_TYPE: return SidesSizeType.SHORT_SHORT;
      case DRAW_FIRST_ANGLE_SIDE: return AngleSideType.DEFAULT;
      case DRAW_SECOND_ANGLE_SIDE: return AngleSideType.DEFAULT;
      default:
        throw new AssertionError(this.name());
    }
  }

  public Object getValue(JsonValue jv) {
    switch (this) {
      // boolean values
      case VISIBLE:
      case CHOSEN:
      case MOVABLE:
      case TITLE_VISIBLE:
      case FILL_VISIBLE:
      case DRAW_ASYMPTOTES:
        return jv.asBoolean();

      // ColorGL values
      case FILL_COLOR:
      case CARCASS_COLOR:
      case POINT_COLOR:
        return new ColorGL(jv.asObject());

      // LineType values
      case CARCASS_STYLE:
        return LineType.getByKey(jv.asString());

      // float values
      case CARCASS_THICKNESS:
      case POINT_THICKNESS:
        return jv.asFloat();

      // double values
      case PLANE_INDENT:
        return jv.asDouble();

      // string values
      case LABEL:
        return jv.asString();

      // AngleStyle value
      case ANGLE_STYLE:
        return AngleStyle.getByKey(jv.asString());

      // LineMark value
      case LINE_MARK:
        return LineMark.getByKey(jv.asString());

      // StyleArrow values
      case ARROW_BEGIN:
      case ARROW_END:
        return StyleArrow.getByKey(jv.asString());

      // SidesSizeType value
      case SIDES_SIZE_TYPE:
        return SidesSizeType.getByKey(jv.asString());

      case DRAW_FIRST_ANGLE_SIDE:
      case DRAW_SECOND_ANGLE_SIDE:
        return AngleSideType.getByKey(jv.asString());

      default:
        throw new AssertionError(this.name());
    }

  }

  /**
   * Конвертируем объект в JsonValue.
   * Тип значения выбирается в зависимости от типа параметра DisplayParamType.
   * @param o
   * @return
   */
  public JsonValue toJson(Object o) {
    switch (this) {
      // boolean values
      case VISIBLE:
      case CHOSEN:
      case MOVABLE:
      case FILL_VISIBLE:
      case TITLE_VISIBLE:
      case DRAW_ASYMPTOTES:
        return JsonValue.valueOf((boolean)o);

      // ColorGL values
      case FILL_COLOR:
      case CARCASS_COLOR:
      case POINT_COLOR:
        return ((ColorGL)o).toJson();

      // LineType values
      case CARCASS_STYLE:
        return JsonValue.valueOf(((LineType)o).getKey());

      // float values
      case CARCASS_THICKNESS:
      case POINT_THICKNESS:
        return JsonValue.valueOf((float)o);

      // double values
      case PLANE_INDENT:
        return JsonValue.valueOf((double)o);

      // string values
      case LABEL:
        return JsonValue.valueOf((String)o);

      //AngleStyle value
      case ANGLE_STYLE:
        return JsonValue.valueOf(((AngleStyle)o).getKey());

      // LineMark value
      case LINE_MARK:
        return JsonValue.valueOf(((LineMark)o).getKey());

      // StyleArrow values
      case ARROW_BEGIN:
      case ARROW_END:
        return JsonValue.valueOf(((StyleArrow)o).getKey());

      // SidesSizeType value
      case SIDES_SIZE_TYPE:
        return JsonValue.valueOf(((SidesSizeType)o).getKey());

      // AngleSideType value
      case DRAW_FIRST_ANGLE_SIDE:
      case DRAW_SECOND_ANGLE_SIDE:
        return JsonValue.valueOf(((AngleSideType)o).getKey());

      default:
        throw new AssertionError(this.name());
    }
  }
}
