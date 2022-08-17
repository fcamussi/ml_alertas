package mlsearcher;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase para obtener los sitios (paises) y los estados (provincias)
 *
 * @author Fernando Camussi
 */
public class MLSite {

    Map<String, String> siteMap;
    private String agent;

    /**
     * Constructor
     */
    public MLSite() {
        siteMap = new HashMap<>();
        agent = "MLSearcher";
    }

    /**
     * Setea el agente HTTP
     *
     * @param agent Nombre del agente HTTP
     *              Por defecto es MLSearcher
     */
    public void setAgent(String agent) {
        this.agent = agent;
    }

    /**
     * Consulta y almacena los sitios y los estados
     *
     * @throws Exception Si falla la consulta
     */
    public void request() throws Exception {
        Url url = new Url();
        if (agent != null) {
            url.setAgent(agent);
        }
        String content = url.getContent("https://api.mercadolibre.com/sites");
        JSONArray jsonArr = new JSONArray(content);
        for (int c = 0; c < jsonArr.length(); c++) {
            String siteId = jsonArr.getJSONObject(c).get("id").toString();
            String siteName = jsonArr.getJSONObject(c).get("name").toString();
            siteMap.put(siteId, siteName);
        }
    }

    /**
     * Obtiene los sitios
     *
     * @return Identificadores y nombres de los sitios
     */
    public Map<String, String> getSites() {
        return siteMap;
    }

}
