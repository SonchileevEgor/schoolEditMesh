package gui;

import java.awt.AWTEvent;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

/**
 * Main application canvas.
 *
 * @author alexeev
 */
public class MainEdtCanvas extends BasicEdtCanvas {
  private static boolean _isInitialized = false;

  /**
   * Key event dispatcher.
   */
  private KeyEventDispatcher _keyDispatcher;

  /**
   * Key codes for transferring to the canvas.
   */
  private ArrayList<Integer> _ctrlKeyCodes = new ArrayList<>();

  /**
   * Is control keys transferring to canvas on.
   */
  private boolean _keyDispOn;

  /**
   * Is mouse wheel transferring to canvas on.
   */
  private boolean _mouseWheelOn;

  /**
   * Check if mouse is over the canvas.
   */
  private boolean _isMouseOver;

  public MainEdtCanvas() {
    super();
    _keyDispOn = true;
    _mouseWheelOn = true;
    _isMouseOver = false;
  }

  /**
   * Инициализация диспетчеров.
   */
  public void initDispatchers() {
    if( _isInitialized ) return; // инициализация происходит один раз

    _isInitialized = true;
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        _isMouseOver = true;
      }
      @Override
      public void mouseExited(MouseEvent e) {
        _isMouseOver = false;
      }
    });
    
    /**
     * Все нажатия клавиш ловятся по всему приложению и передаются полотну.
     * Если клавиши нет в списке _ctrlKeyCodes, то событие передаётся следующему по цепочке диспетчеру.
     * Если обработка происходит здесь, то событие дальше не передаётся.
     */
    initControlKeys();
    _keyDispatcher = new KeyEventDispatcher() {
      @Override
      public boolean dispatchKeyEvent(KeyEvent e) {
        if (_keyDispOn && !hasFocus() && _ctrlKeyCodes.contains(e.getKeyCode())) {
          processKeyEvent(e);
          // we don't pass event to the next dispatcher in chain.
          return true;
        }
        return false;
      }
    };
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(_keyDispatcher);

    /**
     * События вращения колёсика мыши также передаются полотну.
     * Обрабатываются в случае, если курсор находится над полотном
     * и включен режим прослушивания колёсика мыши.
     */
    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
      @Override
      public void eventDispatched(AWTEvent e) {
        if (_mouseWheelOn && !_isMouseOver){
          processMouseWheelEvent((MouseWheelEvent)e);

          // prevent triggering event twice.
          ((MouseWheelEvent)e).consume();
        }
      }
    }, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
  }

  public void enableKeyDispatcher() {
    _keyDispOn = true;
  }

  public void disableKeyDispatcher() {
    _keyDispOn = false;
  }

  public void enableMouseWheelDispatcher() {
    _mouseWheelOn = true;
  }

  public void disableMouseWheelDispatcher() {
    _mouseWheelOn = false;
  }

  /**
   * Fire key press event.
   * @param e
   */
  public void fireKeyPressEvent(KeyEvent e) {
    processKeyEvent(e);
  }

  /**
   * Инициализировать список специальных клавиш.
   * Нажатие специальных клавиш в любом месте приложения передаётся на обработку полотну
   * (в случае, если диспетчер клавиш включён).
   */
  private void initControlKeys() {
    _ctrlKeyCodes.add(KeyEvent.VK_ENTER);
    _ctrlKeyCodes.add(KeyEvent.VK_PLUS);
    _ctrlKeyCodes.add(KeyEvent.VK_MINUS);
    _ctrlKeyCodes.add(KeyEvent.VK_P);
    _ctrlKeyCodes.add(KeyEvent.VK_ESCAPE);
    _ctrlKeyCodes.add(KeyEvent.VK_UP);
    _ctrlKeyCodes.add(KeyEvent.VK_DOWN);
  }
}
