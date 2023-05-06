package gui.dialog.create;

import bodies.BodyType;
import builders.PrismByBaseAndTopBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.InvalidBodyNameException;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Create a prism
 * by the base polygon and
 * vertex of the another base,
 * correspondent to the first vertex of base polygon
 * Using PrismByBaseAndVertexBuilder
 * @author alexeev
 */
public class CreatePrismByBaseAndVertexDialog extends CreateBodyDialog {
  private NameTextField _nameField; //name
  private AnchorChooser _baseField;
  private AnchorChooser _topField;  //top vertex

  public CreatePrismByBaseAndVertexDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Задать призму основанием и вершиной");
    _nameField = new NameTextField(BodyType.PRISM.getName(_ctrl.getEditor()));

    _baseField = new AnchorChooser(_ctrl, AnchorType.ANC_POLY);
    _topField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Имя призмы", _nameField);
    addItem("Основание", _baseField);
    addItem("Вершина", _topField);
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
          params.put("base", new BuilderParam("base", "Основание", BuilderParamType.ANCHOR, _baseField.getAnchorID()));
          params.put("top", new BuilderParam("top", "Вершина", BuilderParamType.ANCHOR, _topField.getAnchorID()));
          _ctrl.add(new PrismByBaseAndTopBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        } catch (InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
