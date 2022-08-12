package com.example.mlalertas;

import android.content.Context;
import android.content.Intent;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import mlconsulta.MLBuscar;
import mlconsulta.MLSitio;

public class OneSearchWorker extends Worker {

    public OneSearchWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        int id_busqueda = getInputData().getInt("id_busqueda",0);
        BaseDatos baseDatos = new BaseDatos(getApplicationContext());
        MLBuscar mlBuscar = new MLBuscar();

        Busqueda busqueda = baseDatos.getBusqueda(id_busqueda);
        mlBuscar.setSitio(MLSitio.IDSitio.MLA);
        mlBuscar.setPalabras(busqueda.getPalabrasList());
        try {
            mlBuscar.BuscarProducto();
            baseDatos.addArticulos(id_busqueda, mlBuscar.getArticulos(), false);
        } catch (Exception e) {
        }

        Intent intent = new Intent(BroadcastSignal.ONE_SEARCH_FINISHED);
        intent.putExtra("id_busqueda", id_busqueda);
        intent.setPackage(getApplicationContext().getPackageName());
        getApplicationContext().sendBroadcast(intent);

        return Result.success();
    }
    
}
