package com.programacion.inventario;

import com.programacion.inventario.util.NavigationManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar el NavigationManager con el stage principal
        NavigationManager navigationManager = NavigationManager.getInstance();
        navigationManager.initialize(stage);
        
        // Cargar la vista de login por defecto
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);
        
        // Configurar la ventana
        stage.setTitle("Login - Sistema de Inventario");
        stage.setResizable(false);
        stage.centerOnScreen();
        
        stage.setScene(scene);
        stage.show();
        
        System.out.println("Aplicación iniciada - Sistema de navegación configurado");
    }

    public static void main(String[] args) {
        launch();
    }
}
