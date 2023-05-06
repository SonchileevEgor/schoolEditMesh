package gui.dialog;

import opengl.colorgl.ColorGL;
import bodies.BodyInfoConstructor;
import bodies.BodyType;
import bodies.CircleBody;
import bodies.PointBody;
import bodies.PolygonBody;
import bodies.RibBody;
import editor.state.DisplayParam;
import editor.state.EntityState;
import editor.AnchorType;
import builders.param.BuilderParam;
import static config.Config.PRECISION;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.ExNoBuilder;
import editor.i_Anchor;
import editor.i_AnchorContainer;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_EditorChangeListener;
import editor.i_FocusChangeListener;
import editor.state.i_BodyStateChangeListener;
import geom.Vect3d;
import gui.EdtController;
import gui.elements.AngleChooser;
import gui.elements.ArrowStyleChooser;
import gui.elements.ImmutableTextField;
import gui.elements.LineMarkComboBox;
import gui.elements.LineStyleComboBox;
import gui.elements.LineThicknessComboBox;
import gui.elements.LineType;
import gui.elements.NumberChooser;
import gui.elements.NumericTextField;
import gui.elements.PointThicknessChooserPanel;
import gui.elements.Thickness;
import gui.elements.XYZPanel;
import gui.inspector.BodyTree;
import gui.ui.EdtColorChooser;
import gui.ui.EdtTabPanel;
import gui.ui.EdtTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import opengl.LineMark;
import opengl.StyleArrow;
import opengl.colortheme.CurrentTheme;
import opengl.i_ScaleChangeListener;
import util.Util;

/**
 * Диалог параметров тела. Включает вкладки: параметры отображения (цвета и стили), параметры
 * построения, информацию о теле.
 *
 * @author alexeev
 */
public class BodySettingsDialog extends EdtDialog implements i_BodyStateChangeListener,
        i_EditorChangeListener, i_FocusChangeListener, i_ScaleChangeListener {

  private EdtController _ctrl;

  private final BuilderParamsPanel _builderParamsPanel;
  private final GraphicsParamsPanel _graphicsParamsPanel;
  private final BodyImageInfoPanel _bodyImageInfoPanel;
  private final BodyTree _bodyTree;
  private final JTabbedPane _jtp;

  private String _bodyID;
  private String _anchorID; // только для точек, отрезков и многоугольников
  private EntityState _state;
  private i_BodyBuilder _builder;
  private BodyType _type;
  private boolean _hasBuilderParams;

  /**
   * Флаг, устанавливаемый в true в случае, если параметры билдера были изменены через панель. В
   * этом случае, нужно оповестить об изменении Editor всех слушателей, в т.ч. и сам
   * BodySettingsDialog, но не нужно обновлять панель параметров билдера. (в этом нет смысла, а
   * фокус с полей ввода при обновлении будет утерян)
   */
  private boolean _isValueChangedInBuilderPanel = false;

  public BodySettingsDialog(EdtController ctrl) {
    super(ctrl.getFrame(), "Параметры тела");
    setLayout(new MigLayout(new LC()
            .width(String.valueOf(Util.getScreenWidth() / 3))
            .height(String.valueOf(Util.getScreenHeight() / 3)))
    );
    _ctrl = ctrl;
    _ctrl.addBodyStateChangeListener(this);
    _ctrl.addEditorStateChangeListener(this);
    _ctrl.getFocusCtrl().addFocusChangeListener(this);
    _ctrl.getScene().getRender().addDisplayListener(this);
    _graphicsParamsPanel = new GraphicsParamsPanel();
    _builderParamsPanel = new BuilderParamsPanel();
    _bodyImageInfoPanel = new BodyImageInfoPanel();
    _bodyTree = new BodyTree(_ctrl, false);

    _jtp = new JTabbedPane();
    _jtp.add("Параметры отображения", _graphicsParamsPanel);
    _jtp.add("Параметры построения", _builderParamsPanel);
    _jtp.add("Информация о теле", _bodyImageInfoPanel);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(_bodyTree), _jtp);
    splitPane.setDividerSize(6);
    splitPane.setDividerLocation(0.33);
    splitPane.setContinuousLayout(true);
    add(splitPane, new CC().grow().push());

    setModal(false);
    setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    _bodyID = _ctrl.getFocusCtrl().getLastFocusedBody();

    update();

    _graphicsParamsPanel.update();
    _builderParamsPanel.update();

    _bodyImageInfoPanel.update();
    _bodyTree.update();
    _bodyTree.updateModel();
    pack();
  }

  private void update() {
    _hasBuilderParams = false;
    if (_bodyID != null) {
      try {
        i_Body bd = _ctrl.getBody(_bodyID);
        _state = bd.getState();
        _type = bd.type();
        if (_type == BodyType.POINT) {
          _anchorID = bd.getAnchorID(PointBody.BODY_KEY_POINT);
        } else if (_type == BodyType.RIB) {
          _anchorID = bd.getAnchorID(RibBody.BODY_KEY_RIB);
        } else if (_type.isPolygon()) {
          _anchorID = bd.getAnchorID(PolygonBody.BODY_KEY_POLYGON);
        } else if (_type == BodyType.CIRCLE) {
          _anchorID = bd.getAnchorID(CircleBody.BODY_KEY_DISK);
        } else {
          _anchorID = null;
        }

        try {
          _builder = _ctrl.getBuilder(_bodyID);
          _hasBuilderParams = true;
        } catch (ExNoBuilder ex) {
          _builder = null;
          _hasBuilderParams = false;
        }
        return;
      } catch (ExNoBody ex) {
      }
    }

    _bodyID = null;
    _state = null;
    _type = null;
    _anchorID = null;
    _builder = null;
    _hasBuilderParams = false;
  }

  @Override
  public void updateBodyState(String bodyID) {
    if (bodyID.equals(_bodyID)) {
      _graphicsParamsPanel.update();
    }
    _bodyTree.update();
  }

  @Override
  public void updateEditorState() {
    update();
    _graphicsParamsPanel.update();
    if (!_isValueChangedInBuilderPanel) {
      _builderParamsPanel.update();
    }
    _bodyImageInfoPanel.update();
    _bodyTree.update();
    _bodyTree.updateModel();
    _isValueChangedInBuilderPanel = false;
  }

  @Override
  public void focusCleared(Collection<String> bodyIDs) {
    _bodyID = null;
    update();
    _graphicsParamsPanel.update();
    _builderParamsPanel.update();
    _bodyImageInfoPanel.update();
    _bodyTree.focusCleared();
  }

  @Override
  public void focusLost(String id, boolean isBody) {
    if (_ctrl.getFocusCtrl().isEmpty()) {
      _bodyID = null;
      update();
      _graphicsParamsPanel.update();
      _builderParamsPanel.update();
      _bodyImageInfoPanel.update();
      _bodyTree.updateFocus();
    }
  }

  @Override
  public void focusApply(String id, boolean isBody) {
    if (isBody && !id.equals(_bodyID)) {
      _bodyID = id;
      update();
      _graphicsParamsPanel.update();
      _builderParamsPanel.update();
      _bodyImageInfoPanel.update();
      _bodyTree.updateFocus();
    }
  }

  @Override
  public void scaleChanged(double distance, double meshSize) {
    if (_type == BodyType.PLANE) {
      _graphicsParamsPanel.updateScale(distance);
    }
  }

  class BuilderParamsPanel extends EdtTabPanel {

    public BuilderParamsPanel() {
      super();
    }

    public final void update() {
      removeAll();
      _jtp.setEnabledAt(1, _hasBuilderParams);
      if (_hasBuilderParams) {
        TreeSet<BuilderParam> params = _builder.params();

        for (final BuilderParam p: params) {
          switch (p.type()) {
            case ANCHOR: {
              try {
                ImmutableTextField comp = new ImmutableTextField(_ctrl.getAnchorTitle((String)p.getValue()));
                addItem(p.name(), comp);
              } catch (ExNoAnchor ex) {
              }
              break;
            }
            case ANCHOR_LIST: {
              try {
                i_AnchorContainer anchors = _ctrl.getEditor().anchors();
                ArrayList<String> ancList = (ArrayList<String>)p.getValue();
                String ancListStr = anchors.get(ancList.get(0)).getTitle(); //!! TODO: заменить на StringBuilder
                for (int i = 1; i < ancList.size(); i++) {
                  ancListStr += ", " + anchors.get(ancList.get(i)).getTitle();
                }
                ImmutableTextField comp = new ImmutableTextField(ancListStr);
                addItem(p.name(), comp);
              } catch (ExNoAnchor ex) {
              }
              break;
            }
            case ANGLE_VALUE: {
              AngleChooser comp = new AngleChooser((double)p.getValue());
              comp.addPropertyChangeListener("angle", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  double value = (double)e.getNewValue();
                  p.setValue(value);
                  _ctrl.rebuild();
                  _ctrl.setUndo("изменить параметры построения");
                  _ctrl.redraw();
                }
              });
              addItem(p.name(), comp);
              break;
            }
            case ANGLE_ROTATION: {
              if (_ctrl.getScene().is3d()) {
                AngleChooser comp = new AngleChooser((double)p.getValue());
                comp.addPropertyChangeListener("angle", new PropertyChangeListener() {
                  @Override
                  public void propertyChange(PropertyChangeEvent e) {
                    double value = (double)e.getNewValue();
                    p.setValue(value);
                    _ctrl.rebuild();
                    _ctrl.setUndo("изменить параметры построения");
                    _ctrl.redraw();
                  }
                });
                addItem(p.name(), comp);
              }
              break;
            }
            case BODY: {
              try {
                String bodyID = (String)p.getValue();
                ImmutableTextField comp = new ImmutableTextField(_ctrl.getBodyTitle(bodyID));
                addItem(p.name(), comp);
              } catch (ExNoBody ex) {
              }
              break;
            }
            case DOUBLE: {
              NumericTextField comp = new NumericTextField((double)p.getValue(), false);
              comp.addPropertyChangeListener("value", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  try {
                    double value = ((NumericTextField)e.getSource()).getNumericValue();
                    p.setValue(value);
                    _isValueChangedInBuilderPanel = true;
                    _ctrl.rebuild();
                    _ctrl.setUndo("изменить параметры построения");
                    _ctrl.redraw();
                  } catch (ParseException ex) {
                    //error("Ошибка ввода");
                  }
                }
              });
              addItem(p.name(), comp);
              break;
            }
            case DOUBLE_POSITIVE: {
              NumericTextField comp = new NumericTextField((double)p.getValue(), true);
              comp.addPropertyChangeListener("value", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  try {
                    double value = ((NumericTextField)e.getSource()).getNumericValue();
                    p.setValue(value);
                    _isValueChangedInBuilderPanel = true;
                    _ctrl.rebuild();
                    _ctrl.setUndo("изменить параметры построения");
                    _ctrl.redraw();
                  } catch (ParseException ex) {
                    //error("Ошибка ввода");
                  }
                }
              });
              addItem(p.name(), comp);
              break;
            }
            case INT: {
              final NumberChooser comp = new NumberChooser(3, 20);
              comp.setSelectedItem(p.getValue());
              comp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  int value = comp.getNumber();
                  p.setValue(value);
                  _isValueChangedInBuilderPanel = true;
                  _ctrl.rebuild();
                  _ctrl.setUndo("изменить параметры построения");
                  _ctrl.redraw();
                }
              });
              addItem(p.name(), comp);
              break;
            }
            case VECT: {
              if (_ctrl.getScene().is3d()) {
                Vect3d vect = (Vect3d)p.getValue();
                XYZPanel comp = new XYZPanel(vect.x(), vect.y(), vect.z(), true);
                comp.addPropertyChangeListener("value", new PropertyChangeListener() {
                  @Override
                  public void propertyChange(PropertyChangeEvent e) {
                    Vect3d value = (Vect3d)e.getNewValue();
                    p.setValue(value);
                    _isValueChangedInBuilderPanel = true;
                    _ctrl.rebuild();
                    _ctrl.setUndo("изменить параметры построения");
                    _ctrl.redraw();
                  }
                });
                addItem(p.name(), comp);
              }
              break;
            }
            case POINT: {
              Vect3d vect = (Vect3d)p.getValue();
              XYZPanel comp = new XYZPanel(vect.x(), vect.y(), vect.z(), _ctrl.getScene().is3d());
              comp.addPropertyChangeListener("value", new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  Vect3d value = (Vect3d)e.getNewValue();
                  p.setValue(value);
                  _isValueChangedInBuilderPanel = true;
                  _ctrl.rebuild();
                  _ctrl.setUndo("изменить параметры построения");
                  _ctrl.redraw();
                }
              });
              addItem(p.name(), comp);
            }
          }
        }
      }
      if (BuilderParamsPanel.this.getComponentCount() == 0) {
        ImmutableTextField noParamsLabel = new ImmutableTextField("Нет параметров построения");
        noParamsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addItem(noParamsLabel);
      }
      revalidate();
      repaint();
    }
  }

  class GraphicsParamsPanel extends EdtTabPanel {

    private static final double INDENT_CONST = 0.005;
    private final JCheckBox _visibilityCheckBox = new JCheckBox();
    private final JCheckBox _titleVisibilityCheckBox = new JCheckBox();
    private final JCheckBox _fillVisibilityCheckBox = new JCheckBox();
    private final JCheckBox _visibilityAsymptotesCheckBox = new JCheckBox();
    private final EdtTextField _labelField = new EdtTextField(10);
    private final EdtColorChooser _facetColorChooser = new EdtColorChooser(
            _ctrl, CurrentTheme.getColorTheme().getFacetsFiguresColorGL(), EdtColorChooser.SOLID);
    private final EdtColorChooser _pointColorChooser = new EdtColorChooser(
            _ctrl, CurrentTheme.getColorTheme().getPointsColorGL(), EdtColorChooser.SOLID);
    private final EdtColorChooser _carcassColorChooser = new EdtColorChooser(
            _ctrl, CurrentTheme.getColorTheme().getCarcassFiguresColorGL(), EdtColorChooser.SOLID);
    private final EdtColorChooser _bodyPointsColorChooser = new EdtColorChooser(
            _ctrl, CurrentTheme.getColorTheme().getPointsColorGL(), EdtColorChooser.SOLID);
    private final PointThicknessChooserPanel _pointThicknessChooserPanel = new PointThicknessChooserPanel(Thickness.M);
    private final LineStyleComboBox _carcassStyleChooser = new LineStyleComboBox(LineType.PLAIN);
    private final LineStyleComboBox _ribStyleChooser = new LineStyleComboBox(LineType.PLAIN);
    private final LineThicknessComboBox _carcassThicknessChooser = new LineThicknessComboBox(Thickness.M);
    private final LineThicknessComboBox _lineThicknessChooser = new LineThicknessComboBox(Thickness.M);
    private final LineMarkComboBox _ribMarkChooser = new LineMarkComboBox(LineMark.NONE);
    private final JSlider _planeIndentSlider = new JSlider(1, 150);
    private final ArrowStyleChooser _beginArrowChooser = new ArrowStyleChooser(StyleArrow.NONE, true);
    private final ArrowStyleChooser _endArrowChooser = new ArrowStyleChooser(StyleArrow.NONE, false);

    /**
     * Flag raised when update is running. if flag = true, elements don't trigger event listeners.
     */
    private boolean _isUpdating = false;

    int _visibilityCheckBoxIndex;
    int _titleVisibilityCheckBoxIndex;
    int _fillVisibilityCheckBoxIndex;
    int _visibilityAsymptotesCheckBoxIndex;
    int _labelFieldIndex;
    int _facetColorChooserIndex;
    int _pointColorChooserIndex;
    int _carcassColorChooserIndex;
    int _bodyPointsColorChooserIndex;
    int _pointThicknessChooserPanelIndex;
    int _carcassStyleChooserIndex;
    int _ribStyleChooserIndex;
    int _carcassThicknessChooserIndex;
    int _lineThicknessChooserIndex;
    int _ribMarkChooserIndex;
    int _planeIndentSliderIndex;
    int _arrowsChooserIndex;

    public GraphicsParamsPanel() {
      super();

      _visibilityCheckBox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (_isUpdating) {
            return;
          }

          boolean isVisible = ((JCheckBox)e.getSource()).isSelected();
          if (_state.hasParam(DisplayParam.VISIBLE)) {
            _state.setParam(DisplayParam.VISIBLE, isVisible);
          }
          try {
            // точка - особый случай.
            _ctrl.setBodyVisible(_bodyID, isVisible, isVisible || _type == BodyType.POINT, true, true, true);
          } catch (ExNoBody ex) {
          }
          _ctrl.notifyBodyStateChange(_bodyID);
          _ctrl.setUndo(isVisible ? "показать тело" : "скрыть тело");
          _ctrl.redraw();
        }
      });
      _titleVisibilityCheckBox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (_isUpdating) {
            return;
          }

          boolean isVisible = ((JCheckBox)e.getSource()).isSelected();
          _ctrl.getEditor().anchMgr().getState(_anchorID).setTitleVisible(isVisible);
          _ctrl.notifyAnchorStateChange(_anchorID);
          _ctrl.setUndo(isVisible ? "показать обозначение" : "скрыть обозначение");
          _ctrl.redraw();
        }
      });
      _fillVisibilityCheckBox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (_isUpdating) {
            return;
          }

          boolean isVisible = ((JCheckBox)e.getSource()).isSelected();
          try {
            _ctrl.getEditor().setBodyFill(_bodyID, isVisible);
            _ctrl.notifyBodyStateChange(_bodyID);
            _ctrl.setUndo(isVisible ? "показать поверхность" : "скрыть поверхность");
            _ctrl.redraw();
          } catch (ExNoBody ex) {
          }
        }
      });
      _visibilityAsymptotesCheckBox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (_isUpdating) {
            return;
          }

          boolean isVisible = ((JCheckBox)e.getSource()).isSelected();
          _state.setParam(DisplayParam.DRAW_ASYMPTOTES, isVisible);
          _ctrl.notifyBodyStateChange(_bodyID);
          _ctrl.setUndo(isVisible ? "показать асимптоты" : "скрыть асимптоты");
          _ctrl.redraw();
        }
      });
      _labelField.addPropertyChangeListener("text", new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
          if (_isUpdating) {
            return;
          }

          _ctrl.getEditor().anchMgr().getState(_anchorID).setLabel((String)e.getNewValue());
          _ctrl.setUndo("изменить надпись на отрезке");
          _ctrl.redraw();
        }
      });
      _facetColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_PROPERTY,
              new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  if (_isUpdating) {
                    return;
                  }

                  ColorGL color = new ColorGL((Color)e.getNewValue());
                  _ctrl.getEditor().setBodyFacetColor(_bodyID, color);
                  _ctrl.notifyBodyStateChange(_bodyID);
                  _ctrl.redraw();
                }
              });
      _facetColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
              new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  _ctrl.setUndo("изменить цвет поверхности");
                }
              });
      _pointColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_PROPERTY,
              new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  if (_isUpdating) {
                    return;
                  }

                  ColorGL color = new ColorGL((Color)e.getNewValue());
                  _ctrl.getEditor().anchMgr().getState(_anchorID).setParam(DisplayParam.POINT_COLOR, color);
                  _ctrl.notifyBodyStateChange(_bodyID);
                  _ctrl.redraw();
                }
              });
      _pointColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
              new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  _ctrl.setUndo("изменить цвет");
                }
              });
      _carcassColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_PROPERTY,
              new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  if (_isUpdating) {
                    return;
                  }

                  ColorGL color = new ColorGL((Color)e.getNewValue());
                  _ctrl.getEditor().setBodyCarcassColor(_bodyID, color);
                  _ctrl.notifyBodyStateChange(_bodyID);
                  _ctrl.redraw();
                }
              });
      _carcassColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
              new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  _ctrl.setUndo("изменить цвет");
                }
              });
      _bodyPointsColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_PROPERTY,
              new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  if (_isUpdating) {
                    return;
                  }

                  ColorGL color = new ColorGL((Color)e.getNewValue());
                  try {
                    i_Body body = _ctrl.getBody(_bodyID);
                    for (String anchorID: body.getAnchors().values()) {
                      i_Anchor anchor = _ctrl.getAnchor(anchorID);
                      if (anchor.getAnchorType() == AnchorType.ANC_POINT) {
                        anchor.getState().setParam(DisplayParam.POINT_COLOR, color);
                      }
                    }
                    _ctrl.notifyBodyStateChange(_bodyID);
                    _ctrl.redraw();
                  } catch (ExNoBody | ExNoAnchor ex) {
                  }
                }
              });
      _bodyPointsColorChooser.addPropertyChangeListener(EdtColorChooser.COLOR_CHANGE_PROPERTY,
              new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent e) {
                  _ctrl.setUndo("изменить цвет точек");
                }
              });
      _pointThicknessChooserPanel.addSliderChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          if (_isUpdating) {
            return;
          }

          _ctrl.getEditor().anchMgr().getState(_anchorID).setPointThickness(
                  ((JSlider)e.getSource()).getValue());
          _ctrl.notifyBodyStateChange(_bodyID);
          _ctrl.setUndo("изменить диаметр точки");
          _ctrl.redraw();
        }
      });
      _carcassStyleChooser.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (_isUpdating) {
            return;
          }

          // ItemListener реагирует как на выделение, нак и на снятие выделения пункта.
          if (e.getStateChange() == ItemEvent.SELECTED) {
            _state.setParam(DisplayParam.CARCASS_STYLE,
                    ((LineStyleComboBox)e.getSource()).getSelectedItem());
            _ctrl.setUndo("изменить стиль линий");
            _ctrl.redraw();
          }
        }
      });
      _ribStyleChooser.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (_isUpdating) {
            return;
          }

          // ItemListener реагирует как на выделение, нак и на снятие выделения пункта.
          if (e.getStateChange() == ItemEvent.SELECTED) {
            _ctrl.getEditor().anchMgr().getState(_anchorID).setLineType(
                    (LineType)((LineStyleComboBox)e.getSource()).getSelectedItem());
            _ctrl.setUndo("изменить стиль линий");
            _ctrl.redraw();
          }
        }
      });
      _carcassThicknessChooser.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (_isUpdating) {
            return;
          }

          // ItemListener реагирует как на выделение, нак и на снятие выделения пункта.
          if (e.getStateChange() == ItemEvent.SELECTED) {
            _ctrl.setBodyParam(_bodyID, DisplayParam.CARCASS_THICKNESS, ((Thickness)((LineThicknessComboBox)e.getSource()).getSelectedItem()).getValue());
            _ctrl.setUndo("изменить толщину линий");
            _ctrl.redraw();
          }
        }
      });
      _lineThicknessChooser.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (_isUpdating) {
            return;
          }

          // ItemListener реагирует как на выделение, нак и на снятие выделения пункта.
          if (e.getStateChange() == ItemEvent.SELECTED) {
            _ctrl.getEditor().anchMgr().getState(_anchorID).setCarcassThickness(
                    ((Thickness)((LineThicknessComboBox)e.getSource()).getSelectedItem()).getValue());
            _ctrl.setUndo("изменить толщину линии");
            _ctrl.redraw();
          }
        }
      });
      _ribMarkChooser.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (_isUpdating) {
            return;
          }

          // ItemListener реагирует как на выделение, нак и на снятие выделения пункта.
          if (e.getStateChange() == ItemEvent.SELECTED) {
            _ctrl.getEditor().anchMgr().getState(_anchorID).setLineMark(
                    (LineMark)((LineMarkComboBox)e.getSource()).getSelectedItem());
            _ctrl.setUndo("пометить отрезок");
            _ctrl.redraw();
          }
        }
      });
      _planeIndentSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          if (_isUpdating) {
            return;
          }

          // TODO: After change of camera distance need to change slider value to current value
          _state.setParam(DisplayParam.PLANE_INDENT,
                  ((JSlider)e.getSource()).getValue() * _ctrl.getScene().getCameraPosition().distance() * INDENT_CONST);
          _ctrl.redraw();
        }
      });
      JPanel arrowsPanel = new JPanel(new GridLayout(1, 2));
      arrowsPanel.add(_beginArrowChooser);
      arrowsPanel.add(_endArrowChooser);
      _beginArrowChooser.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (_isUpdating) {
            return;
          }
          // ItemListener реагирует как на выделение, нак и на снятие выделения пункта.
          if (e.getStateChange() == ItemEvent.SELECTED) {
            _ctrl.getEditor().anchMgr().getState(_anchorID).setParam(DisplayParam.ARROW_BEGIN,
                    (StyleArrow)((ArrowStyleChooser)e.getSource()).getSelectedItem());
            _ctrl.setUndo("изменить стиль стрелки");
            _ctrl.redraw();
          }
        }
      });
      _endArrowChooser.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (_isUpdating) {
            return;
          }

          // ItemListener реагирует как на выделение, нак и на снятие выделения пункта.
          if (e.getStateChange() == ItemEvent.SELECTED) {
            _ctrl.getEditor().anchMgr().getState(_anchorID).setParam(DisplayParam.ARROW_END,
                    (StyleArrow)((ArrowStyleChooser)e.getSource()).getSelectedItem());
            _ctrl.setUndo("изменить стиль стрелки");
            _ctrl.redraw();
          }
        }
      });

      _visibilityCheckBoxIndex = addItem("Видимость", null, _visibilityCheckBox, 20, 20);
      _titleVisibilityCheckBoxIndex = addItem("Видимость обозначения", null, _titleVisibilityCheckBox, 20, 20);
      _fillVisibilityCheckBoxIndex = addItem("Заливка", null, _fillVisibilityCheckBox, 20, 20);
      _visibilityAsymptotesCheckBoxIndex = addItem("Показать асимптоты", null, _visibilityAsymptotesCheckBox, 20, 20);
      _labelFieldIndex = addItem("Надпись", _labelField);
      _facetColorChooserIndex = addItem("Цвет поверхности", null, _facetColorChooser, 20, 20);
      _carcassColorChooserIndex = addItem("Цвет линий", null, _carcassColorChooser, 20, 20);
      _pointColorChooserIndex = addItem("Цвет", null, _pointColorChooser, 20, 20);
      _bodyPointsColorChooserIndex = addItem("Цвет точек", null, _bodyPointsColorChooser, 20, 20);
      _pointThicknessChooserPanelIndex = addItem("Диаметр точки", null, _pointThicknessChooserPanel, 20, 20);
      _carcassStyleChooserIndex = addItem("Стиль линий", _carcassStyleChooser);
      _ribStyleChooserIndex = addItem("Стиль линии", _ribStyleChooser);
      _carcassThicknessChooserIndex = addItem("Толщина линий", _carcassThicknessChooser);
      _lineThicknessChooserIndex = addItem("Толщина линии", _lineThicknessChooser);
      _ribMarkChooserIndex = addItem("Метка на отрезке", _ribMarkChooser);
      _planeIndentSliderIndex = addItem("Размер плоскости", _planeIndentSlider);
      _arrowsChooserIndex = addItem("Стрелки", arrowsPanel);
    }

    public final void update() {
      _isUpdating = true; // shut down event listeners

      for (Component c: getComponents()) {
        c.setVisible(false);
      }
      if (_bodyID != null) {
        try {
          if (_ctrl.getBody(_bodyID).exists()) {

            /**
             * ВИДИМОСТЬ.
             */
            setFieldAtIndexVisible(_visibilityCheckBoxIndex, true);
            _visibilityCheckBox.setSelected(_ctrl.isBodyVisible(_bodyID));

            /**
             * ВИДИМОСТЬ ОБОЗНАЧЕНИЯ.
             */
            if (_type == BodyType.POINT && _anchorID != null) {
              setFieldAtIndexVisible(_titleVisibilityCheckBoxIndex, true);
              _titleVisibilityCheckBox.setSelected(_ctrl.getEditor().anchMgr().getState(_anchorID).isTitleVisible());
            }

            /**
             * ВИДИМОСТЬ ПОВЕРХНОСТИ.
             */
            if (_type.hasSurface() && _type.hasCarcass()) {
              boolean isFilled = _ctrl.getEditor().isBodyFilled(_bodyID);
              setFieldAtIndexVisible(_fillVisibilityCheckBoxIndex, true);
              _fillVisibilityCheckBox.setSelected(isFilled);
            }

            /**
             * ВИДИМОСТЬ АСИМПТОТ.
             */
            if (_type == BodyType.HYPERBOLE) {
              _fillVisibilityCheckBox.setSelected((boolean)_state.getParam(DisplayParam.DRAW_ASYMPTOTES));
              setFieldAtIndexVisible(_visibilityAsymptotesCheckBoxIndex, true);
            }

            /**
             * НАДПИСЬ НА ОТРЕЗКЕ.
             */
            if (_type == BodyType.RIB && _anchorID != null) {
              _labelField.setText(_ctrl.getEditor().anchMgr().getState(_anchorID).getLabel());
              setFieldAtIndexVisible(_labelFieldIndex, true);
            }

            /**
             * ЦВЕТ ТОЧКИ.
             */
            if (_type == BodyType.POINT && _anchorID != null) {
              _pointColorChooser.setColor((ColorGL)_ctrl.getEditor().anchMgr().getState(_anchorID).getParam(DisplayParam.POINT_COLOR));
              setFieldAtIndexVisible(_pointColorChooserIndex, true);
            }

            /**
             * ЦВЕТ ПОВЕРХНОСТИ.
             */
            if (_type.hasSurface()) {
              ColorGL fillColor = _ctrl.getEditor().getBodySurfaceColor(_bodyID);
              _facetColorChooser.setColor(fillColor);
              setFieldAtIndexVisible(_facetColorChooserIndex, true);
            }

            /**
             * ЦВЕТ КАРКАСА.
             */
            if (_type.hasCarcass()) {
              ColorGL carcassColor = _ctrl.getEditor().getBodyCarcassColor(_bodyID);
              _carcassColorChooser.setColor(carcassColor);
              setFieldAtIndexVisible(_carcassColorChooserIndex, true);
            }

            /**
             * ЦВЕТ ТОЧЕК.
             */
            if (_type.isPolygon() || _type.isPolyhedron()) {
              _bodyPointsColorChooser.setColor(_ctrl.getEditor().getBodyPointsColor(_bodyID));
              setFieldAtIndexVisible(_bodyPointsColorChooserIndex, true);
            }

            /**
             * ТОЛЩИНА ТОЧКИ.
             */
            if (_type == BodyType.POINT && _anchorID != null) {
              _pointThicknessChooserPanel.setThickness(Thickness.get(_ctrl.getEditor().anchMgr().getState(_anchorID).getPointThickness()));
              setFieldAtIndexVisible(_pointThicknessChooserPanelIndex, true);
            }

            /**
             * СТИЛЬ ЛИНИЙ.
             */
            if (_state.hasParam(DisplayParam.CARCASS_STYLE)) {
              _carcassStyleChooser.setSelectedItem((LineType)_state.getParam(DisplayParam.CARCASS_STYLE));
              setFieldAtIndexVisible(_carcassStyleChooserIndex, true);
            } else if (_type == BodyType.RIB && _anchorID != null) {
              _ribStyleChooser.setSelectedItem(_ctrl.getEditor().anchMgr().getState(_anchorID).getLineType());
              setFieldAtIndexVisible(_ribStyleChooserIndex, true);
            }

            /**
             * ТОЛЩИНА ЛИНИЙ.
             */
            if (_state.hasParam(DisplayParam.CARCASS_THICKNESS)) {
              _carcassThicknessChooser.setSelectedItem(Thickness.get((float)_state.getParam(DisplayParam.CARCASS_THICKNESS)));
              setFieldAtIndexVisible(_carcassThicknessChooserIndex, true);
            } else if ((_type == BodyType.RIB || _type == BodyType.CIRCLE) && (_anchorID != null)) {
              _lineThicknessChooser.setSelectedItem(Thickness.get(_ctrl.getEditor().anchMgr().getState(_anchorID).getCarcassThickness()));
              setFieldAtIndexVisible(_lineThicknessChooserIndex, true);
            }

            /**
             * ПОМЕТКА НА ОТРЕЗКЕ.
             */
            if (_type == BodyType.RIB && _anchorID != null) {
              _ribMarkChooser.setSelectedItem(_ctrl.getEditor().anchMgr().getState(_anchorID).getMark());
              setFieldAtIndexVisible(_ribMarkChooserIndex, true);
            }

            /**
             * РАЗМЕР ПЛОСКОСТИ.
             */
            if (_type == BodyType.PLANE) {
              _planeIndentSlider.setValue((int)((double)_state.getParam(DisplayParam.PLANE_INDENT) / (INDENT_CONST * _ctrl.getScene().getCameraPosition().distance())));
              setFieldAtIndexVisible(_planeIndentSliderIndex, true);
            }

            /**
             * СТРЕЛКИ
             */
            if (_type == BodyType.RIB && _anchorID != null) {
              _beginArrowChooser.setSelectedItem(_ctrl.getEditor().anchMgr().getState(_anchorID).getArrowBegin());
              _endArrowChooser.setSelectedItem(_ctrl.getEditor().anchMgr().getState(_anchorID).getArrowEnd());
              setFieldAtIndexVisible(_arrowsChooserIndex, true);
            }
          }
        } catch (ExNoBody ex) {
        }
      }

      _isUpdating = false; // set up event listeners

      revalidate();
      repaint();
    }

    /**
     * Слайдер размера плоскости нуждается в перерисовке всякий раз, когда меняется масштаб
     * отображения сцены.
     */
    public void updateScale(double distance) {
      if (_bodyID == null) {
        return;
      }
      if (_type == BodyType.PLANE) {
        _planeIndentSlider.setValue((int)((double)_state.getParam(DisplayParam.PLANE_INDENT) / (INDENT_CONST * distance)));
        _planeIndentSlider.revalidate();
        _planeIndentSlider.repaint();
      }
    }
  }

  class BodyImageInfoPanel extends EdtTabPanel {

    public BodyImageInfoPanel() {
      super();
    }

    public void update() {
      removeAll();
      if (_bodyID == null) {
        return;
      }
      try {
        String[][] info = BodyInfoConstructor.getInfo(_ctrl, _bodyID, PRECISION.value());
        for (String[] row: info) {
          ImmutableTextField valueField = new ImmutableTextField(row[1]);
          addItem(row[0], valueField);
        }
      } catch (ExNoBody ex) {
        removeAll();
      }
      revalidate();
      repaint();
    }
  }
}
