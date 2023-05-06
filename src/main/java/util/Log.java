package util;

import java.io.*;

import config.Config;

// Writes to null
class NullOutputStream extends OutputStream {
  public void write(int b) throws IOException {}
  public void write( byte[] b ) throws IOException {}
  public void write( byte[] b, int off, int len ) throws IOException {}
}

public class Log {
  private static String _log_file = FileUtils.joinPath(Config.LOG_DIR, "sch3dedit.log");

  static {
    try {
      out = new PrintStream(_log_file);
    }catch( IOException ex ){
      Fatal.warning("can't create log <" + ex.getMessage() + ">");
      OutputStream null_str = new NullOutputStream();
      out = new PrintStream(null_str);
    }
  }
  public static PrintStream out;
}
