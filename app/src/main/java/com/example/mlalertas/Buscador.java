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

    public int buscar() {
        List<Busqueda> busquedas = baseDatos.getAllBusqueda();
        MLBuscar mlBuscar = new MLBuscar();
        // set Agente
        int nuevos = 0;
        for (Busqueda busqueda : busquedas) {
            mlBuscar.setSitio(MLSitio.IDSitio.MLA);
            mlBuscar.setPalabras(busqueda.getPalabrasList());
            try {
                mlBuscar.BuscarProducto();
                nuevos += baseDatos.addArticulos(busqueda.getId(), mlBuscar.getArticulos(), true);
            } catch (Exception e) {
            }
        }
        return nuevos;
    }

}
