package mlconsulta;

import mlconsulta.MLSitio.IDSitio;


public class Main {

	public static void main(String[] args) {
		try {
			MLSitio mlsitio = new MLSitio();
			System.out.print(mlsitio.getNombreSitio(IDSitio.MLA) + "\n");
			MLBuscar mlbuscar = new MLBuscar();
			mlbuscar.setSitio(IDSitio.MLA);
			String[] palabrasClave = { "MSX", "talent" };
			mlbuscar.setPalabrasClave(palabrasClave);
			mlbuscar.setFiltrado(true);
			mlbuscar.BuscarProducto();
			for (Articulo articulo : mlbuscar.getArticulos()) {
				System.out.print(articulo.permalink + "\n");
			}
			System.out.print(mlbuscar.getArticulos().size() + "\n");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}