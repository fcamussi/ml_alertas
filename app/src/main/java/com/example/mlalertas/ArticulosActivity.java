package com.example.mlalertas;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;

public class ArticulosActivity extends AppCompatActivity {

    //ActivityResultLauncher<Intent> agregarBusquedaLauncher;
    private BaseDatos baseDatos;
    private Cursor cursor;
    private ProgressBar pbArticulos;
    private ListView lvArticulos;
    private ArticuloCursorAdapter adapter;
    private int id_busqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulos);

        id_busqueda = getIntent().getIntExtra("id_busqueda", 0);
        baseDatos = new BaseDatos(this);
        lvArticulos = findViewById(R.id.lvArticulos);
        cursor = baseDatos.getCursorForAdapterArticulo(id_busqueda);
        adapter = new ArticuloCursorAdapter(this, cursor);
        lvArticulos.setAdapter(adapter);
        Busqueda busqueda = baseDatos.getBusqueda(id_busqueda);
        getSupportActionBar().setTitle(busqueda.getPalabras());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cursor = baseDatos.getCursorForAdapterArticulo(id_busqueda);
        adapter.changeCursor(cursor);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}