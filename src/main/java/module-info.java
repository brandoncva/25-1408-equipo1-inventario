module com.programacion.inventario {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.programacion.inventario.controller;
    opens com.programacion.inventario.controller to javafx.fxml;
    exports com.programacion.inventario;
}