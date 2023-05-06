package gui.toolbox;

import gui.EdtController;
import gui.IconList;
import gui.action.ActionFactory;
import gui.action.EdtAction;
import java.util.ArrayList;

/**
 *
 * @author Vladimir
 */
public class PrintingToolBar extends EdtToolBar {
    
    public PrintingToolBar(EdtController ctrl) {
        super(ctrl);
        
        ArrayList<EdtAction> ActionList = new ArrayList<>();
        ActionList.add(ActionFactory.APP_CLOSE.getAction(ctrl));
        
        add(new EdtToolButton(_ctrl, this, IconList.EMPTY.getLargeIcon(), ActionList));
    }
    
}
