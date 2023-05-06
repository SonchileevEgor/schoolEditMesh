package license.license;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

/**
 * Created by MAXbrainRUS on 16-Nov-15.
 */
public class LicenseInfoTest {
    static private final String testFileName = "licenseInfoTestFile";

    @Test
    public void testLoadFromFile() throws Exception {
        LicenseInfo licenseInfo = new LicenseInfo("key", new Date(), new Date(), "mac");
        licenseInfo.saveToFile(testFileName);
        LicenseInfo licenseInfoLoading = new LicenseInfo();
        licenseInfoLoading.loadFromFile(testFileName);
        System.out.println(licenseInfo.equals(licenseInfoLoading));
        assertEquals(licenseInfo, licenseInfoLoading);
    }

    @Test
    public void testEquals() throws Exception {
        LicenseInfo licenseInfo = new LicenseInfo("key", new Date(), new Date(), "mac");
        LicenseInfo licenseInfo2 = new LicenseInfo("key", new Date(), new Date(), "mac");
        assertEquals(licenseInfo, licenseInfo2);
    }

    @Test
    public void testCopyData() throws Exception {
        LicenseInfo licenseInfo = new LicenseInfo("key", new Date(1L), new Date(2L), "mac");
        LicenseInfo licenseInfo2 = new LicenseInfo("key2", new Date(3L), new Date(4L), "mac2");
        assertNotEquals(licenseInfo, licenseInfo2);
        licenseInfo.copyData(licenseInfo2);
        assertEquals(licenseInfo, licenseInfo2);
    }
}