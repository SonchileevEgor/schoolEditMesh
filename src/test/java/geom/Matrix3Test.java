package geom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;


/**
 *
 * @author alexeev
 */
public class Matrix3Test {
  private Matrix3[] m;
  
  public Matrix3Test() {
    m = new Matrix3[] {
      new Matrix3(0, 0, 0, 0, 0, 0, 0, 0, 0),
      new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1),
      new Matrix3(1, 1, 1, 1, 1, 1, 1, 1, 1),
      new Matrix3(-1, 0, 0, 0, -1, 0, 0, 0, -1),
      new Matrix3(2, 1, 0, 1, 0, -1, 0, -1, -2)
    };
  }
  
  @Test
  public void testGetAsArray() {
    assertArrayEquals(m[0].getAsArray(), new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 1.0E-6);
    assertArrayEquals(m[1].getAsArray(), new double[]{1, 0, 0, 0, 1, 0, 0, 0, 1}, 1.0E-6);
    assertArrayEquals(m[2].getAsArray(), new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1}, 1.0E-6);
    assertArrayEquals(m[3].getAsArray(), new double[]{-1, 0, 0, 0, -1, 0, 0, 0, -1}, 1.0E-6);
    assertArrayEquals(m[4].getAsArray(), new double[]{2, 1, 0, 1, 0, -1, 0, -1, -2}, 1.0E-6);
  }

  @Test
  public void testMultiply_Matrix3_Matrix3() {
    System.out.println("multiply(Matrix3, Matrix3)");
    assertArrayEquals(Matrix3.multiply(m[0], m[0]).getAsArray(),
                             new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[0], m[1]).getAsArray(),
                             new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[0], m[2]).getAsArray(),
                             new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[0], m[3]).getAsArray(),
                             new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[0], m[4]).getAsArray(),
                             new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[1], m[1]).getAsArray(),
                             new double[]{1, 0, 0, 0, 1, 0, 0, 0, 1}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[1], m[2]).getAsArray(),
                             new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[1], m[3]).getAsArray(),
                             new double[]{-1, 0, 0, 0, -1, 0, 0, 0, -1}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[1], m[4]).getAsArray(),
                             new double[]{2, 1, 0, 1, 0, -1, 0, -1, -2}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[2], m[2]).getAsArray(),
                             new double[]{3, 3, 3, 3, 3, 3, 3, 3, 3}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[2], m[3]).getAsArray(),
                             new double[]{-1, -1, -1, -1, -1, -1, -1, -1, -1}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[2], m[4]).getAsArray(),
                             new double[]{3, 0, -3, 3, 0, -3, 3, 0, -3}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[3], m[3]).getAsArray(),
                             new double[]{1, 0, 0, 0, 1, 0, 0, 0, 1}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[3], m[4]).getAsArray(),
                             new double[]{-2, -1, 0, -1, 0, 1, 0, 1, 2}, 1.0E-6);
    assertArrayEquals(Matrix3.multiply(m[4], m[4]).getAsArray(),
                             new double[]{5, 2, -1, 2, 2, 2, -1, 2, 5}, 1.0E-6);
    
  }
  
  @Test
  public void testMultiply_double() {
    System.out.println("multiplyDouble(double)");
    assertArrayEquals(m[0].multiply(5).getAsArray(),
                             new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 1.0E-6);
    assertArrayEquals(m[1].multiply(3).getAsArray(),
                             new double[]{3, 0, 0, 0, 3, 0, 0, 0, 3}, 1.0E-6);
    assertArrayEquals(m[2].multiply(-6).getAsArray(),
                             new double[]{-6, -6, -6, -6, -6, -6, -6, -6, -6}, 1.0E-6);
    assertArrayEquals(m[3].multiply(3).getAsArray(),
                             new double[]{-3, 0, 0, 0, -3, 0, 0, 0, -3}, 1.0E-6);
    assertArrayEquals(m[4].multiply(7).getAsArray(),
                             new double[]{14, 7, 0, 7, 0, -7, 0, -7, -14}, 1.0E-6);
  }
  
  
  @Test
  public void testSum() {
    System.out.println("sum(Matrix3, Matrix3)");
    assertArrayEquals(Matrix3.sum(m[0], m[0]).getAsArray(),
                             new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[0], m[1]).getAsArray(),
                             new double[]{1, 0, 0, 0, 1, 0, 0, 0, 1}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[0], m[2]).getAsArray(),
                             new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[0], m[3]).getAsArray(),
                             new double[]{-1, 0, 0, 0, -1, 0, 0, 0, -1}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[0], m[4]).getAsArray(),
                             new double[]{2, 1, 0, 1, 0, -1, 0, -1, -2}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[1], m[1]).getAsArray(),
                             new double[]{2, 0, 0, 0, 2, 0, 0, 0, 2}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[1], m[2]).getAsArray(),
                             new double[]{2, 1, 1, 1, 2, 1, 1, 1, 2}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[1], m[3]).getAsArray(),
                             new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[1], m[4]).getAsArray(),
                             new double[]{3, 1, 0, 1, 1, -1, 0, -1, -1}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[2], m[2]).getAsArray(),
                             new double[]{2, 2, 2, 2, 2, 2, 2, 2, 2}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[2], m[3]).getAsArray(),
                             new double[]{0, 1, 1, 1, 0, 1, 1, 1, 0}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[2], m[4]).getAsArray(),
                             new double[]{3, 2, 1, 2, 1, 0, 1, 0, -1}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[3], m[3]).getAsArray(),
                             new double[]{-2, 0, 0, 0, -2, 0, 0, 0, -2}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[3], m[4]).getAsArray(),
                             new double[]{1, 1, 0, 1, -1, -1, 0, -1, -3}, 1.0E-6);
    assertArrayEquals(Matrix3.sum(m[4], m[4]).getAsArray(),
                             new double[]{4, 2, 0, 2, 0, -2, 0, -2, -4}, 1.0E-6);
  }

  @Test
  public void testSolve() throws Exception {
    // TODO: написать метод
  }

  @Test
  public void testMulOnVect() {
    // TODO: написать метод
  }

  @Test
  public void testAdd() {
    // TODO: написать метод
  }

  @Test
  public void testRightMultiply() {
    // TODO: написать метод
  }

  @Test
  public void testLeftMultiply() {
    // TODO: написать метод
  }

  @Test
  public void testMultiply_Matrix3_double() {
    // TODO: написать метод
  }

  @Test
  public void testData() {
    // TODO: написать метод
  }

  @Test
  public void testGetRotateMatrix_4args() {
    // TODO: написать метод
  }

  @Test
  public void testGetRotateMatrix_double_Vect3d() {
    // TODO: написать метод
  }
  
}
