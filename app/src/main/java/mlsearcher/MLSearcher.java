package mlsearcher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase para buscar artículos en Mercado Libre
 *
 * @author Fernando Camussi
 */
public class MLSearcher {

    private final List<String> wordList;
    private final List<Map<String, String>> itemList;
    private String siteId;
    private String agent;
    private boolean filtered;
    private int maxResultCount;

    /**
     * Constructor
     */
    public MLSearcher() {
        siteId = null;
        agent = "MLSearcher";
        filtered = true;
        maxResultCount = 1000;
        wordList = new ArrayList<>();
        itemList = new ArrayList<>();
    }

    /**
     * Método estático
     * Convierte una string en una lista de strings
     *
     * @param string La string a convertir
     * @return Lista de strings
     */
    static public List<String> stringToStringList(String string) {
        List<String> stringList = new ArrayList<>();
        String[] stringSplited = string.split("\\s+");
        Collections.addAll(stringList, stringSplited);
        return stringList;
    }

    /**
     * Método estático
     * Convierte una lista de strings en una string
     *
     * @param stringList Lista de strings a convertir
     * @return String
     */
    static public String stringListToString(List<String> stringList) {
        StringBuilder string = new StringBuilder(stringList.get(0));
        for (int c = 1; c < stringList.size(); c++) {
            string.append(" ").append(stringList.get(c));
        }
        return string.toString();
    }

    /**
     * Setea el id del sitio donde se hará la búsqueda: MLA, MLB, etc.
     *
     * @param siteId Id del sitio
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
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
     * Setea si se hace un filtrado para que las palabras estén contenidas dentro del título
     *
     * @param filtered true para filtrar, false para no filtrar
     *                 Por defecto es true
     */
    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    /**
     * Setea la cantidad máxima de resultados que se exploran en la búsqueda
     *
     * @param maxResultCount Cantidad máxima de resultados
     *                       El valor por defecto es 1000, que es la cantidad máxima definida por ML
     * @throws Exception Si maxResultCount es mayor a 1000
     */
    public void setMaxResultCount(int maxResultCount) throws Exception {
        if (maxResultCount > 1000) throw new Exception("maxResultCount no puede ser mayor a 1000");
        this.maxResultCount = maxResultCount;
    }

    /**
     * Setea las palabras a buscar
     *
     * @param wordList Lista de palabras
     */
    public void setWords(List<String> wordList) {
        this.wordList.clear();
        for (String word : wordList) {
            this.wordList.add(word.toLowerCase());
        }
    }

    /**
     * Consulta la cantidad de resultados que produce la búsqueda
     *
     * @return La cantidad de resultados
     * @throws Exception Si falla la consulta
     */
    public int getResultCount() throws Exception {
        if (siteId == null) throw new Exception("Id de sitio no especificado");
        Url url = new Url();
        url.setAgent(agent);
        String content = url.getContent(buildURLStr(0));
        JSONObject jsonObj = new JSONObject(content);
        int total = jsonObj.getJSONObject("paging").getInt("total");
        return total;
    }

    /**
     * Realiza la búsqueda
     *
     * @throws Exception Si falla la búsqueda o el id del sitio no fue especificado
     */
    public void searchItems() throws Exception {
        if (siteId == null) throw new Exception("Id de sitio no especificado");
        Url url = new Url();
        url.setAgent(agent);
        String content = url.getContent(buildURLStr(0));
        JSONObject jsonObj = new JSONObject(content);
        int limit = jsonObj.getJSONObject("paging").getInt("limit");
        int total = jsonObj.getJSONObject("paging").getInt("total");

        if (total > maxResultCount) {
            total = maxResultCount;
        }
        itemList.clear();
        JSONArray results = jsonObj.getJSONArray("results");
        addItems(results);
        int nResult = results.length();
        for (int offset = limit; offset < total; offset += limit) {
            content = url.getContent(buildURLStr(offset));
            jsonObj = new JSONObject(content);
            results = jsonObj.getJSONArray("results");
            addItems(results);
            nResult += results.length();
        }
        if (nResult != total) {
            throw new Exception("Falló la obtención de los resultados");
        }
    }

    /**
     * Obtiene la lista de artículos encontrados
     *
     * @return Lista de artículos de tipo Map
     */
    public List<Map<String, String>> getFoundItems() {
        return itemList;
    }

    /* Métodos privados */

    private void addItems(JSONArray jsonArr) throws Exception {
        for (int c = 0; c < jsonArr.length(); c++) {
            String id = jsonArr.getJSONObject(c).get("id").toString();
            String title = jsonArr.getJSONObject(c).get("title").toString();
            String price = jsonArr.getJSONObject(c).get("price").toString();
            String currency = jsonArr.getJSONObject(c).get("currency_id").toString();
            String permalink = jsonArr.getJSONObject(c).get("permalink").toString();
            String thumbnailLink = jsonArr.getJSONObject(c).get("thumbnail").toString();
            String city = jsonArr.getJSONObject(c).getJSONObject("address").get("city_name").toString();
            String state = jsonArr.getJSONObject(c).getJSONObject("address").get("state_name").toString();
            Map<String, String> item = new HashMap<>();
            item.put("id", id);
            item.put("title", title);
            item.put("price", price);
            item.put("currency", currency);
            item.put("permalink", permalink);
            item.put("thumbnail_link", thumbnailLink);
            item.put("city", city);
            item.put("state", state);
            if (filtered) { /* Chequea que cada palabra esté contenida en el título del artículo */
                boolean match = true;
                title = title.toLowerCase();
                for (String word : wordList) {
                    if (!title.contains(word)) {
                        match = false;
                        break;
                    }
                }
                if (match) itemList.add(item);
            } else {
                itemList.add(item);
            }
        }
    }

    private String buildURLStr(int offset) throws Exception {
        String urlStr = "https://api.mercadolibre.com/sites/" + siteId + "/search?q=";
        String wordStr = stringListToString(wordList);
        wordStr = URLEncoder.encode(wordStr, "UTF-8");
        urlStr = urlStr + wordStr;
        urlStr = urlStr + "&offset=" + offset;
        return urlStr;
    }

}
