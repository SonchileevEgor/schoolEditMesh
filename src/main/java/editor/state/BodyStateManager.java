package editor.state;

import editor.Editor;
import editor.i_BodyContainer;
import editor.i_FocusChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import minjson.*;
import opengl.colortheme.CurrentTheme;

/**
 * Manager of body display options.
 *
 * @author alexeev
 */
public class BodyStateManager implements i_FocusChangeListener {

  // map body ID -> body state

  private final Map<String, BodyState> _records = new HashMap<String, BodyState>();

  public BodyStateManager() {
  }

  public void add(String bodyID, BodyState state) {
    _records.put(bodyID, state);
  }

  public BodyState getState(String bodyID) {
    return _records.get(bodyID);
  }

  public void remove(String bodyID) {
    _records.remove(bodyID);
  }

  public void clear() {
    _records.clear();
  }

  /**
   * remove unused records from manager
   *
   * @param edt
   */
  public void refreshAndClean(Editor edt) {
    ArrayList<String> keysToRemove = new ArrayList<String>();
    for (String key: _records.keySet()) {
      if (!edt.bd().contains(key)) {
        keysToRemove.add(key);
      }
    }
    for (String key: keysToRemove) {
      _records.remove(key);
    }
  }

  /**
   * Apply current theme carcass color.
   *
   */
  public void updateCarcassColor() {
    for (Entry e: _records.entrySet()) {
      EntityState state = (EntityState)e.getValue();
      if (state.hasParam(DisplayParam.CARCASS_COLOR)) {
        state.setParam(DisplayParam.CARCASS_COLOR,
                CurrentTheme.getColorTheme().getCarcassFiguresColorGL());
      }
    }
  }

  /**
   * Apply current theme facets color.
   *
   */
  public void updateFacetsColor() {
    for (Entry e: _records.entrySet()) {
      EntityState state = (EntityState)e.getValue();
      if (state.hasParam(DisplayParam.FILL_COLOR)) {
        state.setParam(DisplayParam.FILL_COLOR,
                CurrentTheme.getColorTheme().getFacetsFiguresColorGL());
      }
    }
  }

  public void applyTheme() {
    updateFacetsColor();
    updateCarcassColor();
  }

  public boolean containsKey(String key) {
    return _records.containsKey(key);
  }

  /**
   * Convert to Json object.
   *
   * @param bodies
   * @return
   */
  public JsonArray toJson(i_BodyContainer bodies) {
    JsonArray result = new JsonArray();
    for (String key: _records.keySet()) {
      EntityState state = _records.get(key);
      JsonObject bd = new JsonObject();
      bd.add("id", key);
      bd.add("params", state.toJson());
      result.add(bd);
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
    for (JsonValue jval: json) {
      JsonObject jobj = jval.asObject();
      String id = jobj.get("id").asString();
      JsonObject params = jobj.get("params").asObject();
      BodyState state = new BodyState();
      state.fromJson(params);
      _records.put(id, state);
    }
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
    for (String bodyID: bodyIDs) {
      getState(bodyID).setChosen(false);
    }
  }

  @Override
  public void focusLost(String id, boolean isBody) {
    if (isBody) {
      getState(id).setChosen(false);
    }
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (isBody) {
      getState(id).setChosen(true);
    }
  }
}
