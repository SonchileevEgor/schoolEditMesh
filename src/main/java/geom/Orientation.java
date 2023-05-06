package geom;

/**
 * @author  Leon Ivanovsky
 */

/**
 * enum, which contains directs of bypasses.
 * It needs for the generating cover.
 * Orientation determines triangle's sides of generating cover. In right orientation triangles will have right colors.
 */

public enum Orientation {

    LEFT, RIGHT;

    public Orientation change() {
        if (this == LEFT)
            return RIGHT;
        if (this == RIGHT)
            return LEFT;
        return RIGHT;
    }

    /**
     * Get orientation of triple points
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static Orientation getOrientationTripleOfVectors(Vect3d a, Vect3d b, Vect3d c) {
        double det3 = Det23.calc(a.x(), a.y(), a.z(), b.x(), b.y(), b.z(), c.x(), c.y(), c.z());
        if (det3 > 0.0)
            return Orientation.RIGHT;
        if (det3 < 0.0)
            return Orientation.LEFT;
        return Orientation.RIGHT;
    }

    /**
     * Get orientation of body by 4 its points
     * The basis of determing of orientation is the orientation of triple of vectors from vector's multiplicating
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return
     */
    public static Orientation getBodyOrientation(Vect3d p1, Vect3d p2, Vect3d p3, Vect3d p4) {
        Vect3d vect_a = new Vect3d(p2.x()-p1.x(), p2.y()-p1.y(), p2.z()-p1.z());
        Vect3d vect_b = new Vect3d(p3.x()-p1.x(), p3.y()-p1.y(), p3.z()-p1.z());
        Vect3d vect_axb = new Vect3d(p4.x()-p1.x(), p4.y()-p1.y(), p4.z()-p1.z());
        return Orientation.getOrientationTripleOfVectors(vect_a, vect_b, vect_axb);
    }

}
