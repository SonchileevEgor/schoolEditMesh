package gui.mode;

import gui.EdtController;

/**
 *
 * @author Elena
 */
public class Create2dLineOrPointSymmetryMode extends CreatePlaneOrLineOrPointSymmetryMode {

  public Create2dLineOrPointSymmetryMode(EdtController ctrl) {
    super(ctrl);
  }

  @Override
  protected void setCreationMessengers() {
    _msg.setMessage("Выберите прямую или точку-центр симметрии{MOUSELEFT}",
            "Выберите тело для отображения{MOUSELEFT}");
  }

  @Override
  public ModeList alias() {
    return ModeList.MODE_CREATE_2D_SYM;
  }

  @Override
  protected String description() {
    return "<html><strong>Симметрия</strong><br>относительно прямой или точки-центра";
  }
}
