package gui.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author alexeev
 */
public class EdtListCellRenderer extends JLabel implements ListCellRenderer<Object> {
  private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;

  /**
   * Constructs a default renderer object for an item
   * in a list.
   */
  public EdtListCellRenderer() {
    super();
    setOpaque(true);
    setName("List.cellRenderer");
  }

  @Override
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
  {
    setComponentOrientation(list.getComponentOrientation());

    Color bg = null;
    Color fg = null;

    JList.DropLocation dropLocation = list.getDropLocation();
    if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) {
      bg = UIManager.getColor("List.dropCellBackground");
      fg = UIManager.getColor("List.dropCellForeground");
      isSelected = true;
    }

    if (isSelected) {
      setBackground(bg == null ? list.getSelectionBackground() : bg);
      setForeground(fg == null ? list.getSelectionForeground() : fg);
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }

    if (value instanceof Icon) {
      setIcon((Icon)value);
      setText("");
    } else if (value instanceof String){
      setIcon(null);
      setText(value.toString());
    } else {
      setText("");
    }

    setEnabled(list.isEnabled());
    setFont(list.getFont());

    Border border = null;
    if (cellHasFocus) {
      if (isSelected) {
        border = UIManager.getBorder("List.focusSelectedCellHighlightBorder");
      }
      if (border == null) {
        border = UIManager.getBorder("List.focusCellHighlightBorder");
      }
    } else {
      border = getNoFocusBorder();
    }
    setBorder(border);

    return this;
  }

  private Border getNoFocusBorder() {
    Border border = UIManager.getBorder("List.cellNoFocusBorder");
    if (System.getSecurityManager() != null) {
        if (border != null) return border;
        return SAFE_NO_FOCUS_BORDER;
    } else {
      if (border != null && (noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER)) {
        return border;
      }
      return noFocusBorder;
    }
  }

  /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void validate() {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  *
  * @since 1.5
  */
  @Override
  public void invalidate() {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  *
  * @since 1.5
  */
  @Override
  public void repaint() {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void revalidate() {}
 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void repaint(long tm, int x, int y, int width, int height) {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void repaint(Rectangle r) {}

  /**
   * Overridden for performance reasons.
   * See the <a href="#override">Implementation Note</a>
   * for more information.
   */
  @Override
  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    // Strings get interned...
    if ("text".equals(propertyName) || (("font".equals(propertyName) || "foreground".equals(propertyName))
          && oldValue != newValue
          && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {
      super.firePropertyChange(propertyName, oldValue, newValue);
    }
  }

  /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void firePropertyChange(String propertyName, char oldValue, char newValue) {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void firePropertyChange(String propertyName, short oldValue, short newValue) {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void firePropertyChange(String propertyName, int oldValue, int newValue) {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void firePropertyChange(String propertyName, long oldValue, long newValue) {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void firePropertyChange(String propertyName, float oldValue, float newValue) {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void firePropertyChange(String propertyName, double oldValue, double newValue) {}

 /**
  * Overridden for performance reasons.
  * See the <a href="#override">Implementation Note</a>
  * for more information.
  */
  @Override
  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
}
