package gui.dialog;

import gui.EdtController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import updater.Updater;
import updater.WrongVersionNumberException;

/**
 *
 * @author alexeev
 */
public class AboutDialog extends JDialog {
  JButton _checkVersionButton;
  
  public AboutDialog(final EdtController ctrl) {
    super(ctrl.getFrame(), "О программе");
    _checkVersionButton = new JButton("Проверить наличие обновлений");
    
    _checkVersionButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean errStatus = false;
        String errMessage = "";
        try {
          Updater upd = new Updater(ctrl, "http://localhost:8080/sites/default/files/");
          if( upd.check() ){
            int reply = JOptionPane.showConfirmDialog(AboutDialog.this.getOwner(),
                    "<html><center>Новая версия доступна.<br>Установить обновление?</br>",
                    "Обновить программу",
                    JOptionPane.YES_NO_OPTION);
            if( reply == JOptionPane.YES_OPTION ){
              AboutDialog.this.dispose();
              upd.downloadLastVersion();
            }
          } else {
            JOptionPane.showMessageDialog(ctrl.getFrame(), "Обновление не требуется!");
          }
        } catch( MalformedURLException |
                 WrongVersionNumberException ex) {
          errStatus = true;
          errMessage = "<html><center>Никогда такого не было, и вот опять.</center>";
        } catch( IOException ex ) {
          errStatus = true;
          errMessage = "<html><center>Не удалось подключиться к серверу!<br>"
                  + "Проверьте, пожалуйста, наличие выхода в интернет.</center>";
        } catch( Exception ex ) {
          errStatus = true;
          errMessage = "<html><center>Что-то пошло не так, попробуйте подключиться позднее...</center>";
        }
        if( errStatus ){
          JOptionPane.showMessageDialog(ctrl.getFrame(), errMessage, "Ошибка обновления",
                  JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    
    setLayout(new MigLayout(new LC().insets("10")));
    
    add(new JLabel(config.Config.PROPERTIES.getProperty("companyName", "")),
            new CC().cell(0, 0));
    add(new JLabel(config.Config.PROPERTIES.getProperty("name", "Unknown") + " v." +
                   config.Config.PROPERTIES.getProperty("version", "Unknown")),
            new CC().cell(0, 1));
    add(_checkVersionButton, new CC().cell(0, 2).alignX("right").gapY("30", ""));
    
    setResizable(false);
    setModal(true);
    pack();
    setLocationRelativeTo(ctrl.getFrame());
  }
}