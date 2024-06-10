package com.example.proyectofinal20.Rutina;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.example.proyectofinal20.Objetos.Ejercicio;
import com.example.proyectofinal20.Perfil.Perfil;
import com.example.proyectofinal20.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Ejercicios extends AppCompatActivity {
    private List<Ejercicio> listaEjerciciosData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desplegableejercicios);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        Intent intentNombre = getIntent();
        String nombreuser = intentNombre.getStringExtra("username");
        // Inicializa el ListView
        ListView listaEjercicios = findViewById(R.id.listaEjercicios);
        EditText nombreEjercicio = findViewById(R.id.nombreEjercicio);
        EditText categoriaEjercicio = findViewById(R.id.categoriaEjercicio);
        Button botonAgregarEjercicio = findViewById(R.id.anyadirEjercicio);
        Button botonFiltrarEjercicios = findViewById(R.id.filtrar);
        // Inicializa la base de datos
        BD bd = new BD(this);

        // Ruta al archivo JSON que contiene los ejercicios
        File archivoJson = new File(getFilesDir(), "ejercicios.json");

        // Verifica si el archivo JSON existe
        if (!archivoJson.exists()) {
            // Si el archivo no existe, crea el archivo y llena con datos
            try (FileWriter writer = new FileWriter(archivoJson)) {
                // Ejemplo de archivo JSON con datos de ejercicios
                String jsonData = "{ \"ejercicios\": [" +
                        "{ \"nombreEjercicio\": \"Press de banca\", \"categoria\": \"Pecho\" }," +
                        "{ \"nombreEjercicio\": \"Flexiones\", \"categoria\": \"Pecho\" }," +
                        "{ \"nombreEjercicio\": \"Aperturas con mancuernas\", \"categoria\": \"Pecho\" }," +
                        "{ \"nombreEjercicio\": \"Curl de bíceps con barra\", \"categoria\": \"Bíceps\" }," +
                        "{ \"nombreEjercicio\": \"Curl de martillo con mancuernas\", \"categoria\": \"Bíceps\" }," +
                        "{ \"nombreEjercicio\": \"Extensión de tríceps con barra\", \"categoria\": \"Tríceps\" }," +
                        "{ \"nombreEjercicio\": \"Fondos de tríceps\", \"categoria\": \"Tríceps\" }," +
                        "{ \"nombreEjercicio\": \"Dominadas\", \"categoria\": \"Espalda\" }," +
                        "{ \"nombreEjercicio\": \"Remo con barra\", \"categoria\": \"Espalda\" }," +
                        "{ \"nombreEjercicio\": \"Pulldown con cable\", \"categoria\": \"Espalda\" }," +
                        "{ \"nombreEjercicio\": \"Press de hombros con mancuernas\", \"categoria\": \"Hombros\" }," +
                        "{ \"nombreEjercicio\": \"Elevaciones laterales\", \"categoria\": \"Hombros\" }," +
                        "{ \"nombreEjercicio\": \"Remo al mentón\", \"categoria\": \"Hombros\" }," +
                        "{ \"nombreEjercicio\": \"Abdominales crunch\", \"categoria\": \"Abdomen\" }," +
                        "{ \"nombreEjercicio\": \"Plancha\", \"categoria\": \"Abdomen\" }," +
                        "{ \"nombreEjercicio\": \"Sentadilla\", \"categoria\": \"Piernas\" }," +
                        "{ \"nombreEjercicio\": \"Prensa de pierna\", \"categoria\": \"Piernas\" }" +
                        "]" +
                        "}";

                // Escribe los datos JSON en el archivo
                writer.write(jsonData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Una vez que el archivo ha sido creado, inserta los datos en la base de datos
            insertarEjerciciosEnBaseDeDatos(bd, archivoJson, nombreuser);
        }

        // Obtiene la lista de ejercicios de la base de datos
        listaEjerciciosData = bd.consultarEjercicios(nombreuser);

        // Crea un ArrayAdapter para manejar la lista de ejercicios
        ArrayAdapter<Ejercicio> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaEjerciciosData);

        // Asigna el adaptador al ListView
        listaEjercicios.setAdapter(adapter);

        /*Menu de navegación*/
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent = getIntent();
                String username = intent.getStringExtra("username");

                if (id == R.id.navigation_home) {
                    Intent intentPrincipal = new Intent(Ejercicios.this, Principal.class);
                    intentPrincipal.putExtra("username", username);
                    startActivity(intentPrincipal);
                    finish();
                    return true;
                } else if (id == R.id.navigation_routines) {
                    Intent intentRutinas = new Intent(Ejercicios.this, Rutinas.class);
                    intentRutinas.putExtra("username", username);
                    startActivity(intentRutinas);
                    finish();
                    return true;
                } else if (id == R.id.navigation_profile) {
                    Intent intentPerfil = new Intent(Ejercicios.this, Perfil.class);
                    intentPerfil.putExtra("username", username);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                }
                return false;
            }
        });

// Agrega este código para establecer el elemento seleccionado
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);


        botonAgregarEjercicio.setOnClickListener(v -> {
            String nombre = nombreEjercicio.getText().toString();
            String categoria = categoriaEjercicio.getText().toString();
            if(nombre.isEmpty() || categoria.isEmpty()){
                Toast.makeText(Ejercicios.this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
            }
            else{
                // Crea un objeto Ejercicio y añádelo a la base de datos
                Ejercicio ejercicio = new Ejercicio(nombre, categoria);
                bd.insertEjercicio(ejercicio, nombreuser);

                // Actualiza la lista de ejercicios
                listaEjerciciosData.clear();
                listaEjerciciosData.addAll(bd.consultarEjercicios(nombreuser));
                adapter.notifyDataSetChanged();
                Toast.makeText(Ejercicios.this, "Se ha añadido el ejercicio " + nombreEjercicio + ".", Toast.LENGTH_SHORT).show();
            }
        });

        botonFiltrarEjercicios.setOnClickListener(v -> {
            String categoria = categoriaEjercicio.getText().toString();
            if (categoria.isEmpty()) {
                listaEjerciciosData.clear();
                listaEjerciciosData.addAll(bd.consultarEjercicios(nombreuser));
                adapter.notifyDataSetChanged();
                return;
            }

            // Realiza la consulta de ejercicios por categoría y usuario específico
            List<Ejercicio> ejerciciosFiltrados = bd.consultarEjerciciosPorCategoria(nombreuser, categoria);

            // Verifica los resultados de la consulta
            if (ejerciciosFiltrados == null || ejerciciosFiltrados.isEmpty()) {
                Toast.makeText(Ejercicios.this, "No se encontraron ejercicios en la categoría especificada.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualiza la lista de ejercicios
            listaEjerciciosData.clear();
            listaEjerciciosData.addAll(ejerciciosFiltrados);

            // Notifica al adaptador para que actualice la interfaz de usuario
            adapter.notifyDataSetChanged();
        });

    }

    /**
     * Inserta los ejercicios desde el archivo JSON en la base de datos.
     *
     * @param bd        La instancia de base de datos.
     * @param archivoJson El archivo JSON con los datos de los ejercicios.
     */

    private void insertarEjerciciosEnBaseDeDatos(BD bd, File archivoJson, String nombreuser) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoJson))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            JSONArray ejerciciosArray = jsonObject.getJSONArray("ejercicios");

            for (int i = 0; i < ejerciciosArray.length(); i++) {
                JSONObject ejercicioObject = ejerciciosArray.getJSONObject(i);
                String nombreEjercicio = ejercicioObject.getString("nombreEjercicio");
                String categoria = ejercicioObject.getString("categoria");

                // Crea un objeto Ejercicio y añádelo a la base de datos
                Ejercicio ejercicio = new Ejercicio(nombreEjercicio, categoria);
                bd.insertEjercicio(ejercicio, nombreuser);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
