package com.programacion.inventario.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileManager {
    // Directorio donde se almacenarán los archivos de la aplicación
    public static final String DATA_DIRECTORY = "data";

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

    //ESCRIBIR
    public void writeToFile(String filename, String content, boolean append) {
        try (FileWriter writer = new FileWriter(filename, append)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    //LEER
    public List<String> readFromFile(String filename) {
        List<String> listLine = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Saltar líneas vacías
                listLine.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return listLine;
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

    public long getFileSize(String filename) {
        try {
            return Files.size(Paths.get(filename));
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Reescribe completamente un archivo
     */
    public boolean rewriteFile(String filename, List<String> lines) {
        try {
            // Crear archivo temporal
            String tempFile = filename + ".tmp";
            writeToFile(tempFile, "", false);

            for (String line : lines) {
                writeToFile(tempFile, line + "\n", true);
            }

            // Reemplazar archivo original
            deleteFile(filename);
            java.nio.file.Files.move(
                    java.nio.file.Paths.get(tempFile),
                    java.nio.file.Paths.get(filename)
            );

            return true;
        } catch (Exception e) {
            System.err.println("Error reescribiendo archivo: " + e.getMessage());
            return false;
        }
    }
}
