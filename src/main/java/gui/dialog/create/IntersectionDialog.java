package gui.dialog.create;

import bodies.BodyType;
import builders.CircleXCircleBuilder;
import builders.ConeSectionBuilder;
import builders.CubeSectionBuilder;
import builders.CylinderSectionBuilder;
import builders.LineXConeBuilder;
import builders.LineXCylinderBuilder;
import builders.LineXLineBuilder;
import builders.LineXPlaneBuilder;
import builders.PlaneXPlaneBuilder;
import builders.PrismSectionBuilder;
import builders.PyramidSectionBuilder;
import builders.SphereSectionBuilder;
import builders.SphereXSphereBuilder;
import builders.TetrahedronSectionBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_Body;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.BodyChooserList;
import gui.elements.NameTextField;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import util.Util;

/**
 * Dialog for building intersection of two bodies.
 *
 * @author alexeev
 */
public class IntersectionDialog extends CreateBodyDialog {
  public IntersectionDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Провести пересечение объектов");
    _nameField = new NameTextField();

    _bodyTable = new HashMap<BodyType, BodyType[]>();
    _bodyTable.put(BodyType.CIRCLE, new BodyType[]{BodyType.CIRCLE});
    _bodyTable.put(BodyType.CONE, new BodyType[]{BodyType.LINE, BodyType.PLANE});
    _bodyTable.put(BodyType.CUBE, new BodyType[]{BodyType.PLANE});
    _bodyTable.put(BodyType.CYLINDER, new BodyType[]{BodyType.LINE, BodyType.PLANE});
    _bodyTable.put(BodyType.LINE, new BodyType[]{BodyType.CONE, BodyType.CYLINDER, BodyType.LINE, BodyType.PLANE, BodyType.RIB});
    _bodyTable.put(BodyType.PLANE, new BodyType[]{BodyType.CONE, BodyType.CYLINDER, BodyType.CUBE, BodyType.LINE, BodyType.PLANE, BodyType.PRISM, BodyType.PYRAMID, BodyType.RIB, BodyType.SPHERE, BodyType.TETRAHEDRON});
    _bodyTable.put(BodyType.PRISM, new BodyType[]{BodyType.PLANE});
    _bodyTable.put(BodyType.PYRAMID, new BodyType[]{BodyType.PLANE});
    _bodyTable.put(BodyType.RIB, new BodyType[]{BodyType.LINE, BodyType.PLANE, BodyType.RIB});
    _bodyTable.put(BodyType.SPHERE, new BodyType[]{BodyType.PLANE, BodyType.SPHERE});
    _bodyTable.put(BodyType.TETRAHEDRON, new BodyType[]{BodyType.PLANE});

    _leftBodyList = new BodyChooserList(_ctrl.getBodyContainer(), new ArrayList<BodyType>(_bodyTable.keySet()));
    JScrollPane leftListScroller = new JScrollPane(_leftBodyList);
    leftListScroller.setPreferredSize(new Dimension(150, 300));

    _rightBodyList = new BodyChooserList(_ctrl.getBodyContainer(), new ArrayList<BodyType>());
    JScrollPane rightListScroller = new JScrollPane(_rightBodyList);
    rightListScroller.setPreferredSize(new Dimension(150, 300));

    _leftBodyList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        _rightBodyList.rebuild(Arrays.asList(_bodyTable.get(_leftBodyList.getBody().type())), _leftBodyList.getBody().id());
      }
    });

    JPanel intersectionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
    intersectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
    intersectionPanel.add(leftListScroller);
    intersectionPanel.add(rightListScroller);

    addItem("Имя пересечения", _nameField);
    addItem(intersectionPanel, "center");
    addButtons();
    addFeedback();

    pack();
		setCenterLocation();
  }

  @Override
  protected AbstractAction OKAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent ae) {
        try {
          if (_leftBodyList.getSelectedIndex() == -1 ||
              _rightBodyList.getSelectedIndex() == -1) {
            showMessage("Укажите два тела", 1);
          } else {
            _ctrl.validateBodyTitle(_nameField.getText());
            HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
            i_Body leftBody = _leftBodyList.getBody();
            i_Body rightBody = _rightBodyList.getBody();
            switch (leftBody.type()) {
              case CUBE:
                switch (rightBody.type()) {
                  case PLANE:
                    params.put("cube", new BuilderParam("cube", "Куб", BuilderParamType.BODY, leftBody.id()));
                    params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new CubeSectionBuilder(params), _errorHandler, false);
                    break;
                  default:
                    throw new ExNoBody("");
                }
                break;
              case CIRCLE:
                switch (rightBody.type()) {
                  case CIRCLE:
                    CircleXCircleBuilder builder = new CircleXCircleBuilder(_nameField.getText());
                    builder.addCircle1(leftBody.id());
                    builder.addCircle2(rightBody.id());
                    builder.addDirection(false); //!! TODO: add two builders.
                    _ctrl.add(builder, _errorHandler, false);
                    break;
                  default:
                    throw new ExNoBody("");
                }
                break;
              case CONE:
                switch (rightBody.type()) {
                  case PLANE:
                    params.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, leftBody.id()));
                    params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new ConeSectionBuilder(params), _errorHandler, false);
                    break;
                  case LINE:
                    params.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, leftBody.id()));
                    params.put("line", new BuilderParam("line", "Прямая", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new LineXConeBuilder(params), _errorHandler, false);
                    break;
                  default:
                    throw new ExNoBody("");
                }
                break;
              case CYLINDER:
                switch (rightBody.type()) {
                  case PLANE:
                    params.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, leftBody.id()));
                    params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new CylinderSectionBuilder(params), _errorHandler, false);
                    break;
                  case LINE:
                    params.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, leftBody.id()));
                    params.put("line", new BuilderParam("line", "Прямая", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new LineXCylinderBuilder(params), _errorHandler, false);
                    break;
                  default:
                    throw new ExNoBody("");
                }
                break;
              case LINE:
                switch (rightBody.type()) {
                  case CONE:
                    params.put("line", new BuilderParam("line", "Прямая", BuilderParamType.BODY, leftBody.id()));
                    params.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new LineXConeBuilder(params), _errorHandler, false);
                    break;
                  case CYLINDER:
                    params.put("line", new BuilderParam("line", "Прямая", BuilderParamType.BODY, leftBody.id()));
                    params.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new LineXCylinderBuilder(params), _errorHandler, false);
                    break;
                  case PLANE:
                    params.put("line", new BuilderParam("line", "Прямая", BuilderParamType.BODY, leftBody.id()));
                    params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new LineXPlaneBuilder(params), _errorHandler, false);
                    break;
                  case LINE:
                    LineXLineBuilder builder = new LineXLineBuilder(_nameField.getText());
                    builder.addLine1(leftBody.id());
                    builder.addLine2(rightBody.id());
                    _ctrl.add(builder, _errorHandler, enabled);
                    break;
                  default:
                    throw new ExNoBody("");
                }
                break;
              case PLANE:
                switch (rightBody.type()) {
                  case CONE:
                    params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, leftBody.id()));
                    params.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new ConeSectionBuilder(params), _errorHandler, false);
                    break;
                  case CYLINDER:
                    params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, leftBody.id()));
                    params.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new CylinderSectionBuilder(params), _errorHandler, false);
                    break;
                  case PLANE:
                    params.put("p1", new BuilderParam("p1", "Плоскость", BuilderParamType.BODY, leftBody.id()));
                    params.put("p2", new BuilderParam("p2", "Плоскость", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new PlaneXPlaneBuilder(params), _errorHandler, false);
                    break;
                  case LINE:
                    params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, leftBody.id()));
                    params.put("line", new BuilderParam("line", "Прямая", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new LineXPlaneBuilder(params), _errorHandler, false);
                    break;
                  case CUBE:
                    params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, leftBody.id()));
                    params.put("cube", new BuilderParam("cube", "Куб", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new CubeSectionBuilder(params), _errorHandler, false);
                    break;
                  case PRISM:
                    params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, leftBody.id()));
                    params.put("prism", new BuilderParam("prism", "Призма", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new PrismSectionBuilder(params), _errorHandler, false);
                    break;
                  case PYRAMID:
                    params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, leftBody.id()));
                    params.put("pyramid", new BuilderParam("pyramid", "Пирамида", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new PyramidSectionBuilder(params), _errorHandler, false);
                    break;
                  case TETRAHEDRON:
                    params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, leftBody.id()));
                    params.put("tetrahedron", new BuilderParam("tetrahedron", "Тетраэдр", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new TetrahedronSectionBuilder(params), _errorHandler, false);
                    break;
                  case SPHERE:
                    params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, leftBody.id()));
                    params.put("sphere", new BuilderParam("sphere", "Сфера", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new SphereSectionBuilder(params), _errorHandler, false);
                    break;
                  default:
                    throw new ExNoBody("");
                }
                break;
              case PRISM:
                switch (rightBody.type()) {
                  case PLANE:
                    params.put("prism", new BuilderParam("prism", "Призма", BuilderParamType.BODY, leftBody.id()));
                    params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new PrismSectionBuilder(params), _errorHandler, false);
                    break;
                  default:
                    throw new ExNoBody("");
                }
                break;
              case PYRAMID:
                switch (rightBody.type()) {
                  case PLANE:
                    params.put("pyramid", new BuilderParam("pyramid", "Пирамида", BuilderParamType.BODY, leftBody.id()));
                    params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new PyramidSectionBuilder(params), _errorHandler, false);
                    break;
                  default:
                    throw new ExNoBody("");
                }
                break;
              case SPHERE:
                switch (rightBody.type()) {
                  case PLANE:
                    params.put("sphere", new BuilderParam("sphere", "Сфера", BuilderParamType.BODY, leftBody.id()));
                    params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new SphereSectionBuilder(params), _errorHandler, false);
                    break;
                  case SPHERE:
                    params.put("s1", new BuilderParam("s1", "Сфера", BuilderParamType.BODY, leftBody.id()));
                    params.put("s2", new BuilderParam("s2", "Сфера", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new SphereXSphereBuilder(params), _errorHandler, false);
                    break;
                  default:
                    throw new ExNoBody("");
                }
                break;
              case TETRAHEDRON:
                switch (rightBody.type()) {
                  case PLANE:
                    params.put("tetrahedron", new BuilderParam("tetrahedron", "Тетраэдр", BuilderParamType.BODY, leftBody.id()));
                    params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, rightBody.id()));
                    _ctrl.add(new TetrahedronSectionBuilder(params), _errorHandler, false);
                    break;
                  default:
                    throw new ExNoBody("");
                }
                break;
              default:
                throw new ExNoBody("");
            }
            accepted = true;
            dispose();
          }
        } catch (ExNoBody | InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }

  private BodyChooserList _leftBodyList;
  private BodyChooserList _rightBodyList;
  private HashMap<BodyType, BodyType[]> _bodyTable;
  private NameTextField _nameField;
}
