package geom;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author alexeev
 */
public class Hyperbole3dTest {
  Hyperbole3d hyp1;

  public Hyperbole3dTest() {
    try {
      hyp1 = new Hyperbole3d(new Vect3d(-1, -1, 0), new Vect3d(1, 1, 0), new Vect3d(1, 0.5, 0));
    } catch( ExDegeneration ex ) {
      fail();
    }
  }

  @BeforeEach
  public void setUp() {
  }

  @AfterEach
  public void tearDown() {
  }

  /**
   * Test of f1 method, of class Hyperbole3d.
   */
  public void testF1() {
  }

  /**
   * Test of f2 method, of class Hyperbole3d.
   */
  public void testF2() {
  }

  /**
   * Test of newSystemOfCoorHyperb method, of class Hyperbole3d.
   */
  public void testNewSystemOfCoorHyperb() {
  }

  /**
   * Test of normal method, of class Hyperbole3d.
   */
  public void testNormal() throws Exception {
  }

  /**
   * Test of contains method, of class Hyperbole3d.
   */
  @Test
  public void testContains() {
    assertTrue(hyp1.contains(new Vect3d(2, 0.25, 0)));
    assertTrue(hyp1.contains(new Vect3d(4, 0.125, 0)));
    assertTrue(hyp1.contains(new Vect3d(-8, -0.0625, 0)));
    assertFalse(hyp1.contains(new Vect3d(-8, -0.0625, 1)));
    assertFalse(hyp1.contains(new Vect3d(-8, 0.0625, 1)));
  }

  /**
   * Test of center method, of class Hyperbole3d.
   */
  public void testCenter() {
  }

  /**
   * Test of realAxle method, of class Hyperbole3d.
   */
  public void testRealAxle() {
  }

  /**
   * Test of b method, of class Hyperbole3d.
   */
  public void testB() {
  }

  /**
   * Test of e method, of class Hyperbole3d.
   */
  public void testE() {
  }

  /**
   * Test of getTopRightHyb method, of class Hyperbole3d.
   */
  public void testGetTopRightHyb() {
  }

  /**
   * Test of getTopLeftHyb method, of class Hyperbole3d.
   */
  public void testGetTopLeftHyb() {
  }

  /**
   * Test of getPointImagynaryAxle method, of class Hyperbole3d.
   */
  public void testGetPointImagynaryAxle() throws Exception {
  }

  /**
   * Test of getPointThridAxle method, of class Hyperbole3d.
   */
  public void testGetPointThridAxle() throws Exception {
  }

  /**
   * Test of plane method, of class Hyperbole3d.
   */
  public void testPlane() throws Exception {
  }

  /**
   * Test of getPoint method, of class Hyperbole3d.
   */
  public void testGetPoint() {
  }

  /**
   * Test of intersect method, of class Hyperbole3d.
   */
  public void testIntersect() {
  }

  /**
   * Test of deconstr method, of class Hyperbole3d.
   */
  public void testDeconstr() {
  }

  /**
   * Test of constr method, of class Hyperbole3d.
   */
  public void testConstr() {
  }

  /**
   * Test of intersectWithRay method, of class Hyperbole3d.
   */
  public void testIntersectWithRay() {
  }

  /**
   * Test of getUpVect method, of class Hyperbole3d.
   */
  public void testGetUpVect() {
  }

  /**
   * Test of isOrientable method, of class Hyperbole3d.
   */
  public void testIsOrientable() {
  }

  /**
   * Test of type method, of class Hyperbole3d.
   */
  public void testType() {
  }

}
