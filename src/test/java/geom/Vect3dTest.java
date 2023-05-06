package geom;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Vect3dTest {

  private Map<ArrayList<Vect3d>, Vect3d> centerMassExamples = new HashMap<>();
  private Map<ArrayList<Vect3d[]>, Boolean> equalsArrayOfVect = new HashMap<>();


  @BeforeEach
  public void setUp() throws Exception {
    ArrayList<Vect3d> points = new ArrayList<>();
    centerMassExamples.put(points, null);

    points = new ArrayList<>();
    points.add(new Vect3d(1, 1, 1));
    centerMassExamples.put(points, new Vect3d(1, 1, 1));

    points = new ArrayList<>();
    points.add(new Vect3d(0, 0, 0));
    points.add(new Vect3d(2, 0, 0));
    centerMassExamples.put(points, new Vect3d(1, 0, 0));

    points = new ArrayList<>();
    points.add(new Vect3d(1, 0, 0));
    points.add(new Vect3d(-1, 0, 0));
    points.add(new Vect3d(0, 3, 0));
    centerMassExamples.put(points, new Vect3d(0, 1, 0));

    points = new ArrayList<>();
    points.add(new Vect3d(1, 0, 0));
    points.add(new Vect3d(-1, 0, 0));
    points.add(new Vect3d(0, 1, 0));
    points.add(new Vect3d(0, -1, 0));
    centerMassExamples.put(points, new Vect3d(0, 0, 0));

// Примеры для проверки равенства массивов точек
    ArrayList<Vect3d[]> point = new ArrayList<Vect3d[]>();
    Vect3d[] vectors1 = new Vect3d[3];
    vectors1[0] = new Vect3d(1, 0, 0);
    vectors1[1] = new Vect3d(0, 1, 0);
    vectors1[2] = new Vect3d(0, 0, 1);
    point.add(vectors1);

    Vect3d[] Vectors2 = new Vect3d[3];
    Vectors2[0] = new Vect3d(1, 0, 0);
    Vectors2[1] = new Vect3d(0, 1, 0);
    Vectors2[2] = new Vect3d(0, 0, 1);
    point.add(Vectors2);

    equalsArrayOfVect.put(point, true);

    point = new ArrayList<Vect3d[]>();
    vectors1 = new Vect3d[3];
    vectors1[0] = new Vect3d(1, 0, 0);
    vectors1[1] = new Vect3d(0, 1, 0);
    vectors1[2] = new Vect3d(0, 0, 1);
    point.add(vectors1);

   Vectors2 = new Vect3d[3];
    Vectors2[0] = new Vect3d(1, 0, 0);
    Vectors2[1] = new Vect3d(0, 1, 0);
    Vectors2[2] = new Vect3d(0, 0, -1);
    point.add(Vectors2);

    equalsArrayOfVect.put(point, Boolean.FALSE);

    point = new ArrayList<Vect3d[]>();
    vectors1 = new Vect3d[3];
    vectors1[0] = new Vect3d(1, 0, 0);
    vectors1[1] = new Vect3d(0, 1, 0);
    vectors1[2] = new Vect3d(0, 0, 1);
    point.add(vectors1);

   Vectors2 = new Vect3d[4];
    Vectors2[0] = new Vect3d(1, 0, 0);
    Vectors2[1] = new Vect3d(0, 1, 0);
    Vectors2[2] = new Vect3d(0, 0, 1);
    Vectors2[3] = new Vect3d(0, 0, 1);
    point.add(Vectors2);
    equalsArrayOfVect.put(point, Boolean.FALSE);

    point = new ArrayList<Vect3d[]>();
    vectors1 = new Vect3d[4];
    vectors1[0] = new Vect3d(1, 0, 0);
    vectors1[1] = new Vect3d(0, 1, 0);
    vectors1[2] = new Vect3d(0, 0, 1);
    vectors1[3] = new Vect3d(0, 0, 1);
    point.add(vectors1);

   Vectors2 = new Vect3d[3];
    Vectors2[0] = new Vect3d(1, 0, 0);
    Vectors2[1] = new Vect3d(0, 1, 0);
    Vectors2[2] = new Vect3d(0, 0, 1);

    point.add(Vectors2);

    equalsArrayOfVect.put(point, Boolean.FALSE);


  }

  @Test
  public void testCenterOfMass() throws Exception {
    for(Map.Entry<ArrayList<Vect3d>, Vect3d> entry : centerMassExamples.entrySet()){
      ArrayList<Vect3d> key = entry.getKey();
      Vect3d value = entry.getValue();
      Vect3d res = Vect3d.centerOfMass(key);
      assertEquals(value, res);
    }
  }

@Test
  public void testequalsArrayOfVect() throws Exception {
    for(Map.Entry<ArrayList<Vect3d[]>, Boolean> entry : equalsArrayOfVect.entrySet()){
      ArrayList<Vect3d[]> key = entry.getKey();
      Boolean value = entry.getValue();
      Boolean res = Vect3d.equals(key.get(0), key.get(1));
      assertEquals(value, res);
    }
  }
}