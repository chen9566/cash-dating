package me.jiangcai.dating.entity;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.security.crypto.codec.Hex;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 银行,总得管理下它们吧
 *
 * @author CJ
 */
@Entity
@Data
public class Bank {

    @Id
    @Column(length = 10)
    private String code;
    @Column(length = 20)
    private String name;
    /**
     * @see me.jiangcai.dating.Version#v102000
     * @since 1.2
     */
    private int weight = 50;
    /**
     * 背景,等同于css的background属性
     *
     * @since 1.3
     */
    @Column(length = 100)
    private String background = "linear-gradient(to right, #E75C65 , #E8507D);";

    public static boolean containsHanScript(String s) {
        return s.codePoints().anyMatch(
                codePoint ->
                        Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN);
    }

    @SneakyThrows({IOException.class, NoSuchAlgorithmException.class})
    public static String toAsc(String input) {
        if (!containsHanScript(input))
            return input;
        byte[] data = input.getBytes("UTF-8");
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        return new String(Hex.encode(messageDigest.digest(data)));
    }

    /**
     * @return 在这个web系统的图片uri
     * @since 1.3
     */
    public String getImageUri() {
        return "/images/banks/" + toAsc(name) + ".png";
    }
}
