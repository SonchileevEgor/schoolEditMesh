package editor;

import bodies.BodyType;
import static config.Config.LOG_LEVEL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.Log;

/**
 * Simple container of i_Body objects.
 *
 * @author lt
 */
public class BodyContainer implements i_BodyContainer {
  private ArrayList<i_Body> _bodies;
  private Map<String, Integer> _map;

  public BodyContainer(){
    _bodies = new ArrayList<>();
    _map = new HashMap<>();
  }
  
  @Override
  public void add(i_Body bd){
    int idx = _bodies.size();
    _bodies.add(bd);
    _map.put(bd.id(), idx);
  }

  @Override
  public int size() {
    return _bodies.size();
  }

  @Override
  public i_Body get(int idx) {
    // Here we return reference (not the copy)
    // in the reason of safety of interface i_Body
    return _bodies.get(idx);
  }

  /**
   * get list of bodies titles
   * @return
   */
  @Override
  public ArrayList<String> getAllTitles() {
    ArrayList<String> result = new ArrayList<>();
    for (int idx = 0; idx < size(); idx++) {
      result.add(get(idx).getTitle());
    }
    return result;
  }

  @Override
  public i_Body get( String id ) throws ExNoBody {
    Integer result = _map.get(id);
    if( result == null ){
      if (LOG_LEVEL.value() >= 2) {
        Log.out.printf("BodyContainer: error: can't find <%s>%n", id);
        for( int j = 0; j < size(); j++ )
          Log.out.printf("  [%d]=<%s>%n", j, get(j).id());
      }
      throw new ExNoBody("");
    }
    else {
      return _bodies.get(result);
    }
  }

  @Override
  public ArrayList<i_Body> getAllBodies(){
    ArrayList<i_Body> result = new ArrayList<>();
    for (int idx = 0; idx < size(); idx++) {
      result.add(get(idx));
    }

    return result;
  }
  
  @Override
  public ArrayList<i_Body> getBodiesByIDs(List<String> ids) {
    ArrayList<i_Body> result = new ArrayList<>();
    for( String id : ids ){
      Integer idx = _map.get(id);
      if( idx != null ){
        result.add(_bodies.get(idx));
      }
    }

    return result;
  }

  @Override
  public ArrayList<i_Body> getBodiesByType(BodyType type) {
    ArrayList<i_Body> result = new ArrayList<>();
    for (int idx = 0; idx < size(); idx++) {
      i_Body bd = get(idx);
      if (bd.type() == type)
        result.add(bd);
    }
    return result;
  }
  
  @Override
  public String getBodyIdByTitle(String title) throws ExNoBody {
    for (i_Body body : _bodies) {
      if (body.getTitle().equals(title))
        return body.id();
    }
    throw new ExNoBody("Нет тела с названием <" + title + ">");
  }

  @Override
  public boolean contains(String bodyID) {
    return _map.containsKey(bodyID);
  }

  @Override
  public boolean hasTitle(String title) {
    for (i_Body body : _bodies) {
      if (body.getTitle().equals(title))
        return true;
    }
    return false;
  }

  @Override
  public ArrayList<i_Body> getPolygonBodies() {
    ArrayList<i_Body> result = new ArrayList<>();
    for (i_Body bd : _bodies) {
      if (bd.type().isPolygon())
        result.add(bd);
    }
    return result;
  }

  @Override
  public void clear() {
    _bodies.clear();
    _map.clear();
  }

  @Override
  public ArrayList<String> getBodyIDs() {
    return new ArrayList<>(_map.keySet());
  }
};
