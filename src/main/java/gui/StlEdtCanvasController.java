package gui;

import gui.mode.CoverScreenMode;
import gui.mode.ScreenMode;
import gui.mode.adapters.CoverHighlightAdapter;
import gui.mode.adapters.DefaultKeyAdapter;
import gui.mode.i_ModeChangeListener;
import gui.mode.i_ModeParamChangeListener;
import gui.mode.param.CreateModeParamType;
import java.util.ArrayList;

/**
 *
 * @author Vladimir
 */
public class StlEdtCanvasController extends BasicEdtCanvasController{
  private final EdtController _ctrl;
  private final MainEdtCanvas _mainCanvas; // дублируем поле для обращения без приведения типа
  private ScreenMode _mode;

  /**
   * Permanent listeners.
   */
  private final ArrayList<i_ModeParamChangeListener> _paramChangeListeners;
  private final CoverHighlightAdapter _coverHighlightAdapter;
  private final DefaultKeyAdapter _defaultKeyAdapter;

    
  public StlEdtCanvasController(EdtController ctrl, MainEdtCanvas canvas) {
    super(canvas);
    _ctrl = ctrl;
    _mainCanvas = canvas;
    _paramChangeListeners = new ArrayList<>();
    _defaultKeyAdapter = new DefaultKeyAdapter(_ctrl);
    _coverHighlightAdapter = new CoverHighlightAdapter(_ctrl, this);
  }
   
  @Override
  public void initMouseListeners() {
    super.initMouseListeners();
    addMouseListener(_coverHighlightAdapter);
    addKeyListener(_defaultKeyAdapter);
  }

  public CoverHighlightAdapter getCoverHighlightAdapter() {
    return _coverHighlightAdapter;
  }

  public DefaultKeyAdapter getDefaultKeyAdapter() {
    return _defaultKeyAdapter;
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
    setMode(new CoverScreenMode(_ctrl));
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
   *
   */
  public void notifyModeParamChange(CreateModeParamType type){
    for (i_ModeParamChangeListener l : _paramChangeListeners){
      l.updateParams(type);
    }
  }
}
