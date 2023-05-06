package gui.dialog.create;

import bodies.BodyType;
import builders.PrismRegularBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.BodyChooserComboBox;
import gui.elements.IntegerTextField;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Create regular prism
 * by two consequent vertices,
 * plane which contains these vertices
 * vertex of another base
 * and number of vertices
 * Using PrismRegularBuilder
 * @author alexeev
 */
public class CreateRegularPrismDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _topField;
  private BodyChooserComboBox _planeField;
  private AnchorChooser _pointField1;
  private AnchorChooser _pointField2;
  private IntegerTextField _vertexQtyField;

  public CreateRegularPrismDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить правильную призму");
    _nameField = new NameTextField(BodyType.PRISM.getName(_ctrl.getEditor()));
    _topField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    ArrayList<BodyType> planeList = new ArrayList<BodyType>();
    planeList.add(BodyType.PLANE);

    _planeField = new BodyChooserComboBox(_ctrl.getEditor().bd(), planeList);
    _pointField1 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField2 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _vertexQtyField = new IntegerTextField(3, true);

    addItem("Имя призмы", _nameField);
    addItem("Точка на втором основании", _topField);
    addItem("Плоскость", _planeField);
    addItem("1-я вершина основания", _pointField1);
    addItem("2-я вершина основания", _pointField2);
    addItem("Кол-во вершин основания", _vertexQtyField);
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
          if (!(_vertexQtyField.verify()) ||
              (_vertexQtyField.getNumericValue() < 3) ||
              (_vertexQtyField.getNumericValue() > 20)) {
            showMessage("Введено недопустимое количество вершин", 1);
          } else if (_pointField1.getSelectedItem().equals(_pointField2.getSelectedItem())) {
            showMessage("Выберите различные точки", 1);
          } else {
            _ctrl.validateBodyTitle(_nameField.getText());
            HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
            params.put("top", new BuilderParam("top", "Вершина", BuilderParamType.ANCHOR, _topField.getAnchorID()));
            params.put("plane", new BuilderParam("plane", "Плоскость основания", BuilderParamType.BODY, _planeField.getBody().id()));
            params.put("A", new BuilderParam("A", "A", BuilderParamType.ANCHOR, _pointField1.getAnchorID()));
            params.put("B", new BuilderParam("B", "B", BuilderParamType.ANCHOR, _pointField2.getAnchorID()));
            params.put("num", new BuilderParam("num", "Количество вершин", BuilderParamType.INT, (int)_vertexQtyField.getNumericValue()));
            _ctrl.add(new PrismRegularBuilder(params), _errorHandler, false);
            accepted = true;
            dispose();
          }
        } catch (ParseException | InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}

