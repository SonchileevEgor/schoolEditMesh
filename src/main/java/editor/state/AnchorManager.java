package editor.state;

import editor.i_AnchorContainer;
import java.util.HashMap;
import java.util.Map;
import minjson.JsonArray;
import minjson.JsonObject;
import minjson.JsonValue;
import opengl.colortheme.CurrentTheme;

/**
 * Manager of anchor name and state distribution.
 *
 * @author alexeev
 */
public class AnchorManager {
  /**
   * Map anchor ID - anchor name.
   * Use StringBuilder because we need to pass titles by reference.
   * (Strings cannot be passed by reference)
   */
  private final HashMap<String, StringBuilder> _names = new HashMap<>();

  /**
   * Map anchor ID - anchor state.
   */
  private final HashMap<String, AnchorState> _states = new HashMap<>();

  public AnchorManager() {}

  public void add(String anchorID, StringBuilder anchorName, AnchorState anchorState) {
    _names.put(anchorID, anchorName);
    _states.put(anchorID, anchorState);
  }

  /**
   * Title value (string).
   * @param anchorID
   * @return
   */
  public String getTitleValue(String anchorID) {
    return _names.get(anchorID).toString();
  }

  /**
   * Reference to the title (StringBuilder).
   * @param anchorID
   * @return
   */
  public StringBuilder getTitleRef(String anchorID) {
    return _names.get(anchorID);
  }

  public AnchorState getState(String anchorID) {
    return _states.get(anchorID);
  }

  public void remove(String anchorID) {
    _names.remove(anchorID);
    _states.remove(anchorID);
  }

  public void clear() {
    _names.clear();
    _states.clear();
  }

  public boolean containsKey(String key) {
    return _names.containsKey(key);
  }

  public void applyTheme() {
    for (Map.Entry e : _states.entrySet()) {
      AnchorState state = (AnchorState)e.getValue();
      if (state.hasParam(DisplayParam.POINT_COLOR)) {
        state.setParam(DisplayParam.POINT_COLOR, CurrentTheme.getColorTheme().getPointsColorGL());
      }
      if (state.hasParam(DisplayParam.CARCASS_COLOR)) {
        state.setParam(DisplayParam.CARCASS_COLOR, CurrentTheme.getColorTheme().getCarcassFiguresColorGL());
      }
      if (state.hasParam(DisplayParam.FILL_COLOR)) {
        state.setParam(DisplayParam.FILL_COLOR, CurrentTheme.getColorTheme().getFacetsFiguresColorGL());
      }
    }
  }

  public JsonArray toJson(i_AnchorContainer anchors) {
    JsonArray result = new JsonArray();
    for (String key : _states.keySet()) {
      AnchorState state = _states.get(key);
      JsonObject anch = new JsonObject();
      anch.add("id", key);
      anch.add("title", _names.get(key).toString());
      anch.add("params", state.toJson());
      result.add(anch);
    }
    return result;
  }

  /**
   * Load from Json object.
   *
   * @param json
   */
  public void loadJson(JsonArray json) {
    clear();
    for (JsonValue jval : json) {
      JsonObject jobj = jval.asObject();
      String id = jobj.get("id").asString();
      String title = jobj.get("title").asString();
      JsonObject params = jobj.get("params").asObject();
      AnchorState state = new AnchorState();
      state.fromJson(params);
      _names.put(id, new StringBuilder(title));
      _states.put(id, state);
    }
  }
}