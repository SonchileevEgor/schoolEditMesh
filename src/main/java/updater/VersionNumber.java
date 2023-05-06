package updater;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author alexeev
 */
public class VersionNumber implements Comparable<VersionNumber>{
  /**
   * Регулярное выражение, соответствующее номеру версии.
   */
  private static Pattern versionPattern =
          Pattern.compile("([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})[^0-9]*");
  
  public final int[] version;
  
  public VersionNumber() {
    version = new int[]{0, 0, 0, 0};
  }
  
  /**
   * Номер версии по строковому представлению.
   * @param version 
   * @throws updater.WrongVersionNumberException 
   */
  public VersionNumber(String version) throws WrongVersionNumberException{
    Matcher m = versionPattern.matcher(version);
    if( m.matches() && m.groupCount() == 4){
      this.version = new int[]{
        Integer.parseInt(m.group(1)),
        Integer.parseInt(m.group(2)),
        Integer.parseInt(m.group(3)),
        Integer.parseInt(m.group(4)),
      };
    } else {
      throw new WrongVersionNumberException("Неверный формат версии: " + version);
    }
  }
  
  /**
   * Сравнение номеров версий.
   * Если текущая версия выше, возвращается true.
   * Если равна или ниже - false.
   * @param v
   * @return 
   */
  public final boolean isGreater(VersionNumber v){
    if( version[0] > v.version[0] )
      return true;
    else if( version[0] == v.version[0] )
      if( version[1] > v.version[1] )
        return true;
      else if( version[1] == v.version[1] )
        if( version[2] > v.version[2] )
          return true;
        else if( version[2] == v.version[2] )
          if( version[3] > v.version[3] )
            return true;
    return false;
  }
  
  @Override
  public boolean equals(Object o){
    if( o == null )
      return false;
    
    if( o.getClass() != VersionNumber.class )
      return false;
    
    VersionNumber v = (VersionNumber) o;
    return (version[0] == v.version[0]) && 
           (version[1] == v.version[1]) && 
           (version[2] == v.version[2]) && 
           (version[3] == v.version[3]);
  }

  @Override
  public int hashCode() {
    // auto-generated method
    int hash = 3;
    hash = 11 * hash + Arrays.hashCode(this.version);
    return hash;
  }
  
  @Override
  public String toString(){
    return String.format("%d.%d.%d.%d", version[0], version[1], version[2], version[3]);
  }

  @Override
  public int compareTo(VersionNumber o) {
    if( equals(o) )
      return 0;
    else if( isGreater(o) )
      return 1;
    else
      return -1;
  }
}
