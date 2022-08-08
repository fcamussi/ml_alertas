package com.example.mlalertas;

import java.util.ArrayList;
import java.util.List;

public class Busqueda {

    private int id;
    private List<String> palabrasList;
    private boolean articuloNuevo;

    public Busqueda() {
        int id = -1;
        List<String> palabrasList = null;
        boolean articuloNuevo = false;
    }

    public Busqueda(int id, List<String> palabrasList, boolean articuloNuevo) {
        this.id = id;
        this.palabrasList = palabrasList;
        this.articuloNuevo = articuloNuevo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getPalabrasList() {
        return palabrasList;
    }

    public void setPalabrasList(List<String> palabrasList) {
        this.palabrasList = palabrasList;
    }

    public boolean isArticuloNuevo() {
        return articuloNuevo;
    }

    public void setArticuloNuevo(boolean articuloNuevo) {
        this.articuloNuevo = articuloNuevo;
    }

    public String getPalabras() {
        String palabras = palabrasList.get(0);
        for (int c = 1; c < palabrasList.size(); c++) {
            palabras = palabras + " " + palabrasList.get(c);
        }
        return palabras;
    }

    public void setPalabras(String palabras) {
        palabrasList = new ArrayList<>();
        String[] palabrasSplited = palabras.split("\\s+");
        for (String palabra : palabrasSplited) {
            palabrasList.add(palabra);
        }
    }

}
