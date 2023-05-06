package gui.dialog.create;

import gui.elements.SidesSizeType;
import bodies.BodyType;
import bodies.RibBody;
import bodies.TriangleBody;
import builders.AngleByThreePointsBuilder;
import builders.HeightBuilder;
import static config.Config.LOG_LEVEL;
import editor.AnchorType;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_Anchor;
import editor.i_Body;
import editor.i_BodyBuilder;
import editor.state.DisplayParam;
import error.i_ErrorHandler;
import gui.EdtController;
import gui.elements.AnchorChooser;
import gui.elements.NameTextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import util.Log;


public class CreateHeightDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointField;
  private String _triangleID;

  public CreateHeightDialog(EdtController ctrl, i_ErrorHandler errorHandler, String triangleID)
    throws ExNoBody {
    super(ctrl, errorHandler, "Провести высоту");
    _nameField = new NameTextField(BodyType.POINT.getName(_ctrl.getEditor()));
    _pointField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT, triangleID);
    _triangleID = triangleID;

    addItem("Имя основания", _nameField);
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
          _ctrl.validateBodyTitle(_nameField.getText());
          String pointID = _pointField.getAnchorID();

          HeightBuilder height_builder = new HeightBuilder(_nameField.getText());
          height_builder.addTriangle(_triangleID);
          height_builder.addVertex(pointID);
          _ctrl.add(height_builder, _errorHandler, false);

          try {
            // просим у треугольника ID его вершин
            String[] vertexIDs = new String[]{
              _ctrl.getAnchorID(_triangleID, TriangleBody.BODY_KEY_A),
              _ctrl.getAnchorID(_triangleID, TriangleBody.BODY_KEY_B),
              _ctrl.getAnchorID(_triangleID, TriangleBody.BODY_KEY_C)};

            // находим основание биссектрисы
            i_Anchor altitudeBase = _ctrl.getAnchor(height_builder.id(), RibBody.BODY_KEY_B);

            ArrayList<i_BodyBuilder> builders = new ArrayList<i_BodyBuilder>();
            for( String vertexID: vertexIDs ){
              if( !vertexID.equals(pointID)){
                // создаём угол
                AngleByThreePointsBuilder builder = new AngleByThreePointsBuilder(
                        _ctrl.getAnchorTitle(pointID) + _ctrl.getAnchorTitle(altitudeBase.id()) +
                                _ctrl.getAnchorTitle(vertexID));
                builder.addPointOnFirstSide(pointID);
                builder.addVertex(altitudeBase.id());
                builder.addPointOnSecondSide(vertexID);
                builder.addLessThanPiParam(true);
                builders.add(builder);
              }
            }
            // добавляем углы в редактор
            _ctrl.addBodies(builders, _errorHandler, false);

            // устанавливаем параметр углов
            for( i_BodyBuilder builder : builders ){
              i_Body result = _ctrl.getBody(builder.id());
              result.getState().setParam(DisplayParam.SIDES_SIZE_TYPE, SidesSizeType.SHORT_LONG);
            }
          } catch( ExNoAnchor | ExNoBody ex ){
            if (LOG_LEVEL.value() >= 2) {
              Log.out.println("Не удалось добавить углы при построении высоты");
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
