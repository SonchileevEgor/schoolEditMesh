package opengl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import geom.Vect3d;
import opengl.colorgl.ColorGL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The class for finding of polygons located in the same plane.
 *
 * <p>Проблема в том, что при рисовании в OpenGL нескольких полигонов, находящихся в одной плоскости,
 * при включенном тесте глубины, эти полигоны перекрывают друг друга частично
 * (перекрытие зависит от погрешности в вычислениях).
 *
 * <p>Для устранения этого эффекта и служит данный класс.
 * Он детектирует полигоны, находящиеся в одной плоскости, и группирует их.
 * С помощью функции {@link #getOverlappedPolygons()} можно получить потенциально перекрытые полигоны
 * в порядке их добавления функцией {@link #addPolygon(ArrayList, Render)}.
 *
 * <p><b>Использование:</b>
 * <ol>
 *   <li>Добавляем информацию о всех полигонах с помощью команды {@link #addPolygon(ArrayList, Render)}</li>
 *   <li>С помощью функции {@link #getOverlappedPolygons()} получаем потенциально перекрытые полигоны
 *   в порядке добавления, сгруппированные по плоскостям</li>
 *   <li>После перестроения сцены необходимо выполнить команду {@link #reset()},
 *   чтобы исключить старые полигоны из рассмотрения</li>
 * </ol>
 *
 * @see #isOverlapped(ArrayList, Render)
 * @see #setEnabled(boolean)
 * @see #setReadOnly(boolean)
 */
public class PlaneManager {

  /**
   * Точность, при которой плоскости считаются одинаковыми. Имеет значение 10^число_разрядов
   */
  static private int accuracy = 10000;
  private boolean enabled = true;
  /**
   * Режим чтения: в режиме чтения функция {@link #addPolygon(ArrayList, Render)} игнорируется,
   * а функция {@link #isOverlapped(ArrayList, Render)} работает в стандартном режиме
   */
  private boolean readOnly = false;
  private Multimap<PlaneEquation, DrawingPolygon> equationHashMap = ArrayListMultimap.create();

  /**
   * Получить состояние режима "только для чтения"
   * @return Состояние режима "только для чтения"
   * @see #readOnly
   */
  public boolean isReadOnly() {
    return readOnly;
  }

  /**
   * Установить состояние режима "только для чтения"
   *
   * <p>Режим чтения: в режиме чтения функция {@link #addPolygon(ArrayList, Render)} игнорируется,
   * а функция {@link #isOverlapped(ArrayList, Render)} работает в стандартном режиме
   *
   * @param readOnly true - включить, false - выключить.
   * @see #readOnly
   */
  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  /**
   * Получить список потенциально перекрытых полигонов.
   *
   * Сгруппированы по основополагающей плоскости, для каждой выводится список полигонов,
   * находящихся в этой плоскости в порядке добавления
   * @return Список потенциально перекрытых полигонов.
     */
  public List<List<DrawingPolygon>> getOverlappedPolygons() {
    List<List<DrawingPolygon>> res = new ArrayList<>();
    for (Map.Entry<PlaneEquation, Collection<DrawingPolygon>> e : equationHashMap.asMap().entrySet()) {
      if (e.getValue().size() > 1) {
        res.add(new ArrayList<>(e.getValue()));
      }
    }
    return res;
  }

  /**
   * Узнать, находится ли полигон в списке потенциально перекрытых.
   * Работат только когда включен ({@link #isEnabled()}) и включен в режиме "только для чтения" {@link #isReadOnly()}
   * @param points Полигон как набор точек
   * @param ren
   * @return true, если полигон в списке потенциально перекрытых и выставлен нужный режим, иначе false
   */
  public boolean isOverlapped(ArrayList<Vect3d> points, Render ren) {
    if (!enabled)
      return false;
    if (!readOnly)
      return false;
    int size = points.size();
    Vect3d normal = Vect3d.getNormal3Points(points.get(0), points.get(size / 3), points.get(2 * size / 3));
    Vect3d norm = Vect3d.getNormalizedVector(normal);
    PlaneEquation plane = calcPlaneEquation(norm, points.get(0), ren);
    return equationHashMap.get(plane).size() > 1;
  }

  /**
   * Возвращает, включен ли режим отслеживания полигонов в одной плоскости.
   * <br>Если выключен, то функция {@link #addPolygon(ArrayList, Render)} не добавляет полигон
   * в рассмотрение, а функция {@link #isOverlapped(ArrayList, Render)} возвращает false.
   *
   * @return Включен ли режим отслеживания полигонов в одной плоскости.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Включение и выключение режима отслеживания полигонов в одной плоскости.
   * <br>Если выключен, то функция {@link #addPolygon(ArrayList, Render)} не добавляет полигон
   * в рассмотрение, а функция {@link #isOverlapped(ArrayList, Render)} возвращает false.
   *
   * @param enabled true - включить, false - выключить.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Добавить очередной полигон на рассмотрение.
   * Вызов игнорируется, если выключен или включен режим "только для чтения"
   *
   * @param points Полигон как набор точек
   * @param ren
   */
  public void addPolygon(ArrayList<Vect3d> points, Render ren) {
    if (readOnly || !enabled)
      return;
    int size = points.size();
    Vect3d normal = Vect3d.getNormal3Points(points.get(0), points.get(size / 3), points.get(2 * size / 3));
    Vect3d norm = Vect3d.getNormalizedVector(normal);
    PlaneEquation plane = calcPlaneEquation(norm, points.get(0), ren);

    DrawingPolygon drawingPolygon = new DrawingPolygon();
    try {
      drawingPolygon.color = Drawer.getCurrentColor(ren);
      drawingPolygon.transformMatrix = Drawer.getTrasformMatrix(ren).copy();
    } catch (NullPointerException e) {
      drawingPolygon.color = new ColorGL();
      drawingPolygon.transformMatrix = new TransformMatrix();
    }
    drawingPolygon.points = points;
    equationHashMap.put(plane, drawingPolygon);
  }

  /**
   * Удалить старые полигоны из рассмотрения
   */
  public void reset(){
    equationHashMap.clear();
  }

  /**
   * Get plane equation from normal vector and point with using {@link #accuracy}
   * @param normal Normal vector of polygon
   * @param point Any point of polygon
   * @return Equation of the plane on which the polygon located
   */
  private PlaneEquation calcPlaneEquation(Vect3d normal, Vect3d point, Render ren){
    PlaneEquation res = new PlaneEquation();
    Vect3d normNormal = Vect3d.getNormalizedVector(normal);

    // Т к некоторые полигоны рисуются с использованием видовой трансформации, необходимо
    // применить видовую трасформацию для получения реальных координат полигона на сцене
    Matrix4 modelMatrix;
    try {
      modelMatrix = Drawer.getTrasformMatrix(ren).getModelExt();
    } catch (NullPointerException e) {
      modelMatrix = new Matrix4();
    }
    normNormal = modelMatrix.mulOnVect(normNormal, 0);
    point = modelMatrix.mulOnVect(point, 1);
    double offset =
            normNormal.x() * point.x() +
            normNormal.y() * point.y() +
            normNormal.z() * point.z();

    int xRound = (int)(normNormal.x() * accuracy);
    int yRound = (int)(normNormal.y() * accuracy);
    int zRound = (int)(normNormal.z() * accuracy);
    int offsetRound = (int)(offset * accuracy);

    res.setAll(xRound, yRound, zRound, offsetRound);

    // Учитываем, что плоскости с нормалью n и -n одинаковые
    if (xRound < 0){
      res.multiply(-1);
    }
    else if (xRound == 0){
      if (yRound < 0){
        res.multiply(-1);
      }
      else if (yRound == 0){
        if (zRound < 0)
          res.multiply(-1);
      }
    }
    return res;
  }

}

/**
 * Class for storing equation of plane.
 * equals() and hashCode() are needed for usage of class as HashMap key
 */
class PlaneEquation {
  int normalX;
  int normalY;
  int normalZ;
  int offset;

  void setAll(int x, int y, int z, int offset){
    setNormal(x, y, z);
    setOffset(offset);
  }

  void setNormal(int x, int y, int z){
    normalX = x;
    normalY = y;
    normalZ = z;
  }
  void setOffset(int offset){
    this.offset = offset;
  }
  void multiply(int mul){
    normalX *= mul;
    normalY *= mul;
    normalZ *= mul;
    offset *= mul;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public String toString() {
    return Integer.toString(normalX) + " " +
            Integer.toString(normalY) + " " +
            Integer.toString(normalZ) + " " +
            Integer.toString(offset);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    
    if (o.getClass() != this.getClass())
      return false;
    
    PlaneEquation plane = (PlaneEquation)o;
    return plane.normalX == this.normalX &&
            plane.normalY == this.normalY &&
            plane.normalZ == this.normalZ &&
            plane.offset == this.offset;
  }
}

