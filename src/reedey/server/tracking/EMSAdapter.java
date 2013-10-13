package reedey.server.tracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import reedey.shared.exceptions.ServiceException;


public class EMSAdapter {

	private static final String URL = "http://80.91.187.254:8080/servlet/SMCSearch2?barcode={barcode}&lang=ua";
	
	private static final String SEARCH_KEY = "</div><div style=\"text-align:center;padding:10px;\">";
	
	public EMSAdapter() {
		
	}
	
	public String getMessage(String barcode) throws IOException {
		String html = requestHttp(getUrl(barcode));
		return extractMessage(html);
	}
	
	private String getUrl(String barcode) {
		return URL.replace("{barcode}", barcode);
	}
	
	private String extractMessage(String html) {
		int index1 = html.indexOf(SEARCH_KEY);
		int index2 = html.indexOf(SEARCH_KEY, index1 + 1);
		if (index1 == -1 || index2 == -1)
			throw new ServiceException("Cannot parse response");
		String result = html.substring(index1 + SEARCH_KEY.length(), index2);
		result = result.replaceAll("\t", "");
		result = result.replaceAll("\n", "");
		result = result.replaceAll("\r", "");
		return result;
	}
	
	private String requestHttp(String urlInput) throws IOException {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(urlInput);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), "UTF8"));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
		return result;
	}
}
