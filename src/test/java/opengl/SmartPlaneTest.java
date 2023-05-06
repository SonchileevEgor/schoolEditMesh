package opengl;

import geom.Plane3d;
import geom.Vect3d;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by Maks on 4/2/2015.
 */
public class SmartPlaneTest {

  @BeforeEach
  public void setUp() throws Exception {

  }

  @AfterEach
  public void tearDown() throws Exception {

  }

  @Test
  public void constructorTest1() throws Exception{
    Plane3d plane = new Plane3d(new Vect3d(0, 0, 1), new Vect3d(0, 0, 0));
    ArrayList<Vect3d> points = new ArrayList<>();
    points.add(new Vect3d(0, 0, 0));
    SmartPlane sp = new SmartPlane(points, plane);
    assertEquals(false, sp.isCorrect());
  }
  @Test
  public void constructorTest2() throws Exception{
    Plane3d plane = new Plane3d(new Vect3d(0, 0, 1), new Vect3d(0, 0, 0));
    ArrayList<Vect3d> points2 = new ArrayList<>();
    points2.add(new Vect3d(0, 0, 0));
    points2.add(new Vect3d(1, 0, 0));
    SmartPlane sp = new SmartPlane(points2, plane);
    assertEquals(true, sp.isCorrect());
    ArrayList<Vect3d> res = sp.getAsRectangle(1d);
  }
}