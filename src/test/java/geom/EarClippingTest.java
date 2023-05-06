package geom;

import maquettes.EarClipping;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

public class EarClippingTest {

  @Test
  public void testPolygon() throws Exception {
    Polygon3d pl = new Polygon3d(new Vect3d(0, 0, 0), new Vect3d(0, 1, 0), new Vect3d(1, 1, 0), new Vect3d(2, 0, 0));
    ArrayList<Vect3d> points = pl.points();
    EarClipping clipping = new EarClipping(points);
    ArrayList<Triang3d> trs1 = clipping.triangulate();
    ArrayList<Triang3d> trs2 = new ArrayList<>(
        Arrays.asList(
            new Triang3d(new Vect3d(1, 1, 0), new Vect3d(2, 0, 0), new Vect3d(0, 0, 0)),
            new Triang3d(new Vect3d(1, 1, 0), new Vect3d(0, 0, 0), new Vect3d(0, 1, 0))
        )
    );
    assertArrayEquals(trs1.toArray(), trs2.toArray());
  }

  @Test
  public void testNoConvexPolygons() throws Exception {
    ArrayList<Vect3d> points = new ArrayList<>(Arrays.asList(
        new Vect3d(0, 0, 0), new Vect3d(1, 3, 0), new Vect3d(2, 2, 0), new Vect3d(3, 3, 0), new Vect3d(3, 0, 0)));
    EarClipping clipping = new EarClipping(points);
    ArrayList<Triang3d> trs1 = clipping.triangulate();
    ArrayList<Triang3d> trs2 = new ArrayList<>(
        Arrays.asList(
            new Triang3d(new Vect3d(2, 2, 0), new Vect3d(3, 3, 0), new Vect3d(3, 0, 0)),
            new Triang3d(new Vect3d(0, 0, 0), new Vect3d(1, 3, 0), new Vect3d(2, 2, 0)),
            new Triang3d(new Vect3d(3, 0, 0), new Vect3d(0, 0, 0), new Vect3d(2, 2, 0))
        )
    );
    assertArrayEquals(trs1.toArray(), trs2.toArray());
  }

}
