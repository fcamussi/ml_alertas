package com.example.mlalertas;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ListView lvBusquedas;
    private BusquedaCursorAdapter adapter;
    ActivityResultLauncher<Intent> agregarBusquedaLauncher;
    BaseDatos baseDatos;
    Buscador buscador;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseDatos = new BaseDatos(this);
        lvBusquedas = findViewById(R.id.lvBusquedas);
        cursor = baseDatos.getCursorBusquedas();
        adapter = new BusquedaCursorAdapter(this, cursor);
        lvBusquedas.setAdapter(adapter);

        agregarBusquedaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    agregarBusquedaResult(result.getResultCode(), data);
                });

        buscador = new Buscador(baseDatos);
        buscador.iniciar();
    }

    private void actualizarCursor() {
        cursor = baseDatos.getCursorBusquedas();
        if (adapter != null) {
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAgregar:
                agregarBusqueda();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void agregarBusqueda() {
        Intent data = new Intent(this, BusquedaActivity.class);
        agregarBusquedaLauncher.launch(data);
    }

    public void agregarBusquedaResult(int result, Intent data) {
        if (result == Activity.RESULT_OK) {
            String palabras = data.getStringExtra("palabras");
            Busqueda busqueda = new Busqueda(palabras, false);
            baseDatos.addBusqueda(busqueda);
            cursor = baseDatos.getCursorBusquedas();
            adapter.changeCursor(cursor);
            Toast.makeText(this, "Búsqueda agregada", Toast.LENGTH_SHORT).show();
        }
    }

}