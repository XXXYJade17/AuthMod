package com.XXXYJade.AuthMod.Password;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {
    private PasswordUtils() {
    }

    ;

    public static String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("不支持SHA-256算法，请检查运行环境。", e);
        }
    }

    public static boolean verifyPassword(String password, String storedPassword) {
        try {
            String encryptedPassword = encryptPassword(password);
            return encryptedPassword.equals(storedPassword);
        } catch (RuntimeException e) {
            System.err.println("密码验证过程中出现错误: " + e.getMessage());
            return false;
        }
    }
}
