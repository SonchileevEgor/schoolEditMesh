package gui.ui;
;
import java.util.ArrayList;
import javax.swing.JPopupMenu;

/**
 * Simple pop-up menu
 * Shown when corresponding ToolButton clicked
 * @author alexeev
 */
public class EdtToolMenu extends JPopupMenu {

  /**
   * Construct ToolMenu by the list of EdtToolMenuItem's
   * @param tools
   */
  public EdtToolMenu(ArrayList<EdtToolMenuItem> tools) {
    super();

    for(EdtToolMenuItem tb : tools) {
      add(tb);
    }
  }
  
  public EdtToolMenu() { super(); }
}
