package gui.menubar;

import opengl.scenegl.i_GlobalModeChangeListener;
import gui.EdtController;
import gui.IconList;
import gui.dialog.ConstructBodyDialog;
import gui.layout.i_LayoutChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import opengl.colortheme.CurrentTheme;
import opengl.scenegl.SceneType;

/**
 * Menu "View"/"Вид" on JMenuBar
 * @author alexeev
 */
public class ViewMenu extends JMenu {
  private EdtController _ctrl;
  private JMenuItem _appSettingsMenuItem;
  private ConsoleMenuItem _consoleMenuItem;
  private ManipulatorMenuItem _manipulatorMenuItem;
  private CanvasToolsMenuItem _toolsNemuItem;

  public ViewMenu(EdtController ctrl) {
    super("Вид");
    _ctrl = ctrl;
    initColorSettingsMenuItem();
    initThemeMenu();
    addSeparator();
    initConsoleMenuItem();
    initManipulatorMenuItem();
    initToolsMenuItem();
    
    initCreateMenuItem();
  }

  private void initConsoleMenuItem() {
    _consoleMenuItem = new ConsoleMenuItem();
    _ctrl.getLayoutController().addLayoutListener(_consoleMenuItem);

    _consoleMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = ((AbstractButton)e.getSource()).isSelected();
        _ctrl.getLayoutController().setConsoleVisible(selected);
      }
    });
    add(_consoleMenuItem);
  }

  private void initManipulatorMenuItem() {
    _manipulatorMenuItem = new ManipulatorMenuItem();
    _ctrl.addGlobalModeChangeListener(_manipulatorMenuItem);
    _ctrl.getLayoutController().addLayoutListener(_manipulatorMenuItem);

    _manipulatorMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = ((AbstractButton)e.getSource()).isSelected();
        _ctrl.getLayoutController().setRotationWidgetVisible(selected);
      }
    });
    add(_manipulatorMenuItem);
  }

  private void initColorSettingsMenuItem() {
    _appSettingsMenuItem = new JMenuItem("Настройки", IconList.OPTIONS.getMediumIcon());
    _appSettingsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
    _appSettingsMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        _ctrl.showAppSettingsDialog();
      }
    });
    add(_appSettingsMenuItem);
  }

  private void initToolsMenuItem() {
    _toolsNemuItem = new CanvasToolsMenuItem();
    _ctrl.getLayoutController().addLayoutListener(_toolsNemuItem);
    _toolsNemuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean selected = ((AbstractButton)e.getSource()).isSelected();
        _ctrl.getLayoutController().setCanvasToolPanelExpanded(selected);
      }
    });
    add(_toolsNemuItem);
  }

  private void initThemeMenu() {
    JMenu themeMenu = new JMenu("Цветовая схема");
    themeMenu.setIcon(IconList.COLOR_SCH.getMediumIcon());
    for (int i = 0; i < CurrentTheme.size(); i++) {
      final int index = i;
      JMenuItem mi = new JMenuItem(CurrentTheme.getName(i), IconList.COLOR_SCH.getMediumIcon());
      mi.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
          CurrentTheme.load(index);
          CurrentTheme.notifyThemeChange();
          _ctrl.getEditor().bodyMgr().applyTheme();
          _ctrl.getEditor().anchMgr().applyTheme();
          _ctrl.redraw();
        }
      });
      themeMenu.add(mi);
      add(themeMenu);
    }
  }

  class ConsoleMenuItem extends JCheckBoxMenuItem implements i_LayoutChangeListener {
    public ConsoleMenuItem() {
      super("Консоль ошибок");
      setSelected(false);
    }
    @Override
    public void layoutChanged() {
      setSelected(_ctrl.getLayoutController().isConsoleVisible());
    }
  }

  class ManipulatorMenuItem extends JCheckBoxMenuItem implements i_GlobalModeChangeListener,
      i_LayoutChangeListener {
    public ManipulatorMenuItem() {
      super("Манипулятор вращения");
      setSelected(false);
    }

    @Override
    public void modeChanged(SceneType is3d) {
      // disable manipulator when scene is 2D.
      setEnabled(is3d.is3d());
      if( !is3d.is3d() ){
        setSelected(false);
      }
    }

    @Override
    public void layoutChanged() {
      setSelected(_ctrl.getLayoutController().isRotationWidgetVisible());
    }
  }

  class CanvasToolsMenuItem extends JCheckBoxMenuItem implements i_LayoutChangeListener {
    public CanvasToolsMenuItem() {
      super("Панель инструментов");
      setSelected(true);
    }
    @Override
    public void layoutChanged() {
      setSelected(_ctrl.getLayoutController().isCanvasToolPanelExpanded());
    }
  }
  
  private void initCreateMenuItem() {
    JMenuItem createMI = new JMenuItem("Создать тело");
    createMI.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new ConstructBodyDialog(_ctrl).setVisible(true);
      }
    });
    add(createMI);
  }
}
