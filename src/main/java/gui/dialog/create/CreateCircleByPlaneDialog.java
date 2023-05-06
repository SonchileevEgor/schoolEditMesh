package gui.dialog.create;

import bodies.BodyType;
import builders.CircleByPlaneBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
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
 *
 * @author
 */
public class CreateCircleByPlaneDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _center;
  private AnchorChooser _point;
  private BodyChooserComboBox _planeField;
  private Editor _edt;
  /**
   *
   * @param ctrl
   */
  public CreateCircleByPlaneDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить окружность по плоскости, центру и точке");
    _edt = _ctrl.getEditor();
    _nameField = new NameTextField(BodyType.CIRCLE.getName(_ctrl.getEditor()));
    _center = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _point = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    ArrayList<BodyType> planeList = new ArrayList<BodyType>();
    planeList.add(BodyType.PLANE);

    _planeField = new BodyChooserComboBox(_ctrl.getEditor().bd(), planeList);
    addItem("Имя окружности", _nameField);
    addItem("Центр", _center);
    addItem("Точка на окружности", _point);
    addItem("Плоскость", _planeField);
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
        if ((_center.getSelectedItem()).equals(_point.getSelectedItem())) {
          showMessage("Выберите различные точки", 1);
        } else {
          try {
            _ctrl.validateBodyTitle(_nameField.getText());
            HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
            params.put("name", new BuilderParam("name", "Имя окружности", BuilderParamType.NAME, _nameField.getText()));
            params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
            params.put("center", new BuilderParam("center", "Центр", BuilderParamType.ANCHOR, (String)_center.getAnchorID()));
            params.put("point", new BuilderParam("point", "Точка на окружности", BuilderParamType.ANCHOR, (String)_point.getAnchorID()));
            _ctrl.add(new CircleByPlaneBuilder(params), _errorHandler, false);
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