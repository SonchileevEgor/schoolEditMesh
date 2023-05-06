package gui.keyboard;

import gui.EdtController;
import gui.ui.EdtFramedPanel;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * Панель клавиатуры для интерактивных досок.
 * Реализована синглтоном, т.к. либо она не требуется вообще (в десктопной версии),
 * либо включается один раз за сессию (при работе с интерактивной доской).
 * 
 * @author alexeev
 */
public class KeyboardPanel extends JFrame {
  private static final Font FONT = new Font("Arial", Font.BOLD, 18);
  private static final String BUTTON_SIZE = "30!";
  
  private static Robot _robot;
  private static EdtController _ctrl;
  private static KeyboardPanel _instance;
  private static boolean _isCreated = false;
  
  private MoveMouseAdapter _moveAdapter;
  
  private KeyboardPanel() {}
  
  public static KeyboardPanel create(EdtController ctrl) {
    if (!_isCreated) {
      _instance = new KeyboardPanel(ctrl);
    }
    _isCreated = true;
    return _instance;
  }
  
  private KeyboardPanel(EdtController ctrl) {
    super("Экранная клавиатура");
    _ctrl = ctrl;
    try {
      _robot = new Robot();
    } catch (AWTException ex) {}
    
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setUndecorated(true);
    setResizable(false);
    setAlwaysOnTop(true);
    setFocusableWindowState(false);
    setLayout(new BorderLayout());
    
    EdtFramedPanel wrapper = new EdtFramedPanel("Экранная клавиатура");
    wrapper.setLayout(new MigLayout());
    wrapper.add(new CursorKeyboardPanel(_ctrl, BUTTON_SIZE, _robot, FONT), new CC().cell(0, 0));
    wrapper.add(new NumericKeyboardPanel(_ctrl, BUTTON_SIZE, _robot, FONT), new CC().cell(1, 0));
    add(wrapper, BorderLayout.CENTER);
    
    _moveAdapter = new MoveMouseAdapter();
    wrapper.addMouseListener(_moveAdapter);
    wrapper.addMouseMotionListener(_moveAdapter);
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentHidden(ComponentEvent e) {
        _ctrl.status().setKeyboardIcon(false);
      }
      @Override
      public void componentShown(ComponentEvent e) {
        _ctrl.status().setKeyboardIcon(true);
      }
    });
    
    pack();
  }
  
  public void toggleVisible() {
    setVisible(!isVisible());
  }
  
  /**
   * Класс, позволяющий таскать JFrame за любую незанятую часть его содержимого.
   * 
   * @author alexeev
   */
  class MoveMouseAdapter extends MouseAdapter {
    Point start_drag = new Point();
    Point start_loc = new Point();

    Point getScreenLocation(MouseEvent e) {
      Point cursor = e.getPoint();
      Point target_location = KeyboardPanel.this.getLocationOnScreen();
      return new Point((int) (target_location.getX() + cursor.getX()),
          (int) (target_location.getY() + cursor.getY()));
    }

    @Override
    public void mousePressed(MouseEvent e) {
      start_drag = getScreenLocation(e);
      start_loc = KeyboardPanel.this.getLocation();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      Point current = getScreenLocation(e);
      Point offset = new Point((int) current.getX() - (int) start_drag.getX(),
          (int) current.getY() - (int) start_drag.getY());
      Point new_location = new Point(
          (int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc.getY() + offset.getY()));
      KeyboardPanel.this.setLocation(new_location);
    }
  }
  
  public static void main(String[] args) {
    EdtController ctrl = new EdtController();
    KeyboardPanel keyboard = new KeyboardPanel(ctrl);
    keyboard.setVisible(true);
  }
}