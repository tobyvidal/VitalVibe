package com.example.proyectofinal20.BD;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import com.example.proyectofinal20.Objetos.Ejercicio;
import com.example.proyectofinal20.Objetos.EjercicioEnRutina;
import com.example.proyectofinal20.Rutina.Ejercicios;

import java.util.ArrayList;
import java.util.List;

public class BD extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "VitalVibe.db";
    private static final int DATABASE_VERSION = 1;

    public BD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Crear la tabla Usuario si no existe
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Usuario (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NombreUsuario VARCHAR(255), " +
                "Genero VARCHAR(255), " +
                "Edad INTEGER, " +
                "Altura FLOAT, " +
                "Peso FLOAT, " +
                "Contrasena VARCHAR(255));");

        // Crear la tabla Rutinas si no existe
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Rutinas (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NombreRutina VARCHAR(255), " +
                "Notas VARCHAR(255), " +
                "ID_Usuario INTEGER, " +
                "FOREIGN KEY(ID_Usuario) REFERENCES Usuario(ID));");

        // Crear la tabla Ejercicios si no existe
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Ejercicios (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NombreEjercicio VARCHAR(255), " +
                "Categoria VARCHAR(255), " +
                "ID_Rutina INTEGER, " +
                "ID_Usuario INTEGER, " +  // Agrega el campo ID_Usuario
                "FOREIGN KEY(ID_Rutina) REFERENCES Rutinas(ID), " +
                "FOREIGN KEY(ID_Usuario) REFERENCES Usuario(ID));");

        // Crear la tabla EjerciciosEnRutinas si no existe
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS EjerciciosEnRutinas (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NombreEjercicio VARCHAR(255), " +
                "Categoria VARCHAR(255), " +
                "Series INTEGER, " +
                "Repeticiones INTEGER, " +
                "ID_Rutina INTEGER, " +
                "ID_Usuario INTEGER, " +  // Agrega el campo ID_Usuario
                "FOREIGN KEY(ID_Rutina) REFERENCES Rutinas(ID), " +
                "FOREIGN KEY(ID_Usuario) REFERENCES Usuario(ID));");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Aquí es donde debes manejar las actualizaciones de la base de datos
    }

    /*Obtiene el nombre del usuario por ID y devuelve su nombre.*/
    public String obtenerNombreUsuarioPorId(int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT NombreUsuario FROM Usuario WHERE ID = ?", new String[]{String.valueOf(idUsuario)});

        String nombreUsuario = null; // Valor por defecto en caso de que no se encuentre el usuario

        if (cursor.moveToFirst()) {
            nombreUsuario = cursor.getString(0); // Obtener el nombre del usuario
        }

        cursor.close();
        return nombreUsuario;
    }

    /*Consulta en la base de datos el usuario.*/
    public Cursor consultarUsuarioYContrasena(String nombreUsuario, String contrasena) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Usuario WHERE NombreUsuario = ? AND Contrasena = ?", new String[]{nombreUsuario, contrasena});
        return cursor;
    }

    /*Mediante el nombre obtiene el ID del usuario.*/
    public int obtenerIdUsuarioPorNombre(String nombreUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        int idUsuario = -1; // Valor por defecto en caso de que no se encuentre el usuario

        if (cursor.moveToFirst()) {
            idUsuario = cursor.getInt(0); // Obtener el ID del usuario
        }

        cursor.close();
        return idUsuario;
    }

    /*Consulta el usuario por el nombre pasado por parametros.*/
    public Cursor consultarUsuario(String nombreUsuario) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});
        return cursor;
    }

    // Método para insertar datos en la base de datos
    public void insertData(String nombreUsuario, String genero, int edadUsuario, float alturaUsuario, float pesoUsuario, String contrasena) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("INSERT INTO Usuario (NombreUsuario, Genero, Edad, Altura, Peso, Contrasena) VALUES ('" +
                nombreUsuario + "', '" + genero + "', " + edadUsuario + ", " + alturaUsuario + ", " + pesoUsuario + ", '" + contrasena + "');");
    }

    /*Consulta el nombre de todas las rutinas.*/
    public List<String> consultarNombresRutinas(String nombreUsuario) {
        List<String> nombresRutinas = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            Cursor cursorRutinas = sqLiteDatabase.rawQuery("SELECT DISTINCT Rutinas.NombreRutina FROM Rutinas WHERE Rutinas.ID_Usuario = ?", new String[]{String.valueOf(idUsuario)});

            while (cursorRutinas.moveToNext()) {
                String nombreRutina = cursorRutinas.getString(0);
                nombresRutinas.add(nombreRutina);
            }

            cursorRutinas.close();
        }

        cursorUsuario.close();
        return nombresRutinas;
    }

    /*Consulta todos los ejercicios de una rutina.*/
    @SuppressLint("Range")
    public List<Ejercicio> consultarEjercicios(String nombreRutina, String nombreUsuario) {
        List<Ejercicio> ejercicios = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        // Primero, busca el ID del usuario por su nombre
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});
        if (cursorUsuario != null && cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            cursorUsuario.close();

            // Luego, busca el ID de la rutina por su nombre y ID de usuario
            Cursor cursorRutina = sqLiteDatabase.rawQuery("SELECT ID FROM Rutinas WHERE NombreRutina = ? AND ID_Usuario = ?", new String[]{nombreRutina, String.valueOf(idUsuario)});
            if (cursorRutina != null && cursorRutina.moveToFirst()) {
                int idRutina = cursorRutina.getInt(0);
                cursorRutina.close();

                // Finalmente, usa el ID de la rutina para buscar los ejercicios
                Cursor cursorEjercicios = sqLiteDatabase.rawQuery("SELECT NombreEjercicio, Categoria FROM Ejercicios WHERE ID_Rutina = ?", new String[]{String.valueOf(idRutina)});

                if (cursorEjercicios != null) {
                    while (cursorEjercicios.moveToNext()) {
                        String nombreEjercicio = cursorEjercicios.getString(cursorEjercicios.getColumnIndex("NombreEjercicio"));
                        String categoria = cursorEjercicios.getString(cursorEjercicios.getColumnIndex("Categoria"));
                        int series = cursorEjercicios.getInt(cursorEjercicios.getColumnIndex("Series"));
                        int repeticiones = cursorEjercicios.getInt(cursorEjercicios.getColumnIndex("Repeticiones"));

                        // Crea un objeto Ejercicio con los datos obtenidos
                        Ejercicio ejercicio = new Ejercicio(nombreEjercicio, categoria, series, repeticiones);
                        ejercicios.add(ejercicio);
                    }
                    cursorEjercicios.close();
                }
            }
        }

        return ejercicios;
    }

    public int consultarEdadUsuario(String nombreUsuario) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT Edad FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int edad = cursor.getInt(cursor.getColumnIndex("Edad"));
            cursor.close();
            return edad;
        } else {
            return -1;  // Devuelve -1 si el usuario no se encuentra
        }
    }

    public float consultarAlturaUsuario(String nombreUsuario) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT Altura FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") float altura = cursor.getFloat(cursor.getColumnIndex("Altura"));
            cursor.close();
            return altura;
        } else {
            return -1;  // Devuelve -1 si el usuario no se encuentra
        }
    }

    public float consultarPesoUsuario(String nombreUsuario) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT Peso FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") float peso = cursor.getFloat(cursor.getColumnIndex("Peso"));
            cursor.close();
            return peso;
        } else {
            return -1;  // Devuelve -1 si el usuario no se encuentra
        }
    }

    public String consultarGeneroUsuario(String nombreUsuario) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT Genero FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String genero = cursor.getString(cursor.getColumnIndex("Genero"));
            cursor.close();
            return genero;
        } else {
            return null;
        }
    }

    public String consultarContrasenaUsuario(String nombreUsuario) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT Contrasena FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String contrasena = cursor.getString(cursor.getColumnIndex("Contrasena"));
            cursor.close();
            return contrasena;
        } else {
            return null;
        }
    }

    public void actualizarNombreUsuario(String nombreUsuario, String nuevoNombre) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            ContentValues contentValues = new ContentValues();
            contentValues.put("NombreUsuario", nuevoNombre);
            sqLiteDatabase.update("Usuario", contentValues, "ID = ?", new String[]{String.valueOf(idUsuario)});
            cursorUsuario.close();
        }
    }

    public void actualizarEdadUsuario(String nombreUsuario, String nuevaEdad) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Edad", Integer.parseInt(nuevaEdad));
            sqLiteDatabase.update("Usuario", contentValues, "ID = ?", new String[]{String.valueOf(idUsuario)});
            cursorUsuario.close();
        }
    }

    public void actualizarPesoUsuario(String nombreUsuario, String nuevoPeso) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Peso", Float.parseFloat(nuevoPeso));
            sqLiteDatabase.update("Usuario", contentValues, "ID = ?", new String[]{String.valueOf(idUsuario)});
            cursorUsuario.close();
        }
    }

    public void actualizarContrasenaUsuario(String nombreUsuario, String nuevaContrasena) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Contrasena", nuevaContrasena);
            sqLiteDatabase.update("Usuario", contentValues, "ID = ?", new String[]{String.valueOf(idUsuario)});
            cursorUsuario.close();
        }
    }

    public void actualizarGeneroUsuario(String nombreUsuario, String nuevoGenero) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Genero", nuevoGenero);
            sqLiteDatabase.update("Usuario", contentValues, "ID = ?", new String[]{String.valueOf(idUsuario)});
            cursorUsuario.close();
        }
    }

    public void actualizarNivelUsuario(String nombreUsuario, String nuevoNivel) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Nivel", nuevoNivel);
            sqLiteDatabase.update("Nivel", contentValues, "ID_Usuario = ?", new String[]{String.valueOf(idUsuario)});
            cursorUsuario.close();
        }
    }

    public void actualizarAlturaUsuario(String nombreUsuario, String nuevaAltura) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Altura", Float.parseFloat(nuevaAltura));
            sqLiteDatabase.update("Usuario", contentValues, "ID = ?", new String[]{String.valueOf(idUsuario)});
            cursorUsuario.close();
        }
    }

    @SuppressLint("Range")
    public List<Ejercicio> consultarEjercicios(String nombreUsuario) {
        List<Ejercicio> ejercicios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener el ID del usuario basado en su nombre de usuario
        Cursor cursorUsuario = db.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        // Verificar si el usuario existe
        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);

            // Consulta para obtener los ejercicios asociados con el ID del usuario
            Cursor cursorEjercicios = db.rawQuery("SELECT NombreEjercicio, Categoria FROM Ejercicios WHERE ID_Usuario = ?", new String[]{String.valueOf(idUsuario)});

            while (cursorEjercicios.moveToNext()) {
                // Obtener nombre y categoría del ejercicio
                String nombre = cursorEjercicios.getString(cursorEjercicios.getColumnIndex("NombreEjercicio"));
                String categoria = cursorEjercicios.getString(cursorEjercicios.getColumnIndex("Categoria"));

                // Crear un objeto Ejercicio y agregarlo a la lista
                Ejercicio ejercicio = new Ejercicio(nombre, categoria);
                ejercicios.add(ejercicio);
            }

            cursorEjercicios.close();
        }

        cursorUsuario.close();
        return ejercicios;
    }



    public void insertEjercicio(Ejercicio ejercicio, String nombreUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorUsuario = db.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);

            ContentValues contentValues = new ContentValues();
            contentValues.put("NombreEjercicio", ejercicio.nombre);
            contentValues.put("Categoria", ejercicio.categoria);
            contentValues.put("ID_Usuario", idUsuario); // Asociar ejercicio con el ID del usuario
            db.insert("Ejercicios", null, contentValues);
        }

        cursorUsuario.close();
    }
    @SuppressLint("Range")
    public List<Ejercicio> consultarEjerciciosPorCategoria(String nombreUsuario, String categoria) {
        List<Ejercicio> ejercicios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener el ID del usuario basado en el nombre de usuario
        Cursor cursorUsuario = db.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});
        int idUsuario = -1;

        if (cursorUsuario.moveToFirst()) {
            idUsuario = cursorUsuario.getInt(cursorUsuario.getColumnIndex("ID"));
        }

        // Cerrar el cursor para liberar recursos
        cursorUsuario.close();

        // Si no se encontró el usuario, retornar la lista vacía
        if (idUsuario == -1) {
            return ejercicios;
        }

        // Consulta SQL para obtener ejercicios por categoría y usuario específico
        Cursor cursor = db.rawQuery("SELECT NombreEjercicio, Categoria FROM Ejercicios WHERE Categoria = ? AND ID_Usuario = ?", new String[]{categoria, String.valueOf(idUsuario)});

        while (cursor.moveToNext()) {
            // Obtener el nombre y la categoría del ejercicio
            String nombreEjercicio = cursor.getString(cursor.getColumnIndex("NombreEjercicio"));
            String categoriaEjercicio = cursor.getString(cursor.getColumnIndex("Categoria"));

            // Crear un objeto Ejercicio con los valores obtenidos
            Ejercicio ejercicio = new Ejercicio(nombreEjercicio, categoriaEjercicio);

            // Añadir el ejercicio a la lista
            ejercicios.add(ejercicio);
        }

        // Cerrar el cursor para liberar recursos
        cursor.close();

        // Retornar la lista de ejercicios por categoría y usuario específico
        return ejercicios;
    }

    public Cursor consultarRutinaPorNombreYUsuario(String nombreRutina, String nombreUsuario) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            Cursor cursorRutina = sqLiteDatabase.rawQuery("SELECT * FROM Rutinas WHERE NombreRutina = ? AND ID_Usuario = ?", new String[]{nombreRutina, String.valueOf(idUsuario)});
            cursorUsuario.close();
            return cursorRutina;
        }

        cursorUsuario.close();
        return null;
    }

    public void insertRutina(String nombreRutina, String nombreUsuario) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursor.moveToFirst()) {
            int idUsuario = cursor.getInt(0);
            ContentValues contentValues = new ContentValues();
            contentValues.put("NombreRutina", nombreRutina);
            contentValues.put("ID_Usuario", idUsuario);
            sqLiteDatabase.insert("Rutinas", null, contentValues);
        }

        cursor.close();
    }

    public int obtenerIdRutina(String nombreRutina, String nombreUsuario) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});

        if (cursorUsuario.moveToFirst()) {
            int idUsuario = cursorUsuario.getInt(0);
            Cursor cursorRutina = sqLiteDatabase.rawQuery("SELECT ID FROM Rutinas WHERE NombreRutina = ? AND ID_Usuario = ?", new String[]{nombreRutina, String.valueOf(idUsuario)});
            if (cursorRutina != null && cursorRutina.moveToFirst()) {
                @SuppressLint("Range") int idRutina = cursorRutina.getInt(cursorRutina.getColumnIndex("ID"));
                cursorRutina.close();
                cursorUsuario.close();
                return idRutina;
            }
        }

        cursorUsuario.close();
        return -1;  // Devuelve -1 si la rutina no se encuentra
    }

    /*public void insertarEjercicioEnRutina(String nombreUsuario, String nombreRutina, Ejercicio ejercicio) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // Buscar el ID del usuario por su nombre
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});
        int idUsuario = -1;
        if (cursorUsuario.moveToFirst()) {
            idUsuario = cursorUsuario.getInt(0);
        }
        cursorUsuario.close();

        // Buscar el ID de la rutina por su nombre y ID de usuario
        Cursor cursorRutina = sqLiteDatabase.rawQuery("SELECT ID FROM Rutinas WHERE NombreRutina = ? AND ID_Usuario = ?", new String[]{nombreRutina, String.valueOf(idUsuario)});
        int idRutina = -1;
        if (cursorRutina.moveToFirst()) {
            idRutina = cursorRutina.getInt(0);
        }
        cursorRutina.close();

        // Verificar que se haya encontrado el ID de la rutina
        if (idRutina != -1) {
            // Utilizar un objeto ContentValues para insertar los datos de forma segura
            ContentValues contentValues = new ContentValues();
            contentValues.put("NombreEjercicio", ejercicio.getNombre());
            contentValues.put("Categoria", ejercicio.getCategoria());
            contentValues.put("Series", ejercicio.getSeries());
            contentValues.put("Repeticiones", ejercicio.getRepeticiones());
            contentValues.put("ID_Rutina", idRutina);

            // Insertar el ejercicio en la base de datos
            sqLiteDatabase.insert("Ejercicios", null, contentValues);
        } else {
            // Manejar el caso en el que no se encuentra la rutina
            // Puedes lanzar una excepción o registrar un mensaje de error
        }
    }*/

    public List<EjercicioEnRutina> consultarEjerciciosPorRutinaYUsuario(int idRutina, int idUsuario) {
        List<EjercicioEnRutina> ejercicios = new ArrayList<>();

        // Verifica que los IDs sean válidos
        if (idRutina <= 0 || idUsuario <= 0) {
            return ejercicios;  // Devuelve una lista vacía si los IDs no son válidos
        }

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            // Consulta SQL con parámetros para ID_Rutina y ID_Usuario
            String sql = "SELECT NombreEjercicio, Categoria, Series, Repeticiones FROM EjerciciosEnRutinas WHERE ID_Rutina = ? AND ID_Usuario = ?";
            String[] selectionArgs = {String.valueOf(idRutina), String.valueOf(idUsuario)};

            try (Cursor cursor = db.rawQuery(sql, selectionArgs)) {
                // Procesa los resultados de la consulta
                while (cursor != null && cursor.moveToNext()) {
                    // Obtiene los valores de NombreEjercicio y Categoria
                    String nombreEjercicio = cursor.getString(cursor.getColumnIndexOrThrow("NombreEjercicio"));
                    String categoria = cursor.getString(cursor.getColumnIndexOrThrow("Categoria"));
                    int series = cursor.getInt(cursor.getColumnIndexOrThrow("Series"));
                    int repeticiones = cursor.getInt(cursor.getColumnIndexOrThrow("Repeticiones"));

                    // Crea un objeto Ejercicio con los valores obtenidos
                    EjercicioEnRutina ejercicio = new EjercicioEnRutina(nombreEjercicio, categoria, series, repeticiones);

                    // Añade el ejercicio a la lista
                    ejercicios.add(ejercicio);
                }
            }
        } catch (Exception e) {
            // Manejar cualquier excepción, por ejemplo, registro un mensaje de error
            e.printStackTrace();
        }

        return ejercicios;  // Devuelve la lista de ejercicios obtenidos
    }


    /*public void insertarEjercicioEnRutina(String nombreUsuario, String nombreRutina, Ejercicio ejercicio) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // Buscar el ID del usuario por su nombre
        Cursor cursorUsuario = sqLiteDatabase.rawQuery("SELECT ID FROM Usuario WHERE NombreUsuario = ?", new String[]{nombreUsuario});
        int idUsuario = -1;
        if (cursorUsuario.moveToFirst()) {
            idUsuario = cursorUsuario.getInt(0);
        }
        cursorUsuario.close();

        // Buscar el ID de la rutina por su nombre y ID de usuario
        Cursor cursorRutina = sqLiteDatabase.rawQuery("SELECT ID FROM Rutinas WHERE NombreRutina = ? AND ID_Usuario = ?", new String[]{nombreRutina, String.valueOf(idUsuario)});
        int idRutina = -1;
        if (cursorRutina.moveToFirst()) {
            idRutina = cursorRutina.getInt(0);
        }
        cursorRutina.close();

        // Verificar que se haya encontrado el ID de la rutina
        if (idRutina != -1) {
            // Verificar si el ejercicio ya existe para el usuario
            Cursor cursorEjercicio = sqLiteDatabase.rawQuery("SELECT ID FROM Ejercicios WHERE NombreEjercicio = ? AND ID_Usuario = ?", new String[]{ejercicio.getNombre(), String.valueOf(idUsuario)});

            if (cursorEjercicio.moveToFirst()) {
                // Si el ejercicio ya existe, actualiza el ID_Rutina
                int idEjercicio = cursorEjercicio.getInt(0);
                ContentValues contentValues = new ContentValues();
                contentValues.put("ID_Rutina", idRutina);
                sqLiteDatabase.update("Ejercicios", contentValues, "ID = ?", new String[]{String.valueOf(idEjercicio)});
            } else {
                // Si el ejercicio no existe, inserta un nuevo ejercicio en la base de datos
                ContentValues contentValues = new ContentValues();
                contentValues.put("NombreEjercicio", ejercicio.getNombre());
                contentValues.put("Categoria", ejercicio.getCategoria());
                contentValues.put("Series", ejercicio.getSeries());
                contentValues.put("Repeticiones", ejercicio.getRepeticiones());
                contentValues.put("ID_Rutina", idRutina);
                contentValues.put("ID_Usuario", idUsuario);
                sqLiteDatabase.insert("Ejercicios", null, contentValues);
            }

            cursorEjercicio.close();
        } else {
            // Manejar el caso en el que no se encuentra la rutina
            //Toast.makeText(this, "Rutina no encontrada para asociar el ejercicio", Toast.LENGTH_SHORT).show();
        }
    }*/

    public void insertarEjercicioEnRutinas(int idRutina, int idUsuario, EjercicioEnRutina ejercico) {
        // Obtener una instancia de la base de datos en modo de escritura
        SQLiteDatabase db = this.getWritableDatabase();

        // Crear un objeto ContentValues para almacenar los valores a insertar
        ContentValues contentValues = new ContentValues();
        contentValues.put("NombreEjercicio", ejercico.getNombre());
        contentValues.put("Categoria", ejercico.getCategoria());
        contentValues.put("Series", ejercico.getSeries());
        contentValues.put("Repeticiones", ejercico.getRepeticiones());
        contentValues.put("ID_Rutina", idRutina);
        contentValues.put("ID_Usuario", idUsuario);

        // Insertar los valores en la tabla EjerciciosEnRutinas
        db.insert("EjerciciosEnRutinas", null, contentValues);

        // Cerrar la base de datos
        db.close();
    }

    public void updateNotas(int idUsuario, int idRutina, String nuevasNotas) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Notas", nuevasNotas);
        String whereClause = "ID = ? AND ID_Usuario = ?";
        String[] whereArgs = new String[] { String.valueOf(idRutina), String.valueOf(idUsuario) };
        sqLiteDatabase.update("Rutinas", contentValues, whereClause, whereArgs);
    }

    @SuppressLint("Range")
    public String getNotas(int idUsuario, int idRutina) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String selectQuery = "SELECT Notas FROM Rutinas WHERE ID = ? AND ID_Usuario = ?";
        String[] whereArgs = new String[] { String.valueOf(idRutina), String.valueOf(idUsuario) };
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, whereArgs);
        String notas = "";
        if (cursor.moveToFirst()) {
            notas = cursor.getString(cursor.getColumnIndex("Notas"));
        }
        cursor.close();
        return notas;
    }

    public void actualizarEjercicioEnRutina(int idUsuario, int idRutina, EjercicioEnRutina ejercicio) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Prepare a ContentValues object with the exercise data to update
        ContentValues values = new ContentValues();
        values.put("Series", ejercicio.getSeries());
        values.put("Repeticiones", ejercicio.getRepeticiones());

        // Execute the update query
        int rowsAffected = db.update(
                "EjerciciosEnRutinas",
                values,
                "ID_Rutina = ? AND ID_Usuario = ?",
                new String[]{String.valueOf(idRutina), String.valueOf(idUsuario)}
        );

        // Close the database
        db.close();
    }

    public String obtenerNombreRutina(int idUsuario, int idRutina) {
        // Crea una nueva instancia de SQLiteDatabase
        SQLiteDatabase db = this.getReadableDatabase();

        // Define la consulta SQL
        String sql = "SELECT NombreRutina FROM Rutinas WHERE ID_Usuario = ? AND ID = ?";

        // Ejecuta la consulta
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idUsuario), String.valueOf(idRutina)});

        // Verifica si la consulta devolvió algún resultado
        if (cursor.moveToFirst()) {
            // Si la consulta devolvió un resultado, obtén el nombre de la rutina
            @SuppressLint("Range") String nombreRutina = cursor.getString(cursor.getColumnIndex("NombreRutina"));

            // Cierra el cursor
            cursor.close();

            // Devuelve el nombre de la rutina
            return nombreRutina;
        } else {
            // Si la consulta no devolvió ningún resultado, devuelve null
            return null;
        }
    }

    public void eliminarEjercicioDeRutina(int idUsuario, int idRutina, String nombreEjercicio) {
        // Crea una nueva instancia de SQLiteDatabase
        SQLiteDatabase db = this.getWritableDatabase();

        // Define la sentencia SQL DELETE
        String sql = "DELETE FROM EjerciciosEnRutinas WHERE ID_Usuario = ? AND ID_Rutina = ? AND NombreEjercicio = ?";

        // Ejecuta la sentencia SQL DELETE
        db.execSQL(sql, new String[]{String.valueOf(idUsuario), String.valueOf(idRutina), nombreEjercicio});
    }

    public boolean existeEjercicioEnRutina(int idUsuario, int idRutina, String nombreEjercicio) {
        // Crea una nueva instancia de SQLiteDatabase
        SQLiteDatabase db = this.getReadableDatabase();

        // Define la consulta SQL
        String sql = "SELECT * FROM EjerciciosEnRutinas WHERE ID_Usuario = ? AND ID_Rutina = ? AND NombreEjercicio = ?";

        // Ejecuta la consulta
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idUsuario), String.valueOf(idRutina), nombreEjercicio});

        // Verifica si la consulta devolvió algún resultado
        boolean existe = cursor.moveToFirst();

        // Cierra el cursor
        cursor.close();

        // Devuelve true si el ejercicio existe en la rutina, false en caso contrario
        return existe;
    }

    public void eliminarRutina(int idUsuario, int idRutina) {
        // Crea una nueva instancia de SQLiteDatabase
        SQLiteDatabase db = this.getWritableDatabase();

        // Define la sentencia SQL DELETE
        String sql = "DELETE FROM Rutinas WHERE ID_Usuario = ? AND ID = ?";

        // Ejecuta la sentencia SQL DELETE
        db.execSQL(sql, new String[]{String.valueOf(idUsuario), String.valueOf(idRutina)});
    }
}