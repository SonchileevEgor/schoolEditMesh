package builders;

import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import geom.Vect3d;
import gui.EdtController;

/**
 * Тестер для билдера точки по координатам.
 * @author alexeev
 */
public class PointBuilderTest extends BuilderTest {
  public PointBuilderTest(EdtController ctrl) {
    super(ctrl, PointBuilder.ALIAS);
  }

  @Override
  public void createBuilder() {
    builder = AllBuildersManager.create(builderAlias, "P", new Vect3d(0, 0, 0));
  }
}
