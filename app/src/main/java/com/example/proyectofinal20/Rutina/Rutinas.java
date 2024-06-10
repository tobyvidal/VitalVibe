package com.example.proyectofinal20.Rutina;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectofinal20.BD.BD;
import com.example.proyectofinal20.Home.Principal;
import com.example.proyectofinal20.Perfil.Perfil;
import com.example.proyectofinal20.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class Rutinas extends AppCompatActivity {
    private BD bd;
    private ListView listView;
    private ArrayAdapter<String> adapter; // Nuevo campo para el adaptador

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rutinas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        // Inicializa la base de datos y los componentes de la interfaz de usuario
        bd = new BD(this);
        listView = findViewById(R.id.listView);
        Button buttonNuevaRutina = findViewById(R.id.buttonNuevaRutina);
        EditText editText = findViewById(R.id.nombreRutina);
        Intent intent = getIntent();
        String nombreUsuario = intent.getStringExtra("username");

        // Obtener la lista de rutinas para el usuario
        List<String> rutinas = bd.consultarNombresRutinas(nombreUsuario);

        // Configurar el ArrayAdapter para el ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rutinas);
        listView.setAdapter(adapter);

        // Configura el OnItemClickListener para manejar clics en los elementos de la lista
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String rutinaSeleccionada = rutinas.get(position);
            // Crear un Intent para iniciar VistaRutina y pasar el ID de la rutina
            Intent intentVistaRutina = new Intent(Rutinas.this, VistaRutina.class);
            intentVistaRutina.putExtra("username", nombreUsuario);
            intentVistaRutina.putExtra("NombreRutina", rutinaSeleccionada);
            // Iniciar la actividad VistaRutina
            startActivity(intentVistaRutina);
        });

        // Configuración de BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                Intent intentHome = new Intent(Rutinas.this, Principal.class);
                intentHome.putExtra("username", nombreUsuario);
                startActivity(intentHome);
                finish();
                return true;
            } else if (itemId == R.id.navigation_routines) {
                // No necesitas hacer nada aquí ya que ya estás en la actividad de rutinas
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intentPerfil = new Intent(Rutinas.this, Perfil.class);
                intentPerfil.putExtra("username", nombreUsuario);
                startActivity(intentPerfil);
                finish();
                return true;
            }
            return false;
        });

// Agrega este código para establecer el elemento seleccionado
        bottomNavigationView.setSelectedItemId(R.id.navigation_routines);


        // Configura el botón para crear nuevas rutinas
        buttonNuevaRutina.setOnClickListener(v -> {
            String nombreRutina = editText.getText().toString();

            if (!nombreRutina.isEmpty()) {
                // Verificar si la rutina ya existe
                int idRutina = bd.obtenerIdRutina(nombreRutina, nombreUsuario);

                if (idRutina != -1) {
                    Toast.makeText(Rutinas.this, "Ya existe una rutina con ese nombre", Toast.LENGTH_SHORT).show();
                } else {
                    // Crear la nueva rutina en la base de datos
                    bd.insertRutina(nombreRutina, nombreUsuario);
                    Toast.makeText(Rutinas.this, "Rutina creada: " + nombreRutina, Toast.LENGTH_SHORT).show();
                    // Actualizar la lista de rutinas después de crear una nueva rutina
                    actualizarListaRutinas(nombreUsuario);
                }
            } else {
                Toast.makeText(Rutinas.this, "Por favor, ingresa un nombre para la rutina", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para actualizar la lista de rutinas después de crear una nueva rutina
    private void actualizarListaRutinas(String nombreUsuario) {
        List<String> rutinas = bd.consultarNombresRutinas(nombreUsuario);
        if (rutinas != null) {
            // Actualiza los datos del adaptador y notifica que los datos han cambiado
            adapter.clear();
            adapter.addAll(rutinas);
            adapter.notifyDataSetChanged();
        }
    }
}