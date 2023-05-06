package editor.state;

/**
 *
 * @author alexeev
 */
public interface i_BodyStateChangeListener {
  /**
   * Reaction on body state changing.
   * @param bodyID ID of the updated body
   */
  public void updateBodyState(String bodyID);
}
