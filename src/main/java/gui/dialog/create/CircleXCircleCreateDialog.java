package gui.dialog.create;

import bodies.BodyType;
import builders.CircleXCircleBuilder;
import editor.i_BodyBuilder;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.BodyChooserComboBox;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;

/**
 *
 * @author alexeev
 */
public class CircleXCircleCreateDialog extends CreateBodyDialog {
  private BodyChooserComboBox _circle1Field;
  private BodyChooserComboBox _circle2Field;

  public CircleXCircleCreateDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Пересечь две окружности");

    ArrayList<BodyType> circleList = new ArrayList<BodyType>();
    circleList.add(BodyType.CIRCLE);

    _circle1Field = new BodyChooserComboBox(_ctrl.getEditor().bd(), circleList);
    _circle2Field = new BodyChooserComboBox(_ctrl.getEditor().bd(), circleList);

    addItem("Окружность", _circle1Field);
    addItem("Окружность", _circle2Field);
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
        if (_circle1Field.getSelectedItem().equals(_circle2Field.getSelectedItem())) {
          showMessage("Выберите различные окружности", 1);
        } else {
          ArrayList<i_BodyBuilder> builders = new ArrayList<i_BodyBuilder>();
          CircleXCircleBuilder bb1 = new CircleXCircleBuilder(
                  BodyType.POINT.getName(_ctrl.getEditor()) + "_1");
          bb1.addCircle1(_circle1Field.getBody().id());
          bb1.addCircle2(_circle2Field.getBody().id());
          bb1.addDirection(true);
          CircleXCircleBuilder bb2 = new CircleXCircleBuilder(
                  BodyType.POINT.getName(_ctrl.getEditor()) + "_2");
          bb2.addCircle1(_circle1Field.getBody().id());
          bb2.addCircle2(_circle2Field.getBody().id());
          bb2.addDirection(false);
          builders.add(bb1);
          builders.add(bb2);
          _ctrl.addBodies(builders, _errorHandler, false);
          
          accepted = true;
          dispose();
        }
      }
    };
  }
}
