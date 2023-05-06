package gui.dialog;

import gui.BasicEdtCanvasController;
import gui.EdtController;
import gui.elements.MaquetteInfoPanel;
import gui.elements.PreviewPrintCanvas;
import gui.elements.PrintingThicknessPanel;
import gui.elements.PrintingToolPanel;
import gui.inspector.InspectorPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import maquettes.Maquette;
import maquettes.PrintingThickness;
import util.Fatal;
import util.FileUtils;

/**
 * Input window for thicknesses of printing elements
 * with aux info panel about metering values of body's elements
 * @author Leonid Ivanovsky
 */
public class PrintingThicknessDialog extends EdtDialog {
  // имя временного файла, в который сохраняется модель для предварительного просмотра.
  static public final String TMP_FILENAME;

  static {
    TMP_FILENAME = "default.ply";
    try {
      if( !FileUtils.exists(TMP_FILENAME) ){
        File f = new File(TMP_FILENAME);
        f.createNewFile();
      }
    } catch( IOException ex ){
      JOptionPane.showMessageDialog(null, "Cannot create file " + TMP_FILENAME, "Fatal error", JOptionPane.ERROR);
      Fatal.error("Cannot create file " + TMP_FILENAME);
    }
  }

  // panel, which contains element's thicknesses for generating cover
  private final PrintingThicknessPanel _thicknessPanel;

  // panel, which contains metering values of body's elements in the drawing
  private final MaquetteInfoPanel _modelInfoPanel;

  // panel with a preview of model
  private final PreviewPrintCanvas _previewPanel;

  // Maquette of scene
  private Maquette _maquette;

  // is dialog accepted
  private boolean _accepted = false;

  public PrintingThicknessDialog(JFrame owner, Maquette maq,
                                        double maxEdgeLength,
                                        double minEdgeLength,
                                        double maxFaceArea,
                                        double minFaceArea, EdtController ctrl) throws IOException {
    super(owner, "Параметры для печати");
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    _maquette = maq;
    
    JTabbedPane jtp = new JTabbedPane();
    
    InspectorPanel ip = new InspectorPanel(ctrl);
    PrintingToolPanel ptp = new PrintingToolPanel(ctrl);
    _modelInfoPanel = new MaquetteInfoPanel(maxEdgeLength, minEdgeLength, maxFaceArea, minFaceArea);
    _thicknessPanel = new PrintingThicknessPanel(PrintingThickness.getVerticeThickness(),
        PrintingThickness.getEdgeThickness(), PrintingThickness.getFaceThickness());
    _thicknessPanel.updateToolTipText(PrintingThickness.getOptimalThicknessValues(minEdgeLength));

    jtp.addTab("Толщина элементов", _thicknessPanel);
    jtp.addTab("Сведения о модели", _modelInfoPanel);
    jtp.addTab("Инспектор моделей", ip);

    gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 1;
    add(jtp, gbc);
    
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    add(ptp, gbc);

    _maquette.getCarcasse().sculptModels();
    
    _previewPanel = new PreviewPrintCanvas(ctrl.getEditor(), _maquette.getCarcasse().getCovers());
    _previewPanel.setPreferredSize(new Dimension(600, 500));
    JPanel previewWrapper = new JPanel(new BorderLayout());
    previewWrapper.add(_previewPanel, BorderLayout.CENTER);

    gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 1; gbc.gridheight = 1;
    add(previewWrapper, gbc);

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Отмена");
    JButton previewButton = new JButton("Предпросмотр");

    okButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent ae) {
        try {
          _accepted = true;
          PrintingThickness.setThicknesses(
                  _thicknessPanel.getVerticeThickness(),
                  _thicknessPanel.getEdgeThickness(),
                  _thicknessPanel.getFaceThickness());
          dispose();
        } catch (ParseException pe) {
          util.Fatal.warning("Ошибка ввода параметров!");
        }
      }
    });

    cancelButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent ae) {
        dispose();
      }
    });

    previewButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        setEnabled(false);
        (new SwingWorker<Object, Object>(){
          @Override
          protected String doInBackground() {
              //!!TODO: reply about error
              
              try {
                  PrintingThickness.setThicknesses(
                          _thicknessPanel.getVerticeThickness(),
                          _thicknessPanel.getEdgeThickness(),
                          _thicknessPanel.getFaceThickness());
              } catch (ParseException ex) {
                  Logger.getLogger(PrintingThicknessDialog.class.getName()).log(Level.SEVERE, null, ex);
              }
              try {
                  _previewPanel.reload();
              } catch (IOException ex) {
                  Logger.getLogger(PrintingThicknessDialog.class.getName()).log(Level.SEVERE, null, ex);
              }
              return null;
          }
          @Override
          protected void done() {
            setEnabled(true);
          }
        }).execute();
      }
    });
    previewButton.setEnabled(true);

    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonsPanel.add(okButton);
    buttonsPanel.add(cancelButton);
    buttonsPanel.add(previewButton);

    gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.gridheight = 1;
    add(buttonsPanel, gbc);

    BasicEdtCanvasController _previewCanvasCtrl = new BasicEdtCanvasController(_previewPanel);
    _previewCanvasCtrl.getRotateAdapter().setMouseButtons(false, false);
    _previewCanvasCtrl.initMouseListeners();

    setModal(true);

    pack();
    setCenterLocation();
  }

  public boolean isAccepted(){
    return _accepted;
  }
}