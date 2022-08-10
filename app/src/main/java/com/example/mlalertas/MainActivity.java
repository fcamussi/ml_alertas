package com.example.mlalertas;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> agregarBusquedaLauncher;
    private BaseDatos baseDatos;
    private Buscador buscador;
    private Cursor cursor;
    private ListView lvBusquedas;
    private BusquedaCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseDatos = new BaseDatos(this);
        lvBusquedas = findViewById(R.id.lvBusquedas);
        cursor = baseDatos.getCursorForAdapterBusqueda();
        adapter = new BusquedaCursorAdapter(this, cursor);
        lvBusquedas.setAdapter(adapter);

        agregarBusquedaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    agregarBusquedaResult(result.getResultCode(), data);
                });

        lvBusquedas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                abrirArticulos(Integer.parseInt(view.getTag().toString()));
            }
        });

        PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(BuscadorWorker.class,
                15, TimeUnit.MINUTES).build();
        // ExistingPeriodicWorkPolicy.KEEP: conserva el trabajo existente e ignora el nuevo
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("Buscador",
                ExistingPeriodicWorkPolicy.REPLACE, work);
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
            Busqueda busqueda = new Busqueda();
            busqueda.setPalabras(palabras);
            baseDatos.addBusqueda(busqueda);
            cursor = baseDatos.getCursorForAdapterBusqueda();
            adapter.changeCursor(cursor);
            Toast.makeText(this, "Búsqueda agregada", Toast.LENGTH_SHORT).show();
        }
    }

    public void abrirArticulos(int id_busqueda) {
        //Toast.makeText(this, view.getTag().toString(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ArticulosActivity.class);
        intent.putExtra("id_busqueda", id_busqueda);
        startActivity(intent);
    }

}