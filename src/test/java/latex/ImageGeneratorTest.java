package latex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImageGeneratorTest {
  private String resourcesPath = "src/test/resources/";

  public boolean compareExactly(File one, File two) throws IOException {

    if (one.length()==two.length()) {
      FileInputStream fis1 = new FileInputStream(one);
      FileInputStream fis2 =new FileInputStream(two);
      int temp = 0;
      while ((temp = fis1.read()) != -1) {
        if (temp != fis2.read()) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @BeforeEach
  public void setUp() throws Exception {
    checkOutDirectory();
  }

  @Test
  public void testEscapeSpaces() throws Exception {
    assertEquals("1\\ +\\ 1\\ =\\ 2", ImageGenerator.escapeSpaces("1 + 1 = 2"));
    assertEquals("Привет\\ мир", ImageGenerator.escapeSpaces("Привет мир"));
    assertEquals("\\ \\ \\ \\ \\ ", ImageGenerator.escapeSpaces("     "));
  }

  private void checkOutDirectory(){
    File outDir = new File("out");
    if (outDir.exists()){
      if (outDir.isDirectory()){
        ;//All right
      }
      else {
        outDir.delete();
        outDir.mkdir();
      }
    }
    else{
      outDir.mkdir();
    }
  }

  /**
   * Отключен, т к периодически получаются файлы, которые выглядят одинаково, но бинарно отличаются.
   * @throws Exception
   */
  public void testGenerateImageWithTextToFile() throws Exception {
    String test1fileName = "HelloWorld.png";
    ImageGenerator.generateImageWithTextToFile("x^2+5 Привет мир", 25f, "out/"+test1fileName);
    File generatedFile = new File("out/"+test1fileName);
    File testFile = new File(resourcesPath + "test_files/test_image1.png");

    if (!generatedFile.exists())
      throw new NoSuchFileException(generatedFile.getName());
    if (!testFile.exists())
      throw new NoSuchFileException(testFile.getName());

    assertTrue(compareExactly(generatedFile, testFile));
  }
}