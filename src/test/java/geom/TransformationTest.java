package geom;

import static geom.SpaceTransformation.rotationOfPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 * @author Elena
 */
public class TransformationTest {
    @Test
    public void test1() throws ExDegeneration {
        Vect3d result = rotationOfPoint(new Vect3d(1, 0, 0),
                new Vect3d(0, 0, 0), new Vect3d(0, 0, 1), Math.PI);
        Vect3d answer = new Vect3d(-1, 0, 0);
        Assertions.assertEquals(Vect3d.equals(result, answer), true);
    }
}
