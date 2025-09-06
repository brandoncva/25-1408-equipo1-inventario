package com.programacion.inventario.controller;

import com.programacion.inventario.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * MainController - Controlador de la pantalla principal
 * 
 * Este controlador maneja toda la lógica de la pantalla principal del sistema,
 * incluyendo la navegación entre diferentes módulos y la gestión del dashboard.
 * 
 * Características principales:
 * - Navegación centralizada usando NavigationManager
 * - Gestión del estado de la interfaz
 * - Carga de estadísticas del sistema
 * - Manejo de sesión del usuario
 */
public class MainController implements Initializable, NavigationManager.ParameterReceiver {
    
    // Referencias a elementos de la interfaz
    @FXML private Label userInfoLabel;
    @FXML private VBox dashboardContent;
    @FXML private VBox dynamicContent;
    @FXML private Label contentTitleLabel;
    @FXML private Label contentDescriptionLabel;
    @FXML private Label userCountLabel;
    @FXML private Label productCountLabel;
    
    // NavigationManager para manejar la navegación
    private NavigationManager navigationManager;
    
    // Información del usuario actual
    private String currentUsername;
    private String currentUserRole;
    
    // Estado de la navegación actual
    private NavigationManager.Screen currentScreen;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar NavigationManager
        navigationManager = NavigationManager.getInstance();
        
        // Configurar estado inicial
        setupInitialState();
        
        // Configurar eventos
        setupEventHandlers();
        
        System.out.println("MainController inicializado - Sistema de navegación listo");
    }
    
    /**
     * Configura el estado inicial del controlador
     */
    private void setupInitialState() {
        // Mostrar dashboard por defecto
        showDashboard();
        
        // Cargar estadísticas iniciales
        loadSystemStatistics();
    }
    
    /**
     * Configura los manejadores de eventos
     */
    private void setupEventHandlers() {
        // Aquí se pueden agregar más eventos si es necesario
    }
    
    /**
     * Recibe parámetros de navegación (implementa ParameterReceiver)
     */
    @Override
    public void receiveParameters(Map<String, Object> parameters) {
        if (parameters != null) {
            // Extraer información del usuario si está disponible
            if (parameters.containsKey("username")) {
                currentUsername = (String) parameters.get("username");
                updateUserInfo();
            }
            
            if (parameters.containsKey("role")) {
                currentUserRole = (String) parameters.get("role");
                updateUserInfo();
            }
            
            // Mostrar pantalla específica si se solicita
            if (parameters.containsKey("screen")) {
                String screenName = (String) parameters.get("screen");
                navigateToScreenByName(screenName);
            }
        }
    }
    
    /**
     * Actualiza la información del usuario en la interfaz
     */
    private void updateUserInfo() {
        if (currentUsername != null) {
            String displayText = "Usuario: " + currentUsername;
            if (currentUserRole != null) {
                displayText += " (" + currentUserRole + ")";
            }
            userInfoLabel.setText(displayText);
        }
    }
    
    /**
     * Carga las estadísticas del sistema
     */
    private void loadSystemStatistics() {
        // TODO: Implementar carga real de estadísticas desde archivos
        // Por ahora, valores de ejemplo
        userCountLabel.setText("5");
        productCountLabel.setText("0");
    }
    
    // ===== MÉTODOS DE NAVEGACIÓN =====
    
    /**
     * Navega al dashboard principal
     */
    @FXML
    private void navigateToDashboard() {
        showDashboard();
        currentScreen = NavigationManager.Screen.MAIN;
        System.out.println("Navegando al Dashboard");
    }
    
    /**
     * Navega a la gestión de usuarios
     */
    @FXML
    private void navigateToUsuarios() {
        showDynamicContent("Gestión de Usuarios", 
                          "Aquí puedes administrar todos los usuarios del sistema, crear nuevos usuarios, " +
                          "modificar permisos y gestionar roles de acceso.");
        currentScreen = NavigationManager.Screen.USUARIOS;
        System.out.println("Navegando a Gestión de Usuarios");
    }
    
    /**
     * Navega a la gestión de productos
     */
    @FXML
    private void navigateToProductos() {
        showDynamicContent("Gestión de Productos", 
                          "Administra el inventario de productos, agrega nuevos items, " +
                          "actualiza stock y gestiona categorías de productos.");
        currentScreen = NavigationManager.Screen.PRODUCTOS;
        System.out.println("Navegando a Gestión de Productos");
    }
    
    /**
     * Navega a los reportes
     */
    @FXML
    private void navigateToReportes() {
        showDynamicContent("Reportes del Sistema", 
                          "Genera y visualiza reportes sobre usuarios, productos, " +
                          "actividad del sistema y estadísticas generales.");
        currentScreen = NavigationManager.Screen.REPORTES;
        System.out.println("Navegando a Reportes");
    }
    
    /**
     * Navega a la configuración
     */
    @FXML
    private void navigateToConfiguracion() {
        showDynamicContent("Configuración del Sistema", 
                          "Configura parámetros del sistema, preferencias de usuario, " +
                          "configuración de archivos y opciones avanzadas.");
        System.out.println("Navegando a Configuración");
    }

    /**
     * Navega a la pantalla "Acerca de"
     */
    @FXML
    private void navigateToAbout() {
        try {
            NavigationManager navigationManager = NavigationManager.getInstance();
            navigationManager.navigateTo(NavigationManager.Screen.ABOUT);
            currentScreen = NavigationManager.Screen.ABOUT;
            System.out.println("Navegando a Acerca de");
        } catch (Exception e) {
            System.err.println("Error al navegar a About: " + e.getMessage());
            showError("Error de Navegación", "No se pudo cargar la pantalla Acerca de.");
        }
    }
    
    /**
     * Maneja el cierre de sesión
     */
    @FXML
    private void handleLogout() {
        // Mostrar confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que quieres cerrar sesión?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                performLogout();
            }
        });
    }
    
    /**
     * Ejecuta el cierre de sesión
     */
    private void performLogout() {
        try {
            // Limpiar datos de sesión
            currentUsername = null;
            currentUserRole = null;
            
            // Limpiar cache de navegación
            navigationManager.clearCache();
            
            // Navegar de vuelta al login
            navigationManager.navigateTo(NavigationManager.Screen.LOGIN, true);
            
            System.out.println("Sesión cerrada exitosamente");
            
        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
            showError("Error de Cierre de Sesión", "No se pudo cerrar la sesión correctamente.");
        }
    }
    
    // ===== MÉTODOS AUXILIARES =====
    
    /**
     * Muestra el dashboard principal
     */
    private void showDashboard() {
        dashboardContent.setVisible(true);
        dynamicContent.setVisible(false);
    }
    
    /**
     * Muestra contenido dinámico con título y descripción
     */
    private void showDynamicContent(String title, String description) {
        dashboardContent.setVisible(false);
        dynamicContent.setVisible(true);
        
        contentTitleLabel.setText(title);
        contentDescriptionLabel.setText(description);
    }
    
    /**
     * Navega a una pantalla específica por nombre
     */
    private void navigateToScreenByName(String screenName) {
        try {
            NavigationManager.Screen screen = NavigationManager.Screen.valueOf(screenName.toUpperCase());
            navigationManager.navigateTo(screen);
        } catch (IllegalArgumentException e) {
            System.err.println("Pantalla no encontrada: " + screenName);
            showError("Error de Navegación", "La pantalla solicitada no existe.");
        }
    }
    
    /**
     * Muestra un mensaje de error
     */
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Obtiene la pantalla actual
     */
    public NavigationManager.Screen getCurrentScreen() {
        return currentScreen;
    }
    
    /**
     * Obtiene el nombre del usuario actual
     */
    public String getCurrentUsername() {
        return currentUsername;
    }
}
