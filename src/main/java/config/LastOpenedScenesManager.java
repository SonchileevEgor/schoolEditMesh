package config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import util.FileUtils;
import util.Log;

/**
 * Manager of last opened scenes.
 * @author alexeev
 */
public class LastOpenedScenesManager {
  static {
    _paths = new ArrayList<String>();
    load(Config.LAST_OPENED_SCENES_FILE);
  }
  static private ArrayList<String> _paths;

  // File storing list of scenes
  static private String _filePath;

  // Size of cache buffer
  static private int _capacity = 5;

  private LastOpenedScenesManager() {}

  /**
   * Load list of last opened scenes
   * from text file.
   * @param filePath
   */
  static public void load( String filePath ){
    _paths.clear();
    try(BufferedReader reader = Files.newBufferedReader(
            FileSystems.getDefault().getPath(filePath), StandardCharsets.UTF_8)){
      String line;
      while( (line = reader.readLine()) != null ){
        _paths.add(line);
      }
      _filePath = filePath;
    } catch( IOException ex ){
      Log.out.printf("Could not open file <%s>: %s%n", filePath, ex.getMessage());
    }
  }

  /**
   * Add scene to list of recent scenes.
   * @param scFile
   */
  static public void add( String scFile ){
    if( _paths.contains(scFile) )
      return;
    _paths.add(scFile);
    if( _paths.size() > _capacity ){
      // Buffer size exceeded
      _paths.remove(0);
    }
    save();
  }

  static public void save() {
    try( BufferedWriter f = Files.newBufferedWriter(
            FileSystems.getDefault().getPath(_filePath), StandardCharsets.UTF_8) ){
      for( String path : _paths ){
        f.write(path + "\n");
      }
    } catch( IOException ex ){
      Log.out.printf("Could not save list of recent scenes to <%s> : %s", _filePath, ex.getMessage());
    }
  }

  /**
   * Get full paths of recent scenes files.
   * @return
   */
  static public ArrayList<String> getPaths() {
    return new ArrayList<String>(_paths);
  }

  /**
   * Number of recent scenes files.
   * @return
   */
  static public int size() {
    return _paths.size();
  }

  /**
   * Get file names of recent scenes files.
   * @return
   */
  static public ArrayList<String> getFilenames() {
    ArrayList<String> result = new ArrayList<String>();
    for( String path : _paths ){
      result.add(FileUtils.getFilename(path));
    }
    return result;
  }

  static public void print() {
    for( String path : _paths ){
      System.out.println(path);
    }
  }
}
