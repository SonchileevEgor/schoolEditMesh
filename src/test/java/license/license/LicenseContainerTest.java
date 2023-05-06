package license.license;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Date;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Created by MAXbrainRUS on 18-Nov-15.
 */
public class LicenseContainerTest {    
    @TempDir
    Path folder = FileSystems.getDefault().getPath(config.Config.TMP_DIR);

    @Test
    public void testDefaultConstructor() throws Exception {
        LicenseContainer licenseContainer = new LicenseContainer(true);
        String key = licenseContainer.generateNewKey(new Date(), new Date());
        assertEquals(SecureKey.validate(key), true);
    }

    @Test
    public void testGenerateNewKey() throws Exception {
        File newFile = new File(folder.toString(), "tmp");
        LicenseContainer licenseContainer = new LicenseContainer(true, newFile);
        for (int i = 0; i < 10; i++) {
            String key = licenseContainer.generateNewKey(new Date(), new Date());
            assertEquals(SecureKey.validate(key), true);
        }
        newFile.deleteOnExit();
    }

    @Test
    public void testGetUsedLicense() throws Exception {
        File newFile = new File(folder.toString(), "tmp");
        int N = 4;
        LicenseContainer licenseContainer = new LicenseContainer(true, newFile);
        ArrayList<String> keys = new ArrayList<>();
        String[] macs = {"mac1", "mac2", "mac3", "mac4"};
        String[] wrongMacs = {"wrong_mac1", "wrong_mac21", "wrong_mac3", "wrong_mac4"};
        ArrayList<LicenseInfo> licenses = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            String key = licenseContainer.generateNewKey(new Date(), new Date());
            keys.add(key);
            licenses.add(licenseContainer.getLicense(key, macs[i]));
        }
        for (int i = 0; i < N; i++) {
            LicenseInfo license = licenseContainer.getLicense(keys.get(i), macs[i]);
            assertEquals(license, null);
            license = licenseContainer.getLicense(keys.get(i), wrongMacs[i]);
            assertEquals(license, null);
            license = licenseContainer.getUsedLicense(keys.get(i), wrongMacs[i]);
            assertEquals(license, null);
            license = licenseContainer.getUsedLicense(keys.get(i), macs[i]);
            assertEquals(license, licenses.get(i));
        }
        newFile.deleteOnExit();
    }

    @Test
    public void testLoadStateFromFile() throws Exception {
        File newFile = new File(folder.toString(), "tmp");
        LicenseContainer licenseContainer = new LicenseContainer(true, newFile);
        ArrayList<String> keys = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            keys.add(licenseContainer.generateNewKey(new Date(), new Date()));
        }
        // Load state from file
        licenseContainer = new LicenseContainer(false, newFile);
        for (int i = 0; i < 10; i++) {
            // Keys must be in container
            assertNotNull(licenseContainer.getLicense(keys.get(i), "mac"));
        }
        newFile.deleteOnExit();
    }
}