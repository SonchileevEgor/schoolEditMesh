package anchors;

import editor.state.AnchorState;
import editor.AnchorType;
import editor.i_Anchor;
import geom.Circle3d;
import geom.Polygon3d;
import geom.Rib3d;
import geom.Vect3d;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexeev
 */
abstract public class Anchor implements i_Anchor {
  protected AnchorState _state;
  protected StringBuilder _title;
  protected String _id;
  protected String _bodyID;
  protected AnchorType _type;
  protected ArrayList<String> _pointIDs;

  /**
   * Якоря создаются в классе Editor!
   * Создать якорь.
   * @param anchorID
   *    ID якоря
   * @param bodyID
   *    ID тела
   * @param type
   *    Тип якоря
   * @param anchorName
   *    Имя якоря
   * @param state
   *    Привязанное состояние
   */
  Anchor(String anchorID, String bodyID, AnchorType type, StringBuilder anchorName, AnchorState state){
    _id = anchorID;
    _bodyID = bodyID;
    _type = type;
    _title = anchorName;
    _state = state;
    _pointIDs = new ArrayList<>();
  }

  @Override
  public String getBodyID() {
    return _bodyID;
  }

  @Override
  public AnchorState getState() {
    return _state;
  }

  @Override
  public void setVisible(boolean visible) {
    _state.setVisible(visible);
  }

  @Override
  public boolean isVisible() {
    return _state.isVisible();
  }

  @Override
  public AnchorType getAnchorType(){
    return _type;
  }

  @Override
  public Vect3d getPoint() {
    return null;
  }

  @Override
  public Rib3d getRib() {
    return null;
  }

  @Override
  public Polygon3d getPoly() {
    return null;
  }

  @Override
  public Circle3d getDisk() {
    return null;
  }

  @Override
  public boolean isEqualRib(String id1, String id2) {
    return false;
  }

  @Override
  public boolean isEqualPlane(List<String> anchorIDs) {
    return false;
  }

  @Override
  public boolean isMovable() {
    return _state.isMovable();
  }

  @Override
  public void setMovable(boolean movable) {
    _state.setMovable(movable);
  }

  @Override
  public ArrayList<String> arrayIDs(){
    return new ArrayList<>(_pointIDs);
  }

  @Override
  public String getTitle() {
    return _title.toString();
  }

  @Override
  public String id() {
    return _id;
  }
  
  @Override
  public String alias() {
    return "";
  }
}