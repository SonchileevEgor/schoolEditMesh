package gui;

import bodies.BodyType;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author alexeev
 */
public class IconListTest {

  public IconListTest() {
  }

  @BeforeEach
  public void setUp() {
  }

  @AfterEach
  public void tearDown() {
  }

  /**
   * Test of getByBodyType method, of class IconList.
   */
  @Test
  public void testGetByBodyType() {
    System.out.println("getByBodyType");
    for( BodyType type : BodyType.values() ){
      try {
        IconList.getByBodyType(type);
      } catch( IllegalArgumentException ex ){
        fail(type.toString());
      }
    }
  }
}
