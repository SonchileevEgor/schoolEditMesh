package gui.dialog;

import gui.IconList;
import gui.elements.LicenseKeyTextField;
import gui.ui.EdtInputDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import license.applications.CustomNetworkConfig;
import license.applications.NetworkConfig;
import license.encryption.WrongEncryptedKeyException;
import license.license.LicenseInfo;
import license.license.LicenseValidationException;
import license.network.ClientUser;
import license.network.ConnectionFailedException;
import license.network.WrongLicenseKeyException;
import license.pcidentifier.NoMACAddressException;
import license.pcidentifier.PcIdentifier;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * License key enter dialog.
 * @author alexeev.
 */
public class LicenseKeyInputDialog extends EdtInputDialog {
  private ClientUser _clientUser;
  private LicenseKeyTextField _keyField;
  private LicenseInfo _serverInfo;

  public LicenseKeyInputDialog() throws UnknownHostException {
    super(null, "Ввод ключевой информации");

    NetworkConfig networkConfig = new CustomNetworkConfig(
            14080,
            InetAddress.getByName(config.Config.LICENSE_SERVER_ADDRESS.value()),
            "This is very security encryption key");

    _clientUser = new ClientUser(networkConfig);
    _keyField = new LicenseKeyTextField();
    _keyField.setHorizontalAlignment(JTextField.LEFT);
    _keyField.setLayout(new MigLayout(new LC().fillX()));

    JPanel wrapper = new JPanel(new MigLayout());
    wrapper.add(new JLabel(IconList.KEY.getImage()), new CC().cell(0, 0, 1, 2));
    wrapper.add(new JLabel("Введите лицензионный ключ:"), new CC().cell(1, 0, 1, 1).width("300!"));
    wrapper.add(_keyField, new CC().cell(1, 1, 1, 1).growX());

    addItem(wrapper, "center");
    addButtons();
    addFeedback();

    pack();
    setCenterLocation();
  }

  public LicenseInfo getLicenseInfo() {
    return _serverInfo;
  }

  @Override
  public AbstractAction OKAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          _serverInfo = _clientUser.getLicenseFromServer(_keyField.getText());
          _serverInfo.validate(PcIdentifier.getMACAddresses(), new Date());
          _serverInfo.saveToFile(config.Config.LICENSE_INFO_FILE);
          accepted = true;
          dispose();
        } catch( NoMACAddressException |
                 ConnectionFailedException |
                 WrongEncryptedKeyException |
                 LicenseValidationException |
                 WrongLicenseKeyException ex ){
          _feedback.error(ex.getMessage());
        } catch( FileNotFoundException ex ){
          _feedback.error("Не удалось сохранить ключевую информацию!");
        }
      }
    };
  }
}
