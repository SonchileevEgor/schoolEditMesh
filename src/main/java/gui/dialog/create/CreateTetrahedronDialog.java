package gui.dialog.create;

import bodies.BodyType;
import builders.TetrahedronBuilder;
import editor.AnchorType;
import editor.ExNoAnchor;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Create a tetrahedron by four vertices
 * Using TetrahedronBuilder
 * @author alexeev
 */
public class CreateTetrahedronDialog extends CreateBodyDialog {
  private AnchorChooser _pointField1;
  private AnchorChooser _pointField2;
  private AnchorChooser _pointField3;
  private AnchorChooser _pointField4;

  public CreateTetrahedronDialog(EdtController ctrl, i_ErrorHandler errorHandler) {
    super(ctrl, errorHandler, "Задать тетраэдр четырьмя вершинами");
    _pointField1 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField2 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField3 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);
    _pointField4 = new AnchorChooser(_ctrl, AnchorType.ANC_POINT);

    addItem("A", _pointField1);
    addItem("B", _pointField2);
    addItem("C", _pointField3);
    addItem("D", _pointField4);
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
        if (util.Util.hasDuplicates(new Object[]{
            _pointField1.getSelectedItem(),
            _pointField2.getSelectedItem(),
            _pointField3.getSelectedItem(),
            _pointField4.getSelectedItem()})){
          showMessage("Выберите различные точки", 1);
        } else {
          String name;
          try {
            name = _ctrl.getAnchor(_pointField1.getAnchorID()).getTitle() +
                   _ctrl.getAnchor(_pointField2.getAnchorID()).getTitle() +
                   _ctrl.getAnchor(_pointField3.getAnchorID()).getTitle() +
                   _ctrl.getAnchor(_pointField4.getAnchorID()).getTitle();
          }
          catch (ExNoAnchor ex) {
            name = BodyType.TETRAHEDRON.getName(_ctrl.getEditor());
          }
          TetrahedronBuilder builder = new TetrahedronBuilder(name);
          builder.addA((String)_pointField1.getAnchorID());
          builder.addB((String)_pointField2.getAnchorID());
          builder.addC((String)_pointField3.getAnchorID());
          builder.addD((String)_pointField4.getAnchorID());
          _ctrl.add(builder, _errorHandler, false);
          accepted = true;
          dispose();
        }
      }
    };
  }
}
