package gui.inspector.bodycontext;

import editor.ExNoBody;
import gui.EdtController;

/**
 *
 * @author alexeev
 */
public class DefaultBodyContextMenu extends BodyContextMenu {
  public DefaultBodyContextMenu(EdtController ctrl, String bodyID) throws ExNoBody {
    super(ctrl, bodyID);
    
    if(ctrl.getBody(bodyID).isRenamable()) {
      add(MenuItemFactory.createRenameMI(ctrl, bodyID));
    }
    add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createParamsMI(ctrl, bodyID));
  }
}
