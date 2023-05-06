package gui.dialog.create;

import bodies.BodyType;
import builders.PolygonBuilder;
import builders.TriangleBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorListChooser;
import gui.elements.NameTextField;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Create polygon by amount of vertices and the list of vertices
 * Using PolygonBuilder
 * @author alexeev
 */
public class CreatePolygonDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private JSpinner _vertQtySpinner;
  private AnchorListChooser _anchors;

  public CreatePolygonDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Задать многоугольник набором вершин");

    _vertQtySpinner = new JSpinner(new SpinnerNumberModel(3, 3, 20, 1));
    _vertQtySpinner.setPreferredSize(new Dimension(73, 25));
    ((JSpinner.DefaultEditor)_vertQtySpinner.getEditor()).getTextField().setEditable(true);
    _anchors = new AnchorListChooser(_ctrl, AnchorType.ANC_POINT,  3);
    _nameField = new NameTextField(BodyType.POLYGON.getName(_ctrl.getEditor()));
    _vertQtySpinner.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent ce) {
        int newLength = (int)_vertQtySpinner.getValue();
        int oldLength = _anchors.length();
        if (newLength > oldLength) {
          for (int j = 0; j < newLength - oldLength; j++) {
            _anchors.inc();
            pack();
            setCenterLocation();
          }
        } else if (newLength < oldLength) {
          for (int j = 0; j < oldLength - newLength; j++) {
            _anchors.dec();
            pack();
            setCenterLocation();
          }
        }
      }
    });

    addItem("Имя полигона", _nameField);
    addItem("Количество вершин", _vertQtySpinner);
    addItem(_anchors, "center");
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
        if (_anchors.hasDuplication()) {
          showMessage("Выберите различные точки", 1);
        } else {
          try {
            _ctrl.validateBodyTitle(_nameField.getText());
            HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, _nameField.getText()));
            ArrayList<String> anch = new ArrayList<String>();
            for (int j = 0; j < _anchors.length(); j++){
              anch.add(_anchors.getAnchorID(j));
            }
            // check number of vertices
            if (_anchors.length() == 3) {
              // create triangle
              params.put("A", new BuilderParam("A", "A", BuilderParamType.ANCHOR, anch.get(0)));
              params.put("B", new BuilderParam("B", "B", BuilderParamType.ANCHOR, anch.get(1)));
              params.put("C", new BuilderParam("C", "C", BuilderParamType.ANCHOR, anch.get(2)));
              _ctrl.add(new TriangleBuilder(params), _errorHandler, false);
            } else {
              params.put("points", new BuilderParam("points", "Список вершин", BuilderParamType.ANCHOR_LIST, anch));
              _ctrl.add(new PolygonBuilder(params), _errorHandler, false);
            }
            accepted = true;
            dispose();
          } catch (InvalidBodyNameException ex) {
            showMessage(ex.getMessage(), 2);
          }
        }
      }
    };
  }
}
