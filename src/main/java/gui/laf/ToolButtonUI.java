package gui.laf;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author alexeev
 */
public class ToolButtonUI extends BasicButtonUI {
  private static ToolButtonUI _instance = null;

  @Override
  public void installUI(JComponent c){
    super.installUI(c);
    AbstractButton button = (AbstractButton) c;
    button.setRolloverEnabled(true);
  }

  public static ComponentUI createUI(JComponent c){
    if (_instance == null)
      _instance = new ToolButtonUI();
    return _instance;
  }

  @Override
  public void paint(Graphics g, JComponent c) {
    Graphics2D g2d = ( Graphics2D ) g;
    g2d.setRenderingHint ( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    JButton button = (JButton) c;
    ButtonModel buttonModel = button.getModel();

    if (buttonModel.isRollover()) {
      g2d.setPaint(new GradientPaint(0, 0, AppColor.WHITE.color(), 0, c.getHeight(), AppColor.ORANGE_VERY_LIGHT.color()));
    } else {
      g2d.setPaint(new GradientPaint(0, 0, AppColor.WHITE.color(), 0, c.getHeight(), AppColor.LIGHT_GRAY.color()));
    }
    if (buttonModel.isPressed()) {
      g2d.setPaint(new GradientPaint(0, 0, AppColor.ORANGE_ULTRA_LIGHT.color(), 0, c.getHeight(), AppColor.ORANGE.color()));
    }
    g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);

    if (button.isDefaultButton()) {
      g2d.setPaint(AppColor.ORANGE_VERY_LIGHT.color());
      g2d.drawRoundRect(1, 1, c.getWidth() - 3, c.getHeight() - 3, 6, 6);
      g2d.drawRoundRect(2, 2, c.getWidth() - 5, c.getHeight() - 5, 4, 4);
    }

    if (buttonModel.isEnabled()) {
      g2d.setPaint(Color.BLACK);
    } else {
      g2d.setPaint(AppColor.GRAY.color());
    }
    g2d.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 6, 6);

    // Отрисовка текста и иконки изображения
    super.paint(g, c);
  }
}
