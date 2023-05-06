package gui.dialog;

import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 *
 * @author alexeev
 */
public class EdtDialog extends JDialog {

  public EdtDialog(JFrame frame, String title) {
    super(frame, title);

    setArrowsAsFocusTraversalKeys();
  }

  /**
   * Create dialog by its title without link to the main frame.
   * @param title
   */
  public EdtDialog(String title) {
    super();

    setTitle(title);
    setArrowsAsFocusTraversalKeys();
  }

  /**
   * Set at the center of parent frame or screen.
   */
  public final void setCenterLocation() {
    if( getOwner().isActive() ){
      util.Util.setCenterLocation(getOwner(), this);
    } else {
      util.Util.setCenterLocation(null, this);
    }
  }

  /**
   * Установить стрелки в качестве клавиш перехода между элементами управления.
   */
  private void setArrowsAsFocusTraversalKeys() {
    KeyStroke downArrow = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
    KeyStroke upArrow = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
    HashSet<AWTKeyStroke> down = new HashSet(getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
    HashSet<AWTKeyStroke> up = new HashSet(getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
    down.add(downArrow);
    up.add(upArrow);
    setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, down);
    setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, up);
  }
}