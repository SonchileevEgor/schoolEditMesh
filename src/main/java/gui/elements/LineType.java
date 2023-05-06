package gui.elements;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 *
 * @author alexeev
 */
public enum LineType {
  PLAIN("plain"){
    @Override
    public Stroke getStroke() {
      return new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 1);
    }
  },
  /*DOTTED("dotted"){
    @Override
    public Stroke getStroke() {
      return new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[] { 0.2f, 5.0f }, 1);
    }
  },*/
  DASHED("dashed"){
    @Override
    public Stroke getStroke() {
      return new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[] { 5.0f, 5.0f }, 1);
    }
  };

  public abstract Stroke getStroke();

  public String getKey() {
    return _name;
  }

  public static LineType getByKey(String key) {
    for (LineType item : values()){
      if (key.equals(item.getKey())){
        return item;
      }
    }
    throw new AssertionError("Error: type of line <" + key + "> is not supported");
  }

  private LineType(String str) {
    _name = str;
  }

  private String _name;
}
