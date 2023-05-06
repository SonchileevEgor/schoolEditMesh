package gui.dialog.create;

import bodies.BodyType;
import builders.SphereOnPlaneProjectionBuilder;
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
 *
 * @author alexeev
 */
public class PlaneProjectionDialog extends CreateBodyDialog {
  private i_BodyContainer _bodies;
  private NameTextField _nameField;
  private BodyChooserComboBox _bodyField;
  private BodyChooserComboBox _planeField;

  public PlaneProjectionDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Проекция сферы на плоскость");
    _nameField = new NameTextField(BodyType.SPHERE.getName(_ctrl.getEditor()));
    _bodies = ctrl.getBodyContainer();

    //types which allow to be projected
    ArrayList<BodyType> allowedTypes = new ArrayList<BodyType>();
    allowedTypes.add(BodyType.SPHERE);

    _bodyField = new BodyChooserComboBox(_bodies, allowedTypes);

    ArrayList<BodyType> planeList = new ArrayList<BodyType>();
    planeList.add(BodyType.PLANE);

    _planeField = new BodyChooserComboBox(_bodies, planeList);

    addItem("Имя проекции", _nameField);
    addItem("Сфера", _bodyField);
    addItem("Плоскость", _planeField);
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
            case SPHERE:
              params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
              params.put("sphere", new BuilderParam("sphere", "Сфера", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new SphereOnPlaneProjectionBuilder(params), _errorHandler, false);
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
