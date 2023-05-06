package gui.keyboard;

import gui.EdtController;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author alexeev
 */
public class CursorKeyboardPanel extends JPanel{
  /**
   * Create cursor keyboard panel.
   * 
   * @param ctrl
   * @param size side length of buttons.
   * @param robot
   * @param f
   */
  public CursorKeyboardPanel(EdtController ctrl, String size, Robot robot, Font f) {
    super(new MigLayout(new LC().gridGap("2", "2"),
            new AC().size(size).fill(), new AC().size(size).fill()));
    
    KeyboardButton left = new KeyboardButton("\u2190", KeyEvent.VK_LEFT, robot, f);
    KeyboardButton right = new KeyboardButton("\u2192", KeyEvent.VK_RIGHT, robot, f);
    KeyboardButton up = new KeyboardButton("\u2191", KeyEvent.VK_UP, robot, f);
    KeyboardButton down = new KeyboardButton("\u2193", KeyEvent.VK_DOWN, robot, f);
    KeyboardButton enter = new KeyboardButton("\u25BA", KeyEvent.VK_ENTER, robot, f);
    
    add(up, new CC().cell(1, 0));
    add(left, new CC().cell(0, 1));
    add(enter, new CC().cell(1, 1));
    add(right, new CC().cell(2, 1));
    add(down, new CC().cell(1, 2));
  }
}
