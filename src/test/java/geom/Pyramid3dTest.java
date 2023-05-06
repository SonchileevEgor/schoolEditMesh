package geom;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author alexeev
 */
public class Pyramid3dTest {
  private Pyramid3d _pyr1;

  @BeforeEach
  public void setUp() throws ExDegeneration, ExZeroDivision {
    ArrayList<Vect3d> pts = new ArrayList<>();
    pts.add(new Vect3d(0, 0, 0));
    pts.add(new Vect3d(0, 1, 0));
    pts.add(new Vect3d(1, 1, 0));
    pts.add(new Vect3d(1, 0, 0));
    pts.add(new Vect3d(0.5, 0.5, 2));
    _pyr1 = new Pyramid3d(pts);
  }

  @Test
  public void testHeightLength() {
    assertEquals(2, _pyr1.heightLength(), Checker.eps());
  }

  @Test
  public void testVolume() {
    assertEquals(2.0 / 3, _pyr1.volume(), Checker.eps());
  }

  @Test
  public void testLateralSurfaceArea() {
    assertEquals(Math.sqrt(17), _pyr1.lateralSurfaceArea(), Checker.eps());
  }

  @Test
  public void testSurfaceArea() {
    assertEquals(Math.sqrt(17) + 1, _pyr1.surfaceArea(), Checker.eps());
  }

  @Test
  public void testInSphere1() throws ExGeom{
    ArrayList<Vect3d> pts = new ArrayList<>();
    pts.add(new Vect3d(1.15, -1.15, 0));
    pts.add(new Vect3d(1.12, 0.55, 0));
    pts.add(new Vect3d(-0.86, 1, 0));
    pts.add(new Vect3d(-0.75, -0.98, 0.8));
    Pyramid3d pyr = new Pyramid3d(pts);
    Sphere3d actual = pyr.inSphere();
    assertEquals(actual.center().equals(new Vect3d(0.275074, -0.140945, 0.173338)), true);
    assertEquals(actual.radius(), 0.173338, Checker.eps());
  }

  @Test
  public void testInSphere2() throws ExGeom{
    ArrayList<Vect3d> pts = new ArrayList<>();
    pts.add(new Vect3d(0, 0, 0));
    pts.add(new Vect3d(0, 0, 1));
    pts.add(new Vect3d(1, 0, 0));
    pts.add(new Vect3d(0, 1, 0));
    Pyramid3d pyr = new Pyramid3d(pts);
    Sphere3d actual = pyr.inSphere();
    assertEquals(actual.center().equals(new Vect3d(0.211325, 0.211325, 0.211325)), true);
    assertEquals(actual.radius(), 0.211325, Checker.eps());
  }
}