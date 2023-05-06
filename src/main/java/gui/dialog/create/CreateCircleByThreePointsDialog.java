package gui.dialog.create;

import bodies.BodyType;
import builders.CircleByThreePointsBuilder;
import editor.AnchorType;
import editor.Editor;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Create a circle by three points dialog.
 *
 * @author alexeev
 */
public class CreateCircleByThreePointsDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointField1;
  private AnchorChooser _pointField2;
  private AnchorChooser _pointField3;
  private Editor _edt;

  /**
   *
   * @param ctrl
   */
  public CreateCircleByThreePointsDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить окружность по трём точкам");
    _edt = _ctrl.getEditor();
    _nameField = new NameTextField(BodyType.CIRCLE.getName(_ctrl.getEditor()));
    _pointField1 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField2 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField3 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Имя окружности:", _nameField);
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
        if (util.Util.hasDuplicates(new Object[]{_pointField1.getSelectedItem(),
                                                 _pointField2.getSelectedItem(),
                                                 _pointField3.getSelectedItem()})) {
          showMessage("Выберите различные точки", 1);
        } else {
          try {
            _ctrl.validateBodyTitle(_nameField.getText());
            CircleByThreePointsBuilder builder = new CircleByThreePointsBuilder(_nameField.getText());
            builder.addA((String)_pointField1.getAnchorID());
            builder.addB((String)_pointField2.getAnchorID());
            builder.addC((String)_pointField3.getAnchorID());
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