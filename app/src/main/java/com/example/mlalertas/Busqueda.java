package com.example.mlalertas;

import java.util.ArrayList;
import java.util.List;

public class Busqueda {

    private int id;
    private List<String> palabrasList;
    private boolean articuloNuevo;
    private boolean visible;
    private boolean borrado;

    public Busqueda() {
        int id = -1;
        List<String> palabrasList = null;
        articuloNuevo = false;
        visible = false;
        borrado = false;
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

    public boolean isBorrado() {
        return borrado;
    }

    public void setBorrado(boolean borrado) {
        this.borrado = borrado;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
