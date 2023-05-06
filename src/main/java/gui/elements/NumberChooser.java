package gui.elements;

import javax.swing.JComboBox;

/**
 *
 * @author rita
 */
public class NumberChooser extends JComboBox {
  private int[] _num;

  /**
   * Constructor of number chooser by first and last number value.
   * Combobox contains all natural number values from first to last.
   * @param first start number
   * @param last finish number
   */
  public NumberChooser(int first, int last) {
    super();
    if (last > first) {
      _num = new int[last - first + 1];
      for (int i = 0; first + i <= last; i++) {
        _num[i] = first + i;
        addItem(first + i);
      }
    } else {
      _num = new int[0];
    }
  }

  /**
   * Selected number in combobox by combobox index
   * @return number
   */
  public int getNumber() {
    return _num[getSelectedIndex()];
  }
}
