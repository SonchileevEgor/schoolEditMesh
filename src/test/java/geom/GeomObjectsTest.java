package geom;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class GeomObjectsTest {
  // List of methods
  private ArrayList<String> _methods = new ArrayList<>(Arrays.asList("intersect"));
  // List of types to find methods
  private ArrayList<Class<?>> _types = new ArrayList<Class<?>>(Arrays.asList(Polygon3d.class));
  // Tables to compare
  private boolean[][][] _table = {
      {{true}},
  };

  @BeforeEach
  public void setUp() throws Exception {
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void testHasFunction() throws Exception {
    // Check table size
    assertEquals(_table.length, _types.size());
    for (boolean[][] row : _table) {
      assertEquals(row.length, _types.size());
      for (boolean[] cell : row)
        assertEquals(cell.length, _methods.size());
    }

    // Check methods
    for (int i = 0; i < _types.size(); i++) {
      for (int j = 0; j < _table[i].length; j++) {
        Class<?> type = _types.get(i);
        boolean[] row = _table[i][j];
        for (int k = 0; k < _methods.size(); k++) {
          if (row[k]) {
            String method = _methods.get(k);
            Method m = type.getMethod(method, _types.get(j));
          }
        }
      }
    }
  }

}