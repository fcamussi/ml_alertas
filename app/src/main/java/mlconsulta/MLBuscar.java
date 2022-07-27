package mlconsulta;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import mlconsulta.MLSitio.IDSitio;

/**
 * Clase para buscar productos en Mercado Libre
 * @author Fernando Camussi
 */

public class MLBuscar {

	/* Variables privadas */
	private String sitio;
	private String estado; // provincias
	private ArrayList<String> palabrasList;
	private boolean filtrado = true;
	private ArrayList<Articulo> articulosList;

	/**
	 * Constructor
	 */
	public MLBuscar()
	{
		palabrasList = new ArrayList<>();
		articulosList = new ArrayList<>();
	}

	/**
	 * Setea el sitio donde se realiza la búsqueda: MLA, MLB, etc.
	 * @param id El id del sitio, de tipo IDSitio
	 */
	public void setSitio(IDSitio id) {
		this.sitio = id.name();
	}

	/**
	 * Setea el estado/provincia
	 * @param estado String con el nombre del estado 
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	/**
	 * Setea las palabras a buscar
	 * @param palabras Un String con las palabras separadas por un espacio
	 */
	public void setPalabras(String palabras) {
		this.palabrasList.clear();
		String palabrasSplited [] = palabras.split("\\s+");
		for (String palabra : palabrasSplited) {
			this.palabrasList.add(palabra.toLowerCase());
		}
	}
	
	/**
	 * Setea si se hace un filtrado para que el título del artículo contenga
	 * todas las palabras
	 * @param filtrado true para que se filtre, false para que no. Por defecto es true
	 */
	public void setFiltrado(boolean filtrado) {
		this.filtrado = filtrado;
	}

	/**
	 * Realiza la búsqueda
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
	 * @return lista de artículos de tipo ListaArticulo
	 */
	public ArrayList<Articulo> getArticulos() {
		return articulosList;
	}


	/* Métodos privados */
	
	private void cargarRegistros(JSONArray jsonArr) throws Exception {
		for (int c = 0; c < jsonArr.length(); c++) {
			Articulo articulo = new Articulo();
			articulo.id = jsonArr.getJSONObject(c).get("id").toString();
			articulo.title = jsonArr.getJSONObject(c).get("title").toString().toLowerCase();
			articulo.permalink = jsonArr.getJSONObject(c).get("permalink").toString();
			if (this.filtrado) {
				/* Chequea que cada palabra clave esté contenido en el título del artículo */
				boolean coincide = true;
				for (String pc : this.palabrasList) {
					if (!articulo.title.contains(pc)) {
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