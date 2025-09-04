package com.programacion.inventario.controller;

import com.programacion.inventario.util.FileManager;
import com.programacion.inventario.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        System.out.println("LoginController inicializado - Sistema de almacenamiento TXT listo");
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

    private boolean saveUserCredentials(String username, String password) {
        fileManager.writeToFile(USERS_FILE, username + ":" + password + "\n", true);
        System.out.println("Credenciales guardadas para usuario: " + username);
        return true;
    }

    private void createDefaultUser() {
        //SEEDER
        saveUserCredentials("admin", "admin123");
        saveUserCredentials("profesor", "clase2024");
    }

    public boolean validateUserCredentials(String username, String password) {
        try {
            // Verificar si el archivo existe
            if (!fileManager.fileExists(USERS_FILE)) {
                System.out.println("Archivo de usuarios no existe, creando usuario por defecto...");
                createDefaultUser();
            }

            List<String> listUsuarios = fileManager.readFromFile(USERS_FILE);

            for(String line : listUsuarios) {
                if (line.trim().isEmpty()) continue; // Saltar líneas vacías
                String[] credentials = line.split(":");
                if (credentials.length == 2) {
                    if (credentials[0].equals(username) && credentials[1].equals(password)) {
                        System.out.println("Credenciales válidas para: " + username);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Credenciales inválidas para: " + username);
        }
        return false;
    }
}
