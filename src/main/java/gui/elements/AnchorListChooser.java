package gui.elements;

import editor.AnchorType;
import gui.EdtController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Input control for choosing list of anchors
 * @author alexeev
 */
public class AnchorListChooser extends JPanel {
  public AnchorListChooser(EdtController ctrl, AnchorType type, int num) {
    super();
    _ctrl = ctrl;
    _type = type;
    
    setBorder(new EmptyBorder(10, 0, 0, 0));
    
    GridBagLayout gbl = new GridBagLayout();
    setLayout(gbl);

    GridBagConstraints gbc;

    for (int i = 0; i < num; i++) {
      gbc = new GridBagConstraints();
      JLabel l = new JLabel("Вершина ".concat(Integer.toString(i + 1)));
      AnchorChooser ac = new AnchorChooser(_ctrl, _type);
      _choosers.add(ac);
      
      gbc.gridx = 0;
      gbc.gridy = i;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.anchor = GridBagConstraints.EAST;
      gbc.insets = new Insets(3, 0, 3, 5);

      gbl.setConstraints(l, gbc);

      gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = i;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.insets = new Insets(3, 0, 3, 5);

      gbl.setConstraints(ac, gbc);
      
      add(l);
      add(ac);
    }
    
    
  }
  
  /**
   * Number of choosers on panel
   * @return
   */
  public int length() {
    return _choosers.size();
  }
  
  /**
   * increase number of AnchorChooser's on the panel
   */
  public void inc() {
    GridBagLayout gbl = (GridBagLayout)getLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    JLabel l = new JLabel("Вершина ".concat(Integer.toString(length() + 1)));
    AnchorChooser ac = new AnchorChooser(_ctrl, _type);
    
    gbc.gridx = 0;
    gbc.gridy = length();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(3, 0, 3, 5);
    
    gbl.setConstraints(l, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = length();
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(3, 0, 3, 5);

    gbl.setConstraints(ac, gbc);
    
    add(l);
    add(ac);
    _choosers.add(ac);
    
    repaint();
  }
  
  /**
   * remove the last AnchorChooser on the panel
   */
  public void dec() {
    remove(length() * 2 - 1);
    remove(length() * 2 - 2);
    _choosers.remove(length() - 1);
    
    repaint();
  }
  
  /**
   * ID of anchor at chooser #idx
   * @param idx Number of AnchorChooser
   * @return
   */
  public String getAnchorID (int idx) {
    return _choosers.get(idx).getAnchorID();
  }
  
  /**
   * check if there is a duplicate choice
   * @return <strong>true</strong> if duplication occurred
   */
  public boolean hasDuplication() {
    for (int i = 0; i < length() - 1; i++) {
      for (int j = i + 1; j < length(); j++) {
        if (_choosers.get(i).getSelectedItem().equals(_choosers.get(j).getSelectedItem()))
          return true;
      }
    }
    return false;
  }
  
  private EdtController _ctrl;
  private ArrayList<AnchorChooser> _choosers = new ArrayList<AnchorChooser>();
  private AnchorType _type;
}
