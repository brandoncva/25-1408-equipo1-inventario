package com.programacion.inventario.controller;

import com.programacion.inventario.util.FileManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label messageLabel;

    private FileManager fileManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar los managers
        fileManager = new FileManager();

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
            boolean isValid = fileManager.validateUserCredentials(username, password);

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
                        // ::TODO:Navegar a la ventana principal
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Login Exitoso");
                        alert.setHeaderText(null);
                        alert.setContentText("¡Bienvenido, " + username + "!");
                        alert.showAndWait();
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
        if (fileManager.validateUserCredentials(username, "dummy")) {
            // Si las credenciales son válidas con cualquier contraseña dummy,
            // significa que el usuario ya existe
            showMessage("El usuario ya existe. Intente con otro nombre.", "error");
            return;
        }

        setButtonsDisabled(true);
        showMessage("Registrando nuevo usuario...", "info");

        try {
            // CONCEPTO CLAVE: Guardar nuevas credenciales en archivo TXT
            boolean success = fileManager.saveUserCredentials(username, password);

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

}
