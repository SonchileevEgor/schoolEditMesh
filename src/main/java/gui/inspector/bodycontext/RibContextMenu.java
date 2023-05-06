package gui.inspector.bodycontext;

import editor.ExNoBody;
import editor.i_Anchor;
import gui.EdtController;
import gui.action.ActionFactory;

/**
 * Context menu of RibBody.
 * @author alexeev
 */
public class RibContextMenu extends BodyContextMenu {
  public RibContextMenu(EdtController ctrl, String bodyID) throws ExNoBody {
    super(ctrl, bodyID);
    i_Anchor a = ctrl.getDuplicateAnchor(bodyID);

    if (a != null) {
      add(ActionFactory.DISSECT_RIB.getAction(_ctrl, a.id()));
      add(ActionFactory.LABEL_RIB.getAction(_ctrl, a.id()));
      add(MenuItemFactory.createDivideRibMI(ctrl, a.id()));
    }

    addSeparator();

    if (ctrl.getBody(bodyID).isRenamable()) {
      add(MenuItemFactory.createRenameMI(ctrl, bodyID));
    }
    add(MenuItemFactory.createVisMI(ctrl, bodyID));
    add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createParamsMI(ctrl, bodyID));
  }
}
