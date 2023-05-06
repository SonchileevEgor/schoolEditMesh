package geom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpherCoordTest {
  // Data test
  private Map<Vect3d, SpherCoord> _data = new HashMap<>();

  @BeforeEach
  public void setUp() throws Exception {
    // My tests
    _data.put(new Vect3d(0, 0, 1), new SpherCoord(0, 0, 1));
    _data.put(new Vect3d(1, 0, 0), new SpherCoord(0, Angle3d.degree2Radians(90), 1));
    _data.put(new Vect3d(1, 1, 0), new SpherCoord(
            Angle3d.degree2Radians(45),
            Angle3d.degree2Radians(90),
            Math.sqrt(2)
    ));
    _data.put(new Vect3d(-1, -1, 0), new SpherCoord(
            Angle3d.degree2Radians(-135),
            Angle3d.degree2Radians(90),
            Math.sqrt(2)
    ));

    // Test on centerSpherical
    _data.put(new Vect3d(1, 1, 1), new SpherCoord(0, 0, 0, new Vect3d(1, 1, 1)));
    _data.put(new Vect3d(E, E, E + 1), new SpherCoord(0, 0, 1, new Vect3d(E, E, E)));


    // Examples from http://mathhelpplanet.com/static.php?p=sfericheskie-koordinaty
    _data.put(new Vect3d(4, -3, 12), new SpherCoord(-atan(3.0 / 4), acos(12.0 / 13), 13));
    _data.put(new Vect3d(-sqrt(2), sqrt(6), -2 * sqrt(2)), new SpherCoord(2 * PI / 3, 3 * PI / 4, 4));
  }

  @AfterEach
  public void tearDown() throws Exception {

  }

  @Test
  public void testToCartesian() throws Exception {
    for(Map.Entry<Vect3d, SpherCoord> entry : _data.entrySet()) {
      Vect3d key = entry.getKey();
      SpherCoord value = entry.getValue();
      Vect3d res = value.toCartesian();
      assertEquals(key, res);
    }
  }

  @Test
  public void testFromCartesian() throws Exception {
    for(Map.Entry<Vect3d, SpherCoord> entry : _data.entrySet()) {
      Vect3d key = entry.getKey();
      SpherCoord value = entry.getValue();
      SpherCoord res = new SpherCoord(key, value.getCenterSpherical());
      assertTrue(value.isEqual(res));
    }
  }

  @Test
  public void testSetAll() throws Exception {
    SpherCoord spherCoord = new SpherCoord(PI / 6, PI / 4, 10);
    SpherCoord spherCoord2 = new SpherCoord(13 * PI / 6, -15 * PI / 4, 10);

    SpherCoord spherCoord3 = new SpherCoord(0, 0, 1);
    SpherCoord spherCoord4 = new SpherCoord(-2 * PI, 100 * 2 * PI, 1);
    assertTrue(spherCoord.isEqual(spherCoord2));
    assertTrue(spherCoord3.isEqual(spherCoord4));
  }

  @Test
  public void testIsBigTheta() throws Exception {
    ArrayList<SpherCoord> normThetaList = new ArrayList<>();
    ArrayList<SpherCoord> bigThetaList = new ArrayList<>();

    normThetaList.add(new SpherCoord(0, 0, 0));
    normThetaList.add(new SpherCoord(0, PI / 4, 0));
    normThetaList.add(new SpherCoord(0, PI / 2, 0));
    normThetaList.add(new SpherCoord(0, PI, 0));

    bigThetaList.add(new SpherCoord(0, PI + PI / 100, 0));
    bigThetaList.add(new SpherCoord(0, PI + 3 * PI / 4, 0));
    bigThetaList.add(new SpherCoord(0, PI + 99 * PI / 100, 0));

    for (int i = 0; i < normThetaList.size(); i++) {
        assertFalse(normThetaList.get(i).isBigTheta(),
                String.format("BigTheta of element num %d", i));
    }
    for (int i = 0; i < bigThetaList.size(); i++) {
        assertTrue(bigThetaList.get(i).isBigTheta(),
                String.format("BigTheta of element num %d", i));
    }
  }
}