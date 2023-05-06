package geom;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class Plane3dTest {

  @Test
  public void testBisectorPlaneOf2Planes1() throws ExGeom{
    Plane3d pl1 = new Plane3d(Vect3d.UZ, Vect3d.O);
    Plane3d pl2 = new Plane3d(Vect3d.UY, Vect3d.O);
    ArrayList<Plane3d> actual = Plane3d.bisectorPlaneOf2Planes(pl1, pl2);
    Plane3d expected = new Plane3d(new Vect3d(0, 1, 1), Vect3d.O);
    assertEquals(Plane3d.equals(expected, actual.get(0)), true);
    expected = new Plane3d(new Vect3d(0, -1, 1), Vect3d.O);
    assertEquals(Plane3d.equals(expected, actual.get(1)), true);
  }

  @Test
  public void testBisectorPlaneOf2Planes2() throws ExGeom{
    Plane3d pl1 = new Plane3d(new Vect3d(0, -1, 1), Vect3d.O);
    Plane3d pl2 = new Plane3d(Vect3d.UZ, Vect3d.O);
    ArrayList<Plane3d> actual = Plane3d.bisectorPlaneOf2Planes(pl1, pl2);
    Plane3d expected = new Plane3d(new Vect3d(0, -1 * Math.tan(22.5/180*Math.PI), 1), Vect3d.O);
    assertEquals(Plane3d.equals(expected, actual.get(0)), true);
    expected = new Plane3d(new Vect3d(0, 1, Math.tan(22.5/180*Math.PI)), Vect3d.O);
    assertEquals(Plane3d.equals(expected, actual.get(1)), true);
  }

  @Test
  public void testBisectorPlaneOf2Planes3() throws ExGeom{
    Plane3d pl1 = new Plane3d(new Vect3d(0, -1, -1), new Vect3d(0, -1, 0));
    Plane3d pl2 = new Plane3d(Vect3d.UZ, Vect3d.O);
    ArrayList<Plane3d> actual = Plane3d.bisectorPlaneOf2Planes(pl1, pl2);
    Plane3d expected = new Plane3d(new Vect3d(0, -1, Math.tan(22.5/180*Math.PI)), new Vect3d(0, -1, 0));
    assertEquals(Plane3d.equals(expected, actual.get(0)), true);
    expected = new Plane3d(new Vect3d(0, Math.tan(22.5/180*Math.PI), 1), new Vect3d(0, -1, 0));
    assertEquals(Plane3d.equals(expected, actual.get(1)), true);
  }

  @Test
  public void testBisectorPlaneOf2Planes4() throws Exception{
    Plane3d pl1 = new Plane3d(new Vect3d(0, -1, 1), Vect3d.O);
    Plane3d pl2 = new Plane3d(new Vect3d(0, -1, 1), new Vect3d(0, 0, 5));
    assertThrows(ExGeom.class, () -> {
        Plane3d.bisectorPlaneOf2Planes(pl1, pl2);
    });
  }
}
