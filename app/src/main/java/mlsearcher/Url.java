package mlsearcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Clase para obtener el contenido de una URL
 *
 * @author Fernando Camussi
 */
public class Url {

    private String agent = null;

    /**
     * Setea el agente HTTP
     *
     * @param agent Nombre del agente HTTP
     */
    public void setAgent(String agent) {
        this.agent = agent;
    }

    /**
     * Obtiene el contenido de una URL
     *
     * @param urlStr URL
     * @return Contenido de la URL
     * @throws Exception Si el contenido de la URL no pudo obtenerse
     */
    public String getContent(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        URLConnection urlConn = url.openConnection();
        urlConn.setReadTimeout(5000);
        if (agent != null) {
            urlConn.setRequestProperty("Agent", agent);
        }
        urlConn.setRequestProperty("Accept", ""); // para que ML retorne el JSON de forma raw
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

}
