package gui.dialog.create;

import bodies.BodyType;
import builders.SphereOutConeBuilder;
import builders.SphereOutCubeBuilder;
import builders.SphereOutCylinderBuilder;
import builders.SphereOutPrismBuilder;
import builders.SphereOutPyramidBuilder;
import builders.SphereOutTetrahedronBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.BodyChooserComboBox;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Circumscribe a sphere into a specified body
 * Using SphereIn[...]Builder
 * @author alexeev
 */
public class CircumscribeSphereDialog extends CreateBodyDialog {
  private i_BodyContainer _bodies;
  private NameTextField _nameField;
  private BodyChooserComboBox _bodyField;

  public CircumscribeSphereDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Описать сферу");
    _bodies = _ctrl.getEditor().bd();
    _nameField = new NameTextField(BodyType.SPHERE.getName(_ctrl.getEditor()));
    _bodyField = new BodyChooserComboBox(_bodies, BodyType.getBodiesWithCircumscribedSphere());

    addItem("Имя объекта", _nameField);
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
          i_BodyBuilder bb;
          _ctrl.validateBodyTitle(_nameField.getText());
          HashMap<String, BuilderParam> params = new HashMap<>();
          i_Body body = _bodyField.getBody();
          params.put("name", new BuilderParam("name", "Имя тела", BuilderParamType.NAME, _nameField.getText()));
          switch (body.type()) {
            case CUBE:
              params.put("cube", new BuilderParam("cube", "Куб", BuilderParamType.BODY, body.id()));
              bb = new SphereOutCubeBuilder(params);
              break;
            case TETRAHEDRON:
              params.put("tetrahedron", new BuilderParam("tetrahedron", "Тетраэдр", BuilderParamType.BODY, body.id()));
              bb = new SphereOutTetrahedronBuilder(params);
              break;
            case PRISM:
            case PARALLELEPIPED:
              params.put("prism", new BuilderParam("prism", "Призма", BuilderParamType.BODY, body.id()));
              bb = new SphereOutPrismBuilder(params);
              break;
            case PYRAMID:
              params.put("pyramid", new BuilderParam("pyramid", "Пирамида", BuilderParamType.BODY, body.id()));
              bb = new SphereOutPyramidBuilder(params);
              break;
            case CONE:
              params.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, body.id()));
              bb = new SphereOutConeBuilder(params);
              break;
            case CYLINDER:
              params.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, body.id()));
              bb = new SphereOutCylinderBuilder(params);
              break;
            default:
              throw new ExNoBody("");
          }
          _ctrl.add(bb, _errorHandler, false);
          accepted = true;
          dispose();
        } catch (InvalidBodyNameException | ExNoBody ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
