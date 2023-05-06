package gui.dialog.create;

import gui.elements.SidesSizeType;
import bodies.BodyType;
import bodies.RibBody;
import bodies.TriangleBody;
import builders.AngleByThreePointsBuilder;
import builders.BisectrixBuilder;
import static config.Config.LOG_LEVEL;
import editor.AnchorType;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.InvalidBodyNameException;
import editor.i_Anchor;
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

/**
 * Диалог построения биссектрисы треугольника.
 * @author alexeev
 */
public class CreateBisectrixDialog extends CreateBodyDialog {
  private NameTextField _nameField;
  private AnchorChooser _pointField;
  private String _triangleID;

  public CreateBisectrixDialog(EdtController ctrl, i_ErrorHandler errorHandler, String triangleID)
    throws ExNoBody {
    super(ctrl, errorHandler, "Провести биссектрису");
    _nameField = new NameTextField();
    _pointField = new AnchorChooser(_ctrl, AnchorType.ANC_POINT, triangleID);
    _nameField.setText(BodyType.POINT.getName(_ctrl.getEditor()));
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
          String name = _nameField.getText();
          String pointID = _pointField.getAnchorID();

          _ctrl.validateBodyTitle(name);
          BisectrixBuilder bisectrix_builder = new BisectrixBuilder(name);
          bisectrix_builder.addTriangle(_triangleID);
          bisectrix_builder.addVertex(pointID);
          _ctrl.add(bisectrix_builder, _errorHandler, false);

          try {
            // просим у треугольника ID его вершин
            String[] vertexIDs = new String[]{
              _ctrl.getAnchorID(_triangleID, TriangleBody.BODY_KEY_A),
              _ctrl.getAnchorID(_triangleID, TriangleBody.BODY_KEY_B),
              _ctrl.getAnchorID(_triangleID, TriangleBody.BODY_KEY_C)};

            // находим основание биссектрисы
            i_Anchor bisectrixBase = _ctrl.getAnchor(bisectrix_builder.id(), RibBody.BODY_KEY_B);

            ArrayList<i_BodyBuilder> builders = new ArrayList<i_BodyBuilder>();
            for( String vertexID: vertexIDs ){
              if( !vertexID.equals(pointID)){
                // создаём угол
                AngleByThreePointsBuilder builder = new AngleByThreePointsBuilder(
                    _ctrl.getAnchorTitle(vertexID) + _ctrl.getAnchorTitle(pointID) +
                            _ctrl.getAnchorTitle(bisectrixBase.id()));
                builder.addPointOnFirstSide(vertexID);
                builder.addVertex(pointID);
                builder.addPointOnSecondSide(bisectrixBase.id());
                builder.addLessThanPiParam(true);
                builders.add(builder);
              }
            }
            // добавляем углы в редактор
            _ctrl.addBodies(builders, _errorHandler, false);

            // устанавливаем параметр углов
            for( i_BodyBuilder builder : builders ){
              _ctrl.getBody(builder.id()).getState().setParam(
                      DisplayParam.SIDES_SIZE_TYPE, SidesSizeType.SHORT_SHORT);
            }
          } catch( ExNoAnchor | ExNoBody ex ){
            if (LOG_LEVEL.value() >= 2) {
              Log.out.println("Не удалось добавить углы при построении биссектрисы");
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
