package opengl.sceneparameters;

import geom.Checker;
import geom.Vect3d;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CameraPositionTest {

  @BeforeEach
  public void setUp() throws Exception {

  }

  @AfterEach
  public void tearDown() throws Exception {

  }

  @Test
  public void testMoveCenterPos() throws Exception {
    CameraPosition cam = new CameraPosition(new Vect3d(1, 1, 1), new Vect3d(1, 1, 0));
    cam.moveCenterPos(-1, -1);
    assertEquals(new Vect3d(0, 0, 0), cam.center());
    assertEquals(new Vect3d(0, 0, 1), cam.eye());

    cam = new CameraPosition(new Vect3d(1, 1, 1), new Vect3d(0, 1, 1));
    cam.moveCenterPos(-1, -1);
    assertEquals(new Vect3d(0, 0, 0), cam.center());
    assertEquals(new Vect3d(1, 0, 0), cam.eye());

  }

  @Test
  public void testTurnCamera() throws Exception {
    CameraPosition cam = new CameraPosition(new Vect3d(1, 1, 1), new Vect3d(1, 1, 0));
    cam.turnCamera(0, Math.PI / 2);
    assertEquals(new Vect3d(2, 1, 0), cam.eye());
    cam.turnCamera( -Math.PI / 2, 0);
    assertEquals(new Vect3d(1, 0, 0), cam.eye());
    cam.turnCamera( -Math.PI / 2, 0);
    assertEquals(new Vect3d(0, 1, 0), cam.eye());
  }

  @Test
  public void testTurnCamera1() throws Exception {
    CameraPosition cam = new CameraPosition();
    Vect3d pos = new Vect3d(1, 1, 1);
    cam.turnCamera(pos);
    assertEquals(pos, cam.eye());
    pos = new Vect3d(5, -0.12345678, 100500);
    cam.turnCamera(pos);
    assertEquals(pos, cam.eye());
  }

  @Test
  public void testSetCameraDistance() throws Exception {
    CameraPosition cam = new CameraPosition(new Vect3d(1, 1, 1));
    assertEquals(Math.sqrt(3), cam.distance(), Checker.eps());
    double testDistance = 0.123456789012;
    cam.setCameraDistance(testDistance);
    assertEquals(testDistance, cam.distance(), Checker.eps());
  }

  @Test
  public void testUp() throws Exception {
    CameraPosition cam = new CameraPosition();

    cam.setPosition(new Vect3d(1, 2, 3), new Vect3d(0, 0, 0));
    assertEquals(new Vect3d(0, 0, 1), cam.up());
    cam.turnCamera(0, Math.PI);
    assertEquals(new Vect3d(0, 0, -1), cam.up());
    cam.turnCamera(0, Math.PI);
    assertEquals(new Vect3d(0, 0, 1), cam.up());


    cam.setPosition(new Vect3d(1, 2, -3), new Vect3d(0, 0, 0));
    assertEquals(new Vect3d(0, 0, 1), cam.up());
    cam.turnCamera(0, Math.PI);
    assertEquals(new Vect3d(0, 0, -1), cam.up());
    cam.turnCamera(0, Math.PI);
    assertEquals(new Vect3d(0, 0, 1), cam.up());

    cam.setPosition(new Vect3d(-1, 2, 3), new Vect3d(0, 0, 0));
    assertEquals(new Vect3d(0, 0, 1), cam.up());
    cam.turnCamera(0, Math.PI);
    assertEquals(new Vect3d(0, 0, -1), cam.up());
    cam.turnCamera(0, Math.PI);
    assertEquals(new Vect3d(0, 0, 1), cam.up());


    cam.setPosition(new Vect3d(0, 0, 1), new Vect3d(0, 0, 0));
    assertEquals(new Vect3d(0, 1, 0), cam.up());
    cam.setPosition(new Vect3d(0, 0, Math.PI), new Vect3d(0, 0, 0));
    assertEquals(new Vect3d(0, 1, 0), cam.up());

    cam.setPosition(new Vect3d(0, 0, -1), new Vect3d(0, 0, 0));
    assertEquals(new Vect3d(0, 1, 0), cam.up());
    cam.setPosition(new Vect3d(0, 0, -Math.PI), new Vect3d(0, 0, 0));
    assertEquals(new Vect3d(0, 1, 0), cam.up());
  }

  @Test
  public void testGetUpPerpendicularVect() throws Exception {
    CameraPosition cam = new CameraPosition(new Vect3d(1, 0, 0));
    Vect3d upPer = cam.getUpPerpendicularVect();
    assertEquals(new Vect3d(0, 0, 1), upPer.getNormalized());

    cam.setEye(new Vect3d(1, 0, 1));
    upPer = cam.getUpPerpendicularVect();
    assertEquals(new Vect3d(-1, 0, 1).getNormalized(), upPer.getNormalized());

    // Depends from center point
    cam = new CameraPosition(new Vect3d(1, 0, 0), new Vect3d(0, 1, 0));
    upPer = cam.getUpPerpendicularVect();
    assertEquals(new Vect3d(0, 0, 1), upPer.getNormalized());

    cam = new CameraPosition(new Vect3d(1, 1, 1), new Vect3d(0, 1, 0));
    upPer = cam.getUpPerpendicularVect();
    assertEquals(new Vect3d(-1, 0, 1).getNormalized(), upPer.getNormalized());
  }

}