package reedey.server.tracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import reedey.server.impl.Properties;
import reedey.shared.exceptions.ServiceException;


public class EMSAdapter {

	private static final String URL = Properties.getString("emsukr.url"); //$NON-NLS-1$
	
	private static final String SEARCH_KEY = "h2></center>"; //$NON-NLS-1$
	private static final String SEARCH_END = "</div>"; //$NON-NLS-1$
	
	public EMSAdapter() {
		
	}
	
	public String getMessage(String barcode) throws IOException {
		String html = requestHttp(getUrl(barcode));
		return extractMessage(html, barcode);
	}
	
	private String getUrl(String barcode) {
		return URL.replace("{barcode}", barcode); //$NON-NLS-1$
	}
	
	private String extractMessage(String html, String barcode) {
	    if (html.contains(Messages.getString("track.incorrect"))) {
	        return "[http://otsledit.com.ua/] " + Messages.getString("track.incorrect1")
	                + " " + barcode + " " + Messages.getString("track.incorrect") + ".";
	    }
		int index1 = html.indexOf(SEARCH_KEY);
		int index2 = html.indexOf(SEARCH_END, index1);
		if (index1 == -1 || index2 == -1)
			throw new ServiceException("Cannot parse response. indexes=[" + index1 + "," + index2 + "]\n" + html); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String result = html.substring(index1 + SEARCH_KEY.length(), index2);
		//result = result.replaceAll("\t", "");
		result = result.replaceAll("\r", ""); //$NON-NLS-1$ //$NON-NLS-2$
		result = result.replaceAll("\n", " "); //$NON-NLS-1$ //$NON-NLS-2$
		if (result.contains(Messages.getString("ems.not.connected")) || result.contains(Messages.getString("ems.not.respond")) //$NON-NLS-1$ //$NON-NLS-2$
				|| result.contains(Messages.getString("ems.something.wrong"))) { //$NON-NLS-1$
		    throw new ServiceException("Connection error:\n" + result); //$NON-NLS-1$
		}
		//result = result.replaceAll("  ", " ").replaceAll("  ", " ");
		return result.trim();
	}
	
	private String requestHttp(String urlInput) throws IOException {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = ""; //$NON-NLS-1$
		try {
			url = new URL(urlInput);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET"); //$NON-NLS-1$
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), "windows-1251")); //$NON-NLS-1$
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
	    System.out.println(new EMSAdapter().getMessage("RB21559389111")); //$NON-NLS-1$
	}
}
