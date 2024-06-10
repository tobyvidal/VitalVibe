package com.example.proyectofinal20.Rutina;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextPaint;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.proyectofinal20.BD.BD;
import com.example.proyectofinal20.Objetos.EjercicioEnRutina;
import com.example.proyectofinal20.R;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class VistaRutina extends AppCompatActivity {
    private EditText nombreRutinaEditText;
    private EditText notasRutinaEditText; // Nuevo campo para las notas
    private ListView listaEjerciciosListView;
    private Button guardarButton;

    private Button guardarNota;
    private Button eliminarButton;

    private Button enviarRutina;
    private BD bd;
    private ArrayAdapter<EjercicioEnRutina> ejerciciosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_rutina);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        if(checkPermission()) {
            /*Permiso concedido*/
        } else {
            requestPermissions();
        }

        // Inicializa la base de datos
        bd = new BD(this);

        // Inicializa los campos de la actividad
        nombreRutinaEditText = findViewById(R.id.nombreRutinaEditText);
        notasRutinaEditText = findViewById(R.id.notasRutinaEditText); // Inicializa el nuevo campo
        listaEjerciciosListView = findViewById(R.id.listaEjercicios);
        guardarButton = findViewById(R.id.guardarButton);
        eliminarButton = findViewById(R.id.eliminarButton);
        enviarRutina = findViewById(R.id.enviarRutinaButton);
        guardarNota = findViewById(R.id.guardarNotasButton); // Inicializa el nuevo botón

        // Obtener el ID de la rutina desde el Intent
        Intent intent = getIntent();
        String nombreUsuario = intent.getStringExtra("username");
        String nombreRutina = intent.getStringExtra("NombreRutina");
        int idUsuario = bd.obtenerIdUsuarioPorNombre(nombreUsuario);
        int idRutina = bd.obtenerIdRutina(nombreRutina, nombreUsuario);

        String notas = bd.getNotas(idUsuario, idRutina);
        if (notas != null && !notas.isEmpty()) {
            notasRutinaEditText.setText(notas);
        }
        else{
            notasRutinaEditText.setText("Ingrese notas aquí.");
        }

        // Cargar los detalles de la rutina y los ejercicios asociados
        nombreRutinaEditText.setText(nombreRutina);
        cargarEjerciciosDeRutina(nombreUsuario, nombreRutina);

        // Configurar el botón de guardar cambios
        guardarButton.setOnClickListener(v -> {
            anyadirEjercicio(nombreUsuario, nombreRutina);
        });

        guardarNota.setOnClickListener(v -> {
            guardarNotas(nombreUsuario, nombreRutina); // Guardar las notas cuando se hace clic en el botón;
        });

        // Configurar el botón de eliminar rutina
        eliminarButton.setOnClickListener(v -> confirmarEliminarRutina(idUsuario, idRutina, nombreUsuario, nombreRutina));



        enviarRutina.setOnClickListener(v -> crearPdf());


    }

    // Cargar los ejercicios asociados a la rutina desde la base de datos
    private void cargarEjerciciosDeRutina(String nombreUsuario, String nombreRutina) {
        // Obtener los ID de usuario y rutina
        int idUsuario = bd.obtenerIdUsuarioPorNombre(nombreUsuario);
        int idRutina = bd.obtenerIdRutina(nombreRutina, nombreUsuario);
        if (idUsuario == -1 || idRutina == -1) {
            Toast.makeText(this, "No se encontró la rutina", Toast.LENGTH_SHORT).show();
        } else {
            // Consultar los ejercicios asociados a la rutina y usuario específicos
            List<EjercicioEnRutina> ejercicios = bd.consultarEjerciciosPorRutinaYUsuario(idRutina, idUsuario);

            if (!ejercicios.isEmpty()) {
                // Crear un ArrayAdapter para los ejercicios
                ejerciciosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ejercicios);

                // Establecer el ArrayAdapter en el ListView
                listaEjerciciosListView.setAdapter(ejerciciosAdapter);

                // Configurar el OnItemClickListener para manejar clics en los elementos de la lista
                listaEjerciciosListView.setOnItemClickListener((parent, view, position, id) -> {
                    // Obtener el ejercicio seleccionado
                    EjercicioEnRutina ejercicioSeleccionado = ejercicios.get(position);

                    // Crear un Intent para iniciar EditarEjercicio y pasar los datos del ejercicio seleccionado
                    Intent intentEditarEjercicio = new Intent(VistaRutina.this, EditarEjercicio.class);
                    intentEditarEjercicio.putExtra("idUsuario", idUsuario);
                    intentEditarEjercicio.putExtra("idRutina", idRutina);
                    intentEditarEjercicio.putExtra("nombreEjercicio", ejercicioSeleccionado.getNombre());
                    intentEditarEjercicio.putExtra("categoria", ejercicioSeleccionado.getCategoria());
                    intentEditarEjercicio.putExtra("series", ejercicioSeleccionado.getSeries());
                    intentEditarEjercicio.putExtra("repeticiones", ejercicioSeleccionado.getRepeticiones());

                    // Iniciar la actividad EditarEjercicio
                    startActivity(intentEditarEjercicio);
                    finish();
                });
            } else {
                // Mostrar un Toast si no se encuentran ejercicios asociados a la rutina
                Toast.makeText(this, "No se encontraron ejercicios asociados a esta rutina.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Guardar los cambios realizados a la rutina
    private void anyadirEjercicio(String nombreUsuario, String nombreRutina) {
        String nombre = nombreRutinaEditText.getText().toString();

        // Verificar que el campo de nombre no esté vacío
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Por favor, completa el campo de nombre.", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            Intent intent = new Intent(VistaRutina.this, ViewEjercicio.class);

            if(nombreRutina.isEmpty()){
                Toast toast = Toast.makeText(this, "No se ha encontrado la rutina", Toast.LENGTH_SHORT);
            }
            else{
                intent.putExtra("username", nombreUsuario);
                intent.putExtra("nombreRutina", nombreRutina);
                Toast toast = Toast.makeText(this, "No se ha encontrado la rutina" + nombreRutina, Toast.LENGTH_SHORT);
                startActivity(intent);
            }

        }

        // Actualizar la rutina en la base de datos
        //bd.actualizarRutina(idRutina, nombre);
        //Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
        finish(); // Cerrar la actividad
    }

    private void confirmarEliminarRutina(int idUsuario, int idRutina, String nombreUsuario, String nombreRutina) {
        // Crear un nuevo AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Configurar el mensaje y los botones del AlertDialog
        builder.setMessage("¿Estás seguro de que quieres eliminar esta rutina?")
                .setPositiveButton("Sí", (dialog, id) -> {
                    // Si el usuario hace clic en "Sí", eliminar la rutina
                    eliminarRutina(idUsuario, idRutina, nombreUsuario, nombreRutina);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    // Si el usuario hace clic en "No", cerrar el diálogo
                    dialog.dismiss();
                });

        // Crear y mostrar el AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // Eliminar la rutina
    private void eliminarRutina(int idUsuario, int idRutina, String nombreUsuario, String nombreRutina) {
        bd.eliminarRutina(idUsuario, idRutina);
        Toast.makeText(this, "Rutina eliminada", Toast.LENGTH_SHORT).show();
        cargarEjerciciosDeRutina(nombreUsuario, nombreRutina);
    }

    // Nuevo método para guardar las notas
    private void guardarNotas(String nombreUsuario, String nombreRutina) {
        String notas = notasRutinaEditText.getText().toString();
        if(!notas.isEmpty()){
            int idUsuario = bd.obtenerIdUsuarioPorNombre(nombreUsuario);
            int idRutina = bd.obtenerIdRutina(nombreRutina, nombreUsuario);
            bd.updateNotas(idUsuario, idRutina, notas);
            Toast.makeText(this, "Se han guardado la nota.", Toast.LENGTH_SHORT).show();
        }
       else{
           Toast.makeText(this, "No se han guardado las notas, rellene el campo.", Toast.LENGTH_SHORT).show();
        }
    }

    private void crearPdf() {
        if (!checkPermission()) {
            requestPermissions();
            return;
        }

// Declara el objeto PdfDocument fuera del bloque try-catch para cerrarlo finalmente
        PdfDocument pdfDocument = new PdfDocument();

        try {
            // Crea una nueva página con la configuración adecuada
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            // Configura el canvas para dibujar en la página
            Canvas canvas = page.getCanvas();

            // Define los márgenes
            int marginLeft = 50;
            int marginTop = 50;

            // Define los estilos de texto
            Paint titlePaint = new Paint();
            titlePaint.setTextSize(20);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            Paint headerPaint = new Paint();
            headerPaint.setTextSize(14);
            headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            Paint textPaint = new Paint();
            textPaint.setTextSize(14);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            // Escribe el nombre de la rutina en el encabezado
            String nombreRutina = nombreRutinaEditText.getText().toString();
            canvas.drawText("Rutina: " + nombreRutina, marginLeft, marginTop, titlePaint);

            // Escribe los ejercicios
            int yPos = marginTop + 50; // Deja espacio para el encabezado
            if (ejerciciosAdapter != null) {
                for (int i = 0; i < ejerciciosAdapter.getCount(); i++) {
                    EjercicioEnRutina ejercicio = ejerciciosAdapter.getItem(i);
                    String textoEjercicio = String.format("Ejercicio: %s, Categoría: %s, Series: %d, Repeticiones: %d",
                            ejercicio.getNombre(),
                            ejercicio.getCategoria(),
                            ejercicio.getSeries(),
                            ejercicio.getRepeticiones());
                    canvas.drawText(textoEjercicio, marginLeft, yPos, textPaint);
                    yPos += 20; // Ajusta la posición Y para el próximo ejercicio

                    // Dibuja una línea divisoria
                    canvas.drawLine(marginLeft, yPos, pageInfo.getPageWidth() - marginLeft, yPos, textPaint);
                    yPos += 20; // Deja espacio para la línea divisoria
                }
            } else {
                canvas.drawText("No se encontraron ejercicios asociados.", marginLeft, yPos, textPaint);
            }

            // Escribe el número de página en el pie de página
            canvas.drawText("Página 1", pageInfo.getPageWidth() / 2, pageInfo.getPageHeight() - marginTop, headerPaint);

            // Finaliza la página
            pdfDocument.finishPage(page);

            // Obtén el directorio externo de la aplicación para guardar el PDF
            File myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File pdfFile = new File(myDir, nombreRutina + ".pdf");

            // Guarda el PDF en un archivo
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                pdfDocument.writeTo(fos);
                Toast.makeText(this, "PDF creado correctamente", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            // Comparte el PDF usando FileProvider
            try {
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Compartir PDF"));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al compartir el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al crear el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            // Asegúrate de cerrar el documento PDF en el bloque finally
            pdfDocument.close();
        }
    }





    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 200);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 200) {
            if(grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if(writeStorage && readStorage) {
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }













}