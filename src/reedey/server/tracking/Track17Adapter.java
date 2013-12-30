package reedey.server.tracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import reedey.server.impl.Properties;

public class Track17Adapter {
    
    private static final String TRACK_URL = Properties.getString("track17.url");;
    private static final String TRANSLATE_URL = Properties.getString("bing.url");;
    private static final String APP_ID = Properties.getString("bing.app.id");
    
    private Track17Hash hasher = new Track17Hash();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd mm:hh");
    
    public Track17Adapter() {
        
    }
    
    public Map<Date, String> getMessages(String barcode) throws Exception {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("lo", "www.17track.net"));
        params.add(new BasicNameValuePair("pt", "0"));
        params.add(new BasicNameValuePair("num", barcode));
        params.add(new BasicNameValuePair("hs", hasher.hs(barcode)));
        String json = requestHttp(TRACK_URL, params);
        if (json == null || json.isEmpty())
            throw new IOException("Got empty response for barcode=" + barcode);
        return extractMessages(json.substring(1, json.length() - 1));
    }
    
    private Map<Date, String> extractMessages(String json) throws Exception {
        LinkedHashMap<Date, String> result = new LinkedHashMap<>();
        JSONObject obj = new JSONObject(json);
        String res = obj.getString("msg");
        if (!"Ok".equals(res) || obj.isNull("dat"))
            throw new IOException("Incorrect response:\n" + json);
        
        JSONObject data = obj.getJSONObject("dat");
        JSONArray sender = data.getJSONArray("x");
        for (int i = 0; i < sender.length(); i++) {
            JSONObject item = sender.getJSONObject(i);
            result.put(df.parse(item.getString("a")), item.getString("b"));
        }
            
        return result;
    }
    
    public String translateMessage(String message) throws IOException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appId", APP_ID));
        params.add(new BasicNameValuePair("to", "uk"));
        params.add(new BasicNameValuePair("text", message));
        String result = requestHttp(TRANSLATE_URL, params);
        if (result == null || result.isEmpty())
            throw new IOException("Got empty response for message=" + message);
        return result;
    }
    
    private String requestHttp(String urlInput, List<NameValuePair> params) throws IOException {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = ""; //$NON-NLS-1$
        try {
            url = new URL(urlInput + "?" + getQuery(params));
            conn = (HttpURLConnection) url.openConnection();

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
    
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        return result.toString();
    }
    
    // test
    public static void main(String[] args) throws Exception {
        //System.out.println(new Track17Adapter().getMessages("RC336753544CN")); //$NON-NLS-1$
        String[] messages = new Track17Adapter().getMessages("RC336753544CN").values().toArray(new String[0]);
        //System.out.println(messages); //$NON-NLS-1$
        for (String message : messages)
            System.out.println(new Track17Adapter().translateMessage(message));
    }
}
