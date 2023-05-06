package license.encryption;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Simple library for encryption messages
 */
public class Encryption {
    /**
     * Initialization vector for encryption algorithm.
     */
    private static byte[] ivBytes = "sch3dedt".getBytes();

    /**
     * Encrypt message with a symmetric key.
     * <p>
     * <p>
     * Using "DES/CBC/PKCS5Padding" algorithm with "SHA1PRNG" algorithm for getting random numbers.
     *
     * @param input bytes for encryption.
     * @param key   symmetric key for encrypt/decrypt input bytes.
     * @return encrypted input as array of bytes.
     * @throws EncryptionInternalError if this encryption isn't supported.
     */
    public static byte[] encrypt(byte[] input, byte[] key) throws EncryptionInternalError {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("DES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key);
            keygen.init(56, secureRandom);
            byte[] key56 = keygen.generateKey().getEncoded();
            // wrap key data in Key/IV specs to pass to cipher
            SecretKeySpec keySpec = new SecretKeySpec(key56, "DES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            // create the cipher with the algorithm you choose
            // see javadoc for Cipher class for more info, e.g.

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            // Encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = new byte[cipher.getOutputSize(input.length)];
            int enc_len = cipher.update(input, 0, input.length, encrypted, 0);
            enc_len += cipher.doFinal(encrypted, enc_len);
            encrypted = Arrays.copyOf(encrypted, enc_len);
            return encrypted;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
            throw new EncryptionInternalError();
        }
    }

    /**
     * The same as {@link #encrypt(byte[], byte[])} but gets and returns result as a string.
     *
     * @param input string for encryption
     * @param key   symmetric key for encrypt/decrypt input bytes.
     * @return encrypted message as a string where every byte represented as his HEX representation(lowercase letters).
     * @throws EncryptionInternalError if this encryption isn't supported.
     */
    public static String encryptTextMode(String input, byte[] key) throws EncryptionInternalError {
        byte[] encryptedData = Encryption.encrypt(input.getBytes(), key);
        return Hex.encodeHexString(encryptedData);
    }

    /**
     * Decrypt message with a symmetric key.
     * <p>
     * Using "DES/CBC/PKCS5Padding" algorithm with "SHA1PRNG" algorithm for getting random numbers.
     *
     * @param input bytes for decryption.
     * @param key   symmetric key for encrypt/decrypt input bytes.
     * @return decrypted input as array of bytes.
     * @throws EncryptionInternalError    if this encryption isn't supported.
     * @throws WrongEncryptedKeyException if key doesn't fit for this input.
     */
    public static byte[] decrypt(byte[] input, byte[] key) throws EncryptionInternalError, WrongEncryptedKeyException {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("DES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key);
            keygen.init(56, secureRandom);
            byte[] key56 = keygen.generateKey().getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(key56, "DES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = new byte[cipher.getOutputSize(input.length)];
            int dec_len = cipher.update(input, 0, input.length, decrypted, 0);
            dec_len += cipher.doFinal(decrypted, dec_len);
            // Byte array has zero bytes in the end
            return Arrays.copyOf(decrypted, dec_len);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            throw new EncryptionInternalError();
        } catch (BadPaddingException | ShortBufferException | IllegalBlockSizeException | InvalidKeyException e) {
            throw new WrongEncryptedKeyException();
        }

    }

    /**
     * The same as {@link #decrypt(byte[], byte[])} but gets and returns result as a string.
     *
     * @param input encrypted message as a string where every byte represented as his HEX representation(lowercase letters).
     * @param key   symmetric key for encrypt/decrypt input bytes.
     * @return decrypted input as a string.
     * @throws EncryptionInternalError    if this encryption isn't supported.
     * @throws WrongEncryptedKeyException if key doesn't fit for this input.
     */
    public static String decryptTextMode(String input, byte[] key) throws EncryptionInternalError, WrongEncryptedKeyException {
        try {
            byte[] decryptedData = Encryption.decrypt(Hex.decodeHex(input.toCharArray()), key);
            return new String(decryptedData);
        } catch (DecoderException e) {
            throw new WrongEncryptedKeyException();
        }
    }
}