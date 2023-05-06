package editor.state;

import bodies.BodyType;
import minjson.JsonObject;

/**
 * Graphics state of the body.
 * @author alexeev
 */
public class BodyState extends EntityState {
  public BodyState() {
    super();
  }
  
  public BodyState(BodyType t) {
    super(t.getParams());
  }

  public BodyState(BodyType t, JsonObject jo) {
    super(t.getParams(), jo);
  }
}