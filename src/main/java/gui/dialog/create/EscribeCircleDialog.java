package gui.dialog.create;

import bodies.BodyType;
import builders.ExCircleBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 *
 * @author alexeev
 */
public class EscribeCircleDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointField;
  private String _triangleID;

  public EscribeCircleDialog(EdtController ctrl, i_ErrorHandler errorHandler, String triangleID)
    throws ExNoBody {
    super(ctrl, errorHandler, "Вневписать окружность");
    _nameField = new NameTextField(BodyType.CIRCLE.getName(_ctrl.getEditor()));
    _pointField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT, triangleID);
    _triangleID = triangleID;

    addItem("Имя окружности", _nameField);
    addItem("Вершина", _pointField);
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
          HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
          params.put("name", new BuilderParam("name", "Имя высоты", BuilderParamType.NAME, _nameField.getText()));
          params.put("triangle", new BuilderParam("triangle", "Треугольник", BuilderParamType.BODY, _triangleID));
          params.put("vertex", new BuilderParam("vertex", "Вершина", BuilderParamType.ANCHOR, (String)_pointField.getAnchorID()));
          _ctrl.add(new ExCircleBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        } catch (InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
