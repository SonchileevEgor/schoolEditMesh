package editor;

// Simple container of i_BodyBuilder objects.

import java.util.ArrayList;
import java.util.HashMap;

public class BodyBuilderContainer {
  private ArrayList<i_BodyBuilder> _builders;
  private HashMap<String, Integer> _map;

  public BodyBuilderContainer(){
    _builders = new ArrayList<i_BodyBuilder>();
    _map = new HashMap<String, Integer>();
  }

  public int len() {
    return _builders.size();
  }

  public i_BodyBuilder get (int idx) {
    // Here we return reference (not the copy)
    // in the reason of safety of interface i_BodyBuilder
    return _builders.get(idx);
  }

  public i_BodyBuilder get (String id) throws ExNoBuilder {
    if (_map.get(id) != null) {
      return _builders.get(_map.get(id));
    } else {
      throw new ExNoBuilder(id);
    }
  }

  public void remove (int idx) {
    _builders.remove(idx);
  }

  public void add(i_BodyBuilder bb){
    int idx = _builders.size();
    _builders.add(bb);
    _map.put(bb.id(), idx);
  }

  public void remove(String id) {
    Integer idx = _map.get(id);
    if (idx != null) {
      _builders.remove(idx.intValue());
      _map.remove(id);

      for (int i = idx; i < len(); i++) {
        _map.remove(get(i).id());
        _map.put(get(i).id(), i);
      }
    }
  }

  public ArrayList<i_BodyBuilder> get_all() {
    ArrayList<i_BodyBuilder> result = new ArrayList<i_BodyBuilder>();
    for (int idx = 0; idx < len(); idx++) {
      result.add(get(idx));
    }
    return result;
  }

  public void clear() {
    _builders.clear();
    _map.clear();
  }
};
