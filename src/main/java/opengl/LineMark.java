package opengl;

/**
 * Стиль штрихов на ребре.
 *
 * @author alexeev-laptop
 */
public enum LineMark {
  NONE("none"),
  SINGLE("single"),
  DOUBLE("double"),
  V_STYLE("v_style"),
  W_STYLE("w_style");

  private LineMark(String str) {
    _name = str;
  }

  public static LineMark getByKey(String str) {
    assert(str.equals("none")     ||
           str.equals("single")   ||
           str.equals("double")   ||
           str.equals("v_style")  ||
           str.equals("w_style"));

    if (str.equals("none"))
      return NONE;
    if (str.equals("single"))
      return SINGLE;
    if (str.equals("double"))
      return DOUBLE;
    if (str.equals("v_style"))
      return V_STYLE;
    if (str.equals("w_style"))
      return W_STYLE;

    return null;
  }

  public String getKey() {
    return _name;
  }

  /**
   * Human-readable name of instance.
   */
  private String _name;
}
