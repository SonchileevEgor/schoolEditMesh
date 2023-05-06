package gui.toolbox;

import gui.toolbox.undo.UndoPanel;
import opengl.scenegl.i_GlobalModeChangeListener;
import gui.EdtController;
import gui.laf.AppColor;
import gui.ui.EdtPanel;
import javax.swing.JPanel;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import opengl.scenegl.SceneType;

/**
 * Container for toolbars.
 *
 * @author alexeev
 */
public class ToolPanel extends EdtPanel implements i_GlobalModeChangeListener {
  private final Scene3dToolBar _toolbar3d;
  private final Scene2dToolBar _toolbar2d;
  private final SceneStlToolBar _toolbarStl;

  /**
   * @param ctrl
   */
  public ToolPanel(EdtController ctrl){
    super(new MigLayout(new LC().fillX()));
    _toolbar2d = new Scene2dToolBar(ctrl);
    ctrl.addAppSettingsChangeListener(_toolbar2d); // мы можем изменять содержимое тулбара
    
    _toolbar3d = new Scene3dToolBar(ctrl);
    ctrl.addAppSettingsChangeListener(_toolbar3d); // мы можем изменять содержимое тулбара
    
    _toolbarStl = new SceneStlToolBar(ctrl);
    JPanel undoPanel = new UndoPanel(ctrl);
    JPanel toolBarWrapper = new JPanel();
    toolBarWrapper.setOpaque(false);
    toolBarWrapper.add(_toolbar2d);
    toolBarWrapper.add(_toolbar3d);
    toolBarWrapper.add(_toolbarStl);
    undoPanel.setOpaque(false);

    add(toolBarWrapper, new CC().dockWest());
    add(undoPanel, new CC().dockEast());

    setBackground(AppColor.LIGHT_GRAY.color());
  }

  @Override
  public void modeChanged(SceneType type) {
    _toolbar2d.setVisible(type == SceneType.Scene2D);
    _toolbar3d.setVisible(type == SceneType.Scene3D);
    _toolbarStl.setVisible(type == SceneType.SceneSTL);
    revalidate();
    repaint();
  }
}
