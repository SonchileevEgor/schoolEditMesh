package gui.mode.adapters;

import gui.MainEdtCanvasController;
import gui.EdtController;
import java.awt.event.KeyAdapter;

/**
 *
 * @author alexeev-laptop
 */
class EdtKeyAdapter extends KeyAdapter {
  protected EdtController _ctrl;
  protected boolean _enabled;


  public EdtKeyAdapter(EdtController ctrl) {
    _ctrl = ctrl;
  }

  public void disable() {
    _enabled = false;
  }

  public void enable() {
    _enabled = true;
  }

  protected MainEdtCanvasController canvas() {
    return _ctrl.getMainCanvasCtrl();
  }
}
