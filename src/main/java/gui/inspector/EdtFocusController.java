package gui.inspector;

import bodies.BodyType;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Body;
import editor.i_FocusChangeListener;
import editor.state.AnchorState;
import editor.state.BodyState;
import editor.state.DisplayParamSet;
import gui.EdtController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import maquettes.CoverContainer;
import maquettes.i_Cover;

/**
 * Focus and highlighting controller.
 *
 * @author alexeev
 */
public final class EdtFocusController {

  private EdtController _ctrl;
  /**
   * Focused anchors.
   */
  private List<String> _focusedAnchors = new ArrayList<>();

  /**
   * Focused bodies.
   */
  private List<String> _focusedBodies = new ArrayList<>();

  /**
   * Focused covers.
   */
  private List<String> _focusedCovers = new ArrayList<>();

  /**
   * Highlighted anchors. Subset of _focusedAnchors.
   */
  private List<String> _hlAnchors = new ArrayList<>();

  /**
   * Highlighted bodies. Subset of _focusedBodies.
   */
  private List<String> _hlBodies = new ArrayList<>();

  /**
   * Highlighted covers. Subset of _focusedBodies.
   */
  private List<String> _hlCovers = new ArrayList<>();

  private List<i_FocusChangeListener> _listeners = new ArrayList<>();

  public EdtFocusController(EdtController ctrl) {
    _ctrl = ctrl;
    addFocusChangeListener(_ctrl.getEditor().bodyMgr());
  }

  public List<String> getFocusedBodies() {
    return _focusedBodies;
  }

  public List<String> getFocusedAnchors() {
    return _focusedAnchors;
  }

  public List<String> getFocusedCovers() {
    return _focusedCovers;
  }

  public String getLastFocusedBody() {
    if (_focusedBodies.isEmpty()) {
      return null;
    } else {
      return _focusedBodies.get(_focusedBodies.size() - 1);
    }
  }

  public String getLastFocusedRemovableBody() {
    if (!_focusedBodies.isEmpty()) {
      for (int i = _focusedBodies.size() - 1; i >= 0; i--) {
        try {
          String id = _focusedBodies.get(i);
          if (_ctrl.getBody(id).hasBuilder()) {
            return id;
          }
        } catch (ExNoBody ex) {
        }
      }
    }
    return null;
  }

  public boolean contains(String bodyID) {
    return _focusedBodies.contains(bodyID);
  }

  /**
   * Пуст ли список тел.
   *
   * @return
   */
  public boolean isEmpty() {
    return _focusedBodies.isEmpty();
  }

  /**
   * Подсвечиваем тело, не фокусируясь на нём.
   *
   * @param id
   */
  public void highlightBody(String id) {
    clearHighlight();

    if (_ctrl.containsBody(id)) {
      _hlBodies.add(id);
      try {
        i_Body b = _ctrl.getBody(id);
        b.getState().setChosen(true);
        for (String anchorID: _ctrl.getBody(id).getAnchors().values()) {
          try {
            _ctrl.getAnchor(anchorID).getState().setChosen(true);
            _hlAnchors.add(anchorID);
          } catch (ExNoAnchor ex) {
          }
        }
      } catch (ExNoBody ex) {
      }
    }
  }

  /**
   * Clear highlight from highlighted but not focused bodies and anchors.
   */
  public void clearHighlight() {
    for (String bodyID: _hlBodies) {
      BodyState state = _ctrl.getEditor().bodyMgr().getState(bodyID);
      if (state != null && !_focusedBodies.contains(bodyID)) {
        // if body is focused, we don't need to clear highlight
        state.setChosen(false);
      }
    }
    for (String anchorID: _hlAnchors) {
      AnchorState state = _ctrl.getEditor().anchMgr().getState(anchorID);
      if (state != null && !_focusedAnchors.contains(anchorID)) {
        state.setChosen(false);
      }
    }
    for (i_Cover cover: _ctrl.getMeshes()) {
      if (cover.getChosen() && !_focusedCovers.contains(cover.id())) {
        cover.setChosen(false);
      }
    }

    _hlCovers.clear();
    _hlBodies.clear();
    _hlAnchors.clear();
    _ctrl.redraw();
  }

  public void highlightCover(String id) {
    clearHighlight();

    CoverContainer cc = new CoverContainer((List<i_Cover>)_ctrl.getMeshes());
    if (cc.contains(id)) {
      _hlCovers.add(id);
      i_Cover cover = _ctrl.getCover(id);
      cover.setChosen(true);
    }
  }

  public void setFocusOnCover(String id) {
    clearFocus();

    if (_ctrl.containsCover(id)) {
      _focusedCovers.add(id);
      notifyFocusChange(id, false, true);
    }
  }

  /**
   * Toggle focus state on body.
   *
   * @param id
   * @return is body focused now.
   */
  public boolean toggleFocusOnBody(String id) {
    boolean hasFocus = _focusedBodies.contains(id);
    if (hasFocus) {
      removeFocusFromBody(id);
    } else {
      addFocusOnBody(id);
    }
    return !hasFocus;
  }

  public boolean toggleFocusOnCover(String id) {
    boolean hasFocus = _focusedCovers.contains(id);
    if (hasFocus) {
      removeFocusFromCover(id);
    } else {
      setFocusOnCover(id);
    }
    return !hasFocus;
  }

  /**
   * Take off focus from body.
   *
   * @param id
   */
  public void removeFocusFromBody(String id) {
    // если тело не в фокусе, ничего не делаем
    if (!_focusedBodies.contains(id)) {
      return;
    }

    _focusedBodies.remove(id);

    if (_ctrl.containsBody(id)) {
      try {
        for (String anchorID: _ctrl.getBody(id).getAnchors().values()) {
          if (_focusedAnchors.contains(anchorID)) {
            // Если якорь принадлежит её какому-то телу в фокусе,
            // то с него фокус не снимаем.
            boolean belongsToAnotherFocusedBody = false;
            for (String bodyID: _focusedBodies) {
              try {
                if (_ctrl.getBody(bodyID).getAnchors().containsValue(anchorID)) {
                  belongsToAnotherFocusedBody = true;
                  break;
                }
              } catch (ExNoBody ex) {
              }
            }
            if (!belongsToAnotherFocusedBody) {
              _ctrl.getAnchor(anchorID).getState().setChosen(false);
              _focusedAnchors.remove(anchorID);
              notifyFocusChange(anchorID, false, false);
            }
          }
        }
      } catch (ExNoBody | ExNoAnchor ex) {
      }
      notifyFocusChange(id, true, false);
    }
  }

  public void removeFocusFromCover(String id) {
    if (!_focusedCovers.contains(id)) {
      return;
    }

    _focusedCovers.remove(id);
    notifyFocusChange(id, false, true);
  }

  /**
   * Apply focus on body. Another bodies don't lose focus.
   *
   * @param id
   */
  public void addFocusOnBody(String id) {
    if (_ctrl.containsBody(id) && !_focusedBodies.contains(id)) {
      try {
        for (String anchorID: _ctrl.getBody(id).getAnchors().values()) {
          _ctrl.getAnchor(anchorID).getState().setChosen(true);
          _focusedAnchors.add(anchorID);
          notifyFocusChange(anchorID, false, true);
        }
      } catch (ExNoBody | ExNoAnchor ex) {
      }
      _focusedBodies.add(id);
      notifyFocusChange(id, true, true);
    }
  }

  /**
   * Apply focus on body. Another bodies lose focus.
   *
   * @param id
   */
  public void setFocusOnBody(String id) {
    clearFocus();

    if (_ctrl.containsBody(id)) {
      try {
        for (String anchorID: _ctrl.getBody(id).getAnchors().values()) {
          _ctrl.getAnchor(anchorID).getState().setChosen(true);
          _focusedAnchors.add(anchorID);
          notifyFocusChange(anchorID, false, true);
        }
      } catch (ExNoBody | ExNoAnchor ex) {
      }
      _focusedBodies.add(id);
      notifyFocusChange(id, true, true);
    }
  }

  /**
   * Take off focus from all anchors and bodies.
   */
  public void clearFocus() {
    notifyFocusCleared();

    _focusedCovers.clear();
    _focusedBodies.clear();
    _focusedAnchors.clear();
  }

  /**
   * Множество общих параметров тел в фокусе.
   *
   * @return
   */
  public DisplayParamSet getCommonParams() {
    DisplayParamSet result = new DisplayParamSet();

    if (_focusedBodies.isEmpty()) {
      return result;
    }

    Iterator<String> i = _focusedBodies.iterator();
    String bodyID = i.next();
    try {
      result = _ctrl.getBody(bodyID).getState().getParamSet();
      while (i.hasNext()) {
        bodyID = i.next();
        result = result.intersect(_ctrl.getBody(bodyID).getState().getParamSet());
      }
    } catch (ExNoBody ex) {
    }

    return result;
  }

  /**
   * Get BodyTree branch type, where body is located.
   *
   * @param bodyID
   * @return
   */
  private int getBranchType(String bodyID) {
    try {
      return _ctrl.getBody(bodyID).type().getBranchType();
    } catch (ExNoBody ex) {
    }
    return 0;
  }

  /**
   * Get body ID, on which we focus after focused bodies are deleted. If there is non-focused body
   * of the same type with the last focused removable body (lfb), we choose the lowest body in tree,
   * that is higher than lfb, or the closest body to lfb (if lfb is the highest of its type).
   *
   * @return
   */
  public String getBodyIDToFocus() {
    String lastBodyID = getLastFocusedRemovableBody();
    String result = null;
    if (lastBodyID != null) {
      boolean lfbExceeded = false;
      int typeIndex = getBranchType(lastBodyID);
      for (i_Body bd: _ctrl.getBodies()) {
        if (bd.type().getBranchType() == typeIndex) {
          if (!_focusedBodies.contains(bd.id())) {
            if (lfbExceeded) {
              return bd.id();
            } else {
              result = bd.id();
            }
          } else if (lastBodyID.equals(bd.id())) {
            if (result != null) {
              return result;
            } else {
              lfbExceeded = true;
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * Являются ли все тела в фокусе точками. Если нет тел в фокусе - возвращаем false.
   *
   * @return
   */
  public boolean arePoints() {
    if (_focusedBodies.isEmpty()) {
      return false;
    }
    for (String bodyID: _focusedBodies) {
      try {
        if (_ctrl.getBody(bodyID).type() != BodyType.POINT) {
          return false;
        }
      } catch (ExNoBody ex) {
        return false;
      }
    }
    return true;
  }

  /**
   * Есть ли среди тел с фокусом тело, которое можно удалить.
   *
   * @return
   */
  public boolean containsRemovableBody() {
    if (_focusedBodies.isEmpty()) {
      return false;
    }
    for (String bodyID: _focusedBodies) {
      try {
        if (_ctrl.getBody(bodyID).hasBuilder()) {
          return true;
        }
      } catch (ExNoBody ex) {
      }
    }
    return false;
  }

  /**
   * Есть ли среди тел с фокусом несуществующее тело.
   *
   * @return
   */
  public boolean containsNonExistsBody() {
    if (_focusedBodies.isEmpty()) {
      return false;
    }
    for (String bodyID: _focusedBodies) {
      try {
        if (!_ctrl.getBody(bodyID).exists()) {
          return true;
        }
      } catch (ExNoBody ex) {
        return true;
      }
    }
    return false;
  }

  /**
   * Есть ли среди тел с фокусом тело, имеющее поверхность.
   *
   * @return
   */
  public boolean containsBodyWithSurface() {
    if (_focusedBodies.isEmpty()) {
      return false;
    }
    for (String bodyID: _focusedBodies) {
      try {
        if (_ctrl.getBody(bodyID).type().hasSurface()) {
          return true;
        }
      } catch (ExNoBody ex) {
      }
    }
    return false;
  }

  /**
   * Все ли тела имеют поверхность.
   *
   * @return
   */
  public boolean areBodiesWithSurface() {
    if (_focusedBodies.isEmpty()) {
      return false;
    }
    for (String bodyID: _focusedBodies) {
      try {
        if (!_ctrl.getBody(bodyID).type().hasSurface()) {
          return false;
        }
      } catch (ExNoBody ex) {
      }
    }
    return true;
  }

  /**
   * У всех ли тел есть вершины.
   *
   * @return
   */
  public boolean areBodiesWithPoints() {
    if (_focusedBodies.isEmpty()) {
      return false;
    }
    for (String bodyID: _focusedBodies) {
      try {
        BodyType t = _ctrl.getBody(bodyID).type();
        if (!t.isPolygon() && !t.isPolyhedron()) {
          return false;
        }
      } catch (ExNoBody ex) {
      }
    }
    return true;
  }

  /**
   * Все ли тела имеют каркас.
   *
   * @return
   */
  public boolean areBodiesWithCarcass() {
    if (_focusedBodies.isEmpty()) {
      return false;
    }
    for (String bodyID: _focusedBodies) {
      try {
        if (!_ctrl.getBody(bodyID).type().hasCarcass()) {
          return false;
        }
      } catch (ExNoBody ex) {
      }
    }
    return true;
  }

  /**
   * Register focus change listener.
   *
   * @param listener
   */
  public void addFocusChangeListener(i_FocusChangeListener listener) {
    _listeners.add(listener);
  }

  /**
   * Unregister focus change listener.
   *
   * @param listener
   */
  public void removeFocusChangeListener(i_FocusChangeListener listener) {
    _listeners.remove(listener);
  }

  /**
   * Оповещаем слушателей об обновлении фокуса.
   *
   * @param id ID тела / якоря
   * @param isBody
   * @param enable true, если тело получило фокус; false - если потеряло.
   */
  public void notifyFocusChange(String id, boolean isBody, boolean enable) {
    if (id == null) {
      return; // да, такое может быть.
    }
    /**
     * В методе focusApply может происходить удаление слушателя из списка. Во избежание
     * ConcurrentModificationException производим перебор по элементам копии списка.
     */
    ArrayList<i_FocusChangeListener> listenersClone = new ArrayList<>(_listeners);

    if (enable) {
      for (i_FocusChangeListener l: listenersClone) {
        l.focusApply(id, isBody);
      }
    } else {
      for (i_FocusChangeListener l: listenersClone) {
        l.focusLost(id, isBody);
      }
    }
  }

  public void notifyFocusCleared() {
    for (i_FocusChangeListener l: new ArrayList<>(_listeners)) {
      l.focusCleared(new ArrayList<>(_focusedBodies));
    }
  }
}
