package reedey.server.tracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import reedey.shared.exceptions.ServiceException;


public class EMSAdapter {

	private static final String URL = "http://otsledit.com.ua/index.php?co=ukrposhta&nomer_pos={barcode}";
	
	private static final String SEARCH_KEY = "h2></center>";
	private static final String SEARCH_END = "</div>";
	
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
		int index2 = html.indexOf(SEARCH_END, index1);
		if (index1 == -1 || index2 == -1)
			throw new ServiceException("Cannot parse response. indexes=[" + index1 + "," + index2 + "]\n" + html);
		String result = html.substring(index1 + SEARCH_KEY.length(), index2);
		//result = result.replaceAll("\t", "");
		result = result.replaceAll("\r", "");
		result = result.replaceAll("\n", " ");
		if (result.contains("Нет связи с сервером") || result.contains("Сервер УкрПочты не отвечает")
				|| result.contains("Что-то пошло не так")) {
		    throw new ServiceException("Connection error:\n" + result);
		}
		//result = result.replaceAll("  ", " ").replaceAll("  ", " ");
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
					new InputStreamReader(conn.getInputStream(), "windows-1251"));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
		return result;
	}
	
	// test
	public static void main(String[] args) throws IOException {
	    System.out.println(new EMSAdapter().getMessage("RB215593892CN"));
	}
}
