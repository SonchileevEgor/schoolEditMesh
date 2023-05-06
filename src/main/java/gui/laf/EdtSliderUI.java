package gui.laf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 *
 * @author alexeev
 */
public class EdtSliderUI extends BasicSliderUI {

  @Override
  public void installUI(JComponent c){
    super.installUI(c);
  }

  public static EdtSliderUI createUI(JComponent c){
    return new EdtSliderUI((JSlider)c);
  }

  public EdtSliderUI(JSlider slider) {
    super(slider);
  }

  @Override
  public void paint(Graphics g, JComponent c) {
    Rectangle clip = g.getClipBounds();
    Graphics2D g2d = (Graphics2D)g;
    JSlider s = (JSlider)c;
    int w = clip.width;
    int h = clip.height;
    g2d.setPaint(s.getBackground());
    g2d.fillRoundRect(0, 0, w, h, 8, 8);
    g2d.setPaint(AppColor.ORANGE_ULTRA_LIGHT.color());
    g2d.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
    g2d.drawRoundRect(1, 1, w - 3, h - 3, 6, 6);
    g2d.drawRoundRect(2, 2, w - 5, h - 5, 4, 4);
    super.paint(g, c);
  }

  /**
   * Действие при фокусировке на слайдере.
   * @param g
   */
  @Override
  public void paintFocus(Graphics g)  {
    Graphics2D g2d = (Graphics2D)g;
    Rectangle clip = g.getClipBounds();
    g2d.setPaint(getFocusColor());
    g2d.drawRoundRect(0, 0, clip.width - 1, clip.height - 1, 8, 8);
    g2d.drawRoundRect(1, 1, clip.width - 3, clip.height - 3, 6, 6);
  }

  @Override
  protected Dimension getThumbSize() {
    if (isThumbCircle()) {
      return new Dimension(12, 12);
    } else if (slider.getOrientation() == JSlider.HORIZONTAL) {
      return new Dimension(12, 18);
    } else {
      return new Dimension(20, 12);
    }
  }

  /**
   * Проверяем, рисуется ли указатель как окружность.
   * @return
   */
  private boolean isThumbCircle() {
    Boolean paintThumbArrowShape = (Boolean)slider.getClientProperty("Slider.paintThumbArrowShape");

    boolean isPropertyNull = (paintThumbArrowShape == null);
    paintThumbArrowShape = false;

    return (!slider.getPaintTicks() && isPropertyNull) || (!paintThumbArrowShape && !isPropertyNull);
  }

  /**
   * Отображение указателя слайдера.
   * @param g
   */
  @Override
  public void paintThumb(Graphics g)  {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Rectangle knobBounds = thumbRect;
    int w = thumbRect.width;
    int h = thumbRect.height;
    Color shadow = getShadowColor();
    Color hightlight = getHighlightColor();

    g2d.translate(knobBounds.x, knobBounds.y);

    if (slider.isEnabled()) {
      g2d.setColor(slider.getBackground());
    }
    else {
      g2d.setColor(slider.getBackground().darker());
    }

    if (isThumbCircle()) {
      g2d.setPaint(hightlight);
      g2d.fillOval(0, 0, w, w);

      g.setColor(shadow);
      g.drawOval(0, 0, w-1, w-1);
    } else if (slider.getOrientation() == JSlider.HORIZONTAL) {
      int cw = w / 2;
      int ch = h / 3;
      g2d.setPaint(hightlight);
      g2d.fillArc(0, 0, w, w, 0, 180);
      g2d.fillPolygon(new int[]{0, w, cw}, new int[]{ch, ch, h}, 3);
      g.setColor(shadow);
      g.drawArc(0, 0, w-1, w-1, 0, 180);
      g.drawLine(0, ch - 1, cw - 1, h - 1);
      g.drawLine(w - 1, ch - 1, cw, h - 1);
    } else if (slider.getOrientation() == JSlider.VERTICAL) {
      //!! TODO: переопределить рисование вертикального слайдера.
      super.paintThumb(g);
    }

    g.translate(-knobBounds.x, -knobBounds.y);
  }

  /**
   * Отображение полосы слайдера.
   * @param g
   */
  @Override
  public void paintTrack(Graphics g)  {
    Rectangle trackBounds = trackRect;
    Color shadow = getShadowColor();
    Color hightlight = getHighlightColor();

    if (slider.getOrientation() == JSlider.HORIZONTAL) {
      int cy = (trackBounds.height / 2) - 2;
      int w = trackBounds.width;

      g.translate(trackBounds.x, trackBounds.y + cy);

      g.setColor(hightlight);
      g.fillRoundRect(0, 0, w, 4, 2, 2);

      g.setColor(shadow);
      g.drawRoundRect(0, 0, w - 1, 3, 2, 2);

      g.translate(-trackBounds.x, -(trackBounds.y + cy));
    }
    else {
      int cx = (trackBounds.width / 2) - 2;
      int ch = trackBounds.height;

      g.translate(trackBounds.x + cx, trackBounds.y);

      g.setColor(shadow);
      g.drawLine(0, 0, 0, ch - 1);
      g.drawLine(1, 0, 2, 0);
      g.setColor(hightlight);
      g.drawLine(3, 0, 3, ch);
      g.drawLine(0, ch, 3, ch);
      g.setColor(Color.black);
      g.drawLine(1, 1, 1, ch-2);

      g.translate(-(trackBounds.x + cx), -trackBounds.y);
    }
  }

  /**
   * Добавляю перерисовку слайдера во время перетаскивания.
   * В базовом классе её нет, без этого картинка "смазывается".
   * @param slider
   * @return
   */
  @Override
  protected TrackListener createTrackListener(final JSlider slider) {
    return new TrackListener() {
      @Override
      public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (slider.isEnabled()) {
          slider.repaint();
        }
      }
    };
  }
}
