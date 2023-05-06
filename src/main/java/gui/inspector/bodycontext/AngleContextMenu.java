package gui.inspector.bodycontext;

import editor.ExNoBody;
import gui.EdtController;

/**
 * Context menu for arc.
 * @author rita
 */
public class AngleContextMenu extends BodyContextMenu {
  public AngleContextMenu(final EdtController ctrl, final String bodyID) throws ExNoBody {
    super(ctrl, bodyID);
    if (ctrl.getMainCanvasCtrl().is3d()) {
      add(MenuItemFactory.createViewFromAboveMI(ctrl, bodyID));
    }

    addSeparator();

    add(MenuItemFactory.createRenameMI(ctrl, bodyID));
    add(MenuItemFactory.createVisMI(ctrl, bodyID));
    add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createParamsMI(ctrl, bodyID));
  }
}
