package reedey.server.tracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;




import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import reedey.server.impl.Properties;

public class Track17Adapter {
    
    private static final String TRACK_URL = Properties.getString("track17.url");; //$NON-NLS-1$
    private static final String TRANSLATE_URL = Properties.getString("bing.url");; //$NON-NLS-1$
    private static final String APP_ID = Properties.getString("bing.app.id"); //$NON-NLS-1$
    
    private Track17Hash hasher = new Track17Hash();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd mm:hh"); //$NON-NLS-1$
    
    public Track17Adapter() {
        
    }
    
    public Map<Date, String> getMessages(String barcode) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("lo", "www.17track.net"); //$NON-NLS-1$ //$NON-NLS-2$
        params.put("pt", "0"); //$NON-NLS-1$ //$NON-NLS-2$
        params.put("num", barcode); //$NON-NLS-1$
        params.put("hs", hasher.hs(barcode)); //$NON-NLS-1$
        String json = requestHttp(TRACK_URL, params);
        if (json == null || json.isEmpty())
            throw new IOException("Got empty response for barcode=" + barcode); //$NON-NLS-1$
        return extractMessages(json.substring(1, json.length() - 1));
    }
    
    private Map<Date, String> extractMessages(String json) throws Exception {
        LinkedHashMap<Date, String> result = new LinkedHashMap<>();
        JSONObject obj = new JSONObject(json);
        String res = obj.getString("msg"); //$NON-NLS-1$
        if (!"Ok".equals(res) || obj.isNull("dat")) //$NON-NLS-1$ //$NON-NLS-2$
            throw new IOException("Incorrect response:\n" + json); //$NON-NLS-1$
        
        JSONObject data = obj.getJSONObject("dat"); //$NON-NLS-1$
        JSONArray sender = data.getJSONArray("x"); //$NON-NLS-1$
        for (int i = 0; i < sender.length(); i++) {
            JSONObject item = sender.getJSONObject(i);
            result.put(df.parse(item.getString("a")), item.getString("b")); //$NON-NLS-1$ //$NON-NLS-2$
        }
            
        return result;
    }
    
    public String translateMessage(String message) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("appId", APP_ID); //$NON-NLS-1$
        params.put("to", "uk"); //$NON-NLS-1$ //$NON-NLS-2$
        params.put("text", message); //$NON-NLS-1$
        String result = requestHttp(TRANSLATE_URL, params);
        if (result == null || result.isEmpty())
            throw new IOException("Got empty response for message=" + message); //$NON-NLS-1$
        return Messages.getString("track.translated") + " " + result; //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private String requestHttp(String urlInput, Map<String, String> params) throws IOException {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = ""; //$NON-NLS-1$
        try {
            url = new URL(urlInput + "?" + getQuery(params)); //$NON-NLS-1$
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000);
            conn.setRequestMethod("GET"); //$NON-NLS-1$

            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8")); //$NON-NLS-1$
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
        return result;
    }
    
    private String getQuery(Map<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Entry<String, String> pair : params.entrySet())
        {
            if (first)
                first = false;
            else
                result.append("&"); //$NON-NLS-1$
            result.append(URLEncoder.encode(pair.getKey(), "UTF-8")); //$NON-NLS-1$
            result.append("="); //$NON-NLS-1$
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8")); //$NON-NLS-1$
        }
        return result.toString();
    }
    
    // test
    public static void main(String[] args) throws Exception {
        //System.out.println(new Track17Adapter().getMessages("RC336753544CN")); //$NON-NLS-1$
        String[] messages = new Track17Adapter().getMessages("RC336753544CN").values().toArray(new String[0]); //$NON-NLS-1$
        //System.out.println(messages); //$NON-NLS-1$
        for (String message : messages)
            System.out.println(new Track17Adapter().translateMessage(message));
    }
}
