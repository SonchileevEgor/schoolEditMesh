package gui.inspector.bodycontext;

import editor.ExNoBody;
import editor.i_Anchor;
import gui.EdtController;
import gui.IconList;
import gui.action.ActionFactory;
import gui.action.CreateDialogActionFactory;
import javax.swing.JMenu;

/**
 * Context menu of TriangleBody
 * @author alexeev
 */
public class TriangleContextMenu extends BodyContextMenu {

  public TriangleContextMenu(EdtController ctrl, String bodyID) throws ExNoBody {
    super(ctrl, bodyID);
    i_Anchor a = ctrl.getDuplicateAnchor(bodyID);

    JMenu circleMenu = new JMenu("Окружность...");
    JMenu pointsMenu = new JMenu("Замечательные точки...");
    JMenu linesMenu = new JMenu("Провести...");

    linesMenu.setIcon(IconList.CHEVIANA_TRIANGLE.getMediumIcon());
    circleMenu.setIcon(IconList.CIRCLE_TRIANGLE.getMediumIcon());
    pointsMenu.setIcon(IconList.POINT_TRIANGLE.getMediumIcon());

    if (a != null) {
      circleMenu.add(ActionFactory.INSCRIBE_CIRCLE.getAction(ctrl, a.id()));
      circleMenu.add(ActionFactory.CIRCUMSCRIBE_CIRCLE.getAction(ctrl, a.id()));
      pointsMenu.add(ActionFactory.CENTROID.getAction(ctrl, bodyID));
      pointsMenu.add(ActionFactory.ORTHOCENTER.getAction(ctrl, bodyID));
    }
    circleMenu.add(CreateDialogActionFactory.ESCRIBE_CIRCLE.getAction(ctrl, bodyID));
    linesMenu.add(CreateDialogActionFactory.MEDIAN.getAction(ctrl, bodyID));
    linesMenu.add(CreateDialogActionFactory.BISECTRIX.getAction(ctrl, bodyID));
    linesMenu.add(CreateDialogActionFactory.HEIGHT.getAction(ctrl, bodyID));
    linesMenu.add(CreateDialogActionFactory.MIDDLE_PERPENDICULAR.getAction(ctrl, bodyID));

    add(linesMenu);
    add(circleMenu);
    add(pointsMenu);

    if( ctrl.getMainCanvasCtrl().is3d() ) {
      addSeparator();
      add(MenuItemFactory.createViewFromAboveMI(ctrl, bodyID));
      if( a != null ){
        add(ActionFactory.PLANE_BY_TRIANGLE.getAction(ctrl, bodyID));
      }
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