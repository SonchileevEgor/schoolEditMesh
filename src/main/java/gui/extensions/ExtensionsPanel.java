package gui.extensions;

import gui.EdtController;
import gui.ui.EdtTabPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import javax.swing.JCheckBox;

/**
 *
 * @author alexeev
 */
public class ExtensionsPanel extends EdtTabPanel {
  private EdtController _ctrl;
  private EnumMap<Extensions, JCheckBox> _checkboxes;
  
  public ExtensionsPanel(EdtController ctrl) {
    super();
    _ctrl = ctrl;
    _checkboxes = new EnumMap<>(Extensions.class);
    
    for( final Extensions ext : Extensions.values() ){
      JCheckBox ch = new JCheckBox();
      _checkboxes.put(ext, ch);
      ch.setSelected(ctrl.getExtensionMgr().getStatus(ext));
      ch.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          _ctrl.getExtensionMgr().changeStatus(ext, ((JCheckBox)(e.getSource())).isSelected());
          _ctrl.notifyAppSettingsChange();
        }
      });
      addItem(ext.getName(), ext.getDescription(), ch, 20, 20);
    }
  }
  
  public final void update() {
    for( Extensions ext : Extensions.values() ){
      _checkboxes.get(ext).setSelected(_ctrl.getExtensionMgr().getStatus(ext));
    }
  }
}
