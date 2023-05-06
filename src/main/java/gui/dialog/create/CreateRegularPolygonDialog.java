package gui.dialog.create;

import bodies.BodyType;
import builders.RegularPolygonBuilder;
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
 * Create regular polygon
 * by two consequent vertices,
 * plane which contains these vertices
 * and number of vertices
 * Using RegularPolygonBuilder
 * @author alexeev
 */
public class CreateRegularPolygonDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private BodyChooserComboBox _planeField;
  private AnchorChooser _pointField1;
  private AnchorChooser _pointField2;
  private IntegerTextField _vertexQtyField;

  public CreateRegularPolygonDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить правильный многоугольник");
    _nameField = new NameTextField(BodyType.POLYGON.getName(_ctrl.getEditor()));

    ArrayList<BodyType> planeList = new ArrayList<BodyType>();
    planeList.add(BodyType.PLANE);

    _planeField = new BodyChooserComboBox(_ctrl.getEditor().bd(), planeList);
    _pointField1 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField2 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _vertexQtyField = new IntegerTextField(3, true);

    addItem("Имя многоугольника", _nameField);
    addItem("Плоскость", _planeField);
    addItem("Первая вершина", _pointField1);
    addItem("Вторая вершина", _pointField2);
    addItem("Количество вершин", _vertexQtyField);
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
            params.put("plane", new BuilderParam("plane", "Плоскость основания", BuilderParamType.BODY, _planeField.getBody().id()));
            params.put("A", new BuilderParam("A", "A", BuilderParamType.ANCHOR, _pointField1.getAnchorID()));
            params.put("B", new BuilderParam("B", "B", BuilderParamType.ANCHOR, _pointField2.getAnchorID()));
            params.put("num", new BuilderParam("num", "Количество вершин", BuilderParamType.INT, (int)_vertexQtyField.getNumericValue()));
            _ctrl.add(new RegularPolygonBuilder(params), _errorHandler, false);
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
