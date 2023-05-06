package builders;

import bodies.BodyType;
import bodies.PointBody;
import builders.param.BuilderParam;
import builders.param.BuilderParamType;
import editor.Editor;
import editor.ExNoAnchor;
import editor.ExNoBody;
import editor.behavior.Behavior;
import editor.behavior.BehaviorFactory;
import editor.i_Body;
import editor.i_BodyBuilder;
import geom.Plane3d;
import geom.Vect3d;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import util.Util;

/**
 * Interface for builder (virtual creator) of i_Body object.
 */
abstract public class BodyBuilder implements i_BodyBuilder {
  /**
   * Key for accessing builder params.
   */
  public static final String BLDKEY_NAME = "name";
  public static final String BLDKEY_A = "A";
  public static final String BLDKEY_B = "B";
  public static final String BLDKEY_C = "C";
  public static final String BLDKEY_C1 = "c1";
  public static final String BLDKEY_C2 = "c2";
  public static final String BLDKEY_D = "D";
  public static final String BLDKEY_P = "P";
  public static final String BLDKEY_ALPHA = "alpha";
  public static final String BLDKEY_ANGLE = "angle";
  public static final String BLDKEY_ARC = "arc";
  public static final String BLDKEY_BASE = "base";
  public static final String BLDKEY_BETA = "beta";
  public static final String BLDKEY_BODY = "body";
  public static final String BLDKEY_CENTER = "center";
  public static final String BLDKEY_CIRCLE = "circle";
  public static final String BLDKEY_CUBE = "cube";
  public static final String BLDKEY_CYLINDER = "cylinder";
  public static final String BLDKEY_COEFFICIENT = "coef";
  public static final String BLDKEY_CONE = "cone";
  public static final String BLDKEY_DIRECTION = "direction";
  public static final String BLDKEY_DISK = "disk";
  public static final String BLDKEY_DODECAHEDRON = "dodecahedron";
  public static final String BLDKEY_ELLIPSOID = "ellipsoid";
  public static final String BLDKEY_FACET = "facet";
  public static final String BLDKEY_FOCUS = "focus";
  public static final String BLDKEY_FOCUS1 = "focus1";
  public static final String BLDKEY_FOCUS2 = "focus2";
  public static final String BLDKEY_FUNCTION_STRING = "functionString";
  public static final String BLDKEY_HEIGHT = "height";
  public static final String BLDKEY_ICOSAHEDRON = "icosahedron";  
  public static final String BLDKEY_LINE = "line";
  public static final String BLDKEY_NORMAL = "normal";
  public static final String BLDKEY_POINT_NUM = "num";
  public static final String BLDKEY_OCTAHEDRON = "octahedron";
  public static final String BLDKEY_RIB = "rib";
  public static final String BLDKEY_RADIUS = "radius";
  public static final String BLDKEY_RAY = "ray";
  public static final String BLDKEY_PLANE = "plane";
  public static final String BLDKEY_POINT = "point";
  public static final String BLDKEY_POINTS = "points";
  public static final String BLDKEY_POINT_ON_BOUND = "pointOnBound";
  public static final String BLDKEY_POLYGON = "poly";
  public static final String BLDKEY_PHI = "phi";
  public static final String BLDKEY_PRISM = "prism";
  public static final String BLDKEY_PYRAMID = "pyramid";
  public static final String BLDKEY_RHO = "rho";
  public static final String BLDKEY_SPHERE = "sphere";
  public static final String BLDKEY_TETRAHEDRON = "tetrahedron";
  public static final String BLDKEY_THETA = "theta";
  public static final String BLDKEY_TOP = "top";
  public static final String BLDKEY_TRIANGLE = "triangle";
  public static final String BLDKEY_VERTEX = "vertex";

  String _id;

  /**
   * Map builder key - builder parameter.
   */
  HashMap<String, BuilderParam> _paramsMap;

  /**
   * Set of parameters (sorted by priority).
   */
  TreeSet<BuilderParam> _params;

  /**
   * Order of keys for reflexive call.
   */
  ArrayList<String> _keyOrder;

  /**
   * Flag indicates existence of body.
   * Required for correct error handling.
   */
  boolean _exists;

  public BodyBuilder() {
    _id = Util.getRandomString(16);
    _exists = true;
    initParams();
  }

  /**
   * Create empty builder with given title and ID.
   *
   * @param id ID of the builder.
   * @param title Title of the builder.
   */
  public BodyBuilder(String id, String title) {
    _id = id;
    _exists = true;
    initParams();
    setValue(BLDKEY_NAME, title);
  }

  /**
   * Create empty builder with given title and randomly generated ID.
   *
   * @param title Title of the builder.
   */
  public BodyBuilder(String title) {
    this(Util.getRandomString(16), title);
  }

  /**
   * Create builder by ID and set of parameters.
   *
   * @param id
   * @param params
   */
  public BodyBuilder(String id, HashMap<String, BuilderParam> params) {
    _id = id;
    _exists = true;
    initParams();
    for( Map.Entry<String, BuilderParam> param : params.entrySet() ){
      setValue(param.getKey(), param.getValue().getValue());
    }
  }

  /**
   * Create builder by set of parameters and randomly generated ID.
   *
   * @param params
   */
  public BodyBuilder(HashMap<String, BuilderParam> params) {
    this(Util.getRandomString(16), params);
  }

  /**
   * Инициализация параметров билдера (без значений).
   */
  protected void initParams() {
    _paramsMap = new HashMap<>();
    _params = new TreeSet<>(BuilderParam.PRIORITY_COMPARATOR);
    _keyOrder = new ArrayList<>();
    addParam(BLDKEY_NAME, BuilderParam.NAME_ALIAS, BuilderParamType.NAME);
  }

  /**
   * Add parameter with given key, title, type and priority.
   *
   * @param key Key of the parameter.
   * @param title Title of the parameter (display in UI).
   * @param t Type of the parameter.
   * @param priority
   */
  protected final void addParam(String key, String title, BuilderParamType t, int priority) {
    BuilderParam p = new BuilderParam(key, title, t);
    p.setPriority(priority);
    _paramsMap.put(key, p);
    _params.add(p);
    _keyOrder.add(key);
  }


  /**
   * Add parameter with given key, title, and type.
   *
   * @param key Key of the parameter.
   * @param title Title of the parameter (display in UI).
   * @param t Type of the parameter.
   */
  protected final void addParam(String key, String title, BuilderParamType t) {
    BuilderParam p = new BuilderParam(key, title, t);
    _paramsMap.put(key, p);
    _params.add(p);
    _keyOrder.add(key);
  }

  /**
   * Fix given points movement in plane.
   * @param edt
   * @param anchors
   * @param plane
   */
  protected final void fixPointsOnPlane(Editor edt, Collection<String> anchors, Plane3d plane) {
    for (String anchorID : anchors){
      try {
        String bodyID = edt.anchors().get(anchorID).getBodyID();
        i_Body b = edt.bd().get(bodyID);
        if (b.type() == BodyType.POINT) {
          PointBody pb = (PointBody)b;
          if (pb.getBehavior().type() == Behavior.FREE) {
            pb.setBehavior(BehaviorFactory.createPlaneBehavior(plane));
          }
        }
      } catch (ExNoAnchor | ExNoBody ex) {}
    }
  }

  /**
   * Get parameter value in boolean format.
   * @param param
   * @return parameter value
   */
  public boolean getValueAsBoolean(String param) {
    return (boolean)getValue(param);
  }

  /**
   * Get parameter value in double format.
   * @param param
   * @return parameter value
   */
  public double getValueAsDouble(String param) {
    return (double)getValue(param);
  }

  /**
   * Get parameter value in integer format.
   * @param param
   * @return parameter value
   */
  public int getValueAsInt(String param) {
    return (int)getValue(param);
  }

  /**
   * Get parameter value in Vect3d format.
   * @param param
   * @return parameter value
   */
  public Vect3d getValueAsVect(String param) {
    return (Vect3d)getValue(param);
  }

  /**
   * Get parameter value in String format.
   * @param param
   * @return parameter value
   */
  public String getValueAsString(String param) {
    return getValue(param).toString();
  }

  /**
   * Get type of given parameter.
   * @param param Key of the builder parameter.
   * @return
   */
  @Override
  public BuilderParamType getParamType(String param){
    return _paramsMap.get(param).type();
  }

  @Override
  public boolean hasParam(String paramKey) {
    return _paramsMap.containsKey(paramKey) &&
            (_paramsMap.get(paramKey).getValue() != null);
  }

  @Override
  public final void setValue(String param, Object value) {
    _paramsMap.get(param).setValue(value);
  }

  @Override
  public Object getValue(String param) {
    BuilderParam p = _paramsMap.get(param);
    return (p == null) ? null : p.getValue();
  }

  @Override
  public String title() {
    return (String)_paramsMap.get(BLDKEY_NAME).getValue();
  }

  @Override
  public TreeSet<BuilderParam> params() {
    return _params;
  }

  @Override
  public ArrayList<String> keys() {
    return _keyOrder;
  }

  @Override
  public String id() {
    return _id;
  }
};
