import config.Config;
import static config.Config.DEFAULT_FONT;
import editor.Editor;
import gui.*;
import gui.action.ActionFactory;
import gui.dialog.LicenseKeyInputDialog;
import gui.dialog.OpenSceneDialog;
import gui.inspector.InspectorPanel;
import gui.laf.AppColor;
import gui.layout.EdtCanvasPanel;
import gui.layout.EdtLayoutController;
import gui.mode.DefaultScreenMode;
import gui.statusbar.StatusStrip;
import gui.toolbox.ToolPanel;
import license.license.LicenseInfo;
import license.license.LicenseValidationException;
import license.pcidentifier.NoMACAddressException;
import license.pcidentifier.PcIdentifier;
import opengl.scenegl.Scene2d;
import opengl.scenegl.Scene3d;
import opengl.scenegl.SceneGL;
import util.Log;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;


class SplashWindow extends JWindow {
  private JLabel _label;
  private int _percent;
  private String _message = "";

  /**
   * Создание стартового экрана (splash-screen).
   * @param image изображение на экране.
   */
  public SplashWindow(ImageIcon image) {
    super();
    _label = new JLabel(image){
      @Override
      public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setComposite(AlphaComposite.Src);
        g.setColor(AppColor.ORANGE_LIGHT.color());
        g.fillRect(250, 370, (int)(1.2 * _percent), 15);
        g.setColor(AppColor.BLACK.color());
        g.drawRect(250, 370, 120, 15);
        g.setPaintMode();
        g.setFont(DEFAULT_FONT.value());
        g.drawString(_message, 250, 365);
      };
    };
    getContentPane().add(_label, BorderLayout.CENTER);
    pack();
    util.Util.setCenterLocation(null, this);
    setVisible(true);
  }

  /**
   * Обновление сплэш-скрина.
   * @param percent Процент загрузки.
   * @param message Сообщение о стадии загрузки.
   */
  public void render(int percent, String message) {
    _percent = percent;
    _message = message;
    revalidate();
    repaint();
  }
}

public class sch3dedit implements Runnable {
  static private EdtController _ctrl;
  static private MainEdtCanvas _canvas;


  private static synchronized void setLookAndFeel() {
    try {
      // set system-depending Look and Feel
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException |
            ClassNotFoundException |
            InstantiationException |
            IllegalAccessException e) {
    }

    //setting up default font for each interface element
    Enumeration<Object> keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value instanceof FontUIResource) {
        UIManager.put(key, new FontUIResource(DEFAULT_FONT.value()));
      }
    }

    // handle mouse press events when popup is visible
    // По умолчанию, при видимом JPopupMenu первое нажатие мыши (по любому месту экрана) скрывает меню, но не обрабатывается приложением.
    // Чтобы выключить это идиотское поведение, нужно установить соответствующий флаг в false.
    UIManager.put("PopupMenu.consumeEventOnClose", Boolean.FALSE);
    UIManager.put("ColorChooser.swatchesSwatchSize", new Dimension(12, 12));
    UIManager.put("ColorChooser.swatchesRecentSwatchSize", new Dimension(12, 12));
    UIManager.put("ColorChooser.swatchesDisplayedMnemonicIndex", 0);
    UIManager.put("ColorChooser.rgbDisplayedMnemonicIndex", 1);
    UIManager.put("CheckBoxMenuItem.checkIcon", new Icon() {
      @Override
      public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ButtonModel m = ((JMenuItem)c).getModel();
        // paint border
        g2.setPaint(m.isEnabled() ? Color.BLACK : Color.GRAY);
        g2.drawRect(2, 2, 20, 20);
        if( m.isSelected() ){
          g2.setPaint(m.isEnabled() ? Color.BLACK : Color.GRAY);
          g2.drawLine(7, 11, 11, 17);
          g2.drawLine(11, 17, 18, 6);
        }
      }

      @Override
      public int getIconWidth() {
        return 24;
      }

      @Override
      public int getIconHeight() {
        return 24;
      }
    });

    UIManager.put("CheckBox.rollover", Boolean.TRUE);
    UIManager.put("ToolTip.border", BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(0.2f, 0.6f, 1f), 1),
        BorderFactory.createEmptyBorder(2, 2, 2, 2)));

    //tooltip features
    ToolTipManager.sharedInstance().setInitialDelay(200);
    Toolkit.getDefaultToolkit().setDynamicLayout(false);
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
  }

  private static synchronized void createAndDisplayGUI() {
    OpenSceneDialog osd = new OpenSceneDialog();
    osd.setVisible(true);
    if (!osd.isAccepted()){
      Log.out.println("Application was closed.");
      System.exit(0);
    }
    Log.out.println("Application starts in " + (osd.is3d() ? "3D" : "2D") + " mode");
    
    final boolean is3d = osd.is3d();

    final SplashWindow splash = new SplashWindow(IconList.SPLASH.getImage());
    (new SwingWorker<Object, Object>(){
      @Override
      protected String doInBackground() {
        splash.render(0, "Загружаю ядро...");

        Editor edt = _ctrl.getEditor();
        SceneGL scene = is3d ?
            new Scene3d(edt) :
            new Scene2d(edt);

        splash.render(20, "Инициализирую GUI...");

        _canvas.setScene(scene);
        InspectorPanel ip = new InspectorPanel(_ctrl);
        StatusStrip ss = new StatusStrip(_ctrl);
        EdtCanvasPanel cpanel = new EdtCanvasPanel(_ctrl, _canvas);

        setKeyboardActions(cpanel);
        setKeyboardActions(ip);
        ToolPanel tpanel = new ToolPanel(_ctrl);
        _ctrl.addGlobalModeChangeListener(tpanel);
        JSplitPane centerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ip, cpanel);
        EdtFrame frame = new EdtFrame(_ctrl, tpanel, ss, centerPanel);
        _ctrl.setLayoutController(new EdtLayoutController(cpanel, ip, centerPanel, ss, tpanel, frame));
        frame.setJMenuBar(new EdtMenuBar(_ctrl));
        _ctrl.initDialogs();
        _ctrl.addEditorStateChangeListener(ip);
        _ctrl.notifyEditorStateChange();
        _ctrl.notifyGlobalModeChange();
        splash.render(70, "Инициализирую графику...");
        frame.setVisible(true); //!!SIC: именно в таком порядке. сначала показать окно, потом нарисовать

        splash.render(90, "Завершение загрузки...");
        return null;
      }
      @Override
      protected void done() {
        splash.dispose();
        _ctrl.redraw();

        // Setting mode is the last operation, because it includes mouse listeners linking.
        // Linking those listeners with canvas uninitialized causes exception throwing.
        SwingUtilities.invokeLater(new Runnable(){
          @Override
          public void run() {
            _canvas.initDispatchers();
            _ctrl.getMainCanvasCtrl().initMouseListeners();
            _ctrl.getMainCanvasCtrl().setMode(new DefaultScreenMode(_ctrl));
          }
        });
      }
    }).execute();
  }

  private static void setKeyboardActions(JComponent comp) {
    // Register actions in component's ActionMap.
    comp.getActionMap().put("SWITCH_SCENE_COLORS", ActionFactory.SWITCH_SCENE_COLORS.getAction(_ctrl));
    comp.getActionMap().put("SWITCH_VIEW_MODE", ActionFactory.SWITCH_VIEW_MODE.getAction(_ctrl));
    comp.getActionMap().put("SWITCH_PROJECTION", ActionFactory.SWITCH_PROJECTION.getAction(_ctrl));
    comp.getActionMap().put("SWITCH_SCENE_ANTIALIASING", ActionFactory.SWITCH_SCENE_ANTIALIASING.getAction(_ctrl));
    comp.getActionMap().put("HIDE_INSPECTOR_PANEL", ActionFactory.TOGGLE_INSPECTOR.getAction(_ctrl));
    comp.getActionMap().put("DELETE_FOCUSED_BODIES", ActionFactory.DELETE_FOCUSED_BODIES.getAction(_ctrl));

    // Register KeyStroke used to fire the action.  I am registering this with the
    // InputMap used when the component's parent window has focus.
    comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), "SWITCH_VIEW_MODE");
    comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "SWITCH_PROJECTION");
    comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "SWITCH_SCENE_ANTIALIASING");
    comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "SWITCH_SCENE_COLORS");
    comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, InputEvent.SHIFT_DOWN_MASK), "HIDE_INSPECTOR_PANEL");
    comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE_FOCUSED_BODIES");
  }

  /**
   * Force set path where JOGL dynamic library were locate on application base directory.
   * <p>Fixed problem: If application runs not from home directory it loss his dynamic libraries.
   */
  static public void setJoglLibraryPath() {
    try {
      String path = new File(sch3dedit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
      System.setProperty("java.library.path", path);
    } catch (URISyntaxException e) {
      System.out.println("Warning: Can't set dynamic library search path");
    }
  }

  public static void main(String[] args) {
    setJoglLibraryPath();
    Config.init();
    setLookAndFeel();
    if (args.length == 0) {
      _ctrl = new EdtController();
    } else {
      _ctrl = new EdtController(args[0]);
    }

    // Canvas should be initialized before GUI creation.
    _canvas = new MainEdtCanvas();
    _ctrl.setStlCanvasController(new StlEdtCanvasController(_ctrl, _canvas));
    _ctrl.setMainCanvasController(new MainEdtCanvasController(_ctrl, _canvas));

    sch3dedit app = new sch3dedit();
    
    // check licensed access On/Off
    if (config.Config.USE_LICENSE) {
        // trying to read license info.
        LicenseInfo licenseInfo = new LicenseInfo();
        String errMessage = "";
        boolean isLicenseValid = false;

        try {
          licenseInfo.loadFromFile(config.Config.LICENSE_INFO_FILE);
          licenseInfo.validate(PcIdentifier.getMACAddresses(), new Date());
          isLicenseValid = true;
        } catch( IOException ex ){
          errMessage = "Ошибка: не удалось загрузить файл лицензии.";
        } catch( NoMACAddressException |
                 LicenseValidationException ex ){
          errMessage = ex.getMessage();
        }

        if( !isLicenseValid ){
          JOptionPane.showMessageDialog(null, errMessage, "Ошибка лицензирования", JOptionPane.ERROR_MESSAGE);
          try {
            LicenseKeyInputDialog keyDialog = new LicenseKeyInputDialog();
            keyDialog.setVisible(true);
            if( keyDialog.accepted )
              isLicenseValid = true;
          } catch( UnknownHostException ex ){
            JOptionPane.showMessageDialog(null, "<html>Извините, не удалось установить связь с сервером лицензирования!<br>"
                    + "Проверьте настройки выхода в интернет.", "Ошибка подключения",
                    JOptionPane.ERROR_MESSAGE);
          }
        }

        if( isLicenseValid ){
          // GUI creation should be placed in the dedicated thread.
          SwingUtilities.invokeLater(app);
        } else {
          System.exit(0);
        }
    } else {
        // GUI creation should be placed in the dedicated thread.
        SwingUtilities.invokeLater(app);
    }
  }

  @Override
  public void run() {
    createAndDisplayGUI();
  }
}
