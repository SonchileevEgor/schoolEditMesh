package gui.dialog;

import gui.elements.ButtonLabel;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Окно справки.
 * @author alexeev
 */
public class HelpDialog extends JDialog {
  public HelpDialog(JFrame owner) {
    super(owner, "Справка");
    setLayout(new GridBagLayout());

    addItem("Esc", "стандартный режим");
    addItem("P", "режим постановки точек");
    addItem(new String[]{"Ctrl", "Z"}, "отменить");
    addItem(new String[]{"Ctrl", "Y"}, "повторить");
    addItem(new String[]{"Ctrl", "P"}, "смена проекции");
    addItem("W", "поворот камеры влево");
    addItem("A", "поворот камеры вправо");
    addItem("S", "поворот камеры вниз");
    addItem("D", "поворот камеры вверх");
    addItem(new String[]{"Ctrl", "M"}, "карандаш / цвет");
    addItem(new String[]{"Ctrl", "C"}, "цветовая схема");
    addItem(new String[]{"Ctrl", "A"}, "режим сглаживания");
    addItem(new String[]{"Ctrl", "+"}, "увеличить");
    addItem(new String[]{"Ctrl", "-"}, "уменьшить");
    addItem(new String[]{"Shift", "Esc"}, "скрыть / показать инспектор");

    setResizable(false);
    setModal(true);
    pack();
    setLocationRelativeTo(owner);
  }

  private void addItem(String[] keys, String description) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = GridBagConstraints.RELATIVE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 5, 0, 0);
    JPanel keysPanel = new JPanel();
    keysPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    for (int i = 0; i < keys.length - 1; i++) {
      keysPanel.add(new ButtonLabel(keys[i]));
      keysPanel.add(new JLabel("+"));
    }
    keysPanel.add(new ButtonLabel(keys[keys.length - 1]));
    add(keysPanel, gbc);

    gbc.gridx = 1;
    gbc.gridy = GridBagConstraints.RELATIVE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 5, 0, 5);
    add(new JLabel(description, JLabel.RIGHT), gbc);
  }

  private void addItem(String key, String description) {
    addItem(new String[]{key}, description);
  }
}
