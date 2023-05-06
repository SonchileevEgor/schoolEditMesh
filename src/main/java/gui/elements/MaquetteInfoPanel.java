package gui.elements;

import static config.Config.PRECISION;
import gui.ui.EdtTabPanel;
import javax.swing.JLabel;

/**
 * Info panel with metering values of body's elements in the drawing
 * @author Leonid Ivanovsky
 */
public class MaquetteInfoPanel extends EdtTabPanel {
  // label with max length of edge in the drawing
  private final JLabel _maxEdgeLength;

  // label with min length of edge in the drawing
  private final JLabel _minEdgeLength;

  // label with max area of face in the drawing
  private final JLabel _maxFaceArea;

  // label with min area of face in the drawing
  private final JLabel _minFaceArea;

  public MaquetteInfoPanel(double max_edge, double min_edge, double max_face, double min_face) {
    super();
    _maxEdgeLength = new JLabel(util.Util.valueOf(max_edge, PRECISION.value()));
    _minEdgeLength = new JLabel(util.Util.valueOf(min_edge, PRECISION.value()));
    _maxFaceArea = new JLabel(util.Util.valueOf(max_face, PRECISION.value()));
    _minFaceArea = new JLabel(util.Util.valueOf(min_face, PRECISION.value()));
    addItem("Наибольшая длина ребра:", _maxEdgeLength);
    addItem("Наименьшая длина ребра:", _minEdgeLength);
    addItem("Наибольшая площадь грани:", _maxFaceArea);
    addItem("Наименьшая площадь грани:", _minFaceArea);
  }
}
