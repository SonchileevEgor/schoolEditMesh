package license.encryption;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Test for encryption library
 */
public class EncryptionTest {
    @Test
    public void testEncryptDecrypt() throws Exception {
        String input = "There is input data";
        String key = "This is a key";
        byte[] encryptedBytes = Encryption.encrypt(input.getBytes(), key.getBytes());
        byte[] decryptedBytes = Encryption.decrypt(encryptedBytes, key.getBytes());
        assertArrayEquals(input.getBytes(), decryptedBytes);
    }

    @Test
    public void testEncryptDecryptTextMode() throws Exception {
        String input = "There is input data";
        String key = "This is a key";
        String encryptedString = Encryption.encryptTextMode(input, key.getBytes());
        String decryptedString = Encryption.decryptTextMode(encryptedString, key.getBytes());
        assertEquals(input, decryptedString);
    }

    @Test
    public void testEncryptSimpleTest() throws Exception {
        assertThrows(AssertionError.class, () -> {
            String input = "There is input data";
            String key = "This is a key";
            byte[] encryptedBytes = Encryption.encrypt(input.getBytes(), key.getBytes());
            // Start input and encrypted input must be different
            assertArrayEquals(input.getBytes(), encryptedBytes);
        });
    }

    @Test
    public void testEncryptSimpleTestTextMode() throws Exception {
        String input = "There is input data";
        String key = "This is a key";
        String encryptedString = Encryption.encryptTextMode(input, key.getBytes());
        // Start input and encrypted input must be different
        assertNotEquals(input, encryptedString);
    }

    /**
     * We shouldn't get true input with wrong key
     */
    @Test
    public void testWrongKey() throws Exception {
        assertThrows(WrongEncryptedKeyException.class, () -> {
            String input = "There is input data";
            String trueKey = "This is a true key";
            String wrongKey = "This is a wrong key";
            byte[] encryptedBytes = Encryption.encrypt(input.getBytes(), trueKey.getBytes());
            byte[] decryptedBytes = Encryption.decrypt(encryptedBytes, wrongKey.getBytes());
        });
    }

    /**
     * We shouldn't get true input with wrong key
     */
    @Test
    public void testWrongKeyTextMode() throws Exception {
        assertThrows(WrongEncryptedKeyException.class, () -> {
            String input = "There is input data";
            String trueKey = "This is a true key";
            String wrongKey = "This is a wrong key";
            String encryptedString = Encryption.encryptTextMode(input, trueKey.getBytes());
            String decryptedString = Encryption.decryptTextMode(encryptedString, wrongKey.getBytes());
            assertNotEquals(encryptedString, decryptedString);
        });
    }

    /**
     * Encrypted test message should be same on different machines.
     *
     * @throws Exception
     */
    @Test
    public void testTestMessageEncrypted() throws Exception {
        String testMessage = "Test message";
        String testEncryptionKey = "Test encryption key";
        String testEncryptedMessage = Encryption.encryptTextMode(testMessage, testEncryptionKey.getBytes());
        assertEquals(testEncryptedMessage, "818b9e341142b35a82a1261428935a59");
    }
}