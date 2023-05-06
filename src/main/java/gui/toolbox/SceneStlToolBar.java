package gui.toolbox;

import gui.EdtController;
import gui.IconList;
import gui.action.ActionFactory;
import gui.action.EdtAction;
import gui.mode.ModeList;
import java.util.ArrayList;

/**
 *
 * @author Vladimir
 */
class SceneStlToolBar extends EdtToolBar {
    
    public SceneStlToolBar(EdtController ctrl) {
        super(ctrl);
        
        ArrayList<EdtAction> ActionList = new ArrayList<>();
        ActionList.add(ModeList.MODE_COVER.getAction(ctrl));
        ActionList.add(ActionFactory.FILE_IMPORT_3D.getAction(ctrl));
        ActionList.add(ActionFactory.FILE_SAVE_AS_3D.getAction(ctrl));
        
        add(new EdtToolButton(_ctrl, this, IconList.FILE_OPEN.getLargeIcon(), ActionList));
    }
    
}
