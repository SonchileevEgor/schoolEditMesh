package geom;


import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Line3dTest {

  @BeforeEach
  public void setUp() throws Exception {

  }

  @AfterEach
  public void tearDown() throws Exception {

  }

  @Test
  public void testDistFromPoint() throws Exception {
    Vect3d p1 = new Vect3d(0, 0, 0);
    Vect3d p2 = new Vect3d(0, 0, 1);
    Vect3d p3 = new Vect3d(0, 1, 0);
    Vect3d p4 = new Vect3d(0, 1, 1);
    Vect3d p5 = new Vect3d(1, 1, 1);

    Line3d line = new Line3d(p1, Vect3d.sub(p2, p1));
    double dist = line.distFromPoint(p3);
    assertEquals(1, dist, Checker.eps());
    dist = line.distFromPoint(p4);
    assertEquals(1, dist, Checker.eps());
    dist = line.distFromPoint(p5);
    assertEquals(Math.sqrt(2), dist, Checker.eps());
  }
}