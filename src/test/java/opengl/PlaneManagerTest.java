package opengl;

import geom.Matrix3;
import geom.Vect3d;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by MAXbrainRUS on 28-Dec-15.
 */
public class PlaneManagerTest {
    Vect3d O = new Vect3d(0, 0, 0);
    Vect3d A = new Vect3d(1, 0, 0);
    Vect3d B = new Vect3d(0, 1, 0);
    Vect3d C = new Vect3d(1, 1, 0);
    Vect3d M = new Vect3d(-1, 0, 0);
    ArrayList<Vect3d> polygonOAB;
    ArrayList<Vect3d> polygonOAC;
    ArrayList<Vect3d> polygonABC;
    ArrayList<Vect3d> polygonABD;

    ArrayList<Vect3d> polygonAOB;


    @BeforeEach
    public void before(){
        Vect3d D = new Vect3d(0, 0, 1);
        polygonOAB = new ArrayList<>();
        polygonOAB.add(O);
        polygonOAB.add(A);
        polygonOAB.add(B);

        polygonOAC = new ArrayList<>();
        polygonOAC.add(O);
        polygonOAC.add(A);
        polygonOAC.add(C);

        polygonABC = new ArrayList<>();
        polygonABC.add(A);
        polygonABC.add(B);
        polygonABC.add(C);

        polygonABD = new ArrayList<>();
        polygonABD.add(A);
        polygonABD.add(B);
        polygonABD.add(D);

        polygonAOB = new ArrayList<>();
        polygonAOB.add(A);
        polygonAOB.add(O);
        polygonAOB.add(B);
    }

    @Test
    public void testGetOverlappedPolygons() throws Exception {
        PlaneManager planeManager = new PlaneManager();

        planeManager.addPolygon(polygonOAB, null);
        planeManager.addPolygon(polygonOAC, null);
        planeManager.addPolygon(polygonABC, null);
        planeManager.addPolygon(polygonABD, null);

        List<List<DrawingPolygon>> l = planeManager.getOverlappedPolygons();

        assertEquals(l.size(), 1);
        List<DrawingPolygon> l0 = l.get(0);
        assertEquals(l0.size(), 3);
        assertEquals(l0.get(0).points, polygonOAB);
        assertEquals(l0.get(1).points, polygonOAC);
        assertEquals(l0.get(2).points, polygonABC);
    }
    @Test
    public void testIsOverlappedPolygons() throws Exception {
        PlaneManager planeManager = new PlaneManager();
        assertTrue(planeManager.isEnabled());

        planeManager.addPolygon(polygonOAB, null);
        planeManager.addPolygon(polygonOAC, null);
        planeManager.addPolygon(polygonABC, null);
        planeManager.addPolygon(polygonABD, null);

        // Works only in "read only" mode
        assertFalse(planeManager.isReadOnly());

        assertFalse(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonOAC, null));
        assertFalse(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonABD, null));

        planeManager.setReadOnly(true);

        assertTrue(planeManager.isReadOnly());

        assertTrue(planeManager.isOverlapped(polygonABC, null));
        assertTrue(planeManager.isOverlapped(polygonOAC, null));
        assertTrue(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonABD, null));

        planeManager.setEnabled(false);
        assertFalse(planeManager.isEnabled());
        assertFalse(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonOAC, null));
        assertFalse(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonABD, null));

        planeManager.reset();

        assertFalse(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonOAC, null));
        assertFalse(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonABD, null));
    }

    @Test
    public void testWhenDisabled() throws Exception {
        PlaneManager planeManager = new PlaneManager();
        planeManager.setEnabled(false);

        planeManager.addPolygon(polygonOAB, null);
        planeManager.addPolygon(polygonOAC, null);
        planeManager.addPolygon(polygonABC, null);
        planeManager.addPolygon(polygonABD, null);

        assertFalse(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonOAC, null));
        assertFalse(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonABD, null));
    }

    @Test
    public void testWhenReadOnly() throws Exception {
        PlaneManager planeManager = new PlaneManager();
        planeManager.setReadOnly(false);

        planeManager.addPolygon(polygonOAB, null);
        planeManager.addPolygon(polygonOAC, null);
        planeManager.addPolygon(polygonABC, null);
        planeManager.addPolygon(polygonABD, null);

        assertFalse(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonOAC, null));
        assertFalse(planeManager.isOverlapped(polygonABC, null));
        assertFalse(planeManager.isOverlapped(polygonABD, null));
    }

    @Test
    public void testOppositeNormals() throws Exception{
        PlaneManager planeManager = new PlaneManager();
        Matrix3 matrix3 = Matrix3.getRotateMatrix(36, 0, 1, 0);
        for (int i = 0; i < 3; i++) {
            polygonOAB.set(i, matrix3.mulOnVect(polygonOAB.get(i)));
            polygonAOB.set(i, matrix3.mulOnVect(polygonAOB.get(i)));
        }

        planeManager.addPolygon(polygonOAB, null);
        planeManager.addPolygon(polygonAOB, null);
        planeManager.setReadOnly(true);

        assertTrue(planeManager.isOverlapped(polygonOAB, null));
        assertTrue(planeManager.isOverlapped(polygonAOB, null));
    }

}