package gui.dialog.create;

import bodies.BodyType;
import builders.PrismBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
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
 * Create prism by amount of vertices of the base polygon,
 * the list of vertices of base polygon
 * and vertex of another base,
 * which is correspondent to the first vertex of the list
 * Using PrismBuilder
 * @author alexeev
 */
public class CreatePrismDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _topField;
  private JSpinner _vertQtySpinner;
  private AnchorListChooser _anchors;

  public CreatePrismDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Задать призму точками основания и вершиной");
    _nameField = new NameTextField(BodyType.PRISM.getName(_ctrl.getEditor()));
    _topField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _vertQtySpinner = new JSpinner(new SpinnerNumberModel(3, 3, 20, 1));
    _vertQtySpinner.setPreferredSize(new Dimension(73, 25));
    ((JSpinner.DefaultEditor)_vertQtySpinner.getEditor()).getTextField().setEditable(true);
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
    _anchors = new AnchorListChooser(_ctrl, AnchorType.ANC_POINT,  3);

    addItem("Имя призмы", _nameField);
    addItem("Вершина", _topField);
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
            anch.add((String)_topField.getAnchorID());
            params.put("points", new BuilderParam("points", "Список вершин", BuilderParamType.ANCHOR_LIST, anch));
            _ctrl.add(new PrismBuilder(params), _errorHandler, false);
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
