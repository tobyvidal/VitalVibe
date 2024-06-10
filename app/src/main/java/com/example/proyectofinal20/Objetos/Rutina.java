package com.example.proyectofinal20.Objetos;

public class Rutina {
    // Atributos públicos
    public String nombre;
    public Ejercicio ejercicio;

    // Constructor con todos los atributos
    public Rutina(String nombre, Ejercicio ejercicio) {
        this.nombre = nombre;
        this.ejercicio = ejercicio;
    }

    // Constructor solo con el nombre
    public Rutina(String nombre) {
        this.nombre = nombre;
        this.ejercicio = null; // Inicializa el atributo ejercicio con null si no se proporciona
    }

    // Getters y setters para nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Getters y setters para ejercicio
    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }

    // Método toString para representar el objeto Rutina como una cadena
    @Override
    public String toString() {
        return "Rutina: nombre='" + nombre + '\'' +
                ", ejercicio=" + ejercicio;
    }
}
