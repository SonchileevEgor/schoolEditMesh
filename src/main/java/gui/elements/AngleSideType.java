package gui.elements;

/**
 * Type of angle side drawing.
 * @author alexeev
 */
public enum AngleSideType {
  DEFAULT("default"),
  SEGMENT("segment"),
  LINE("ray");

  private AngleSideType(String key){
    _key = key;
  }

  public String getKey(){
    return _key;
  }

  public static AngleSideType getByKey(String key){
    for (AngleSideType item : values()){
      if (key.equals(item.getKey())){
        return item;
      }
    }
    throw new RuntimeException("Error: type of line <" + key + "> is not supported");
  }

  private String _key;
}
