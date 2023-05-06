package gui.dialog.create;

import bodies.BodyType;
import builders.OutTangentForTwoCirclesBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.i_BodyBuilder;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.BodyChooserComboBox;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 *
 * @author alexeev
 */
public class OutTangentForTwoCirclesCreateDialog extends CreateBodyDialog {
  private BodyChooserComboBox _circleField1;
  private BodyChooserComboBox _circleField2;

  public OutTangentForTwoCirclesCreateDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Провести внешние касательные к двум окружностям");
    ArrayList<BodyType> circleList = new ArrayList<BodyType>();
    circleList.add(BodyType.CIRCLE);

    _circleField1 = new BodyChooserComboBox(_ctrl.getEditor().bd(), circleList);
    _circleField2 = new BodyChooserComboBox(_ctrl.getEditor().bd(), circleList);

    addItem("Первая окружность", _circleField1);
    addItem("Вторая окружность", _circleField2);
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

        if (_circleField1.getSelectedItem().equals(_circleField2.getSelectedItem())) {
          showMessage("Выберите разные окружности", 1);
        } else {
          ArrayList<i_BodyBuilder> builders = new ArrayList<i_BodyBuilder>();
          HashMap<String, BuilderParam> params1 = new HashMap<String, BuilderParam>();
          params1.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(_ctrl.getEditor()) + "_1"));
          params1.put("c1", new BuilderParam("c1", "Первая окружность", BuilderParamType.BODY, _circleField1.getBody().id()));
          params1.put("c2", new BuilderParam("c2", "Вторая окружность", BuilderParamType.BODY, _circleField2.getBody().id()));
          params1.put("direction", new BuilderParam("direction", "Направление", BuilderParamType.BOOLEAN, true));
          builders.add(new OutTangentForTwoCirclesBuilder(params1));
          HashMap<String, BuilderParam> params2 = new HashMap<String, BuilderParam>();
          params2.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(_ctrl.getEditor()) + "_1"));
          params2.put("c1", new BuilderParam("c1", "Первая окружность", BuilderParamType.BODY, _circleField1.getBody().id()));
          params2.put("c2", new BuilderParam("c2", "Вторая окружность", BuilderParamType.BODY, _circleField2.getBody().id()));
          params2.put("direction", new BuilderParam("direction", "Направление", BuilderParamType.BOOLEAN, false));
          builders.add(new OutTangentForTwoCirclesBuilder(params2));
          _ctrl.addBodies(builders, _errorHandler, false);
          accepted = true;
          dispose();
        }
      }
    };
  }
}
