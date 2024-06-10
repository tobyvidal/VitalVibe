package com.example.proyectofinal20.Rutina;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectofinal20.BD.BD;
import com.example.proyectofinal20.Home.Principal;
import com.example.proyectofinal20.Perfil.Perfil;
import com.example.proyectofinal20.Objetos.Ejercicio;
import com.example.proyectofinal20.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ViewEjercicio extends AppCompatActivity {
    private List<Ejercicio> listaEjerciciosData;
    private ArrayAdapter<Ejercicio> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewejercicios);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        // Obtener información del Intent
        Intent intent = getIntent();
        String nombreUsuario = intent.getStringExtra("username");
        String nombreRutina = intent.getStringExtra("nombreRutina");

        // Inicializa la base de datos y los componentes de la interfaz
        BD bd = new BD(this);
        int IdUsuario = bd.obtenerIdUsuarioPorNombre(nombreUsuario);
        int idRutina = bd.obtenerIdRutina(nombreRutina, nombreUsuario);
        ListView listaEjercicios = findViewById(R.id.listaEjercicios);
        EditText nombreEjercicioEditText = findViewById(R.id.nombreEjercicio);
        EditText categoriaEjercicioEditText = findViewById(R.id.categoriaEjercicio);
        Button botonAgregarEjercicio = findViewById(R.id.anyadirEjercicio);
        Button botonFiltrarEjercicios = findViewById(R.id.filtrar);

        // Obtener la lista de ejercicios
        listaEjerciciosData = bd.consultarEjercicios(nombreUsuario);

        // Configurar el ArrayAdapter para el ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaEjerciciosData);
        listaEjercicios.setAdapter(adapter);

        // Configurar el OnItemClickListener para manejar clics en los elementos de la lista
        listaEjercicios.setOnItemClickListener((parent, view, position, id) -> {
            Ejercicio ejercicioSeleccionado = listaEjerciciosData.get(position);

            // Verificar si el ejercicio ya existe en la rutina
            if (bd.existeEjercicioEnRutina(IdUsuario, idRutina, ejercicioSeleccionado.getNombre())) {
                // Si el ejercicio ya existe en la rutina, mostrar un Toast
                Toast.makeText(this, "Este ejercicio ya existe en la rutina.", Toast.LENGTH_SHORT).show();
            } else {
                // Si el ejercicio no existe en la rutina, continuar con el programa

                // Crear un Intent para iniciar GrabarEjercicio
                Intent intentGrabarEjercicio = new Intent(ViewEjercicio.this, GrabarEjercicio.class);

                // Pasar los datos del ejercicio seleccionado como extras en el Intent
                intentGrabarEjercicio.putExtra("username", nombreUsuario);
                intentGrabarEjercicio.putExtra("nombreRutina", nombreRutina);
                intentGrabarEjercicio.putExtra("nombreEjercicio", ejercicioSeleccionado.getNombre());
                intentGrabarEjercicio.putExtra("categoria", ejercicioSeleccionado.getCategoria());

                // Iniciar la actividad GrabarEjercicio
                startActivity(intentGrabarEjercicio);
            }
        });

        // Configurar el botón de agregar ejercicio
        botonAgregarEjercicio.setOnClickListener(v -> {
            String nombre = nombreEjercicioEditText.getText().toString();
            String categoria = categoriaEjercicioEditText.getText().toString();
            if (nombre.isEmpty() || categoria.isEmpty()) {
                Toast.makeText(ViewEjercicio.this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Crear un objeto Ejercicio y añadirlo a la base de datos
                Ejercicio ejercicio = new Ejercicio(nombre, categoria);
                bd.insertEjercicio(ejercicio, nombreUsuario);

                // Actualizar la lista de ejercicios
                listaEjerciciosData.clear();
                listaEjerciciosData.addAll(bd.consultarEjercicios(nombreUsuario));
                adapter.notifyDataSetChanged();
                Toast.makeText(ViewEjercicio.this, "Se ha añadido el ejercicio " + nombre + ".", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el botón de filtrar ejercicios
        botonFiltrarEjercicios.setOnClickListener(v -> {
            String categoria = categoriaEjercicioEditText.getText().toString();
            if (categoria.isEmpty()) {
                listaEjerciciosData.clear();
                listaEjerciciosData.addAll(bd.consultarEjercicios(nombreUsuario));
                adapter.notifyDataSetChanged();
                return;
            }

            // Realiza la consulta de ejercicios por categoría y usuario específico
            List<Ejercicio> ejerciciosFiltrados = bd.consultarEjerciciosPorCategoria(nombreUsuario, categoria);

            // Verifica los resultados de la consulta
            if (ejerciciosFiltrados == null || ejerciciosFiltrados.isEmpty()) {
                Toast.makeText(ViewEjercicio.this, "No se encontraron ejercicios en la categoría especificada.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualiza la lista de ejercicios
            listaEjerciciosData.clear();
            listaEjerciciosData.addAll(ejerciciosFiltrados);

            // Notifica al adaptador para que actualice la interfaz de usuario
            adapter.notifyDataSetChanged();
        });
    }
}
