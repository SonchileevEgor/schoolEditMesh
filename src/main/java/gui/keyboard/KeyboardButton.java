package gui.keyboard;

import java.awt.Font;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Key on the screen keyboard.
 * 
 * @author alexeev
 */
public class KeyboardButton extends JButton {
  private final Robot _robot;
  private final int _keyCode;
  
  public KeyboardButton(String label, int keyCode, Robot robot, Font f) {
    super(label);
    _robot = robot;
    _keyCode = keyCode;
    setFont(f);
    setMargin(new Insets(0, 0, 0, 0));
    addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        _robot.keyPress(_keyCode);
      }
    });
  }
}
