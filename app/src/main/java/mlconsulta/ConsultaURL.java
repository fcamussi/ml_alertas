package mlconsulta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


class ConsultaURL {

	static public String consultar(String url_str) throws Exception {
		URL url = new URL(url_str);
		URLConnection urlConn = url.openConnection();
		urlConn.setRequestProperty("Agent", "Mercado Alertas Libre");	
		urlConn.setRequestProperty("Accept", "");	
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		StringBuffer stringBuffer = new StringBuffer();
		String line;

		while ((line = bufferedReader.readLine()) != null) {
			stringBuffer.append(line);
		}
		
		return stringBuffer.toString();
	}

}
