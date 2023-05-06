package gui.dialog.create;

import bodies.BodyType;
import builders.LineOrthogonalLineBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.BodyChooserComboBox;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 *
 * @author alexeev
 */
public class CreateLineOrthogonalLineDialog extends CreateBodyDialog {
  /**
   *
   * @param ctrl
   */
  public CreateLineOrthogonalLineDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Провести прямую перпендикулярно другой прямой");

    ArrayList<BodyType> lineList = new ArrayList<BodyType>();
    lineList.add(BodyType.LINE);

    _lineField = new BodyChooserComboBox(_ctrl.getEditor().bd(), lineList);
    _pointField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Прямая", _lineField);
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
        HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
        params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.LINE.getName(_ctrl.getEditor())));
        params.put("line", new BuilderParam("line", "Прямая", BuilderParamType.BODY, _lineField.getBody().id()));
        params.put("P", new BuilderParam("P", "Точка", BuilderParamType.ANCHOR, _pointField.getAnchorID()));
        _ctrl.add(new LineOrthogonalLineBuilder(params), _errorHandler, false);
        accepted = true;
        dispose();
      }
    };
  }

  private BodyChooserComboBox _lineField;
  private AnchorChooser _pointField;
}
