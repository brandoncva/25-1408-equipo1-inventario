package com.programacion.inventario.controller;

import com.programacion.inventario.util.FileManager;
import com.programacion.inventario.util.NavigationManager;
import com.programacion.inventario.util.SecurityUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;
    @FXML private Button decryptButton;
    @FXML private VBox securityReportBox;
    @FXML private Text securityReportText;

    private FileManager fileManager;
    private String USERS_FILE;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar los managers
        fileManager = new FileManager();
        this.USERS_FILE = fileManager.DATA_DIRECTORY + "/usuarios.txt";

        // Configurar eventos de teclado (Enter para login)
        passwordField.setOnAction(event -> handleLogin());
        usernameField.setOnAction(event -> passwordField.requestFocus());

        // Limpiar mensaje al escribir
        usernameField.textProperty().addListener((obs, oldText, newText) -> clearMessage());
        passwordField.textProperty().addListener((obs, oldText, newText) -> clearMessage());

        // Ocultar panel de reporte inicialmente
        securityReportBox.setVisible(false);

        System.out.println("LoginController inicializado - Sistema de cifrado listo");
    }

    /**
     * Maneja el proceso de login
     * Demuestra la lectura de archivos TXT para validar credenciales
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validaciones básicas
        if (username.isEmpty()) {
            showMessage("Por favor ingrese un nombre de usuario", "error");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showMessage("Por favor ingrese una contraseña", "error");
            passwordField.requestFocus();
            return;
        }

        // Desactivar botones durante el proceso
        setButtonsDisabled(true);
        showMessage("Validando credenciales...", "info");

        try {
            // CONCEPTO CLAVE: Validación de credenciales desde archivo TXT
            boolean isValid = validateUserCredentials(username, password);

            if (isValid) {
                showMessage("¡Login exitoso! Cargando sistema principal...", "success");

                // Pequeña pausa para mostrar el mensaje
                javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(1000); // Pausa de 1 segundo
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        // Navegar a la ventana principal usando NavigationManager
                        try {
                            // Crear parámetros para pasar al MainController
                            Map<String, Object> parameters = new HashMap<>();
                            parameters.put("username", username);
                            parameters.put("role", "Usuario"); // Por defecto, se puede mejorar

                            // Navegar a la pantalla principal
                            NavigationManager navigationManager = NavigationManager.getInstance();
                            navigationManager.navigateTo(NavigationManager.Screen.MAIN, parameters);

                        } catch (Exception e) {
                            System.err.println("Error al navegar: " + e.getMessage());
                            showMessage("Error al cargar la pantalla principal", "error");
                        }
                    }
                };

                new Thread(task).start();

            } else {
                showMessage("Usuario o contraseña incorrectos", "error");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de Login");
                alert.setHeaderText(null);
                alert.setContentText("Las credenciales ingresadas son inválidas. Intente nuevamente.");
                alert.showAndWait();
                passwordField.clear();
                passwordField.requestFocus();
            }

        } catch (Exception e) {
            showMessage("Error al validar credenciales: " + e.getMessage(), "error");
            e.printStackTrace();
        } finally {
            setButtonsDisabled(false);
        }
    }

    /**
     * Maneja el proceso de registro de nuevos usuarios
     * Demuestra la escritura de archivos TXT
     */
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validaciones básicas
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Complete todos los campos para registrarse", "error");
            return;
        }

        if (password.length() < 4) {
            showMessage("La contraseña debe tener al menos 4 caracteres", "error");
            return;
        }

        // Verificar si el usuario ya existe
        if (validateUserCredentials(username, "dummy")) {
            // Si las credenciales son válidas con cualquier contraseña dummy,
            // significa que el usuario ya existe
            showMessage("El usuario ya existe. Intente con otro nombre.", "error");
            return;
        }

        setButtonsDisabled(true);
        showMessage("Registrando nuevo usuario...", "info");

        try {
            // CONCEPTO CLAVE: Guardar nuevas credenciales en archivo TXT
            boolean success = saveUserCredentials(username, password);

            if (success) {
                showMessage("¡Usuario registrado exitosamente! Puede hacer login ahora.", "success");
                passwordField.clear();
                usernameField.requestFocus();
            } else {
                showMessage("Error al registrar usuario. Intente nuevamente.", "error");
            }

        } catch (Exception e) {
            showMessage("Error durante el registro: " + e.getMessage(), "error");
            e.printStackTrace();
        } finally {
            setButtonsDisabled(false);
        }
    }

    /**
     * Muestra mensajes al usuario con diferentes estilos según el tipo
     */
    private void showMessage(String message, String type) {
        messageLabel.setText(message);

        switch (type.toLowerCase()) {
            case "success":
                messageLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                break;
            case "error":
                messageLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                break;
            case "info":
                messageLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: #333333;");
        }
    }

    /**
     * Limpia el mensaje mostrado
     */
    private void clearMessage() {
        messageLabel.setText("");
    }

    /**
     * Habilita/deshabilita los botones
     */
    private void setButtonsDisabled(boolean disabled) {
        loginButton.setDisable(disabled);
        registerButton.setDisable(disabled);
    }

    /**
     * Guarda credenciales de usuario con cifrado SHA-256
     */
    private boolean saveUserCredentials(String username, String password) {
        try {
            String salt = SecurityUtils.generateSalt();
            String hashedPassword = SecurityUtils.hashPassword(password, salt);
            String userRecord = username + ":" + hashedPassword + ":" + salt + "\n";

            fileManager.writeToFile(USERS_FILE, userRecord, true);
            System.out.println("Credenciales cifradas guardadas para usuario: " + username);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar credenciales cifradas: " + e.getMessage());
            return false;
        }
    }

    private void createDefaultUser() {
        //SEEDER con cifrado
        saveUserCredentials("admin", "admin123");
        saveUserCredentials("profesor", "clase2024");
    }

    /**
     * Valida credenciales de usuario contra hashes almacenados
     */
    public boolean validateUserCredentials(String username, String password) {
        try {
            // Verificar si el archivo existe
            if (!fileManager.fileExists(USERS_FILE)) {
                System.out.println("Archivo de usuarios no existe, creando usuario por defecto...");
                createDefaultUser();
                return false;
            }

            List<String> listUsuarios = fileManager.readFromFile(USERS_FILE);
            boolean needsMigration = false;

            for(int i = 0; i < listUsuarios.size(); i++) {
                String line = listUsuarios.get(i);
                if (line.trim().isEmpty()) continue;

                // Migrar formato antiguo si es necesario
                if (SecurityUtils.isPlainTextFormat(line)) {
                    String migratedLine = SecurityUtils.migrateToHashedFormat(line);
                    listUsuarios.set(i, migratedLine);
                    needsMigration = true;
                    line = migratedLine;
                }

                String[] credentials = line.split(":");
                if (credentials.length == 3 && credentials[0].equals(username)) {
                    String storedHash = credentials[1];
                    String salt = credentials[2];

                    boolean isValid = SecurityUtils.verifyPassword(password, storedHash, salt);
                    if (isValid) {
                        System.out.println("Credenciales válidas para: " + username);
                        return true;
                    }
                }
            }

            // Migrar archivo completo si se detectaron formatos antiguos
            if (needsMigration) {
                migrateUserFile(listUsuarios);
            }

        } catch (Exception e) {
            System.out.println("Error validando credenciales: " + e.getMessage());
        }
        return false;
    }

    /**
     * Migra todo el archivo de usuarios a formato cifrado
     */
    private void migrateUserFile(List<String> userLines) {
        try {
            // Crear backup del archivo antiguo
            String backupFile = USERS_FILE + ".backup";
            fileManager.writeToFile(backupFile, "", false);

            for (String line : userLines) {
                if (line.trim().isEmpty()) continue;

                if (SecurityUtils.isPlainTextFormat(line)) {
                    String migratedLine = SecurityUtils.migrateToHashedFormat(line);
                    fileManager.writeToFile(backupFile, migratedLine + "\n", true);
                } else {
                    fileManager.writeToFile(backupFile, line + "\n", true);
                }
            }

            // Reemplazar archivo original
            fileManager.deleteFile(USERS_FILE);
            java.nio.file.Files.move(
                    java.nio.file.Paths.get(backupFile),
                    java.nio.file.Paths.get(USERS_FILE)
            );

            System.out.println("Archivo de usuarios migrado a formato cifrado");
        } catch (Exception e) {
            System.err.println("Error migrando archivo de usuarios: " + e.getMessage());
        }
    }

    /**
     * Maneja la acción de descifrar y mostrar credenciales
     */
    @FXML
    private void handleDecryptCredentials() {
        try {
            if (!fileManager.fileExists(USERS_FILE)) {
                showMessage("No existe archivo de usuarios para analizar", "error");
                return;
            }

            List<String> userLines = fileManager.readFromFile(USERS_FILE);

            // Generar reporte de seguridad
            String report = SecurityUtils.generateSecurityReport(userLines);

            // Mostrar reporte
            securityReportText.setText(report);
            securityReportBox.setVisible(true);

            showMessage("Reporte de seguridad generado correctamente", "success");

        } catch (Exception e) {
            showMessage("Error al generar reporte: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    /**
     * Cierra el panel de reporte de seguridad
     */
    @FXML
    private void closeSecurityReport() {
        securityReportBox.setVisible(false);
    }

    /**
     * Exporta el reporte de seguridad a un archivo de texto
     */
    @FXML
    private void exportSecurityReport() {
        try {
            String report = securityReportText.getText();
            String reportFile = fileManager.DATA_DIRECTORY + "/security_report.txt";

            fileManager.writeToFile(reportFile, report, false);
            showMessage("Reporte exportado a: " + reportFile, "success");

        } catch (Exception e) {
            showMessage("Error al exportar reporte: " + e.getMessage(), "error");
        }
    }

    /**
     * Migra todos los usuarios a formato cifrado
     */
    @FXML
    private void migrateAllUsers() {
        try {
            if (!fileManager.fileExists(USERS_FILE)) {
                showMessage("No existe archivo de usuarios", "error");
                return;
            }

            List<String> userLines = fileManager.readFromFile(USERS_FILE);
            boolean migrated = false;

            for (int i = 0; i < userLines.size(); i++) {
                String line = userLines.get(i);
                if (line.trim().isEmpty()) continue;

                if (SecurityUtils.isPlainTextFormat(line)) {
                    String migratedLine = SecurityUtils.migrateToHashedFormat(line);
                    userLines.set(i, migratedLine);
                    migrated = true;
                }
            }

            if (migrated) {
                fileManager.rewriteFile(USERS_FILE, userLines);
                showMessage("Todos los usuarios migrados a formato cifrado", "success");
            } else {
                showMessage("Todos los usuarios ya están en formato cifrado", "info");
            }

        } catch (Exception e) {
            showMessage("Error en migración: " + e.getMessage(), "error");
        }
    }
}

