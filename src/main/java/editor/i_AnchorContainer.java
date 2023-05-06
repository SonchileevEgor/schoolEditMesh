package editor;

import anchors.*;
import geom.Vect3d;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for the container of anchors.
 */
public interface i_AnchorContainer {
  /**
   * Get coordinates of point anchor.
   * Assuming
   * @param anchorID
   * @return
   * @throws ExNoAnchor
   */
  Vect3d getVect(String anchorID) throws ExNoAnchor;

  /**
   * Get anchor by given ID.
   * @param id
   * @return
   * @throws editor.ExNoAnchor
   */
  i_Anchor get(String id) throws ExNoAnchor;

  /**
   * Get list of anchors.
   * @return
   */
  ArrayList<i_Anchor> getAnchors();

  /**
   * Get anchors created by given body
   * of the specified type.
   * @param bodyID body ID
   * @param type anchor type
   * @return
   */
  ArrayList<String> getAnchors(String bodyID, AnchorType type);

  /**
   * Get all anchors with given type.
   * @param type anchor type
   * @return
   */
  ArrayList<i_Anchor> getAnchorsByType(AnchorType type);

  ArrayList<PointAnchor> getPointAnchors();

  ArrayList<RibAnchor> getRibAnchors();

  ArrayList<PolyAnchor> getPolyAnchors();

  ArrayList<DiskAnchor> getDiskAnchors();

  /**
   * Returns true if there exists an anchor with given title.
   * @param title
   * @return
   */
  boolean hasTitle(String title);

  /**
   * Size of the container.
   * @return
   */
  int size();

  /**
   * Add anchor to the container.
   * @param anc
   */
  void add(i_Anchor anc);

  /**
   * Find anchor that has the same vertices as in given list.
   * If there's no such an anchor, return null.
   * @param anchorIDs
   * @return
   */
  i_Anchor findEqual(List<String> anchorIDs);

  /**
   * Find anchor that has the same vertices as in given list.
   * If there's no such an anchor, return null.
   * @param anchorIDs
   * @return
   */
  i_Anchor findEqual(String... anchorIDs);

  /**
   * Clear container.
   */
  void clear();

  /**
   * Get maximal length of edge.
   * @return
   */
  double getMaxEdgeLength();

  /**
   * Get minimal length of edge.
   * @return
   */
  double getMinEdgeLength();

  /**
   * Get maximal area of face.
   * @return
   */
  double getMaxFaceArea();

  /**
   * Get minimal area of face.
   * @return
   */
  double getMinFaceArea();
};
