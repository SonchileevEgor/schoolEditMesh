package gui.dialog.create;

import builders.LineByTwoPointsBuilder;
import editor.AnchorType;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Create plane by three points
 * Using PlaneByThreePointsBuilder
 * @author alexeev
 */
public class CreateLineByTwoPointDialog extends CreateBodyDialog {
  private AnchorChooser _pointField1;
  private AnchorChooser _pointField2;

  public CreateLineByTwoPointDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить прямую по двум точкам");
    _pointField1 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField2 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);


    addItem("Точка 1:", _pointField1);
    addItem("Точка 2:", _pointField2);
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
        if ((_pointField1.getSelectedItem()).equals((_pointField2.getSelectedItem()))) {
          showMessage("Выберите различные точки", 1);
        } else {
          LineByTwoPointsBuilder builder = new LineByTwoPointsBuilder(
              (String)_pointField1.getSelectedItem() + (String)_pointField2.getSelectedItem());
          builder.addA((String)_pointField1.getAnchorID());
          builder.addB((String)_pointField1.getAnchorID());
          _ctrl.add(builder, _errorHandler, false);
          accepted = true;
          dispose();
        }
      }
    };
  }
 }