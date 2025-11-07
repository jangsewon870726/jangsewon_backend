package com.wirebarley.transfer.core.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 비밀번호 암호화(SHA-256) 및 Salt 생성을 위한 유틸리티 클래스
 */
public final class SecurityUtil {

    private static final int SALT_SIZE = 16;
    private static final String HASH_ALGORITHM = "SHA-256";

    private SecurityUtil() {
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_SIZE];
        random.nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);

            md.update(password.getBytes(StandardCharsets.UTF_8));
            md.update(saltBytes);

            byte[] hashedPassword = md.digest();

            return Base64.getEncoder().encodeToString(hashedPassword);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 암호화 알고리즘을 찾을 수 없습니다.", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Salt 디코딩에 실패했습니다.", e);
        }
    }
}