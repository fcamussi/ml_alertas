package mlconsulta;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import mlconsulta.MLSitio.IDSitio;

/**
 * Clase para buscar productos en Mercado Libre
 *
 * @author Fernando Camussi
 */

public class MLBuscar {

    private final List<String> palabrasList;
    private final List<Articulo> articulosList;
    private String sitio;
    private String estado; // provincias
    private boolean filtrado;

    /**
     * Constructor
     */
    public MLBuscar() {
        palabrasList = new ArrayList<>();
        articulosList = new ArrayList<>();
        sitio = "";
        estado = "";
        filtrado = true;
    }

    /**
     * Setea el sitio donde se realiza la búsqueda: MLA, MLB, etc.
     *
     * @param id El id del sitio, de tipo IDSitio
     */
    public void setSitio(IDSitio id) {
        this.sitio = id.name();
    }

    /**
     * Setea el estado/provincia
     *
     * @param estado String con el nombre del estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Setea las palabras a buscar
     *
     * @param palabrasList Una lista de String con las palabras
     */
    public void setPalabras(List<String> palabrasList) {
        this.palabrasList.clear();
        for (String palabra : palabrasList) {
            this.palabrasList.add(palabra.toLowerCase());
        }
    }

    /**
     * Setea si se hace un filtrado para que el título del artículo contenga
     * todas las palabras
     *
     * @param filtrado true para que se filtre, false para que no. Por defecto es true
     */
    public void setFiltrado(boolean filtrado) {
        this.filtrado = filtrado;
    }

    /**
     * Realiza la búsqueda
     *
     * @throws Exception
     */
    public void BuscarProducto() throws Exception {
        String resultado = ConsultaURL.consultar(this.construirURLStr(0));
        JSONObject jsonObj = new JSONObject(resultado);
        int limit = jsonObj.getJSONObject("paging").getInt("limit");
        int total = jsonObj.getJSONObject("paging").getInt("total");

        this.articulosList.clear();
        this.cargarRegistros(jsonObj.getJSONArray("results"));
        for (int offset = limit; offset < total; offset += limit) {
            resultado = ConsultaURL.consultar(this.construirURLStr(offset));
            jsonObj = new JSONObject(resultado);
            this.cargarRegistros(jsonObj.getJSONArray("results"));
        }
    }

    /**
     * Obtiene la lista de artículos encontrados
     *
     * @return lista de artículos
     */
    public List<Articulo> getArticulos() {
        return articulosList;
    }


    /* Métodos privados */

    private void cargarRegistros(JSONArray jsonArr) throws Exception {
        for (int c = 0; c < jsonArr.length(); c++) {
            String id = jsonArr.getJSONObject(c).get("id").toString();
            String title = jsonArr.getJSONObject(c).get("title").toString().toLowerCase();
            String permalink = jsonArr.getJSONObject(c).get("permalink").toString();
            Articulo articulo = new Articulo(id, title, permalink);
            if (this.filtrado) {
                /* Chequea que cada palabra esté contenida en el título del artículo */
                boolean coincide = true;
                for (String palabra : this.palabrasList) {
                    if (!articulo.getTitle().contains(palabra)) {
                        coincide = false;
                    }
                }
                if (coincide) articulosList.add(articulo);
            } else {
                articulosList.add(articulo);
            }
        }
    }

    private String construirURLStr(int offset) throws Exception {
        String url_str = "https://api.mercadolibre.com/sites/" + this.sitio + "/search?q=";
        String producto = this.palabrasList.get(0);
        for (int c = 1; c < this.palabrasList.size(); c++) {
            producto = producto + " " + this.palabrasList.get(c);
        }
        producto = URLEncoder.encode(producto, "UTF-8");
        url_str = url_str + producto;
        url_str = url_str + "&offset=" + offset;
        return url_str;
    }

}