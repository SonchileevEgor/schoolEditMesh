package bodies;

import static bodies.PointBody.BODY_KEY_POINT;
import static config.Config.GUI_EPS;
import opengl.colorgl.ColorGL;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import editor.Editor;
import editor.behavior.Behavior;
import editor.behavior.i_PointMoveBehavior;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.AbstractPolygon;
import geom.ExDegeneration;
import geom.ElementaryFunction2d;
import geom.Line3d;
import geom.NewSystemOfCoor;
import geom.Plane3d;
import geom.Polygon3d;
import geom.Ray3d;
import geom.Vect3d;
import geom.i_Geom;
import java.util.ArrayList;
import opengl.CalculatorGL;

/**
 *
 * @author alena
 */
public class ElementaryFunctionBody extends BodyAdapter {
  private ElementaryFunction2d _elementaryFunction; // math object elementaryFunction

  /**
   * Constructor of elementaryFunction by math elementaryFunction
   * @param id
   * @param title
   * @param elementaryFunction math object ElementaryFunction2d
   */
  public ElementaryFunctionBody(String id, String title, ElementaryFunction2d elementaryFunction) {
    super(id, title);
    _alias = "функция";
    _elementaryFunction = elementaryFunction;
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public ElementaryFunctionBody(String id, String title) {
    super(id, title);
    _alias = "функция";
    _exists = false;
  }

  public ElementaryFunction2d elementaryFunction() {
    return _elementaryFunction;
  }

  @Override
  public BodyType type() {
    return BodyType.ELEMENTARYFUNCTION;
  }

  @Override
  public i_Geom getGeom() {
    return _elementaryFunction;
  }

  @Override
  public i_Body getBody(String id, String title, i_Geom geom) {
    return new ElementaryFunctionBody(id, title, (ElementaryFunction2d) geom);
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    //!! TODO: Реализовать функцию.
    return null;
  }
  
  public void glDrawCarcass(Render ren) {
    if (!exists()) {
      return;
    }
    if (_state.isVisible()) {
      Drawer.setObjectColor(ren, (ColorGL) _state.getParam(DisplayParam.CARCASS_COLOR));
      if (_state.isChosen()) {
        Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS) * 1.5);
      } else {
        Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
      }
      Drawer.drawElementaryFunction(ren, _elementaryFunction, TypeFigure.CURVE);
    }
  }

  @Override
  public void glDrawFacets(Render ren){
    // Point has not facets
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    ArrayList<Polygon3d> faces = new ArrayList<>();
    return faces;
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    /// TODO check
  }
}
