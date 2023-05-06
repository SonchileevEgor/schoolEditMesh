package editor.state;

import java.util.HashMap;
import java.util.Map.Entry;
import minjson.JsonObject;
import minjson.JsonValue;
import minjson.i_Jsonable;

/**
 * Набор свойств объекта сцены (якоря или тела).
 * @author alexeev
 */
public abstract class EntityState implements i_Jsonable {
  protected HashMap<DisplayParam, Object> _values;
  protected DisplayParamSet _params;

  public EntityState() {
    _params = new DisplayParamSet();
    _values = new HashMap<>();
  }
  
  /**
   * Create entity (anchor or body) state.
   * @param dpSet set of parameters.
   */
  public EntityState(final DisplayParamSet dpSet) {
    _params = dpSet;
    _values = new HashMap<DisplayParam, Object>();

    // fill default values
    for (DisplayParam dp : dpSet) {
      _values.put(dp, dp.getDefaultValue());
    }
  }

  public EntityState(final DisplayParamSet set, JsonObject jo) {
    this(set);
    for (DisplayParam p : DisplayParam.values()){
      JsonValue value = jo.get(p.getKey());
      if (value != null) {
        setParam(p, p.getValue(value));
      }
    }
  }

  /**
   * Set of existing parameters.
   * @return
   */
  public DisplayParamSet getParamSet() {
    return _params;
  }

  /**
   * Return value of parameter, if exists.
   * Otherwise, return default value for this parameter.
   * @param parName
   * @return
   */
  public Object getParam(DisplayParam parName) {
    Object result = _values.get(parName);
    return (result == null) ? parName.getDefaultValue() : result;
  }

  /**
   * Set parameter value.
   * @param parName
   * @param value
   */
  public final void setParam(DisplayParam parName, Object value) {
    _values.put(parName, value);
  }

  public boolean hasParam(DisplayParam parName) {
    return (_params.contains(parName));
  }

  public boolean isVisible() {
    if (!hasParam(DisplayParam.VISIBLE)) return false;

    return (boolean)_values.get(DisplayParam.VISIBLE);
  }

  public boolean isChosen() {
    if (!hasParam(DisplayParam.CHOSEN)) return false;

    return (boolean)_values.get(DisplayParam.CHOSEN);
  }

  /**
   * Set entity visible.
   * If entity has visibility option.
   *
   * @param visible
   */
  public void setVisible(boolean visible) {
    setParam(DisplayParam.VISIBLE, visible);
  }

  public void setChosen(boolean chosen) {
    setParam(DisplayParam.CHOSEN, chosen);
  }

  public JsonValue toJson() {
    JsonObject obj = new JsonObject();
    for (Entry e : _values.entrySet()) {
      DisplayParam d = (DisplayParam)e.getKey();
      obj.add(d.getKey(), d.toJson(e.getValue()));
    }
    return obj;
  }
  
  public void fromJson(JsonValue v) {
    _params.clear();
    _values.clear();
    JsonObject obj = v.asObject();
    for (DisplayParam p : DisplayParam.values()) {
      JsonValue value = obj.get(p.getKey());
      if (value != null) {
        _params.add(p);
        _values.put(p, p.getValue(value));
      }
    }
  }
}