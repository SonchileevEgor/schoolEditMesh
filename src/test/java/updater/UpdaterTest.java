package updater;

import gui.EdtController;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

/**
 * С MockServer проблемы, пока отключаю.
 * @author alexeev
 */
@Disabled
public class UpdaterTest {
  private String resourcesPath;
  private ClientAndServer mockServer;
  private Updater updater;

  @BeforeEach
  public void prepare() {
    resourcesPath = "src/test/resources/";
    mockServer = ClientAndServer.startClientAndServer(10080);

    File testFile = new File(resourcesPath + "test_files/updater/versions.json");

    try {
      mockServer.when(HttpRequest.request().withPath("/versions.json")).
              respond(HttpResponse.response().withBody(
                      new BinaryBody(Files.readAllBytes(testFile.toPath()))));
    } catch( IOException ex ){
      fail("Не обнаружен тестовый файл");
    }
  }

  @AfterEach
  public void finish() {
    mockServer.stop();
  }

  @Test
  public void testUpdater() {
    try {
      updater = new Updater(new EdtController(), "http://127.0.0.1:10080/");
    } catch( IOException ex ) {
      System.out.print("IO Exception: cannot create Updater instance");
      fail();
    } catch( WrongVersionNumberException ex ){
      System.out.print("Wrong format of test file \"versions.json\"");
      fail();
    }
  }
}
