package com.programacion.inventario.controller;

import com.programacion.inventario.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * UsuariosController - Controlador para la gestión de usuarios
 * 
 * Este controlador maneja la lógica de la pantalla de gestión de usuarios,
 * demostrando cómo se puede extender el sistema con nuevas funcionalidades.
 */
public class UsuariosController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TableView<Object> usuariosTable;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupRoleComboBox();
        System.out.println("UsuariosController inicializado");
    }
    
    /**
     * Configura el ComboBox de roles
     */
    private void setupRoleComboBox() {
        roleComboBox.getItems().addAll("Usuario", "Administrador", "Profesor", "Estudiante");
        roleComboBox.setValue("Usuario");
    }
    
    /**
     * Maneja la acción de agregar usuario
     */
    @FXML
    private void agregarUsuario() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();
        
        // Validaciones básicas
        if (username.isEmpty()) {
            showAlert("Error", "El nombre de usuario es obligatorio", Alert.AlertType.ERROR);
            return;
        }
        
        if (password.isEmpty()) {
            showAlert("Error", "La contraseña es obligatoria", Alert.AlertType.ERROR);
            return;
        }
        
        if (role == null) {
            showAlert("Error", "Debe seleccionar un rol", Alert.AlertType.ERROR);
            return;
        }
        
        // TODO: Implementar lógica real de guardado
        // Por ahora, solo mostrar mensaje de éxito
        showAlert("Éxito", "Usuario agregado correctamente: " + username, Alert.AlertType.INFORMATION);
        
        // Limpiar campos
        limpiarCampos();
    }
    
    /**
     * Maneja el retorno al menú principal
     */
    @FXML
    private void volverAlMenu() {
        try {
            NavigationManager navigationManager = NavigationManager.getInstance();
            navigationManager.navigateTo(NavigationManager.Screen.MAIN);
        } catch (Exception e) {
            System.err.println("Error al volver al menú: " + e.getMessage());
            showAlert("Error", "No se pudo volver al menú principal", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Limpia los campos del formulario
     */
    private void limpiarCampos() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue("Usuario");
        usernameField.requestFocus();
    }
    
    /**
     * Muestra una alerta al usuario
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
