package com.programacion.inventario.controller;

import com.programacion.inventario.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * AboutController - Controlador para la pantalla "Acerca de"
 * Muestra información sobre la aplicación, versión y desarrolladores
 */
public class AboutController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("AboutController inicializado");
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