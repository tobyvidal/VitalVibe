package com.example.proyectofinal20.Rutina;

import android.content.Intent;
import android.database.Cursor;
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
import com.example.proyectofinal20.Objetos.Ejercicio;
import com.example.proyectofinal20.Objetos.EjercicioEnRutina;
import com.example.proyectofinal20.R;

public class GrabarEjercicio extends AppCompatActivity {
    private EditText nombreEditText;
    private EditText categoriaEditText;
    private EditText seriesEditText;
    private EditText repeticionesEditText;
    private Button grabarButton;
    private BD bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabar_ejercicio);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        // Inicializa la base de datos
        bd = new BD(this);

        // Inicializa los campos de la actividad
        nombreEditText = findViewById(R.id.nombreEditText);
        categoriaEditText = findViewById(R.id.categoriaEditText);
        seriesEditText = findViewById(R.id.seriesEditText);
        repeticionesEditText = findViewById(R.id.repeticionesEditText);
        grabarButton = findViewById(R.id.grabarButton);

        // Obtener los datos del ejercicio desde el Intent
        Intent intent = getIntent();
        String nombreUsuario = intent.getStringExtra("username");
        String nombreRutina = intent.getStringExtra("nombreRutina");
        String nombreEjercicio = intent.getStringExtra("nombreEjercicio");
        String categoria = intent.getStringExtra("categoria");

        // Asegúrate de que los datos recibidos no sean nulos
        if (nombreUsuario != null && nombreRutina != null && nombreEjercicio != null && categoria != null) {
            // Asigna los valores a los campos de texto correctos
            nombreEditText.setText(nombreEjercicio);
            categoriaEditText.setText(categoria);
        } else {
            Toast.makeText(this, "Error al recibir datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar el botón de grabar
        grabarButton.setOnClickListener(v -> {
            // Obtener los valores de los campos de texto
            String nuevoNombre = nombreEditText.getText().toString();
            String nuevaCategoria = categoriaEditText.getText().toString();

            // Asegúrate de que los valores de series y repeticiones se puedan convertir a enteros
            int nuevasSeries;
            int nuevasRepeticiones;

            try {
                nuevasSeries = Integer.parseInt(seriesEditText.getText().toString());
                nuevasRepeticiones = Integer.parseInt(repeticionesEditText.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Las series y repeticiones deben ser números enteros", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica que los valores de entrada no estén vacíos
            if (nuevoNombre.isEmpty() || nuevaCategoria.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                // Crea un objeto Ejercicio
                EjercicioEnRutina ejercicio = new EjercicioEnRutina(nuevoNombre, nuevaCategoria, nuevasSeries, nuevasRepeticiones);
                int IdUsuario = bd.obtenerIdUsuarioPorNombre(nombreUsuario);
                int IdRutina = bd.obtenerIdRutina(nombreRutina, nombreUsuario);
                // Inserta el ejercicio en la rutina
                bd.insertarEjercicioEnRutinas(IdRutina, IdUsuario, ejercicio);

                // Notifica al usuario que el ejercicio ha sido guardado correctamente
                Toast.makeText(this, "Ejercicio guardado correctamente", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(GrabarEjercicio.this, VistaRutina.class);
                intent1.putExtra("username", nombreUsuario);
                intent1.putExtra("NombreRutina", nombreRutina);
                startActivity(intent1);
                finish();
            }


        });
    }
}
