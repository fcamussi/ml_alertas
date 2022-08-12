package com.example.mlalertas;

import android.content.Context;
import android.content.Intent;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PeriodicSearcherWorker extends Worker {

    public PeriodicSearcherWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        BaseDatos baseDatos = new BaseDatos(getApplicationContext());
        Buscador buscador = new Buscador(baseDatos);

        int nuevos = buscador.buscar();
        if (nuevos > 0) {

        }

        return Result.success();
    }

/*
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("data", String.valueOf(NumeroAleatorio.get()));
        intent.setPackage(getApplicationContext().getPackageName()); // para que solo ésta aplicación lo pueda recibir
        getApplicationContext().sendBroadcast(intent);
 */

}
