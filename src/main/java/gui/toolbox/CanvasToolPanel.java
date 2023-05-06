package gui.toolbox;

import config.Config;
import static config.Config.AXES_VISIBLE;
import static config.Config.GRID_VISIBLE;
import static config.Config.LINE_WIDTH;
import static config.Config.POINT_WIDTH;
import static config.Config.VIEW_VOLUME_VISIBLE;
import opengl.colorgl.ColorGL;
import opengl.scenegl.i_GlobalModeChangeListener;
import gui.layout.EdtCanvasPanel;
import gui.EdtController;
import gui.IconList;
import gui.elements.AngleStyleButton;
import gui.elements.LineThicknessComboBox;
import gui.elements.PointThicknessChooserPanel;
import gui.elements.Thickness;
import gui.i_AppSettingsChangeListener;
import gui.laf.AppColor;
import gui.ui.AlphaDecorator;
import gui.ui.EdtColorChooser;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import opengl.Drawer;
import opengl.colortheme.CurrentTheme;
import opengl.colortheme.i_ColorThemeChangeListener;
import opengl.scenegl.*;

/**
 * Tool panel on the top of canvas.
 * @author alexeev
 */
public class CanvasToolPanel extends JPanel implements i_GlobalModeChangeListener,
    i_ColorThemeChangeListener, i_AppSettingsChangeListener {
  private EdtController _ctrl;
  private EdtCanvasPanel _cp;

  /**
   * State of the panel.
   * Shown or hidden.
   */
  private boolean _expanded;
  private AlphaDecorator _alphaWrapper;
  private JButton _toggleButton;

  private JToggleButton _axesButton;
  private JToggleButton _gridButton;
  private JToggleButton _cubeButton;
  private JToggleButton _bigPlanesButton;
  private JToggleButton _magnetButton;
  private AngleStyleButton _angleButton;
  private LineThicknessComboBox _lineThicknessChooser;
  private EdtColorChooser _lineColorChooser;
  private PointThicknessChooserPanel _pointThicknessChooserPanel;
  private EdtColorChooser _pointColorChooser;
  private EdtColorChooser _surfaceColorChooser;

  public CanvasToolPanel(EdtController ctrl, EdtCanvasPanel cp){
    super(new MigLayout(new LC().fillX().hideMode(3), new AC().fill()));
    _ctrl = ctrl;
    _cp = cp;
    _expanded = true;

    CurrentTheme.addThemeChangeListener(this);
    _ctrl.addGlobalModeChangeListener(this);

    setOpaque(false);
    JPanel wrapper = new JPanel(new MigLayout(new LC().hideMode(3).insets("2", "2", "2", "2")));
    wrapper.setFocusable(true);
    wrapper.setBackground(AppColor.LIGHT_GRAY.opaque());
    _alphaWrapper = new AlphaDecorator(wrapper);
    add(_alphaWrapper, new CC().dockWest());

    // Кнопка скрытия панели
    _toggleButton = new JButton();
    _toggleButton.setFocusable(false);
    _toggleButton.setSelected(_expanded);
    _toggleButton.setIcon(IconList.TRIANGLE_UP.getTinyIcon());
    _toggleButton.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        setExpanded(!_expanded);
        _ctrl.notifyAppSettingsChange();
      }
    });

    JPanel rightPanel = new JPanel();
    rightPanel.setOpaque(false);
    rightPanel.setLayout(new MigLayout());
    rightPanel.add(_toggleButton, new CC().dockEast().width("16!").height("12!").gapX("5", "2"));
    rightPanel.add(new JLabel("Инструменты"));
    add(rightPanel, new CC().dockEast());

    // Показать/скрыть координатные оси.
    _axesButton = new JToggleButton(IconList.SHOW_AXES.getSmallIcon());
    _axesButton.setFocusable(false);
    _axesButton.setToolTipText("Оси");
    _axesButton.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        AXES_VISIBLE.setValue(!AXES_VISIBLE.value());
        _ctrl.notifyAppSettingsChange();
        _ctrl.getMainCanvasCtrl().redraw();
      }
    });

    // Показать/скрыть сетку на плоскости OXY.
    _gridButton = new JToggleButton(IconList.SHOW_GRID.getSmallIcon());
    _gridButton.setFocusable(false);
    _gridButton.setToolTipText("Сетка");
    _gridButton.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        GRID_VISIBLE.setValue(!GRID_VISIBLE.value());
        _ctrl.notifyAppSettingsChange();
        _ctrl.getMainCanvasCtrl().redraw();
      }
    });

    // Показать/скрыть куб области видимости.
    _cubeButton = new JToggleButton(IconList.BORDER.getSmallIcon());
    _cubeButton.setFocusable(false);
    _cubeButton.setToolTipText("Область видимости");
    _cubeButton.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        VIEW_VOLUME_VISIBLE.setValue(!VIEW_VOLUME_VISIBLE.value());
        _ctrl.notifyAppSettingsChange();
        _ctrl.getMainCanvasCtrl().redraw();
      }
    });

    // Использовать ограниченные/неограниченные плоскости
    _bigPlanesButton = new JToggleButton(IconList.UNLIM_PL.getSmallIcon());
    _bigPlanesButton.setFocusable(false);
    _bigPlanesButton.setToolTipText("Рисовать неограниченные плоскости");
    _bigPlanesButton.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        Drawer.setDrawBigPlanes(!Drawer.isDrawBigPlanes());
        _ctrl.notifyAppSettingsChange();
        _ctrl.redraw();
      }
    });

    // Переключить режим пртягивания точек
    _magnetButton = new JToggleButton(IconList.MAGNET.getSmallIcon());
    _magnetButton.setFocusable(false);
    _magnetButton.setToolTipText("Притягивание точек к сетке");
    _magnetButton.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        _ctrl.getMainCanvasCtrl().toggleManget();
        _ctrl.notifyAppSettingsChange();
        _ctrl.redraw();
      }
    });

    // толщина линий
    _lineThicknessChooser = new LineThicknessComboBox(Thickness.get(LINE_WIDTH.value()));
    _lineThicknessChooser.setFocusable(false);
    _lineThicknessChooser.setToolTipText("Толщина линий");
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

    // цвет линий
    _lineColorChooser = new EdtColorChooser(_ctrl, CurrentTheme.getColorTheme().getCarcassFiguresColorGL(), EdtColorChooser.FRAME);
    _lineColorChooser.setFocusable(false);
    _lineColorChooser.setToolTipText("Цвет линий");
    _lineColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
          CurrentTheme.getColorTheme().setCarcassFiguresColorGL(new ColorGL((Color)pce.getNewValue()));
          CurrentTheme.notifyThemeChange();
        }
      });

    // толщина точек
    _pointThicknessChooserPanel = new PointThicknessChooserPanel(Thickness.M);
    _pointThicknessChooserPanel.setToolTipText("Диаметр точек");
    _pointThicknessChooserPanel.setFocusable(false);
    _pointThicknessChooserPanel.addSliderChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent pce) {
        POINT_WIDTH.setValue((float)((JSlider)pce.getSource()).getValue());
        _ctrl.notifyAppSettingsChange();
      }
    });

    // цвет точек
    _pointColorChooser = new EdtColorChooser(_ctrl, CurrentTheme.getColorTheme().getPointsColorGL(), EdtColorChooser.DISK);
    _pointColorChooser.setFocusable(false);
    _pointColorChooser.setToolTipText("Цвет точек");
    _pointColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
          CurrentTheme.getColorTheme().setPointsColorGL(new ColorGL((Color)pce.getNewValue()));
          CurrentTheme.notifyThemeChange();
        }
      });

    // цвет поверхностей
    _surfaceColorChooser = new EdtColorChooser(_ctrl, CurrentTheme.getColorTheme().getFacetsFiguresColorGL(), EdtColorChooser.SOLID);
    _surfaceColorChooser.setFocusable(false);
    _surfaceColorChooser.setToolTipText("Цвет поверхностей");
    _surfaceColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
          CurrentTheme.getColorTheme().setFacetsFiguresColorGL(new ColorGL((Color)pce.getNewValue()));
          CurrentTheme.notifyThemeChange();
        }
      });

    _angleButton = new AngleStyleButton(ctrl);

    refreshSettings();

    wrapper.add(_axesButton, new CC().width("24!").height("24!"));
    wrapper.add(_gridButton, new CC().width("24!").height("24!"));
    wrapper.add(_cubeButton, new CC().width("24!").height("24!"));
    wrapper.add(_bigPlanesButton, new CC().width("24!").height("24!"));
    wrapper.add(_magnetButton, new CC().width("24!").height("24!"));
    wrapper.add(_pointThicknessChooserPanel, new CC().width("24!").height("24!"));
    wrapper.add(_lineThicknessChooser, new CC().width("100!").height("22!"));
    wrapper.add(_pointColorChooser, new CC().width("22!").height("22!"));
    wrapper.add(_lineColorChooser, new CC().width("22!").height("22!"));
    wrapper.add(_surfaceColorChooser, new CC().width("22!").height("22!"));
    wrapper.add(_angleButton, new CC().width("30!").height("24!"));
  }

  public boolean isExpanded() {
    return _expanded;
  }

  /**
   * Set state of panel.
   * Shown or hidden.
   *
   * @param expanded
   */
  public void setExpanded(boolean expanded) {
    _expanded = expanded;
    _toggleButton.setIcon(_expanded ?
        IconList.TRIANGLE_UP.getTinyIcon() :
        IconList.TRIANGLE_DOWN.getTinyIcon());
    _alphaWrapper.setVisible(_expanded);
    _ctrl.getLayoutController().notifyLayoutChanged();
  }

  /**
   * Обновляем панели в соответствии с новыми настройками приложения.
   */
  private void refreshSettings() {
    _axesButton.setSelected(AXES_VISIBLE.value());
    _gridButton.setSelected(GRID_VISIBLE.value());
    _cubeButton.setSelected(VIEW_VOLUME_VISIBLE.value());
    _bigPlanesButton.setSelected(Drawer.isDrawBigPlanes());
    _magnetButton.setSelected(_ctrl.getMainCanvasCtrl().isMagnet());
    _lineThicknessChooser.setSelectedItem(Thickness.get(LINE_WIDTH.value()));
    _pointThicknessChooserPanel.setThickness(Thickness.get(POINT_WIDTH.value()));
  }

  @Override
  public void modeChanged(SceneType type) {
    _bigPlanesButton.setVisible(type == SceneType.Scene3D);
    _magnetButton.setVisible(type != SceneType.SceneSTL);
    _pointColorChooser.setVisible(type != SceneType.SceneSTL);
    _pointThicknessChooserPanel.setVisible(type != SceneType.SceneSTL);
    _lineThicknessChooser.setVisible(type != SceneType.SceneSTL);
    _lineColorChooser.setVisible(type != SceneType.SceneSTL);
    _surfaceColorChooser.setVisible(type != SceneType.SceneSTL);
    _cubeButton.setVisible(type.is3d());
    revalidate();
    repaint();
  }

  @Override
  public void colorThemeChanged() {
    _pointColorChooser.setColor(CurrentTheme.getColorTheme().getPointsColorGL());
    _lineColorChooser.setColor(CurrentTheme.getColorTheme().getCarcassFiguresColorGL());
    _surfaceColorChooser.setColor(CurrentTheme.getColorTheme().getFacetsFiguresColorGL());
    revalidate();
    repaint();
  }

  @Override
  public void settingsChanged() {
    refreshSettings();
  }
}
