package gui.elements;

import gui.laf.AppColor;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * JLabel looks like button.
 * 
 * @author alexeev
 */
public class ButtonLabel extends JLabel {
  public ButtonLabel(String text) {
    super(text);
    setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
  }

  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setPaint(new GradientPaint(0, 0, AppColor.WHITE.color(), 0, getHeight(), AppColor.LIGHT_GRAY.color()));
    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
    g2d.setPaint(Color.BLACK);
    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
    super.paintComponent(g);
  }
}