package com.example.mlalertas;

import java.util.ArrayList;

import mlconsulta.Articulo;
import mlconsulta.MLBuscar;
import mlconsulta.MLSitio;

public class Buscador {

    BaseDatos baseDatos;

    public Buscador(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    public void buscarArticulos() {
        ArrayList<Busqueda> busquedas = baseDatos.getBusquedas();
        for (Busqueda busqueda : busquedas) {
            buscarArticulo(busqueda.getPalabrasList());
        }
    }

    private void buscarArticulo(ArrayList<String> palabrasList) {
        MLBuscar mlBuscar = new MLBuscar();
        // set Agente
        try {
            mlBuscar.setSitio(MLSitio.IDSitio.MLA);
            mlBuscar.setPalabras(palabrasList);
            mlBuscar.BuscarProducto();
            for (Articulo articulo : mlBuscar.getArticulos()) {
                System.out.print(articulo.getPermalink() + "\n");
            }
            System.out.print(mlBuscar.getArticulos().size() + "\n");
        } catch (Exception e) {
        }
    }

}

/*
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("data", String.valueOf(NumeroAleatorio.get()));
        intent.setPackage(getApplicationContext().getPackageName()); // para que solo ésta aplicación lo pueda recibir
        getApplicationContext().sendBroadcast(intent);
 */