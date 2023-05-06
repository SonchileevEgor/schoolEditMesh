package geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for checking Checker class
 * @author rita
 */
public class CheckerTest {
    private Vect3d[] vects;
    private double[] num;

    private Map<Vect3d[], Boolean> threeVectorsExamples;

    @BeforeEach
    public void setUp() {
        threeVectorsExamples = new HashMap<>();
        
        vects = new Vect3d[16];
        vects[0] = new Vect3d(0, 0, 0);
        vects[1] = new Vect3d(1, 0, 0);
        vects[2] = new Vect3d(0, 1 + Checker.eps() / 2, 0);
        vects[3] = new Vect3d(0, 0, 1);
        vects[4] = new Vect3d(-1 - Checker.eps() / 3, 0, 0);
        vects[5] = new Vect3d(0, -1 - Checker.eps() * 2, 0);
        vects[6] = new Vect3d(0, 0, -1);
        vects[7] = new Vect3d(1, 1, 0);
        vects[8] = new Vect3d(2, 8, -0.5);
        vects[9] = new Vect3d(4, -4, 2.2);
        vects[10] = new Vect3d(2.4, 1, 1);
        vects[11] = new Vect3d(-1, 1.2, 1.2);
        vects[12] = new Vect3d(Math.sqrt(2), -Math.sqrt(2), Math.sqrt(5));
        vects[13] = new Vect3d(3, 0, 0);
        vects[14] = new Vect3d(0, 1, 0);
        vects[15] = new Vect3d(1, -2, 0);

        num = new double[5];
        num[0] = 0;
        num[1] = Checker.eps() / 2;
        num[2] = 1.5;
        num[3] = 1.5 - Checker.eps() / 3;
        num[4] = -Checker.eps() * 2;

        Vect3d[] threeVectors = new Vect3d[3];
        threeVectorsExamples.put(threeVectors, null);

        threeVectors[0] = new Vect3d(1, 0, 0);
        threeVectors[1] = new Vect3d(0, 1, 0);
        threeVectors[2] = new Vect3d(0, 0, 1);
        threeVectorsExamples.put(threeVectors, true);

        threeVectors[0] = new Vect3d(1, 0, 0);
        threeVectors[1] = new Vect3d(0, 1, 0);
        threeVectors[2] = new Vect3d(0, 0, -1);
        threeVectorsExamples.put(threeVectors, false);
    }

    @Test
    public void testisPlaneContainPoint() throws ExDegeneration {
        Plane3d plane = new Plane3d(new Vect3d(0, 6, 0),
                new Vect3d(9, 0, 0),
                new Vect3d(0, 0, 0));

        ArrayList<Vect3d> points = new ArrayList<>();
        points.add(new Vect3d(2, 2, 0));
        points.add(new Vect3d(5, 1, 0));
        points.add(new Vect3d(2.97026, 4.01983, 0)); 
        points.add(new Vect3d(4.54185, 2.9721, 0));
        assertEquals(true, Checker.isPlaneContainPoint(plane, points.get(0)));
        assertEquals(true, Checker.isPlaneContainPoint(plane, points.get(1)));
        assertEquals(true, Checker.isPlaneContainPoint(plane, points.get(2)));
        assertEquals(true, Checker.isPlaneContainPoint(plane, points.get(3)));
    }
  
    @Test
    public void testisThreeVectorsRight() throws Exception {
        for(Map.Entry<Vect3d[], Boolean> entry : threeVectorsExamples.entrySet()){
            Vect3d[] key = entry.getKey();
            Boolean value = entry.getValue();
            Boolean res = Checker.isThreeVectorsRight(key[0], key[1], key[2]);
            assertEquals(value, res);
        }
    }

    @Test
    public void testIsEqual() {
        System.out.println("isEqual");
        assertEquals(true, Checker.isEqual(num[0], num[0]));
        assertEquals(true, Checker.isEqual(num[0], num[1]));
        assertEquals(false, Checker.isEqual(num[0], num[2]));
        assertEquals(true, Checker.isEqual(num[2], num[3]));
        assertEquals(false, Checker.isEqual(num[0], num[4]));
        assertEquals(false, Checker.isEqual(num[1], num[4]));
    }

    @Test
    public void testIsOrthogonal() {
        System.out.println("isOrthogonal");
        assertEquals(true, Checker.isOrthogonal(vects[1], vects[2]));
        assertEquals(true, Checker.isOrthogonal(vects[1], vects[3]));
        assertEquals(false, Checker.isOrthogonal(vects[1], vects[1]));
        assertEquals(true, Checker.isOrthogonal(vects[10], vects[11]));
        assertEquals(true, Checker.isOrthogonal(vects[6], vects[7]));
        assertEquals(false, Checker.isOrthogonal(vects[1], vects[4]));
    }

    @Test
    public void testIsEqualNorm() {
        System.out.println("isEqualNorm");
        assertEquals(false, Checker.isEqualNorm(vects[0], vects[1]));
        assertEquals(true, Checker.isEqualNorm(vects[1], vects[2]));
        assertEquals(false, Checker.isEqualNorm(vects[1], vects[5]));
        assertEquals(true, Checker.isEqualNorm(vects[2], vects[4]));
        assertEquals(false, Checker.isEqualNorm(vects[6], vects[7]));
        assertEquals(true, Checker.isEqualNorm(vects[12], vects[13]));
        assertEquals(false, Checker.isEqualNorm(vects[5], vects[6]));
    }

    @Test
    public void testThreePointsOnTheLine() {
        System.out.println("threePointsOnTheLine");
        assertEquals(true, Checker.threePointsOnTheLine(vects[0], vects[1], vects[4]));
        assertEquals(true, Checker.threePointsOnTheLine(vects[0], vects[2], vects[5]));
        assertEquals(true, Checker.threePointsOnTheLine(vects[0], vects[1], vects[13]));
        assertEquals(true, Checker.threePointsOnTheLine(vects[0], vects[0], vects[8]));
        assertEquals(true, Checker.threePointsOnTheLine(vects[0], vects[2], vects[14]));
        assertEquals(false, Checker.threePointsOnTheLine(vects[13], vects[14], vects[15]));
    }
    
    @Test
    public void testLieOnOneLine() throws ExDegeneration, ExZeroDivision, ExGeom {
        ArrayList<Vect3d> p1= new ArrayList<>();
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,2,0));
        p1.add(new Vect3d(0,3,0));
        p1.add(new Vect3d(0,4,0));
        p1.add(new Vect3d(0,5,0));
        assertEquals(Checker.lieOnOneLine(p1) , true);
    }
    
    @Test
    public void testLieOnOneLine1() throws ExDegeneration, ExZeroDivision, ExGeom{
        ArrayList<Vect3d> p1= new ArrayList<>();
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,4,0));
        p1.add(new Vect3d(0,5,0));
        assertEquals(Checker.lieOnOneLine(p1) , true);
    }
  
    @Test
    public void testLieOnOneLine2() throws ExDegeneration, ExZeroDivision, ExGeom{
        ArrayList<Vect3d> p1= new ArrayList<>();
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,0,0));
        assertEquals(Checker.lieOnOneLine(p1) , true);
    }
    
    @Test
    public void testLieOnOneLine3() throws ExDegeneration, ExZeroDivision, ExGeom{
        ArrayList<Vect3d> p1= new ArrayList<>();
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,1,2));
        p1.add(new Vect3d(0,5,0));
        assertEquals(Checker.lieOnOneLine(p1) , false);
    }
  
    @Test
    public void testLieOnOneLine4() throws ExDegeneration, ExZeroDivision, ExGeom{
        ArrayList<Vect3d> p1= new ArrayList<>();
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,0,0));
        assertEquals(Checker.lieOnOneLine(p1) , true);
    }

    @Test
    public void testAtLeast3PointsOneline1() throws ExDegeneration, ExZeroDivision, ExGeom{
        ArrayList<Vect3d> p1= new ArrayList<>();
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,2,0));
        p1.add(new Vect3d(0,3,0));
        p1.add(new Vect3d(0,4,0));
        p1.add(new Vect3d(0,5,0));
        assertEquals(Checker.atLeast3PointsOneline(p1) , true);
    }
   
    @Test
    public void testAtLeast3PointsOneline2() throws ExDegeneration, ExZeroDivision, ExGeom{
        ArrayList<Vect3d> p1= new ArrayList<>();
        p1.add(new Vect3d(0,0,0));
        p1.add(new Vect3d(0,1,0));
        p1.add(new Vect3d(0,2,0));
        p1.add(new Vect3d(0,3,3));
        p1.add(new Vect3d(0,3,6));
        p1.add(new Vect3d(1,2,7));
        assertEquals(Checker.atLeast3PointsOneline(p1) , true);
    }
   
    @Test
    public void testAtLeast3PointsOneline3() throws ExDegeneration, ExZeroDivision, ExGeom{
        ArrayList<Vect3d> p1= new ArrayList<>();
        p1.add(new Vect3d(0, 0, 0));
        p1.add(new Vect3d(0, 1, 0));
        p1.add(new Vect3d(0, 1, 2));
        p1.add(new Vect3d(1, 1, 1));
        assertEquals(Checker.atLeast3PointsOneline(p1) , false);
    }

    @Test
    public void testIsCheckIntersectRib() {
        System.out.println("geom.CheckerTest.testIsCheckIntersectRib()");
        ArrayList<Rib3d> list = new ArrayList<>();
        try {
            list.add(new Rib3d(new Vect3d(-3.578, -13.840, 40.000), new Vect3d(4.522, -23.712, 40.000)));
            list.add(new Rib3d(new Vect3d(4.522, -23.712, 40.000), new Vect3d(20.950, -14.146, 40.000)));
            list.add(new Rib3d(new Vect3d(20.950, -14.146, 40.000), new Vect3d(11.201, -5.310, 40.000)));
            list.add(new Rib3d(new Vect3d(25.193, 2.767, 40.000), new Vect3d(11.201, -5.310, 40.000)));
            list.add(new Rib3d(new Vect3d(-17.667, -21.973, 40.000), new Vect3d(-3.578, -13.840, 40.000)));
            list.add(new Rib3d(new Vect3d(-3.578, -13.840, 40.000), new Vect3d(11.201, -5.310, 40.000)));
            list.add(new Rib3d(new Vect3d(25.193, 2.767, 40.000), new Vect3d(20.950, -14.146, 40.000)));
            list.add(new Rib3d(new Vect3d(4.522, -23.712, 40.000), new Vect3d(11.201, -5.310, 40.000)));
            list.add(new Rib3d(new Vect3d(-17.667, -21.973, 40.000), new Vect3d(4.522, -23.712, 40.000)));
            list.add(new Rib3d(new Vect3d(-3.578, -13.840, 40.000), new Vect3d(20.950, -14.146, 40.000)));
            list.add(new Rib3d(new Vect3d(25.188, -46.720, 40.000), new Vect3d(4.522, -23.712, 40.000)));
            list.add(new Rib3d(new Vect3d(25.188, -46.720, 40.000), new Vect3d(20.950, -14.146, 40.000)));
            list.add(new Rib3d(new Vect3d(25.188, -46.720, 40.000), new Vect3d(11.201, -5.310, 40.000)));
            list.add(new Rib3d(new Vect3d(25.188, -46.720, 40.000), new Vect3d(25.193, 2.767, 40.000)));
            list.add(new Rib3d(new Vect3d(25.188, -46.720, 40.000), new Vect3d(-17.667, -21.973, 40.000)));
        } catch (ExDegeneration ex) {
            Logger.getLogger(CheckerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(-3.578, -13.840, 40.000), new Vect3d(4.522, -23.712, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(4.522, -23.712, 40.000), new Vect3d(20.950, -14.146, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(20.950, -14.146, 40.000), new Vect3d(11.201, -5.310, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(25.193, 2.767, 40.000), new Vect3d(11.201, -5.310, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(-17.667, -21.973, 40.000), new Vect3d(-3.578, -13.840, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(-3.578, -13.840, 40.000), new Vect3d(11.201, -5.310, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(25.193, 2.767, 40.000), new Vect3d(20.950, -14.146, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(4.522, -23.712, 40.000), new Vect3d(11.201, -5.310, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(-17.667, -21.973, 40.000), new Vect3d(4.522, -23.712, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(-3.578, -13.840, 40.000), new Vect3d(20.950, -14.146, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(25.188, -46.720, 40.000), new Vect3d(4.522, -23.712, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(25.188, -46.720, 40.000), new Vect3d(20.950, -14.146, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(25.188, -46.720, 40.000), new Vect3d(11.201, -5.310, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(25.188, -46.720, 40.000), new Vect3d(25.193, 2.767, 40.000))));
            assertTrue(Checker.checkIntersectRib(list, 
                    new Rib3d(new Vect3d(25.188, -46.720, 40.000), new Vect3d(-17.667, -21.973, 40.000))));
        } catch (ExDegeneration ex) {
            Logger.getLogger(CheckerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    @Test
    public void testIsPointInSphere() {
        System.out.println("isPointInSphere");
        Sphere3d sphere = new Sphere3d(Vect3d.O, 1.0);
        assertTrue(Checker.isPointInSphere(vects[0], sphere));
        assertTrue(Checker.isPointInSphere(vects[1], sphere));
        assertTrue(Checker.isPointInSphere(vects[2], sphere));
        assertTrue(Checker.isPointInSphere(vects[3], sphere));
        assertTrue(Checker.isPointInSphere(vects[4], sphere));
        assertFalse(Checker.isPointInSphere(vects[5], sphere));
        assertTrue(Checker.isPointInSphere(vects[6], sphere));
        assertFalse(Checker.isPointInSphere(vects[7], sphere));
        assertFalse(Checker.isPointInSphere(vects[8], sphere));
        assertFalse(Checker.isPointInSphere(vects[9], sphere));
        assertFalse(Checker.isPointInSphere(vects[10], sphere));
    }
  
    @Test
    public void testIsArrayContainPoint() {
        ArrayList<Vect3d> points = new ArrayList<>(Arrays.asList(vects));
        assertTrue(Checker.isArrayContainPoint(points, new Vect3d(0, 1, 0)));
        assertTrue(Checker.isArrayContainPoint(points, new Vect3d(-1, 0, 0)));
        assertFalse(Checker.isArrayContainPoint(points, new Vect3d(0, -1, 0)));
        assertTrue(Checker.isArrayContainPoint(points, new Vect3d(0, 0, -1)));
        assertTrue(Checker.isArrayContainPoint(points, new Vect3d(-1, 1.2, 1.2 + Checker.eps() / 2)));
        assertTrue(Checker.isArrayContainPoint(points, new Vect3d(0, 1 - Checker.eps() / 2, 0)));
        assertFalse(Checker.isArrayContainPoint(points, new Vect3d(0, 0, 0.1)));
    }
}
