package gui.dialog.create;

import bodies.BodyType;
import builders.ConeBuilder;
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
 * Create cone by the vertex, center and radius of the base circle
 * Using ConeBuilder
 * @author alexeev
 */
public class CreateConeDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointField;
  private AnchorChooser _centerField;
  private NumericTextField _radiusField;

  public CreateConeDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Задать конус вершиной, центром и радиусом основания");
    _nameField = new NameTextField();
    _nameField.setText(BodyType.CONE.getName(_ctrl.getEditor()));
    _pointField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _centerField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _radiusField = new NumericTextField(1, true);

    addItem("Имя конуса", _nameField);
    addItem("Вершина конуса", _pointField);
    addItem("Центр основания", _centerField);
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
          HashMap<String, BuilderParam> params = new HashMap<>();
          params.put(ConeBuilder.BLDKEY_NAME, new BuilderParam(ConeBuilder.BLDKEY_NAME, "Имя конуса", BuilderParamType.NAME, _nameField.getText()));
          params.put(ConeBuilder.BLDKEY_CENTER, new BuilderParam(ConeBuilder.BLDKEY_CENTER, "Центр основания", BuilderParamType.ANCHOR, _centerField.getAnchorID()));
          params.put(ConeBuilder.BLDKEY_VERTEX, new BuilderParam(ConeBuilder.BLDKEY_VERTEX, "Вершина конуса", BuilderParamType.ANCHOR, _pointField.getAnchorID()));
          params.put(ConeBuilder.BLDKEY_RADIUS, new BuilderParam(ConeBuilder.BLDKEY_RADIUS, "Радиус основания", BuilderParamType.DOUBLE, radius));
          _ctrl.add(new ConeBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        } catch (ParseException | InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
