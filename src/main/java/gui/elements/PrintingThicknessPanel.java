package gui.elements;

import gui.ui.EdtTabPanel;
import java.text.ParseException;
import java.util.Locale;

/**
 * Input panel for thicknesses of printing elements
 * @author Leonid Ivanovsky
 */
public class PrintingThicknessPanel extends EdtTabPanel {
  private final NumericTextField _verticeThicknessField = new NumericTextField(100, true);
  private final NumericTextField _edgeThicknessField = new NumericTextField(100, true);
  private final NumericTextField _faceThicknessField = new NumericTextField(100, true);
  
  public PrintingThicknessPanel() {
    super();
    addItem("Диаметр вершины:", _verticeThicknessField);
    addItem("Толщина ребра:", _edgeThicknessField);
    addItem("Толщина грани:", _faceThicknessField);
  }

  public PrintingThicknessPanel(double vert_th, double edge_th, double face_th) {
    this();
    _verticeThicknessField.setValue(vert_th);
    _edgeThicknessField.setValue(edge_th);
    _faceThicknessField.setValue(face_th);
  }

  public void updateToolTipText(double[] opt_values) {
    String str = "Рекомендуется: ";
    _verticeThicknessField.setToolTipText(str+String.format(Locale.ENGLISH, "%.3f", opt_values[0]));
    _edgeThicknessField.setToolTipText(str+String.format(Locale.ENGLISH, "%.3f", opt_values[1]));
    _faceThicknessField.setToolTipText(str+String.format(Locale.ENGLISH, "%.3f", opt_values[2]));
  }

  public double getVerticeThickness() throws ParseException {
    return _verticeThicknessField.getNumericValue();
  }

  public double getEdgeThickness() throws ParseException {
    return _edgeThicknessField.getNumericValue();
  }

  public double getFaceThickness() throws ParseException {
    return _faceThicknessField.getNumericValue();
  }
}
