package gui.dialog.create;

import builders.RibBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * Create a rib
 * by two vertices
 * Using RibBuilder
 * @author alexeev
 */
public class CreateRibDialog extends CreateBodyDialog {
  private AnchorChooser _pointField1;
  private AnchorChooser _pointField2;

  public CreateRibDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Задать отрезок двумя вершинами");

    _pointField1 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField2 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("Вершина 1:", _pointField1);
    addItem("Вершина 2:", _pointField2);
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
          HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
          params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, (String)_pointField1.getSelectedItem() + (String)_pointField2.getSelectedItem()));
          params.put("A", new BuilderParam("A", "A", BuilderParamType.ANCHOR, (String)_pointField1.getAnchorID()));
          params.put("B", new BuilderParam("B", "B", BuilderParamType.ANCHOR, (String)_pointField2.getAnchorID()));
          _ctrl.add(new RibBuilder(params), _errorHandler, false);
          accepted = true;
          dispose();
        }
      }
    };
  }
}
