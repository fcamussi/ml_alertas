package com.example.mlalertas;

import java.util.List;

import mlconsulta.Articulo;
import mlconsulta.MLBuscar;
import mlconsulta.MLSitio;

public class Buscador {

    BaseDatos baseDatos;

    public Buscador(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    public void buscar() {
        List<Busqueda> busquedas = baseDatos.getBusquedas();
        for (Busqueda busqueda : busquedas) {
            MLBuscar mlBuscar = new MLBuscar();
            // set Agente
            mlBuscar.setSitio(MLSitio.IDSitio.MLA);
            mlBuscar.setPalabras(busqueda.getPalabrasList());
            try {
                mlBuscar.BuscarProducto();
                // podria armar una lista completa con los articulos de todas las busquedas y
                // pasarselo, es mas facil, y que te tire la cantidad de nuevos articulos: el problema es id_busqueda
                baseDatos.addArticulos(busqueda.getId(), mlBuscar.getArticulos());
            } catch (Exception e) {
            }
        }
    }

}

/*
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("data", String.valueOf(NumeroAleatorio.get()));
        intent.setPackage(getApplicationContext().getPackageName()); // para que solo ésta aplicación lo pueda recibir
        getApplicationContext().sendBroadcast(intent);
 */