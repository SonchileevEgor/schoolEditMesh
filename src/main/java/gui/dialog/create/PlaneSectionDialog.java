package gui.dialog.create;

import bodies.BodyType;
import builders.ConeSectionBuilder;
import builders.CubeSectionBuilder;
import builders.CylinderSectionBuilder;
import builders.PlaneXPlaneBuilder;
import builders.PrismSectionBuilder;
import builders.PyramidSectionBuilder;
import builders.SphereSectionBuilder;
import builders.TetrahedronSectionBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import editor.i_Body;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.BodyChooserComboBox;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Constructs a section of the body by the specified plane
 * @author alexeev
 */
public class PlaneSectionDialog extends CreateBodyDialog {
  private i_BodyContainer _bodies;
  private NameTextField _nameField;
  private BodyChooserComboBox _bodyField;
  private BodyChooserComboBox _planeField;

  public PlaneSectionDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Провести сечение объекта плоскостью");
    _bodies = _ctrl.getEditor().bd();
    _nameField = new NameTextField();
    _bodyField = new BodyChooserComboBox(_bodies, BodyType.getTypesForPlaneSection());

    ArrayList<BodyType> planeList = new ArrayList<BodyType>();
    planeList.add(BodyType.PLANE);

    _planeField = new BodyChooserComboBox(_bodies, planeList);

    addItem("Имя сечения", _nameField);
    addItem("Тело", _bodyField);
    addItem("Секущая плоскость", _planeField);
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
          _ctrl.validateBodyTitle(_nameField.getText());
          HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
          params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
          i_Body body = _bodyField.getBody();
          switch (body.type()) {
            case CUBE:
              params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
              params.put("cube", new BuilderParam("cube", "Куб", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new CubeSectionBuilder(params), _errorHandler, false);
              break;
            case SPHERE:
              params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
              params.put("sphere", new BuilderParam("sphere", "Сфера", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new SphereSectionBuilder(params), _errorHandler, false);
              break;
            case CONE:
              params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
              params.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new ConeSectionBuilder(params), _errorHandler, false);
              break;
            case PYRAMID:
              params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
              params.put("pyramid", new BuilderParam("pyramid", "Пирамида", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new PyramidSectionBuilder(params), _errorHandler, false);
              break;
            case PRISM:
              params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
              params.put("prism", new BuilderParam("prism", "Призма", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new PrismSectionBuilder(params), _errorHandler, false);
              break;
            case TETRAHEDRON:
            case REG_TETRAHEDRON:
              params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
              params.put("tetrahedron", new BuilderParam("tetrahedron", "Тетраэдр", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new TetrahedronSectionBuilder(params), _errorHandler, false);
              break;
            case CYLINDER:
              params.put("plane", new BuilderParam("plane", "Секущая плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
              params.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new CylinderSectionBuilder(params), _errorHandler, false);
              break;
            case PLANE:
              params.put("p1", new BuilderParam("p1", "Плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
              params.put("p2", new BuilderParam("p2", "Плоскость", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new PlaneXPlaneBuilder(params), _errorHandler, false);
              break;
            default:
              showMessage("Выберите другое тело", 1);
              return;
          }
          accepted = true;
          dispose();
        } catch (InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
