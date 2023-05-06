package gui.ui;

import java.awt.Component;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author alexeev
 */
public class EdtTabPanel extends EdtPanel {
  protected int _compCount;
  protected HashMap<Integer, Component[]> _components;

  public EdtTabPanel() {
    super();
    setLayout(new MigLayout(new LC().fillX().hideMode(3).insetsAll("0")));
    _compCount = 0;
    _components = new HashMap<Integer, Component[]>();
  }

  /**
   * Add labeled item with help text specified.
   * Help text may be null.
   *
   * @param label
   * @param helpText
   * @param comp
   * @param compWidth
   *    длина компоненты ввода
   * @param compHeight
   *    высота компоненты ввода
   * @return
   */
  public int addItem(String label, String helpText, JComponent comp, int compWidth, int compHeight) {
    EdtLabel l = (helpText == null) ? new EdtLabel(label) : new EdtLabel(label, helpText);
    _components.put(_compCount, new Component[]{l, comp});
    add(l, new CC().cell(0, 2 * _compCount).alignX("left").gapX("5", "5").gapY("0", "0"));
    add(comp, new CC().cell(1, 2 * _compCount).alignX("right").gapX("5", "5").gapY("0", "0")
            .width(String.format("%d!", compWidth)).height(String.format("%d!", compHeight)));

    addSeparator();

    return _compCount++;
  }

  /**
   * Add labeled item with help text specified.
   * Help text may be null.
   *
   * @param label
   * @param helpText
   * @param comp
   * @return
   */
  public int addItem(String label, String helpText, JComponent comp) {
    EdtLabel l = (helpText == null) ? new EdtLabel(label) : new EdtLabel(label, helpText);
    _components.put(_compCount, new Component[]{l, comp});
    add(l, new CC().cell(0, 2 * _compCount).alignX("left").gapX("5", "5").gapY("0", "0"));
    add(comp, new CC().cell(1, 2 * _compCount).alignX("right").gapX("5", "5").gapY("0", "0")
            .growX());

    addSeparator();

    return _compCount++;
  }

  /**
   * Add labeled item without help pictogram.
   * Alias for <code>addItem(label, null, comp)</code>.
   *
   * @param label
   * @param comp
   * @return
   */
  public int addItem(String label, JComponent comp) {
    return addItem(label, null, comp);
  }

  public int addItem(JComponent comp) {
    add(comp, new CC().cell(0, 2 * _compCount, 2, 1).alignX("center").gapX("5", "5").gapY("0", "0")
            .growX());
    _components.put(_compCount, new Component[]{comp});

    addSeparator();

    return _compCount++;
  }

  public void setFieldAtIndexVisible(int index, boolean visible) {
    for (Component comp : _components.get(index)) {
      comp.setVisible(visible);
    }
  }

  private void addSeparator(){
    JPanel separator = new JPanel(new MigLayout(new LC().fillX().insetsAll("0")));
    separator.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    add(separator, new CC().height("2!").cell(0, 2 * _compCount + 1, 2, 1).growX());
  }

  @Override
  public void removeAll() {
    super.removeAll();
    _compCount = 0;
    _components.clear();
  }
}
