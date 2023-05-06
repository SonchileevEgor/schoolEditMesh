package util;

public class Fatal {
  public static void warning( String message ){
    Log.out.println("warning: " + message);
  }

  public static void error( String message ){
    Log.out.println("error: " + message);
    System.exit(1);
  }

  public static void usage( String message ){
    Log.out.println("error: wrong command line args");
    System.exit(1);
  }
};
