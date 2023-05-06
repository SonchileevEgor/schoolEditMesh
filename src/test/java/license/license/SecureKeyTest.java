package license.license;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SecureKey functions
 */
public class SecureKeyTest {

    @Test
    public void testValidate() throws Exception {
        String key = SecureKey.generateSecureKey();
        assertEquals(SecureKey.validate(key), true);
        // Wrong validate characters
        assertEquals(SecureKey.validate("AAAAA-AAAA-AAAA-AAAAA"), false);
        // Correct validate characters
        assertEquals(SecureKey.validate("AAAAA-AAAA-AAAA-AAAD8"), true);
    }

    @Test
    public void testAddMinuses() throws Exception {
        String keyWithoutMinuses = SecureKey.generateSecureKey(false);
        assertEquals(SecureKey.validate(keyWithoutMinuses), true);
        String keyWithMinuses = SecureKey.addMinuses(keyWithoutMinuses);
        assertEquals(SecureKey.validate(keyWithMinuses), true);
    }

    @Test
    public void testAddMinuses2() throws Exception {
        assertThrows(WrongKeyFormatException.class, () -> {
            String key = SecureKey.generateSecureKey(true);
            SecureKey.addMinuses(key);
        });
    }

    @Test
    public void testCheckKeyFormat() throws Exception {
        assertEquals(SecureKey.checkKeyFormat("AAAAA-AAAA-AAAA-AAAAA"), true);
        assertEquals(SecureKey.checkKeyFormat("AAAAAAAAAAAAAAAAAA"), true);
        assertEquals(SecureKey.checkKeyFormat("AAAAAAAAAAAAAAAAAAB"), false);
        assertEquals(SecureKey.checkKeyFormat("AAAAA-AAAA-AAAA-AAAAAB"), false);
        assertEquals(SecureKey.checkKeyFormat("AAAAA-AAAA-AAAA-AAAAa"), false);
        assertEquals(SecureKey.checkKeyFormat("AAAAA-AAAA-AAAA-AAAA$"), false);
        assertEquals(SecureKey.checkKeyFormat(""), false);
    }

    @Test
    public void testCheckKeyFormatWithoutMinuses() throws Exception {
        String key = SecureKey.generateSecureKey(false);
        assertEquals(SecureKey.checkKeyFormatWithoutMinuses(key), true);
        key = SecureKey.addMinuses(key);
        assertEquals(SecureKey.checkKeyFormatWithoutMinuses(key), false);
    }

    @Test
    public void testCheckKeyFormatWithMinuses() throws Exception {
        String key = SecureKey.generateSecureKey(true);
        assertEquals(SecureKey.checkKeyFormatWithMinuses(key), true);
        key = SecureKey.cutMinuses(key);
        assertEquals(SecureKey.checkKeyFormatWithMinuses(key), false);
    }

    @Test
    public void testCutMinuses() throws Exception {
        assertThrows(WrongKeyFormatException.class, () -> {
            String key = SecureKey.generateSecureKey(false);
            SecureKey.cutMinuses(key);
        });
    }

    @Test
    public void testGenerateSecureKey() throws Exception {
        String key1 = SecureKey.generateSecureKey(true, "seed".getBytes());
        String key2 = SecureKey.generateSecureKey(true, "seed".getBytes());
        assertEquals(key1, key2);
    }

    @Test
    public void testGenerateSecureKey1() throws Exception {
        String key1 = SecureKey.generateSecureKey(true, "seed".getBytes());
        String key2 = SecureKey.generateSecureKey(true, "another seed".getBytes());
        assertNotEquals(key1, key2);
    }

    @Test
    public void testGenerateSecureKey2() throws Exception {
        String key1 = SecureKey.generateSecureKey();
        String key2 = SecureKey.generateSecureKey();
        assertNotEquals(key1, key2);
    }
}