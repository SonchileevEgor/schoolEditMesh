package updater;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author alexeev
 */
public class VersionNumberTest {
  private final String[] correctVersions = new String[]{
    "0.0.0.0", "255.255.255.255", "0.000.010.000", "0.0.100.000", "700.0.000.000",
    "100.0.0.0", "130.0.0.1", "1.0.0.0", "1.2.0.0", "1.0.0.1", "10.0.0.1", "10.0.2.0"};
  
  private final String[] sortedCorrectVersions = new String[]{
    "0.0.0.0", "0.000.010.000", "0.0.100.000", "1.0.0.0", "1.0.0.1", "1.2.0.0",
    "10.0.0.1", "10.0.2.0", "100.0.0.0", "130.0.0.1", "255.255.255.255", "700.0.000.000"};
  
  private final String[] incorrectVersions = new String[]{
    "0.0.0.X", "255..255.255", "0.1000.000.000", "0.0000.000.1", "0.000.000",
    "100..0.0", "130.0.0.", "2.1.0.0.0", "1....0", "0125551233", "", "-1.024.337.129"};
  
  public VersionNumberTest() {
  }

  @Test
  public void testVersionNumber() {
    System.out.println("Testing version number parser...");
    for( String version : incorrectVersions ) {
      try {
        System.out.println(new VersionNumber(version));
        fail(version);
      } catch( WrongVersionNumberException ex ){
        System.out.println(ex.getMessage());
      }
    }
    for( String version : correctVersions ) {
      try {
        System.out.println(new VersionNumber(version));
      } catch( WrongVersionNumberException ex ){
        fail(version);
      }
    }
  }
  
  @Test
  public void test_isGreater() {
    ArrayList<VersionNumber> vlist = new ArrayList<>(
            Collections2.transform(Arrays.asList(correctVersions), 
              new Function<String, VersionNumber>() {
                @Override
                public VersionNumber apply(String input) {
                  try {
                    return new VersionNumber(input);
                  } catch( WrongVersionNumberException ex ){
                    return new VersionNumber();
                  }
                }
              })
    );
    
    ArrayList<VersionNumber> correctList = new ArrayList<>(
            Collections2.transform(Arrays.asList(sortedCorrectVersions),
              new Function<String, VersionNumber>() {
                @Override
                public VersionNumber apply(String input) {
                  try {
                    return new VersionNumber(input);
                  } catch( WrongVersionNumberException ex ){
                    return new VersionNumber();
                  }
                }
              })
    );
    Collections.sort(vlist);
    for( int i = 0; i < vlist.size(); i++ ){
      if( !vlist.get(i).equals(correctList.get(i)) ){
        fail("Список версий отсортирован неверно!");
      }
    }
  }
  
}
