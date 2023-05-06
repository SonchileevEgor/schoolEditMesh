package gui;

import bodies.BodyType;
import bodies.PointBody;
import builders.PointBuilder;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import static config.Config.GUI_EPS;
import editor.ExBadRef;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.ExNoBuilder;
import editor.i_Anchor;
import editor.i_Body;
import geom.Vect3d;
import gui.mode.DefaultScreenMode;
import gui.mode.ScreenMode;
import gui.mode.adapters.DefaultKeyAdapter;
import gui.mode.adapters.HighlightAdapter;
import gui.mode.i_ModeChangeListener;
import gui.mode.i_ModeParamChangeListener;
import gui.mode.param.CreateModeParamType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import util.Util;

/**
 * Контроллер для главного полотна приложения.
 *
 * @author alexeev
 */
public class MainEdtCanvasController extends BasicEdtCanvasController {
  private final EdtController _ctrl;
  private final MainEdtCanvas _mainCanvas; // дублируем поле для обращения без приведения типа
  private ScreenMode _mode;
  private boolean _magnet = false;

  /**
   * Permanent listeners.
   */
  private final ArrayList<i_ModeParamChangeListener> _paramChangeListeners;
  private final HighlightAdapter _highlightAdapter;
  private final DefaultKeyAdapter _defaultKeyAdapter;

  // Предикат, проверяющий, является ли тело с данным ID перемещаемой точкой.
  private final Predicate<String> movablePoints = new Predicate<String>() {
    @Override
    public boolean apply(String t) {
      try {
        return ((PointBody)_ctrl.getBody(t)).isMovable();
      } catch( ExNoBody ex ){
        return false;
      }
    }
  };

  public MainEdtCanvasController(EdtController ctrl, MainEdtCanvas canvas) {
    super(canvas);
    _ctrl = ctrl;
    _mainCanvas = canvas;
    _paramChangeListeners = new ArrayList<i_ModeParamChangeListener>();
    _defaultKeyAdapter = new DefaultKeyAdapter(_ctrl);
    _highlightAdapter = new HighlightAdapter(_ctrl, this);
  }

  public MainEdtCanvas getMainCanvas(){
    return _mainCanvas;
  }

  @Override
  public void initMouseListeners() {
    super.initMouseListeners();
    addMouseListener(_highlightAdapter);
    addKeyListener(_defaultKeyAdapter);
  }

  public HighlightAdapter getHighlightAdapter() {
    return _highlightAdapter;
  }

  public DefaultKeyAdapter getDefaultKeyAdapter() {
    return _defaultKeyAdapter;
  }

  public boolean isMagnet() {
    return _magnet;
  }

  public void setMagnet(boolean m) {
    _magnet = m;
  }

  public void toggleManget() {
    _magnet = !_magnet;
  }

  /**
   * Set a screen mode
   * that defines listening mouse or keyboard events on canvas.
   * @param mode
   */
  public void setMode(ScreenMode mode) {
    if (_mode != null)
      _mode.dispose();
    _mode = mode;
    _mode.run();
    notifyModeChange(mode);
  }

  public void setDefaultMode() {
    setMode(new DefaultScreenMode(_ctrl));
  }

  /**
   * List of anchors built by PointBuilder. ("Basic" points)
   *
   * @return
   */
  public ArrayList<i_Anchor> getBasicPoints() {
    ArrayList<i_Anchor> result = new ArrayList<>();
    for (i_Body b : _ctrl.getBodies()) {
      // Если точка построена по набору координат, то её можно дёргать
      try {
        if (b.type() == BodyType.POINT && _ctrl.getBuilder(b.id()).alias().equals(PointBuilder.ALIAS)) {
          try {
            result.add(_ctrl.getAnchor(b.getAnchorID("P")));
          } catch (ExNoAnchor ex) {}
        }
      } catch (ExNoBuilder ex) {}
    }
    return result;
  }

  /**
   * Ближайшая перемещаемая точка (тело), которая находится на экране в точке (x, y).
   * @param x
   * @param y
   * @return ID ближайшей точки, либо null
   */
  public String getNearestMovablePoint(int x, int y) {
    HashMap<String, Vect3d> points = getPickedPointBodies(x, y);

    try {
      return Collections.max(Maps.filterKeys(points, movablePoints).entrySet(),
              Util.getComparator(_canvas.getScene().getCameraPosition().eye())).getKey();
    } catch( NoSuchElementException ex ){
      // Если коллекция пуста, возвращаем null.
      return null;
    }
  }

  /**
   * Список видимых точек (тел), соответствующих координатам (x,y) на экране.
   * @param x координата X на экране
   * @param y координата Y на экране
   * @return
   */
  public final HashMap<String, Vect3d> getPickedPointBodies(int x, int y) {
    double[] coord = new double[2];

    ArrayList<i_Body> points = _ctrl.getBodiesByType(BodyType.POINT);
    HashMap<String, Vect3d> result = new HashMap<>();

    for (i_Body point : points) {
      try {
        i_Anchor a = _ctrl.getAnchor(point.id(), "P");
        if (!a.isVisible()) continue; // нас интересуют только видимые точки
        _canvas.getScene().getDisplayCoord(a.getPoint(), coord);
        if (Math.abs(coord[0] - x) <= GUI_EPS.value() &&
            Math.abs(y - (_canvas.getHeight() - coord[1])) <= GUI_EPS.value()){
          result.put(point.id(), a.getPoint());
        }
      } catch (ExBadRef ex) {}
    }

    return result;
  }

  /**
   * Observer pattern section.
   */

  /**
   * Register mode change listener.
   * @param listener
   */
  public void addModeChangeListener(i_ModeChangeListener listener){
    _listeners.add(listener);
  }

  /**
   * Unregister mode change listener.
   * @param listener
   */
  public void removeModeChangeListener(i_ModeChangeListener listener){
    _listeners.remove(listener);
  }

  /**
   * Notify registered mode change listeners to update.
   * @param mode
   */
  public void notifyModeChange(ScreenMode mode){
    for (i_ModeChangeListener l : _listeners){
      l.updateMode(mode);
    }
  }

  /**
   * Register mode param mode change listener
   * @param listener
   */
  public void addModeParamChangeListener(i_ModeParamChangeListener listener) {
    _paramChangeListeners.add(listener);
  }

  /**
   * Unregister mode param change listener
   * @param listener
   */
  public void removeModeParamChangeListener(i_ModeParamChangeListener listener) {
    _paramChangeListeners.remove(listener);
  }

  /**
   * Notify registered mode param change listeners to update.
   * @param type
   */
  public void notifyModeParamChange(CreateModeParamType type){
    for (i_ModeParamChangeListener l : _paramChangeListeners){
      l.updateParams(type);
    }
  }

  // Delegated methods from MainEdtCanvas.

  /**
   * Enable transferring of application key events to the canvas.
   */
  public void enableKeyDispatcher() {
    _mainCanvas.enableKeyDispatcher();
  }

  /**
   * Disable transferring of application key events to the canvas.
   */
  public void disableKeyDispatcher() {
    _mainCanvas.disableKeyDispatcher();
  }

  /**
   * Enable transferring of application mouse wheel events to the canvas.
   */
  public void enableMouseWheelDispatcher() {
    _mainCanvas.enableMouseWheelDispatcher();
  }

  /**
   * Disable transferring of application mouse wheel events to the canvas.
   */
  public void disableMouseWheelDispatcher() {
    _mainCanvas.disableMouseWheelDispatcher();
  }
}
