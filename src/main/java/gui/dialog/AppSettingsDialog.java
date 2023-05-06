package gui.dialog;

import config.Config;
import static config.Config.AXES_VISIBLE;
import static config.Config.EXTENSIONS;
import static config.Config.GRID_INTENSITY;
import static config.Config.GRID_VISIBLE;
import static config.Config.LINE_WIDTH;
import static config.Config.MATH_FONT_SIZE;
import static config.Config.OPTIONAL_CONFIG;
import static config.Config.OPT_CONFIG_DIR;
import static config.Config.POINT_TITLE_VISIBLE;
import static config.Config.POINT_WIDTH;
import static config.Config.PRECISION;
import opengl.colorgl.ColorGL;
import opengl.scenegl.i_GlobalModeChangeListener;
import gui.EdtController;
import gui.elements.HintTextField;
import gui.elements.LineThicknessComboBox;
import gui.elements.PointThicknessChooserPanel;
import gui.elements.Thickness;
import gui.extensions.ExtensionsPanel;
import gui.i_AppSettingsChangeListener;
import gui.ui.EdtColorChooser;
import gui.ui.EdtInputDialog;
import gui.ui.EdtTabPanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import latex.ImageGenerator;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import opengl.ProjectionMode;
import opengl.colortheme.CurrentTheme;
import opengl.colortheme.i_ColorThemeChangeListener;
import opengl.scenegl.SceneType;
import org.apache.commons.io.FilenameUtils;

/**
 * Application settings dialog.
 *
 * @author alexeev
 */
public class AppSettingsDialog extends EdtInputDialog implements i_ColorThemeChangeListener,
    i_AppSettingsChangeListener, i_GlobalModeChangeListener {
  // List of possible font sizes.
  private static final ArrayList<Integer> FONTSIZE =
          new ArrayList<>(Arrays.asList(14, 16, 18, 20, 22, 24, 26, 32, 40, 48));
  
  private EdtController _ctrl;

  private final EdtColorChooser _pointsPanel;
  private final EdtColorChooser _carcassPanel;
  private final EdtColorChooser _facetsPanel;

  private final JCheckBox _axesVisibleCheckBox;
  private final JCheckBox _gridVisibleCheckBox;
  private final JCheckBox _antialiasingCheckBox;
  private final JCheckBox _pointTitlesVisibleCheckBox;
  private final JCheckBox _changeProjectionCheckBox;
  private final JComboBox _precisionComboBox;
  private final PointThicknessChooserPanel _pointThicknessChooser;
  private final LineThicknessComboBox _lineThicknessChooser;
  private final JSlider _fontSizeSlider;
  private final JSlider _gridIntensitySlider;
  
  private final ExtensionsPanel _extPanel;
  
  /**
   * Constructs a settings dialog
   * @param ctrl
   */
  public AppSettingsDialog(EdtController ctrl) {
    super(ctrl.getFrame(), "Настройки");
    _ctrl = ctrl;
    CurrentTheme.addThemeChangeListener(this);

    _pointsPanel = new EdtColorChooser(_ctrl, CurrentTheme.getColorTheme().getPointsColorGL(), EdtColorChooser.SOLID);
    _carcassPanel = new EdtColorChooser(_ctrl, CurrentTheme.getColorTheme().getCarcassFiguresColorGL(), EdtColorChooser.SOLID);
    _facetsPanel = new EdtColorChooser(_ctrl, CurrentTheme.getColorTheme().getFacetsFiguresColorGL(), EdtColorChooser.SOLID);

    _axesVisibleCheckBox = new JCheckBox();
    _antialiasingCheckBox = new JCheckBox();
    _gridVisibleCheckBox = new JCheckBox();
    _pointTitlesVisibleCheckBox = new JCheckBox();
    _changeProjectionCheckBox = new JCheckBox();
    _lineThicknessChooser = new LineThicknessComboBox(Thickness.get(LINE_WIDTH.value()));
    _pointThicknessChooser = new PointThicknessChooserPanel(Thickness.get(POINT_WIDTH.value()));
    _precisionComboBox = new JComboBox(new Integer[] {0, 1, 2, 3, 4, 5});
    
    _fontSizeSlider = new JSlider(0, 9, FONTSIZE.indexOf(MATH_FONT_SIZE.value()));
    _fontSizeSlider.setMajorTickSpacing(5);
    _fontSizeSlider.setMinorTickSpacing(1);
    _fontSizeSlider.setPaintTicks(true);
    
    _gridIntensitySlider = new JSlider(0, 100, (int)(100 * GRID_INTENSITY.value()));
    _gridIntensitySlider.setMajorTickSpacing(50);
    _gridIntensitySlider.setMinorTickSpacing(10);
    _gridIntensitySlider.setPaintTicks(true);
    
    _extPanel = new ExtensionsPanel(_ctrl);

    refreshSettings();
    refreshModeDependingOptions(_ctrl.getMainCanvasCtrl().is3d());

    // Цвет точек
    _pointsPanel.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          CurrentTheme.getColorTheme().setPointsColorGL(new ColorGL((Color)evt.getNewValue()));
          CurrentTheme.notifyThemeChange();
        }
      });

    // Цвет каркасов
    _carcassPanel.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          CurrentTheme.getColorTheme().setCarcassFiguresColorGL(new ColorGL((Color)evt.getNewValue()));
          CurrentTheme.notifyThemeChange();
        }
      });

    // Цвет поверхностей
    _facetsPanel.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          CurrentTheme.getColorTheme().setFacetsFiguresColorGL(new ColorGL((Color)evt.getNewValue()));
          CurrentTheme.notifyThemeChange();
        }
      });

    // Видимость осей
    _axesVisibleCheckBox.addActionListener(new AbstractAction(){
      @Override
      public void actionPerformed(ActionEvent e) {
        AXES_VISIBLE.setValue(((JCheckBox)e.getSource()).isSelected());
        _ctrl.notifyAppSettingsChange();
        _ctrl.getMainCanvasCtrl().redraw();
      }
    });

    // Видимость сетки
    _gridVisibleCheckBox.addActionListener(new AbstractAction(){
      @Override
      public void actionPerformed(ActionEvent e) {
        GRID_VISIBLE.setValue(((JCheckBox)e.getSource()).isSelected());
        _ctrl.notifyAppSettingsChange();
        _ctrl.getMainCanvasCtrl().redraw();
      }
    });

    // Сглаживание
    _antialiasingCheckBox.addActionListener(new AbstractAction(){
      @Override
      public void actionPerformed(ActionEvent e) {
        _ctrl.getScene().getRender().setUseAntialiasing(((JCheckBox)e.getSource()).isSelected());
        _ctrl.notifyAppSettingsChange();
        _ctrl.redraw();
      }
    });

    // Проекция
    _changeProjectionCheckBox.addActionListener(new AbstractAction(){
      @Override
      public void actionPerformed(ActionEvent e) {
        if (((JCheckBox)e.getSource()).isSelected()) {
          _ctrl.getScene().setProjection(ProjectionMode.PERSPECTIVE_PROJECTION);
        } else {
          _ctrl.getScene().setProjection(ProjectionMode.ORTHO_PROJECTION);
        }
        _ctrl.notifyAppSettingsChange();
        _ctrl.redraw();
      }
    });

    // Показать обозначения точек
    _pointTitlesVisibleCheckBox.addActionListener(new AbstractAction(){
      @Override
      public void actionPerformed(ActionEvent e) {
        POINT_TITLE_VISIBLE.setValue(((JCheckBox)e.getSource()).isSelected());
        _ctrl.notifyAppSettingsChange();
      }
    });
    
    // Точность (количество знаков после запятой)
    _precisionComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        PRECISION.setValue((Integer)e.getItem());
        _ctrl.notifyEditorStateChange();
        _ctrl.notifyAppSettingsChange();
      }
    });
    
    _fontSizeSlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        MATH_FONT_SIZE.setValue(FONTSIZE.get(((JSlider)e.getSource()).getValue()));
        ImageGenerator.clearCache();
        _ctrl.redraw();
      }
    });

    // Интенстивность сетки
    _gridIntensitySlider.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        double newValue = 0.01 * ((JSlider)e.getSource()).getValue();
        _ctrl.getScene().setGridColorIntensity(newValue);
        _ctrl.redraw();
      }
    });
    
    // толщина линий
    _lineThicknessChooser.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent ie) {
        if (ie.getStateChange() == ItemEvent.SELECTED) {
          Thickness value = (Thickness)ie.getItem();
          LINE_WIDTH.setValue(value.getValue());
          _ctrl.notifyAppSettingsChange();
          _ctrl.redraw();
        }
      }
    });
    
    // толщина точек
    _pointThicknessChooser.setToolTipText("Диаметр точек");
    _pointThicknessChooser.setFocusable(false);
    _pointThicknessChooser.addSliderChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent pce) {
        POINT_WIDTH.setValue((float)((JSlider)pce.getSource()).getValue());
        _ctrl.notifyAppSettingsChange();
      }
    });
    
    JTabbedPane jtp = new JTabbedPane();

    EdtTabPanel colorsPanel = new EdtTabPanel();

    colorsPanel.addItem("Точки", null, _pointsPanel, 20, 20);
    colorsPanel.addItem("Рёбра", null, _carcassPanel, 20, 20);
    colorsPanel.addItem("Заливка граней", null, _facetsPanel, 20, 20);
    colorsPanel.addItem("Толщина линий", null, _lineThicknessChooser);
    colorsPanel.addItem("Размер точек", null, _pointThicknessChooser, 22, 22);

    EdtTabPanel settingsPanel = new EdtTabPanel();

    settingsPanel.addItem("Показывать оси", null, _axesVisibleCheckBox, 20, 20);
    settingsPanel.addItem("Показывать сетку", null,_gridVisibleCheckBox, 20, 20);
    settingsPanel.addItem("Показывать имена точек", null,_pointTitlesVisibleCheckBox, 20, 20);
    settingsPanel.addItem("Сглаживание", null,_antialiasingCheckBox, 20, 20);
    settingsPanel.addItem(
            "Перспективная проекция", "Установить перспективную или ортографическую проекцию",
            _changeProjectionCheckBox, 20, 20);
    settingsPanel.addItem("Прозрачность сетки",
            "Установить уровень прозрачности сетки на плоскости Oxy",_gridIntensitySlider);
    settingsPanel.addItem("Размер шрифта обозначений", _fontSizeSlider);
    settingsPanel.addItem("Точность числовых значений",
            "Количество отображаемых знаков после запятой", _precisionComboBox);
    
    EdtTabPanel userconfPanel = new EdtTabPanel();
    userconfPanel.addItem(new UserconfPanel());
    
    EdtTabPanel extPanel = new EdtTabPanel();
    extPanel.addItem(_extPanel);

    jtp.add("Графика", colorsPanel);
    jtp.add("Установки", settingsPanel);
    jtp.add("Конфигурация", userconfPanel);
    jtp.add("Расширения", extPanel);
    add(jtp, new CC().grow().push().wrap());
    add(_feedback, new CC().grow().push());
    pack();
  }

  private void refreshSettings() {
    _axesVisibleCheckBox.setSelected(AXES_VISIBLE.value());
    _gridVisibleCheckBox.setSelected(GRID_VISIBLE.value());
    _antialiasingCheckBox.setSelected(_ctrl.getScene().getRender().isUseAntialiasing());
    _pointTitlesVisibleCheckBox.setSelected(POINT_TITLE_VISIBLE.value());
    _changeProjectionCheckBox.setSelected(_ctrl.getScene().getProjection() == ProjectionMode.PERSPECTIVE_PROJECTION);
    if (PRECISION.value() >= 0 && PRECISION.value() <= 5) {
      _precisionComboBox.setSelectedIndex(PRECISION.value());
    } else {
      _precisionComboBox.setSelectedIndex(0);
    }
    // скрываем слайдер, если сетки не видно
    _gridIntensitySlider.setEnabled(GRID_VISIBLE.value());
    _lineThicknessChooser.setSelectedItem(Thickness.get(LINE_WIDTH.value()));
    _pointThicknessChooser.setThickness(Thickness.get(POINT_WIDTH.value()));
    
    // обновляем панель расширений
    _extPanel.update();
  }

  private void refreshModeDependingOptions(boolean is3d) {
    _changeProjectionCheckBox.setEnabled(is3d);
  }

  @Override
  public void colorThemeChanged() {
    _pointsPanel.setColor(CurrentTheme.getColorTheme().getPointsColorGL());
    _carcassPanel.setColor(CurrentTheme.getColorTheme().getCarcassFiguresColorGL());
    _facetsPanel.setColor(CurrentTheme.getColorTheme().getFacetsFiguresColorGL());
    revalidate();
    repaint();
  }

  @Override
  public void settingsChanged() {
    refreshSettings();
  }

  @Override
  public void modeChanged(SceneType is3d) {
    refreshModeDependingOptions(is3d.is3d());
  }
  
  class UserconfPanel extends JPanel {
    private static final int DEFAULT_CONF_MODE = 0;
    private static final int CURRENT_CONF_MODE = 1;
    private static final int OTHER_CONF_MODE = 2;
    private static final int CREATE_CONF_MODE = 3;
    
    private int _currentMode;
    
    // Список пользовательских конфигураций.
    private final JComboBox _userconfComboBox;
    
    // Поле ввода имени новой конфигурации.
//    private final EdtTextField _confNameInputField;
    private final HintTextField _confNameInputField;
    
    // Кнопка сохранения конфигурации.
    private final JButton _saveConfButton;
    
    // Кнопка загрузки конфигурации.
    private final JButton _loadConfButton;
    
    // Кнопка создания конфигурации.
    private final JButton _createConfButton;
    
    // Кнопка удаления конфигурации.
    private final JButton _deleteConfButton;
    
    // Кнопка сохранения конфигурации.
//    private final JButton _importConfButton;
    
    // Пользовательские файлы конфигурации.
    HashMap<String, File> _confFiles;

    UserconfPanel() {
      super(new MigLayout(new LC().fillX()));
      _userconfComboBox = new JComboBox();
      _confNameInputField = new HintTextField("Введите имя конфигурации...");
      _saveConfButton = new JButton("Сохранить текущую конфигурацию");
      _loadConfButton = new JButton("Загрузить конфигурацию");
      _deleteConfButton = new JButton("Удалить конфигурацию");
      _createConfButton = new JButton("Создать");
      _confFiles = new HashMap<>();
      initUserConfComboBox();
      initLoadConfButton();
      initSaveConfButton();
      initDeleteConfButton();
      initCreateConfButton();
      
      update();
      
      JPanel rightButtonsPanel = new JPanel(new MigLayout(new LC().hideMode(3).insets("0").fillX()));
      rightButtonsPanel.add(_loadConfButton, new CC().growX().wrap());
      rightButtonsPanel.add(_saveConfButton, new CC().growX().wrap());
      rightButtonsPanel.add(_deleteConfButton, new CC().growX().wrap());
      rightButtonsPanel.add(_createConfButton, new CC().growX().wrap());
      
      add(_userconfComboBox, new CC().cell(0, 0).grow());
      add(_confNameInputField, new CC().cell(0, 1).grow());
      add(rightButtonsPanel, new CC().cell(1, 0).spanY(2).grow());
    }
    
    private void initUserConfComboBox() {
      _userconfComboBox.setSelectedItem(OPTIONAL_CONFIG.value());
      _userconfComboBox.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          int currIndex = _userconfComboBox.getSelectedIndex();
          if( currIndex == 0 ) {
            // стандартная конфигурация.
            _currentMode = DEFAULT_CONF_MODE;
          } else if( _userconfComboBox.getSelectedIndex() == _userconfComboBox.getItemCount() - 1 ){
            // создание новой конфигурации
            _currentMode = CREATE_CONF_MODE;
          } else if( _userconfComboBox.getSelectedItem().equals(OPTIONAL_CONFIG.value()) ){
            // изменение текущей конфигурации
            _currentMode = CURRENT_CONF_MODE;
          } else {
            // настройка другой конфигурации
            _currentMode = OTHER_CONF_MODE;
          }
          updateLayout();
        }
      });
    }
    
    /**
     * В зависимости от текущего режима изменяем расположение элементов интерфейса.
     */
    private void updateLayout() {
      switch( _currentMode ){
        case DEFAULT_CONF_MODE:
          _loadConfButton.setVisible(true);
          _saveConfButton.setVisible(false);
          _deleteConfButton.setVisible(false);
          _createConfButton.setVisible(false);
          _confNameInputField.setVisible(false);
          break;
        case CURRENT_CONF_MODE:
          _loadConfButton.setVisible(true);
          _saveConfButton.setVisible(true);
          _deleteConfButton.setVisible(true);
          _createConfButton.setVisible(false);
          _confNameInputField.setVisible(false);
          break;
        case OTHER_CONF_MODE:
          _loadConfButton.setVisible(true);
          _saveConfButton.setVisible(false);
          _deleteConfButton.setVisible(true);
          _createConfButton.setVisible(false);
          _confNameInputField.setVisible(false);
          break;
        case CREATE_CONF_MODE:
          _loadConfButton.setVisible(false);
          _saveConfButton.setVisible(false);
          _deleteConfButton.setVisible(false);
          _createConfButton.setVisible(true);
          _confNameInputField.setVisible(true);
          break;
      }
    }
    
    private void initLoadConfButton() {
      _loadConfButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if( _currentMode == DEFAULT_CONF_MODE ){
             // сбрасываем к настройкам по умолчанию
            int reply = JOptionPane.showConfirmDialog(
                    AppSettingsDialog.this,
                    "Сбросить к настройкам по умолчанию?",
                    "Сброс конфигурации",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if( reply == JOptionPane.YES_OPTION ){
              Config.toDefault();
              _ctrl.getExtensionMgr().fromJson(EXTENSIONS.value());
              _ctrl.notifyAppSettingsChange();
              Config.changeUserconf("default");
              SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  update();
                }
              });
            }
          } else {
            String confName = (String) _userconfComboBox.getSelectedItem();
            if( Config.loadOptionalParams(_confFiles.get(confName)) ){
              _ctrl.getExtensionMgr().fromJson(EXTENSIONS.value());
              _ctrl.notifyAppSettingsChange();
              showMessage(String.format("Конфигурация %s загружена.", confName), 0);
              config.Config.changeUserconf(confName);
              SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  update();
                }
              });
            }
          }
        }
      });
    }
    
    private void initSaveConfButton() {
      _saveConfButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String confName = (String) _userconfComboBox.getSelectedItem();
          if( config.Config.saveOptionalParams(confName) ){
            showMessage(String.format("Конфигурация %s сохранена.", confName), 0);
            config.Config.changeUserconf(confName);
          } else {
            showMessage(String.format("Не удалось сохранить конфигурацию %s", confName), 2);
          }
        }
      });
    }
    
    private void initDeleteConfButton() {
      _deleteConfButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String confName = (String) _userconfComboBox.getSelectedItem();
          
          int reply = JOptionPane.showConfirmDialog(
                  AppSettingsDialog.this,
                  "Вы уверены, что хотите удалить файл конфигурации?",
                  "Удаление файла конфигурации",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.PLAIN_MESSAGE);
          if( reply == JOptionPane.YES_OPTION ){
            if( _currentMode == CURRENT_CONF_MODE ){
              // удаляем текущую конфигурацию
              // сбрасываем к настройкам по умолчанию
              Config.toDefault();
              _ctrl.getExtensionMgr().fromJson(EXTENSIONS.value());
              _ctrl.notifyAppSettingsChange();
              Config.changeUserconf("default");
            }

            // удаляем конфигурацию
            try {
              if( _confFiles.get(confName).delete() ){
                showMessage(String.format("Конфигурация %s удалена.", confName), 0);
              } else {
                showMessage("Не удалось удалить файл конфигурации!", 2);
              }
              update();
            } catch( SecurityException ex ) {
              showMessage("Недостаточно прав для удаления файла!", 2);
            }

            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                update();
              }
            });
          }
        }
      });
    }
    
    private void initCreateConfButton() {
      _createConfButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // выбрано создание новой конфигурации
          String newConfName = _confNameInputField.getText();
          if( newConfName.isEmpty() ){
            // Текстовое поле пусто, нужно ввести имя
            showMessage("Введите название новой конфигурации!", 1);
          } else if ( _confFiles.containsKey(newConfName) ) {
            showMessage(String.format("Конфигурация %s уже существует!", newConfName), 1);
          } else {
            if( config.Config.saveOptionalParams(newConfName) ){
              _confNameInputField.clear();
              showMessage(String.format("Конфигурация %s создана.", newConfName), 0);
              config.Config.changeUserconf(newConfName);
              SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  update();
                }
              });
            } else {
              showMessage(String.format("Не удалось создать конфигурацию %s", newConfName), 2);
            }
          }
        }
      });
    }
    
    /**
     * Обновить панель с учётом изменения файлов конфигурации.
     */
    private void update() {
      /**
       * Конструируем список доступных конфигураций.
       * Каждой конфигурации соответствует файл из папки ~/.3dschooledit/userconf/
       * Стандартной конфигурации соответствует файл ~/.3dschooledit/config
       */
      File confDir = new File(OPT_CONFIG_DIR);
      FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.endsWith(".cfg");
        }
      };
      File[] confList = confDir.listFiles(filter);
      _confFiles.clear();
      _userconfComboBox.removeAllItems();
      _userconfComboBox.addItem("default");
      for( File conf : confList ){
        String confName = FilenameUtils.removeExtension(conf.getName());
        _confFiles.put(confName, conf);
        _userconfComboBox.addItem(confName);
      }
      _userconfComboBox.addItem("Создать новую конфигурацию...");
      _userconfComboBox.setSelectedItem(OPTIONAL_CONFIG.value());
      updateLayout();
    }
  }
}