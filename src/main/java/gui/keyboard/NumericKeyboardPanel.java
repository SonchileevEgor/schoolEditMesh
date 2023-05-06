package gui.keyboard;

import com.jogamp.newt.event.KeyEvent;
import gui.EdtController;
import gui.ui.EdtPanel;
import java.awt.Font;
import java.awt.Robot;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Numpad of the screen keyboard.
 * 
 * @author alexeev
 */
public class NumericKeyboardPanel extends EdtPanel {
  /**
   * Create numeric keyboard panel.
   * 
   * @param ctrl
   * @param size side length of buttons.
   * @param robot
   * @param f
   */
  public NumericKeyboardPanel(EdtController ctrl, String size, Robot robot, Font f) {
    super(new MigLayout(new LC().wrapAfter(3).gridGap("2", "2"),
            new AC().size(size).fill(), new AC().size(size).fill()));
    
    add(new KeyboardButton("7", KeyEvent.VK_7, robot, f));
    add(new KeyboardButton("8", KeyEvent.VK_8, robot, f));
    add(new KeyboardButton("9", KeyEvent.VK_9, robot, f));
    add(new KeyboardButton("4", KeyEvent.VK_4, robot, f));
    add(new KeyboardButton("5", KeyEvent.VK_5, robot, f));
    add(new KeyboardButton("6", KeyEvent.VK_6, robot, f));
    add(new KeyboardButton("1", KeyEvent.VK_1, robot, f));
    add(new KeyboardButton("2", KeyEvent.VK_2, robot, f));
    add(new KeyboardButton("3", KeyEvent.VK_3, robot, f));
    add(new KeyboardButton("0", KeyEvent.VK_0, robot, f), new CC().spanX(2));
    add(new KeyboardButton(",", KeyEvent.VK_PERIOD, robot, f));
  }
}