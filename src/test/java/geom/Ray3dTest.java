package geom;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/*
 *
 * @author otec666
 */
public class Ray3dTest {
    private Ray3d _ray;
    private Vect3d _point;

    @BeforeEach
    public void setUp() throws ExDegeneration {
        _ray= new Ray3d(new Vect3d(0,0,0), new Vect3d(1,0,0));

    }
   
    @Test
    public void testIntersect2d() throws ExDegeneration{
        //Точка лежит на луче, но не совпадает с вершиной
        _point = new Vect3d(2,0,0);
        assertTrue(_ray.containsPoint(_point), "Method Ray3d.isContainPoint is incorrect");
        _point = new Vect3d(1,0,0);
        assertTrue(_ray.containsPoint(_point), "Method Ray3d.isContainPoint is incorrect");
        //Точка совпадает с вершиной луча
        _point = new Vect3d(0,0,0);
        assertFalse(_ray.containsPoint(_point), "Method Ray3d.isContainPoint is incorrect");
        //Точка лежит на прямой содержащей луч, но не лежит на луче
        _point = new Vect3d(-1,0,0);
        assertFalse(_ray.containsPoint(_point), "Method Ray3d.isContainPoint is incorrect");
        // Точка не лежит на прямой содержащей луч и не лежит на луче
        _point = new Vect3d(2,1,0);
        assertFalse(_ray.containsPoint(_point), "Method Ray3d.isContainPoint is incorrect");
    }

}