package geom;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Ivan
 */
public class ConicPivotsTest {

  public ConicPivotsTest() {
  }
  private ConicPivots _conicPivots;

  /**
   * for ellipse
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void testGeneralEquationOfTheSecondOrderOnPlaneOXY1() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0),
            new Vect3d(0, 3, 0), new Vect3d(3, 2, 0), new Vect3d(-1, 2, 0));
    assertEquals(_conicPivots.coef()[0] / _conicPivots.coef()[4], -0.44444444444, Checker.eps());
    assertEquals(_conicPivots.coef()[1], 0, Checker.eps());
    assertEquals(_conicPivots.coef()[2] / _conicPivots.coef()[4], -0.66666666666, Checker.eps());
    assertEquals(_conicPivots.coef()[3] / _conicPivots.coef()[4], 0.44444444444, Checker.eps());
  }

  /**
   * for ellipse
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void newCenter1() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0),
            new Vect3d(0, 3, 0), new Vect3d(3, 2, 0), new Vect3d(-1, 2, 0));
    assertEquals(_conicPivots.newCenter().x(), 1, Checker.eps());
    assertEquals(_conicPivots.newCenter().y(), 1.5, Checker.eps());
    assertEquals(_conicPivots.newCenter().z(), 0, Checker.eps());
  }

  /**
   * for ellipse
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void referencePointInTheCanonicalCoordinateSystem1() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0),
            new Vect3d(0, 3, 0), new Vect3d(3, 2, 0), new Vect3d(-1, 2, 0));
    ArrayList<Vect3d> pivots = _conicPivots.referencePointInTheCanonicalCoordinateSystem();
    assertEquals(pivots.get(0).x(), -1.20761472884911947, Checker.eps());
    assertEquals(pivots.get(0).y(), 0, Checker.eps());
    assertEquals(pivots.get(0).z(), 0, Checker.eps());
    assertEquals(pivots.get(1).x(), 1.2076147288491192, Checker.eps());
    assertEquals(pivots.get(1).y(), 0, Checker.eps());
    assertEquals(pivots.get(1).z(), 0, Checker.eps());
    assertEquals(pivots.get(2).x(), 0, Checker.eps());
    assertEquals(pivots.get(2).y(), 1.707825127659933, Checker.eps());
    assertEquals(pivots.get(2).z(), 0, Checker.eps());
  }

  /**
   * for ellipse
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void pivots1() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(0, 0, 0), new Vect3d(2, 0, 0),
            new Vect3d(0, 3, 0), new Vect3d(3, 2, 0), new Vect3d(-1, 2, 0));
    ArrayList<Vect3d> pivots = _conicPivots.pivots();
    assertEquals(pivots.get(0).x(), -0.2076147288491199, Checker.eps());
    assertEquals(pivots.get(0).y(), 1.5, Checker.eps());
    assertEquals(pivots.get(0).z(), 0, Checker.eps());
    assertEquals(pivots.get(1).x(), 2.20761472884912, Checker.eps());
    assertEquals(pivots.get(1).y(), 1.5, Checker.eps());
    assertEquals(pivots.get(1).z(), 0, Checker.eps());
    assertEquals(pivots.get(2).x(), 1, Checker.eps());
    assertEquals(pivots.get(2).y(), 3.207825127659933, Checker.eps());
    assertEquals(pivots.get(2).z(), 0, Checker.eps());
  }

  /**
   * for parabola
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void testGeneralEquationOfTheSecondOrderOnPlaneOXY2() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(0, 0, 0), new Vect3d(4, 0, 0),
            new Vect3d(2, -4, 0), new Vect3d(3, -3, 0), new Vect3d(1, -3, 0));
    assertEquals(_conicPivots.coef()[0] / _conicPivots.coef()[4], 2, Checker.eps());
    assertEquals(_conicPivots.coef()[1], 0, Checker.eps());
    assertEquals(_conicPivots.coef()[2], 0, Checker.eps());
    assertEquals(_conicPivots.coef()[3] / _conicPivots.coef()[4], -4, Checker.eps());
  }

  /**
   * for parabola
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void newCenter2() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(0, 0, 0), new Vect3d(4, 0, 0),
            new Vect3d(2, -4, 0), new Vect3d(3, -3, 0), new Vect3d(1, -3, 0));
    assertEquals(_conicPivots.newCenter().x(), 2, Checker.eps());
    assertEquals(_conicPivots.newCenter().y(), 4, Checker.eps());
    assertEquals(_conicPivots.newCenter().z(), 0, Checker.eps());
  }

  /**
   * for parabola
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void referencePointInTheCanonicalCoordinateSystem2() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(0, 0, 0), new Vect3d(4, 0, 0),
            new Vect3d(2, -4, 0), new Vect3d(3, -3, 0), new Vect3d(1, -3, 0));
    ArrayList<Vect3d> pivots = _conicPivots.referencePointInTheCanonicalCoordinateSystem();
    assertEquals(pivots.get(0).x(), -0.25, Checker.eps());
    assertEquals(pivots.get(0).y(), 1, Checker.eps());
    assertEquals(pivots.get(0).z(), 0, Checker.eps());
    assertEquals(pivots.get(1).x(), -0.25, Checker.eps());
    assertEquals(pivots.get(1).y(), -2, Checker.eps());
    assertEquals(pivots.get(1).z(), 0, Checker.eps());
    assertEquals(pivots.get(2).x(), 0.25, Checker.eps());
    assertEquals(pivots.get(2).y(), 0, Checker.eps());
    assertEquals(pivots.get(2).z(), 0, Checker.eps());
  }

  /**
   * for parabola
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void pivots2() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(0, 0, 0), new Vect3d(4, 0, 0),
            new Vect3d(2, -4, 0), new Vect3d(3, -3, 0), new Vect3d(1, -3, 0));
    ArrayList<Vect3d> pivots = _conicPivots.pivots();
    assertEquals(pivots.get(0).x(), 3, Checker.eps());
    assertEquals(pivots.get(0).y(), -4.25, Checker.eps());
    assertEquals(pivots.get(0).z(), 0, Checker.eps());
    assertEquals(pivots.get(1).x(), 0, Checker.eps());
    assertEquals(pivots.get(1).y(), -4.25, Checker.eps());
    assertEquals(pivots.get(1).z(), 0, Checker.eps());
    assertEquals(pivots.get(2).x(), 2, Checker.eps());
    assertEquals(pivots.get(2).y(), -3.75, Checker.eps());
    assertEquals(pivots.get(2).z(), 0, Checker.eps());

  }

  /**
   * for hyperbole
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void pivots3() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(0, 0, 0), new Vect3d(4, 0, 0),
            new Vect3d(3, 2, 0), new Vect3d(1, 2, 0), new Vect3d(2, 1, 0));
    ArrayList<Vect3d> pivots = _conicPivots.pivots();
    assertEquals(pivots.get(0).x(), 2, Checker.eps());
    assertEquals(pivots.get(0).y(), 0.7387513919839089, Checker.eps());
    assertEquals(pivots.get(0).z(), 0, Checker.eps());
    assertEquals(pivots.get(1).x(), 2, Checker.eps());
    assertEquals(pivots.get(1).y(), 1.861248608016091, Checker.eps());
    assertEquals(pivots.get(1).z(), 0, Checker.eps());
    assertEquals(pivots.get(2).x(), 2.75, Checker.eps());
    assertEquals(pivots.get(2).y(), 1.861248608016091, Checker.eps());
    assertEquals(pivots.get(2).z(), 0, Checker.eps());
  }

  /**
   * for parabola
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void pivots4() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(0, 0, 0), new Vect3d(-6, 0, 0),
            new Vect3d(0, -2, 0), new Vect3d(-16, 10, 0), new Vect3d(-30, 10, 0));
    ArrayList<Vect3d> pivots = _conicPivots.pivots();
    assertEquals(pivots.get(0).x(), 1.6127864045000422, Checker.eps());
    assertEquals(pivots.get(0).y(), -3.0244271909999156, Checker.eps());
    assertEquals(pivots.get(0).z(), 0, Checker.eps());
    assertEquals(pivots.get(1).x(), 2.954427190999916, Checker.eps());
    assertEquals(pivots.get(1).y(), -0.3411456180001684, Checker.eps());
    assertEquals(pivots.get(1).z(), 0, Checker.eps());
    assertEquals(pivots.get(2).x(), 1.9, Checker.eps());
    assertEquals(pivots.get(2).y(), -2.05, Checker.eps());
    assertEquals(pivots.get(2).z(), 0, Checker.eps());
  }

  /**
   * for ellipse
   *
   * @throws ExDegeneration
   * @throws ExZeroDivision
   */
  @Test
  public void pivots5() throws ExDegeneration, ExZeroDivision {
    _conicPivots = new ConicPivots(new Vect3d(3, 1, 1), new Vect3d(2, 2, 2),
            new Vect3d(-1, 1, 1), new Vect3d(1, 0, 0), new Vect3d(0, 0, 0));
    ArrayList<Vect3d> pivots = _conicPivots.pivots();
    assertEquals(pivots.get(0).x(), 2.5404743412829136, Checker.eps());
    assertEquals(pivots.get(0).y(), 1.3462137077136713, Checker.eps());
    assertEquals(pivots.get(0).z(), 1.3462137077136713, Checker.eps());
    assertEquals(pivots.get(1).x(), -0.5404743412829145, Checker.eps());
    assertEquals(pivots.get(1).y(), 0.6537862922863287, Checker.eps());
    assertEquals(pivots.get(1).z(), 0.6537862922863287, Checker.eps());
    assertEquals(pivots.get(2).x(), 0.5831768491232872, Checker.eps());
    assertEquals(pivots.get(2).y(), 1.9273251671902492, Checker.eps());
    assertEquals(pivots.get(2).z(), 1.9273251671902492, Checker.eps());
  }
}
