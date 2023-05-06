package util;

import static config.Config.PRECISION;
import geom.Vect3d;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Utilities.
 *
 * @author alexeev.
 */
public class Util {
  private static final Random RANDOM = new Random(System.currentTimeMillis());

  public static final int SCENE_3D = 1;
  public static final int SCENE_2D = -1;
  public static final int SCENE_UNKNOWN = 0;
  public static final int SCENE_STL = 2;
  public static final int SCENE_PLY = 3;
  
  // Оптимизация функции valueOf преобразования в строку чисел с плавающей точкой.
  // Объект DecimalFormat создаётся один;
  // точность вывода в приложении меняется редко, 
  // поэтому при вызове функции просисходит проверка того, изменилась ли она
  // (чтобы не возводить 10 в степень при каждом вызове).
  // Оптимизация необходима, поскольку это одна из наиболее часто вызываемых функции в GUI.
  private static final DecimalFormat dFormat;
  private static int _currPrecisionSigns;
  private static double _currPrecision;
  
  static {
    _currPrecisionSigns = PRECISION.value();
    _currPrecision = Math.pow(10, -_currPrecisionSigns);
    dFormat = new DecimalFormat();
    dFormat.setMinimumIntegerDigits(1);
    dFormat.setMaximumFractionDigits(_currPrecisionSigns);
  }

  private Util() {}

  /**
   * Check whether two lists have common element.
   * @param a
   * @param b
   * @return
   */
  public static boolean joint(Collection a, Collection b) {
    return !Collections.disjoint(a, b);
  }

  /**
   * Ax + By + Cz + D = 0.
   * @param v0 - 1st point of plane
   * @param v1 - 2nd point of plane
   * @param v2 - 3rd point of plane
   * @return coef A, B, C, D by 3 points of plane
   */
  public static double[] getCoefPlane(Vect3d v0, Vect3d v1, Vect3d v2) {
      return new double[]
         {v0.y() * v1.z() - v0.y() * v2.z() - v1.y() * v0.z() + v1.y()*v2.z() + v2.y() * v0.z() - v2.y() * v1.z(),
          -v0.x() * v1.z() + v0.x() * v2.z() + v1.x() * v0.z() - v1.x() * v2.z() - v2.x() * v0.z() + v2.x() * v1.z(),
          v0.x() * v1.y() - v0.x() * v2.y() - v1.x() * v0.y() + v1.x() * v2.y() + v2.x() * v0.y() - v2.x() * v1.y(),
          v0.x() * v1.y() * v2.z() + v0.x() * v1.z() * v2.y() + v1.x() * v0.y() * v2.z() - v1.x() * v2.y() * v0.z()
              - v2.x() * v0.y() * v1.z() + v2.x() * v1.y() * v0.z()};
  }

  /**
   * Ax + By + Cz + D = 0.
   * @param points of plane
   * @return coef A, B, C, D by 3 points of plane
   */
  public static double[] getCoefPlane(ArrayList<Vect3d> points) {
      if (points.size() == 3)
        return getCoefPlane(points.get(0), points.get(1), points.get(2));
      else return null;
  }
  
  /**
   * Компаратор, сравнивающий пары "ID - точка" по расстоянию до "глаза"
   * — фиксированной точки в пространстве.
   * Note: this comparator imposes orderings that are inconsistent with equals.
   * @param eye
   * @return 
   */
  public static Comparator<Map.Entry<String, Vect3d>> getComparator(final Vect3d eye) {
    return new Comparator<Map.Entry<String, Vect3d>>() {
      @Override
      public int compare(Map.Entry<String, Vect3d> o1, Map.Entry<String, Vect3d> o2) {
        return Double.compare(Vect3d.dist(o1.getValue(), eye), Vect3d.dist(o2.getValue(), eye));
      }
    };
  }
  
  /**
   * Поиск ближайшей точке к камере из списка.
   * @param points
   * @param eye
   * @return ближайшая точка или null, если список пуст.
   */
  public static Vect3d getClosestPointToCamera(Collection<Vect3d> points, final Vect3d eye) {
    Comparator<Vect3d> cmp = new Comparator<Vect3d>() {
      @Override
      public int compare(Vect3d o1, Vect3d o2) {
        return Double.compare(Vect3d.dist(eye, o1), Vect3d.dist(eye, o2));
      }
    };
    try {
      return Collections.min(points, cmp);
    } catch( NoSuchElementException ex ){
      return null;
    }
  }
  
  /**
   * Check whether array has duplicate elements.
   * O(n<sup>2</sup>) algorithm.
   * @param a
   */
  public static boolean hasDuplicates(Object[] a) {
    for( int i = 0; i < a.length - 1; i++ ){
      for( int j = i + 1; j < a.length; j++ ){
        if( a[i].equals(a[j]) )
          return true;
      }
    }
    return false;
  }

  /**
   * Get indices of repeating elements.
   * O(n<sup>2</sup>) algorithm.
   *
   * If repeating elements don't exist, return empty array.
   * @param a
   * @return
   */
  public static ArrayList<Integer> getDuplicates(Object[] a) {
    Object dup = null;
    int secondIndex = -1;
    ArrayList<Integer> result = new ArrayList<>();
    outer:
    for( int i = 0; i < a.length - 1; i++ ){
      for( int j = i + 1; j < a.length; j++ ){
        if( a[i].equals(a[j]) ){
          dup = a[i];
          result.add(i);
          result.add(j);
          secondIndex = j;
          break outer;
        }
      }
    }
    if( secondIndex != -1 ){
      // repeating elements were found
      for( int i = secondIndex + 1; i < a.length; i++ ){
        if( a[i].equals(dup) ){
          result.add(i);
        }
      }
    }
    return result;
  }

  /**
   * Check whether array has duplicate elements.
   * @param compare  if compare[i] == false, we aren't compare i-th element.
   * @param a
   */
  public static boolean hasDuplicates(Object[] a, boolean[] compare) {
    for( int i = 0; i < a.length - 1; i++ ){
      for( int j = i + 1; j < a.length; j++ ){
        if( a[i].equals(a[j]) && compare[i] && compare[j] )
          return true;
      }
    }
    return false;
  }

  /**
   * Concatenate array of strings.
   * @param s
   * @return
   */
  public static String concat(String[] s) {
    StringBuilder builder = new StringBuilder();
    for (String item : s) {
      builder.append(item);
    }
    return builder.toString();
  }

  /**
   * Округление числа <code>value</code>
   * с точностью до <code>roundindStep</code>
   * @param value
   * @param roundingStep
   * @return
   */
  public static double round(double value, double roundingStep) {
    assert roundingStep > 0;
    return Math.round(value / roundingStep) * roundingStep;
  }

  /**
   * Set dialog location at the center of given frame.
   * If frame is null, set at the center of screen.
   * @param fr frame
   * @param d dialog
   */
  public static final void setCenterLocation(Component fr, Component d) {
    Dimension parentSize = (fr == null) ?
        Toolkit.getDefaultToolkit().getScreenSize() : fr.getSize();
    int left_X = (fr == null) ? 0 : fr.getX();
    int top_Y = (fr == null) ? 0 : fr.getY();
    d.setLocation((parentSize.width - d.getWidth()) / 2 + left_X,
                (parentSize.height - d.getHeight()) / 2 + top_Y);
  }

  public static final int getScreenWidth(){
    return (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
  }

  public static final int getScreenHeight(){
    return (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
  }

  /**
   * Format real value with given precision.
   *
   * @param value
   * @param precision
   * @return
   */
  public static final String valueOf(double value, int precision) {
    if( _currPrecisionSigns != precision ){
      _currPrecisionSigns = precision;
      _currPrecision = Math.pow(10, -precision);
      dFormat.setMaximumFractionDigits(precision);
    }
    
    // workaround for preventing "-0" or "-0.00" results
    if( Math.abs(value) < _currPrecision)
      value = 0.0;
    
    String result = dFormat.format(value);
    
    return result;
  }

  /**
   * Remove suffix "_[0-9]+".
   * @param str
   * @return
   */
  public static final String removeSuffix(String str) {
    String[] split = str.split("_");
    if (split.length == 1)
      return str;
    String suffix = split[split.length - 1];
    if (suffix.matches("[0-9]+")) {
      return str.substring(0, str.length() - suffix.length() - 1);
    } else {
      return str;
    }
  }

  /**
   * Check if string has numeric suffix "_[0-9]+".
   * @param str
   * @return
   */
  public static boolean hasNumericSuffix(String str) {
    String[] split = str.split("_");
    if (split.length == 1)
      return false;
    return (split[split.length - 1].matches("[0-9]+"));
  }

  /**
   * Get randomly generated string of given length.
   * @param length
   * @return
   */
  public static final String getRandomString(int length) {
    char[] data = new char[length];
    for (int i = 0; i < length; i++) {
      data[i] = (char)(RANDOM.nextInt(26) + 97);
    }
    return String.copyValueOf(data);
  }

  /**
   * Localize UI.
   */
  public static final void localize() {
    Locale.setDefault(Locale.forLanguageTag("ru"));/*
//    UIManager.put("ColorChooser.background
    UIManager.put("ColorChooser.cancelText", "Отмена");
//    UIManager.put("ColorChooser.font
//    UIManager.put("ColorChooser.foreground
    UIManager.put("ColorChooser.hsbBlueText", "Синий");
    UIManager.put("ColorChooser.hsbBrightnessText", "Яркость");
    UIManager.put("ColorChooser.hsbGreenText", "Зелёный");
    UIManager.put("ColorChooser.hsbHueText", "Тон");
//    UIManager.put("ColorChooser.hsbMnemonic", "");
    UIManager.put("ColorChooser.hsbNameText", "hsbNameText");
    UIManager.put("ColorChooser.hsbRedText", "Красный");
    UIManager.put("ColorChooser.hsbSaturationText", "Насыщенность");
    UIManager.put("ColorChooser.okText", "OK");
    UIManager.put("ColorChooser.previewText", "Просмотр");
//    UIManager.put("ColorChooser.resetMnemonic", "Сброс");
    UIManager.put("ColorChooser.resetText", "Сброс");
    UIManager.put("ColorChooser.rgbBlueText", "Синий");
    UIManager.put("ColorChooser.rgbGreenMnemonic", "Жопа");
    UIManager.put("ColorChooser.rgbGreenText", "Зелёный");
    UIManager.put("ColorChooser.rgbNameText", "RGB");
    UIManager.put("ColorChooser.rgbRedText", "Красный");
    UIManager.put("ColorChooser.rgbAlphaText", "Прозрачность");
    UIManager.put("ColorChooser.sampleText", "Sch3Dedit");
    UIManager.put("ColorChooser.swatchesNameText", "Палитра");
//    UIManager.put("ColorChooser.swatchesRecentSwatchSize
    UIManager.put("ColorChooser.swatchesRecentText", "Недавние цвета");
//    UIManager.put("ColorChooser.swatchesSwatchSize*/
  }

  public static int isPLY(String fname) {
      if (fname.endsWith(".ply")) {
        return SCENE_PLY;
      } else if (fname.endsWith(".stl")) {
          return SCENE_STL;
      } else {
          return SCENE_UNKNOWN;
      }
  }
  /**
   * Check is file with given name is 2D or 3D scene.
   * @param fname
   * @return
   */
  public static int is3d(String fname) {
    if( fname.endsWith(".sc2") ){
      return SCENE_2D;
    } else if( fname.endsWith(".sc3") ){
      return SCENE_3D;
    } else {
      return SCENE_UNKNOWN;
    }
  }
}
