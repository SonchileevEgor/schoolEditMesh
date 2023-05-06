package gui.elements;

import gui.EdtController;
import gui.toolbox.PrintingToolBar;
import gui.ui.EdtPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * @author Vladimir
 */
public class PrintingToolPanel extends EdtPanel {
    private final PrintingToolBar _printingBar;

    public PrintingToolPanel(EdtController ctrl) {
        super(new BorderLayout());
        _printingBar = new PrintingToolBar(ctrl);
        
        JPanel toolBar = new JPanel();
        toolBar.add(_printingBar);
        setPreferredSize(new Dimension(0, 50));
        add(toolBar, BorderLayout.WEST);
    }
    
    
}
