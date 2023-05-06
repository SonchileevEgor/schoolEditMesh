package editor;

import editor.state.AnchorState;
import geom.Circle3d;
import geom.Polygon3d;
import geom.Rib3d;
import geom.Vect3d;
import java.util.ArrayList;
import java.util.List;
import opengl.Render;

/**
 *
 * @author alexeev
 */
public interface i_Anchor extends i_Identifiable {

  /**
   * ID of the body that created this anchor.
   * @return
   */
  public String getBodyID();

  /**
   * Graphics state of anchor.
   * @return
   */
  public AnchorState getState();

  /**
   * Title of the anchor.
   * @return
   */
  public String getTitle();

  /**
   * Type of anchor.
   * @return
   */
  public AnchorType getAnchorType();

  /**
   * Array of IDs of points of anchor
   * @return
   */
  public ArrayList<String> arrayIDs();

  /**
   * Coordinates of point anchor.
   * @return
   */
  public Vect3d getPoint();

  /**
   * Visibility state.
   * @return
   */
  public Rib3d getRib();

  /**
   * Check whether the anchor rib vertices coincides with given values.
   * @param id1
   * @param id2
   * @return
   */
  public boolean isEqualRib(String id1, String id2);

  /**
   * Geom object Polygon3d of polygon anchor.
   * @return
   */
  public Polygon3d getPoly();

  /**
   * Returns true if the plane equal to other plane-anchor.
   * @param anchorIDs
   * @return
   */
  public boolean isEqualPlane(List<String> anchorIDs);

  /**
   * Geom object Circle3d of disk anchor.
   * @return
   */
  public Circle3d getDisk();

  /**
   * Visibility state of anchor.
   * @return
   */
  public boolean isVisible();

  /**
   * Is anchor movable.
   * @return
   */
  public boolean isMovable();

  /**
   * Set visibility of the anchor.
   * @param visible
   */
  public void setVisible(boolean visible);

  /**
   * Set movable flag of the anchor.
   * @param movable
   */
  public void setMovable(boolean movable);

  /**
   * Draw anchor carcass on the canvas.
   * @param ren
   */
  public void drawCarcass(Render ren);

  /**
   * Draw anchor surface on the canvas.
   * @param ren
   */
  public void drawSurface(Render ren);
  
  /**
   * Alias of an anchor (point, segment, circle, or disk).
   * Needed, f. e., to distinction of circle and disk.
   * @return 
   */
  public String alias();

  public String toString(int precision, boolean is3d);
}
