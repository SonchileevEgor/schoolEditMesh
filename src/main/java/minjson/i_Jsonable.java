package minjson;

/**
 * Interface of class, which admits save / load from Json file.
 * @author alexeev
 */
public interface i_Jsonable {
  /**
   * Load class state from Json.
   * @param v 
   */
  void fromJson(JsonValue v);
  
  /**
   * Save class state to Json.
   * @return 
   */
  JsonValue toJson();
}
