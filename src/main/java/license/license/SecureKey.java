package license.license;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Contains methods for license key generation.
 * <p>
 * Format "AAAAA-AAAA-AAAA-AAABB" or without "-", where "A" and "B" are upper letter or digit. "BB" - characters for validation.
 * Last 2 letters uses for validation
 */
public class SecureKey {
    /**
     * Allowable symbols for key (upper letters and digit)
     */
    static private final String allowableSymbols = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Key length without "-" characters.
     */
    static private final int secureKeyWithoutMinusesLength = 18;
    /**
     * Key length wit "-" characters.
     */
    static private final int secureKeyWithMinusesLength = secureKeyWithoutMinusesLength + 3;
    /**
     * Number of validation characters for key validation (at the and of key)
     */
    static private final int secureKeyNumberOfValidationCharacters = 2;
    /**
     * class for getting md5 hash form string
     */
    static private MessageDigest md5Calculator = null;

    static private void init() {
        if (md5Calculator == null) {
            try {
                md5Calculator = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * Calc validate characters on the part of key
     *
     * @param keyPartWithoutMinuses first part of key, without validate characters in no-minuses format
     * @return validate characters
     */
    static private String calcValidateCharacters(String keyPartWithoutMinuses) {
        init();
        byte[] hash = md5Calculator.digest(keyPartWithoutMinuses.getBytes());
        String hashHexString = new BigInteger(1, hash).toString(16);
        return hashHexString.toUpperCase().substring(0, secureKeyNumberOfValidationCharacters);
    }

    /**
     * Check validity of the key
     *
     * @param keyValue key for checking
     * @return true for valid key, false for others
     */
    static public boolean validate(String keyValue) {
        if (checkKeyFormatWithoutMinuses(keyValue)) {
            int secureKeyHeadLength = secureKeyWithoutMinusesLength - secureKeyNumberOfValidationCharacters;
            String headKey = keyValue.substring(0, secureKeyHeadLength);
            String tailKey = keyValue.substring(secureKeyHeadLength);
            return calcValidateCharacters(headKey).contentEquals(tailKey);
        } else if (checkKeyFormatWithMinuses(keyValue)) {
            try {
                return validate(cutMinuses(keyValue));
            } catch (WrongKeyFormatException e) {
                // It shouldn't happened
                throw new RuntimeException(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Convert key without minuses in key with minuses: "ABCDEFGHIJKLMNOPQR" -> "ABCDE-FGHI-JKLM-NOPQR"
     *
     * @param keyWithoutMinuses key for conversion without minuses
     * @return key with minuses
     * @throws WrongKeyFormatException if key has wrong format
     */
    static public String addMinuses(String keyWithoutMinuses) throws WrongKeyFormatException {
        if (!checkKeyFormatWithoutMinuses(keyWithoutMinuses))
            throw new WrongKeyFormatException();
        StringBuilder key = new StringBuilder();
        key.append(keyWithoutMinuses.substring(0, 5));
        key.append('-');
        key.append(keyWithoutMinuses.substring(5, 9));
        key.append('-');
        key.append(keyWithoutMinuses.substring(9, 13));
        key.append('-');
        key.append(keyWithoutMinuses.substring(13));
        return key.toString();
    }

    /**
     * Check key format
     *
     * @param key key for checking
     * @return true if key has correct format, else false
     */
    static public boolean checkKeyFormat(String key) {
        return checkKeyFormatWithMinuses(key) || checkKeyFormatWithoutMinuses(key);
    }

    /**
     * Check key format for keys without minuses
     *
     * @param keyWithoutMinuses key for checking
     * @return true if key has correct format, else false
     */
    static public boolean checkKeyFormatWithoutMinuses(String keyWithoutMinuses) {
        if (keyWithoutMinuses.length() == secureKeyWithoutMinusesLength) {
            for (int i = 0; i < secureKeyWithoutMinusesLength; i++) {
                if (allowableSymbols.indexOf(keyWithoutMinuses.charAt(i)) == -1)
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Check key format for keys with minuses
     *
     * @param keyWithMinuses key for checking
     * @return true if key has correct format, else false
     */
    static public boolean checkKeyFormatWithMinuses(String keyWithMinuses) {
        if (keyWithMinuses.length() == secureKeyWithMinusesLength) {
            for (int i = 0; i < secureKeyWithMinusesLength; i++) {
                if (i == 5 || i == 10 || i == 15) {
                    if (keyWithMinuses.charAt(i) != '-') {
                        return false;
                    }
                } else {
                    if (allowableSymbols.indexOf(keyWithMinuses.charAt(i)) == -1)
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Convert key with minuses in key without minuses: "ABCDE-FGHI-JKLM-NOPQR" -> "ABCDEFGHIJKLMNOPQR"
     *
     * @param keyWithMinuses key for conversion with minuses
     * @return key without minuses
     * @throws WrongKeyFormatException if key has wrong format
     */
    static public String cutMinuses(String keyWithMinuses) throws WrongKeyFormatException {
        if (!checkKeyFormatWithMinuses(keyWithMinuses))
            throw new WrongKeyFormatException();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(keyWithMinuses.substring(0, 5));
        stringBuilder.append(keyWithMinuses.substring(6, 10));
        stringBuilder.append(keyWithMinuses.substring(11, 15));
        stringBuilder.append(keyWithMinuses.substring(16));
        return stringBuilder.toString();
    }

    /**
     * Get random symbol witch can be located in valid key
     *
     * @param secureRandom using random
     * @return random allowable symbol
     */
    static private char getRandomAllowableSymbol(SecureRandom secureRandom) {
        return allowableSymbols.charAt(secureRandom.nextInt(allowableSymbols.length()));
    }

    /**
     * Generate a new secure key
     *
     * @param withMinuses format for generated key
     * @param seed        seed for random algorithm. The same seeds give the same keys.
     * @return New secure key
     */
    static public String generateSecureKey(boolean withMinuses, byte[] seed) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(seed);
            StringBuilder secureKey = new StringBuilder();
            for (int i = 0; i < secureKeyWithoutMinusesLength - secureKeyNumberOfValidationCharacters; i++) {
                secureKey.append(getRandomAllowableSymbol(secureRandom));
            }
            String hashCharacters = calcValidateCharacters(secureKey.toString());
            secureKey.append(hashCharacters);

            if (withMinuses) {
                try {
                    return addMinuses(secureKey.toString());
                } catch (WrongKeyFormatException e) {
                    throw new RuntimeException("Can't generate secure key with correct format(((");
                }
            } else {
                return secureKey.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The same as {@link #generateSecureKey(boolean, byte[])} with random seed.
     */
    static public String generateSecureKey(boolean withMinuses) {
        byte[] seed = SecureRandom.getSeed(128);
        return generateSecureKey(withMinuses, seed);
    }

    /**
     * The same as {@link #generateSecureKey(boolean, byte[])} with random seed and default (with minuses) format.
     */
    static public String generateSecureKey() {
        return generateSecureKey(true);
    }

    /**
     * Generate first key using master seed as a seed.
     *
     * @return
     */
    static public String getFirstKey(byte[] seed) {
        return generateSecureKey(true, seed);
    }

    public static void main(String[] args) {
        String masterSeed = "School 3d editor seed";
        ArrayList<String> keys = new ArrayList<>();
        keys.add(generateSecureKey(true, masterSeed.getBytes()));
        for (int i = 0; i < 10; i++) {
            String key = generateSecureKey(true, (keys.get(keys.size() - 1) + masterSeed).getBytes());
            keys.add(key);
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(keys.get(i));
        }
    }
}
