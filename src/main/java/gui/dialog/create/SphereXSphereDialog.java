package gui.dialog.create;

import bodies.BodyType;
import builders.SphereXSphereBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.BodyChooserComboBox;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 *
 * @author alexeev
 */
public class SphereXSphereDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private BodyChooserComboBox _sphereField1;
  private BodyChooserComboBox _sphereField2;

  public SphereXSphereDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить пересечение сфер");

    _nameField = new NameTextField(BodyType.CIRCLE.getName(_ctrl.getEditor()));

    ArrayList<BodyType> sphereList = new ArrayList<BodyType>();
    sphereList.add(BodyType.SPHERE);

    _sphereField1 = new BodyChooserComboBox(_ctrl.getEditor().bd(), sphereList);
    _sphereField2 = new BodyChooserComboBox(_ctrl.getEditor().bd(), sphereList);
    _sphereField1.setSelectedIndex(0);
    _sphereField2.setSelectedIndex(1);

    addItem("Имя окружности", _nameField);
    addItem("Сфера", _sphereField1);
    addItem("Сфера", _sphereField2);
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
        if (_sphereField1.getSelectedItem().equals(_sphereField2.getSelectedItem())) {
          showMessage("Выберите различные сферы", 1);
        } else {
          HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
          params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
          params.put("s1", new BuilderParam("s1", "Сфера", BuilderParamType.BODY, _sphereField1.getBody().id()));
          params.put("s2", new BuilderParam("s2", "Сфера", BuilderParamType.BODY, _sphereField2.getBody().id()));
          _ctrl.add(new SphereXSphereBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        }
      }
    };
  }
}
