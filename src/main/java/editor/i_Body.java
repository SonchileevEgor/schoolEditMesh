package editor;

import geom.i_Geom;
import geom.AbstractPolygon;
import bodies.BodyType;
import editor.state.BodyState;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Ray3d;
import geom.Rib3d;
import geom.Vect3d;
import java.util.ArrayList;
import java.util.HashMap;
import opengl.Render;

// The interface of common body.

public interface i_Body extends i_Identifiable {

  @Override
  public String id();

  /**
   * Type of the body.
   * @return
   */
  public BodyType type();

  /**
   * Human-readable name of body type.
   * @return
   */
  public String alias();

  /**
   * Existence of the body.
   * @return
   */
  public boolean exists();

  /**
   * Set alias of body.
   * @param alias
   */
  public void setAlias(String alias);

  /**
   * Title of the body
   * @return
   */
  public String getTitle();
  
  /**
   * Is body renamable.
   * @return 
   */
  public boolean isRenamable();
  
  /**
   * Set ability of body to change name.
   */
  public void setRenamable(boolean renamable);

  /**
   * Gets anchor ID by key
   * @param key
   * @return
   */
  public String getAnchorID(String key);

  /**
   * Anchors attached to this body.
   * @return
   */
  public HashMap<String, String> getAnchors();

  /**
   * Attach anchor to body.
   * @param key
   * @param id
   */
  public void addAnchor(String key, String id);

  /**
   * Adding anchors-rib of body to editor.
   * @param edt
   */
  public void addRibs(Editor edt);

  /**
   * Adding anchors-facet of body to editor.
   * @param edt
   */
  public void addPlanes(Editor edt);

  /**
   * Draw carcass of body.
   * Body draws itself depending on its state.
   *
   * @param ren OpenGL Render
   */
  public void glDrawCarcass(Render ren);

  /**
   * Draw facets of body.
   * Body draw itself depending on its state.
   *
   * @param ren OpenGL Render
   */
  public void glDrawFacets(Render ren);

  /**
   * Get intersection with plane as a abstract polygon
   * @param plane A plane that intersects with the body
   * @return Resulting of intersection
   */
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane);

  /**
   * Graphics state of body.
   * @return
   */
  public BodyState getState();

  /**
   * Attach reference to state to body.
   * @param state
   */
  public void setState(BodyState state);

  /**
   * True, if body has a builder;
   * False, if body is a clone of anchor.
   * @return
   */
  public boolean hasBuilder();

  /**
   * Set hasBuilder field.
   * @param hasBuilder
   */
  public void setHasBuilder(boolean hasBuilder);
  
  /**
   * Intersection of body with ray.
   * Object is assumed as solid, boundary included.
   * Method is used when body is picked on scene.
   * Render is needed for providing screen features (scale, resolution, etc.)
   * @param ren
   * @param ray
   * @param x Coordinate X of canvas. Redundant data used for performance.
   * @param y Coordinate Y of canvas. Redundant data used for performance.
   * @return 
   *    Intersection point (closest to camera) or null.
   */
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y);

  /**
   * Return Geom object of body.
   * @return
   */
  public i_Geom getGeom();

  /**
   * @param id
   * @param title
   * @param geom
   * @return body by its id, name and geom object
   */
  public i_Body getBody(String id, String title, i_Geom geom);

  /**
   * Add anchors of points and circles to result.
   * @param result
   * @param edt
   */
  public void addAnchorsToBody(i_Body result, Editor edt);

  public ArrayList<Rib3d> getAllEdges(Editor edt);

  public ArrayList<Vect3d> getAllVertices();

  public ArrayList<Polygon3d> getAllFaces(Editor edt);
};
