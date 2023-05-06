package gui.mode.adapters;

import bodies.BodyType;
import builders.PointBuilder;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import static config.Config.PRECISION;
import editor.i_Body;
import geom.ExDegeneration;
import geom.Plane3d;
import geom.Ray3d;
import geom.Vect3d;
import gui.EdtController;
import gui.MainEdtCanvasController;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.swing.SwingUtilities;
import opengl.scenegl.Scene2d;
import util.Util;

/**
 * Highlight / focus adapter.
 *
 * @author alexeev
 */
public class HighlightAdapter extends MainEdtMouseAdapter {
  /**
   * Указываем, какие якоря и тела мы можем выбрать.
   */
  ArrayList<BodyType> _types;

  /**
   * Указываем, тела с какими ID можно выделять.
   */
  ArrayList<String> _allowedIDs;

  /**
   * ID тела, по которому был совершён последний клик.
   * Если ни в кого не попали - возвращаем null.
   */
  String _selectedID;

  /**
   * Точка, соответствующая последнему клику по телу.
   * Если ни в кого не попали - возвращаем null.
   */
  Vect3d _selectedPoint;

  /**
   * Adapter mode.
   * if true, point will be created after click.
   */
  boolean _isCreatePointModeOn;

  /**
   * Режим выбора тел по ID.
   * Стандартный режим - выбор тел по типу,
   * если флаг включён, то работает режим выбора по ID.
   */
  boolean _isSelectionByIDModeOn;

  Vect3d _newPoint;

  /**
   *
   * @param ctrl
   * @param canvas
   *    Адаптер создаётся до привязки контроллера полотна к контроллеру приложения,
   *    поэтому в конструктор они передаются отдельно.
   */
  public HighlightAdapter(EdtController ctrl, MainEdtCanvasController canvas) {
    super(ctrl, canvas);
    _types = new ArrayList<>();
    _allowedIDs = new ArrayList<>();
    _types.add(BodyType.POINT);
    _selectedID = null;
    _selectedPoint = null;
    _newPoint = null;
  }

  /**
   * Включить режим создания точек по клику.
   * Актуально только для 2D.
   * @param isCreatePointModeOn
   */
  public void setCreatePointMode(boolean isCreatePointModeOn) {
    _isCreatePointModeOn = isCreatePointModeOn;
  }

  /**
   * Включить режим выбора тел по ID.
   * @param isSelectionByIDModeOn
   */
  public void setAllowedSelectionByIDMode(boolean isSelectionByIDModeOn) {
    _isSelectionByIDModeOn = isSelectionByIDModeOn;
  }

  @Override
  public void reset() {
    super.reset();
    _isCreatePointModeOn = false;
    _isSelectionByIDModeOn = false;
    _allowedIDs.clear();
    setTypes(BodyType.POINT);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (_block) return;
    boolean isLMB = SwingUtilities.isLeftMouseButton(e);
    boolean isRMB = SwingUtilities.isRightMouseButton(e);
    if (!isLMB && !isRMB) return;
    _selectedID = getSelectedID(e.getX(), e.getY());

    // Create point if _createPointModeOn is true.
    if (_selectedID == null && isLMB && _isCreatePointModeOn) {
      if (_ctrl.getScene().is3d()) {

        //!! TODO: add code for 3d

      } else {
        try {
          _newPoint = getVect(e.getX(), e.getY());
          if (_canvas.getScene().getViewVolume().inViewingCube(_newPoint)) {
            _selectedID = createPoint(_newPoint);
          }
        } catch (ExDegeneration ex) {}
      }
    }
    if (_selectedID != null) {
      if (isLMB) {
        _ctrl.getFocusCtrl().setFocusOnBody(_selectedID);
        _ctrl.redraw();
      }
      if (isRMB) {
        _ctrl.showBodyContextMenu(_selectedID, (Component)e.getSource(), e.getX(), e.getY());
      }
    }
  }

  /**
   * Create point on scene.
   * @param p
   * @return body ID
   */
  protected final String createPoint(Vect3d p) {
    PointBuilder builder = new PointBuilder(BodyType.POINT.getName(_ctrl.getEditor()));
    builder.setValue(PointBuilder.BLDKEY_P, p);
    _ctrl.add(builder, null, false);
    return builder.id();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (_block) return;

    String selectedID = getSelectedID(e.getX(), e.getY());
    if (selectedID != null) {
      // Выделяем тело, не фокусируясь на нём.
      if (!selectedID.equals(_selectedID)) {
        _selectedID = selectedID;
        _ctrl.getFocusCtrl().highlightBody(_selectedID);
      }
    } else {
        _selectedID = null;
        _ctrl.getFocusCtrl().clearHighlight();
    }


    if (_isCreatePointModeOn && !_ctrl.getMainCanvasCtrl().is3d()) {
      try {
        // show new point coords
        _newPoint = getVect(e.getX(), e.getY());
        _ctrl.status().showValue(_newPoint.toString(PRECISION.value(), _ctrl.getScene().is3d()));
      } catch (ExDegeneration ex) {}
    }
  }

  /**
   * ID тела по координатам указателя мыши.
   * Обновляется значение поля selectedPoint.
   * @param mouseX
   * @param mouseY
   * @return ID, если попали в тело, иначе null.
   */
  private String getSelectedID(int mouseX, int mouseY) {
    String selectedID = null;
    _selectedPoint = null;

    HashMap<String, Vect3d> intersectPts = new HashMap<>();
    final HashMap<String, Integer> priorities = new HashMap<>();
    try {
      Ray3d sight = _canvas.getSightRay(mouseX, mouseY);
      Iterable<i_Body> bodies = _isSelectionByIDModeOn ?
              _ctrl.getBodiesByIDs(_allowedIDs) :
              _ctrl.getBodiesByTypes(_types);
      for( i_Body bd : bodies ) {
        // Интересуют только существующие видимые тела.
        if( !bd.exists() || !_ctrl.isBodyVisible(bd.id()) )
          continue;
        Vect3d pt = bd.intersectWithRay(_canvas.getScene().getRender(), sight, mouseX, mouseY);
        if( pt != null ){
          if (_canvas.isInViewingCube(pt)){
            intersectPts.put(bd.id(), pt);
            priorities.put(bd.id(), bd.type().choosePriority);
          }
        }
      }
    } catch (ExDegeneration ex) {}

    if( intersectPts.isEmpty() ){
      return selectedID;
    }

    // выбираем тела с максимальным приоритетом
    final int maxPriority = Collections.max(priorities.values());

    Map<String, Vect3d> filteredPts = Maps.filterKeys(intersectPts, new Predicate<String>(){
      @Override
      public boolean apply(String input) {
        return priorities.get(input) == maxPriority;
      }
    });

    if( _isSelectionByIDModeOn ){
      // Оставляем тела только с допустимыми ID.
      filteredPts.keySet().retainAll(_allowedIDs);
    }

    // выбираем ближайшую точку
    try {
      selectedID = Collections.min(filteredPts.entrySet(),
                Util.getComparator(_canvas.getScene().getCameraPosition().eye())).getKey();
    } catch( NoSuchElementException ex ){}

    if( selectedID != null ){
      _selectedPoint = filteredPts.get(selectedID);
    }

    return selectedID;
  }

  public Vect3d getVect(double mouseX, double mouseY) throws ExDegeneration {
    // show new point coords
    Ray3d sight = _canvas.getSightRay(mouseX, mouseY);
    Plane3d viewPlane = ((Scene2d)_canvas.getScene()).getViewPlane();
    Vect3d result = sight.intersectionWithPlane(viewPlane);

    /**
      * Если включен режим притягивания к сетке,
      * округляем координаты точки.
      */
    if (_mainCanvas.isMagnet()) {
      result = new Vect3d(Util.round(result.x(), _canvas.getMeshSize()),
                     Util.round(result.y(), _canvas.getMeshSize()), 0);
    }
    return result;
  }

  /**
   * ID of selected body.
   * @return
   */
  public String id() {
    return _selectedID;
  }

  /**
   * Point of selected body.
   * @return
   */
  public Vect3d point() {
    return _selectedPoint;
  }

  /**
   * Set types available to focus / highlight.
   * @param types
   */
  public void setTypes(ArrayList<BodyType> types) {
    _types.clear();
    _types.addAll(types);
  }

  public void setTypes(BodyType... types) {
    _types.clear();
    _types.addAll(Arrays.asList(types));
  }

  /**
   * Установить допустимые ID тел для выбора.
   * Метод не устанавливает автоматически режим выбора тел по ID.
   * Его нужно вызывать явно.
   * @param ids
   */
  public void setAllowedIDs(Collection<String> ids){
    _allowedIDs.clear();
    _allowedIDs.addAll(ids);
  }

  /**
   * Установить допустимые ID тел для выбора.
   * Метод не устанавливает автоматически режим выбора тел по ID.
   * Его нужно вызывать явно.
   * @param ids
   */
  public void setAllowedIDs(String... ids){
    _allowedIDs.clear();
    _allowedIDs.addAll(Arrays.asList(ids));
  }
}
