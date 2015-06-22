package me.justup.upme.db_upme;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Knows how to compute md5 hash
 *
 * @author Mike Jennings
 */
public class MD5 {
    private static MD5 singleton;

    public static MD5 getInstance() {
        if (singleton == null) {
            singleton = new MD5();
        }
        return singleton;
    }

    private MessageDigest md5;

    private MD5() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // MD5 is built-in, so this should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Given a string value, compute the md5 hash of this string (as a hex
     * value)
     */
    public String hash(String value) {
        md5.reset();
        byte[] hashBytes;
        try {
            hashBytes = md5.digest(value.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // should not happen, UTF-8 is built-in
            throw new RuntimeException(e);
        }
        String hash = toHex(hashBytes);

        while (32 - hash.length() > 0)
            hash = "0" + hash;
        return hash;
    }

    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        return bi.toString(16).toLowerCase();
    }
}
