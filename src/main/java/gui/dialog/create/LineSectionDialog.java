package gui.dialog.create;

import bodies.BodyType;
import builders.LineXConeBuilder;
import builders.LineXCylinderBuilder;
import builders.LineXLineBuilder;
import builders.LineXPlaneBuilder;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import editor.i_Body;
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
 * @author rita
 */
public class LineSectionDialog extends CreateBodyDialog {
  private i_BodyContainer _bodies;
  private NameTextField _nameField;
  private BodyChooserComboBox _bodyField;
  private BodyChooserComboBox _lineField;

  public LineSectionDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Провести сечение объекта прямой");
    _nameField = new NameTextField(BodyType.POINT.getName(_ctrl.getEditor()));
    _bodies = ctrl.getBodyContainer();

    //types which allow to be sectioned
    ArrayList<BodyType> allowedTypes = new ArrayList<BodyType>();
    allowedTypes.add(BodyType.CYLINDER);
    allowedTypes.add(BodyType.PLANE);
    allowedTypes.add(BodyType.CONE);
    allowedTypes.add(BodyType.LINE);
    allowedTypes.add(BodyType.RIB);

    _bodyField = new BodyChooserComboBox(_bodies, allowedTypes);

    ArrayList<BodyType> lineList = new ArrayList<BodyType>();
    lineList.add(BodyType.LINE);

    _lineField = new BodyChooserComboBox(_bodies, lineList);

    addItem("Имя сечения", _nameField);
    addItem("Тело", _bodyField);
    addItem("Секущая прямая", _lineField);
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
          params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
          i_Body body = _bodyField.getBody();
          switch (body.type()) {
            case CYLINDER:
              params.put("cylinder", new BuilderParam("cylinder", "Цилиндр", BuilderParamType.BODY, _bodyField.getBody().id()));
              params.put("line", new BuilderParam("line", "Секущая прямая", BuilderParamType.BODY, _lineField.getBody().id()));
              _ctrl.add(new LineXCylinderBuilder(params), _errorHandler, false);
              break;
            case PLANE:
              params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, _bodyField.getBody().id()));
              params.put("line", new BuilderParam("line", "Секущая прямая", BuilderParamType.BODY, _lineField.getBody().id()));
              _ctrl.add(new LineXPlaneBuilder(params), _errorHandler, false);
              break;
            case CONE:
              params.put("cone", new BuilderParam("cone", "Конус", BuilderParamType.BODY, _bodyField.getBody().id()));
              params.put("line", new BuilderParam("line", "Секущая прямая", BuilderParamType.BODY, _lineField.getBody().id()));
              _ctrl.add(new LineXConeBuilder(params), _errorHandler, false);
              break;
            case LINE:
              LineXLineBuilder builder = new LineXLineBuilder(params);
              builder.addLine1(_bodyField.getBody().id());
              builder.addLine2(_lineField.getBody().id());
              _ctrl.add(builder, _errorHandler, false);
              break;
            default:
              showMessage("Это тело не может быть пересечено", 1);
              return;
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