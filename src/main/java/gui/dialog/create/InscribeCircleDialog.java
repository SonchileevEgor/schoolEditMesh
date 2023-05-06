package gui.dialog.create;

import bodies.BodyType;
import builders.CircleInPolygonBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Inscribes a circle into the plain figure
 * Using CircleInPolygonBuilder
 * @author alexeev
 */
public class InscribeCircleDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _polyField;

  public InscribeCircleDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Вписать окружность");
    _nameField = new NameTextField(BodyType.CIRCLE.getName(_ctrl.getEditor()));
    _polyField = new AnchorChooser(ctrl, AnchorType.ANC_POLY);

    addItem("Имя окружности", _nameField);
    addItem("Многоугольник", _polyField);
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
          params.put("poly", new BuilderParam("poly", "Многоугольник", BuilderParamType.ANCHOR, _polyField.getAnchorID()));
          _ctrl.add(new CircleInPolygonBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        } catch (InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
