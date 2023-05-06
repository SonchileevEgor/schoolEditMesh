package bodies;

import geom.EmptyPolygon;
import geom.AbstractPolygon;
import opengl.colorgl.ColorGL;
import editor.state.BodyState;
import editor.state.DisplayParam;
import editor.Editor;
import editor.i_Body;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Rib3d;
import geom.Vect3d;

import java.util.ArrayList;
import java.util.HashMap;
import opengl.Render;

/**
 *
 * @author alexeev
 */
public abstract class BodyAdapter implements i_Body {
  protected String _id;
  protected String _title;
  protected String _alias;
  protected boolean _isRenamable;
  protected BodyState _state;
  protected boolean _hasBuilder;
  protected boolean _exists;
  protected HashMap<String, String> _anchors; //<key, anchorID>

  public BodyAdapter(String id, String title) {
    _id = id;
    _title = title;
    _hasBuilder = true;
    _anchors = new HashMap<>();
    _isRenamable = true;
  }

  @Override
  public String id() {
    return _id;
  }

  @Override
  public BodyType type() { return null; }

  @Override
  public String alias() {
    return _alias;
  }

  @Override
  public boolean exists() {
    return _exists;
  }

  @Override
  public void setAlias(String alias) {
    _alias = alias;
  }

  @Override
  public String getTitle() {
    return _title;
  }

  @Override
  public void addRibs(Editor edt) {}

  @Override
  public void addPlanes(Editor edt) {}

  @Override
  public void glDrawCarcass(Render ren) { }

  @Override
  public void glDrawFacets(Render ren) { }

  @Override
  public BodyState getState() {
    return _state;
  }

  @Override
  public void setState(BodyState state) {
    _state = state;
  }

  @Override
  public void addAnchor(String key, String id) {
    _anchors.put(key, id);
  }

  @Override
  public String getAnchorID(String key) {
    return _anchors.get(key);
  }

  @Override
  public HashMap<String, String> getAnchors() {
    return _anchors;
  }

  @Override
  public AbstractPolygon getIntersectionWithPlane(Plane3d plane) {
    return new EmptyPolygon();
  }

  @Override
  public ArrayList<Rib3d> getAllEdges(Editor edt) {
    return new ArrayList<>();
  }

  @Override
  public ArrayList<Vect3d> getAllVertices() {
    return new ArrayList<>();
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    return new ArrayList<>();
  }

  @Override
  public boolean hasBuilder() {
    return _hasBuilder;
  }

  @Override
  public void setHasBuilder(boolean hasBuilder) {
    _hasBuilder = hasBuilder;
  }
  
  protected ColorGL getChosenCarcassColor() {
    return ((ColorGL)getState().getParam(DisplayParam.CARCASS_COLOR)).emphasize();
  }

  protected ColorGL getChosenFacetsColor() {
    return ((ColorGL)getState().getParam(DisplayParam.FILL_COLOR)).emphasize();
  }
  
  @Override
  public boolean isRenamable(){
    return _isRenamable;
  }
  
  @Override
  public void setRenamable(boolean renamable){
    _isRenamable = renamable;
  }
}
