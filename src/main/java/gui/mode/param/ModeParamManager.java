package gui.mode.param;

import static config.Config.PRECISION;
import gui.EdtController;
import java.util.EnumMap;

/**
 *
 * @author rita
 */
public class ModeParamManager {
  private EdtController _ctrl;
  private EnumMap<CreateModeParamType, ModeParam> _params;

  /**
   * Parameters that were chosen in current creation mode.
   */
  private EnumMap<CreateModeParamType, Boolean> _chosenParams;

  /**
   * Parameters that are used in current creation mode.
   */
  private EnumMap<CreateModeParamType, Integer> _usedParams;

  /**
   * Create body mode parameters manager.
   * @param ctrl
   * @author alexeev, rita
   */
  public ModeParamManager(EdtController ctrl) {
    _ctrl = ctrl;
    _params = new EnumMap<>(CreateModeParamType.class);
    _chosenParams = new EnumMap<>(CreateModeParamType.class);
    _usedParams = new EnumMap<>(CreateModeParamType.class);

    Angle2ModeParam angle2Param = new Angle2ModeParam();
    AngleModeParam angleParam = new AngleModeParam();
    Coefficient2ModeParam coef2Param = new Coefficient2ModeParam();
    CoefficientModeParam coefParam = new CoefficientModeParam();
    DistanceModeParam lengthParam = new DistanceModeParam(CreateModeParamType.LENGTH);
    DistanceModeParam widthParam = new DistanceModeParam(CreateModeParamType.WIDTH);
    DistanceModeParam heightParam = new DistanceModeParam(CreateModeParamType.HEIGHT);
    DistanceModeParam radiusParam = new DistanceModeParam(CreateModeParamType.RADIUS);
    RatioModeParam ratioParam = new RatioModeParam();
    RotationAngleModeParam rotAngleParam = new RotationAngleModeParam();
    VertNumModeParam vertNumParam = new VertNumModeParam();

    _ctrl.getScene().getRender().addDisplayListener(lengthParam);
    _ctrl.getScene().getRender().addDisplayListener(widthParam);
    _ctrl.getScene().getRender().addDisplayListener(heightParam);
    _ctrl.getScene().getRender().addDisplayListener(radiusParam);
    _ctrl.getScene().getRender().notifyScaleChange();

    _params.put(CreateModeParamType.ANGLE2, angle2Param);
    _params.put(CreateModeParamType.ANGLE, angleParam);
    _params.put(CreateModeParamType.COEFFICIENT, coefParam);
    _params.put(CreateModeParamType.COEFFICIENT2, coef2Param);
    _params.put(CreateModeParamType.HEIGHT, heightParam);
    _params.put(CreateModeParamType.LENGTH, lengthParam);
    _params.put(CreateModeParamType.RADIUS, radiusParam);
    _params.put(CreateModeParamType.RATIO, ratioParam);
    _params.put(CreateModeParamType.ROT_ANGLE, rotAngleParam);
    _params.put(CreateModeParamType.VERT_NUMBER, vertNumParam);
    _params.put(CreateModeParamType.WIDTH, widthParam);

    reset();
  }

  public double getValueAsDouble(CreateModeParamType type) {
    return _params.get(type).asDouble();
  }

  public int getValueAsInt(CreateModeParamType type) {
    return _params.get(type).asInt();
  }

  public String getValueAsString(CreateModeParamType type) {
    return _params.get(type).asString();
  }
  
  public String getFormattedValue(CreateModeParamType type, int presicion){
    return _params.get(type).getFormattedValue(presicion);
  }

  /**
   * Set default values to all parameters.
   * Set used and chosen options to false.
   * Zero means that the option is not used (_usedParams)
   */
  public final void reset() {
    for( CreateModeParamType type : CreateModeParamType.values() ) {
      _params.get(type).setDefaultValue();
      _chosenParams.put(type, Boolean.FALSE);
      _usedParams.put(type, (int)0);
    }
  }

  public void setChosen(CreateModeParamType type) {
    _chosenParams.put(type, Boolean.TRUE);
  }

  public boolean isChosen(CreateModeParamType type) {
    return _chosenParams.get(type);
  }

  /**
   * Establish parameter is used in current mode.
   * @param type
   * @param num
   * Parameters are numbered from one.
   * Zero means that the option is not used.
   */
  public void setUsed(CreateModeParamType type, int num) {
    _usedParams.put(type, num);
  }

  /**
   * Is parameter used in current mode.
   * @param type
   * @return
   */
  public int isUsed(CreateModeParamType type) {
    return _usedParams.get(type);
  }

  /**
   * Increase parameter of give ntype one time.
   * @param type
   */
  public void inc(CreateModeParamType type) {
    _params.get(type).inc();
  }

  /**
   * Decrease parameter of given type one time.
   * @param type
   */
  public void dec(CreateModeParamType type) {
    _params.get(type).dec();
  }

  /**
   * Change parameter value of given type @link{stepNum} times.
   * @param type
   * @param stepNum
   */
  public void change(CreateModeParamType type, int stepNum) {
    _params.get(type).change(stepNum);
  }

  public void setValue(CreateModeParamType type, Object value) {
   _params.get(type).setValue(value);
  }

  public void showValue(CreateModeParamType type) {
    _ctrl.status().showValue(_params.get(type).getFormattedString(PRECISION.value()));
  }

  public EnumMap<CreateModeParamType, Integer> getUsedParams() {
    return _usedParams;
  }
}
