package editor;

import java.util.ArrayList;

/**
 * Класс, хранящий цепочку состояний.
 * @author alexeev
 */
public class UndoRedo {
  private static boolean LOGON = false;

  // список доступных состояний для перехода.
  private ArrayList<URState> _states;

  // индекс текущего состояния в списке.
  private int _currState;

  public UndoRedo() {
    _states = new ArrayList<URState>();
    _currState = 0;
  }

  public boolean isRedoEmpty() {
    return _currState >= _states.size() - 1;
  }

  public boolean isUndoEmpty() {
    return _currState <= 0;
  }

  /**
   * Инициализировать класс для использования с новой сценой.
   * Добавляем текущее состояние редактора.
   * @param edt
   */
  public void reload(Editor edt) {
    _states.clear();
    _states.add(new URState(edt.bb(), edt.bd(), edt.anchors(), edt.anchMgr(), edt.bodyMgr(), ""));
    _currState = 0;
  }

  /**
   * Добавить действие для отмены.
   * Все действия состояния после текущего забываются.
   * @param edt
   * @param descr
   */
  public void addUndo(Editor edt, String descr) {
    URState newState = new URState(
            edt.bb(), edt.bd(), edt.anchors(), edt.anchMgr(), edt.bodyMgr(), descr);
    for( int i = _states.size() - 1; i > _currState; i-- ){
      _states.remove(i);
    }
    _states.add(newState);
    _currState++;
    printState();
  }

  /**
   * Undo action.
   * Смещаемся вниз по цепочке состояний.
   * @return выполнена ли операция
   */
  public boolean undo() {
    if( _currState > 0 ){
      _currState--;
      printState();
      return true;
    } else {
      printState();
      return false;
    }
  }

  /**
   * Redo action.
   * Смещаемся вверх по цепочке состояний.
   * @return выполнена ли операция
   */
  public boolean redo() {
    if( _currState < _states.size() - 1 ) {
      _currState++;
      printState();
      return true;
    } else {
      printState();
      return false;
    }
  }

  /**
   * Список описаний всех undo-состояний.
   * @return
   */
  public ArrayList<String> getAllUndoDescriptions() {
    ArrayList<String> descr = new ArrayList<String>();
    for( int i = 1; i <= _currState; i++ ){
      descr.add(_states.get(i).getDescription());
    }

    printState();

    return descr;
  }

  /**
   * Список описаний всех redo-состояний.
   * @return
   */
  public ArrayList<String> getAllRedoDescriptions() {
    ArrayList<String> descr = new ArrayList<String>();
    for( int i = _currState + 1; i < _states.size(); i++ ){
      descr.add(_states.get(i).getDescription());
    }

    return descr;
  }

  /**
   * Текущее состояние.
   * @return
   */
  public URState getCurrState() {
    return _states.get(_currState);
  }

  public void printState() {
    if( LOGON ){
      System.out.printf("Undo/Redo: %d states, current is %d%n", _states.size(), _currState);
    }
  }
}
