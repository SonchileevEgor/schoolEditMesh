package gui.ui;

import opengl.colorgl.ColorGL;
import gui.EdtController;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Color chooser control element.
 * When pressed, color chooser dialog will be shown.
 * @author alexeev
 */
public class EdtColorChooser extends JPanel {
  private static final Color BORDER_COLOR = Color.BLACK;

  /**
   * Property of color change.
   * Use this property when color is changing continuously (f.e. from EdtColorChooserDialog).
   */
  public static final String COLOR_PROPERTY = "color";

  /**
   * Property of color change.
   * This property is used for undo/redo events generation.
   */
  public static final String COLOR_CHANGE_PROPERTY = "colorchange";

  /**
   * Rectangular frame shape.
   */
  public static final int FRAME = 0;

  /**
   * Solid rectangular shape.
   */
  public static final int SOLID = 1;

  /**
   * Shape of the disk.
   */
  public static final int DISK = 2;

  private EdtController _ctrl;
  private ColorGL _color;
  private final int _shape;

  /**
   * @param ctrl
   * @param initColor color
   * @param shape shape of chooser
   */
  public EdtColorChooser(EdtController ctrl, ColorGL initColor, int shape) {
    super();
    _ctrl = ctrl;
    _color = initColor;
    _shape = shape;
    setOpaque(false);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if( SwingUtilities.isLeftMouseButton(e)){
          Color chosenColor =
                  _ctrl.showColorChooserDialog(
                    new ChangeListener() {
                      @Override
                      public void stateChanged(ChangeEvent e) {
                        Color newColor = ((ColorSelectionModel)e.getSource()).getSelectedColor();
                        _color = new ColorGL(newColor);
                        firePropertyChange(COLOR_PROPERTY, _color, newColor);
                        revalidate();
                        repaint();
                      }
                    }, _color.toRGBAColor());
          if( chosenColor != null ){
            firePropertyChange(COLOR_CHANGE_PROPERTY, _color, chosenColor);
          }
        }
      }
    });
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setPaint(_color.toRGBAColor());
    if (_shape == SOLID) {
      g2d.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
      g2d.setPaint(BORDER_COLOR);
      g2d.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
    } else if (_shape == FRAME) {
      g2d.drawRect(2, 2, getWidth() - 5, getHeight() - 5);
      g2d.drawRect(3, 3, getWidth() - 7, getHeight() - 7);
      g2d.drawRect(4, 4, getWidth() - 9, getHeight() - 9);
      g2d.setPaint(Color.BLACK);
      g2d.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
      g2d.drawRect(5, 5, getWidth() - 11, getHeight() - 11);
    } else if (_shape == DISK) {
      g2d.fillOval(3, 3, getWidth() - 6, getHeight() - 6);
      g2d.setPaint(Color.BLACK);
      g2d.drawOval(3, 3, getWidth() - 7, getHeight() - 7);
    }
  }

  public void setColor(ColorGL c) {
    _color = c;
  }
}