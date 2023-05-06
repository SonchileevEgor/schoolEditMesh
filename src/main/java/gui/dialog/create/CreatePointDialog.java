package gui.dialog.create;

import bodies.BodyType;
import builders.PointBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import geom.Vect3d;
import gui.EdtController;
import gui.elements.NameTextField;
import gui.elements.XYZPanel;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Create point by the (x,y,z) coordinates
 * Using PointBuilder
 * @author alexeev
 */
public class CreatePointDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private XYZPanel _coordPanel;

  public CreatePointDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Задать точку набором координат");
    _nameField = new NameTextField(BodyType.POINT.getName(_ctrl.getEditor()));
    _coordPanel = new XYZPanel(ctrl.getScene().is3d());

    addItem("Имя точки", _nameField);
    addItem("Координаты точки", _coordPanel);
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
          Vect3d p = new Vect3d(_coordPanel.x(), _coordPanel.y(), _coordPanel.z());
          HashMap<String, BuilderParam> params = new HashMap<>();
          params.put("name", new BuilderParam("name", "Имя точки", BuilderParamType.NAME, _nameField.getText()));
          params.put("P", new BuilderParam("P", "Координаты точки", BuilderParamType.POINT, p));
          _ctrl.add(new PointBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        } catch (ParseException | InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
