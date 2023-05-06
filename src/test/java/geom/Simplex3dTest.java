package geom;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 *
 * @author alexeev
 */
public class Simplex3dTest {
  private Simplex3d _s;
  
  @BeforeEach
  public void setUp(){
    try {
      // Regular tetrahedron with edge length 2.
      _s = new Simplex3d(new Vect3d(1, 0, -1 / Math.sqrt(2)),
                                   new Vect3d(-1, 0, -1 / Math.sqrt(2)),
                                   new Vect3d(0, 1, 1 / Math.sqrt(2)),
                                   new Vect3d(0, -1, 1 / Math.sqrt(2)));
    } catch (ExDegeneration ex) {
      fail("Simplex3d constructor is incorrect");
    }
  }

  @Test
  public void testOutSphere() {
    Sphere3d outSphere = _s.outSphere();
    assertEquals(outSphere.radius(), Math.sqrt(1.5), Checker.eps(),
            "Simplex3d.outSphere() function is incorrect (radius is wrong)");
    assertEquals(Vect3d.dist(outSphere.center(), new Vect3d()), 0, Checker.eps(),
            "Simplex3d.outSphere() function is incorrect (center is wrong)");
  }

  @Test
  public void testInSphere() throws ExGeom {
    Sphere3d inSphere = _s.inSphere();
    assertEquals(inSphere.radius(), 1 / Math.sqrt(6), Checker.eps(),
            "Simplex3d.inSphere() function is incorrect (radius is wrong)");
    assertEquals(Vect3d.dist(inSphere.center(), new Vect3d()), 0, Checker.eps(),
            "Simplex3d.inSphere() function is incorrect (center is wrong)");
  }


  @Test
  public void testFaceArea() {
    double idealFaceArea = Math.sqrt(3.0);
    assertEquals(_s.faceArea(0), idealFaceArea, Checker.eps(),
            "Simplex3d.faceArea() function is incorrect (at index 0)");
    assertEquals(_s.faceArea(1), idealFaceArea, Checker.eps(),
            "Simplex3d.faceArea() function is incorrect (at index 1)");
    assertEquals(_s.faceArea(2), idealFaceArea, Checker.eps(),
            "Simplex3d.faceArea() function is incorrect (at index 2)");
    assertEquals(_s.faceArea(3), idealFaceArea, Checker.eps(),
            "Simplex3d.faceArea() function is incorrect (at index 3)");
  }


  @Test
  public void testFullArea() {
    assertEquals(_s.surfaceArea(), 4 * Math.sqrt(3), Checker.eps(),
            "Simplex3d.fullArea() function is incorrect");
  }

  @Test
  public void testVolume() {
    assertEquals(_s.volume(), 2 * Math.sqrt(2) / 3, Checker.eps(), "Simplex3d.volume() function is incorrect");
  }
}
