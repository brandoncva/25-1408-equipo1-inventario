// Archivo de configuración del módulo Java para una aplicación de inventario
// que utiliza JavaFX. Define las dependencias del módulo y los paquetes que se exportan

module com.programacion.inventario {
    //IMPORT
    requires javafx.controls;
    requires javafx.fxml;
    requires java.compiler;

    // EXPORT
    exports com.programacion.inventario.controller;
    opens com.programacion.inventario.controller to javafx.fxml;

    exports com.programacion.inventario.model;
    opens com.programacion.inventario.model to javafx.fxml;

    exports com.programacion.inventario.util;
    opens com.programacion.inventario.util to javafx.fxml;

    exports com.programacion.inventario;
}