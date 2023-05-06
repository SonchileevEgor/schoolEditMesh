package geom;

import static geom.Line3d.getLineByTwoPoints;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Polygon3dTest {

  private Polygon3d _tr;

  @BeforeEach
  public void setUp() throws Exception {
    _tr = new Polygon3d(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0), new Vect3d(0, 1, 0));
  }

  // Находятся на параллельных плоскостях
  @Test
  public void testNotIntersectionParallelPlane() {
    assertThrows(ExGeom.class, () -> {
        Polygon3d tr1 = new Polygon3d(new Vect3d(0, 0, 1), new Vect3d(1, 0, 1), new Vect3d(0, 1, 1));
        _tr.intersect3d(tr1);
    });
  }

  // Находятся в одной плоскости но не пересекаются
  @Test
  public void testNotIntersectionOnePlane() {
    assertThrows(ExGeom.class, () -> {
        Polygon3d tr1 = new Polygon3d(new Vect3d(2, 0, 0), new Vect3d(3, 0, 0), new Vect3d(2, 1, 0));
        _tr.intersect3d(tr1);
    });
  }

  // Одна точка пересечения
  @Test
  public void testIntersectionOnePoint() throws Exception {
    Polygon3d tr1 = new Polygon3d(new Vect3d(0, 0, 0), new Vect3d(0, 0, 1), new Vect3d(1, 0, 1));
    ArrayList<Vect3d> pointsIntersect = _tr.intersect3d(tr1);
    ArrayList<Vect3d> pointsIdeal = new ArrayList<>();
    pointsIdeal.add(new Vect3d(0, 0, 0));
    assertArrayEquals(pointsIntersect.toArray(), pointsIdeal.toArray());
  }

  // Два многоугольника совпадают
  @Test
  public void testIntersectionEquals() throws Exception {
    Polygon3d tr1 = new Polygon3d(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0), new Vect3d(0, 1, 0));
    ArrayList<Vect3d> pointsIntersect = _tr.intersect3d(tr1);
    ArrayList<Vect3d> pointsIdeal = new ArrayList<>();
    pointsIdeal.add(new Vect3d(0, 0, 0));
    pointsIdeal.add(new Vect3d(1, 0, 0));
    pointsIdeal.add(new Vect3d(0, 1, 0));
    assertEquals(pointsIntersect, pointsIdeal);
  }

  // Много точек пересечения
  @Test
  public void testIntersectionManyPoints() throws Exception {
    Polygon3d tr1 = new Polygon3d(new Vect3d(0.5, 0, 0), new Vect3d(0, 0.5, 0), new Vect3d(1, 1, 0));
    ArrayList<Vect3d> pointsIntersect = _tr.intersect3d(tr1);
    assertEquals(pointsIntersect.size(), 4);
  }

  // Пересечение есть отрезок
  @Test
  public void testIntersectionOneRib() throws Exception {
    Polygon3d tr1 = new Polygon3d(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0), new Vect3d(1, 0, 1));
    ArrayList<Vect3d> pointsIntersect = _tr.intersect3d(tr1);
    ArrayList<Vect3d> pointsIdeal = new ArrayList<>();
    pointsIdeal.add(new Vect3d(0, 0, 0));
    pointsIdeal.add(new Vect3d(1, 0, 0));
    assertArrayEquals(pointsIntersect.toArray(), pointsIdeal.toArray());
  }

  // Многоугольники находятся в одной плоскости и имеют один общий отрезок
  @Test
  public void testIntersectionOneRibInOnePlane() throws Exception {
    Polygon3d tr1 = new Polygon3d(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0), new Vect3d(1, -1, 0));
    ArrayList<Vect3d> pointsIntersect = _tr.intersect3d(tr1);
    ArrayList<Vect3d> pointsIdeal = new ArrayList<>();
    pointsIdeal.add(new Vect3d(0, 0, 0));
    pointsIdeal.add(new Vect3d(1, 0, 0));
    assertArrayEquals(pointsIntersect.toArray(), pointsIdeal.toArray());
  }

  @Test
  public void testIntersect() throws Exception {
    Polygon3d tr1 = new Polygon3d(new Vect3d(0.1, 0.1, -0.2), new Vect3d(0.2, 0.05, 0.2), new Vect3d(0.05, 0.2, 0.2));
    ArrayList<Vect3d> pointsIntersect = _tr.intersect3d(tr1);
    ArrayList<Vect3d> pointsIdeal = new ArrayList<>();
    pointsIdeal.add(new Vect3d(0.15, 0.075, 0));
    pointsIdeal.add(new Vect3d(0.075, 0.15, 0));
    assertEquals(pointsIntersect.size(), 2);
    assertArrayEquals(pointsIntersect.toArray(), pointsIdeal.toArray());
  }

  // Тесты для персечения многоугольника и прямой.
  //Многоугольник считается телесным.
  @Test    
  public void testPolyXLine1() throws ExZeroDivision, ExGeom{
    // Прямая персеекает многоугольник в одной внутренней точке
    Line3d line =   getLineByTwoPoints(new Vect3d(2, 2, -1), new Vect3d(2, 2, -2));
      ArrayList<Vect3d> p1= new ArrayList<>();
      p1.add(new Vect3d(0,0,0));
      p1.add(new Vect3d(0,4,0));
      p1.add(new Vect3d(4,4,0));
      p1.add(new Vect3d(4,0,0));
      Polygon3d poly = new Polygon3d(p1); 
      ArrayList<Vect3d> result= poly.intersectWithLine(line);
      ArrayList<Vect3d> answer = new ArrayList<>();
      answer.add(new Vect3d(2,2,0));
      //answer.add(new Vect3d(0,3,0));
      boolean x = Vect3d.equalsAsSet(result, answer);
      assertEquals(Vect3d.equalsAsSet(result, answer) , true);
  }
  @Test
  public void testPolyXLine2() throws ExZeroDivision, ExGeom{
    // Прямая персеекает многоугольник в вершине
    Line3d line =   getLineByTwoPoints(new Vect3d(0, 0, -1), new Vect3d(0, 0, -2));
      ArrayList<Vect3d> p1= new ArrayList<>();
      p1.add(new Vect3d(0,0,0));
      p1.add(new Vect3d(0,4,0));
      p1.add(new Vect3d(4,4,0));
      p1.add(new Vect3d(4,0,0));
      Polygon3d poly = new Polygon3d(p1); 
     ArrayList<Vect3d> result= poly.intersectWithLine(line);
      ArrayList<Vect3d> answer = new ArrayList<>();
      answer.add(new Vect3d(0,0,0));
      //answer.add(new Vect3d(0,3,0));
      boolean x = Vect3d.equalsAsSet(result, answer);
      assertEquals(Vect3d.equalsAsSet(result, answer) , true);
  }

  @Test
  public void testPolyXLine3() throws ExZeroDivision, ExGeom{
    // Прямая и многоугольник не лежат в одной плоскости.
    //Прямая пересекает сторону многоугольника
      Line3d line =   getLineByTwoPoints(new Vect3d(2, 0, -1), new Vect3d(2, 0, -2));
      ArrayList<Vect3d> p1= new ArrayList<>();
      p1.add(new Vect3d(0,0,0));
      p1.add(new Vect3d(0,4,0));
      p1.add(new Vect3d(4,4,0));
      p1.add(new Vect3d(4,0,0));
      Polygon3d poly = new Polygon3d(p1); 
     ArrayList<Vect3d> result= poly.intersectWithLine(line);
      ArrayList<Vect3d> answer = new ArrayList<>();
      answer.add(new Vect3d(2,0,0));
      //answer.add(new Vect3d(0,3,0));
      boolean x = Vect3d.equalsAsSet(result, answer);
      assertEquals(Vect3d.equalsAsSet(result, answer) , true);
  }

  @Test
  public void testPolyXLine4() throws ExZeroDivision, ExGeom{
    // прямая и многоугольник лежат в одной плоскости и пересекаются
    Line3d line =   getLineByTwoPoints(new Vect3d(2, 9, 0), new Vect3d(2, 1, 0));
      ArrayList<Vect3d> p1= new ArrayList<>();
      p1.add(new Vect3d(0,0,0));
      p1.add(new Vect3d(0,4,0));
      p1.add(new Vect3d(4,4,0));
      p1.add(new Vect3d(4,0,0));
      Polygon3d poly = new Polygon3d(p1); 
     ArrayList<Vect3d> result= poly.intersectWithLine(line);
      ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(2,2,0));
      //answer.add(new Vect3d(0,3,0));
      boolean x = Vect3d.equalsAsSet(result, answer);
      assertEquals(Vect3d.equalsAsSet(result, answer) , true);
  }

  @Test
  public void testPolyXLine5() throws ExZeroDivision, ExGeom{
    // прямая и многоугльник лежат в одной поскости и неперескаются
    Line3d line =   getLineByTwoPoints(new Vect3d(6, 0, 0), new Vect3d(6, 1, 0));
      ArrayList<Vect3d> p1= new ArrayList<>();
      p1.add(new Vect3d(0,0,0));
      p1.add(new Vect3d(0,4,0));
      p1.add(new Vect3d(4,4,0));
      p1.add(new Vect3d(4,0,0));
      Polygon3d poly = new Polygon3d(p1); 
     ArrayList<Vect3d> result= poly.intersectWithLine(line);
      ArrayList<Vect3d> answer = new ArrayList<>();
      //answer.add(new Vect3d(2,2,0));
      //answer.add(new Vect3d(0,3,0));
      boolean x = Vect3d.equalsAsSet(result, answer);
      assertEquals(Vect3d.equalsAsSet(result, answer) , true);
  }
 
  
  
  
}