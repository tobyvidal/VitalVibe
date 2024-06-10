package com.example.proyectofinal20.Registro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectofinal20.BD.BD;
import com.example.proyectofinal20.Home.Principal;
import com.example.proyectofinal20.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class InicioSesion extends AppCompatActivity {

    private static final String TAG = "IniciarSesion";
    private static final String FILENAME = "frases.txt";

    private ArrayList<String> fortunesList;
    private BD bd; // Añadir una instancia de la clase BD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciosesion);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        // Cargar las frases motivadoras
        fortunesList = loadFortunes();
        TextView fortuneTextView = findViewById(R.id.textView22);
        fortuneTextView.setText(getRandomFortune());
        bd = new BD(this); // Inicializar la instancia de BD
        EditText usernameEditText = findViewById(R.id.editTextText);
        EditText passwordEditText = findViewById(R.id.editTextText2);
        Button iniciarSesionButton = findViewById(R.id.button3);
        Button crearCuentaButton = findViewById(R.id.botonC);
        CheckBox recordarUsuarioCheckBox = findViewById(R.id.checkBox);


        SharedPreferences sharedPreferences = getSharedPreferences("preferencias", MODE_PRIVATE);
        Integer savedUsername = sharedPreferences.getInt("ID", 0);
        boolean isRemembered = sharedPreferences.getBoolean("remember", false);

      if (isRemembered && savedUsername != null) {
            String nombreUsuario = bd.obtenerNombreUsuarioPorId(savedUsername);
            // El usuario ya ha iniciado sesión y ha seleccionado recordar, iniciar la actividad principal
            Intent intent = new Intent(InicioSesion.this, Principal.class);
            intent.putExtra("username", nombreUsuario); // Añadir el nombre de usuario como un extra en el Intent
            startActivity(intent);
            finish();
        }

        iniciarSesionButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            Log.d(TAG, "Username: " + username);
            Log.d(TAG, "Password: " + password);

            // Consultar el usuario en la base de datos
            Cursor cursor = bd.consultarUsuarioYContrasena(username, password);
            if (cursor.moveToFirst()) {
                // La contraseña es correcta, puedes continuar con el inicio de sesión
                Toast.makeText(InicioSesion.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InicioSesion.this, Principal.class);
                intent.putExtra("username", username); // Añadir el nombre de usuario como un extra en el Intent
                if (recordarUsuarioCheckBox.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    int id = bd.obtenerIdUsuarioPorNombre(username);
                    editor.putInt("ID", id);
                    editor.putBoolean("remember", true);
                    editor.apply();
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                }
                else{
                    startActivity(intent);
                }

            } else {
                // El usuario no existe o la contraseña es incorrecta
                Toast.makeText(InicioSesion.this, "Los datos son incorrectos", Toast.LENGTH_SHORT).show();
            }
        });



        crearCuentaButton.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesion.this, CrearCuenta.class);
            startActivity(intent);

        });
    }

    private String getRandomFortune() {
        Random random = new Random();
        int randomIndex = random.nextInt(fortunesList.size());
        return fortunesList.get(randomIndex);
    }
    private ArrayList<String> loadFortunes() {
        ArrayList<String> fortunes = new ArrayList<>();

        try {
            // Abrir el archivo desde la carpeta de archivos internos
            InputStream inputStream = openFileInput(FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            // Leer cada línea y agregarla a la lista de fortunas
            while ((line = bufferedReader.readLine()) != null) {
                fortunes.add(line);
            }

            // Cerrar el flujo de lectura
            inputStream.close();
        } catch (IOException e) {
            // Si el archivo no existe, cargar las frases iniciales
            fortunes.add("El éxito está en tu horizonte. Mantén la perseverancia.");
            fortunes.add("La paciencia es la llave del éxito. Todo llega a su debido tiempo.");
            fortunes.add("Un pequeño cambio puede hacer una gran diferencia en tu vida.");
            fortunes.add("Un pequeño cambio puede hacer una gran diferencia en tu vida.");
            fortunes.add("El que no pelea por lo que quiere, no merece lo que desea.");
            fortunes.add("El éxito en el gimnasio comienza con la voluntad de intentarlo una vez más.");
            fortunes.add("El dolor que sientes hoy será la fuerza que sientas mañana.");
            fortunes.add("Nunca te rindas. Los comienzos son siempre los más difíciles.");
            fortunes.add("Entrena como si fueras un atleta, come como si fueras un nutricionista, duerme como si fueras un bebé y crecerás como un campeón.");
            fortunes.add("El cuerpo logra lo que la mente cree.");
            fortunes.add("Tu única competencia es quien eras ayer.");
            fortunes.add("No busques excusas, busca resultados.");
            fortunes.add("Los cambios no ocurren de la noche a la mañana, pero sí ocurren.");
            fortunes.add("Sudar ahora, brillar después.");
            fortunes.add("El gimnasio no es solo un lugar, es un estado mental.");
            // Guardar las frases iniciales en el archivo
            saveFortunes(fortunes);
        }

        return fortunes;
    }

    private void saveFortunes(ArrayList<String> fortunes) {
        try {
            // Abrir el archivo para escritura
            openFileOutput(FILENAME, MODE_PRIVATE).close();
            // Escribir cada fortuna en una línea del archivo
            for (String fortune : fortunes) {
                fortune += "\n";
                openFileOutput(FILENAME, MODE_APPEND).write(fortune.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}