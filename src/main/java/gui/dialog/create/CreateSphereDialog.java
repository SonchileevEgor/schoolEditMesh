package gui.dialog.create;

import bodies.BodyType;
import builders.SphereBuilder;
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
 * Create sphere by center and radius
 * Using SphereBuilder
 * @author alexeev
 */
public class CreateSphereDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private NumericTextField _radiusField;
  private AnchorChooser _centerList;

  public CreateSphereDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Добавить сферу");
    _nameField = new NameTextField(BodyType.SPHERE.getName(_ctrl.getEditor()));
    _radiusField = new NumericTextField(1, true);
    _centerList = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Имя сферы", _nameField);
    addItem("Радиус сферы", _radiusField);
    addItem("Центр сферы", _centerList);
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
          params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
          params.put("center", new BuilderParam("center", "Центр сферы", BuilderParamType.ANCHOR, _centerList.getAnchorID()));
          params.put("radius", new BuilderParam("radius", "Радиус сферы", BuilderParamType.DOUBLE_POSITIVE, radius));
          _ctrl.add(new SphereBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        } catch (ParseException | InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
