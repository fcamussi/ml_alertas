package com.example.mlalertas;

import java.util.ArrayList;

public class Busqueda {

    private int id = -1;
    private ArrayList<String> palabrasList = null;
    private boolean articuloNuevo = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getPalabrasList() {
        return palabrasList;
    }

    public void setPalabrasList(ArrayList<String> palabrasList) {
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
        String palabrasSplited[] = palabras.split("\\s+");
        for (String palabra : palabrasSplited) {
            palabrasList.add(palabra);
        }
    }

}
