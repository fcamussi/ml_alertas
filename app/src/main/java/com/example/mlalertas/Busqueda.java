package com.example.mlalertas;

public class Busqueda {

    private String palabras;
    private boolean articuloNuevo = false;

    public Busqueda(String palabras, boolean articuloNuevo) {
        this.palabras = palabras;
        this.articuloNuevo = articuloNuevo;
    }

    public String getPalabras() {
        return palabras;
    }

    public void setPalabras(String palabras) {
        this.palabras = palabras;
    }

    public boolean isArticuloNuevo() {
        return articuloNuevo;
    }

    public void setArticuloNuevo(boolean articuloNuevo) {
        this.articuloNuevo = articuloNuevo;
    }

}
