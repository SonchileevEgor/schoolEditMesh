package gui.dialog.create;

import bodies.BodyType;
import builders.PlaneThreePointsBuilder;
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
 * Create plane by three points
 * Using PlaneByThreePointsBuilder
 * @author alexeev
 */
public class CreatePlaneByThreePointsDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointField1;
  private AnchorChooser _pointField2;
  private AnchorChooser _pointField3;

  public CreatePlaneByThreePointsDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить плоскость по трём точкам");
    _nameField = new NameTextField(BodyType.PLANE.getName(_ctrl.getEditor()));
    _pointField1 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField2 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField3 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Имя плоскости", _nameField);
    addItem("Точка 1:", _pointField1);
    addItem("Точка 2:", _pointField2);
    addItem("Точка 3:", _pointField3);
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
          params.put("name", new BuilderParam("name", "Имя плоскости", BuilderParamType.NAME, _nameField.getText()));
          params.put("A", new BuilderParam("A", "A", BuilderParamType.ANCHOR, (String)_pointField1.getAnchorID()));
          params.put("B", new BuilderParam("B", "B", BuilderParamType.ANCHOR, (String)_pointField2.getAnchorID()));
          params.put("C", new BuilderParam("C", "C", BuilderParamType.ANCHOR, (String)_pointField3.getAnchorID()));
          _ctrl.add(new PlaneThreePointsBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        } catch (InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
