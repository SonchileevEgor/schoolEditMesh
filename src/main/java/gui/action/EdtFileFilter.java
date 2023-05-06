package gui.action;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FilenameUtils;

/**
 * File filter for native sch3dedit formats.
 * @author alexeev
 */
public class EdtFileFilter extends FileFilter {
  @Override
  public boolean accept(File f) {
    return FilenameUtils.isExtension(f.getName(), new String[]{"scene", "sc2", "sc3"}) ||
           f.isDirectory();
  }
  @Override
  public String getDescription() {
    return "Сцена School3Dedit";
  }
}
