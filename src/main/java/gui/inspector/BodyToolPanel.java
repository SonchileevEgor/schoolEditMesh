package gui.inspector;

import opengl.colorgl.ColorGL;
import bodies.BodyType;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.state.AnchorState;
import editor.state.DisplayParam;
import editor.state.i_BodyStateChangeListener;
import gui.EdtController;
import gui.IconList;
import gui.ui.EdtColorChooser;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import opengl.colortheme.CurrentTheme;

/**
 * Панель инструментов для тела.
 * @author alexeev
 */
public class BodyToolPanel extends JPanel implements i_BodyStateChangeListener {
  private final EdtController _ctrl;

  private final JToggleButton _showBodyButton;
  private boolean _isShowBodyButtonPressed;

  private final JToggleButton _fillButton;
  private boolean _fillButtonPressed;

  private final JButton _showTitleButton;
  private final JButton _removeButton;

  /**
   * Color of focused points chooser.
   */
  private final EdtColorChooser _pointColorChooser;
  /**
   * Color of carcass chooser.
   */
  private final EdtColorChooser _carcassColorChooser;
  /**
   * Color of surface chooser.
   */
  private final EdtColorChooser _surfaceColorChooser;
  /**
   * Color of points of body chooser.
   */
  private final EdtColorChooser _bodyPointsColorChooser;

  public BodyToolPanel(EdtController ctrl) {
    super();
    _ctrl = ctrl;
    _ctrl.addBodyStateChangeListener(this);
    setLayout(new MigLayout(new LC().fillX().noGrid()
            .leftToRight(false).insetsAll("0").alignX("right").hideMode(3)));
    _showBodyButton = new JToggleButton(IconList.SHOWN.getSmallIcon());
    _fillButtonPressed = true;
    _fillButton = new JToggleButton(IconList.FILL.getSmallIcon());
    _showTitleButton = new JButton(IconList.SHOW_LABEL.getSmallIcon());
    _removeButton = new JButton(IconList.WASTE.getSmallIcon());
    _pointColorChooser = new EdtColorChooser(
              _ctrl, CurrentTheme.getColorTheme().getPointsColorGL(), EdtColorChooser.SOLID);
    _carcassColorChooser = new EdtColorChooser(
            _ctrl, CurrentTheme.getColorTheme().getCarcassFiguresColorGL(), EdtColorChooser.FRAME);
    _surfaceColorChooser = new EdtColorChooser(
            _ctrl, CurrentTheme.getColorTheme().getFacetsFiguresColorGL(), EdtColorChooser.SOLID);
    _bodyPointsColorChooser = new EdtColorChooser(
            _ctrl, CurrentTheme.getColorTheme().getPointsColorGL(), EdtColorChooser.DISK);
    _showBodyButton.setToolTipText("Показать");
    _showTitleButton.setToolTipText("Показать обозначение");
    _removeButton.setToolTipText("Удалить");
    _pointColorChooser.setToolTipText("Цвет точки");
    _carcassColorChooser.setToolTipText("Цвет каркаса");
    _surfaceColorChooser.setToolTipText("Цвет поверхности");
    _bodyPointsColorChooser.setToolTipText("Цвет точек");
    _showBodyButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!_isShowBodyButtonPressed) {
          for (String bodyID : _ctrl.getFocusCtrl().getFocusedBodies()) {
            try {
              _ctrl.setBodyVisible(bodyID, true, true, true, true, true);
              _ctrl.notifyBodyStateChange(bodyID);
            } catch (ExNoBody ex) {}
          }
        } else {
          for (String bodyID : _ctrl.getFocusCtrl().getFocusedBodies()) {
            try {
              BodyType type = _ctrl.getBody(bodyID).type();
              _ctrl.setBodyVisible(bodyID, false, type == BodyType.POINT, true, true, true);
              _ctrl.notifyBodyStateChange(bodyID);
            } catch (ExNoBody ex) {}
          }
        }
        _ctrl.setUndo(_isShowBodyButtonPressed ? "Скрыть" : "Показать");
        _ctrl.redraw();
      }
    });
    _fillButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (String bodyID : _ctrl.getFocusCtrl().getFocusedBodies()) {
          try {
            _ctrl.getEditor().setBodyFill(bodyID, !_fillButtonPressed);
            _ctrl.notifyBodyStateChange(bodyID);
          } catch (ExNoBody ex) {}
        }
        _ctrl.setUndo(_fillButtonPressed ? "Скрыть поверхность" : "Показать поверхность");
        _ctrl.redraw();
      }
    });
    _showTitleButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (String bodyID : _ctrl.getFocusCtrl().getFocusedBodies()) {
          try {
            AnchorState state = _ctrl.getAnchor(bodyID, "P").getState();
            state.setTitleVisible(!(boolean)state.getParam(DisplayParam.TITLE_VISIBLE));
            _ctrl.notifyBodyStateChange(bodyID);
          } catch (ExNoAnchor | ExNoBody ex) {}
        }
        _ctrl.redraw();
      }
    });
    _removeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        _ctrl.removeBodies(_ctrl.getFocusCtrl().getFocusedBodies());
        _ctrl.redraw();
      }
    });
    _pointColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
          ColorGL newColor = new ColorGL((Color)e.getNewValue());
          for (String bodyID : _ctrl.getFocusCtrl().getFocusedBodies()) {
            try {
              AnchorState state = _ctrl.getAnchor(bodyID, "P").getState();
              state.setParam(DisplayParam.POINT_COLOR, newColor);
              _ctrl.notifyBodyStateChange(bodyID);
            } catch (ExNoAnchor | ExNoBody ex) {}
          }
          _ctrl.redraw();
        }
      });
    _pointColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
          _ctrl.setUndo("Изменение цвета");
        }
      });
    _carcassColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
          ColorGL newColor = new ColorGL((Color)e.getNewValue());
          for (String bodyID : _ctrl.getFocusCtrl().getFocusedBodies()) {
            _ctrl.getEditor().setBodyCarcassColor(bodyID, newColor);
            _ctrl.notifyBodyStateChange(bodyID);
          }
          _ctrl.redraw();
        }
      });
    _carcassColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
          _ctrl.setUndo("Изменение цвета");
        }
      });
    _surfaceColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
          ColorGL newColor = new ColorGL((Color)e.getNewValue());
          for (String bodyID : _ctrl.getFocusCtrl().getFocusedBodies()) {
            _ctrl.getEditor().setBodyFacetColor(bodyID, newColor);
            _ctrl.notifyBodyStateChange(bodyID);
          }
          _ctrl.redraw();
        }
      });
    _surfaceColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
          _ctrl.setUndo("Изменение цвета");
        }
      });
    _bodyPointsColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
          ColorGL newColor = new ColorGL((Color)e.getNewValue());
          for (String bodyID : _ctrl.getFocusCtrl().getFocusedBodies()) {
            _ctrl.getEditor().setBodyPointsColor(bodyID, newColor);
            _ctrl.notifyBodyStateChange(bodyID);
          }
          _ctrl.redraw();
        }
      });
    _bodyPointsColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
      new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
          _ctrl.setUndo("Изменение цвета точек");
        }
      });

    CC buttonConstraints = new CC().width("20!").height("20!");
    CC colorChooserConstraints = new CC().width("18!").height("18!");


    // Кнопки добавляются справа налево
    add(_removeButton, buttonConstraints);
    add(_showTitleButton, buttonConstraints);
    add(_fillButton, buttonConstraints);
    add(_bodyPointsColorChooser, colorChooserConstraints);
    add(_surfaceColorChooser, colorChooserConstraints);
    add(_carcassColorChooser, colorChooserConstraints);
    add(_pointColorChooser, colorChooserConstraints);
    add(_showBodyButton, buttonConstraints);

    update();
  }

  public final void update() {
    String firstBodyID = "";
    int numFocusedBodies = _ctrl.getFocusCtrl().getFocusedBodies().size();
    boolean isEmpty = (numFocusedBodies == 0);
    boolean hasNonExistBody = _ctrl.getFocusCtrl().containsNonExistsBody();
    boolean arePoints = _ctrl.getFocusCtrl().arePoints();
    boolean areBodiesWithCarcass = _ctrl.getFocusCtrl().areBodiesWithCarcass();
    boolean areBodiesWithSurface = _ctrl.getFocusCtrl().areBodiesWithSurface();
    boolean areBodiesWithPoints = _ctrl.getFocusCtrl().areBodiesWithPoints();
    if (!isEmpty) {
      firstBodyID = _ctrl.getFocusCtrl().getFocusedBodies().get(0);
      _isShowBodyButtonPressed = _ctrl.isBodyVisible(firstBodyID);
    }
    _showTitleButton.setVisible(arePoints && !hasNonExistBody);
    _showBodyButton.setVisible(!isEmpty && !hasNonExistBody);
    _showBodyButton.setSelected(_isShowBodyButtonPressed);
    _showBodyButton.setToolTipText(_isShowBodyButtonPressed ? "Скрыть" : "Показать");
    _showBodyButton.setIcon(_isShowBodyButtonPressed ? IconList.SHOWN.getSmallIcon() : IconList.HIDDEN.getSmallIcon());
    _fillButton.setVisible(areBodiesWithSurface && areBodiesWithCarcass && !hasNonExistBody);
    _removeButton.setVisible(_ctrl.getFocusCtrl().containsRemovableBody());
    _surfaceColorChooser.setVisible(areBodiesWithSurface && !hasNonExistBody);
    _carcassColorChooser.setVisible(areBodiesWithCarcass && !hasNonExistBody);
    _pointColorChooser.setVisible(arePoints && !hasNonExistBody);
    _bodyPointsColorChooser.setVisible(areBodiesWithPoints && !hasNonExistBody);
    if (areBodiesWithSurface && !hasNonExistBody) {
      if (areBodiesWithCarcass) {
        _fillButtonPressed = _ctrl.getEditor().isBodyFilled(firstBodyID);
        _fillButton.setSelected(_fillButtonPressed);
        _fillButton.setToolTipText(_fillButtonPressed ? "Скрыть поверхность" : "Показать поверхность");
      }
      if (numFocusedBodies == 1) {
        ColorGL bodyColor = _ctrl.getEditor().getBodySurfaceColor(firstBodyID);
        _surfaceColorChooser.setColor(bodyColor);
      } else if (numFocusedBodies > 1) {
        _surfaceColorChooser.setColor(CurrentTheme.getColorTheme().getFacetsFiguresColorGL());
      }
    }
    if (areBodiesWithCarcass) {
      if (numFocusedBodies == 1) {
        ColorGL bodyColor = _ctrl.getEditor().getBodyCarcassColor(firstBodyID);
        _carcassColorChooser.setColor(bodyColor);
      } else if (numFocusedBodies > 1) {
        _carcassColorChooser.setColor(CurrentTheme.getColorTheme().getCarcassFiguresColorGL());
      }
    }
    if (arePoints) {
      if (numFocusedBodies == 1) {
        try {
          AnchorState state = _ctrl.getAnchor(firstBodyID, "P").getState();
          ColorGL pointColor = (ColorGL)state.getParam(DisplayParam.POINT_COLOR);
          _pointColorChooser.setColor(pointColor);
        } catch (ExNoBody | ExNoAnchor ex) {}
      } else if (numFocusedBodies > 1) {
        _pointColorChooser.setColor(CurrentTheme.getColorTheme().getPointsColorGL());
      }
    }
    if (areBodiesWithPoints) {
      if (numFocusedBodies == 1) {
        _bodyPointsColorChooser.setColor(_ctrl.getEditor().getBodyPointsColor(firstBodyID));
      } else if (numFocusedBodies > 1) {
        _bodyPointsColorChooser.setColor(CurrentTheme.getColorTheme().getPointsColorGL());
      }
    }
    revalidate();
    repaint();
  }

  @Override
  public void updateBodyState(String bodyID) {
    if (_ctrl.getFocusCtrl().contains(bodyID))
      update();
  }
}