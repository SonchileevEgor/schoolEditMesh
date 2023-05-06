package gui.elements;

import bodies.BodyType;
import editor.i_Body;
import editor.i_BodyContainer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 * Input control for choosing bodies from list
 * filtered by types.
 * 
 * @author alexeev
 */
public class BodyChooserList extends JList {  
  
  private BodyChooserList(i_BodyContainer bd) {
    super();
    _bodies = bd;
    
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setLayoutOrientation(JList.HORIZONTAL_WRAP);
    setVisibleRowCount(-1);
  }
  /**
   *
   * @param bd
   * @param types
   */
  public BodyChooserList(i_BodyContainer bd, List<BodyType> types) {
    this(bd);
    _bd = new ArrayList<i_Body>();
    
    for (i_Body b : bd.getAllBodies())
      if (types.contains(b.type()))
        _bd.add(b);
    
    String[] bodyTitles = new String[_bd.size()];
    for (int i = 0; i < _bd.size(); i++) {
      bodyTitles[i] = _bd.get(i).alias() + " " + _bd.get(i).getTitle();
    }
    
    setListData(bodyTitles);
  }
  /**
   * Constructor with specified id of the body that could be restricted.
   * 
   * @param bd
   * @param types
   * @param restrictBodyID ID of the body that could be restricted
   */
  public BodyChooserList(i_BodyContainer bd, List<BodyType> types, String restrictBodyID) {
    this(bd);
    _bd = new ArrayList<i_Body>();
    
    for (i_Body b : bd.getAllBodies())
      if (types.contains(b.type()) && !b.id().equals(restrictBodyID))
        _bd.add(b);
    
    String[] bodyTitles = new String[_bd.size()];
    for (int i = 0; i < _bd.size(); i++) {
      bodyTitles[i] = _bd.get(i).alias() + " " + _bd.get(i).getTitle();
    }
    
    setListData(bodyTitles);
  }
  
  public void rebuild(List<BodyType> types) {
    _bd.clear();
    for (i_Body b : _bodies.getAllBodies())
      if (types.contains(b.type()))
        _bd.add(b);
    
    String[] bodyTitles = new String[_bd.size()];
    for (int i = 0; i < _bd.size(); i++) {
      bodyTitles[i] = _bd.get(i).alias() + " " + _bd.get(i).getTitle();
    }
    setListData(bodyTitles);
  }
  
  public void rebuild(List<BodyType> types, String restrictBodyID) {
    _bd.clear();
    for (i_Body b : _bodies.getAllBodies())
      if (types.contains(b.type()) && !b.id().equals(restrictBodyID))
        _bd.add(b);
    
    String[] bodyTitles = new String[_bd.size()];
    for (int i = 0; i < _bd.size(); i++) {
      bodyTitles[i] = _bd.get(i).alias() + " " + _bd.get(i).getTitle();
    }
    setListData(bodyTitles);
  }
  
  /**
   *
   * @return
   */
  public i_Body getBody() {
    return _bd.get(getSelectedIndex());
  }
  
  private i_BodyContainer _bodies;
  private ArrayList<i_Body> _bd;
}
