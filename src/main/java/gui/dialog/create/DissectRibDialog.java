package gui.dialog.create;

import bodies.BodyType;
import bodies.RibBody;
import builders.PointBuilderHull2;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_BodyContainer;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.BodyChooserComboBox;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * @author alexeev
 */
public class DissectRibDialog extends CreateBodyDialog {
  private i_BodyContainer _bodies;
  private NameTextField _nameField;
  private BodyChooserComboBox _bodyField;

  public DissectRibDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Отметить середину отрезка");
    _bodies = _ctrl.getEditor().bd();
    _nameField = new NameTextField(BodyType.POINT.getName(_ctrl.getEditor()));

    ArrayList<BodyType> ribList = new ArrayList<BodyType>();
    ribList.add(BodyType.RIB);

    _bodyField = new BodyChooserComboBox(_ctrl.getEditor().bd(), ribList);

    addItem("Имя точки", _nameField);
    addItem("Отрезок", _bodyField);
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
          String bodyID = _bodyField.getBody().id();
          RibBody rib = (RibBody)_bodies.get(bodyID);
          HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
          params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
          params.put("A", new BuilderParam("A", "A", BuilderParamType.ANCHOR, rib.getAnchors().get("A")));
          params.put("B", new BuilderParam("B", "B", BuilderParamType.ANCHOR, rib.getAnchors().get("B")));
          params.put("alpha", new BuilderParam("alpha", "Коэффициент", BuilderParamType.DOUBLE, 0.5));
          _ctrl.add(new PointBuilderHull2(params), _errorHandler, false);
          accepted = true;
          dispose();
        } catch (ExNoBody | InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
