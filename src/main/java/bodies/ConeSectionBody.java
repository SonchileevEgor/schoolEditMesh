package bodies;

import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;
import editor.Editor;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.i_Geom;
import geom.*;

import java.util.ArrayList;

/**
 * Meta body - cone coneSection.
 *
 */
public class ConeSectionBody extends BodyAdapter{
  private ConeSection3d _coneSection; // math object cone coneSection

  /**
   * Constructor of cone coneSection by plane
   * @param id
   * @param title
   * @param cone math object Cone3d
   * @param plane math object Plane3d
   * @throws geom.ExDegeneration
   * @throws geom.ExZeroDivision
   */
  public ConeSectionBody(String id, String title, Cone3d cone, Plane3d plane)
    throws ExGeom, ExZeroDivision {
    super(id, title);
    _alias = "коническое сечение";
    _coneSection = cone.intersectWithPlane(plane);
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public ConeSectionBody(String id, String title) {
    super(id, title);
    _alias = "коническое сечение";
    _exists = false;
  }

  public ConeSection3d coneSection() {
    return _coneSection;
  }

  /**
   * Russian name of cone section type
   * @return string with russian words
   */
  public String ruType() {
    String ruType;
    switch(_coneSection.sectionType()) {
      case "triangle":
        ruType = "Треугольник";
        break;
      case "hyperbola":
        ruType = "Часть гиперболы";
        break;
      case "parabola":
        ruType = "Часть параболы";
        break;
      case "part of ellipse":
        ruType = "Часть эллипса";
        break;
      case "segment":
        ruType = "Отрезок";
        break;
      case "cone vertex":
        ruType = "Вершина конуса";
        break;
      case "point":
        ruType = "Точка";
        break;
      case "ellipse":
        ruType = "Эллипс";
        break;
      case "empty":
        ruType = "Пустое множество точек";
        break;
      case "circle":
        ruType = "Окружность";
        break;
      default:
        ruType = "Не удалось определить";
        break;
    }
    return ruType;
  }

  @Override
  public BodyType type() { return BodyType.CONE_SECTION; }

  @Override
  public void glDrawCarcass(Render ren){
    if (!exists())
      return;
    if (_state.isVisible()) {
      Drawer.setObjectCarcassColor(ren, _state);
      try {
        Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
        Drawer.drawConeSection(ren, _coneSection, TypeFigure.WIRE);
      } catch (ExDegeneration ex) { }
    }
  }

  @Override
  public void glDrawFacets(Render ren){
    if (!exists() || !(boolean)_state.getParam(DisplayParam.FILL_VISIBLE))
      return;
    if (_state.isVisible()) {
      Drawer.setObjectFacetColor(ren, _state);
      try {
        Drawer.drawConeSection(ren, _coneSection, TypeFigure.SOLID);
      } catch (ExDegeneration ex){ }
    }
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    ArrayList<Polygon3d> faces = _coneSection.faces();
    return faces;
  }

  @Override
  public i_Geom getGeom() {
    return _coneSection;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
      ConeSection3d sec = (ConeSection3d)geom;
      try {
          return new ConeSectionBody(id, title, sec.getConeSysOfCoor().coneInOldCoor(sec.cone()), sec.getConeSysOfCoor().planeInOldCoor(sec.plane()));
      } catch (ExGeom | ExZeroDivision ex) { }
      return null;
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    ConeSectionBody section = (ConeSectionBody) result;
    ArrayList<Vect3d> points = section.coneSection().points();
    for (int i = 0; i < points.size(); i++) {
      edt.addAnchor(points.get(i), result, "p" + i);
    }
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    //!! TODO: Реализовать функцию.
    return null;
  }
}
