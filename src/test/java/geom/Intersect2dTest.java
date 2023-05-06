package geom;

import static geom.Line3d.getLineByTwoPoints;
import static geom.Line3d.line3dByTwoPoints;
import static geom.Ray3d.ray3dByTwoPoints;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author alexeev
 */
public class Intersect2dTest {

  private i_Geom _line1;
  private i_Geom _line2;
  private i_Geom _rib1;
  private i_Geom _rib2;

  @BeforeEach
  public void setUp() throws ExDegeneration, ExGeom, ExZeroDivision {
    Vect3d p1 = new Vect3d(0, 0, 0);
    Vect3d p2 = new Vect3d(1, 0, 0);
    Vect3d p3 = new Vect3d(0, 1, 0);
    Vect3d p4 = new Vect3d(1, 1, 0);
    _line1 = Line3d.getLineByTwoPoints(p1, p4);
    _line2 = Line3d.getLineByTwoPoints(p2, p3);
    _rib1 = new Rib3d(p1, p4);
    _rib2 = new Rib3d(p2, p3);

    Vect3d v1;
    Vect3d v2;
    Vect3d v3;
    Vect3d v4;
    Rib3d rib1;
    Rib3d rib2;
    Line3d line[];
    Ray3d ray[];
    ArrayList<Vect3d> answer = new ArrayList<>();
  }

  // Все тетсируемые функции ищут пересечение кривой с кривой.
  // Причем если перечение проходит через одну из уже существующих точек, наприме, вершину многоугольника,
  // то эта точка в список не добавляется
  // Тесты для пересечения двух лучей
  // Различные случаи непересекающихся лучей
  @Test
  public void testRayXray1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(2, 1.5, 0), new Vect3d(1, 1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect");
  }

  @Test
  public void testRayXray2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(1, 1, 0), new Vect3d(2, 1.5, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect");
  }

  @Test
  public void testRayXray3() throws ExDegeneration, ExZeroDivision, ExGeom {
    //
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(1, 1, 0), new Vect3d(1, 2, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray4() throws ExDegeneration, ExZeroDivision, ExGeom {
    //
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(1, 1, 0), new Vect3d(2, 2, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray5() throws ExDegeneration, ExZeroDivision, ExGeom {
    //
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(1, 1, 0), new Vect3d(1, 2, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray6() throws ExDegeneration, ExZeroDivision, ExGeom {
    //
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(-1, 0, 0), new Vect3d(1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray7() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Лучи не пересекаются и лежат на одной прямой
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(-1, 0, 0), new Vect3d(-3, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray8() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Лучи накладываются по лучу
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(-1, 0, 0), new Vect3d(1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray9() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Лучи накладываются по отрезку
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(1, 0, 0), new Vect3d(-1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray10() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Лучи совпадают
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray11() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Вершины совпадают, но лучи не лежат на одной прямой
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray12() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Вершины совпадают, а лучи направлены в разные стороны
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(-1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray13() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Один луч содержит вершину двугого
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(1, -1, 0), new Vect3d(-1, 1, 0));
    ArrayList<Vect3d> result = ray1.intersect(ray2);
//      ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXray14() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Лучи пересекаются в одной точке
    Ray3d ray1 = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Ray3d ray2 = ray3dByTwoPoints(new Vect3d(1, -1, 0), new Vect3d(1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray1, ray2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

//тесты для персечения двух прямых
  @Test
  public void testLineXLine1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Прямые скрещиваются
    Line3d line1 = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Line3d line2 = getLineByTwoPoints(new Vect3d(0, -1, 0), new Vect3d(0, 0, 1));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(line1, line2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXLine2() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Прямые совпадают
    Line3d line1 = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Line3d line2 = getLineByTwoPoints(new Vect3d(1, 0, 0), new Vect3d(3, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(line1, line2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXLine3() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Прямые параллельны, но не совпадают
    Line3d line1 = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Line3d line2 = getLineByTwoPoints(new Vect3d(1, 1, 0), new Vect3d(2, 1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(line1, line2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXLine4() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Прямые пересекаются
    Line3d line1 = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Line3d line2 = getLineByTwoPoints(new Vect3d(1, 1, 0), new Vect3d(1, -1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(line1, line2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  //Тесты для пересечения двух отрезков
  @Test
  public void testRibXRib1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Один отрезок лежит внутри другого
    Rib3d rib1 = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(3, 0, 0));
    Rib3d rib2 = new Rib3d(new Vect3d(1, 0, 0), new Vect3d(2, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(rib1, rib2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRib2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Отрезки накладываются
    Rib3d rib1 = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Rib3d rib2 = new Rib3d(new Vect3d(1, 0, 0), new Vect3d(-1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(rib1, rib2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRib3() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезки лежат на одной прямой и имеют общую вершину
    Rib3d rib1 = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Rib3d rib2 = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(-1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(rib1, rib2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRib4() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Отрезки не лежат на одной прямой и имеют общую вершину
    Rib3d rib1 = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Rib3d rib2 = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(0, -1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(rib1, rib2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRib5() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Отрезки не лежат на одной прямой  вершина одного из отрезков лежит на другом.
    Rib3d rib1 = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Rib3d rib2 = new Rib3d(new Vect3d(1, 1, 0), new Vect3d(-1, -1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(rib1, rib2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRib6() throws ExDegeneration, ExZeroDivision, ExGeom {
    // // Внутренности отрезков пересекаются.
    Rib3d rib1 = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    Rib3d rib2 = new Rib3d(new Vect3d(1, 1, 0), new Vect3d(0, -1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(rib1, rib2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0.5, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRib7() throws ExDegeneration, ExZeroDivision, ExGeom {
    //
    Rib3d rib1 = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib2 = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(rib1, rib2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

// Тесты для пересечения многоугольников
  @Test
  public void testPolyXPoly1() throws ExDegeneration, ExZeroDivision, ExGeom {
    Polygon3d poly1;
    Polygon3d poly2;
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    ArrayList<Vect3d> p2 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    poly1 = new Polygon3d(p1);
    p2.add(new Vect3d(-1, 2, 0));
    p2.add(new Vect3d(2, 5, 0));
    p2.add(new Vect3d(5, 2, 0));
    poly2 = new Polygon3d(p2);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly1, poly2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 2, 0));
    answer.add(new Vect3d(0, 3, 0));
    answer.add(new Vect3d(1, 4, 0));
    answer.add(new Vect3d(3, 4, 0));
    answer.add(new Vect3d(4, 3, 0));
    answer.add(new Vect3d(4, 2, 0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testPolyXPoly2() throws ExDegeneration, ExZeroDivision, ExGeom {
    Polygon3d poly1;
    Polygon3d poly2;
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    ArrayList<Vect3d> p2 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    poly1 = new Polygon3d(p1);
    p2.add(new Vect3d(-4, 2, 0));
    p2.add(new Vect3d(-2, 4, 0));
    p2.add(new Vect3d(4, 4, 0));
    poly2 = new Polygon3d(p2);
    ArrayList<Vect3d> result = poly1.intersect(poly2);
    //ArrayList<Vect3d> result= Intersect2d.intersect2d(poly1, poly2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(4,4,0));
    //answer.add(new Vect3d(-2,4,0));
    answer.add(new Vect3d(0, 3, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  //Тесты для пересечения линии с окружностью.
  @Test
  public void testLineXCircle1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //окружность и прямая имеют две точки пересечения.
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Line3d line = line3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(3, 0, 0));
    answer.add(new Vect3d(-3, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXCircle2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //окружность и прямая касаются.
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Line3d line = line3dByTwoPoints(new Vect3d(1, 3, 0), new Vect3d(2, 3, 0));
    // ArrayList<Vect3d> result= circ.intersect(line);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 3, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXCircle3() throws ExDegeneration, ExZeroDivision, ExGeom {
    //окружность и прямая имеют одну точку пересечения, и прямая перпендикулярна плоскости окружности
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Line3d line = line3dByTwoPoints(new Vect3d(0, 3, 1), new Vect3d(0, 3, 2));
    //ArrayList<Vect3d> result= circ.intersect(line);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 3, 0));
    // boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXCircle4() throws ExDegeneration, ExZeroDivision, ExGeom {
    //окружность и прямая имеют одну точку пересечения, и прямая  не под прямым углом к плоскости окружности
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Line3d line = line3dByTwoPoints(new Vect3d(0, 2, 1), new Vect3d(0, 4, -1));
    //ArrayList<Vect3d> result= circ.intersect(line);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 3, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXCircle5() throws ExDegeneration, ExZeroDivision, ExGeom {
    //окружность и прямая имеют одну точку пересечения, и прямая  не под прямым углом к плоскости окружности
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Line3d line = line3dByTwoPoints(new Vect3d(0, 2, 1), new Vect3d(0, 4, -1));
    //ArrayList<Vect3d> result= circ.intersect(line);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 3, 0));
    // boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXCircle6() throws ExDegeneration, ExZeroDivision, ExGeom {
    //прямая и окружность не пересекаются, но лежат в одной плоскости
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Line3d line = line3dByTwoPoints(new Vect3d(5, 0, 0), new Vect3d(5, 1, 0));
    //ArrayList<Vect3d> result= circ.intersect(line);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,3,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXCircle7() throws ExDegeneration, ExZeroDivision, ExGeom {
    //прямая и окружность не пересекаются, но но окружность пересекает круг
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Line3d line = line3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(0, 1, 7));
    //ArrayList<Vect3d> result= circ.intersect(line);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,3,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXCircle8() throws ExDegeneration, ExZeroDivision, ExGeom {
    //прямая и окружность не пересекаются, прямая под углом к плоскости круга
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Line3d line = line3dByTwoPoints(new Vect3d(5, 0, 0), new Vect3d(6, 1, 7));
    //ArrayList<Vect3d> result= circ.intersect(line);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,3,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXCircle9() throws ExDegeneration, ExZeroDivision, ExGeom {
    //прямая и окружность не пересекаются, но но окружность пересекает круг
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Line3d line = line3dByTwoPoints(new Vect3d(5, 0, 0), new Vect3d(5, 0, 7));
    //ArrayList<Vect3d> result= circ.intersect(line);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,3,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  //Тесты для пересечения окружности с отрезком

  @Test
  public void testRibXCircle1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //окружность и отрезок лежат в одной плоскости и касаются
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(-1, 3, 0), new Vect3d(1, 3, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 3, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXCircle2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //окружность и отрезок не лежат в одной плоскости и касаются
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(-1, 3, 5), new Vect3d(1, 3, -5));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 3, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXCircle3() throws ExDegeneration, ExZeroDivision, ExGeom {
    //окружность и отрезок имеют две точки пересечения
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(-5, 0, 0), new Vect3d(5, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(3, 0, 0));
    answer.add(new Vect3d(-3, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXCircle4() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и окружность лежат  в одной плоскости различные случаи
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(-1, 0, 0), new Vect3d(0, 0, 0));
    ArrayList<Vect3d> result = circ.intersect(rib);
    //    ArrayList<Vect3d> result= Intersect2d.intersect2d(circ, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,3,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXCircle5() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и окружность лежат  в одной плоскости различные случаи
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(-1, 0, 0), new Vect3d(5, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(3, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXCircle6() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и окружность лежат  в одной плоскости различные случаи
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(-3, 0, 0), new Vect3d(3, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXCircle7() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и окружность лежат  в одной плоскости различные случаи
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(-3, 0, 0), new Vect3d(1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXCircle8() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и окружность лежат  в одной плоскости различные случаи
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(6, 0, 0), new Vect3d(5, 1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  //отрезок и окружность не пересекаются и не лежат в одной плоскости.

  @Test
  public void testRibXCircle9() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и окружность лежат  в одной плоскости различные случаи
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(0, 0, 1));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXCircle10() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и окружность лежат  в одной плоскости различные случаи
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(1, 0, 2));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  // Тесты для пересечения прямой и отрезка. Отрезок считается открытым.

  @Test
  public void testRibXLine1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Отрезок и прямая пересекаются и перпендикулярны.
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(0, -1, 0), new Vect3d(0, 1, 0));
    ArrayList<Vect3d> result = rib.intersect(line);
    //ArrayList<Vect3d> result= Intersect2d.intersect2d(line, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXLine2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезко и прямая перескаются и не перпендикулярны
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(0, -1, 0), new Vect3d(3, 2, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(line, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXLine3() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок лежит на прямой
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(2, 0, 0), new Vect3d(3, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(line, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXLine4() throws ExDegeneration, ExZeroDivision, ExGeom {
    //один из концов отрезка лежит на прямой
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(0, 1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(line, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXLine5() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и прямая лежат на параллельных прямых
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(0, 2, 0), new Vect3d(0, 3, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(line, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXLine6() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и прямая не лежат на параллельных прямых, но лежат в одной плоскости и не пересекаются
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(1, 2, 0), new Vect3d(3, 4, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(line, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXLine7() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и прямая не лежат в одной плоскости
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(1, 2, 6), new Vect3d(3, 4, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(line, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  //Тесты для пересечения луча и прямой

  @Test
  public void testLineXRay1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //прямая и луч пересекаются
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Ray3d ray = ray3dByTwoPoints(new Vect3d(2, 1, 0), new Vect3d(1, -1, 0));
    ArrayList<Vect3d> result = line.intersect(ray);
    //ArrayList<Vect3d> result= Intersect2d.intersect2d(ray, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1.5, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXRay2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч лежит на прямой
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Ray3d ray = ray3dByTwoPoints(new Vect3d(2, 0, 0), new Vect3d(1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXRay3() throws ExDegeneration, ExZeroDivision, ExGeom {
    //вершина луча лежит на прямой
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, -1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXRay4() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч параллелен прямой
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 1, 0), new Vect3d(1, 1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXRay5() throws ExDegeneration, ExZeroDivision, ExGeom {
    //прямая и луч лежат в одной плоскости, но не переескаются
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 1, 0), new Vect3d(0, 2, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testLineXRay6() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч и прямая не лежат в одной плоскости
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 1), new Vect3d(1, -1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  //Тесты для пересечения отрезка и луча

  @Test
  public void testRibXRay1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Есть точка пересечения
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(1, 1, 0), new Vect3d(1, -1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRay2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //вершина луча лежит на отрезке
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(1, 1, 0), new Vect3d(-1, -1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRay3() throws ExDegeneration, ExZeroDivision, ExGeom {
    //вершина луча и отрезка совпадают и они лежат на одной прямой
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(-1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRay4() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Вершина лучи и отрезка совпадают и они не лежат на одной прямой
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(1, 1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRay5() throws ExDegeneration, ExZeroDivision, ExGeom {
    // идин из концов отрезка лежит на луче
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(1, 1, 0), new Vect3d(1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRay6() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч и отрезок не пересекаются и лежат на параллельных прямых
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(0, 1, 0), new Vect3d(1, 1, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRay7() throws ExDegeneration, ExZeroDivision, ExGeom {
    //отрезок и луч лежат на скрещивающихся прямых
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(1, 1, 0), new Vect3d(1, 2, 3));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRay8() throws ExDegeneration, ExZeroDivision, ExGeom {
    // луч и отрезок не пересекаются и лежат на пересекающихся прямых
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(1, 1, 0), new Vect3d(1, 2, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRibXRay9() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч и отрезок не пересекаются и лежат на пересекающихся прямых2
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    Rib3d rib = new Rib3d(new Vect3d(1, 1, 0), new Vect3d(-1, 0, 0));
    //ArrayList<Vect3d> result= circ.intersect(rib);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(ray, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(1,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  //тесты для пересечения многоугольника с прямой.

  @Test
  public void testPolyXLine1() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Прямая содержит одну из сторон многоугольника
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testPolyXLine2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //  Прямая и многоугольник имеют две точки пересечения
    Line3d line = getLineByTwoPoints(new Vect3d(0, 2, 0), new Vect3d(2, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = poly.intersect(line);
    //ArrayList<Vect3d> result= Intersect2d.intersect2d(poly, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 2, 0));
    answer.add(new Vect3d(2, 0, 0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testPolyXLine3() throws ExDegeneration, ExZeroDivision, ExGeom {
    //  Прямая проходит через одну из вершин
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, -1, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = poly.intersect(line);
    //ArrayList<Vect3d> result= Intersect2d.intersect2d(poly, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testPolyXLine4() throws ExDegeneration, ExZeroDivision, ExGeom {
    //  Прямая проходит через две вершины
    Line3d line = getLineByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(4, 4, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testPolyXLine5() throws ExDegeneration, ExZeroDivision, ExGeom {
    //  Прямая пересекает ровно одну сторону
    Line3d line = getLineByTwoPoints(new Vect3d(0, 1, 0), new Vect3d(2, 0, 2));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 1, 0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testPolyXLine7() throws ExDegeneration, ExZeroDivision, ExGeom {
    //  Прямая пересекает ровно одну сторону
    Line3d line = getLineByTwoPoints(new Vect3d(1, 2, 0), new Vect3d(2, 2, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    //ArrayList<Vect3d> result = poly.intersect(line);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 2, 0));
    answer.add(new Vect3d(4, 2, 0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testPolyXLine8() throws ExDegeneration, ExZeroDivision, ExGeom {
    //  Прямая пересекает ровно одну сторону
    Line3d line = getLineByTwoPoints(new Vect3d(1, 2, 0), new Vect3d(5, 2, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    //ArrayList<Vect3d> result = poly.intersect(line);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, 2, 0));
    answer.add(new Vect3d(4, 2, 0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  //тесты для пересечения многугольника с отрезком
  @Test
  public void testPolyXRib1() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок содержит сторону многоугольника
    Rib3d rib = new Rib3d(new Vect3d(-1, 0, 0), new Vect3d(5, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib2() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок содержит сторону многоугольника и одна из вершин совпадает
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(5, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib3() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок совпадает со стороной многоугольника
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(4, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib4() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок лежит внутри стороны многоугольника
    Rib3d rib = new Rib3d(new Vect3d(1, 0, 0), new Vect3d(2, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib5() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок и сторона накладываются
    Rib3d rib = new Rib3d(new Vect3d(1, 0, 0), new Vect3d(5, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib6() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок и сторона лежат на одной прямой и имеют одну общую точку
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(-1, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib7() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок и сторона лежат на одной прямой и не пересекаются
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib8() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок пересекает 1 строну многоугольника во внутренней точке
    Rib3d rib = new Rib3d(new Vect3d(1, 1, 0), new Vect3d(1, -1, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1, 0, 0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib9() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок пересекает сторону, но отрезок не лежит в плоскости многоугольника
    Rib3d rib = new Rib3d(new Vect3d(1, 1, 1), new Vect3d(1, -1, -1));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1, 0, 0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib10() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок пересекает две стороны во внутренних точках
    Rib3d rib = new Rib3d(new Vect3d(1, -1, 0), new Vect3d(1, 5, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1, 0, 0));
    answer.add(new Vect3d(1, 4, 0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib11() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок пересекает одну сторону и вершину
    Rib3d rib = new Rib3d(new Vect3d(1, -2, 0), new Vect3d(5, 6, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(2, 0, 0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib12() throws ExDegeneration, ExZeroDivision, ExGeom {
    // вершина отрезка лежит на стороне, и отрезок проходит через вершину
    Rib3d rib = new Rib3d(new Vect3d(2, 0, 0), new Vect3d(5, 6, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib13() throws ExDegeneration, ExZeroDivision, ExGeom {
    // 1 вершина отрезка лежит на стороне, вторая совпадает с вершиной многоугольника
    Rib3d rib = new Rib3d(new Vect3d(2, 0, 0), new Vect3d(4, 4, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib14() throws ExDegeneration, ExZeroDivision, ExGeom {
    // две вершины отрека совпадают с вершинами многоугольника
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 0), new Vect3d(4, 4, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib15() throws ExDegeneration, ExZeroDivision, ExGeom {
    // две вершины многоугольника лежат на отрезке
    Rib3d rib = new Rib3d(new Vect3d(-1, -1, 0), new Vect3d(5, 5, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRib16() throws ExDegeneration, ExZeroDivision, ExGeom {
    // отрезок пересекает плосксть многоугольника и проходит через его вершину
    Rib3d rib = new Rib3d(new Vect3d(0, 0, 1), new Vect3d(0, 0, -1));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, rib);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(0,2,0));
    //answer.add(new Vect3d(0,3,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  // Тесты для пересечения луча и многоугольника

  public void testPolyXRay1() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Луч пересекает многоугольник в двух точках
    Ray3d ray = ray3dByTwoPoints(new Vect3d(1, -1, 0), new Vect3d(1, 1, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1, 0, 0));
    answer.add(new Vect3d(1, 4, 0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay2() throws ExDegeneration, ExZeroDivision, ExGeom {
    // луч проходит через две вершины многоугольника
    Ray3d ray = ray3dByTwoPoints(new Vect3d(-1, -1, 0), new Vect3d(1, 1, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(1,0,0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay3() throws ExDegeneration, ExZeroDivision, ExGeom {
    // вершина луча совпадает с одной из вершин многоугольника и луч проходит через другую вершину
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 1, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(1,0,0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay4() throws ExDegeneration, ExZeroDivision, ExGeom {
    // вершина луча совпадает с одной из верши, но луч не пересекает многоугольник 1сл
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(-1, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(1,0,0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay5() throws ExDegeneration, ExZeroDivision, ExGeom {
    // вершина луча совпадает с одной из верши, но луч не пересекает многоугольник 2сл
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(-1, 1, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(1,0,0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay6() throws ExDegeneration, ExZeroDivision, ExGeom {
    // вершина луча совпадает с одной из вершин многоугольника, а луч пересекает сторону многоугольника
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 4, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1, 4, 0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay7() throws ExDegeneration, ExZeroDivision, ExGeom {
    // вершина луча совпадает с вершиной многоугольника, но луч не лежит в плосксоти многоугольника
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 1, 1));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(1,0,0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay8() throws ExDegeneration, ExZeroDivision, ExGeom {
    //вершина луча совпадает с вершиной многоугольника и сторона многоугольника лежит на луче
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(1,0,0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay9() throws ExDegeneration, ExZeroDivision, ExGeom {
    // сторона лежит на луче
    Ray3d ray = ray3dByTwoPoints(new Vect3d(-1, 0, 0), new Vect3d(1, 0, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(1,0,0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay10() throws ExDegeneration, ExZeroDivision, ExGeom {
    // луч пересекает многоугольник ровно в одной точке
    Ray3d ray = ray3dByTwoPoints(new Vect3d(1, 0, -1), new Vect3d(1, 0, 1));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(1, 0, 0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay11() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Луч содержит ровно одну вершину многоугольника
    Ray3d ray = ray3dByTwoPoints(new Vect3d(0, -1, 0), new Vect3d(0, 1, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(1,0,0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  public void testPolyXRay12() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Вершина луча лежит на строное, но луч не лежит в плоскости многоугольника
    Ray3d ray = ray3dByTwoPoints(new Vect3d(1, 0, 0), new Vect3d(1, 1, 1));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(poly, ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(1,0,0));
    //answer.add(new Vect3d(1,4,0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }
  //тесты для пересечения луча и окружности

  @Test
  public void testRayXCircle1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Луч и окружность пересекаются в двух точках
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(-5, 0, 0), new Vect3d(1, 0, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(3, 0, 0));
    answer.add(new Vect3d(-3, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Луч и окружность пересекаются в одной точке
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(3, 0, 0));
      //answer.add(new Vect3d(-3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle3() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч и окружность пересекаются в одной точке, но не лежат в одной плоскости
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(3, 1, 0), new Vect3d(3, -1, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(3, 0, 0));
     // answer.add(new Vect3d(-3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle4() throws ExDegeneration, ExZeroDivision, ExGeom {
    // вершина луч лежит на окружности и луч перескает окружность в одной точке
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(3, 0, 0), new Vect3d(0, 0, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    // answer.add(new Vect3d(3,0,0));
    answer.add(new Vect3d(-3, 0, 0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle5() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Вершина луча лежит на окружности, они лежат в одной плоскости, но не пересекаются
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(3, 0, 0), new Vect3d(4, 0, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(3,0,0));
    //answer.add(new Vect3d(-3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle6() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Вершина луча лежит на окружности, но луч не лежит в плоскости окружности
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(3, 0, 0), new Vect3d(3, 1, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(3,0,0));
    // answer.add(new Vect3d(-3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle7() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч и окружность лежат в одной плоскости, но не пресекаются(прямая, сожержащая луч персекает окружность)
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(5, 0, 0), new Vect3d(6, 0, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    //  answer.add(new Vect3d(3,0,0));
    //  answer.add(new Vect3d(-3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle8() throws ExDegeneration, ExZeroDivision, ExGeom {
    // луч и окружность лежат в одной плоскости, не пересекаются(прямая содержащая луч не пересекает окружность)
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(7, 0, 0), new Vect3d(0, 7, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(3,0,0));
    // answer.add(new Vect3d(-3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle9() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Луч касается окружности не в вершине.
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(0, 3, 0), new Vect3d(-1, 3, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(0,3,0));
    // answer.add(new Vect3d(-3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle10() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч лежит в поскости параллельной плоскости окружности
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(0, 1, 1), new Vect3d(1, 1, 1));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(3,0,0));
    // answer.add(new Vect3d(-3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle11() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч перескает окружнсоть в одной точке и не лежит в плосксоти окружности
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(4, 1, 1), new Vect3d(3, 0, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(3, 0, 0));
     // answer.add(new Vect3d(-3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testRayXCircle12() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч пересекает круг, но не пересекает окружность
    Circle3d circ = new Circle3d(new Vect3d(-3, 0, 0), new Vect3d(0, 3, 0), new Vect3d(3, 0, 0));
    Ray3d line = ray3dByTwoPoints(new Vect3d(1, 1, -1), new Vect3d(1, 1, 1));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
     // answer.add(new Vect3d(3,0,0));
    // answer.add(new Vect3d(-3,0,0));
    //boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  // Тесты для пересечения окружности и многоугольника
  @Test
  public void testPolyXCircle1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //Описанная вокруг многоуголника окружность
    Circle3d circ = new Circle3d(new Vect3d(0, 0, 0), new Vect3d(0, 4, 0), new Vect3d(4, 4, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, poly);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(3,0,0));
    // answer.add(new Vect3d(-3,0,0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testPolyXCircle2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //касается сторон
    Circle3d circ = new Circle3d(new Vect3d(2, 0, 0), new Vect3d(0, 2, 0), new Vect3d(2, 4, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, poly);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(2, 0, 0));
    answer.add(new Vect3d(0, 2, 0));
    answer.add(new Vect3d(2, 4, 0));
    answer.add(new Vect3d(4, 2, 0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testPolyXCircle3() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Проходи через одну вершину и пересекает две стороны
    Circle3d circ = new Circle3d(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0), new Vect3d(0, 2, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, poly);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(2, 0, 0));
    answer.add(new Vect3d(0, 2, 0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testPolyXCircle4() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Пересекает стороны многоугольника
    Circle3d circ = new Circle3d(new Vect3d(2, 0, 0), new Vect3d(0, 2, 0), new Vect3d(2, 4, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));
    p1.add(new Vect3d(4, 0, 0));
    Polygon3d poly = new Polygon3d(p1);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ, poly);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(2, 0, 0));
    answer.add(new Vect3d(0, 2, 0));
    answer.add(new Vect3d(2, 4, 0));
    answer.add(new Vect3d(4, 2, 0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  //Тесты для пересечения двух окружностей

  @Test
  public void testСircleXCircle1() throws ExDegeneration, ExZeroDivision, ExGeom {
    // Окружности лежат в одной плоскости и пересекаются в двух точках
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(4, 8, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(4, 0, 0));
    answer.add(new Vect3d(0, 4, 0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testСircleXCircle2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //окружности перекаются в двух точках, но не лежат в одной плоскости
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(4, 8, 1));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(4, 0, 0));
    answer.add(new Vect3d(0, 4, 0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testСircleXCircle3() throws ExDegeneration, ExZeroDivision, ExGeom {
    // окружности касаются внешним образом
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(8, 4, 0), new Vect3d(8, -4, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(4, 0, 0));
    //answer.add(new Vect3d(0,2,0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testСircleXCircle4() throws ExDegeneration, ExZeroDivision, ExGeom {
    // окружности касаются внутренним образом
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(2, 2, 0), new Vect3d(2, -2, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(4, 0, 0));
    //answer.add(new Vect3d(0,2,0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testСircleXCircle5() throws ExDegeneration, ExZeroDivision, ExGeom {
    // окружности касаются в одной точке, но не длежат в одно плоскости
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(4, 1, 1), new Vect3d(4, -1, 1));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(4, 0, 0));
    //answer.add(new Vect3d(0,2,0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testСircleXCircle6() throws ExDegeneration, ExZeroDivision, ExGeom {
    // окружности не перекаются и одна лежит внутри другой
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(2, 0, 0), new Vect3d(0, 2, 0), new Vect3d(0, -2, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(2,0,0));
    //answer.add(new Vect3d(0,2,0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testСircleXCircle7() throws ExDegeneration, ExZeroDivision, ExGeom {
    // окружности не пересекаются и лежат в одной плоскости
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(8, 0, 0), new Vect3d(7, 0, 0), new Vect3d(8, 1, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(2,0,0));
    //answer.add(new Vect3d(0,2,0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testСircleXCircle8() throws ExDegeneration, ExZeroDivision, ExGeom {
    // окружности не перескатся и лежат в параллельных плоскостях
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(4, 0, 1), new Vect3d(0, 4, 1), new Vect3d(0, -4, 1));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(2,0,0));
    //answer.add(new Vect3d(0,2,0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testСircleXCircle9() throws ExDegeneration, ExZeroDivision, ExGeom {
    //  окружнсоти не пересекаются и лежат в пересекающихся плоскостях
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(5, -1, 0), new Vect3d(-1, 5, 0), new Vect3d(2, 2, 2));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(4,0,0));
    //answer.add(new Vect3d(0,4,0));
    //answer.add(new Vect3d(5,-1,0));
    //answer.add(new Vect3d(-1,5,0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testСircleXCircle10() throws ExDegeneration, ExZeroDivision, ExGeom {
    // окружности совпадают
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
      //answer.add(new Vect3d(2,0,0));
    //answer.add(new Vect3d(0,2,0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testСircleXCircle11() throws ExDegeneration, ExZeroDivision, ExGeom {
    //окружности лежат в разных плоскостях и имеют одну точку пересечения
    Circle3d circ1 = new Circle3d(new Vect3d(4, 0, 0), new Vect3d(0, 4, 0), new Vect3d(0, -4, 0));
    Circle3d circ2 = new Circle3d(new Vect3d(0, 4, 0), new Vect3d(5, -1, 0), new Vect3d(2, 2, 2));
    ArrayList<Vect3d> result = Intersect2d.intersect2d(circ1, circ2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    //answer.add(new Vect3d(4,0,0));
    answer.add(new Vect3d(0, 4, 0));
      //answer.add(new Vect3d(5,-1,0));
    //answer.add(new Vect3d(-1,5,0));
    assertTrue(Vect3d.equalsAsSet(result, answer), "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testTriangleXLine1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //
    Line3d line = getLineByTwoPoints(new Vect3d(1, 2, 0), new Vect3d(2, 2, 0));
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 0, 0));
    p1.add(new Vect3d(0, 4, 0));
    p1.add(new Vect3d(4, 4, 0));

    Triang3d poly = new Triang3d(p1);
    //ArrayList<Vect3d> result = poly.intersect(line);
    ArrayList<Vect3d> result = poly.intersect(line);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(2, 2, 0));
    answer.add(new Vect3d(0, 2, 0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(x, "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testArcXArc1() throws ExDegeneration, ExZeroDivision, ExGeom {
    // дуги не пересекаются, когда окружности должны были бы
    // (связано с тем, что функция пересечения сделана по образу пересечения окружностей)
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 15, 0));
    p1.add(new Vect3d(15, 5, 0));
    p1.add(new Vect3d(40, 10, 0));

    ArrayList<Vect3d> p2 = new ArrayList<Vect3d>();
    p2.add(new Vect3d(5, 15, 0));
    p2.add(new Vect3d(20, 20, 0));
    p2.add(new Vect3d(30, 15, 0));

    Arc3d arc1 = new Arc3d(p1);
    Arc3d arc2 = new Arc3d(p2);
    //ArrayList<Vect3d> result = poly.intersect(line);
    ArrayList<Vect3d> result = arc1.intersect(arc2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();

    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(x, "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testArcXArc2() throws ExDegeneration, ExZeroDivision, ExGeom {
    //дуги в перпендикулярных плоскостях
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(-1, 0, 0));
    p1.add(new Vect3d(1, 0, 0));
    p1.add(new Vect3d(0, 1, 0));

    ArrayList<Vect3d> p2 = new ArrayList<Vect3d>();
    p2.add(new Vect3d(0, 1, 0));
    p2.add(new Vect3d(0, 0, 1));
    p2.add(new Vect3d(0, 0, -1));

    Arc3d arc1 = new Arc3d(p1);
    Arc3d arc2 = new Arc3d(p2);
    //ArrayList<Vect3d> result = poly.intersect(line);
    ArrayList<Vect3d> result = arc1.intersect(arc2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    answer.add(new Vect3d(0, -1, 0));
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(x, "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testArcXArc3() throws ExDegeneration, ExZeroDivision, ExGeom {
    //дуги в разных плоскостях, под углом. Вершины одной лежат на другой. Точек пересечения нет.
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(-1, 0, 0));
    p1.add(new Vect3d(1, 0, 0));
    p1.add(new Vect3d(0, 1, 0));

    ArrayList<Vect3d> p2 = new ArrayList<Vect3d>();
    p2.add(new Vect3d(Math.sqrt(2) / 2, Math.sqrt(2) / 2, 0));//sqrt(2)/2
    p2.add(new Vect3d(0, 0, 1));
    p2.add(new Vect3d(-Math.sqrt(2) / 2, -Math.sqrt(2) / 2, 0));//sqrt(2)/2

    Arc3d arc1 = new Arc3d(p1);
    Arc3d arc2 = new Arc3d(p2);
    //ArrayList<Vect3d> result = poly.intersect(line);
    ArrayList<Vect3d> result = arc1.intersect(arc2);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();
    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(x, "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testAngleXRay1() throws ExDegeneration, ExZeroDivision, ExGeom {
    //луч проходит через точку на стороне угла
    ArrayList<Vect3d> p1 = new ArrayList<Vect3d>();
    p1.add(new Vect3d(0, 15, 0));
    p1.add(new Vect3d(15, 5, 0));
    p1.add(new Vect3d(40, 10, 0));

    ArrayList<Vect3d> p2 = new ArrayList<Vect3d>();
    p2.add(new Vect3d(0, 5, 0));
    p2.add(new Vect3d(0, 15, 0));

    Angle3d ang = new Angle3d(p1);
    Ray3d ray = new Ray3d(p2);

    ArrayList<Vect3d> result = ang.intersect(ray);
    ArrayList<Vect3d> answer = new ArrayList<Vect3d>();

    boolean x = Vect3d.equalsAsSet(result, answer);
    assertTrue(x, "Method intersect2d(i_Geom, i_Geom) is incorrect ");
  }

  @Test
  public void testIntersect2d() throws ExDegeneration, ExZeroDivision {
    Vect3d v = new Vect3d(0.5, 0.5, 0);
    ArrayList<Vect3d> result = Intersect2d.intersect2d(_line1, _line2);
    assertTrue(result.get(0).equals(v), "Method intersect2d(i_Geom, i_Geom) is incorrect");
    result = Intersect2d.intersect2d(_rib1, _rib2);
    assertTrue(result.get(0).equals(v), "Method intersect2d(i_Geom, i_Geom) is incorrect");
    result = Intersect2d.intersect2d(_line1, _rib2);
    assertTrue(result.get(0).equals(v), "Method intersect2d(i_Geom, i_Geom) is incorrect");
    result = Intersect2d.intersect2d(_rib1, _line2);
    assertTrue(result.get(0).equals(v), "Method intersect2d(i_Geom, i_Geom) is incorrect");
  }
}
