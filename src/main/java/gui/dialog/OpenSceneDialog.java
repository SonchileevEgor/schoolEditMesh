package gui.dialog;

import gui.IconList;
import gui.laf.AppColor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * Scene opening dialog.
 * With choose dimension option (2D or 3D).
 *
 * @author alexeev
 */
public class OpenSceneDialog extends JDialog {
  /**
   * Chosen option (2D or 3D).
   */
  private boolean _is3d = false;

  /**
   * True if one option is chosen.
   */
  private boolean _isAccepted = false;

  /**
   * Scene opening dialog.
   * With choose dimension option (2D or 3D).
   */
  public OpenSceneDialog() {
    super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
    setTitle("Создать сцену");
    init();
    pack();
    util.Util.setCenterLocation(null, this);
  }

  public OpenSceneDialog(JFrame owner) {
    super(owner, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
    setTitle("Открыть сцену");
    init();
    pack();
    util.Util.setCenterLocation(owner, this);
  }

  private void init(){
    setLayout(new MigLayout());
    setUndecorated(true);
    setResizable(false);
    setModal(true);

    JPanel wrapper = new JPanel(new MigLayout());
    wrapper.setBackground(AppColor.WHITE.color());
    wrapper.setBorder(BorderFactory.createLineBorder(AppColor.DARK_GRAY.color()));

    JButton open2dButton = new JButton(IconList.OPEN_2D.getImage());
    open2dButton.setMargin(new Insets(0, 0, 0, 0));
    open2dButton.setFocusable(false);

    JButton open3dButton = new JButton(IconList.OPEN_3D.getImage());
    open3dButton.setMargin(new Insets(0, 0, 0, 0));
    open3dButton.setFocusable(false);

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    open2dButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        _is3d = false;
        _isAccepted = true;
        dispose();
      }
    });
    open3dButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        _is3d = true;
        _isAccepted = true;
        dispose();
      }
    });

    wrapper.add(open2dButton, new CC().cell(0, 0));
    wrapper.add(open3dButton, new CC().cell(0, 1));

    add(wrapper, new CC().grow().push());
  }

  public boolean isAccepted(){
    return _isAccepted;
  }

  public boolean is3d(){
    return _is3d;
  }
}
