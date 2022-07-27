package mlconsulta;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import mlconsulta.MLSitio.IDSitio;

/**
 * Clase para buscar productos en Mercado Libre
 * @author Fernando Camussi
 */

public class MLBuscar {

	/**
	 * Constructor
	 */
	public MLBuscar() {
		articulos = new ListaArticulo();
	}

	/* Variables privadas */
	private String sitio;
	private String estado;
	private String[] palabrasClave;
	private boolean filtrado = true;
	private ListaArticulo articulos;

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
	 * Setea las palabras clave a buscar
	 * @param palabrasClave Palabras clave, un array de String
	 */
	public void setPalabrasClave(String[] palabrasClave) {
		for (int c = 0; c < palabrasClave.length; c++) {
			palabrasClave[c] = palabrasClave[c].toLowerCase();
		}
		this.palabrasClave = palabrasClave;
	}
	
	/**
	 * Setea si se hace un filtrado para que el título del artículo contenga
	 * todas las palabras claves
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

		this.articulos.clear();
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
	public ListaArticulo getArticulos() {
		return articulos;
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
				for (String pc : this.palabrasClave) {
					if (!articulo.title.contains(pc)) {
						coincide = false;
					}
				}
				if (coincide) articulos.add(articulo);
			} else {
				articulos.add(articulo);
			}
		}
	}

	private String construirURLStr(int offset) throws Exception {
		String url_str = "https://api.mercadolibre.com/sites/" + this.sitio + "/search?q=";
		String producto = this.palabrasClave[0];
		for (int c = 1; c < this.palabrasClave.length; c++) {
			producto = producto + " " + this.palabrasClave[c];
		}
		producto = URLEncoder.encode(producto, "UTF-8");
		url_str = url_str + producto;
		url_str = url_str + "&offset=" + offset;
		return url_str;
	}

}