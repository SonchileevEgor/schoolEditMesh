package bodies;

import editor.i_Body;
import geom.Plane3d;

/**
 * Интерфейс плоского тела.
 * @author alexeev
 */
public interface i_PlainBody extends i_Body {

  /**
   * Плоскость, в которой лежит тело.
   * @return
   */
  Plane3d plane();
}
