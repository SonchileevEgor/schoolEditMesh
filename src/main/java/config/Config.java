package config;

import geom.Checker;
import java.awt.Font;
import java.io.*;

import java.awt.Color;
import java.awt.Dimension;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.swing.UIManager;
import minjson.JsonObject;
import minjson.JsonValue;
import opengl.colortheme.CurrentTheme;
import opengl.textdrawinggl.TextDrawingType;
import util.FileUtils;
import util.Log;

public class Config {
  // Json-объект главного файла конфигурации.
  private static JsonObject _cfg;
  
  // Use licensed access
  public static boolean USE_LICENSE = false;
  
  /**
   * Project properties.
   * It is needed to parse project properties, like version.
   */
  public static Properties PROPERTIES;

  /**
   * PATH PARAMETERS.
   */
  // Main class location.
  public static String HOME = new File(System.getProperty("user.dir")).getAbsolutePath();
  // User directory
  public static String USER_DIR = FileUtils.getAbsPath(System.getProperty("user.home"));
  // Project directory
  public static String PROJECT_DIR = FileUtils.joinPath(USER_DIR, ".3dschooledit/");
  // User cache dir
  public static String CACHE_DIR = FileUtils.joinPath(PROJECT_DIR, "cache");
  // Log directory
  public static String LOG_DIR = FileUtils.joinPath(PROJECT_DIR, "logs");
  // Temporary directory
  public static String TMP_DIR = FileUtils.joinPath(PROJECT_DIR, "tmp");
  // Directory with themes
  // Source themes dir
  public static String PROJECT_THEME_DIR = FileUtils.joinPath(HOME, "themes");
  // User themes dir
  public static String USER_THEME_DIR = FileUtils.joinPath(PROJECT_DIR, "themes");
  // Configuration file.
  // Source config file
  public static String PROJECT_CONFIG_FILE = FileUtils.joinPath(HOME, "config");
  // User config file
  public static String USER_CONFIG_FILE = FileUtils.joinPath(PROJECT_DIR, "config");
  // Directory with optional configuration files
  public static String OPT_CONFIG_DIR = FileUtils.joinPath(PROJECT_DIR, "userconf");
  // Source icons folder
  public static String PROJECT_ICONS_DIR = FileUtils.joinPath(HOME, "icons");
  // User icons folder
  public static String USER_ICONS_DIR = FileUtils.joinPath(PROJECT_DIR, "icons");
  // License key file location
  public static String LICENSE_INFO_FILE = FileUtils.joinPath(PROJECT_DIR, ".license");
  // File with last opened scenes paths
  public static String LAST_OPENED_SCENES_FILE = FileUtils.joinPath(CACHE_DIR, ".last_opened");

  // Пользовательский файл конфигурации
  public static ConfigParam<String> OPTIONAL_CONFIG = ConfigParamFactory.getStringParam(
          "Пользовательский файл конфигурации", "userconf", true, "");
  
  // Точность вычислений
  public static ConfigParam<Double> EPS =
          new ConfigParam<Double>("Точность вычислений", "geom.eps", false, 0.000001){
    @Override
    public void fromJson(JsonValue v) {
      _value = v.asDouble();
    }

    @Override
    public JsonValue toJson() {
      return JsonValue.valueOf(_value);
    }

    @Override
    public void apply() {
      Checker.setEps(_value);
    }
  };
  
  // Address of license server.
  public static ConfigParam<String> LICENSE_SERVER_ADDRESS = ConfigParamFactory.getStringParam(
          "Сервер линецзий", "license", false, "83.220.171.81");
  
  // Default font for plain text.
  public static ConfigParam<Font> DEFAULT_FONT = ConfigParamFactory.getFontParam(
          "Шрифт", "gui.font", true, new Font("Arial", Font.PLAIN, 10));
  
  // Font size for scene labels.
  public static ConfigParam<Integer> MATH_FONT_SIZE = ConfigParamFactory.getDecimalParam(
          "Размер шрифта обозначений", "gui.math_fontsize", true, 20);
  
  // GUI mouse position error on user selection of objects.
  public static ConfigParam<Integer> GUI_EPS = ConfigParamFactory.getDecimalParam(
          "Допуск мыши", "gui.eps", true, 10);
  
  // Шаг изменения угла в режимах построения объектов.
  public static ConfigParam<Double> ANGLE_STEP = ConfigParamFactory.getDoubleParam(
          "Шаг изменения угла", "gui.angle_step", true, Math.PI / 36);
          
  // Точность выводимых значений (количество знаков после запятой).
  public static ConfigParam<Integer> PRECISION = ConfigParamFactory.getDecimalParam(
          "Точность значений", "gui.precision", true, 5);
  
  // Показать / скрыть имена точек.
  public static ConfigParam<Boolean> POINT_TITLE_VISIBLE = ConfigParamFactory.getBooleanParam(
          "Видимость обозначений точек", "gui.point_name", true, true);
  
  // On / off screen keyboard (for interactive boards).
  public static ConfigParam<Boolean> SCREEN_KEYBOARD_ON = ConfigParamFactory.getBooleanParam(
          "Экранная клавиатура", "gui.screen_keyboard", true, false);
  
  // On / off splash screen.
  public static ConfigParam<Boolean> SPLASH_SCREEN_ON = ConfigParamFactory.getBooleanParam(
          "Splash-screen", "gui.splash", true, true);
  
  // Logging level.
  public static ConfigParam<Integer> LOG_LEVEL = ConfigParamFactory.getDecimalParam(
          "Частота записи в журнал событий", "log.level", true, 0);
  
  //!! TODO: отделить локальные параметры сцены от глобальных.
  // Show / hide axes
  public static ConfigParam<Boolean> AXES_VISIBLE = ConfigParamFactory.getBooleanParam(
          "Показать оси", "gui.axes", true, true);
  
  // On / off grid visibility.
  public static ConfigParam<Boolean> GRID_VISIBLE = ConfigParamFactory.getBooleanParam(
          "Показать сетку", "gui.grid", true, true);
  
  // Grid intensity.
  public static ConfigParam<Double> GRID_INTENSITY = ConfigParamFactory.getDoubleParam(
          "Показать сетку", "gui.grid_intensity", true, 0.9);
  
  // Show view volume cube.
  public static ConfigParam<Boolean> VIEW_VOLUME_VISIBLE = ConfigParamFactory.getBooleanParam(
          "Показать сетку", "gui.view_volume", true, false);
  
  // On / off anti-aliasing
  public static ConfigParam<Boolean> ANTIALIASING_ON = ConfigParamFactory.getBooleanParam(
          "Сглаживание", "gui.antialiasing", true, true);
  
  // Diameter of points
  public static ConfigParam<Float> POINT_WIDTH = ConfigParamFactory.getFloatParam(
          "Диаметр точек", "gui.point_width", true, 3f);
  
  // Width of lines of carcasses of figures in pixels
  public static ConfigParam<Float> LINE_WIDTH = ConfigParamFactory.getFloatParam(
          "Толщина линий", "gui.line_width", true, 1f);
  
  // Width of lines of carcasses of figures when their drawing with stipple in pixels
  public static ConfigParam<Float> STIPPLE_LINE_WIDTH = ConfigParamFactory.getFloatParam(
          "Толщина пунктира", "gui.stipple_line_width", true, 0.5f);
  
  // Width of lines of initial plane in pixels
  public static ConfigParam<Float> INITIAL_GRID_LINE_WIDTH = ConfigParamFactory.getFloatParam(
          "Толщина линий сетки", "gui.initial_plane_width", true, 0.5f);
  
  // Default checkbox size
  public static ConfigParam<Dimension> DEFAULT_CHECKBOX_SIZE = ConfigParamFactory.getDimensionParam(
                  "Размер чекбокса", "gui.checkbox_size", true, new Dimension(20, 20));

  // Selects the type of technology used when drawing text on point
  public static TextDrawingType TEXT_DRAWING_TYPE_ON_POINT = TextDrawingType.TEXTURE;
  
  // Selects the type of technology used when drawing text on segment
  public static TextDrawingType TEXT_DRAWING_TYPE_ON_SEGMENT = TextDrawingType.TEXTURE;
  
  // Amount of rib slices, the ends of which will project points of the cover
  public static ConfigParam<Integer> MAQUETTE_RIB_SLICES = ConfigParamFactory.getDecimalParam(
                  "Горизонтальная детализация рёбер 3D-модели", "maquettes.ribSlices", true, 99);
  
  // Amount of points, which will project around of one rib division
  public static ConfigParam<Integer> MAQUETTE_RIB_PHASES = ConfigParamFactory.getDecimalParam(
                  "Круговая детализация рёбер 3D-модели", "maquettes.ribPhases", true, 120);
  
  // Meridians and parallels are determined a generation points around vertice on the principle of the globe
  public static ConfigParam<Integer> MAQUETTE_VERTICE_MERIDIANS = ConfigParamFactory.getDecimalParam(
          "Детализация вершин 3D-модели (меридианы)", "maquettes.verticeMeridians", true, 120);

  public static ConfigParam<Integer> MAQUETTE_VERTICE_PARALLELS = ConfigParamFactory.getDecimalParam(
          "Детализация вершин 3D-модели (параллели)", "maquettes.verticeParallels", true, 120);
  
  // Minimal thickness of 3d-model, which can be printed on 3d-printer
  public static ConfigParam<Double> MAQUETTE_MIN_PRINT_THICKNESS = ConfigParamFactory.getDoubleParam(
          "Минимальная точность печати", "maquettes.minPrintThickness", true, 0.1);
  
  // Switched-on extensions of editor.
  //!! TODO: сделать так, чтобы обновление переменной было синхронизировано с менеджером
  // EdtController.getExtManager()
  public static ConfigParam<JsonValue> EXTENSIONS = ConfigParamFactory.getJsonParam(
          "Расширения", "extensions", true, new JsonObject());
  
  private static ConfigParam[] params = new ConfigParam[]{
    LICENSE_SERVER_ADDRESS,
    LOG_LEVEL,
    AXES_VISIBLE, GRID_VISIBLE, GRID_INTENSITY, VIEW_VOLUME_VISIBLE, ANTIALIASING_ON,
    EPS, DEFAULT_FONT, MATH_FONT_SIZE, GUI_EPS, ANGLE_STEP, PRECISION, POINT_TITLE_VISIBLE,
    SCREEN_KEYBOARD_ON, SPLASH_SCREEN_ON, POINT_WIDTH, LINE_WIDTH,
    INITIAL_GRID_LINE_WIDTH, STIPPLE_LINE_WIDTH,
    MAQUETTE_RIB_SLICES, MAQUETTE_RIB_PHASES, MAQUETTE_VERTICE_MERIDIANS,
    MAQUETTE_VERTICE_PARALLELS, MAQUETTE_MIN_PRINT_THICKNESS,
    EXTENSIONS
  };

  /**
   * Initialize application configuration.
   */
  public static void init() {
    // Create user directory
    FileUtils.mkdirs(PROJECT_DIR);
    
    // Create optional configuration files directory
    FileUtils.mkdirs(OPT_CONFIG_DIR);

    // Create cache directory in user directory
    FileUtils.mkdirs(CACHE_DIR);

    // Create log directory in user directory
    FileUtils.mkdirs(LOG_DIR);
    
    
    // Check "clean" command line parameter
    if( "clean".equals(System.getProperty("startMode", "normal")) ){
      if( FileUtils.exists(USER_CONFIG_FILE) ){
        new File(USER_CONFIG_FILE).delete();
        Log.out.println("Clean mode on. Default configuration file removed.");
      }
    }

    try {
      // Copy themes from projects directory to user directory
      FileUtils.copyDir(PROJECT_THEME_DIR, USER_THEME_DIR);

      // Copy icons from projects directory to user directory
      FileUtils.copyDir(PROJECT_ICONS_DIR, USER_ICONS_DIR);

      // Copy config from projects directory to user directory
      FileUtils.copy(PROJECT_CONFIG_FILE, USER_CONFIG_FILE);
    } catch (IOException ex) {
      util.Fatal.error("can't write into user home directory <" + ex.getMessage() + ">");
    }

    try (InputStreamReader reader = new InputStreamReader(
            new FileInputStream(USER_CONFIG_FILE), StandardCharsets.UTF_8)){
      _cfg = JsonObject.readFrom(reader);
      
      loadTheme(_cfg.get("theme").asString());
      
      // Загружаем изменяемые параметры из базового файла конфигурации.
      loadParams(_cfg);
      
      // Читаем имя файла с пользовательской конфигурацией
      readParam(_cfg, OPTIONAL_CONFIG);
      
      // Если есть опциональный пользовательский файл конфигурации, то загружаем и его.
      if( !OPTIONAL_CONFIG.value().equals("") ) {
        String optionalConfigPath =
                FileUtils.joinPath(OPT_CONFIG_DIR, OPTIONAL_CONFIG.value()) + ".cfg";
        try (InputStreamReader optReader = new InputStreamReader(
            new FileInputStream(optionalConfigPath), StandardCharsets.UTF_8)){
          JsonObject optionalConfig = JsonObject.readFrom(optReader);
          
          // Загружаем изменяемые параметры из базового файла конфигурации.
          loadParams(optionalConfig);
        } catch( IOException ex ){
          Log.out.printf("Configuration file %s is absent\n", OPTIONAL_CONFIG.value());
        }
      }
      
      readUIColors(_cfg.get("uicolors").asObject());
      readUIStrings(_cfg.get("uistrings").asObject());
    } catch(IOException ex){
      util.Fatal.error("can't open config <" + ex.getMessage() + ">");
    }
    
    applyParams();
    // загружаем свойства приложения из properties-файла.
    loadProperties();
  }
  
  /**
   * Возвращаем установки по умолчанию.
   */
  public static void toDefault(){
    loadParams(_cfg);
  }
  
  /**
   * Загружаем параметры из файла.   
   * @param confFile
   * @return 
   */
  public static boolean loadOptionalParams(File confFile) {
    try (InputStreamReader optReader = new InputStreamReader(
        new FileInputStream(confFile), StandardCharsets.UTF_8)){
      JsonObject optionalConfig = JsonObject.readFrom(optReader);

      // Загружаем изменяемые параметры из базового файла конфигурации.
      loadParams(optionalConfig);
      return true;
    } catch( IOException ex ){
      Log.out.printf("Configuration file %s is absent\n", OPTIONAL_CONFIG.value());
      return false;
    }
  }
  
  /**
   * Сохранение пользовательских параметров.
   * @param confName
   *    название файла конфигурации (без расширения), в который будет производиться сохранение.
   * @return 
   *    статус сохранения (если удалось - true, иначе - false).
   */
  public static boolean saveOptionalParams(String confName) {
    JsonObject cfg = new JsonObject();
    for( ConfigParam p : params ){
      if( p.mutable )
        writeParam(cfg, p);
    }
    String optionalConfigPath =
            FileUtils.joinPath(OPT_CONFIG_DIR, confName) + ".cfg";
    try (OutputStreamWriter writer = new OutputStreamWriter(
            new FileOutputStream(optionalConfigPath), StandardCharsets.UTF_8)){
      cfg.writeTo(writer);
    } catch(IOException ex) {
      Log.out.printf("Cannot write to config file <%s>\n", OPTIONAL_CONFIG.value());
      return false;
    }
    return true;
  }

  /**
   * Изменяем пользовательскую конфигурацию,
   * пишем название новой конфигурации в главный файл.
   * @param confName 
   * @return  
   *    статус сохранения (если удалось - true, иначе - false).
   */
  public static boolean changeUserconf(String confName) {
    OPTIONAL_CONFIG.setValue(confName);
    writeParam(_cfg, OPTIONAL_CONFIG);
    
    try (OutputStreamWriter writer = new OutputStreamWriter(
            new FileOutputStream(USER_CONFIG_FILE), StandardCharsets.UTF_8)){
      _cfg.writeTo(writer);
    } catch(IOException ex) {
      Log.out.printf("Cannot write to config file <%s>\n", OPTIONAL_CONFIG.value());
      return false;
    }
    return true;
  }

  /**
   * Считываем параметр конфигурации из конфигурационного файла.
   * Путь в иерархии записан в ConfigParam.alias
   * Если по данному пути ничего не лежит, то завершаем метод,
   * иначе изменяем значение параметра.
   * @param js
   * @param p 
   */
  private static void readParam(JsonObject js, ConfigParam p) {
    String[] path = p.alias.split(Pattern.quote("."));
    JsonValue val = js;
    for( String level : path ){
      if( val == null ){
        Log.out.printf("Parameter %s is absent\n", p.alias);
        return;
      }
      val = val.asObject().get(level);
    }
    
    if( val != null ){
      p.fromJson(val);
    }
  }
  
  private static void loadParams(JsonObject js) {
    for( ConfigParam p : params ){
      readParam(js, p);
    }
  }
  
  /**
   * Применить все параметры.
   * @param js 
   */
  private static void applyParams() {
    for( ConfigParam p : params ){
      p.apply();
    }
  }
  
  private static void loadTheme(String themeName) {
    CurrentTheme.init();
    CurrentTheme.load(themeName + ".th");
  }

  private static void readUIColors(JsonObject js) {
    for (String key : js.names()){
      JsonObject color = js.get(key).asObject();
      UIManager.put(key, new Color(color.get("r").asFloat(),
                                   color.get("g").asFloat(),
                                   color.get("b").asFloat()));
    }
  }

  private static void readUIStrings(JsonObject js) {
    for (String key : js.names()){
      UIManager.put(key, js.get(key).asString());
    }
  }
  
  /**
   * Load project properties from resources folder.
   * Properties file has filtered with maven build plugin,
   * so we can import into code maven project properties:
   * versions, names of artifacts etc.
   */
  private static void loadProperties() {
    ClassLoader classLoader = Config.class.getClassLoader();
    PROPERTIES = new Properties();
    try(InputStream stream = classLoader.getResourceAsStream(".properties")) {
      PROPERTIES.load(stream);
    } catch(IOException ex){
      Log.out.print("Properties file not loaded!\n");
    }
  }
   
  /**
   * Пишем параметр конфигурации в конфигурационный файл.
   * Путь в иерархии записан в ConfigParam.alias.
   * Если пути в Json-объекте нет, то создаём его.
   * @param js
   * @param p 
   */
  private static void writeParam(JsonObject js, ConfigParam p) {
    String[] path = p.alias.split(Pattern.quote("."));
    JsonObject curr_obj = js;
    String level;
    
    // создаём иерархию в Json-файле.
    for( int i = 0; i < path.length - 1; i++ ){
      level = path[i];
      if( !curr_obj.names().contains(level) ){
        curr_obj.add(level, new JsonObject());
      }
      curr_obj = curr_obj.get(level).asObject();
    }
    String lastLevel = path[path.length - 1];
    curr_obj.set(lastLevel, p.toJson());
  }
}
