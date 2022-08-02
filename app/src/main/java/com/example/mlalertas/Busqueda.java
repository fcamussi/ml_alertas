package com.example.mlalertas;

import java.util.ArrayList;

public class Busqueda {

    private ArrayList<String> palabrasList;
    private boolean articuloNuevo = false;

    public Busqueda(String palabras, boolean articuloNuevo) {
        palabrasList = new ArrayList<>();
        String palabrasSplited [] = palabras.split("\\s+");
        for (String palabra : palabrasSplited) {
            palabrasList.add(palabra);
        }
        this.articuloNuevo = articuloNuevo;
    }

    public ArrayList<String> getPalabras() {
        return palabrasList;
    }

    public String getPalabrasAsString() {
        String palabras = palabrasList.get(0);
        for (int c = 1; c < palabrasList.size(); c++) {
            palabras = palabras + " " + palabrasList.get(c);
        }
        return palabras;
    }

    public boolean isArticuloNuevo() {
        return articuloNuevo;
    }

}
