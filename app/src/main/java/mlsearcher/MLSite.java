package mlsearcher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase para obtener los sitios (paises) y los estados (provincias)
 *
 * @author Fernando Camussi
 */
public class MLSite {

    Map<String, String> siteMap;
    Map<String, List<String>> stateMap;
    private String agent;

    /**
     * Constructor
     */
    public MLSite() {
        siteMap = new HashMap<>();
        stateMap = new HashMap<>();
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
        for (String id : siteMap.keySet()) {
            content = url.getContent("https://api.mercadolibre.com/sites/" + id);
            JSONObject jsonObj = new JSONObject(content);
            String countryId = jsonObj.getString("country_id");
            content = url.getContent("https://api.mercadolibre.com/countries/" + countryId);
            jsonObj = new JSONObject(content);
            jsonArr = jsonObj.getJSONArray("states");
            List<String> stateList = new ArrayList<>();
            for (int c = 0; c < jsonArr.length(); c++) {
                stateList.add(jsonArr.getJSONObject(c).getString("name"));
            }
            stateMap.put(id, stateList);
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

    /**
     * Obtiene los estados de un sitio
     *
     * @param id Identificador del sitio
     * @return Estados del sitio
     */
    public List<String> getStatesBySiteId(String id) {
        return stateMap.get(id);
    }

}
