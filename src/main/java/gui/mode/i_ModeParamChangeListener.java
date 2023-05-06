package gui.mode;

import gui.mode.param.CreateModeParamType;

/**
 * Слушатель изменения параметров режима
 *
 * @author Лютенков
 */
public interface i_ModeParamChangeListener {

  public void updateParams(CreateModeParamType type);
}
