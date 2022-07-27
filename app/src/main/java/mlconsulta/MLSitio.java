package mlconsulta;


public class MLSitio {

	private String [] nombreSitio = {
			"Argentina",
			"Brasil",
			"Chile",
			"Colombia",
			"México",
			"Perú",
			"Uruguay",
			"Venezuela"
		};

	public enum IDSitio {
		MLA,
		MLB,
		MLC,
		MCO,
		MLM,
		MPE,
		MLU,
		MLV  
	}

	public String getNombreSitio(IDSitio id) {
		return nombreSitio[id.ordinal()];
	}

}


// https://api.mercadolibre.com/sites <- paises
// https://api.mercadolibre.com/countries/AR <- estado/provincia