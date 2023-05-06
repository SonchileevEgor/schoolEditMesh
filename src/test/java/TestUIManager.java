
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author alexeev
 */
public class TestUIManager {
  public static void main(String[] args) {
    checkOptionPaneDefaults();
  }

  private static void checkOptionPaneDefaults() {
    try {
      // set system-depending Look and Feel
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (UnsupportedLookAndFeelException |
      ClassNotFoundException |
      InstantiationException |
      IllegalAccessException e) { }
    String[] keys = new String[] {
      "OptionPane.background",
      "OptionPane.border",
      "OptionPane.buttonAreaBorder",
      "OptionPane.buttonClickThreshold",
      "OptionPane.buttonFont",
      "OptionPane.buttonMinimumWidth",
      "OptionPane.buttonOrientation",
      "OptionPane.buttonPadding",
      "OptionPane.cancelButtonMnemonic",
      "OptionPane.cancelButtonText",
      "OptionPane.errorDialog.border.background",
      "OptionPane.errorDialog.titlePane.background",
      "OptionPane.errorDialog.titlePane.foreground",
      "OptionPane.errorDialog.titlePane.shadow",
      "OptionPane.errorIcon",
      "OptioinPane.errorSound",
      "OptionPane.font",
      "OptionPane.foreground",
      "OptionPane.informationIcon",
      "OptionPane.informationSound",
      "OptionPane.inputDialogTitle",
      "OptionPane.isYesLast",
      "OptionPane.messageAreaBorder",
      "OptionPane.messageDialogTitle",
      "OptionPane.messageFont",
      "OptionPane.messageForeground",
      "OptionPane.minimumSize",
      "OptionPane.noButtonMnemonic",
      "OptionPane.noButtonText",
      "OptionPane.okButtonMnemonic",
      "OptionPane.okButtonMnemonic",
      "OptionPane.okButtonText",
      "OptionPane.questionDialog.border.background",
      "OptionPane.questionDialog.titlePane.background",
      "OptionPane.questionDialog.titlePane.foreground",
      "OptionPane.questionDialog.titlePane.shadow",
      "OptionPane.questionIcon",
      "OptionPane.questionSound",
      "OptionPane.sameSizeButtons",
      "OptionPane.setButtonMargin",
      "OptionPane.titleText",
      "OptionPane.warningDialog.border.background",
      "OptionPane.warningDialog.titlePane.background",
      "OptionPane.warningDialog.titlePane.foreground",
      "OptionPane.warningDialog.titlePane.shadow",
      "OptionPane.warningIcon",
      "OptionPane.warningSound",
      "OptionPane.windowBindings",
      "OptionPane.yesButtonMnemonic",
      "OptionPane.yesButtonText"
    };
    for( String key : keys ){
      System.out.println(key + ": " + UIManager.getLookAndFeelDefaults().getString(key));
    }
  }

  private static void checkColorChooserDefaults() {
    try {
      // set system-depending Look and Feel
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (UnsupportedLookAndFeelException |
      ClassNotFoundException |
      InstantiationException |
      IllegalAccessException e) { }
    String[] keys = new String[] {
      "ColorChooser.background",
      "ColorChooser.cancelText",
      "ColorChooser.font",
      "ColorChooser.foreground",
      "ColorChooser.hsbBlueText",
      "ColorChooser.hsbBrightnessText",
      "ColorChooser.hsbDisplayMnemonicIndex",
      "ColorChooser.hsbGreenText",
      "ColorChooser.hsbHueText",
      "ColorChooser.hsbNameText",
      "ColorChooser.hsbRedText",
      "ColorChooser.hsbSaturationText",
      "ColorChooser.okText",
      "ColorChooser.panels",
      "ColorChooser.previewText",
      "ColorChooser.resetText",
      "ColorChooser.rgbBlueText",
      "ColorChooser.rgbDisplayedMnemonicIndex",
      "ColorChooser.rgbGreenText",
      "ColorChooser.rgbNameText",
      "ColorChooser.rgbRedText",
      "ColorChooser.sampleText",
      "ColorChooser.showPreviewPanelText",
      "ColorChooser.swatchesDefaultRecentColor",
      "ColorChooser.swatchesDisplayedMnemonicIndex",
      "ColorChooser.swatchesNameText",
      "ColorChooser.swatchesRecentSwatchSize",
      "ColorChooser.swatchesRecentText",
      "ColorChooser.swatchesSwatchSize"
    };
    for( String key : keys ){
      System.out.println(key + ": " + UIManager.getLookAndFeelDefaults().getString(key));
    }
  }

  private static void checkFileChooserDefaults() {
    try {
      // set system-depending Look and Feel
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (UnsupportedLookAndFeelException |
      ClassNotFoundException |
      InstantiationException |
      IllegalAccessException e) { }
    String[] keys = new String[] {
      "FileChooser.acceptAllFileFilterText",
      "FileChooser.ancestorInputMap",
      "FileChooser.byDateText",
      "FileChooser.byNameText",
      "FileChooser.cancelButtonMnemonic",
      "FileChooser.cancelButtonText",
      "FileChooser.chooseButtonText",
      "FileChooser.createButtonText",
      "FileChooser.desktopName",
      "FileChooser.detailsViewIcon",
      "FileChooser.directoryDescriptionText",
      "FileChooser.directoryOpenButtonMnemonic",
      "FileChooser.directoryOpenButtonText",
      "FileChooser.fileDescriptionText",
      "FileChooser.fileNameLabelMnemonic",
      "FileChooser.fileNameLabelText",
      "FileChooser.filesOfTypeLabelMnemonic",
      "FileChooser.filesOfTypeLabelText",
      "FileChooser.helpButtonMnemonic",
      "FileChooser.helpButtonText",
      "FileChooser.homeFolderIcon",
      "FileChooser.listFont",
      "FileChooser.listViewBackground",
      "FileChooser.listViewBorder",
      "FileChooser.listViewIcon",
      "FileChooser.listViewWindowsStyle",
      "FileChooser.lookInLabelMnemonic",
      "FileChooser.mac.newFolder",
      "FileChooser.mac.newFolder.subsequent",
      "FileChooser.newFolderAccessibleName",
      "FileChooser.newFolderButtonText",
      "FileChooser.newFolderErrorSeparator",
      "FileChooser.newFolderErrorText",
      "FileChooser.newFolderExistsErrorText",
      "FileChooser.newFolderIcon",
      "FileChooser.newFolderPromptText",
      "FileChooser.newFolderTitleText",
      "FileChooser.noPlacesBar",
      "FileChooser.openButtonMnemonic",
      "FileChooser.openDialogText",
      "FileChooser.openTitleText",
      "FileChooser.readOnly",
      "FileChooser.saveButtonMnemonic",
      "FileChooser.saveButtonText",
      "FileChooser.saveDialogFileNameLabelText",
      "FileChooser.saveDialogTitleText",
      "FileChooser.saveTitleText",
      "FileChooser.untitledFileName",
      "FileChooser.untitledFolderName",
      "FileChooser.upFolderIcon",
      "FileChooser.updateButtonMnemonic",
      "FileChooser.updateButtonText",
      "FileChooser.useSystemExtensionHiding",
      "FileChooser.usesSingleFilePane"
    };
    for( String key : keys ){
      System.out.println(key + ": " + UIManager.getLookAndFeelDefaults().getString(key));
    }
  }
}
