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

import androidx.appcompat.app.AppCompatActivity;

public class ArticulosActivity extends AppCompatActivity {

    //ActivityResultLauncher<Intent> agregarBusquedaLauncher;
    private BaseDatos baseDatos;
    private Cursor cursor;
    private ProgressBar pbArticulos;
    private ListView lvArticulos;
    private ArticuloCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulos);

        int id_busqueda = getIntent().getIntExtra("id_busqueda", 0);
        baseDatos = new BaseDatos(this);
        pbArticulos = findViewById(R.id.pbArticulos);
        pbArticulos.setIndeterminate(true);
        pbArticulos.setVisibility(View.GONE);
        lvArticulos = findViewById(R.id.lvArticulos);
        cursor = baseDatos.getCursorForAdapterArticulo(id_busqueda);
        adapter = new ArticuloCursorAdapter(this, cursor);
        lvArticulos.setAdapter(adapter);
        Busqueda busqueda = baseDatos.getBusqueda(id_busqueda);
        getSupportActionBar().setTitle(busqueda.getPalabras());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pbArticulos.setVisibility(View.VISIBLE);
            }
        };
        IntentFilter filter = new IntentFilter(BuscadorWorker.BUSCANDO);
        this.registerReceiver(broadcastReceiver, filter);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cursor = baseDatos.getCursorForAdapterArticulo(id_busqueda);
                adapter.changeCursor(cursor);
                pbArticulos.setVisibility(View.GONE);
            }
        };
        filter = new IntentFilter(BuscadorWorker.BUSQUEDA_FINALIZADA);
        this.registerReceiver(broadcastReceiver, filter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}