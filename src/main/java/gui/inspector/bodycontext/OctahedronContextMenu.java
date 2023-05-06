package gui.inspector.bodycontext;

import editor.ExNoBody;
import gui.EdtController;

/**
 *
 * @author kaznin
 */
public class OctahedronContextMenu extends BodyContextMenu {
  public OctahedronContextMenu(EdtController ctrl, final String bodyID) throws ExNoBody {
    super(ctrl, bodyID);

    add(MenuItemFactory.createRenameMI(ctrl, bodyID));
    add(MenuItemFactory.createVisMI(ctrl, bodyID));
    add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createParamsMI(ctrl, bodyID));
  }
}
