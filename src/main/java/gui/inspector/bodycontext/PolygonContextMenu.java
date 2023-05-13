package gui.inspector.bodycontext;

import editor.ExNoBody;
import editor.i_Anchor;
import gui.EdtController;
import gui.IconList;
import gui.action.ActionFactory;
import javax.swing.JMenu;

/**
 *
 * @author alexeev
 */
public class PolygonContextMenu extends BodyContextMenu {
  public PolygonContextMenu(EdtController ctrl, String bodyID) throws ExNoBody {
    super(ctrl, bodyID);
    i_Anchor a = ctrl.getDuplicateAnchor(bodyID);

    JMenu circleMenu = new JMenu("Окружность...");
    circleMenu.setIcon(IconList.CIRCLE_TRIANGLE.getMediumIcon());

    if (a != null) {
      circleMenu.add(ActionFactory.INSCRIBE_CIRCLE.getAction(ctrl, a.id()));
      circleMenu.add(ActionFactory.CIRCUMSCRIBE_CIRCLE.getAction(ctrl, a.id()));
    }
    add(circleMenu);

    addSeparator();

    if (ctrl.getMainCanvasCtrl().is3d()) {
      add(MenuItemFactory.createViewFromAboveMI(ctrl, bodyID));
      if (a != null) {
        add(ActionFactory.PLANE_BY_POLYGON.getAction(ctrl, a.id()));
      }
      addSeparator();
    }

    if (ctrl.getBody(bodyID).isRenamable()) {
      add(MenuItemFactory.createRenameMI(ctrl, bodyID));
    }
    add(MenuItemFactory.createVisMI(ctrl, bodyID));
    add(MenuItemFactory.buildRhombicDod(ctrl, bodyID));
    add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

    addSeparator();

    add(MenuItemFactory.createParamsMI(ctrl, bodyID));
  }
}