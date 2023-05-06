package gui.ui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Wrapper for JRadioButton.
 * @author alexeev
 */
public class EdtRadioButton extends JRadioButton {
  public EdtRadioButton () {
    this(null, null, false);
  }

  /**
   * Creates an initial unselected radio button
   * with specified icon replacing the text.
   *
   * @param icon  the image replacing the text
   */
  public EdtRadioButton(Icon icon) {
    this("", false);
    JLabel iconLabel = new JLabel(icon);
    iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
    add(iconLabel);
  }

  /**
   * Creates an unselected radio button with the specified text.
   *
   * @param text  the string displayed on the radio button
   */
  public EdtRadioButton (String text) {
    this(text, null, false);
  }

  /**
   * Creates a radio button with the specified text
   * and selection state.
   *
   * @param text  the string displayed on the radio button
   * @param selected  if true, the button is initially selected;
   *                  otherwise, the button is initially unselected
   */
  public EdtRadioButton (String text, boolean selected) {
    this(text, null, selected);
  }

  /**
   * Creates a radio button that has the specified text, image,
   * and selection state.
   *
   * @param text  the string displayed on the radio button
   * @param icon  the image that the button should display
   * @param selected
   */
  public EdtRadioButton (String text, Icon icon, boolean selected) {
    super(text, icon, selected);
    setLayout(new MigLayout(new LC().fillX()));
    setBorderPainted(false);
  }
}
