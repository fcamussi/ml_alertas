package com.example.mlalertas;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BuscadorWorker extends Worker {

    public final static String ARTICULO_NUEVO = "com.example.mlalertas.ARTICULO_NUEVO";

    public BuscadorWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        BaseDatos baseDatos = new BaseDatos(getApplicationContext());
        Buscador buscador = new Buscador(baseDatos);
        int nuevos = buscador.buscar();
        if (nuevos > 0) {
            // enviar noti? alerta?
            /* creo que es asi la cosa:
                enviar siempre el broadcast, si la app esta abierta, entre resumen y
                stop (verificar) se recibe el alerta para que se actualicen los cursores
                En caso de que no esté abierta se muestra una notificacion y da la opción
                de abrir la aplicacion, cada vez que se abra deberian actualizar los cursores
                ¿no lo hacemos ya?
                podria olvidarme de la broadcast, no seria grave notificar siempre que hay algo
                nuevo
                seria bueno cada vez que se agrega una busqueda cerrar el worker y volver a abrirlo
                así no escribo a la BD desde el activity
             */
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
