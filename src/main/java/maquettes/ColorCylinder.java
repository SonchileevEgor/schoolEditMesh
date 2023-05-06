package maquettes;

import geom.Cylinder3d;
import opengl.colorgl.ColorGL;

/**
 *
 * @author Vladimir
 */
class ColorCylinder {
    private Cylinder3d _cylinder;
    private ColorGL _color;
    
    public ColorCylinder(Cylinder3d cylinder, ColorGL color) {
        _cylinder = cylinder;
        _color = color;
    }
    
    public Cylinder3d getCylinder() {
        return _cylinder;
    }
    
    public ColorGL getColor() {
      return _color;
    }
}
