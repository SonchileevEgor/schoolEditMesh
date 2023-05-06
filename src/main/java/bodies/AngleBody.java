package bodies;

import gui.elements.AngleSideType;
import gui.elements.AngleStyle;
import gui.elements.SidesSizeType;
import editor.Editor;
import editor.i_Body;
import editor.state.DisplayParam;
import geom.*;
import java.util.ArrayList;
import opengl.Drawer;
import opengl.Render;
import opengl.TypeFigure;

/**
 * Meta body - angle.
 *
 * @author Rita
 */
public class AngleBody extends BodyAdapter {
  private Angle3d _angle;// math object angle

  public static final String BODY_KEY_A = "A";
  public static final String BODY_KEY_B = "B";
  public static final String BODY_KEY_C = "C";

  /**
   * Constructor of angle by math angle
   * @param id
   * @param title
   * @param angle math object Rib3d
   */
  public AngleBody (String id, String title, Angle3d angle){
    super(id, title);
    _angle = angle;
    _alias = "угол";
    _exists = true;
  }

  /**
   * Constructor for null-body
   * @param id
   * @param title
   */
  public AngleBody(String id, String title) {
    super(id, title);
    _alias = "угол";
    _exists = false;
  }

  // Getters for all vertices of angle
  public Vect3d A(){ return _angle.pointOnFirstSide(); }
  public Vect3d B(){ return _angle.vertex(); }
  public Vect3d C(){ return _angle.pointOnSecondSide(); }

  /**
   * @return math object angle
   */
  public Angle3d angle() { return _angle; }

  /**
   * Расчёт оптимального размера отметки угла.
   * @param ssType relative size of angle sides.
   * @return
   */
  private double getOptimalBadgeSize(Render ren, SidesSizeType ssType) {
    double defaultDist = ren.getCameraPosition().distance() * 0.05;
    double side1Half = _angle.side1().norm() * 0.45;
    double side2Half = _angle.side2().norm() * 0.45;
    double result = 0;
    switch (ssType) {
      case LONG_LONG:
        result = defaultDist;
        break;
      case SHORT_SHORT:
        result = Math.min(defaultDist, Math.min(side1Half, side2Half));
        break;
      case SHORT_LONG:
        result = Math.min(defaultDist, side1Half);
        break;
      case LONG_SHORT:
        result = Math.min(defaultDist, side2Half);
        break;
    }
    return result;
  }

  @Override
  public BodyType type() { return BodyType.ANGLE; }

  @Override
  public void glDrawFacets(Render ren){
    if ( !_exists )
      return;
    if( _state.isVisible() && (boolean)_state.getParam(DisplayParam.FILL_VISIBLE)){
      Drawer.setObjectFacetColor(ren, _state);
      double badgeSize = getOptimalBadgeSize(ren,
              (SidesSizeType)_state.getParam(DisplayParam.SIDES_SIZE_TYPE));
      try {
        if( _angle.isRight() ){
          Drawer.drawMarkOfRightAngle(ren, _angle, badgeSize / 2, TypeFigure.SOLID);
        } else {
          Drawer.drawAngleMark(ren, _angle, badgeSize,
                  (AngleStyle)_state.getParam(DisplayParam.ANGLE_STYLE), TypeFigure.SOLID);
        }
      } catch( ExDegeneration ex ){}
    }
  }

  @Override
  public void glDrawCarcass(Render ren){
    if ( !_exists )
      return;
    if( _state.isVisible() ){
      Drawer.setObjectCarcassColor(ren, _state);
      double badgeSize = getOptimalBadgeSize(ren,
              (SidesSizeType)_state.getParam(DisplayParam.SIDES_SIZE_TYPE));
      try {
        // рисуем стороны угла пунктиром
        switch((AngleSideType)_state.getParam(DisplayParam.DRAW_FIRST_ANGLE_SIDE)){
          case DEFAULT:
            break;
          case SEGMENT:
            Drawer.drawSegmentStipple(ren, _angle.vertex(), _angle.pointOnFirstSide());
            break;
          case LINE:
            Drawer.drawLineStipple(ren, _angle.vertex(), _angle.pointOnFirstSide());
        }
        switch((AngleSideType)_state.getParam(DisplayParam.DRAW_SECOND_ANGLE_SIDE)){
          case DEFAULT:
            break;
          case SEGMENT:
            Drawer.drawSegmentStipple(ren, _angle.vertex(), _angle.pointOnSecondSide());
            break;
          case LINE:
            Drawer.drawLineStipple(ren, _angle.vertex(), _angle.pointOnSecondSide());
        }

        if (_state.isChosen()) {
          Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS) * 1.5);
        } else {
          Drawer.setLineWidth(ren, (float)_state.getParam(DisplayParam.CARCASS_THICKNESS));
        }

        if( _angle.isRight() ) {
          Drawer.drawMarkOfRightAngle(ren, _angle, badgeSize / 2, TypeFigure.WIRE);
        } else {
          Drawer.drawAngleMark(ren, _angle, badgeSize,
                  (AngleStyle)_state.getParam(DisplayParam.ANGLE_STYLE), TypeFigure.WIRE);
        }
      } catch( ExDegeneration ex ){}
    }
  }

  @Override
  public i_Geom getGeom() {
      return _angle;
  }

  @Override
  public i_Body getBody (String id, String title, i_Geom geom) {
    return new AngleBody (id, title, (Angle3d) geom);
  }

  @Override
  public void addAnchorsToBody (i_Body result, Editor edt) {
    AngleBody angle = (AngleBody) result;
    edt.addAnchor(angle.A(), result, BODY_KEY_A);
    edt.addAnchor(angle.B(), result, BODY_KEY_B);
    edt.addAnchor(angle.C(), result, BODY_KEY_C);
  }
  
  @Override
  public Vect3d intersectWithRay(Render ren, Ray3d ray, double x, double y){
    ArrayList<Vect3d> intersection = new Sector3d(_angle, getOptimalBadgeSize(ren,
            (SidesSizeType)_state.getParam(DisplayParam.SIDES_SIZE_TYPE))).intersect(ray);
    return intersection.isEmpty() ? null : intersection.get(0);
  }
}
