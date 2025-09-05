package com.programacion.inventario.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Clase utilitaria para operaciones de seguridad y cifrado
 * Implementa SHA-256 para hashing de contraseñas y funciones de descifrado
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
        String[] parts = line.split(":");
        return parts.length == 2;
    }

    /**
     * Detecta si una línea del archivo está en formato cifrado
     */
    public static boolean isHashedFormat(String line) {
        String[] parts = line.split(":");
        return parts.length == 3;
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

    /**
     * Descifra y obtiene información de credenciales del archivo
     * Retorna un mapa con usuarios y sus detalles de cifrado
     */
    public static Map<String, Map<String, String>> decryptCredentials(List<String> userLines) {
        Map<String, Map<String, String>> decryptedData = new HashMap<>();

        for (String line : userLines) {
            if (line.trim().isEmpty()) continue;

            String[] credentials = line.split(":");
            if (credentials.length >= 2) {
                Map<String, String> userInfo = new HashMap<>();
                String username = credentials[0];

                if (credentials.length == 2) {
                    // Formato texto plano
                    userInfo.put("password", credentials[1]);
                    userInfo.put("hash", "TEXTO_PLANO");
                    userInfo.put("salt", "N/A");
                    userInfo.put("format", "texto_plano");
                    userInfo.put("security_level", "BAJO");
                } else if (credentials.length == 3) {
                    // Formato cifrado
                    userInfo.put("password", "CIFRADO");
                    userInfo.put("hash", credentials[1]);
                    userInfo.put("salt", credentials[2]);
                    userInfo.put("format", "sha256_cifrado");
                    userInfo.put("security_level", "ALTO");
                }

                decryptedData.put(username, userInfo);
            }
        }

        return decryptedData;
    }

    /**
     * Obtiene estadísticas de seguridad del archivo de usuarios
     */
    public static Map<String, Object> getSecurityStats(List<String> userLines) {
        Map<String, Object> stats = new HashMap<>();
        int totalUsers = 0;
        int plainTextUsers = 0;
        int hashedUsers = 0;

        for (String line : userLines) {
            if (line.trim().isEmpty()) continue;

            totalUsers++;
            if (isPlainTextFormat(line)) {
                plainTextUsers++;
            } else if (isHashedFormat(line)) {
                hashedUsers++;
            }
        }

        stats.put("total_usuarios", totalUsers);
        stats.put("usuarios_texto_plano", plainTextUsers);
        stats.put("usuarios_cifrados", hashedUsers);
        stats.put("porcentaje_cifrado", totalUsers > 0 ? (hashedUsers * 100.0 / totalUsers) : 0);
        stats.put("recomendacion", plainTextUsers > 0 ? "MIGRAR_USUARIOS" : "SEGURIDAD_OK");

        return stats;
    }

    /**
     * Verifica la fortaleza de una contraseña
     */
    public static String checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "DEBIL";
        }

        int strength = 0;

        // Longitud mínima
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;

        // Diversidad de caracteres
        if (password.matches(".*[A-Z].*")) strength++; // Mayúsculas
        if (password.matches(".*[a-z].*")) strength++; // Minúsculas
        if (password.matches(".*[0-9].*")) strength++; // Números
        if (password.matches(".*[^A-Za-z0-9].*")) strength++; // Símbolos

        if (strength >= 5) return "MUY_FUERTE";
        if (strength >= 4) return "FUERTE";
        if (strength >= 3) return "MEDIA";
        return "DEBIL";
    }

    /**
     * Genera un reporte de seguridad completo
     */
    public static String generateSecurityReport(List<String> userLines) {
        Map<String, Object> stats = getSecurityStats(userLines);
        Map<String, Map<String, String>> users = decryptCredentials(userLines);

        StringBuilder report = new StringBuilder();
        report.append("=== REPORTE DE SEGURIDAD DEL SISTEMA ===\n\n");

        report.append("ESTADÍSTICAS GENERALES:\n");
        report.append("• Total de usuarios: ").append(stats.get("total_usuarios")).append("\n");
        report.append("• Usuarios en texto plano: ").append(stats.get("usuarios_texto_plano")).append("\n");
        report.append("• Usuarios cifrados: ").append(stats.get("usuarios_cifrados")).append("\n");
        report.append("• Porcentaje de cifrado: ").append(String.format("%.1f", stats.get("porcentaje_cifrado"))).append("%\n");
        report.append("• Recomendación: ").append(stats.get("recomendacion")).append("\n\n");

        report.append("DETALLES POR USUARIO:\n");
        for (Map.Entry<String, Map<String, String>> entry : users.entrySet()) {
            String username = entry.getKey();
            Map<String, String> userInfo = entry.getValue();

            report.append("• ").append(username).append(":\n");
            report.append("  - Formato: ").append(userInfo.get("format")).append("\n");
            report.append("  - Nivel seguridad: ").append(userInfo.get("security_level")).append("\n");

            if ("texto_plano".equals(userInfo.get("format"))) {
                String strength = checkPasswordStrength(userInfo.get("password"));
                report.append("  - Fortaleza contraseña: ").append(strength).append("\n");
                report.append("  - Contraseña: ").append(userInfo.get("password")).append(" (VISIBLE - ¡RIESGO!)\n");
            } else {
                report.append("  - Hash: ").append(userInfo.get("hash").substring(0, 20)).append("...\n");
                report.append("  - Salt: ").append(userInfo.get("salt").substring(0, 10)).append("...\n");
            }
            report.append("\n");
        }

        report.append("RECOMENDACIONES DE SEGURIDAD:\n");
        if ((Integer)stats.get("usuarios_texto_plano") > 0) {
            report.append("⚠️  MIGRAR USUARIOS A FORMATO CIFRADO INMEDIATAMENTE\n");
        }
        report.append("✅ Todos los usuarios deben usar contraseñas con:\n");
        report.append("   - Mínimo 8 caracteres\n");
        report.append("   - Mayúsculas, minúsculas y números\n");
        report.append("   - Símbolos especiales para mayor seguridad\n");

        return report.toString();
    }
}
