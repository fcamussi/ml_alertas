package com.example.mlalertas;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mlconsulta.Articulo;
import mlconsulta.MLBuscar;
import mlconsulta.MLSitio;

public class MainActivity extends AppCompatActivity {

    private ListView lvBusquedas;
    private BusquedaCursorAdapter adapter;
    AdminSQLiteOpenHelper admin;
    SQLiteDatabase BD;
    Cursor cursor;
    ActivityResultLauncher<Intent> agregarBusquedaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvBusquedas = findViewById(R.id.lvBusquedas);
        admin = new AdminSQLiteOpenHelper(this, "BD", null, 1);
        BD = admin.getWritableDatabase();
        actualizarCursor();
        adapter = new BusquedaCursorAdapter(this, cursor);
        lvBusquedas.setAdapter(adapter);

        agregarBusquedaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    agregarBusquedaResult(result.getResultCode(), data);
                });
    }

    private void actualizarCursor() {
        cursor = BD.rawQuery("SELECT PALABRAS AS _id,PALABRAS FROM BUSQUEDAS", null);
        if (adapter != null) {
            adapter.changeCursor(cursor);
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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                //Background work here
                try {
                    MLSitio mlsitio = new MLSitio();
                    System.out.print(mlsitio.getNombreSitio(MLSitio.IDSitio.MLA) + "\n");
                    MLBuscar mlbuscar = new MLBuscar();
                    mlbuscar.setSitio(MLSitio.IDSitio.MLA);
                    String[] palabrasClave = { "MSX", "talent" };
                    mlbuscar.setPalabrasClave(palabrasClave);
                    mlbuscar.setFiltrado(true);
                    mlbuscar.BuscarProducto();
                    for (Articulo articulo : mlbuscar.getArticulos()) {
                        System.out.print(articulo.permalink + "\n");
                    }
                    System.out.print(mlbuscar.getArticulos().size() + "\n");

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                    }
                });
            }
        });
    }

    public void agregarBusquedaResult(int result, Intent data) {
        if (result == Activity.RESULT_OK) {
            String palabras = data.getStringExtra("palabras");
            ContentValues registro = new ContentValues();
            registro.put("PALABRAS", palabras);
            long res = BD.insert("BUSQUEDAS", null, registro);
            actualizarCursor();
            if (res > 0) {
                Toast.makeText(this, "Búsqueda agregada", Toast.LENGTH_SHORT).show();
            }
        }
    }

}