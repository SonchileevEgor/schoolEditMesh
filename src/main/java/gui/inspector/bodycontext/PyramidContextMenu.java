package gui.inspector.bodycontext;

import editor.ExNoBody;
import gui.EdtController;
import gui.IconList;
import gui.action.ActionFactory;
import javax.swing.JMenu;

/**
 *
 * @author alexeev
 */
public class PyramidContextMenu extends BodyContextMenu {
  public PyramidContextMenu(EdtController ctrl, final String bodyID) throws ExNoBody {
    super(ctrl, bodyID);
    JMenu sphereMenu = new JMenu("Сфера...");
    sphereMenu.setIcon(IconList.SPHERE.getMediumIcon());
    sphereMenu.add(ActionFactory.INSCRIBE_SPHERE.getAction(ctrl, bodyID));
    sphereMenu.add(ActionFactory.CIRCUMSCRIBE_SPHERE.getAction(ctrl, bodyID));
    add(sphereMenu);

    addSeparator();

    add(MenuItemFactory.createRenameMI(ctrl, bodyID));
    add(MenuItemFactory.createVisMI(ctrl, bodyID));
    add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createViewFromAboveMI(ctrl, bodyID));

    addSeparator();
    
    add(MenuItemFactory.createParamsMI(ctrl, bodyID));
  }
}