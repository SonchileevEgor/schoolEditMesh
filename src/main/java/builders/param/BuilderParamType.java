package builders.param;

import geom.Vect3d;
import java.util.ArrayList;

/**
 * Тип параметра билдера.
 *
 * @author alexeev
 */
public enum BuilderParamType {
  NAME(String.class, 200),
  ANCHOR(String.class, 100),
  ANCHOR_LIST(ArrayList.class, 90),

  /**
   * Point (3D or 2D).
   */
  POINT(Vect3d.class, 80),
  /**
   * Vector builder parameter.
   * Vector parameter is not shown in body settings dialog in 2D mode.
   */
  VECT(Vect3d.class, 70),

  BODY(String.class, 60),

  INT(Integer.class, 50),

  DOUBLE(Double.class, 40),

  DOUBLE_POSITIVE(Double.class, 30),

  /**
   * Angular value.
   * Could be changed in UI both in 2D and 3D mode.
   */
  ANGLE_VALUE(Double.class, 20),

  /**
   * Rotation angle.
   * Could be changed in UI only in 3D mode.
   */
  ANGLE_ROTATION(Double.class, 20),

  BOOLEAN(Boolean.class, 10);

  public final Class type;
  private final int _priority;

  BuilderParamType(Class type, int priority) {
    this.type = type;
    _priority = priority;
  }

  /**
   * Default priority of the builder parameter.
   * Parameters with big priority are upper in builder params dialog.
   *
   * @return
   */
  public int getDefaultPriority() {
    return _priority;
  }
}
