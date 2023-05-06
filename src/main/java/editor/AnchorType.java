package editor;

import static editor.state.DisplayParam.*;
import editor.state.DisplayParamSet;

/**
 * Enum class for anchor type.
 * @author Dunaeva
 */
public enum AnchorType {
  ANC_POINT("point", new DisplayParamSet(VISIBLE, CHOSEN, MOVABLE, POINT_COLOR, POINT_THICKNESS, TITLE_VISIBLE)),
  ANC_RIB("rib", new DisplayParamSet(VISIBLE, CHOSEN, CARCASS_COLOR, CARCASS_THICKNESS, CARCASS_STYLE, LINE_MARK, LABEL, ARROW_BEGIN, ARROW_END)),
  ANC_POLY("poly", new DisplayParamSet(VISIBLE, CHOSEN, FILL_COLOR)),
  ANC_DISK("disk", new DisplayParamSet(VISIBLE, CHOSEN, CARCASS_COLOR, FILL_COLOR, CARCASS_THICKNESS, FILL_VISIBLE));

  private final String _name; // Human-readable name of anchor type.
  private final DisplayParamSet _params; // параметры отображения якоря данного типа

  private AnchorType(String str, DisplayParamSet params) {
    _name = str;
    _params = params;
  }

  public static AnchorType get(String str) {
    if(!str.equals("point") && !str.equals("rib") && !str.equals("poly") && !str.equals("disk")) {
      throw new AssertionError("Недопустимый тип якоря: " + str);
    }

    if (str.equals("point"))
      return ANC_POINT;
    if (str.equals("rib"))
      return ANC_RIB;
    if (str.equals("poly"))
      return ANC_POLY;
    if (str.equals("disk"))
      return ANC_DISK;

    return null;
  }

  public String getName() {
    return _name;
  }

  /**
   * Список параметров отображения.
   * @return
   */
  public DisplayParamSet getParams() {
    return _params;
  }
}