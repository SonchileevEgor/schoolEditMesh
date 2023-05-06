package editor;

import java.util.Collection;

/**
 *
 * @author alexeev
 */
public interface i_FocusChangeListener {

  /**
   * Reaction on losing focus of all bodies.
   *
   * @param bodyIDs Body IDs of unfocused bodies.
   */
  public void focusCleared(Collection<String> bodyIDs);

  /**
   * Reaction on losing focus of entity.
   *
   * @param id
   * @param isBody
   */
  public void focusLost(String id, boolean isBody);

  /**
   * Reaction on applying focus of entity.
   *
   * @param id
   * @param isBody
   */
  public void focusApply(String id, boolean isBody);
}
