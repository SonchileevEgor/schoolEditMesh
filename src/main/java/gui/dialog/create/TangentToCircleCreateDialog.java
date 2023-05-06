package gui.dialog.create;

import bodies.BodyType;
import builders.TangentFromPointToCircleBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.BodyChooserComboBox;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Constructs two tangent lines to circle.
 *
 * @author alexeev
 */
public class TangentToCircleCreateDialog extends CreateBodyDialog {
  private BodyChooserComboBox _circleField;
  private AnchorChooser _pointField;

  public TangentToCircleCreateDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Провести касательные к окружности");
    ArrayList<BodyType> circleList = new ArrayList<BodyType>();
    circleList.add(BodyType.CIRCLE);

    _circleField = new BodyChooserComboBox(_ctrl.getEditor().bd(), circleList);
    _pointField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Окружность", _circleField);
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
        HashMap<String, BuilderParam> params1 = new HashMap<String, BuilderParam>();
        params1.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(_ctrl.getEditor()) + "_1"));
        params1.put("circle", new BuilderParam("circle", "Окружность", BuilderParamType.BODY, _circleField.getBody().id()));
        params1.put("point", new BuilderParam("point", "Точка", BuilderParamType.ANCHOR, _pointField.getAnchorID()));
        params1.put("direction", new BuilderParam("direction", "Направление", BuilderParamType.BOOLEAN, true));
        _ctrl.add(new TangentFromPointToCircleBuilder(params1), _errorHandler, false);
        HashMap<String, BuilderParam> params2 = new HashMap<String, BuilderParam>();
        params2.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(_ctrl.getEditor()) + "_2"));
        params2.put("circle", new BuilderParam("circle", "Окружность", BuilderParamType.BODY, _circleField.getBody().id()));
        params2.put("point", new BuilderParam("point", "Точка", BuilderParamType.ANCHOR, _pointField.getAnchorID()));
        params2.put("direction", new BuilderParam("direction", "Направление", BuilderParamType.BOOLEAN, false));
        _ctrl.add(new TangentFromPointToCircleBuilder(params2), _errorHandler, false);
        accepted = true;
        dispose();
      }
    };
  }
}
