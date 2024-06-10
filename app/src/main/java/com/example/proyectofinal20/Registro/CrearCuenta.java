package com.example.proyectofinal20.Registro;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectofinal20.BD.BD;
import com.example.proyectofinal20.R;

public class CrearCuenta extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButtonMujer;
    private RadioButton radioButtonHombre;
    private static EditText nombreUsuario;
    private EditText edadUsuario;
    private EditText alturaUsuario;
    private EditText pesoUsuario;
    private EditText contrasena;
    private String genero = "";  // Convertir 'genero' en un campo de la clase
    private Button buttonCrearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crearcuenta);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonMujer = findViewById(R.id.botonMujer);
        radioButtonHombre = findViewById(R.id.botonHombre);
        nombreUsuario = findViewById(R.id.nombreUsuario);
        edadUsuario = findViewById(R.id.editTextNumber);
        alturaUsuario = findViewById(R.id.alturaUsuario);
        pesoUsuario = findViewById(R.id.pesoUsuario);
        contrasena = findViewById(R.id.editTextTextPassword);
        buttonCrearCuenta = findViewById(R.id.botonCrearCuenta);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Aquí va el código que se ejecutará cuando se haga clic en el botón Mujer o Hombre
                if (checkedId == R.id.botonMujer) {
                    genero = "Mujer";
                } else if (checkedId == R.id.botonHombre) {
                    genero = "Hombre";
                }
            }
        });

        buttonCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombreUsuario.getText().toString().isEmpty() ||
                        edadUsuario.getText().toString().isEmpty() ||
                        alturaUsuario.getText().toString().isEmpty() ||
                        pesoUsuario.getText().toString().isEmpty() ||
                        contrasena.getText().toString().isEmpty() ||
                        genero.isEmpty()) {
                    Toast.makeText(CrearCuenta.this, "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show();
                } else {
                    String password = getContrasena();
                    if (password.length() < 8 || !password.matches(".*[A-Z].*")) {
                        Toast.makeText(CrearCuenta.this, "La contraseña debe tener al menos 8 caracteres y contener al menos una letra mayúscula.", Toast.LENGTH_SHORT).show();
                    } else {
                        BD bd = new BD(CrearCuenta.this);
                        Cursor cursor = bd.consultarUsuario(getNombreUsuario());
                        if (cursor.getCount() > 0) {
                            Toast.makeText(CrearCuenta.this, "El nombre de usuario ya existe.", Toast.LENGTH_SHORT).show();
                        } else {
                            bd.insertData(getNombreUsuario(), getGenero(), getEdadUsuario(), getAlturaUsuario(), getPesoUsuario(), getContrasena());
                            Toast.makeText(CrearCuenta.this, "Cuenta creada correctamente.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CrearCuenta.this, InicioSesion.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        });
    }

    public String getGenero(){
        return genero;
    }

    // Métodos para obtener los valores de los otros campos
    public static String getNombreUsuario() {
        return nombreUsuario.getText().toString();
    }

    public int getEdadUsuario() {
        return Integer.parseInt(edadUsuario.getText().toString());
    }

    public float getAlturaUsuario() {
        return Float.parseFloat(alturaUsuario.getText().toString());
    }

    public float getPesoUsuario() {
        return Float.parseFloat(pesoUsuario.getText().toString());
    }

    public String getContrasena() {
        return contrasena.getText().toString();
    }
}