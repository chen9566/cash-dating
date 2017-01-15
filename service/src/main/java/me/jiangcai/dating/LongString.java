package me.jiangcai.dating;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * 它可以提供将long 和 String 互相进行转换的功能
 *
 * @author CJ
 */
public class LongString {

    private static final SecretKeySpec onlyKey = new SecretKeySpec(new byte[]{
            -1, 99, 75, -9, 86, 120, -106, 54,
            38, 41, -99, 103, 8, 44, -20, -100
    }, "AES");

    public static String toString(long l) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, onlyKey);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (DataOutputStream outputStream = new DataOutputStream(new CipherOutputStream(buffer, cipher))) {
                outputStream.writeLong(l);
                outputStream.flush();
            }
            return Hex.encodeHexString(buffer.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static long toLong(String str) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, onlyKey);
            ByteArrayInputStream buffer = new ByteArrayInputStream(Hex.decodeHex(str.toCharArray()));
            try (DataInputStream inputStream = new DataInputStream(new CipherInputStream(buffer, cipher))) {
                return inputStream.readLong();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
