package gui.mode;

import builders.AngleByTwoPointsBuilder;
import gui.EdtController;
import gui.IconList;
import static gui.mode.ScreenMode.checker;
import static gui.mode.param.CreateModeParamType.ANGLE;
import javax.swing.Icon;

/**
 *
 * @author Куганский
 */
public class Create2dAngleRayByTwoPointsMode extends Create2dAngleByTwoPointsMode {

  public Create2dAngleRayByTwoPointsMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public Icon getSmallIcon() {
    return IconList.ANGLE_BIG_SIDE.getLargeIcon();
  }

  @Override
  public Icon getLargeIcon() {
    return IconList.ANGLE_BIG_SIDE.getLargeIcon();
  }

  @Override
  protected String description() {
    return "<html><strong>Угол</strong><br>по вершине и точке на стороне со сторонами - лучами";
  }

  @Override
  protected void create() {
    AngleByTwoPointsBuilder builder = new AngleByTwoPointsBuilder(_name);
    builder.addAngle(valueAsDouble(ANGLE));
    builder.addPointOnFirstSide(_anchor.get(0).id());
    builder.addVertex(_anchor.get(1).id());
    _ctrl.add(builder, checker, false);
    reset();
  }

}
