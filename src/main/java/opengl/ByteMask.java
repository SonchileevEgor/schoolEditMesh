package opengl;

/**
 * Created by maxbrainrus on 9/14/14.
 */
public class ByteMask {
  // Mask 32x32 bits
  byte[] byteMask = new byte[4 * 32];

  /**
   * Creates byte mask with angle in 45 degrees
   */
  public ByteMask(){
    byteMask = getByteMask45x3();
  }

  public static byte[] getByteMask45x7(){
    byte[] byteMask45 = new byte[4 * 32];
    byte[] byteTypes = new byte[8];
    byte byteCur = 0x01;
    for (int i = 0; i < 8; i++) {
      byteTypes[i] = byteCur;
      byteCur = (byte)(byteCur << 1);
    }
    for (int i = 0; i < 32; i++) {
      for (int j = 0; j < 4; j++) {
        byteMask45[i * 4 + j] = byteTypes[i%8];
      }
    }
    return byteMask45;
  }

  public static byte[] getByteMask45x1(){
    byte[] byteMask45 = new byte[4 * 32];
    byte[] byteTypes = new byte[8];
    byte byteCur = 0x01;
    for (int i = 0; i < 8; i++) {
      byteTypes[i] = byteCur;
      byteCur = (byte)(byteCur << 1);
    }
    for (int i = 0; i < 32; i++) {
      for (int j = 0; j < 4; j++) {
        if (j == 3 - (i / 8))
          byteMask45[i * 4 + j] = byteTypes[i%8];
      }
    }
    return byteMask45;
  }

  public static byte[] getByteMask45x3(){
    byte[] byteMask45 = new byte[4 * 32];
    byte[] byteTypes = new byte[8];
    byte byteCur = 0x01;
    for (int i = 0; i < 8; i++) {
      byteTypes[i] = byteCur;
      byteCur = (byte)(byteCur << 1);
    }
    for (int i = 0; i < 32; i++) {
      for (int j = 0; j < 4; j++) {
        if (j == 3 - (i / 8) || j == 1 - (i / 8) || j == 5 - (i / 8))
          byteMask45[i * 4 + j] = byteTypes[i%8];
      }
    }
    return byteMask45;
  }


  public byte[] getByteMask() {
    return byteMask;
  }
}
