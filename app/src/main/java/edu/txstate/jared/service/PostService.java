package edu.txstate.jared.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import edu.txstate.jared.menudemo.Droplet;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class PostService extends IntentService {

    /* constants */
    public static final String TAG =                                "POSTSERVICE";
    public static final String TOKEN_KEY =                          "TOKEN_KEY";
    public static final String METHOD_GET =                         "GET";
    public static final String METHOD_POST =                        "POST";
    public static final String PARAMS_EXTRA =                       "PARAMS";
    public static final String METHOD_EXTRA =                       "METHOD_EXTRA";
    public static final String LATITUDE_EXTRA =                     "LATITUDE_EXTRA";
    public static final String LONGITUDE_EXTRA =                    "LONGITUDE_EXTRA";
    public static final String DATA_EXTRA =                         "DATA_EXTRA";
    public static final String JSON_EXTRA =                         "JSON_EXTRA";
    public static final String HOST =                               "http://104.236.181.178:8000/";

    private Intent mOriginalRequestIntent;

    public PostService() {
        super(TAG);
    }

    /**
     *
     * @param requestIntent
     */
    @Override
    protected void onHandleIntent(Intent requestIntent) {
        mOriginalRequestIntent = requestIntent;

        // get request data from intent
        String method = requestIntent.getStringExtra(METHOD_EXTRA);
        String latitude = requestIntent.getStringExtra(LATITUDE_EXTRA);
        String longitude = requestIntent.getStringExtra(LONGITUDE_EXTRA);
        String data = requestIntent.getStringExtra(DATA_EXTRA);
        String droplet = requestIntent.getStringExtra(JSON_EXTRA);      //droplet formatted as JSON

        switch (method) {

            /* send POST request to the server */
            case METHOD_POST:
                try {
                    Log.d(TAG, "starting POST request");

                    JSONObject json = new JSONObject();
                    json.put(Droplet.OWNER, "Jared");
                    json.put(Droplet.LATITUDE, latitude);
                    json.put(Droplet.LONGITUDE, longitude);
                    json.put(Droplet.DATA, data);

                    URL hostUrl = new URL(HOST + "droplets/all/");

                    /* setup headers */
                    HttpURLConnection conn = (HttpURLConnection) hostUrl.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("Connection", "keep-alive");
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());

                    /* send params to server */
                    Log.d(TAG, "JSON object, valueOf(): " + String.valueOf(json));
                    os.write(String.valueOf(json).getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    /* check out the response from the server */
                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "POST response Code : " + responseCode);                 // looking for a 201
                    Log.d(TAG, "POST response message: " + conn.getResponseMessage());  // looking for 'Created'

                    /* read response if POST was a success */
                    if (responseCode < 400) {
                        String inputLine;
                        String responseText = "";
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((inputLine = reader.readLine()) != null)
                            responseText += inputLine;

                        reader.close();
                    }
                    conn.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }


    public String fetchCSRF() {
        Log.d(TAG, "attempting CSRF fetch");
        String csrfToken = "";
        try {
            CookieManager manager = new CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);
            URL url = new URL("http://104.236.181.178:8000/droplets/all/");
            URLConnection conn = url.openConnection();

            Map<String, List<String>> map = conn.getHeaderFields();
            for (Map.Entry entry : map.entrySet()) {
                Log.d(TAG, entry.getKey() + ": " + entry.getValue());
            }


            conn.getContent();

            CookieStore cookieJar = manager.getCookieStore();
            List<HttpCookie> cookies = cookieJar.getCookies();

            String cookieString = "";

            for (HttpCookie c : cookies) {
                Log.d(TAG, "Got cookie: " + c.toString());

                cookieString += c.getName() + "=" + c.getValue() + ";";

                if (c.getName().equals("csrftoken")) {
                    csrfToken = c.getValue();
                }
            }
            Log.d(TAG, "cookieStr: " + cookieString);
            Log.d(TAG, "csrfToken: " + csrfToken);
//            String cookieStr = cookies.get(0).toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csrfToken;
    }



    public String getAuthToken() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = settings.getString(PostService.TOKEN_KEY, "");
        return token;
    }


}
