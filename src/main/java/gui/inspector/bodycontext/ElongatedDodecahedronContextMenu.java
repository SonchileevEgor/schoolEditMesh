package gui.inspector.bodycontext;

import editor.ExNoBody;
import gui.EdtController;
import gui.IconList;
import gui.action.ActionFactory;
import javax.swing.JMenu;

/**
 *
 * @author kaznin
 */
public class ElongatedDodecahedronContextMenu extends BodyContextMenu {
  public ElongatedDodecahedronContextMenu(EdtController ctrl, final String bodyID) throws ExNoBody {
    super(ctrl, bodyID);

    add(MenuItemFactory.createRenameMI(ctrl, bodyID));
    add(MenuItemFactory.createVisMI(ctrl, bodyID));
    add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createParamsMI(ctrl, bodyID));
  }
}
