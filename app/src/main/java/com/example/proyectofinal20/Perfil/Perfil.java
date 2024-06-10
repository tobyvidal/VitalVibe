package com.example.proyectofinal20.Perfil;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.proyectofinal20.BD.BD;
import com.example.proyectofinal20.Home.Principal;
import com.example.proyectofinal20.R;
import com.example.proyectofinal20.Rutina.Rutinas;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Perfil extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private ImageView imageView;

    private BD bd; // Añadir una instancia de la clase BD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        bd = new BD(this);

        TextView nombre = findViewById(R.id.textView26);
        nombre.setText(username);

        TextView edad = findViewById(R.id.textView32);
        edad.setText(String.valueOf(bd.consultarEdadUsuario(username)));

        TextView altura = findViewById(R.id.textView34);
        altura.setText(String.valueOf(bd.consultarAlturaUsuario(username)));

        TextView peso = findViewById(R.id.textView36);
        peso.setText(String.valueOf(bd.consultarPesoUsuario(username)));

        TextView genero = findViewById(R.id.textView28);
        genero.setText(bd.consultarGeneroUsuario(username));

        TextView contrasena = findViewById(R.id.textView38);
        contrasena.setText(bd.consultarContrasenaUsuario(username));

        imageView = findViewById(R.id.imageView2);

        // Cargar imagen de perfil al iniciar la actividad
        loadProfileImage(username);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    Intent intentHome = new Intent(Perfil.this, Principal.class);
                    intentHome.putExtra("username", username);
                    startActivity(intentHome);
                    finish();
                    return true;
                } else if (id == R.id.navigation_routines) {
                    Intent intentRutinas = new Intent(Perfil.this, Rutinas.class);
                    intentRutinas.putExtra("username", username);
                    startActivity(intentRutinas);
                    finish();
                    return true;
                } else if (id == R.id.navigation_profile) {
                    // No necesitas hacer nada aquí ya que ya estás en la actividad de perfil
                    return true;
                }
                return false;
            }
        });

// Agrega este código para establecer el elemento seleccionado
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);


// Agrega este código para establecer el elemento seleccionado
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);


// Agrega este código para establecer el elemento seleccionado
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);


// Agrega este código para establecer el elemento seleccionado
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);


        Button botonEditar = findViewById(R.id.button43);
        botonEditar.setOnClickListener(v -> {
            Intent intentEditar = new Intent(Perfil.this, EditarPerfil.class);
            intentEditar.putExtra("username", username);
            startActivity(intentEditar);
        });

        // Configurar el clic en la imagen para seleccionar una nueva foto
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Solicitar permiso de lectura de almacenamiento externo si aún no está concedido
                if (ContextCompat.checkSelfPermission(Perfil.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Perfil.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    // Permiso ya concedido, abrir la galería
                    openGallery();
                }
            }
        });
    }

    public SharedPreferences getUserPreferences(String username) {
        // Crea un nombre de archivo para las preferencias del usuario específico
        String preferencesFileName = "user_prefs_" + username;
        // Devuelve las preferencias del usuario específico
        return getSharedPreferences(preferencesFileName, MODE_PRIVATE);
    }


    // Método para cargar la imagen de perfil desde SharedPreferences
    private void loadProfileImage(String username) {
        SharedPreferences prefs = getUserPreferences(username);
        String imagePath = prefs.getString("profile_image", null);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Si el permiso está concedido, cargar la imagen del perfil si está disponible
            if (imagePath != null) {
                imageView.setImageURI(Uri.parse(imagePath));
            } else {
                // Si no hay imagen guardada, establecer una imagen por defecto
                imageView.setImageResource(R.drawable.usuario);
            }
        } else {
            // Si el permiso no está concedido, establecer una imagen por defecto
            imageView.setImageResource(R.drawable.usuario);
        }
    }



    // Método para guardar la imagen de perfil en SharedPreferences
    // Método para guardar la imagen de perfil en SharedPreferences específicos para cada usuario
    private void saveProfileImage(String username, String imagePath) {
        // Accede a las preferencias del usuario específico usando su nombre de usuario
        SharedPreferences prefs = getUserPreferences(username);
        // Crea un editor para editar las preferencias
        SharedPreferences.Editor editor = prefs.edit();
        // Guarda la URI de la imagen de perfil en las preferencias
        editor.putString("profile_image", imagePath);
        // Aplica los cambios para guardarlos
        editor.apply();
    }


    // Método para abrir la galería
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Obtén el nombre de usuario de donde sea necesario (por ejemplo, de las preferencias o del intent)
                Intent intent = getIntent();
                String username = intent.getStringExtra("username");

                // Guarda la imagen de perfil para el usuario específico
                saveProfileImage(username, imageUri.toString());
                imageView.setImageURI(imageUri);
            } else {
                // Manejar el caso de que la URI de la imagen sea nula
                imageView.setImageResource(R.drawable.usuario);
            }
        }
    }


    // Método para manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, abrir la galería
                openGallery();
            } else {
                // Permiso denegado, mostrar un mensaje al usuario
                Toast.makeText(this, "Permiso de lectura de almacenamiento externo denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
