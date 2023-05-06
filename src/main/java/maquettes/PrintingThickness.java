package maquettes;

import static config.Config.MAQUETTE_MIN_PRINT_THICKNESS;

/**
 * Class, which determines parameters for cover's generation
 * Parameters store values of cover's thicknesses around elements of bodies
 * @author Leonid Ivanovsky
 */
public class PrintingThickness {
  private static double _edgeThickness = 2.0 * MAQUETTE_MIN_PRINT_THICKNESS.value();
  private static double _verticeThickness = 4.0 * MAQUETTE_MIN_PRINT_THICKNESS.value();
  private static double _faceThickness = MAQUETTE_MIN_PRINT_THICKNESS.value();

  private PrintingThickness() {}
  
  public static double[] getOptimalThicknessValues(double min_edge_len) {
    return new double[] {min_edge_len / 4, min_edge_len / 8, min_edge_len / 16};
  }

  public static void setVerticeThickness(double vert_th) {
    _verticeThickness = vert_th;
  }

  public static void setEdgeThickness(double edge_th) {
    _edgeThickness = edge_th;
  }

  public static void setFaceThickness(double face_th) {
    _faceThickness = face_th;
  }

  public static void setThicknesses(double vert_th, double edge_th, double face_th) {
    setVerticeThickness(vert_th);
    setEdgeThickness(edge_th);
    setFaceThickness(face_th);
  }

  public static double getEdgeThickness() {
    return _edgeThickness;
  }

  public static double getVerticeThickness() {
    return _verticeThickness;
  }

  public static double getFaceThickness() {
    return _faceThickness;
  }
}