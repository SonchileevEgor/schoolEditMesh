package geom;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author alexeev
 */
public class Prism3dTest {
  private Prism3d _pr1;

  @BeforeEach
  public void setUp() throws ExDegeneration, ExZeroDivision {
    ArrayList<Vect3d> pts = new ArrayList<>();
    pts.add(new Vect3d(0, 0, 0));
    pts.add(new Vect3d(0, 1, 0));
    pts.add(new Vect3d(1, 1, 0));
    pts.add(new Vect3d(1, 0, 0));
    pts.add(new Vect3d(-1, -1, 2));
    _pr1 = new Prism3d(pts);
  }

  @Test
  public void testHeightLength() {
    assertEquals(2, _pr1.heightLength(), Checker.eps());
  }

  @Test
  public void testSurfaceArea() {
    assertEquals(10, _pr1.surfaceArea(), Checker.eps());
  }

  @Test
  public void testVolume() {
    assertEquals(2, _pr1.volume(), Checker.eps());
  }

  @Test
  public void testLateralSurfaceArea() {
    assertEquals(8, _pr1.lateralSurfaceArea(), Checker.eps());
  }

  @Test
  public void testInSphere1() throws ExGeom{
    ArrayList<Vect3d> pts = new ArrayList<>();
    pts.add(new Vect3d(1, 1, 0));
    pts.add(new Vect3d(-1, 1, 0));
    pts.add(new Vect3d(-1, -1, 0));
    pts.add(new Vect3d(1, -1, 0));
    pts.add(new Vect3d(1, 1, 2));
    Prism3d prism = new Prism3d(pts);
    Sphere3d actual = prism.inSphere();
    assertEquals(actual.center().equals(new Vect3d(0, 0, 1)), true);
    assertEquals(actual.radius(), 1, Checker.eps());
  }
}