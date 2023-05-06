package geom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author alexeev
 */
public class Angle3dTest {
  double[] degreeValues;
  double[] radianValues;
  Angle3d[] angles;

  public Angle3dTest() {
    degreeValues = new double[] {
      0, 10, 180, 90, -180, 179, 900, -530, -720, -719
    };
    radianValues = new double[] {
      0, Math.PI / 3, Math.PI, Math.PI / 2, -Math.PI, 2 * Math.PI, 3 * Math.PI + 0.1, -3 * Math.PI + 0.1
    };
    angles = new Angle3d[] {
      new Angle3d(Vect3d.UX, Vect3d.O, Vect3d.UY), // right angle
      new Angle3d(Vect3d.UY, Vect3d.O, Vect3d.UX), // right angle
      new Angle3d(Vect3d.UY, Vect3d.O, Vect3d.UX, true), // right angle
      new Angle3d(Vect3d.UX, Vect3d.O, Vect3d.UY, false), // 270 degrees angle
      new Angle3d(
              new Vect3d(-1, -1, -1),
              new Vect3d(0, 0, 0),
              new Vect3d(1, 1, 1)) // unwrapped angle
    };
  }

    /**
    * Test of isRight method, of class Angle3d.
    */
    @Test
    public void testIsRight() {
        Assertions.assertTrue(angles[0].isRight());
        Assertions.assertTrue(angles[1].isRight());
        Assertions.assertTrue(angles[2].isRight());
        Assertions.assertFalse(angles[3].isRight());
        Assertions.assertFalse(angles[4].isRight());
    }

    /**
     * Test of toMinusPitoPiInterval method, of class Angle3d.
     */
    @Test
    public void testToMinusPitoPiInterval() {
        double[] result = new double[radianValues.length];
        for(int i = 0; i < result.length; i++) {
            result[i] = Angle3d.toMinusPitoPiInterval(radianValues[i]);
        }
        Assertions.assertArrayEquals(
            new double[] {0, Math.PI / 3, Math.PI, Math.PI / 2, Math.PI, 0, -Math.PI + 0.1, -Math.PI + 0.1},
            result,
            0.000001);
    }

    @Test
    public void testtoMinus180to180Interval() {
        double[] result = new double[degreeValues.length];
        for(int i = 0; i < result.length; i++) {
            result[i] = Angle3d.toMinus180to180Interval(degreeValues[i]);
        }
        Assertions.assertArrayEquals(
            new double[] {0, 10, 180, 90, 180, 179, 180, -170, 0, 1},
            result,
            0.000001);
    }

  /**
   * Test of bisectrix method, of class Angle3d.
   * @throws geom.ExDegeneration
   */
  @Test
  public void testBisectrix() throws ExDegeneration {
    Assertions.assertTrue(Line3d.equals(Line3d.line3dByTwoPoints(
            new Vect3d(0, 0, 0), new Vect3d(1, 1, 0)),
            angles[0].bisectrix()));

    Assertions.assertTrue(Line3d.equals(Line3d.line3dByTwoPoints(
            new Vect3d(0, 0, 0), new Vect3d(1, 1, 0)),
            angles[1].bisectrix()));

    Assertions.assertTrue(Line3d.equals(Line3d.line3dByTwoPoints(
            new Vect3d(0, 0, 0), new Vect3d(1, 1, 0)),
            angles[2].bisectrix()));

    Assertions.assertTrue(Line3d.equals(Line3d.line3dByTwoPoints(
            new Vect3d(0, 0, 0), new Vect3d(1, 1, 0)),
            angles[3].bisectrix()));
  }

  /**
   * Test of bisectrix method, of class Angle3d (exceptions).
   */
  @Test
  public void testBisectrixExcept() {
    Assertions.assertThrows(ExDegeneration.class, () -> {
        angles[4].bisectrix();
    });
  }

  /**
   * Test of deconstr method, of class Angle3d.
   */
  public void testDeconstr() {
  }

  /**
   * Test of constr method, of class Angle3d.
   */
  public void testConstr() {
  }

  /**
   * Test of intersectWithRay method, of class Angle3d.
   */
  public void testIntersectWithRay() {
  }

  /**
   * Test of intersect method, of class Angle3d.
   */
  public void testIntersect_Ray3d() throws Exception {
  }

  /**
   * Test of intersect method, of class Angle3d.
   */
  public void testIntersect_Circle3d() throws Exception {
  }

  /**
   * Test of intersect method, of class Angle3d.
   */
  public void testIntersect_Rib3d() throws Exception {
  }

  /**
   * Test of intersect method, of class Angle3d.
   */
  public void testIntersect_Line3d() throws Exception {
  }

  /**
   * Test of intersect method, of class Angle3d.
   */
  public void testIntersect_Polygon3d() throws Exception {
  }

  /**
   * Test of intersect method, of class Angle3d.
   */
  public void testIntersect_Arc3d() throws Exception {
  }

  /**
   * Test of intersect method, of class Angle3d.
   */
  public void testIntersect_Triang3d() throws Exception {
  }

  /**
   * Test of intersect method, of class Angle3d.
   */
  public void testIntersect_Angle3d() throws Exception {
  }

  /**
   * Test of getUpVect method, of class Angle3d.
   */
  public void testGetUpVect() {
  }

  /**
   * Test of isOrientable method, of class Angle3d.
   */
  public void testIsOrientable() {
  }

  /**
   * Test of type method, of class Angle3d.
   */
  public void testType() {
  }

}
