package license.pcidentifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Created by MAXbrainRUS on 19-Nov-15.
 */
public class PcIdentifierTest {
    private static ArrayList<String> macAddresses;

    @BeforeAll
    public static void initMACAddressList() {
        try {
            macAddresses = PcIdentifier.getMACAddresses();
        } catch (NoMACAddressException e) {
            macAddresses = null;
        }


    }

    @Test
    public void testGetMACAddresses() throws Exception {
        // getMACAddresses function shouldn't throw exceptions
        assertNotEquals(macAddresses, null);
        int size = macAddresses.size();
        for (int i = 0; i < size; i++) {
            String macAddress = macAddresses.get(i);
            if (!PcIdentifier.checkMACAddressFormat(macAddress)) {
                throw new Exception("MAC address does not appear as an MAC address");
            }
        }
    }

    @Test
    public void testCheckMACAddressFormat() throws Exception {
        assertEquals(PcIdentifier.checkMACAddressFormat("3D-F2-C9-A6-B3-4F"), true);
        assertEquals(PcIdentifier.checkMACAddressFormat(""), false);
        assertEquals(PcIdentifier.checkMACAddressFormat("3D:F2:C9:A6:B3:4F"), false);
        assertEquals(PcIdentifier.checkMACAddressFormat("3d-f2-c9-a6-b3-4f"), false);
    }
}