package maquettes;

import editor.i_FocusChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Kurgansky
 */
public class CoverContainer implements i_FocusChangeListener {

  private final ArrayList<i_Cover> _covers;

  public CoverContainer() {
    _covers = new ArrayList<>();
  }

  public CoverContainer(List<i_Cover> covers) {
    _covers = new ArrayList<>();
    for (i_Cover cover: covers) {
      _covers.add(cover);
    }
  }

  public void add(i_Cover cover) {
    _covers.add(cover);
  }

  public int size() {
    return _covers.size();
  }

  public i_Cover get(int indx) {
    return _covers.get(indx);
  }

  public i_Cover get(String id) {
    for (i_Cover cover: _covers) {
      if (cover.id().equals(id)) {
        return cover;
      }
    }
    return null;
  }

  public int contains(i_Cover cover) {
    return _covers.contains(cover) ? _covers.indexOf(cover) : -1;
  }

  public ArrayList<i_Cover> getCovers() {
    return _covers;
  }

  public void clear() {
    _covers.clear();
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
    for (String id: bodyIDs) {
      get(id).setChosen(false);
    }
  }

  @Override
  public void focusLost(String id, boolean isBody) {
    i_Cover cover = this.get(id);
    cover.setChosen(false);
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    i_Cover cover = this.get(id);
    cover.setChosen(true);
  }

  public boolean contains(String id) {
    return (this.get(id) != null);

  }
}
