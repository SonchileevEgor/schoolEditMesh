package builders;

import bodies.PointBody;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.i_BodyBuilder;
import geom.Vect3d;
import gui.EdtController;

import org.junit.jupiter.api.Assertions;

/**
 *
 * @author alexeev
 */
public class SphereBuilderTest extends BuilderTest {
  public SphereBuilderTest(EdtController ctrl) {
    super(ctrl, SphereBuilder.ALIAS);
  }

  @Override
  public void createBuilder() {
    try {
      i_BodyBuilder pt = AllBuildersManager.create(PointBuilder.ALIAS, "P", new Vect3d(1, 1, 1));
      ctrl.addSilently(pt);
      builder = AllBuildersManager.create(builderAlias, "sph", ctrl.getAnchorID(pt.id(), PointBody.BODY_KEY_POINT), 1.0);
    } catch( ExNoAnchor | ExNoBody ex ){
      ex.printStackTrace();
      Assertions.fail();
    }
  }
}
