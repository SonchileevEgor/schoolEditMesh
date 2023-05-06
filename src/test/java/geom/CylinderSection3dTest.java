package geom;

import org.junit.jupiter.api.Test;

public class CylinderSection3dTest {

  @Test
  public void testGetPointOfEllipse() throws Exception {
    Vect3d D = new Vect3d(0.37649564578808103,-0.43750249721175827,2.4776279993835852);
    Vect3d E = new Vect3d(0.05396782117676224,0.44983025922282494,1.0687914586337843);
    Cylinder3d cylinder3d = new Cylinder3d(D, E,0.25000000000000006);
    Plane3d plane3d = new Plane3d(new Vect3d(-0.037351885769043615,-0.3707866793329244,4.9860928666701305), E);
    CylinderSection3d cylinderSection3d = new CylinderSection3d(cylinder3d, plane3d);
    for (double p = 0; p < 1; p += 0.01) {
      Vect3d v = cylinderSection3d.getPointOfEllipse(p);
      if (Double.isNaN(v.x()))
        throw new NumberFormatException();
    }
  }
}