package editor;

import builders.AllBuildersManager;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import gui.EdtController;
import error.i_ErrorHandler;
import java.util.ArrayList;
import java.util.TreeSet;
import minjson.JsonObject;

/**
 * Interface for builder (virtual creator) of i_Body object.
 *
 * @author alexeev
 */
 public interface i_BodyBuilder extends i_Identifiable {

  /**
   * Create body by given parameters.
   *
   * @param edt Editor
   * @param eh Error handler (cannot be null)
   * @return
   */
  i_Body create(Editor edt, i_ErrorHandler eh);

  /**
   * Convert builder parameters to Json format.
   * Required for saving scene.
   *
   * @return JsonObject consisting of set of pairs (parameter, ID).
   */
  JsonObject getJSONParams();

  /**
   * Get alias of body builder.
   * Used in {@link AllBuildersManager} as key string.
   *
   * @return alias of body builder
   */
  String alias();

  /**
   *
   * @param ctrl
   * @param precision precision of numbers.
   * @return description of builder
   */
  String description(EdtController ctrl, int precision);

  /**
   * Set value by given parameter key.
   *
   * @param param Key of the parameter.
   * @param value Value of the parameter.
   */
  void setValue(String param, Object value);

  /**
   * Get value of given parameter.
   *
   * @param param Key of the builder parameter.
   * @return
   */
  Object getValue(String param);
  
  /**
   * Get type of given parameter.
   * @param param Key of the builder parameter.
   * @return 
   */
  BuilderParamType getParamType(String param);

  /**
   * Get title of builder.
   * (for displaying in UI)
   *
   * @return Value of "name" parameter.
   */
  String title();

  /**
   * Get sorted list of builder parameters.
   * In descending priority order.
   * 
   * @return
   */
  TreeSet<BuilderParam> params();
  
  /**
   * Keys with order, which is required for the reflexive call.
   * @return 
   */
  ArrayList<String> keys();

  boolean hasParam(String param);
}
