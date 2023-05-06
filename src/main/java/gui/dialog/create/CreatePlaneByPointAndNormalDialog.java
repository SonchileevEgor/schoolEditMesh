package gui.dialog.create;

import bodies.BodyType;
import builders.PlaneBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import geom.Vect3d;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.NameTextField;
import gui.elements.XYZPanel;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Create a plane by the point and coordinates (x,y,z) of the normal vector
 * Using PlaneBuilder
 * @author alexeev
 */
public class CreatePlaneByPointAndNormalDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointList;
  private XYZPanel _normalPanel;

  public CreatePlaneByPointAndNormalDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Задать плоскость точкой и вектором нормали");
    _nameField = new NameTextField(BodyType.PLANE.getName(_ctrl.getEditor()));
    _pointList = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _normalPanel = new XYZPanel(true);

    addItem("Имя плоскости", _nameField);
    addItem("Точка на плоскости", _pointList);
    addItem("Вектор нормали", _normalPanel);
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
          Vect3d n = new Vect3d(_normalPanel.x(), _normalPanel.y(), _normalPanel.z());
          HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
          params.put("name", new BuilderParam("name", "Имя плоскости", BuilderParamType.NAME, BodyType.PLANE.getName(_ctrl.getEditor())));
          params.put("point", new BuilderParam("point", "Точка на плоскости", BuilderParamType.ANCHOR, _pointList.getAnchorID()));
          params.put("normal", new BuilderParam("normal", "Вектор нормали", BuilderParamType.VECT, n));
          _ctrl.add(new PlaneBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        } catch (ParseException | InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
