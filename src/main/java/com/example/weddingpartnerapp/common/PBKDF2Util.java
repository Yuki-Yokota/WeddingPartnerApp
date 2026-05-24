package com.example.weddingpartnerapp.common;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PBKDF2Util {
    // 繰り返し回数（多いほど強いが遅い。最低 10,000 推奨。現在10,000）
    private static final int ITERATIONS = 10000;
    // 生成するキー長（bit単位）
    private static final int KEY_LENGTH = 256;

    /**
     * パスワードをハッシュ化（ソルトを使う）
     */
    public static String hashPassword(String password, byte[] salt) throws Exception{
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * ソルト生成
     */
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 128bit salt
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 入力パスワードと保存済みハッシュの一致を確認
     */
    public static boolean verifyPassword(String inputPassword, String storedHash, byte[] storedSalt) throws Exception {
        String newHash = hashPassword(inputPassword, storedSalt);
        return newHash.equals(storedHash);
    }
}
