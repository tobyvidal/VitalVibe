package com.example.proyectofinal20.Rutina;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectofinal20.BD.BD;
import com.example.proyectofinal20.Objetos.EjercicioEnRutina;
import com.example.proyectofinal20.R;

public class EditarEjercicio extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextCategoria;
    private EditText editTextSeries;
    private EditText editTextRepeticiones;
    private Button btnActualizar;

    private Button btnEliminar;

    private BD bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editarejercicio);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        // Inicializa la base de datos
        bd = new BD(this);

        // Inicializa los campos de la actividad
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextCategoria = findViewById(R.id.editTextCategoria);
        editTextSeries = findViewById(R.id.editTextSeries);
        editTextRepeticiones = findViewById(R.id.editTextRepeticiones);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);

        // Obtener los datos del Intent
        Intent intent = getIntent();
        int idUsuario = intent.getIntExtra("idUsuario", -1);
        int idRutina = intent.getIntExtra("idRutina", -1);
        String nombreEjercicio = intent.getStringExtra("nombreEjercicio");
        String categoria = intent.getStringExtra("categoria");
        int series = intent.getIntExtra("series", 0);
        int repeticiones = intent.getIntExtra("repeticiones", 0);

        String nombreUsuario = bd.obtenerNombreUsuarioPorId(idUsuario);
        String nombreRutina = bd.obtenerNombreRutina(idUsuario, idRutina);

        // Setear los campos con los datos obtenidos del Intent
        editTextNombre.setText(nombreEjercicio);
        editTextCategoria.setText(categoria);
        editTextNombre.setEnabled(false); // Desactiva la edición del nombre
        editTextCategoria.setEnabled(false); // Desactiva la edición de la categoría
        editTextSeries.setText(String.valueOf(series));
        editTextRepeticiones.setText(String.valueOf(repeticiones));

        // Configura el botón de actualizar
        btnActualizar.setOnClickListener(v -> {
            // Obtén los valores actualizados de los campos de texto
            int nuevasSeries = Integer.parseInt(editTextSeries.getText().toString());
            int nuevasRepeticiones = Integer.parseInt(editTextRepeticiones.getText().toString());

            // Crea un objeto EjercicioEnRutina con los nuevos valores
            EjercicioEnRutina ejercicioActualizado = new EjercicioEnRutina(nombreEjercicio, categoria, nuevasSeries, nuevasRepeticiones);

            // Actualiza el ejercicio en la base de datos
            bd.actualizarEjercicioEnRutina(idUsuario, idRutina, ejercicioActualizado);

            // Notifica al usuario que la actualización fue exitosa
            Toast.makeText(this, "Ejercicio actualizado correctamente.", Toast.LENGTH_SHORT).show();

            Intent intent1 = new Intent(EditarEjercicio.this, VistaRutina.class);
            intent1.putExtra("username", nombreUsuario);
            intent1.putExtra("NombreRutina", nombreRutina);
            startActivity(intent1);
            finish();
        });

        btnEliminar.setOnClickListener(v -> confirmarEliminarEjercicio(idUsuario, idRutina, nombreUsuario, nombreRutina, nombreEjercicio));
    }

    private void confirmarEliminarEjercicio(int idUsuario, int idRutina, String nombreUsuario, String nombreRutina, String nombreEjercicio) {
        // Crear un nuevo AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Configurar el mensaje y los botones del AlertDialog
        builder.setMessage("¿Estás seguro de que quieres eliminar este ejercicio?")
                .setPositiveButton("Sí", (dialog, id) -> {
                    // Si el usuario hace clic en "Sí", eliminar la rutina
                    eliminarEjercicioDeRutina(idUsuario, idRutina, nombreUsuario, nombreRutina , nombreEjercicio);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    // Si el usuario hace clic en "No", cerrar el diálogo
                    dialog.dismiss();
                });

        // Crear y mostrar el AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarEjercicioDeRutina(int idUsuario, int idRutina, String nombreUsuario, String nombreRutina ,String nombreEjercicio) {
        // Actualiza el ejercicio en la base de datos
        bd.eliminarEjercicioDeRutina(idUsuario, idRutina, nombreEjercicio);

        // Notifica al usuario que la actualización fue exitosa
        Toast.makeText(this, "Se ha eliminado el ejercicio correctamente.", Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(EditarEjercicio.this, VistaRutina.class);
        intent1.putExtra("username", nombreUsuario);
        intent1.putExtra("NombreRutina", nombreRutina);
        startActivity(intent1);
        finish();
    }
}
