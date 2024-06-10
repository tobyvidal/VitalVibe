package com.example.proyectofinal20.Home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectofinal20.Perfil.Perfil;
import com.example.proyectofinal20.R;
import com.example.proyectofinal20.Rutina.Ejercicios;
import com.example.proyectofinal20.Rutina.Rutinas;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        Intent intentNombre = getIntent();
        String nombreuser = intentNombre.getStringExtra("username");

        TextView texto = findViewById(R.id.textView2);
        texto.setText("Bienvenido " + nombreuser + "!");

        Button botonEjercicios = findViewById(R.id.button);
        /*Menu de navegación*/
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    // No necesitas hacer nada aquí ya que ya estás en la actividad de inicio
                    return true;
                } else if (id == R.id.navigation_routines) {
                    Intent intentRutinas = new Intent(Principal.this, Rutinas.class);
                    intentRutinas.putExtra("username", nombreuser);
                    startActivity(intentRutinas);
                    finish();
                    return true;
                } else if (id == R.id.navigation_profile) {
                    Intent intentPerfil = new Intent(Principal.this, Perfil.class);
                    intentPerfil.putExtra("username", nombreuser);
                    startActivity(intentPerfil);
                    finish();
                    return true;
                }
                return false;
            }
        });

// Agrega este código para establecer el elemento seleccionado
        bottomNavigationView.setSelectedItemId(R.id.navigation_home); // O el elemento correspondiente a la actividad de inicio


        botonEjercicios.setOnClickListener(v -> {
            Intent intentEjercicios = new Intent(Principal.this, Ejercicios.class);
            intentEjercicios.putExtra("username", nombreuser);
            startActivity(intentEjercicios);
        });
    }
}
