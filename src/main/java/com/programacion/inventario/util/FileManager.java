package com.programacion.inventario.util;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileManager {
    // Directorio donde se almacenarán los archivos de la aplicación
    private static final String DATA_DIRECTORY = "data";
    private static final String USERS_FILE = DATA_DIRECTORY + "/usuarios.txt";
    private static final String PRODUCTS_FILE = DATA_DIRECTORY + "/productos.json";

    // Constructor que crea el directorio de datos si no existe
    public FileManager() {
        createDataDirectory();
    }

    private void createDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIRECTORY);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("Directorio de datos creado: " + DATA_DIRECTORY);
            }
        } catch (IOException e) {
            System.err.println("Error al crear directorio de datos: " + e.getMessage());
        }
    }

    public boolean saveUserCredentials(String username, String password) {
        try (FileWriter writer = new FileWriter(USERS_FILE, true)) { // true = append mode
            writer.write(username + ":" + password + "\n");
            writer.flush(); // Fuerza la escritura al disco
            System.out.println("Credenciales guardadas para usuario: " + username);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar credenciales: " + e.getMessage());
            return false;
        }
    }

    public boolean validateUserCredentials(String username, String password) {
        try {
            // Verificar si el archivo existe
            if (!Files.exists(Paths.get(USERS_FILE))) {
                System.out.println("Archivo de usuarios no existe, creando usuario por defecto...");
                createDefaultUser();
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue; // Saltar líneas vacías

                    String[] credentials = line.split(":");
                    if (credentials.length == 2) {
                        if (credentials[0].equals(username) && credentials[1].equals(password)) {
                            System.out.println("Credenciales válidas para: " + username);
                            return true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
        }

        System.out.println("Credenciales inválidas para: " + username);
        return false;
    }

    private void createDefaultUser() {
        saveUserCredentials("admin", "admin123");
        saveUserCredentials("profesor", "clase2024");
    }

    public boolean fileExists(String filename) {
        return Files.exists(Paths.get(filename));
    }

    public boolean deleteFile(String filename) {
        try {
            return Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
            System.err.println("Error al eliminar archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el tamaño de un archivo en bytes
     */
    public long getFileSize(String filename) {
        try {
            return Files.size(Paths.get(filename));
        } catch (IOException e) {
            return -1;
        }
    }
}
