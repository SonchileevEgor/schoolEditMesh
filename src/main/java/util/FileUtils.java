package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import org.apache.commons.io.FilenameUtils;


public class FileUtils {

  public static boolean mkdirs(String path) {
    return new File(path).mkdirs();
  }

  public static boolean exists(String path) {
    return new File(path).exists();
  }

  public static String getAbsPath(String path) {
    return new File(path).getAbsolutePath();
  }

  public static String getFilename(String path) {
    return new File(path).getName();
  }

  public static String joinPath(String... paths) {
    if (paths.length == 0) {
      return "";
    }
    File combined = new File(paths[0]);
    for (int i = 1; i < paths.length; i++) {
      combined = new File(combined, paths[i]);
    }
    return combined.getAbsolutePath();
  }

  /**
   * Set desirable extension to the file.
   * @param fname
   * @param desirableExt extension WITHOUT leading point.
   * @return
   */
  public static String setExtension(String fname, String desirableExt) {
    return FilenameUtils.removeExtension(fname) + "." + desirableExt;
  }

  public static void copy(String source, String destination, CopyOption... option) throws IOException {
    File src = new File(source);
    File dst = new File(destination);
    if (!dst.exists())
      Files.copy(src.toPath(), dst.toPath(), option);
  }

  public static void copyDir(String source, String dest) throws IOException {
    File fSource = new File(source);
    File fDest = new File(dest);
    try {
      if (fSource.isDirectory()) {
        // A simple validation, if the destination is not exist then create it
        if (!fDest.exists()) {
          fDest.mkdirs();
        }
        // Create list of files and directories on the current source
        // Note: with the recursion 'fSource' changed accordingly
        String[] fList = fSource.list();
        for (String path: fList) {
          // Recursion call take place here
          copyDir(FileUtils.joinPath(source, path), FileUtils.joinPath(dest, path));
        }
      } else {
        // Found a file. Copy it into the destination, which is already created in 'if' condition above
        if (!fDest.exists()) {
          // Open a file for read and write (copy)
          try (FileInputStream fInStream = new FileInputStream(fSource)) {
            try (FileOutputStream fOutStream = new FileOutputStream(fDest)) {
              // Read 2K at a time from the file
              byte[] buffer = new byte[2048];
              int iBytesReads;
              // In each successful read, write back to the source
              while ((iBytesReads = fInStream.read(buffer)) >= 0) {
                fOutStream.write(buffer, 0, iBytesReads);
              }
            }
          }
        }
      }
    } catch (Exception ex) {
      // Please handle all the relevant exceptions here
    }
  }
}
