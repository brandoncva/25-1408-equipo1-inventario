package com.programacion.inventario.util;

import com.programacion.inventario.controller.AboutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * NavigationManager - Sistema centralizado de navegación para JavaFX
 * 
 * Esta clase implementa el patrón Singleton para gestionar toda la navegación
 * entre pantallas de la aplicación de manera consistente y reutilizable.
 * 
 * Características principales:
 * - Carga de archivos FXML
 * - Gestión de ventanas (stages)
 * - Transiciones suaves entre pantallas
 * - Manejo centralizado de errores
 * - Cache de controladores para reutilización
 */
public class NavigationManager {
    
    // Instancia única (Singleton)
    private static NavigationManager instance;
    
    // Stage principal de la aplicación
    private Stage primaryStage;
    
    // Cache de controladores para evitar recargar
    private Map<String, Object> controllerCache;
    
    // Directorio base de las vistas FXML
    private static final String VIEWS_PATH = "/com/programacion/inventario/view/";
    
    // Enumeración de todas las pantallas disponibles
    public enum Screen {
        LOGIN("login-view.fxml", "Login - Sistema de Inventario"),
        MAIN("main-view.fxml", "Sistema Principal - Inventario"),
        USUARIOS("usuarios-view.fxml", "Gestión de Usuarios"),
        PRODUCTOS("productos-view.fxml", "Gestión de Productos"),
        PROVEEDORES("proveedores-view.fxml", "Gestión de Proveedores"),
        REPORTES("reportes-view.fxml", "Reportes del Sistema"),
        ABOUT("about-view.fxml","Acerca del Sistema");


        private final String fxmlFile;
        private final String title;

        Screen(String fxmlFile, String title) {
            this.fxmlFile = fxmlFile;
            this.title = title;
        }

        public String getFxmlFile() {
            return fxmlFile;
        }

        public String getTitle() {
            return title;}
    };
    
    /**
     * Constructor privado para implementar Singleton
     */
    private NavigationManager() {
        controllerCache = new HashMap<>();
    }
    
    /**
     * Obtiene la instancia única del NavigationManager
     */
    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }
    
    /**
     * Inicializa el NavigationManager con el stage principal
     */
    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        System.out.println("NavigationManager inicializado con stage principal");
    }
    
    /**
     * Navega a una pantalla específica
     * 
     * @param screen La pantalla a la que navegar
     * @param clearCache Si se debe limpiar el cache de controladores
     */
    public void navigateTo(Screen screen, boolean clearCache) {
        if (primaryStage == null) {
            showError("Error de Navegación", "NavigationManager no ha sido inicializado");
            return;
        }
        
        try {
            // Limpiar cache si se solicita
            if (clearCache) {
                controllerCache.clear();
            }
            
            // Cargar la vista FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(VIEWS_PATH + screen.getFxmlFile()));
            
            // Cargar el archivo FXML
            Parent root = loader.load();
            
            // Obtener el controlador
            Object controller = loader.getController();
            
            // Guardar en cache si no existe
            if (!controllerCache.containsKey(screen.name())) {
                controllerCache.put(screen.name(), controller);
            }
            
            // Configurar la nueva escena
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(screen.getTitle());
            
            // Mostrar la ventana
            primaryStage.show();
            
            System.out.println("Navegación exitosa a: " + screen.name());
            
        } catch (IOException e) {
            String errorMsg = "Error al cargar la pantalla: " + screen.name() + "\n" + e.getMessage();
            System.err.println(errorMsg);
            showError("Error de Carga", errorMsg);
        }
    }
    
    /**
     * Navega a una pantalla específica (mantiene cache por defecto)
     */
    public void navigateTo(Screen screen) {
        navigateTo(screen, false);
    }
    
    /**
     * Navega a una pantalla específica con parámetros
     * 
     * @param screen La pantalla a la que navegar
     * @param parameters Parámetros para pasar al controlador
     */
    public void navigateTo(Screen screen, Map<String, Object> parameters) {
        navigateTo(screen, false);
        
        // Pasar parámetros al controlador si implementa la interfaz
        Object controller = controllerCache.get(screen.name());
        if (controller instanceof ParameterReceiver) {
            ((ParameterReceiver) controller).receiveParameters(parameters);
        }
    }
    
    /**
     * Abre una nueva ventana modal
     * 
     * @param screen La pantalla a mostrar
     * @param title Título de la ventana modal
     * @param ownerStage Stage padre (opcional)
     */
    public void openModal(Screen screen, String title, Stage ownerStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(VIEWS_PATH + screen.getFxmlFile()));
            
            Parent root = loader.load();
            
            // Crear nueva ventana modal
            Stage modalStage = new Stage();
            modalStage.initStyle(StageStyle.UTILITY);
            modalStage.initOwner(ownerStage != null ? ownerStage : primaryStage);
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root));
            
            // Configurar como modal
            modalStage.setResizable(false);
            modalStage.centerOnScreen();
            
            modalStage.showAndWait();
            
        } catch (IOException e) {
            String errorMsg = "Error al abrir modal: " + screen.name() + "\n" + e.getMessage();
            System.err.println(errorMsg);
            showError("Error de Modal", errorMsg);
        }
    }
    
    /**
     * Obtiene un controlador del cache
     */
    public Object getController(Screen screen) {
        return controllerCache.get(screen.name());
    }
    
    /**
     * Limpia el cache de controladores
     */
    public void clearCache() {
        controllerCache.clear();
        System.out.println("Cache de controladores limpiado");
    }
    
    /**
     * Cierra la aplicación
     */
    public void exitApplication() {
        if (primaryStage != null) {
            primaryStage.close();
        }
        System.exit(0);
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
     * Interfaz para controladores que reciben parámetros
     */
    public interface ParameterReceiver {
        void receiveParameters(Map<String, Object> parameters);
    }
}
