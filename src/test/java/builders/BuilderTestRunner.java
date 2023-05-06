package builders;

import gui.EdtController;
import gui.MainEdtCanvas;
import gui.MainEdtCanvasController;
import java.util.ArrayList;
import opengl.scenegl.Scene3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

/**
 * Класс, запускающий тесты для билдеров.
 * В список <code>tests</code> записываются классы тестов, которые нужно запустить.
 * 
 * Для каждого теста запускаются методы, проверяющие:
 * корректность создания, загрузки, сохранения и вывода описания.
 * 
 * @author alexeev
 */
public class BuilderTestRunner {
  private EdtController ctrl;
  private ArrayList<Class> testClasses;
  
  @BeforeEach
  public void setUp() throws Exception {
    config.Config.init();
    ctrl = new EdtController();
    MainEdtCanvas canvas = new MainEdtCanvas();
    Scene3d scene = new Scene3d(ctrl.getEditor());
    canvas.setScene(scene);
    ctrl.setMainCanvasController(new MainEdtCanvasController(ctrl, canvas));
    
    testClasses = new ArrayList<Class>();
    testClasses.add(PointBuilderTest.class);
    testClasses.add(SphereBuilderTest.class);
  }
  
  @Test
  public void run() {
    for( Class T : testClasses ){
      try {
        // Вызываем конструктор класса теста с аргументом ctrl.
        BuilderTest test = (BuilderTest)T.getConstructor(EdtController.class).newInstance(ctrl);
        
        // Создаём билдер.
        test.createBuilder();
        
        // Проверяем корректность сохранения и загрузки.
        test.testSaveLoad();
        
        // Проверяем вывод описания.
        test.testDescription();
      } catch( BuilderTestException ex ){
        Assertions.fail(ex.getMessage());
      } catch( Exception ex ){
        ex.printStackTrace();
        Assertions.fail("Не удалось вызвать конструктор класса " + T.getName());
      }
    }
  }
}
