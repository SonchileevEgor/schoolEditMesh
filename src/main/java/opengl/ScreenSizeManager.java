package opengl;

/**
 * Contain current size of screen
 */
public class ScreenSizeManager {
  static int screenWidth;
  static int screenHeight;

  static public void setScreenSize(int width, int height){
    screenWidth = width;
    screenHeight = height;
  }

  public static int getScreenWidth() {
    return screenWidth;
  }

  public static int getScreenHeight() {
    return screenHeight;
  }

  public static int getMaxSize(){
    return Math.max(screenWidth, screenHeight);
  }

}
