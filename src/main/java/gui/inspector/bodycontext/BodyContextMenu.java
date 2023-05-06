package gui.inspector.bodycontext;

import editor.ExNoBody;
import editor.i_Body;
import gui.EdtController;
import javax.swing.JPopupMenu;

/**
 * Body context menu.
 * Displays on right mouse click on the BodyPanel
 * @author alexeev
 */
public class BodyContextMenu extends JPopupMenu {
  protected EdtController _ctrl;
  protected String _bodyID;

  /**
   * Create body context menu.
   * @param ctrl
   * @param bodyID ID of the body
   * @throws ExNoBody
   */
  protected BodyContextMenu(final EdtController ctrl, final String bodyID) throws ExNoBody {
    super();
    _ctrl = ctrl;
    _bodyID = bodyID;
  }
}