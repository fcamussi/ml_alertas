package com.example.mlalertas;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class ArticulosActivity extends AppCompatActivity {

    //ActivityResultLauncher<Intent> agregarBusquedaLauncher;
    private BaseDatos baseDatos;
    private Cursor cursor;
    private ListView lvArticulos;
    private ArticuloCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulos);

        int id_busqueda = getIntent().getIntExtra("id_busqueda", 0);

        baseDatos = new BaseDatos(this);
        lvArticulos = findViewById(R.id.lvArticulos);
        cursor = baseDatos.getCursorForAdapterArticulo(id_busqueda);
        adapter = new ArticuloCursorAdapter(this, cursor);
        lvArticulos.setAdapter(adapter);
    }

}