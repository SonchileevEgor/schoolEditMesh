package gui.elements;

import gui.EdtController;
import gui.IconList;
import gui.action.EdtAction;
import gui.mode.ModeList;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author alexeev
 */
public class AngleStyleButton extends JButton {

  public AngleStyleButton(EdtController ctrl) {
    super(IconList.ANGLE_SINGLE.getMediumIcon());
    setFocusable(false);
    
    final AnglePopup menu = new AnglePopup(ctrl);

    addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        menu.show(AngleStyleButton.this, 0, AngleStyleButton.this.getHeight());
      }
    });
  }
  
  class AnglePopup extends JPopupMenu {
    AnglePopup(EdtController ctrl) {
      super();
      setLayout(new MigLayout(new LC().insetsAll("0").gridGap("0", "0").wrapAfter(2)));
      addMenuItem(ModeList.MODE_CREATE_ANGLE.getAction(ctrl, AngleStyle.SINGLE),
              IconList.ANGLE_SINGLE.getMediumIcon());
      addMenuItem(ModeList.MODE_CREATE_ANGLE.getAction(ctrl, AngleStyle.DOUBLE),
              IconList.ANGLE_DOUBLE.getMediumIcon());
      addMenuItem(ModeList.MODE_CREATE_ANGLE.getAction(ctrl, AngleStyle.TRIPLE),
              IconList.ANGLE_TRIPLE.getMediumIcon());
      addMenuItem(ModeList.MODE_CREATE_ANGLE.getAction(ctrl, AngleStyle.WAVED),
              IconList.ANGLE_WAVED.getMediumIcon());
    }
    
    private void addMenuItem(Action a, Icon ic){
      JMenuItem mi = new JMenuItem(a){
        @Override
        public Dimension getPreferredSize(){
          return new Dimension(getIcon().getIconWidth() + 4, getIcon().getIconHeight() + 4);
        }
      };
      mi.setText(null);
      mi.setToolTipText((String)a.getValue(Action.LONG_DESCRIPTION));
      mi.setIcon(ic);
      add(mi);
    }
    
  }
}
