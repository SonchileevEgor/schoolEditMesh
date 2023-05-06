package geom;

/**
 *
 * @author alexeev
 */
public class MengerSponge {
  public boolean[][][] b;
  public int length;

  public MengerSponge(int steps) {
    boolean init[][][] = new boolean[1][1][1];
    init[0][0][0] = true;
    int size = 1;
    for (int i = 0; i < steps; i++) {
      init = nextMenger(init, size);
      size = size * 3;
    }
    b = init;
    this.length = size;
  }

  private boolean[][][] nextMenger(boolean[][][] prev, int size) {
    boolean[][][] result = new boolean[size * 3][size * 3][size * 3];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        for (int k = 0; k < size; k++) {
          if (!prev[i][j][k]) {
            result[3 * i][3 * j][3 * k] = false;
            result[3 * i][3 * j][3 * k + 1] = false;
            result[3 * i][3 * j][3 * k + 2] = false;
            result[3 * i][3 * j + 1][3 * k] = false;
            result[3 * i][3 * j + 1][3 * k + 1] = false;
            result[3 * i][3 * j + 1][3 * k + 2] = false;
            result[3 * i][3 * j + 2][3 * k] = false;
            result[3 * i][3 * j + 2][3 * k + 1] = false;
            result[3 * i][3 * j + 2][3 * k + 2] = false;
            result[3 * i + 1][3 * j][3 * k] = false;
            result[3 * i + 1][3 * j][3 * k + 1] = false;
            result[3 * i + 1][3 * j][3 * k + 2] = false;
            result[3 * i + 1][3 * j + 1][3 * k] = false;
            result[3 * i + 1][3 * j + 1][3 * k + 1] = false;
            result[3 * i + 1][3 * j + 1][3 * k + 2] = false;
            result[3 * i + 1][3 * j + 2][3 * k] = false;
            result[3 * i + 1][3 * j + 2][3 * k + 1] = false;
            result[3 * i + 1][3 * j + 2][3 * k + 2] = false;
            result[3 * i + 2][3 * j][3 * k] = false;
            result[3 * i + 2][3 * j][3 * k + 1] = false;
            result[3 * i + 2][3 * j][3 * k + 2] = false;
            result[3 * i + 2][3 * j + 1][3 * k] = false;
            result[3 * i + 2][3 * j + 1][3 * k + 1] = false;
            result[3 * i + 2][3 * j + 1][3 * k + 2] = false;
            result[3 * i + 2][3 * j + 2][3 * k] = false;
            result[3 * i + 2][3 * j + 2][3 * k + 1] = false;
            result[3 * i + 2][3 * j + 2][3 * k + 2] = false;
          } else {
            // если две или три "+1", вырезаем.
            result[3 * i][3 * j][3 * k] = true;
            result[3 * i][3 * j][3 * k + 1] = true;
            result[3 * i][3 * j][3 * k + 2] = true;
            result[3 * i][3 * j + 1][3 * k] = true;
            result[3 * i][3 * j + 1][3 * k + 1] = false;
            result[3 * i][3 * j + 1][3 * k + 2] = true;
            result[3 * i][3 * j + 2][3 * k] = true;
            result[3 * i][3 * j + 2][3 * k + 1] = true;
            result[3 * i][3 * j + 2][3 * k + 2] = true;
            result[3 * i + 1][3 * j][3 * k] = true;
            result[3 * i + 1][3 * j][3 * k + 1] = false;
            result[3 * i + 1][3 * j][3 * k + 2] = true;
            result[3 * i + 1][3 * j + 1][3 * k] = false;
            result[3 * i + 1][3 * j + 1][3 * k + 1] = false;
            result[3 * i + 1][3 * j + 1][3 * k + 2] = false;
            result[3 * i + 1][3 * j + 2][3 * k] = true;
            result[3 * i + 1][3 * j + 2][3 * k + 1] = false;
            result[3 * i + 1][3 * j + 2][3 * k + 2] = true;
            result[3 * i + 2][3 * j][3 * k] = true;
            result[3 * i + 2][3 * j][3 * k + 1] = true;
            result[3 * i + 2][3 * j][3 * k + 2] = true;
            result[3 * i + 2][3 * j + 1][3 * k] = true;
            result[3 * i + 2][3 * j + 1][3 * k + 1] = false;
            result[3 * i + 2][3 * j + 1][3 * k + 2] = true;
            result[3 * i + 2][3 * j + 2][3 * k] = true;
            result[3 * i + 2][3 * j + 2][3 * k + 1] = true;
            result[3 * i + 2][3 * j + 2][3 * k + 2] = true;
          }
        }
      }
    }
    return result;
  }
}
