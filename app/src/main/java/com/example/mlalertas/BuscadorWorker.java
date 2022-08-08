package com.example.mlalertas;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Map;

public class BuscadorWorker extends Worker {

    public final static String ARTICULO_NUEVO = "com.example.mlalertas.ARTICULO_NUEVO";

    public BuscadorWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        Map<String,Object> map = getInputData().getKeyValueMap();
        BaseDatos baseDatos = (BaseDatos)map.get("baseDatos");
        Buscador buscador = new Buscador(baseDatos);
        buscador.buscarArticulos();
        return Result.success();
    }

}
