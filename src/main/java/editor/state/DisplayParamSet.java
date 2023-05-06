package editor.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author alexeev
 */
public class DisplayParamSet implements Iterable<DisplayParam>{
  private ArrayList<DisplayParam> _params;

  public DisplayParamSet() {
    _params = new ArrayList<DisplayParam>();
  }

  public DisplayParamSet(DisplayParam... params) {
    _params = new ArrayList<>(Arrays.asList(params));
  }

  public DisplayParamSet(List<DisplayParam> params) {
    _params = new ArrayList<>(params);
  }

  public List<DisplayParam> params() {
    return new ArrayList<DisplayParam>(_params);
  }

  public boolean contains(DisplayParam d) {
    return _params.contains(d);
  }

  public void add(DisplayParam d) {
    _params.add(d);
  }

  public void remove(DisplayParam d) {
    _params.remove(d);
  }

  public void removeAll(DisplayParamSet d) {
    _params.removeAll(d._params);
  }

  public void clear() {
    _params.clear();
  }

  public DisplayParamSet intersect(DisplayParamSet d) {
    ArrayList<DisplayParam> intersection = new ArrayList<DisplayParam>(_params);
    intersection.removeAll(d.params());
    return new DisplayParamSet(intersection);
  }

  public boolean isEmpty() {
    return _params.isEmpty();
  }

  @Override
  public Iterator<DisplayParam> iterator() {
    return _params.iterator();
  }
}
