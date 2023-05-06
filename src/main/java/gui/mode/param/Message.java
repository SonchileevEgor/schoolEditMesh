package gui.mode.param;

import java.util.ArrayList;

/**
 * Message of create body mode 
 * @author rita
 */
public class Message {
  int _current;
  ArrayList<String> _msgs;
  boolean _repeat; 
  
  public Message() {
    _current = 0;
    _msgs = new ArrayList<String>();
    _repeat = false;
  }
  
  public void setMessage(final String... msgs) {
    for(int i = 0; i < msgs.length; i++)
      _msgs.add(msgs[i]);
  }
  
  public void setRepeatMessage(String msg) {
    _msgs.add(msg);
    _repeat = true;
  }
  
  public String current() {
    String result;
    
    if(_current < _msgs.size()) {
      result = _msgs.get(_current);
      _current++;
    } else if(_repeat) {
      result = _msgs.get(0);
    } else {
      result = "";
    }
    return result;
  }
}
