package gui.mode;

import bodies.BodyType;
import builders.BodyBuilder;
import builders.LineOrthogonalLine2dBuilder;
import builders.LineOrthogonalRay2dBuilder;
import builders.LineOrthogonalRib2dBuilder;
import editor.ExNoBody;
import editor.i_BodyBuilder;
import gui.EdtController;

/**
 *
 * @author Elena
 */
public class Create2dLineOrthLineRayRibMode extends CreateLineOrthogonalLineRibOrRayMode {

  public Create2dLineOrthLineRayRibMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_2D_LINE_ORTH_LINE_OR_RIB_OR_RAY;
  }

  @Override
  protected String description() {
    return "<html><strong>Прямая,</strong><br>проходящая через точку перпендикулярно другой прямой (отрезку, лучу)";
  }

  @Override
  protected void create() {
    try {
      i_BodyBuilder builder;
      if (_type == BodyType.LINE) {
        builder = new LineOrthogonalLine2dBuilder();
        builder.setValue(BodyBuilder.BLDKEY_LINE, _id1);
      } else if (_type == BodyType.RIB) {
        builder = new LineOrthogonalRib2dBuilder();
        _id1 = _ctrl.getBody(_id1).getAnchorID("rib");
        builder.setValue(BodyBuilder.BLDKEY_RIB, _id1);
      } else {
        builder = new LineOrthogonalRay2dBuilder();
        builder.setValue(BodyBuilder.BLDKEY_RAY, _id1);
      }
      builder.setValue(BodyBuilder.BLDKEY_NAME, BodyType.LINE.getName(_ctrl.getEditor()));
      builder.setValue(BodyBuilder.BLDKEY_P, _id2);

      //!! TODO: добавить прямые углы
      _ctrl.add(builder, checker, false);
      reset();
    } catch (ExNoBody ex) {
    }
  }
}
