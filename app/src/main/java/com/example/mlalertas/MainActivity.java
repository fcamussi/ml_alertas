package com.example.mlalertas;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private ProgressBar pbBusquedas;
    private ListView lvBusquedas;
    private BusquedaCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseDatos = new BaseDatos(this);
        pbBusquedas = findViewById(R.id.pbBusquedas);
        pbBusquedas.setIndeterminate(true);
        pbBusquedas.setVisibility(View.GONE);
        lvBusquedas = findViewById(R.id.lvBusquedas);
        cursor = baseDatos.getCursorForAdapterBusqueda();
        adapter = new BusquedaCursorAdapter(this, cursor);
        lvBusquedas.setAdapter(adapter);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pbBusquedas.setVisibility(View.VISIBLE);
            }
        };
        IntentFilter filter = new IntentFilter(BuscadorWorker.BUSCANDO);
        this.registerReceiver(broadcastReceiver, filter);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pbBusquedas.setVisibility(View.GONE);
            }
        };
        filter = new IntentFilter(BuscadorWorker.BUSQUEDA_FINALIZADA);
        this.registerReceiver(broadcastReceiver, filter);

        agregarBusquedaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    agregarBusquedaResult(result.getResultCode(), data);
                });

        lvBusquedas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mostrarArticulos(Integer.parseInt(view.getTag().toString()));
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

    public void mostrarArticulos(int id_busqueda) {
        Intent intent = new Intent(this, ArticulosActivity.class);
        intent.putExtra("id_busqueda", id_busqueda);
        startActivity(intent);
    }

}