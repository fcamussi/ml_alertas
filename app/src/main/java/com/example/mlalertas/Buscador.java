package com.example.mlalertas;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mlconsulta.Articulo;
import mlconsulta.MLBuscar;
import mlconsulta.MLSitio;

public class Buscador {

    BaseDatos baseDatos;
    MLBuscar mlBuscar;
    MLSitio mlSitio;

    public Buscador(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
        mlSitio = new MLSitio();
        mlBuscar = new MLBuscar();
        // set Agente
    }

    public void iniciar() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    buscarArticulos();
                }
            }
        });
    }

    private void buscarArticulos() {
        ArrayList<Busqueda> busquedas = baseDatos.getBusquedas();
        for (Busqueda busqueda: busquedas) {
            buscarArticulo(busqueda.getPalabras());
        }
    }

    private void buscarArticulo(ArrayList<String> palabras) {
        try {
            mlBuscar.setSitio(MLSitio.IDSitio.MLA);
            mlBuscar.setPalabras(palabras);
            mlBuscar.BuscarProducto();

            for (Articulo articulo : mlBuscar.getArticulos()) {
                System.out.print(articulo.getPermalink() + "\n");
            }
            System.out.print(mlBuscar.getArticulos().size() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
