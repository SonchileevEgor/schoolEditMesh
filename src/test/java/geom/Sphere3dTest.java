package geom;

import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class Sphere3dTest {

    @Test
    public void testTangentPlaneInPoint1() throws ExGeom{
        Vect3d point = new Vect3d (0, 5, 0);
        Sphere3d sphere = new Sphere3d (new Vect3d(0, 0, 0), 5);
        Plane3d actual = sphere.tangentPlaneInPoint(point);
        Plane3d expected = new Plane3d (new Vect3d(0, 1, 0), point);
        assertEquals(Plane3d.equals(expected, actual), true);
    }

    @Test
    public void testTangentPlaneInPoint2() throws ExGeom{
        Vect3d point = new Vect3d(0, 3, 3);
        Sphere3d sphere = new Sphere3d (new Vect3d(1, 1, 1), 3);
        Plane3d actual = sphere.tangentPlaneInPoint(point);
        Plane3d expected = new Plane3d (Vect3d.sub(sphere.center(), point), point);
        assertEquals(Plane3d.equals(expected, actual), true);
    }

    //точка не лежит на сфере (внутри)
    @Test
    public void testTangentPlaneInPoint3() throws Exception{
        Assertions.assertThrows(ExGeom.class, () -> {
            Vect3d point = new Vect3d(0, 3, 0);
            Sphere3d sphere = new Sphere3d (new Vect3d(0, 0, 0), 5);
            sphere.tangentPlaneInPoint(point);
        });
    }

    //точка не лежит на сфере (снаружи)
    @Test
    public void testTangentPlaneInPoint4() throws Exception {
        Assertions.assertThrows(ExGeom.class, () -> {
            Vect3d point = new Vect3d (10, -3, 7);
            Sphere3d sphere = new Sphere3d (new Vect3d(0, 0, 0), 5);
            sphere.tangentPlaneInPoint(point);
        });
    }

    //точка не лежит на сфере (центр)
    @Test
    public void testTangentPlaneInPoint5() {
        Assertions.assertThrows(ExGeom.class, () -> {
            Vect3d point = new Vect3d (1, -4, 3);
            Sphere3d sphere = new Sphere3d (new Vect3d(1, -4, 3), 5);
            sphere.tangentPlaneInPoint(point);
        });
    }

    @Test
    public void testIntersectLine() throws ExDegeneration {
        Sphere3d sphere = new Sphere3d(Vect3d.O, 1);
        Line3d l1 = new Line3d(Vect3d.O, Vect3d.UX);
        ArrayList<Vect3d> result = new ArrayList<>();
        result.add(new Vect3d(1, 0, 0));
        result.add(new Vect3d(-1, 0, 0));
        assertEquals(result, sphere.intersect(l1));

        result.clear();
        Line3d l2 = new Line3d(Vect3d.O, new Vect3d(1, 1, 1));
        double r = 1.0 / Math.sqrt(3);
        result.add(new Vect3d(r, r, r));
        result.add(new Vect3d(-r, -r, -r));
        assertEquals(result, sphere.intersect(l2));

        result.clear();
        Line3d l3 = Line3d.line3dByTwoPoints(new Vect3d(2, 3, 3), new Vect3d(-3, -2.5, -2));
        assertTrue(Checker.isEqual(1.0, sphere.intersect(l3).get(0).norm()));
        assertTrue(Checker.isEqual(1.0, sphere.intersect(l3).get(1).norm()));
    }
}
