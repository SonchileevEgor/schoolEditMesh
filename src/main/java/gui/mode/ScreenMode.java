package gui.mode;

import bodies.BodyType;
import bodies.RibBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import error.ModeErrorHandler;
import gui.EdtController;
import gui.IconList;
import gui.MainEdtCanvasController;
import gui.StlEdtCanvasController;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import javax.swing.Icon;
import opengl.drawing.DrawingQueue;

/**
 * Abstract screen mode
 *
 * @author alexeev
 */
public abstract class ScreenMode {

  static Cursor CURSOR_GRAB;
  static Cursor CURSOR_GRABBING;
  static protected ModeErrorHandler checker;

  static {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    CURSOR_GRAB = toolkit.createCustomCursor(
            IconList.CURSOR_GRAB.getMediumIcon().getImage(), new Point(0, 0), "grab");
    CURSOR_GRABBING = toolkit.createCustomCursor(
            IconList.CURSOR_GRABBING.getMediumIcon().getImage(), new Point(0, 0), "grabbing");
    checker = new ModeErrorHandler();
  }

  protected EdtController _ctrl;
  protected Press press;
  protected MouseAdapter _nativeAdapter;
  protected KeyAdapter _nativeKeyAdapter;

  /**
   * Main constructor of screen mode. Данный конструктор осуществляет действия по "очистке" мусора,
   * который после себя оставили предыдущие режимы
   *
   * @param ctrl
   */
  public ScreenMode(EdtController ctrl) {
    _ctrl = ctrl;
    press = new Press(ctrl);
  }

  /**
   * Get mode name from ModeList
   *
   * @return
   */
  abstract public ModeList alias();

  /**
   * Small icon for this mode. If icon is absent, the method @see getDefaultSmallIcon() can bee used
   * for implementation this method
   *
   * @return 24x24 icon
   */
  abstract public Icon getSmallIcon();

  /**
   * Large icon for this mode. If icon is absent, the method @see getDefaultLargeIcon() can bee used
   * for implementation this method
   *
   * @return 32x32 icon
   */
  abstract public Icon getLargeIcon();

  protected MouseAdapter getNativeMouseListener() {
    return new MouseAdapter() {
    };
  }

  protected KeyAdapter getNativeKeyListener() {
    return new KeyAdapter() {
    };
  }

  /**
   * Run this mode. Setting default mode of adapters.
   */
  public void run() {
    canvas().getScaleAdapter().reset();
    canvas().getRotateAdapter().reset();
    canvas().getHighlightAdapter().reset();
    _nativeAdapter = getNativeMouseListener();
    _nativeKeyAdapter = getNativeKeyListener();
    addMouseListener(_nativeAdapter);
    addKeyListener(_nativeKeyAdapter);
  }

  ;

  /**
   * Remove mouse and key listeners.
   */
  public void dispose() {
    _ctrl.status().clear();
    DrawingQueue.clear();
    removeMouseListener(_nativeAdapter);
    removeKeyListener(_nativeKeyAdapter);
  }

  /**
   * Disable KeyEvent transferring to the canvas.
   */
  public void disableKeyDispatcher() {
    canvas().disableKeyDispatcher();
  }

  /**
   * Enable KeyEvent transferring to the canvas.
   */
  public void enableKeyDispatcher() {
    canvas().enableKeyDispatcher();
  }

  /**
   * Repaint scene.
   */
  public void repaint() {
    _ctrl.redraw();
  }

  /**
   * Fire key press event on canvas.
   *
   * @param e
   */
  public void fireKeyPressEvent(KeyEvent e) {
    canvas().getMainCanvas().fireKeyPressEvent(e);
  }

  protected void reset() {
    dispose();
    run();
    canvas().notifyModeChange(this);
  }

  /**
   * The key used for storing the String name for the action, used for a menu or button.
   *
   * @return
   */
  abstract protected String description();

  /**
   * Может ли быть включен данный режим.
   *
   * @return
   */
  abstract protected boolean isEnabled();

  /**
   * Switch to specified mode.
   *
   * @param mode
   */
  protected void switchMode(ScreenMode mode) {
    canvas().setMode(mode);
  }

  /**
   * Switch to default mode.
   */
  protected void exit() {
    canvas().setDefaultMode();
  }

  /**
   * Установка вида курсора при наведении на полотно.
   *
   * @param cursor
   */
  protected void setCursor(Cursor cursor) {
    canvas().setCursor(cursor);
  }

  protected void addMouseListener(MouseAdapter ma) {
    canvas().addMouseListener(ma);
  }

  protected void removeMouseListener(MouseAdapter ma) {
    canvas().removeMouseListener(ma);
  }

  protected void addKeyListener(KeyListener k) {
    canvas().addKeyListener(k);
  }

  protected void removeKeyListener(KeyListener k) {
    canvas().removeKeyListener(k);
  }

  protected final MainEdtCanvasController canvas() {
    return _ctrl.getMainCanvasCtrl();
  }

  protected final StlEdtCanvasController stlCanvas() {
    return _ctrl.getStlCanvasCtrl();
  }

  /**
   * Get point anchor by ID of PointBody. If body is not a point, or there is no body with given ID,
   * return null.
   *
   * @param bodyID
   * @return anchor or null reference
   */
  protected i_Anchor getPointAnchor(String bodyID) {
    try {
      if (_ctrl.getBody(bodyID).type() == BodyType.POINT) {
        return _ctrl.getAnchor(bodyID, "P");
      }
    } catch (ExNoAnchor | ExNoBody ex) {
    }

    return null;
  }

  /**
   * Get polygon anchor by ID of polygon. If body is not a polygon, or there is no body with given
   * ID, return null.
   *
   * @param bodyID
   * @return anchor or null reference
   */
  protected i_Anchor getPolygonAnchor(String bodyID) {
    try {
      if (_ctrl.getBody(bodyID).type().isPolygon()) {
        return _ctrl.getAnchor(bodyID, "facet");
      }
    } catch (ExNoAnchor | ExNoBody ex) {
    }

    return null;
  }

  /**
   * Get disk anchor by ID of circle. If body is not a circle, or there is no body with given ID,
   * return null.
   *
   * @param bodyID
   * @return anchor or null reference
   */
  protected i_Anchor getDiskAnchor(String bodyID) {
    try {
      if (_ctrl.getBody(bodyID).type() == BodyType.CIRCLE) {
        return _ctrl.getAnchor(bodyID, "disk");
      }
    } catch (ExNoAnchor | ExNoBody ex) {
    }

    return null;
  }

  /**
   * Get rib anchor by ID of circle. If body is not a rib, or there is no body with given ID, return
   * null.
   *
   * @param bodyID
   * @return anchor or null reference
   */
  protected i_Anchor getRibAnchor(String bodyID) {
    try {
      if (_ctrl.getBody(bodyID).type() == BodyType.RIB) {
        return _ctrl.getAnchor(bodyID, RibBody.BODY_KEY_RIB);
      }
    } catch (ExNoAnchor | ExNoBody ex) {
    }

    return null;
  }

  protected Icon getDefaultSmallIcon() {
    return IconList.EMPTY.getMediumIcon();
  }

  protected Icon getDefaultLargeIcon() {
    return IconList.EMPTY.getLargeIcon();
  }
}
