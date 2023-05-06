package gui.inspector.bodycontext;

import editor.ExNoBody;
import editor.ExNoBuilder;
import gui.EdtController;
import gui.mode.ModeList;

/**
 * Context menu of PlaneBody
 * @author alexeev
 */
public class PlaneContextMenu extends BodyContextMenu {
  public PlaneContextMenu(EdtController ctrl, String bodyID) throws ExNoBody {
    super(ctrl, bodyID);
    add(MenuItemFactory.createRenameMI(ctrl, bodyID));
    add(MenuItemFactory.createVisMI(ctrl, bodyID));
    add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createViewFromAboveMI(ctrl, bodyID));

    /**
     * Если плоскость построена по точке и вектору нормали,
     * то её можно вращать.
     */
    try {
      if (_ctrl.getBuilder(bodyID).alias().equals("Plane"))
        add(ModeList.MODE_ROTATE_PLANE.getAction(_ctrl, bodyID));
    } catch (ExNoBuilder ex) {}

    addSeparator();

    add(MenuItemFactory.createParamsMI(ctrl, bodyID));
  }
}
