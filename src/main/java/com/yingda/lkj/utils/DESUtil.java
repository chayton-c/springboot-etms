package com.yingda.lkj.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;

public class DESUtil {
    // 算法名称
    public static final String KEY_ALGORITHM = "DES";

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static final String KEY = "12345678";

    // 算法名称/加密模式/填充方式
    // DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
    public static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";

    public static void main(String[] args) throws Exception {
        String encrypted = encrypt("12345678", "123456", "12345678");
        System.out.println(encrypted);
        String decrypt = decrypt("12345678", encrypted, "12345678");
        System.out.println(decrypt);
    }

    public static String encrypt(String key, String str, String ivString) throws Exception {
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        Key secretKey = keyFactory.generateSecret(desKeySpec);

        IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
        // CBC
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        // ECB
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] bytes = cipher.doFinal(str.getBytes());
        return Base64.encodeBase64String(bytes);
    }


    public static String decrypt(String key, String str, String ivString) throws Exception {
        byte[] data = Base64.decodeBase64(str);
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        Key secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(ivString.getBytes());
        // CBC
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        // ECB
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(data);
        return new String(decryptedBytes, CHARSET);
    }
}