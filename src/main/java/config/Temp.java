package config;

/**
 * Temporary configuration options.
 * 
 * @author alexeev
 */
public class Temp {
  // Flag shows whether current editor state was saved.
  public static boolean FILE_SAVED = false;
  // Flag shows whether current config file was saved
  public static boolean CONFIG_SAVED = false;
  // ID of color theme
  public static String COLOR_THEME = "user";
  // Path to the opened file.
  public static String CURRENT_FILE = "";
  // FileChooser's start observing directory.
  public static String SEARCH_DIR = System.getProperty("user.dir");
}
