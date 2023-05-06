package gui.dialog.create;

import bodies.BodyType;
import builders.LineOrthogonalPlaneBuilder;
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
public class CreateLineOrthogonalPlaneDialog extends CreateBodyDialog {
  /**
   *
   * @param ctrl
   */
  public CreateLineOrthogonalPlaneDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Провести прямую перпендикулярно плоскости");

    ArrayList<BodyType> planeList = new ArrayList<BodyType>();
    planeList.add(BodyType.PLANE);

    _planeField = new BodyChooserComboBox(_ctrl.getEditor().bd(), planeList);
    _pointField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Плоскость", _planeField);
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
        params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, _planeField.getBody().id()));
        params.put("P", new BuilderParam("P", "Точка", BuilderParamType.ANCHOR, _pointField.getAnchorID()));
        _ctrl.add(new LineOrthogonalPlaneBuilder(params), _errorHandler, false);

        //!! TODO: добавить прямые углы

        accepted = true;
        dispose();
      }
    };
  }

  private BodyChooserComboBox _planeField;
  private AnchorChooser _pointField;
}
