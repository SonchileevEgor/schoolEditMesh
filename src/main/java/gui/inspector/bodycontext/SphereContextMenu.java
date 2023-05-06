package gui.inspector.bodycontext;

import editor.ExNoBody;
import gui.EdtController;

/**
 * Context menu for sphere.
 * @author alexeev
 */
public class SphereContextMenu extends BodyContextMenu {
  public SphereContextMenu(EdtController ctrl, final String bodyID) throws ExNoBody {
    super(ctrl, bodyID);
    add(MenuItemFactory.createRenameMI(ctrl, bodyID));
    add(MenuItemFactory.createVisMI(ctrl, bodyID));
    add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createParamsMI(ctrl, bodyID));
  }
}
