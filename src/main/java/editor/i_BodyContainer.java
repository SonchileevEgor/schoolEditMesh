package editor;

import bodies.BodyType;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple container of i_Body objects.
 *
 * @author ltwood
 */
public interface i_BodyContainer {
  /**
   * Add body to container.
   * @param bd
   */
  void add(i_Body bd);

  /**
   * Get body by its index in array.
   * @param index
   * @return
   */
  i_Body get(int index);

  /**
   * Get body by ID.
   *
   * @param bodyID
   * @return
   * @throws ExNoBody
   */
  i_Body get(String bodyID) throws ExNoBody;
  
  /**
   * Get list of IDs.
   * 
   * @return 
   */
  ArrayList<String> getBodyIDs();

  /**
   * Get list of all bodies.
   *
   * @return
   */
  ArrayList<i_Body> getAllBodies();

  /**
   * Get bodies of given type.
   *
   * @param bd
   * @return
   */
  ArrayList<i_Body> getBodiesByType(BodyType bd);
  
  /**
   * Get bodies by list of IDs.
   * @param ids
   * @return 
   */
  ArrayList<i_Body> getBodiesByIDs(List<String> ids);
  
  /**
   * Get body by title.
   * @param title
   * @return 
   *    Body ID.
   * @throws editor.ExNoBody
   */
  String getBodyIdByTitle(String title) throws ExNoBody;

  /**
   * Get all polygon bodies.
   * (Inc. triangles, rectangles etc.)
   *
   * @return
   */
  ArrayList<i_Body> getPolygonBodies();

  /**
   * Get list of body titles.
   *
   * @return
   */
  ArrayList<String> getAllTitles();

  /**
   * Check if container contains body with given ID.
   *
   * @param bodyID
   * @return
   */
  boolean contains(String bodyID);

  /**
   * Check if container contains body with given title.
   *
   * @param title
   * @return
   */
  boolean hasTitle(String title);

  /**
   * Get size of container.
   *
   * @return
   */
  int size();

  /**
   * Clear container.
   */
  void clear();
};
