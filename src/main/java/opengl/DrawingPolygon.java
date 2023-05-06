package opengl;

import geom.Vect3d;
import opengl.colorgl.ColorGL;

import java.util.ArrayList;

public class DrawingPolygon {
    public ArrayList<Vect3d> points;
    public ColorGL color;
    public TransformMatrix transformMatrix;

    @Override
    public String toString() {
        return points.toString() + " " + color.toString();
    }
}
