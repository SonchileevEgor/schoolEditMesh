package opengl.colortheme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import minjson.JsonObject;
import util.Log;

/**
 * Current enabled theme using in scene.
 * <br>If you want to get or set some parameters of current theme
 * you should call {@link #getColorTheme()} and
 * use methods of {@link opengl.colortheme.ColorTheme} class.
 * @author MAXbrainRUS
 */
public class CurrentTheme {
  /**
   * Listeners of theme changes.
   */
  static ArrayList<i_ColorThemeChangeListener> _themeChangeListeners;

  // Список имён файлов со сценами.
  static HashMap<Integer, String> _path;

  // Список названий сцен.
  static HashMap<Integer, String> _name;

  // Количество тем.
  static int _num;

  // ID текущей темы.
  static int _themeID;

  // Текущая тема.
  static ColorTheme _theme;

  /**
   * Инициализация класса.
   */
  public static void init() {
    File themeDir = new File(config.Config.USER_THEME_DIR);
    FilenameFilter filter = new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".th");
      }
    };
    File[] themeList = themeDir.listFiles(filter);
    _path = new HashMap<>();
    _name = new HashMap<>();
    _themeChangeListeners = new ArrayList<i_ColorThemeChangeListener>();
    _num = 0;
    for (int i = 0; i < themeList.length; i++) {
      try (InputStreamReader reader = new InputStreamReader(
            new FileInputStream(themeList[i]), StandardCharsets.UTF_8)){
        String name = JsonObject.readFrom(reader).get("name").asString();
        add(themeList[i].getName(), name);
      } catch (IOException ex) {
        Log.out.println("Ошибка чтения файла<" + themeList[i].getName() + ">");
      }
    }
    load(0);
  }

  /**
   * Загрузка темы по её номеру в списке.
   * @param id номер сцены в списке.
   */
  public static void load(int id) {
    String filename = config.Config.USER_THEME_DIR + "/" + _path.get(id);
    try (FileReader reader = new FileReader(filename)){
      ColorTheme newTheme = new ColorTheme(JsonObject.readFrom(reader));
      _themeID = id;
      _theme = newTheme;
    } catch (IOException ex) {
      Log.out.println("Тема №<" + id + "> не загружена");
    }
  }

  /**
   * Загрузка сцены по имени файла.
   * @param shortFilename
   */
  public static void load(String shortFilename) {
    for (Entry e : _path.entrySet()) {
      if (e.getValue().equals(shortFilename)) {
        load((Integer)e.getKey());
        return;
      }
    }
    Log.out.println("Файл <" + config.Config.USER_THEME_DIR + "/" + shortFilename + "> не найден");
  }

  /**
   * Загрузка следующей по порядку темы.
   */
  public static void next() {
    _themeID = (_themeID + 1) % _num;
    load(_themeID);
  }

  /**
   * Имя темы с заданным индексом в списке (для отображения в UI).
   * @param index
   * @return
   */
  public static String getName(int index) {
    assert (index >= 0);
    assert (index < _num);
    return _name.get(index);
  }

  /**
   * Имя текущей темы.
   * @return
   */
  public static String getName() {
    return _name.get(_themeID);
  }

  /**
   * Текущая цветовая схема.
   * @return
   */
  public static ColorTheme getColorTheme() {
    return _theme;
  }

  /**
   * Количество цветовых схем.
   * @return
   */
  public static int size() {
    return _num;
  }

  /**
   * Индекс в списке текущей цветовой схемы.
   * @return
   */
  public static int id() {
    return _themeID;
  }

  public static void addThemeChangeListener(i_ColorThemeChangeListener listener) {
    _themeChangeListeners.add(listener);
  }

  public static void removeThemeChangeListener(i_ColorThemeChangeListener listener) {
    _themeChangeListeners.remove(listener);
  }

  public static void clearThemeChangeListeners() {
    _themeChangeListeners.clear();
  }

  public static void notifyThemeChange() {
    for( i_ColorThemeChangeListener listener : _themeChangeListeners ){
      listener.colorThemeChanged();
    }
  }

  /**
   * Добавление новой темы в список.
   * @param path имя файла
   * @param name имя темы
   */
  private static void add(String path, String name) {
    _path.put(_num, path);
    _name.put(_num, name);
    _num++;
  }

  private CurrentTheme() {}
}
