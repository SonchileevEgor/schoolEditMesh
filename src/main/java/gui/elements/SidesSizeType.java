package gui.elements;

/**
 *
 * @author rita
 */
public enum SidesSizeType {
  SHORT_SHORT("short_short"),
  LONG_LONG("long_long"),
  SHORT_LONG("short_long"),
  LONG_SHORT("long_short");

  private SidesSizeType(String key){
    _key = key;
  }

  public String getKey(){
    return _key;
  }

  public static SidesSizeType getByKey(String key) {
    for (SidesSizeType item : values()){
      if (key.equals(item.getKey())){
        return item;
      }
    }
    throw new RuntimeException("Error: sides type <" + key + "> is not supported");
  }

  private String _key;
}
