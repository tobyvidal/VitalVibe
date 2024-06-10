package com.example.proyectofinal20.Objetos;

public class Ejercicio {
    // Atributos públicos
    public String nombre;
    public String categoria;
    public int series;
    public int repeticiones;

    // Constructor con todos los atributos
    public Ejercicio(String nombre, String categoria, int series, int repeticiones) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.series = series;
        this.repeticiones = repeticiones;
    }

    // Constructor solo con nombre y categoría
    public Ejercicio(String nombre, String categoria) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.series = 0;
        this.repeticiones = 0;
    }

    // Getters y setters para nombre
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Getters y setters para categoría
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // Getters y setters para series
    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    // Getters y setters para repeticiones
    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    // Método toString para representar el objeto Ejercicio como una cadena

    @Override
    public String toString() {
        return "Ejercicio:" + nombre + ", Categoría:" + categoria + ".";
    }
}
