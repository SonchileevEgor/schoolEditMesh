package maquettes;

import bodies.BodyType;

/**
 * @author Leonid Ivanovsky
 */

/**
 * enum needs for right generation of body's face cover
 * it contains dimension's type for bodies
 */

public enum BodyDimension {

    POINT, LINEAR, PLANE, VOLUMETRIC; // dimensions of bodies

    /**
     * Get dimension of body
     * @param figure_type type of body
     * @return dimension's type of body
     */
    public static BodyDimension getBodyDimension(BodyType figure_type) {
        if (figure_type == BodyType.POINT)
            return POINT; // 0D
        if (figure_type == BodyType.LINE || figure_type == BodyType.RIB || figure_type == BodyType.RAY)
            return LINEAR; // 1D
        if (figure_type == BodyType.TRIANGLE || figure_type == BodyType.POLYGON || figure_type == BodyType.CIRCLE || figure_type == BodyType.PLANE || figure_type == BodyType.CONE_SECTION || figure_type == BodyType.CYLINDER_SECTION)
            return PLANE; // 2D
        return VOLUMETRIC; // 3D
    }

}
