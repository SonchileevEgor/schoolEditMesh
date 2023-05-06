package gui.dialog.create;

import bodies.BodyType;
import builders.LineXPolyBuilder;
import builders.PolyXPlaneBuilder;
import builders.PolyXPolyBuilder;
import builders.RibXPolyBuilder;
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
 *
 * @author alexeev
 */
public class PolySectionDialog extends CreateBodyDialog {
  private AnchorChooser _polyField;
  private EntityChooserComboBox _entityField;

  public PolySectionDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Построить пересечение грани");
    _polyField = new AnchorChooser(_ctrl, AnchorType.ANC_POLY);
    _entityField = new EntityChooserComboBox(ctrl.getEditor(), false, true, true, BodyType.PLANE, BodyType.LINE);

    addItem("Грань", _polyField);
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
          if (_entityField.getSelectedID().equals(_polyField.getAnchorID())) {
            showMessage("Выберите разные объекты для пересечения", 1);
          } else {
            HashMap<String, BuilderParam> params = new HashMap<String, BuilderParam>();
            if (_entityField.isBody()) {
              BodyType type = _ctrl.getBody(_entityField.getSelectedID()).type();
              if (type == BodyType.PLANE) {
                params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.RIB.getName(_ctrl.getEditor())));
                params.put("plane", new BuilderParam("plane", "Плоскость", BuilderParamType.BODY, _entityField.getSelectedID()));
                params.put("poly", new BuilderParam("poly", "Грань", BuilderParamType.ANCHOR, _polyField.getAnchorID()));
                _ctrl.add(new PolyXPlaneBuilder(params), _errorHandler, false);
              } else if (type == BodyType.LINE) {
                params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(_ctrl.getEditor())));
                params.put("poly", new BuilderParam("poly", "Грань", BuilderParamType.ANCHOR, _polyField.getAnchorID()));
                params.put("line", new BuilderParam("line", "Прямая", BuilderParamType.BODY, _entityField.getSelectedID()));
                _ctrl.add(new LineXPolyBuilder(params), _errorHandler, false);
              }
            } else {
              AnchorType type = _ctrl.getAnchor(_entityField.getSelectedID()).getAnchorType();
              if (type == AnchorType.ANC_RIB) {
                params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.POINT.getName(_ctrl.getEditor())));
                params.put("poly", new BuilderParam("poly", "Грань", BuilderParamType.ANCHOR, _polyField.getAnchorID()));
                params.put("rib", new BuilderParam("rib", "Отрезок", BuilderParamType.ANCHOR, _entityField.getSelectedID()));
                _ctrl.add(new RibXPolyBuilder(params), _errorHandler, false);
              } else if (type == AnchorType.ANC_POLY) {
                params.put("name", new BuilderParam("name", "Имя", BuilderParamType.NAME, BodyType.RIB.getName(_ctrl.getEditor())));
                params.put("poly1", new BuilderParam("poly1", "Грань", BuilderParamType.ANCHOR, _polyField.getAnchorID()));
                params.put("poly2", new BuilderParam("poly2", "Грань", BuilderParamType.ANCHOR, _entityField.getSelectedID()));
                _ctrl.add(new PolyXPolyBuilder(params), _errorHandler, false);
              }
            }
            accepted = true;
            dispose();
          }
        } catch (ExNoAnchor | ExNoBody ex) {
          util.Fatal.warning("Ошибка пересечения");
        }
      }
    };
  }
}
