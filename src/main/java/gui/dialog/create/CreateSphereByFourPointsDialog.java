package gui.dialog.create;

import bodies.BodyType;
import builders.SphereByFourPointsBuilder;
import editor.AnchorType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Create Sphere by four points
 * Using SphereByFourPointsBuilder
 * @author alexeev
 */
public class CreateSphereByFourPointsDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointField1;
  private AnchorChooser _pointField2;
  private AnchorChooser _pointField3;
  private AnchorChooser _pointField4;

  public CreateSphereByFourPointsDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить сферу по четырём точкам");
    _nameField = new NameTextField(BodyType.SPHERE.getName(_ctrl.getEditor()));
    _pointField1 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField2 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField3 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField4 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Имя сферы", _nameField);
    addItem("Точка 1:", _pointField1);
    addItem("Точка 2:", _pointField2);
    addItem("Точка 3:", _pointField3);
    addItem("Точка 4:", _pointField4);
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
        if (util.Util.hasDuplicates(new Object[]{
                                          _pointField1.getSelectedItem(),
                                          _pointField2.getSelectedItem(),
                                          _pointField3.getSelectedItem(),
                                          _pointField4.getSelectedItem()})){
          showMessage("Выберите различные точки", 1);
        } else {
          try {
            _ctrl.validateBodyTitle(_nameField.getText());
            SphereByFourPointsBuilder builder = new SphereByFourPointsBuilder(_nameField.getText());
            builder.addA((String)_pointField1.getAnchorID());
            builder.addB((String)_pointField2.getAnchorID());
            builder.addC((String)_pointField3.getAnchorID());
            builder.addD((String)_pointField4.getAnchorID());
            _ctrl.add(builder, _errorHandler, false);
            accepted = true;
            dispose();
          } catch (InvalidBodyNameException ex) {
            showMessage(ex.getMessage(), 2);
          }
        }
      }
    };
  }
}
