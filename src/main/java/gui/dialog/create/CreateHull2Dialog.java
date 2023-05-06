package gui.dialog.create;

import bodies.BodyType;
import builders.PointBuilderHull2;
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
 * Create a point as a linear combination of two points
 * Using PointBuilderHull2
 * @author alexeev
 */
public class CreateHull2Dialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointField1;
  private AnchorChooser _pointField2;
  private NumericTextField _factorField;

  /**
   *
   * @param ctrl
   */
  public CreateHull2Dialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить выпуклую комбинацию двух точек");
    _nameField = new NameTextField(BodyType.POINT.getName(_ctrl.getEditor()));
    _pointField1 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField2 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _factorField = new NumericTextField(0.5, false);

    addItem("Имя точки", _nameField);
    addItem("Вершина 1:", _pointField1);
    addItem("Вершина 2:", _pointField2);
    addItem("Множитель", _factorField);
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
        if ((_pointField1.getSelectedItem()).equals((_pointField2.getSelectedItem()))) {
          showMessage("Выберите различные точки", 1);
        } else {
          try {
            _ctrl.validateBodyTitle(_nameField.getText());
            double factor = _factorField.getNumericValue();
            HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
            params.put("name", new BuilderParam("name", "Имя точки", BuilderParamType.NAME, _nameField.getText()));
            params.put("A", new BuilderParam("A", "A", BuilderParamType.ANCHOR, (String)_pointField1.getAnchorID()));
            params.put("B", new BuilderParam("B", "B", BuilderParamType.ANCHOR, (String)_pointField2.getAnchorID()));
            params.put("alpha", new BuilderParam("alpha", "Коэффициент", BuilderParamType.DOUBLE, factor));
            _ctrl.add(new PointBuilderHull2(params), _errorHandler, false);
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
