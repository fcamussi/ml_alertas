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
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> agregarBusquedaLauncher;
    private BaseDatos baseDatos;
    private Buscador buscador;
    private Cursor cursor;
    private ProgressBar pbBusquedas;
    private ListView lvBusquedas;
    private BusquedaCursorAdapter adapter;
    private int activeSearches = 0;
    private BroadcastReceiver broadcastReceiver;

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

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int id_busqueda = intent.getIntExtra("id_busqueda", 0);
                Busqueda busqueda = baseDatos.getBusqueda(id_busqueda);
                busqueda.setVisible(true);
                baseDatos.updateBusqueda(busqueda);
                cursor = baseDatos.getCursorForAdapterBusqueda();
                adapter.changeCursor(cursor);
                activeSearches--;
                if (activeSearches == 0) { // Espero a que finalicen todas las búsquedas agregadas
                    pbBusquedas.setVisibility(View.GONE);
                }
            }
        };
        IntentFilter filter = new IntentFilter(BroadcastSignal.ONE_SEARCH_FINISHED);
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

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(PeriodicSearcherWorker.class,
                15, TimeUnit.MINUTES).build();
        // ExistingPeriodicWorkPolicy.KEEP: conserva el trabajo existente e ignora el nuevo
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("PeriodicSearchWorker",
                ExistingPeriodicWorkPolicy.KEEP, workRequest);
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
            int id_busqueda = baseDatos.addBusqueda(busqueda);
            Data workerData = new Data.Builder().putInt("id_busqueda", id_busqueda).build();
            activeSearches++;
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(OneSearchWorker.class).setInputData(workerData).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            pbBusquedas.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Agregando búsqueda...", Toast.LENGTH_SHORT).show();
        }
    }

    public void mostrarArticulos(int id_busqueda) {
        Intent intent = new Intent(this, ArticulosActivity.class);
        intent.putExtra("id_busqueda", id_busqueda);
        startActivity(intent);
    }

}