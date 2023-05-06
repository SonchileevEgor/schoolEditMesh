package gui.elements;

import editor.AnchorType;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_Anchor;
import gui.EdtController;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JComboBox;

/**
 * Input control for anchors
 * @author alexeev
 */
public class AnchorChooser extends JComboBox {
  private ArrayList<String> _anch;
  
  /**
   *
   * @param ctrl
   * @param type
   */
  public AnchorChooser(EdtController ctrl, AnchorType type) {
    super();
    ArrayList<i_Anchor> anchors = ctrl.getEditor().anchors().getAnchorsByType(type);
    _anch = new ArrayList<String>();
    for (i_Anchor a : anchors) {
      try {
        addItem(ctrl.getAnchorTitle(a.id()));
        _anch.add(a.id());
      } catch (ExNoAnchor ex) {}
    }
//    util.Util.setFixedSize(this, new Dimension(100, 25));
  }

  /**
   * constructor of input control for anchors,
   * belong to the specified body
   * @param ctrl
   * @param type
   * @param bodyID
   * @throws editor.ExNoBody
   */
  public AnchorChooser(EdtController ctrl, AnchorType type, String bodyID) throws ExNoBody {
    super();
    HashMap<String, String> anch = ctrl.getBody(bodyID).getAnchors();
    _anch = new ArrayList<String>();
    for (Entry e : anch.entrySet()) {
      try {
        i_Anchor a = ctrl.getAnchor((String)e.getValue());
        if (a.getAnchorType() == type) {
          addItem(a.getTitle());
          _anch.add(a.id());
        }
      } catch (ExNoAnchor ex) { }
    }
    Dimension size = new Dimension(100, 25);
    setMaximumSize(size);
    setPreferredSize(size);
  }

  /**
   *
   * @return
   */
  public String getAnchorID() {
    return _anch.get(getSelectedIndex());
  }
}
