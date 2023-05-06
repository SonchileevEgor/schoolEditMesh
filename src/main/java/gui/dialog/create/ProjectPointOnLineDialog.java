package gui.dialog.create;

import bodies.BodyType;
import builders.PointOnLineProjectionBuilder;
import builders.PointOnRibProjectionBuilder;
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
 * Constructs a projection of the point onto the rib
 Using PointOnRibProjectionBuilder
 * @author alexeev
 */
public class ProjectPointOnLineDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private BodyChooserComboBox _ribField;
  private AnchorChooser _pointField;

  public ProjectPointOnLineDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Опустить перпендикуляр на прямую");
    _nameField = new NameTextField();

    ArrayList<BodyType> ribList = new ArrayList<BodyType>();
    ribList.add(BodyType.RIB);
    ribList.add(BodyType.LINE);

    _ribField = new BodyChooserComboBox(_ctrl.getEditor().bd(), ribList);
    _pointField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Имя проекции", _nameField);
    addItem("Прямая", _ribField);
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
          if (_ribField.getBody().type() == BodyType.RIB) {
            HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
            params.put("rib", new BuilderParam("rib", "Отрезок", BuilderParamType.BODY, _ribField.getBody().id()));
            params.put("point", new BuilderParam("point", "Точка", BuilderParamType.ANCHOR, _pointField.getAnchorID()));
            _ctrl.add(new PointOnRibProjectionBuilder(params), _errorHandler, false);
          } else if (_ribField.getBody().type() == BodyType.LINE){
            HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
            params.put("line", new BuilderParam("line", "Прямая", BuilderParamType.BODY, _ribField.getBody().id()));
            params.put("point", new BuilderParam("point", "Точка", BuilderParamType.ANCHOR, _pointField.getAnchorID()));
            _ctrl.add(new PointOnLineProjectionBuilder(params), _errorHandler, false);
          } else {
            showMessage("Выберите другое тело", 1);
            return;
          }

          //!! TODO: добавить прямые углы
          
          accepted = true;
          dispose();
        } catch (InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
