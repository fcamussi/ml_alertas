package com.example.mlalertas;

import android.content.Context;
import android.content.Intent;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BuscadorWorker extends Worker {

    public final static String ARTICULO_NUEVO = "com.example.mlalertas.ARTICULO_NUEVO";
    public final static String BUSCANDO = "com.example.mlalertas.BUSCANDO";
    public final static String BUSQUEDA_FINALIZADA = "com.example.mlalertas.BUSQUEDA_FINALIZADA";

    public BuscadorWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        BaseDatos baseDatos = new BaseDatos(getApplicationContext());
        Buscador buscador = new Buscador(baseDatos);
        Intent intent = new Intent(BUSCANDO);
        intent.setPackage(getApplicationContext().getPackageName());
        getApplicationContext().sendBroadcast(intent);
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
                si mato y relanzo el worker tarda en hacer las busquedas y el usuario queda esperando
                quizas deberia usar otro worker... puedo matar el primero y levantarlo cuando retorne
                puedo mandar 2 broadcast, buscando, y encontrado... buscando pone la barrita loca
                encontrado ya sabemos
                el tema esta en agregar, deberia especificarle al worker que busque uno solo, pero
                como espero y lo vuelvo a lanzar para que busque todos?
                por ahora no te vuelvas muy loco, que funcione y sea solida
                no deberia matar y lanzar el otro y esperar, lanzar otro, que ese otro cuando termine
                me avise que quite la barrita loca? ambos pueden mandar señal para la barrita loca
                si hay 2 workers me puede uno decir que finalizo y el otro esta buscando
                SIMPLIFICAR EL DISEÑO! PENSAR COMO SERIA SIMPLE, el problema esta en tener que
                hacer la busqueda al agregar, la otra app creo que lo resuelve haciendo la busqueda
                antes de agregar, pero termina siendo complicado creo
             */
        }
        intent = new Intent(BUSQUEDA_FINALIZADA);
        intent.setPackage(getApplicationContext().getPackageName());
        getApplicationContext().sendBroadcast(intent);
        return Result.success();
    }

/*
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("data", String.valueOf(NumeroAleatorio.get()));
        intent.setPackage(getApplicationContext().getPackageName()); // para que solo ésta aplicación lo pueda recibir
        getApplicationContext().sendBroadcast(intent);
 */

}
