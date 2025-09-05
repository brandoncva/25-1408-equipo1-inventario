package com.programacion.inventario.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Clase utilitaria para operaciones de seguridad y cifrado
 * Implementa SHA-256 para hashing de contraseñas
 */
public class SecurityUtils {

    private static final String SHA_256 = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * Genera un salt aleatorio para mayor seguridad
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashea una contraseña con SHA-256 usando salt
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256);
            String combined = password + salt;
            byte[] hashedBytes = digest.digest(combined.getBytes());

            // Convertir bytes a hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña: " + e.getMessage());
        }
    }

    /**
     * Verifica si una contraseña coincide con el hash almacenado
     */
    public static boolean verifyPassword(String inputPassword, String storedHash, String salt) {
        String hashedInput = hashPassword(inputPassword, salt);
        return hashedInput.equals(storedHash);
    }

    /**
     * Detecta si una línea del archivo está en formato antiguo (texto plano)
     */
    public static boolean isPlainTextFormat(String line) {
        return line.split(":").length == 2;
    }

    /**
     * Convierte una línea de texto plano a formato cifrado
     */
    public static String migrateToHashedFormat(String plainTextLine) {
        String[] parts = plainTextLine.split(":");
        if (parts.length != 2) return plainTextLine;

        String username = parts[0];
        String password = parts[1];
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        return username + ":" + hashedPassword + ":" + salt;
    }
}
