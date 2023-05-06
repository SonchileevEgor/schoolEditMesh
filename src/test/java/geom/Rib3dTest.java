package geom;


import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


public class Rib3dTest {
    
    @Test
    public void testSort() throws ExDegeneration{
        List<Rib3d> test = new ArrayList<>();
        test.add(new Rib3d(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0)));
        test.add(new Rib3d(new Vect3d(4, 0, 0), new Vect3d(5, 0, 0)));
        test.add(new Rib3d(new Vect3d(2, 0, 0), new Vect3d(1, 0, 0)));
        test.add(new Rib3d(new Vect3d(4, 0, 0), new Vect3d(3, 0, 0)));
        test.add(new Rib3d(new Vect3d(2, 0, 0), new Vect3d(3, 0, 0)));
        
        List<Rib3d> sorted = new ArrayList<>();
        sorted.add(new Rib3d(new Vect3d(0, 0, 0), new Vect3d(1, 0, 0)));
        sorted.add(new Rib3d(new Vect3d(1, 0, 0), new Vect3d(2, 0, 0)));
        sorted.add(new Rib3d(new Vect3d(2, 0, 0), new Vect3d(3, 0, 0)));
        sorted.add(new Rib3d(new Vect3d(3, 0, 0), new Vect3d(4, 0, 0)));
        sorted.add(new Rib3d(new Vect3d(4, 0, 0), new Vect3d(5, 0, 0)));
        
        List<Rib3d> sotredTest = Rib3d.sort(test);
        assertEquals(sotredTest.size(), sorted.size());
        assertArrayEquals(sotredTest.toArray(), sorted.toArray());
    }
}