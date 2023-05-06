package util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author alexeev
 */
public class UtilTest {
  private String[] s1, s2, s3, s4, s5;
  private boolean[] b1, b2, b3, b4, b5;

  @BeforeEach
  public void setUp() {
    s1 = new String[] {"F", "F", "F", "A"};
    s2 = new String[] {"f", "F", "C", "U", "ck"};
    s3 = new String[] {"A"};
    s4 = new String[] {};
    s5 = new String[] {"A", "B", "A", "A"};
    b1 = new boolean[] {true, true, false, true};
    b2 = new boolean[] {true, true, true, true, true};
    b3 = new boolean[] {false};
    b4 = new boolean[] {};
    b5 = new boolean[] {false, true, true, false};
  }

  /**
   * Test of hasDuplicates method, of class Util.
   */
  @Test
  public void testHasDuplicates() {
    setUp();
    System.out.println("hasDuplicates");
    assertTrue(Util.hasDuplicates(s1));
    assertFalse(Util.hasDuplicates(s2));
    assertFalse(Util.hasDuplicates(s3));
    assertFalse(Util.hasDuplicates(s4));
  }

  @Test
  public void testConcat() {
    System.out.println("concat");
    assertEquals("FFFA", Util.concat(s1));
    assertEquals("fFCUck", Util.concat(s2));
    assertEquals("A", Util.concat(s3));
    assertEquals("", Util.concat(s4));
  }

  @Test
  public void testHasDuplicates_2() {
    System.out.println("hasDuplicates");
    assertTrue(Util.hasDuplicates(s1, b1));
    assertFalse(Util.hasDuplicates(s2, b2));
    assertFalse(Util.hasDuplicates(s3, b3));
    assertFalse(Util.hasDuplicates(s4, b4));
    assertFalse(Util.hasDuplicates(s5, b5));
  }
}
