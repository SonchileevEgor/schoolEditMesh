package editor;

import editor.state.AnchorManager;
import editor.state.BodyStateManager;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import minjson.JsonArray;
import minjson.JsonObject;
import util.Fatal;

/**
 * Editor state stored in Json format.
 *
 * @author alexeev
 */
public class URState {
  private JsonArray _anchorState;
  private JsonArray _graphicState;
  private JsonArray _state;

  private String _description;

  /**
   * Restore scene from file in Json format.
   *
   * @param filename
   */
  public URState(String filename) {
    try (FileReader input = new FileReader(filename)){
      JsonArray j_input = JsonArray.readFrom(input);
      _state = j_input.get(0).asArray();
      _graphicState = j_input.get(1).asArray();
      _anchorState = j_input.get(2).asArray();
      _description = "Загрузка сцены";
      input.close();
    } catch (IOException ex) {
      Fatal.error("can't open scene <" + ex.getMessage() + ">");
    }
  }

  /**
   * Editor state for Undo-Redo operation.
   * @param bb
   * @param bd
   * @param anch
   * @param anchMgr
   * @param bodyMgr
   * @param descr Description of Undo-Redo state.
   */
  public URState(BodyBuilderContainer bb, i_BodyContainer bd, i_AnchorContainer anch,
                 AnchorManager anchMgr, BodyStateManager bodyMgr, String descr) {
    _state = new JsonArray();
    _description = descr;
    _graphicState = bodyMgr.toJson(bd);
    _anchorState = anchMgr.toJson(anch);

    // Далее процесс повторяет создание JSON-объекта для записи в файл, но без записи в файл :)
    for (int i = 0; i < bb.get_all().size(); i++) {
      JsonObject json = new JsonObject();
      json.add("id", bb.get(i).id());
      json.add("type", bb.get(i).alias());
      if(bb.get(i).title() != null) {
        json.add("title", bb.get(i).title());
      }
      json.add("params", bb.get(i).getJSONParams());
      _state.add(json);
    }
  }

  /**
   * Editor state for Undo-Redo operation.
   * @param edt
   * @param descr Description of Undo-Redo state.
   */
  public URState(Editor edt, String descr) {
    this(edt.bb(), edt.bd(), edt.anchors(), edt.anchMgr(), edt.bodyMgr(), descr);
  }

  /**
   * Editor state for Undo-Redo operation.
   * @param edt
   */
  public URState(Editor edt) {
    this(edt, "");
  }

  public JsonArray getState() {
    return _state;
  }

  public JsonArray getGraphicState() {
    return _graphicState;
  }

  public JsonArray getAnchorState() {
    return _anchorState;
  }

  public String getDescription() {
    return _description;
  }

  public JsonArray toJson() {
    JsonArray result = new JsonArray();
    result.add(_state);
    result.add(_graphicState);
    result.add(_anchorState);

    return result;
  }

  public void writeToFile(String filename) {
    try (FileWriter f = new FileWriter(filename)) {
      toJson().writeTo(f);
    } catch (IOException ex) {
      Fatal.error("IO Exception at <" + ex.getMessage() + ">");
    }
  }
}
