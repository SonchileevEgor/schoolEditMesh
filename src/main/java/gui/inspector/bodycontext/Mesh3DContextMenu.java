/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui.inspector.bodycontext;

import editor.ExNoBody;
import gui.EdtController;

/**
 *
 * @author e-son
 */
public class Mesh3DContextMenu extends BodyContextMenu {
    public Mesh3DContextMenu(EdtController ctrl, final String bodyID) throws ExNoBody {
        super(ctrl, bodyID);

        add(MenuItemFactory.createRenameMI(ctrl, bodyID));
        add(MenuItemFactory.createVisMI(ctrl, bodyID));
        add(MenuItemFactory.createVisMIPnts(ctrl, bodyID));

        add(MenuItemFactory.createRemoveMI(ctrl, bodyID));

        addSeparator();

        add(MenuItemFactory.createParamsMI(ctrl, bodyID));
    }
}
