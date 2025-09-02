package com.programacion.inventario.model;

public class Usuario {
    private String nombre;
    private String password;
    private String rol;

    // Constructor vacío (necesario para Gson)
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String nombre, String password, String rol) {
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}
