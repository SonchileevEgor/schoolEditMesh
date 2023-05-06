package bodies;

import editor.i_Body;
import geom.ExDegeneration;
import geom.Line3d;

/**
 * Класс линейного тела (отрезок, луч, прямая).
 * @author alexeev
 */
public interface i_LinearBody extends i_Body {
  Line3d line() throws ExDegeneration;
}
