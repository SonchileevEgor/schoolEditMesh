package gui.extensions;

import static config.Config.EXTENSIONS;
import java.util.EnumMap;
import minjson.JsonObject;
import minjson.JsonValue;
import minjson.i_Jsonable;

/**
 * Класс, хранящий статус расширений (вкл / выкл).
 * @author alexeev
 */
public class ExtensionManager implements i_Jsonable {
  EnumMap<Extensions, Boolean> _status;
  public ExtensionManager() {
    _status = new EnumMap<>(Extensions.class);
    for( Extensions ext : Extensions.values() ){
      _status.put(ext, ext.isOnByDefault());
    }
  }
  
  public void changeStatus(Extensions ext, boolean newStatus){
    _status.put(ext, newStatus);
    saveState();
  }
  
  public boolean getStatus(Extensions ext){
    return _status.get(ext);
  }
  
  /**
   * Save state to configuration.
   */
  public final void saveState(){
    EXTENSIONS.setValue(toJson());
  }

  @Override
  public void fromJson(JsonValue v) {
    JsonObject obj = v.asObject();
    for( Extensions ext : Extensions.values() ){
      JsonValue state = obj.get(ext.getMachineName());
      if( state != null ){
        _status.put(ext, state.asBoolean());
      }
    }
  }

  @Override
  public JsonValue toJson() {
    JsonObject obj = new JsonObject();
    for( Extensions ext : Extensions.values() ){
      obj.add(ext.getMachineName(), _status.get(ext));
    }
    return obj;
  }
}
