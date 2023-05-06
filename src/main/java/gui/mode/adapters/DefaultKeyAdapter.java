package gui.mode.adapters;

import gui.EdtController;
import gui.action.ActionFactory;
import gui.mode.ModeList;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Key adapter for canvas.
 *
 * @author alexeev
 */
public class DefaultKeyAdapter extends EdtKeyAdapter {
  public DefaultKeyAdapter(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_W:
        if (!_blockRotation && canvas().is3d() && e.getModifiers() == 0)
          ActionFactory.TURN_SCENE_UP.getAction(_ctrl).actionPerformed(
                  new ActionEvent(_ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
        break;
      case KeyEvent.VK_S:
        if (!_blockRotation && canvas().is3d() && e.getModifiers() == 0)
          ActionFactory.TURN_SCENE_DOWN.getAction(_ctrl).actionPerformed(
                  new ActionEvent(_ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
        break;
      case KeyEvent.VK_D:
        if (!_blockRotation && e.getModifiers() == 0)
          ActionFactory.TURN_SCENE_RIGHT.getAction(_ctrl).actionPerformed(
                  new ActionEvent(_ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
        break;
      case KeyEvent.VK_A:
        if (!_blockRotation && e.getModifiers() == 0)
          ActionFactory.TURN_SCENE_LEFT.getAction(_ctrl).actionPerformed(
                  new ActionEvent(_ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
        break;
      case KeyEvent.VK_P:
        if (e.getModifiers() == 0) {
          if (canvas().is3d()){
            ModeList.MODE_CREATE_POINT.getAction(_ctrl).actionPerformed(
                    new ActionEvent(_ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
          } else {
            ModeList.MODE_CREATE_POINT_2D.getAction(_ctrl).actionPerformed(
                    new ActionEvent(_ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
          }
        }
        break;
      case KeyEvent.VK_ESCAPE:
        if (e.getModifiers() == 0) {
          ModeList.MODE_DEFAULT.getAction(_ctrl).actionPerformed(
                    new ActionEvent(_ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
        }
    }
    if (e.getKeyChar() == '+' && e.getModifiers() == KeyEvent.CTRL_MASK) {
      ActionFactory.ZOOM_IN.getAction(_ctrl).actionPerformed(
        new ActionEvent(_ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
    } else if (e.getKeyChar() == '-' && e.getModifiers() == KeyEvent.CTRL_MASK) {
      ActionFactory.ZOOM_OUT.getAction(_ctrl).actionPerformed(
        new ActionEvent(_ctrl.getFrame(), ActionEvent.ACTION_PERFORMED, null));
    }
  }

  /**
   * Lock rotation functions.
   * @param block
   */
  public void blockRotation(boolean block) {
    _blockRotation = block;
  }

  /**
   * Lock rotation functions.
   */
  private boolean _blockRotation;
}
