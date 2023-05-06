package gui.dialog.create;

import bodies.BodyType;
import static builders.BodyBuilder.BLDKEY_NAME;
import builders.ElementaryFunctionBuilder;
import builders.PointBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import geom.Vect3d;
import gui.EdtController;
import gui.elements.NameTextField;
import gui.elements.FunctionInputPanel;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Create elementaryFunction by function
 * @author alena
 */
public class CreateElementaryFunctionDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private FunctionInputPanel _fxPanel;

  public CreateElementaryFunctionDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Задать функцию");
    _nameField = new NameTextField(BodyType.POINT.getName(_ctrl.getEditor()));
    _fxPanel = new FunctionInputPanel();

    addItem("Имя функции", _nameField);
    addItem("Введите функцию:", _fxPanel);
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
          HashMap<String, BuilderParam> elementaryFunctionParams = new HashMap<>();
            elementaryFunctionParams.put(BLDKEY_NAME, new BuilderParam(BLDKEY_NAME, "Имя", BuilderParamType.NAME, _nameField.getText()));
            try {
                elementaryFunctionParams.put("functionString", new BuilderParam("functionString", "Строка функции", BuilderParamType.ANCHOR, _fxPanel.fx()));
                 ElementaryFunctionBuilder builder = new ElementaryFunctionBuilder(elementaryFunctionParams);
                _ctrl.add(builder, _errorHandler, false);
                accepted = true;
                dispose();
            } catch (ParseException ex) {
                showMessage(ex.getMessage(), 2);
            }
           
      }
    };
  }
}
