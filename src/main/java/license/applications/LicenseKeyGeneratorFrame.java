package license.applications;

import static config.Config.LICENSE_SERVER_ADDRESS;
import gui.elements.IntegerTextField;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import license.encryption.WrongEncryptedKeyException;
import license.network.ClientAdmin;
import license.network.ConnectionFailedException;
import license.network.WrongAdminPasswordException;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author alexeev
 */
public class LicenseKeyGeneratorFrame extends JFrame {
    LocalhostConfigMaker localhostConfigMaker = new LocalhostConfigMaker();
    
    private int port = localhostConfigMaker.getPort();
    private String adminPassword = "It_is_the_very_security_password";
    private String encryptionKey = localhostConfigMaker.getEncryptionKey();
    
    private Date startDate;
    private Date finishDate;
    ClientAdmin clientAdmin;
    InetAddress inetAddress;
    
    IntegerTextField numKeysField;
    JFormattedTextField fromField;
    JFormattedTextField tillField;
    
    JTextArea resultArea;
    
  public LicenseKeyGeneratorFrame()
        throws UnknownHostException {
    setLayout(new MigLayout(new LC()));
    createClientAdmin();

    DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

    JLabel fromLabel = new JLabel("FROM: ");
    fromField = new JFormattedTextField(format);
    fromField.setColumns(10);
    fromField.setValue(startDate);

    JLabel tillLabel = new JLabel("TILL: ");
    tillField = new JFormattedTextField(format);
    tillField.setColumns(10);
    tillField.setValue(finishDate);
    
    JLabel numKeysLabel = new JLabel("KEYS NUMBER: ");
    numKeysField = new IntegerTextField(1, true);
    numKeysField.setColumns(6);
            
    resultArea = new JTextArea();
    resultArea.setRows(11);
    JScrollPane areaScrollPane = new JScrollPane(resultArea);

    JButton generateButton = new JButton("Generate!");
    generateButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          int keysNumber = numKeysField.getNumericValue();
          boolean reallyGenerate = false;
          if( keysNumber > 100 ){
            JOptionPane.showMessageDialog(LicenseKeyGeneratorFrame.this,
                    "Too many keys to generate. Enter keys number 100 or less.",
                    "Too many keys error!", JOptionPane.ERROR_MESSAGE);
          } else if( keysNumber > 5 ){
            if(JOptionPane.showConfirmDialog(LicenseKeyGeneratorFrame.this,
                    String.format("Are you really want to generate %d keys?", keysNumber),
                    "Confirm action",
                    JOptionPane.YES_NO_OPTION) ==
                    JOptionPane.OK_OPTION){
              reallyGenerate = true;
            }
          } else {
            reallyGenerate = true;
          }
          if( reallyGenerate ){
            StringBuilder textValue = new StringBuilder();
            try {
              for( int i = 0; i < keysNumber; i++ ){
                String newKey = clientAdmin.getNewLicenseKey(startDate, finishDate);
                textValue.append(MessageFormat.format("Key: \"{0}\" startDate: \"{1}\" finishDate: \"{2}\"\n",
                                  newKey, startDate, finishDate));
              }
            } catch( ConnectionFailedException |
                    WrongAdminPasswordException |
                    WrongEncryptedKeyException ex){
              JOptionPane.showMessageDialog(LicenseKeyGeneratorFrame.this,
                      "Error occured during key generation",
                      "Key generation error!", JOptionPane.ERROR_MESSAGE);
            }
            resultArea.setText(textValue.toString());
          }
        } catch(ParseException ex){
          JOptionPane.showMessageDialog(LicenseKeyGeneratorFrame.this, "Invalid value in keys number field", "Parse error!",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    
    JButton copyButton = new JButton("Copy to clipboard");
    copyButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        StringSelection stringSelection = new StringSelection(resultArea.getText());
        Clipboard brd = Toolkit.getDefaultToolkit().getSystemClipboard();
        brd.setContents(stringSelection, null);
      }
    });
    
    add(fromLabel, new CC().cell(0, 0));
    add(fromField, new CC().cell(1, 0));
    add(tillLabel, new CC().cell(2, 0));
    add(tillField, new CC().cell(3, 0));
    add(numKeysLabel, new CC().cell(0, 1));
    add(numKeysField, new CC().cell(1, 1).spanX(3));
    add(generateButton, new CC().cell(0, 2).spanX(4).alignX("c"));
    add(areaScrollPane, new CC().cell(0, 3).spanX(4).growX().pushX());
    add(copyButton, new CC().cell(3, 4));

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setResizable(false);
    pack();
  }
  
  private void createClientAdmin() throws UnknownHostException {
    startDate = new Date();
    finishDate = new Date();
    finishDate.setTime(finishDate.getTime() + 31536000000L); // Plus one year.
    inetAddress = InetAddress.getByName(LICENSE_SERVER_ADDRESS.value());
    clientAdmin = new ClientAdmin(new CustomNetworkConfig(port, inetAddress, encryptionKey), adminPassword);
  }
  
  public static void main(String[] args){
    try{
      LicenseKeyGeneratorFrame frame = new LicenseKeyGeneratorFrame();
      frame.setVisible(true);
    } catch( UnknownHostException ex ){
      JOptionPane.showMessageDialog(null, "Cannot connect to host", "Connection error!",
              JOptionPane.ERROR_MESSAGE);
    }
  }
  
}
