package gui.inspector.bodycontext;

import editor.ExNoBody;
import gui.EdtController;

/**
 * Context menu for Cylinder and Cone section.
 * @author alexeev
 */
public class SectionContextMenu extends BodyContextMenu {
   public SectionContextMenu(EdtController ctrl, final String bodyID) throws ExNoBody {
    super(ctrl, bodyID);
    add(MenuItemFactory.createViewFromAboveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createRenameMI(ctrl, bodyID));
    add(MenuItemFactory.createVisMI(ctrl, bodyID));
    add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createParamsMI(ctrl, bodyID));
  }
}
