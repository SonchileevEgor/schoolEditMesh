package gui.mode.adapters;

import gui.EdtController;
import gui.MainEdtCanvasController;

/**
 * Mouse adapter working on main canvas, with link to EdtController.
 *
 * @author alexeev
 */
public abstract class MainEdtMouseAdapter extends BasicEdtMouseAdapter {
  protected EdtController _ctrl;
  protected MainEdtCanvasController _mainCanvas; // дублируем поле для простого обращения к полотну

  /**
   *
   * @param ctrl
   * @param canvas
   *    Адаптер создаётся до привязки контроллера полотна к контроллеру приложения,
   *    поэтому в конструктор они передаются отдельно.
   */
  public MainEdtMouseAdapter( EdtController ctrl, MainEdtCanvasController canvas ){
    super(canvas);
    _ctrl = ctrl;
    _mainCanvas = canvas;
  }
}
