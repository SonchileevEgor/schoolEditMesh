package gui.laf;

import java.awt.Color;

/**
 * List of application colors.
 * @author alexeev
 */
public enum AppColor {
  WHITE(255, 255, 255),
  BLUE(51, 153, 255),
  ORANGE(255, 224, 48),
  ORANGE_LIGHT(255, 224, 96),
  ORANGE_VERY_LIGHT(255, 240, 128),
  ORANGE_ULTRA_LIGHT(255, 248, 202),
  EXTREMELY_LIGHT_GRAY(240, 240, 240),
  LIGHT_GRAY(224, 224, 224),
  GRAY(208, 208, 208),
  DARK_GRAY(32, 32, 32),
  BLACK(0, 0, 0),

  TRANSPARENT(0, 0, 0, 0);

  private Color _color;

  AppColor(int r, int g, int b) {
    _color = new Color(r, g, b);
  }

  AppColor(int r, int g, int b, int a) {
    _color = new Color(r, g, b, a);
  }

  public Color color() {
    return _color;
  }

  public Color opaque() {
    return new Color(_color.getRed(), _color.getGreen(), _color.getBlue(), 192);
  }
}