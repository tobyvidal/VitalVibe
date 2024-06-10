package com.example.proyectofinal20.Perfil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectofinal20.BD.BD;
import com.example.proyectofinal20.Home.Principal;
import com.example.proyectofinal20.R;
import com.example.proyectofinal20.Registro.InicioSesion;
import com.example.proyectofinal20.Rutina.Rutinas;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditarPerfil extends AppCompatActivity {

    private String genero = "";
    private String nivel = "";

    private BD bd; // Añadir una instancia de la clase BD
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editarperfil);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        bd = new BD(this);

        EditText nombre = findViewById(R.id.editTextText4);
        EditText edad = findViewById(R.id.editTextText5);
        EditText peso = findViewById(R.id.editTextText6);
        EditText altura = findViewById(R.id.editTextText7);
        EditText contraseña = findViewById(R.id.editTextText8);
        RadioGroup radioGroupGender = findViewById(R.id.radioGroupGender);
        RadioGroup radioGroupLevel = findViewById(R.id.radioGroupLevel);
        CheckBox recordarUsuarioCheckBox = findViewById(R.id.checkBox2);

        SharedPreferences sharedPreferences = getSharedPreferences("preferencias", MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("remember", false);

        if (isRemembered) {
            // El usuario ha seleccionado recordar, hacer visible el CheckBox
            recordarUsuarioCheckBox.setVisibility(View.VISIBLE);
        } else {
            // El usuario no ha seleccionado recordar, hacer invisible el CheckBox
            recordarUsuarioCheckBox.setVisibility(View.GONE);
        }

        recordarUsuarioCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // El usuario ha seleccionado desactivar el inicio de sesión automático, borrar los detalles del usuario de SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("username");
                editor.remove("remember");
                editor.apply();
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                if (id == R.id.navigation_home) {
                    Intent intentPrincipal = new Intent(EditarPerfil.this, Principal.class);
                    intentPrincipal.putExtra("username", username);
                    startActivity(intentPrincipal);
                    finish();
                    return true;
                } else if (id == R.id.navigation_routines) {
                    Intent intentRutinas = new Intent(EditarPerfil.this, Rutinas.class);
                    intentRutinas.putExtra("username", username);
                    startActivity(intentRutinas);
                    finish();
                    return true;
                } else if (id == R.id.navigation_profile) {
                    Intent intentPerfil = new Intent(EditarPerfil.this, Perfil.class);
                    intentPerfil.putExtra("username", username);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                }
                return false;
            }
        });

// Agrega este código para establecer el elemento seleccionado
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);




        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Aquí va el código que se ejecutará cuando se haga clic en el botón Mujer o Hombre
                if (checkedId == R.id.radioButton2) {
                    genero = "Mujer";
                } else if (checkedId == R.id.radioButton) {
                    genero = "Hombre";
                }
            }
        });

        Button actualizarPerfil = findViewById(R.id.button51);
        actualizarPerfil.setOnClickListener(v -> {
            if (!nombre.getText().toString().isEmpty() && !nombre.getText().toString().equals("Nuevo nombre")) {
                Cursor cursor = bd.consultarUsuario(nombre.getText().toString());
                if (cursor.getCount() > 0) {
                    Toast.makeText(this, "Nombre de usuario ya existe.", Toast.LENGTH_SHORT).show();
                } else {
                    bd.actualizarNombreUsuario(username, nombre.getText().toString());
                }
            }

            if (!edad.getText().toString().isEmpty() && !edad.getText().toString().equals("Nueva edad")) {
                String edadString = edad.getText().toString();
                bd.actualizarEdadUsuario(username, edadString);
            }

            if (!peso.getText().toString().isEmpty() && !peso.getText().toString().equals("Nuevo peso")) {
                bd.actualizarPesoUsuario(username, peso.getText().toString());
            }

            if (!contraseña.getText().toString().isEmpty() && !contraseña.getText().toString().equals("Nueva contraseña")) {
                bd.actualizarContrasenaUsuario(username, contraseña.getText().toString());
            }

            if (!genero.isEmpty()) {
                bd.actualizarGeneroUsuario(username, genero);
            }

            if (!nivel.isEmpty()) {
                bd.actualizarNivelUsuario(username, nivel);
            }

            if (!altura.getText().toString().isEmpty() && !altura.getText().toString().equals("Nueva altura")) {
                bd.actualizarAlturaUsuario(username, altura.getText().toString());
            }

            Toast.makeText(this, "Perfil actualizado.", Toast.LENGTH_SHORT).show();
            Intent intentRutinas = new Intent(EditarPerfil.this, InicioSesion.class);
            intentRutinas.putExtra("username", username);
            startActivity(intentRutinas);

        });

    }
}
