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
 *
 * @author rita
 */
public class CylinderSectionBody extends BodyAdapter {
  /**
   * Constructor of cone cylinder.
   * Section by plane
   * @param id
   * @param title
   * @param cylinder math object Cylinder3d
   * @param plane math object Plane3d
   * @throws geom.ExZeroDivision
   * @throws geom.ExDegeneration
   */
  public CylinderSectionBody(String id, String title, Cylinder3d cylinder, Plane3d plane)
    throws ExZeroDivision, ExDegeneration, ExGeom {
    super(id, title);
    _cylinderSection = cylinder.sectionByPlane(plane);
    _alias = "цилиндрическое сечение";
    _exists = true;
  }

  /**
   * constructor for null-body
   * @param id
   * @param title
   */
  public CylinderSectionBody(String id, String title) {
    super(id, title);
    _alias = "цилиндрическое сечение";
    _exists = false;
  }

  public CylinderSection3d cylinderSection() {
    return _cylinderSection;
  }

    /**
   * Russian name of cylinder section type
   * @return string with russian words
   * ,
   */
  public String ruType() {
    String ruType;
    switch(_cylinderSection.sectionType()) {
      case "rectangle":
        ruType = "Прямоугольник";
        break;
      case "part of ellipse":
        ruType = "Часть эллипса";
        break;
      case "segment":
        ruType = "Отрезок";
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
  public BodyType type() {
    return BodyType.CYLINDER_SECTION;
  }

  @Override
  public void glDrawCarcass(Render ren){
    if (!exists())
      return;
    if (_state.isVisible()) {
      Drawer.setObjectCarcassColor(ren, _state);
      try{
        Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
        Drawer.drawCylinderSection(ren, _cylinderSection, TypeFigure.WIRE);
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
        Drawer.drawCylinderSection(ren, _cylinderSection, TypeFigure.SOLID);
      } catch (ExDegeneration ex){ }
    }
  }

  @Override
  public ArrayList<Polygon3d> getAllFaces(Editor edt) {
    ArrayList<Polygon3d> faces = _cylinderSection.faces();
    return faces;
  }

  private CylinderSection3d _cylinderSection; // math object cilinder section

  @Override
  public i_Geom getGeom() {
    return _cylinderSection;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    CylinderSection3d sec = (CylinderSection3d)geom;
    try {
      return new CylinderSectionBody(id, title, sec.getCylSysOfCoor().cylinderInOldCoor(sec.cyl()), sec.getCylSysOfCoor().planeInOldCoor(sec.plane()));
    } catch (ExGeom | ExZeroDivision ex) { }
    return null;
  }

  @Override
  public void addAnchorsToBody(i_Body result, Editor edt) {
    CylinderSectionBody section = (CylinderSectionBody) result;
    for (int i = 0; i < section._cylinderSection.pointsQuantity(); i++) {
      edt.addAnchor(section.cylinderSection().points().get(i), result, "p" + i);
    }
  }
    
    
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y) {
    //!! TODO: Реализовать функцию.
    return null;
  }
}
