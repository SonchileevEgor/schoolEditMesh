package builders;

import editor.i_BodyBuilder;
import gui.EdtController;
import minjson.JsonObject;

/**
 * Базовый класс теста для одного билдера.
 * @author alexeev
 */
public abstract class BuilderTest {
  public EdtController ctrl;
  public String builderAlias;
  public i_BodyBuilder builder;

  public BuilderTest(EdtController ctrl, String builderAlias){
    this.ctrl = ctrl;
    this.builderAlias = builderAlias;
  }

  /**
   * Создание билдера по набору параметров.
   */
  abstract public void createBuilder();

  /**
   * Тестирование билдера.
   *
   * Билдер сохраняется в Json-формате.
   * После вызывается конструктор этого билдера по JsonObject.
   * В ходе этого процесса не должно быть выброшенных исключений.
   *
   * Проверяется,
   * @throws builders.BuilderTestException
   */
  public void testSaveLoad() throws BuilderTestException {
    JsonObject json = builder.getJSONParams();
    try {
      builder.getClass().getDeclaredConstructor(String.class, String.class, JsonObject.class)
              .newInstance(builder.id(), builder.title(), json);
    } catch( Exception ex ){
      throw new BuilderTestException("Не удалось воссоздать билдер " + builder.alias());
    }
  }

  public void testDescription() throws BuilderTestException {
    builder.create(ctrl.getEditor(), null);
    try {
      System.out.println(builder.description(ctrl, 3));
    } catch( Exception ex ){
      ex.printStackTrace();
      throw new BuilderTestException("Ошибка при вызова описания билдера " + builder.alias());
    }
  }
}
