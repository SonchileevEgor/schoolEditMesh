package gui.elements;

import bodies.BodyType;
import editor.i_Body;
import editor.i_BodyContainer;
import java.util.ArrayList;
import javax.swing.JComboBox;

/**
 * Input control for bodies of specified types
 * @author alexeev
 */
public class BodyChooserComboBox extends JComboBox {
  private ArrayList<i_Body> _bd;

  /**
   *
   * @param bd
   * @param types
   */
  public BodyChooserComboBox(i_BodyContainer bd, ArrayList<BodyType> types) {
    super();
    _bd = new ArrayList<i_Body>();

    for (i_Body b : bd.getAllBodies()) {
      if (types.contains(b.type())){
        addItem(b.alias() + " " + b.getTitle());
        _bd.add(b);
      }
    }
  }
  
  /**
   * 
   * @param bd
   * @param type
   * @param bodyID 
   */
  public BodyChooserComboBox(i_BodyContainer bd, BodyType type, String bodyID) {
    super();
    _bd = new ArrayList<i_Body>();
    for (i_Body b : bd.getAllBodies()) {
      if (b.type() == type && !b.id().equals(bodyID)){
        addItem(b.alias() + " " + b.getTitle());
        _bd.add(b);
      }
    }
  }
  
  /**
   *
   * @return
   */
  public i_Body getBody() {
    return _bd.get(getSelectedIndex());
  }
}