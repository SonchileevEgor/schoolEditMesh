package updater;

import gui.EdtController;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import minjson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import util.Log;

/**
 * Класс, осуществляющий проверку наличия обновлений.
 * @author alexeev
 */
public class Updater {
  private EdtController _ctrl;
  private String _serverURL;
  private VersionNumber _lastVersion;
  private String _lastVersionURL;
  
  /**
   * Connect to the server.
   * Get last version number and the correspondent installer URL.
   * @param ctrl
   * @param serverURL
   * @throws WrongVersionNumberException
   * @throws MalformedURLException
   * @throws IOException 
   */
  public Updater(EdtController ctrl, String serverURL)
          throws WrongVersionNumberException, MalformedURLException, IOException {
    _ctrl = ctrl;
    _serverURL = serverURL;
    InputStream stream = new URL(_serverURL + "versions.json").openStream();
    JsonObject json = JsonObject.readFrom(IOUtils.toString(stream));
    String lastVersionStr = json.get("current").asString();
    _lastVersion = new VersionNumber(lastVersionStr);
    _lastVersionURL = json.get("all").asObject().
                           get(lastVersionStr).asObject().
                           get("file").asString();
  }
  
  /**
   * Check that last version is newer than current.
   * If error occur, return false.
   * @return 
   */
  public boolean check(){
    try {
      return _lastVersion.isGreater(
              new VersionNumber(config.Config.PROPERTIES.getProperty("version")));
    } catch( WrongVersionNumberException ex ){
      return false;
    }
  }
  
  /**
   * Download and install the last version from server.
   * Now working only for Windows OS.
   * 
   * @throws MalformedURLException
   * @throws IOException 
   */
  public void downloadLastVersion() throws MalformedURLException, IOException {
    // Выбираем директоию для сохранения инсталлятора.
    JFileChooser fileChooser = new JFileChooser(config.Config.USER_DIR);
    fileChooser.setFileFilter(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.isDirectory();
      }
      @Override
      public String getDescription() {
        return "Директории";
      }
    });
    fileChooser.setApproveButtonText("Выбрать");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = fileChooser.showDialog(_ctrl.getFrame(), "Выберите папку для сохранения");
    
    // Создаём локальный файл инсталлятора в пользовательской папке.
    final File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath()
            + "\\sch3dedit_" + _lastVersion + ".exe");
    
    if( returnVal == JFileChooser.APPROVE_OPTION ){
      if( selectedFile.exists() ){
        // если файл существует, спрашивает о перезаписи.
        int reply = JOptionPane.showConfirmDialog(_ctrl.getFrame(),
                "<html><center>Выбранный файл существует.<br>Перезаписать его?</center>");
        if( reply != JOptionPane.YES_OPTION ){
          return;
        }
      }
      // Скачиваем файл с сервера;
      // Если всё прошло успешно - запускаем.
      SwingWorker downloader = new SwingWorker<Boolean, Integer>(){
        @Override
        protected Boolean doInBackground() throws Exception {
          _ctrl.status().showMessage("Загрузка обновлений...");
          _ctrl.status().simulateAction();
          try {
            // Загружаем файл с сервера.
            FileUtils.copyURLToFile(new URL(_serverURL + _lastVersionURL), selectedFile);
            // Статус завершения - успешно.
            return true;
          } catch( IOException ex ){
            // Статус завершения - ошибка.
            return false;
          }
        }

        @Override
        protected void done() {
          boolean errStatus = false;
          String errMessage = "";
          try {
            // Проверяем статус завершщения
            if( get() ){
              // Открываем директорию и выделяем файл установщика.
              Process p = new ProcessBuilder("explorer.exe",
                      "/select," + selectedFile.getAbsolutePath()).start();
            } else {
              errStatus = true;
              errMessage = "При обновлении произошла ошибка: не удалось загрузить установщик!";
            }
          } catch( ExecutionException | InterruptedException ex ){
            errStatus = true;
            errMessage = "Произошёл разрыв соединения. Попробуйте повторить загрузку.";
          } catch( NullPointerException | IOException ex ){
            errStatus = true;
            errMessage = "<html>Не удалось открыть папку установщика!<br>Найдите и запустите его вручную.";
          }
          if( errStatus ){
            Log.out.print("Error: cannot download installer.");
            JOptionPane.showMessageDialog(_ctrl.getFrame(), errMessage, "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
          } else {
            Log.out.printf("Installer %s successfully downloaded", _lastVersion);
          }
          _ctrl.status().clear();
          _ctrl.getLayoutController().unlockFrame();
        }
      };

      _ctrl.getLayoutController().lockFrame();
      downloader.execute();
    }
  }
  
  /**
   * Открыть веб-страницу по заданному адресу.
   * @param url
   * @throws URISyntaxException
   * @throws IOException 
   */
  private void openWebpage(URL url) throws URISyntaxException, IOException {
    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
      desktop.browse(url.toURI());
    }
  }
}
