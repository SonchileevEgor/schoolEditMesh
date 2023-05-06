package gui.dialog.create;

import bodies.BodyType;
import builders.PointOnPlaneProjectionBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.BodyChooserComboBox;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Constructs a projection of the point onto the plane
 * Using PointOnPlaneProjectionBuilder
 * @author alexeev
 */
public class ProjectPointOnPlaneDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private BodyChooserComboBox _planeField;
  private AnchorChooser _pointField;

  public ProjectPointOnPlaneDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Опустить перпендикуляр на плоскость");
    _nameField = new NameTextField();

    ArrayList<BodyType> planeList = new ArrayList<BodyType>();
    planeList.add(BodyType.PLANE);

    _planeField = new BodyChooserComboBox(_ctrl.getEditor().bd(), planeList);
    _pointField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Имя проекции", _nameField);
    addItem("Плоскость", _planeField);
    addItem("Точка", _pointField);
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
          if (_planeField.getBody().type() == BodyType.PLANE) {
            HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
            params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
            params.put("point", new BuilderParam("point", "Точка", BuilderParamType.ANCHOR, _pointField.getAnchorID()));
            _ctrl.add(new PointOnPlaneProjectionBuilder(params), _errorHandler, false);
          } else {
            showMessage("Выберите другое тело", 1);
          }
          accepted = true;
          dispose();
        } catch (InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}