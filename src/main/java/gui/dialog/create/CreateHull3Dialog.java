package gui.dialog.create;

import bodies.BodyType;
import builders.PointBuilderHull3;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.NameTextField;
import gui.elements.NumericTextField;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Create a point as a linear combination of three points
 * Using PointBuilderHull3
 * @author alexeev
 */
public class CreateHull3Dialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointField1;
  private AnchorChooser _pointField2;
  private AnchorChooser _pointField3;
  private NumericTextField _firstFactorField;
  private NumericTextField _secondFactorField;

  public CreateHull3Dialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить выпуклую комбинацию трёх точек");
    _nameField = new NameTextField(BodyType.POINT.getName(_ctrl.getEditor()));
    _pointField1 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField2 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField3 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _firstFactorField = new NumericTextField(1.0 / 3, false);
    _secondFactorField = new NumericTextField(1.0 / 3, false);

    addItem("Имя точки", _nameField);
    addItem("Точка 1:", _pointField1);
    addItem("Точка 2:", _pointField2);
    addItem("Точка 3:", _pointField3);
    addItem("Множитель 1:", _firstFactorField);
    addItem("Множитель 2:", _secondFactorField);
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

        if ((_pointField1.getSelectedItem()).equals((_pointField2.getSelectedItem())) ||
            (_pointField1.getSelectedItem()).equals((_pointField3.getSelectedItem())) ||
            (_pointField2.getSelectedItem()).equals((_pointField3.getSelectedItem()))) {
          showMessage("Выберите различные точки", 1);
        } else {
          try {
            _ctrl.validateBodyTitle(_nameField.getText());
            double factor1 = _firstFactorField.getNumericValue();
            double factor2 = _secondFactorField.getNumericValue();
            HashMap<String, BuilderParam> params = new HashMap<>();
            params.put("name", new BuilderParam("name", "Имя точки", BuilderParamType.NAME, _nameField.getText()));
            params.put("A", new BuilderParam("A", "A", BuilderParamType.ANCHOR, (String)_pointField1.getAnchorID()));
            params.put("B", new BuilderParam("B", "B", BuilderParamType.ANCHOR, (String)_pointField2.getAnchorID()));
            params.put("C", new BuilderParam("C", "C", BuilderParamType.ANCHOR, (String)_pointField3.getAnchorID()));
            params.put("alpha", new BuilderParam("alpha", "1-й коэффициент", BuilderParamType.DOUBLE, factor1));
            params.put("beta", new BuilderParam("beta", "2-й коэффициент", BuilderParamType.DOUBLE, factor2));
            _ctrl.add(new PointBuilderHull3(params), _errorHandler, false);
            accepted = true;
            dispose();
          } catch (ParseException | InvalidBodyNameException ex) {
            showMessage(ex.getMessage(), 2);
          }
        }
      }
    };
  }
}
