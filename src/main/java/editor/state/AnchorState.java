package editor.state;

import editor.AnchorType;
import gui.elements.LineType;
import minjson.JsonObject;
import minjson.JsonValue;
import opengl.LineMark;
import opengl.StyleArrow;

/**
 * @author alexeev
 */
public class AnchorState extends EntityState {
  protected AnchorType _type;

  public AnchorState() {
    super();
  }
  
  public AnchorState(AnchorType t) {
    super(t.getParams());
  }

  public AnchorState(AnchorType t, JsonObject jo) {
    super(t.getParams(), jo);
  }

  public void setTitleVisible(boolean visible) {
    setParam(DisplayParam.TITLE_VISIBLE, visible);
  }

  public boolean isTitleVisible() {
    if (!hasParam(DisplayParam.TITLE_VISIBLE)) return false;
    return (boolean)getParam(DisplayParam.TITLE_VISIBLE);
  }

  public float getCarcassThickness() {
    return (float)getParam(DisplayParam.CARCASS_THICKNESS);
  }

  public void setCarcassThickness(float thickness) {
    setParam(DisplayParam.CARCASS_THICKNESS, thickness);
  }
  
  /**
   * Толщина точки, на которую наведён фокус.
   * @return 
   */
  public float getFocusedPointThickness() {
    return Math.max(4f, (float)getParam(DisplayParam.POINT_THICKNESS) * 1.5f);
  }

  public float getPointThickness() {
    return (float)getParam(DisplayParam.POINT_THICKNESS);
  }

  public void setPointThickness(float thickness) {
    setParam(DisplayParam.POINT_THICKNESS, thickness);
  }

  public LineType getLineType() {
    return (LineType)getParam(DisplayParam.CARCASS_STYLE);
  }

  public void setLineType(LineType type) {
    setParam(DisplayParam.CARCASS_STYLE, type);
  }

  public String getLabel() {
    return (String)getParam(DisplayParam.LABEL);
  }

  public void setLabel(String label) {
    setParam(DisplayParam.LABEL, label);
  }

  public LineMark getMark() {
    return (LineMark)getParam(DisplayParam.LINE_MARK);
  }

  public void setLineMark(LineMark mark) {
    setParam(DisplayParam.LINE_MARK, mark);
  }

  public boolean isMovable() {
    if (!hasParam(DisplayParam.MOVABLE)) return false;
    return (boolean)getParam(DisplayParam.MOVABLE);
  }

  public void setMovable(boolean movable) {
    setParam(DisplayParam.MOVABLE, movable);
  }

  public boolean isFilled() {
    if (!hasParam(DisplayParam.FILL_VISIBLE)) return false;
    return (boolean)getParam(DisplayParam.FILL_VISIBLE);
  }

  public void setFilled(boolean filled) {
    setParam(DisplayParam.FILL_VISIBLE, filled);
  }

  public StyleArrow getArrowBegin() {
    return (StyleArrow)getParam(DisplayParam.ARROW_BEGIN);
  }

  public StyleArrow getArrowEnd() {
    return (StyleArrow)getParam(DisplayParam.ARROW_END);
  }
}