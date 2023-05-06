package gui.laf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicCheckBoxUI;

/**
 * UI for Checkbox.
 * @author alexeev
 */
public class EdtCheckBoxUI extends BasicCheckBoxUI {
  private static EdtCheckBoxUI _instance = null;
  
  @Override
  public void installUI(JComponent c){
    super.installUI(c);
  }
  
  public static EdtCheckBoxUI createUI(JComponent c){
    if (_instance == null)
      _instance = new EdtCheckBoxUI();
    return _instance;
  }
  
  @Override
  public void paint (Graphics g, JComponent c) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    JCheckBox checkBox = (JCheckBox)c;
    ButtonModel model = checkBox.getModel();

    g2d.setPaint(UIManager.getColor("CheckBox.shadow"));
    g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
    
    if (model.isRollover()) {
      g2d.setPaint(UIManager.getColor("CheckBox.darkShadow"));
      g2d.drawRect(2, 2, c.getWidth() - 5, c.getHeight() - 5);
      g2d.drawRect(3, 3, c.getWidth() - 7, c.getHeight() - 7);
      g2d.drawRect(4, 4, c.getWidth() - 9, c.getHeight() - 9);
    }
    if (model.isPressed()) {
      g2d.setPaint(UIManager.getColor("CheckBox.focus"));
      g2d.drawRect(2, 2, c.getWidth() - 5, c.getHeight() - 5);
      g2d.drawRect(3, 3, c.getWidth() - 7, c.getHeight() - 7);
      g2d.drawRect(4, 4, c.getWidth() - 9, c.getHeight() - 9);
    }
    g2d.setPaint(Color.BLACK);
    g2d.drawRect(5, 5, c.getWidth() - 11, c.getWidth() - 11);
    if (model.isSelected()) {
      g2d.setPaint(checkBox.getForeground());
    } else {
      g2d.setPaint(checkBox.getBackground());
    }
    g2d.fillRect(6, 6, c.getWidth() - 12, c.getWidth() - 12);
  }
}
