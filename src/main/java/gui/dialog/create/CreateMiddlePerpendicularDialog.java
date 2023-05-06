package gui.dialog.create;

import bodies.BodyType;
import bodies.RibBody;
import bodies.TriangleBody;
import builders.AngleByThreePointsBuilder;
import builders.MiddlePerpendicularBuilder;
import static config.Config.LOG_LEVEL;
import editor.AnchorType;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_Anchor;
import editor.i_BodyBuilder;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import util.Log;


public class CreateMiddlePerpendicularDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointField;
  private String _triangleID;

  public CreateMiddlePerpendicularDialog(EdtController ctrl, i_ErrorHandler errorHandler, String triangleID)
    throws ExNoBody {
    super(ctrl, errorHandler, "Провести срединный перпендикуляр");
    _nameField = new NameTextField(BodyType.RIB.getName(_ctrl.getEditor()));
    _pointField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT, triangleID);
    _triangleID = triangleID;

    addItem("Имя отрезка", _nameField);
    addItem("Вершина", _pointField);
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
          String name = _nameField.getText();
          String pointID = _pointField.getAnchorID();

          _ctrl.validateBodyTitle(name);
          MiddlePerpendicularBuilder perp_builder = new MiddlePerpendicularBuilder(name);
          perp_builder.addTriangle(_triangleID);
          perp_builder.addVertex(pointID);
          _ctrl.add(perp_builder, _errorHandler, false);

          try {
            // просим у треугольника ID его вершин
            String[] vertexIDs = new String[]{
              _ctrl.getAnchorID(_triangleID, TriangleBody.BODY_KEY_A),
              _ctrl.getAnchorID(_triangleID, TriangleBody.BODY_KEY_B),
              _ctrl.getAnchorID(_triangleID, TriangleBody.BODY_KEY_C)};

            // находим точки срединного перпендикуляра
            i_Anchor perpAID = _ctrl.getAnchor(perp_builder.id(), RibBody.BODY_KEY_A);
            i_Anchor perpBID = _ctrl.getAnchor(perp_builder.id(), RibBody.BODY_KEY_B);

            ArrayList<i_BodyBuilder> builders = new ArrayList<i_BodyBuilder>();
            for( String vertexID: vertexIDs ){
              if( !vertexID.equals(pointID)){
                // создаём угол
                AngleByThreePointsBuilder builder = new AngleByThreePointsBuilder(
                    _ctrl.getAnchorTitle(vertexID) + _ctrl.getAnchorTitle(perpAID.id()) +
                            _ctrl.getAnchorTitle(perpBID.id()));
                builder.addPointOnFirstSide(vertexID);
                builder.addVertex(perpAID.id());
                builder.addPointOnSecondSide(perpBID.id());
                builder.addLessThanPiParam(true);
                builders.add(builder);
              }
            }
            // добавляем углы в редактор
            _ctrl.addBodies(builders, _errorHandler, false);
          } catch( ExNoAnchor | ExNoBody ex ){
            if (LOG_LEVEL.value() >= 2) {
              Log.out.println("Не удалось добавить углы при построении срединного перпендикуляра");
            }
          }

          accepted = true;
          dispose();
        } catch (InvalidBodyNameException ex) {
          showMessage(ex.getMessage(), 2);
        }
      }
    };
  }
}
