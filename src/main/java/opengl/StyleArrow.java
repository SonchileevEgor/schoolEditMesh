package opengl;

/**
 * Created by maks on 23.06.14.
 */
public enum StyleArrow {
  NONE("none"),
  CONE("cone"),
  LINES("lines");

  private String _key;

  public static StyleArrow getDefault() {
    return NONE;
  }

  StyleArrow( String key ){
    _key = key;
  }

  public String getKey() {
    return _key;
  }

  public static StyleArrow getByKey(String str) {
    if (str.equals("cone"))
      return CONE;
    if (str.equals("lines"))
      return LINES;
    if (str.equals("none"))
      return NONE;

    throw new AssertionError("Error: type of arrow <" + str + "> is not supported");
  }
}
