package gui.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import javax.swing.JComponent;

/**
 * Декоратор, позволяющий компонентам быть полупрозрачными (иметь альфа-канал).
 * @author alexeev
 */
public class AlphaDecorator extends JComponent {
  private JComponent _comp;

  public AlphaDecorator(JComponent comp) {
		_comp = comp;
		setLayout(new BorderLayout());
		setOpaque(false);
		comp.setOpaque(false);
		add(comp, BorderLayout.CENTER);
	}

	/**
	 *  Paint the background using the background Color of the
	 *  contained component
	 */
	@Override
	public void paintComponent(Graphics g) {
    super.paintComponent(g);
		g.setColor(_comp.getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
