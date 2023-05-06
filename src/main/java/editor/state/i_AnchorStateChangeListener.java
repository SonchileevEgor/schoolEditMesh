package editor.state;

import java.util.List;

/**
 *
 * @author alexeev
 */
public interface i_AnchorStateChangeListener {
  /**
   * Reaction on anchor state changing.
   * @param anchorID ID of the updated anchor.
   */
  public void updateAnchorState(String anchorID);
  
  /**
   * Reaction on anchors state changing.
   * @param anchorIDs list of changed anchors
   */
  public void updateAnchorState(List<String> anchorIDs);
}
