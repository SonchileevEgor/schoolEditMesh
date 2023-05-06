package gui.dialog.create;

import bodies.BodyType;
import builders.CylinderBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.NameTextField;
import gui.elements.NumericTextField;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 *
 * @author alexeev
 */
public class CreateCylinderDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _c1Field;
  private AnchorChooser _c2Field;
  private NumericTextField _radiusField;

  public CreateCylinderDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Задать цилиндр вершинами оснований и радиусом");
    _nameField = new NameTextField(BodyType.CYLINDER.getName(_ctrl.getEditor()));
    _c1Field = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _c2Field = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _radiusField = new NumericTextField(1, true);

    addItem("Имя цилиндра", _nameField);
    addItem("Центр первого основания", _c1Field);
    addItem("Центр второго основания", _c2Field);
    addItem("Радиус основания", _radiusField);
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
          double radius = _radiusField.getNumericValue();
          HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
          params.put("name", new BuilderParam("name", "Имя цилиндра", BuilderParamType.NAME, _nameField.getText()));
          params.put("c1", new BuilderParam("c1", "Центр первого основания", BuilderParamType.ANCHOR, _c1Field.getAnchorID()));
          params.put("c2", new BuilderParam("c2", "Центр второго основания", BuilderParamType.ANCHOR, _c2Field.getAnchorID()));
          params.put("radius", new BuilderParam("radius", "Радиус основания", BuilderParamType.DOUBLE, radius));
          _ctrl.add(new CylinderBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        } catch (ParseException | InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
