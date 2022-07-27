package com.example.mlalertas;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mlconsulta.Articulo;
import mlconsulta.MLBuscar;
import mlconsulta.MLSitio;

public class Buscador {

    BaseDatos baseDatos;

    public Buscador(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
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
            String palabras = busqueda.getPalabras();
            buscarArticulo(palabras);
        }
    }

    private void buscarArticulo(String palabras) {
        try {
            MLSitio mlsitio = new MLSitio();
            System.out.print(mlsitio.getNombreSitio(MLSitio.IDSitio.MLA) + "\n");
            MLBuscar mlbuscar = new MLBuscar();
            mlbuscar.setSitio(MLSitio.IDSitio.MLA);
            mlbuscar.setPalabras(palabras);
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

    }

}
