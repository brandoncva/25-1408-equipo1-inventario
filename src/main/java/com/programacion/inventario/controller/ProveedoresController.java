package com.programacion.inventario.controller;

import com.programacion.inventario.model.Proveedor;
import com.programacion.inventario.util.FileManager;
import com.programacion.inventario.util.NavigationManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ProveedoresController implements Initializable {

    @FXML private TextField idField;
    @FXML private TextField nombreField;
    @FXML private TextField contactoField;
    @FXML private TextField telefonoField;
    @FXML private TextField emailField;
    @FXML private TextField direccionField;
    @FXML private Button agregarButton;
    @FXML private Button limpiarButton;
    @FXML private Button volverButton;

    @FXML private TableView<Proveedor> proveedoresTable;
    @FXML private TableColumn<Proveedor, String> idColumn;
    @FXML private TableColumn<Proveedor, String> nombreColumn;
    @FXML private TableColumn<Proveedor, String> contactoColumn;
    @FXML private TableColumn<Proveedor, String> telefonoColumn;

    private FileManager fileManager;
    private String PROVEEDORES_FILE;
    private ObservableList<Proveedor> proveedoresList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileManager = new FileManager();
        this.PROVEEDORES_FILE = fileManager.DATA_DIRECTORY + "/proveedores.txt";

        proveedoresList = FXCollections.observableArrayList();
        setupTable();
        cargarProveedores();

        System.out.println("ProveedoresController inicializado");
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        contactoColumn.setCellValueFactory(new PropertyValueFactory<>("contacto"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        proveedoresTable.setItems(proveedoresList);
    }

    @FXML
    private void agregarProveedor() {
        String id = idField.getText().trim();
        String nombre = nombreField.getText().trim();
        String contacto = contactoField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String email = emailField.getText().trim();
        String direccion = direccionField.getText().trim();

        if (id.isEmpty() || nombre.isEmpty()) {
            showAlert("Error", "ID y Nombre son obligatorios", Alert.AlertType.ERROR);
            return;
        }

        Proveedor proveedor = new Proveedor(id, nombre, contacto, telefono, email, direccion);

        if (guardarProveedor(proveedor)) {
            proveedoresList.add(proveedor);
            limpiarCampos();
            showAlert("Éxito", "Proveedor agregado correctamente", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Error al guardar el proveedor", Alert.AlertType.ERROR);
        }
    }

    private boolean guardarProveedor(Proveedor proveedor) {
        try {
            String registro = proveedor.getId() + "|" +
                    proveedor.getNombre() + "|" +
                    proveedor.getContacto() + "|" +
                    proveedor.getTelefono() + "|" +
                    proveedor.getEmail() + "|" +
                    proveedor.getDireccion() + "\n";

            fileManager.writeToFile(PROVEEDORES_FILE, registro, true);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar proveedor: " + e.getMessage());
            return false;
        }
    }

    private void cargarProveedores() {
        try {
            if (!fileManager.fileExists(PROVEEDORES_FILE)) {
                return;
            }

            proveedoresList.clear();
            var lineas = fileManager.readFromFile(PROVEEDORES_FILE);

            for (String linea : lineas) {
                String[] datos = linea.split("\\|");
                if (datos.length >= 6) {
                    Proveedor prov = new Proveedor(
                            datos[0], datos[1], datos[2],
                            datos[3], datos[4], datos[5]
                    );
                    proveedoresList.add(prov);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar proveedores: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarCampos() {
        idField.clear();
        nombreField.clear();
        contactoField.clear();
        telefonoField.clear();
        emailField.clear();
        direccionField.clear();
        idField.requestFocus();
    }

    @FXML
    private void volverAlMenu() {
        NavigationManager.getInstance().navigateTo(NavigationManager.Screen.MAIN);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}