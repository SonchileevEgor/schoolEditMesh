package gui.dialog.create;

import bodies.BodyType;
import builders.LineXRibBuilder;
import builders.RibXPlaneBuilder;
import builders.RibXPolyBuilder;
import builders.RibXRibBuilder;
import editor.AnchorType;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.ExNoAnchor;
import editor.ExNoBody;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.EntityChooserComboBox;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;

/**
 * @author alexeev
 */
public class RibSectionDialog extends CreateBodyDialog {
  private AnchorChooser _ribField;
  private EntityChooserComboBox _entityField;

  public RibSectionDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить пересечение отрезка");
    _ribField = new AnchorChooser(_ctrl, AnchorType.ANC_RIB);
    _entityField = new EntityChooserComboBox(ctrl.getEditor(), false, true, true, BodyType.PLANE, BodyType.LINE);

    addItem("Отрезок", _ribField);
    addItem("С чем пересечь", _entityField);
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
          if (_entityField.getSelectedID().equals(_ribField.getAnchorID())) {
            showMessage("Выберите разные объекты для пересечения", 1);
          } else {
            HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
            params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(_ctrl.getEditor())));
            if (_entityField.isBody()) {
              BodyType type = _ctrl.getBody(_entityField.getSelectedID()).type();
              if (type == BodyType.PLANE) {
                params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, _entityField.getSelectedID()));
                params.put("rib", new BuilderParam("rib", "Отрезок", BuilderParamType.ANCHOR, _ribField.getAnchorID()));
                _ctrl.add(new RibXPlaneBuilder(params), _errorHandler, false);
              } else if (type == BodyType.LINE) {
                params.put("rib", new BuilderParam("rib", "Отрезок", BuilderParamType.ANCHOR, _ribField.getAnchorID()));
                params.put("line", new BuilderParam("line", "Прямая", BuilderParamType.BODY, _entityField.getSelectedID()));
                _ctrl.add(new LineXRibBuilder(params), _errorHandler, false);
              }
            } else {
              AnchorType type = _ctrl.getAnchor(_entityField.getSelectedID()).getAnchorType();
              if (type == AnchorType.ANC_RIB) {
                params.put("r1", new BuilderParam("r1", "Отрезок", BuilderParamType.ANCHOR, _ribField.getAnchorID()));
                params.put("r2", new BuilderParam("r2", "Отрезок", BuilderParamType.ANCHOR, _entityField.getSelectedID()));
                _ctrl.add(new RibXRibBuilder(params), _errorHandler, false);
              } else if (type == AnchorType.ANC_POLY) {
                params.put("rib", new BuilderParam("rib", "Отрезок", BuilderParamType.ANCHOR, _ribField.getAnchorID()));
                params.put("poly", new BuilderParam("poly", "Грань", BuilderParamType.ANCHOR, _entityField.getSelectedID()));
                _ctrl.add(new RibXPolyBuilder(params), _errorHandler, false);
              }
            }
            accepted = true;
            dispose();
          }
        } catch (ExNoAnchor | ExNoBody ex) {
          showMessage("Это тело нельзя пересечь", 1);
        }
      }
    };
  }
}
