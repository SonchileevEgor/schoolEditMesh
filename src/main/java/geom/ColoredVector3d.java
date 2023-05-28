/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package geom;

import opengl.colorgl.ColorGL;

/**
 *
 * @author e-son
 */
public class ColoredVector3d {
public ColorGL color = ColorGL.RED;
    public double x, y;
    public Vect3d start, end;

    public ColoredVector3d(Vect3d start, Vect3d end) {
        this.start = start;
        this.end = end;
        this.x = end.x() - start.x();
        this.y = end.y() - start.y();
    }
    
    public void setColor(ColorGL clr) {
        this.color = clr;
    }
    
    public ColorGL getColor() {
        return this.color;
    }

    public boolean sameDirection(ColoredVector3d v) {
        return Math.abs(x * v.y - y * v.x) < 1e-9;
    }    
}
