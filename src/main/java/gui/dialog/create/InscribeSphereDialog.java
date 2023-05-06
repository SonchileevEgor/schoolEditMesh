package gui.dialog.create;

import bodies.BodyType;
import builders.SphereInConeBuilder;
import builders.SphereInCubeBuilder;
import builders.SphereInCylinderBuilder;
import builders.SphereInTetrahedronBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import editor.i_Body;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.BodyChooserComboBox;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Inscribes a sphere into the body
 * Using SphereInCubeBuilder, SphereInTetrahedronBuilder
 * @author alexeev
 */
public class InscribeSphereDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private BodyChooserComboBox _bodyField;

  /**
   *
   * @param ctrl
   */
  public InscribeSphereDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Описать сферу");
    _nameField = new NameTextField(BodyType.SPHERE.getName(_ctrl.getEditor()));
    _bodyField = new BodyChooserComboBox(_ctrl.getEditor().bd(), BodyType.getBodiesWithInscribedSphere());

    addItem("Имя сферы", _nameField);
    addItem("Тело", _bodyField);
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
              params.put("cube", new BuilderParam("cube", "Куб", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new SphereInCubeBuilder(params), _errorHandler, false);
              break;
            case TETRAHEDRON:
              params.put("tetrahedron", new BuilderParam("tetrahedron", "Тетраэдр", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new SphereInTetrahedronBuilder(params), _errorHandler, false);
              break;
            case CONE:
              params.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new SphereInConeBuilder(params), _errorHandler, false);
              break;
            case CYLINDER:
              params.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, _bodyField.getBody().id()));
              _ctrl.add(new SphereInCylinderBuilder(params), _errorHandler, false);
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
