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
import edu.txstate.jared.menudemo.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class PostService extends IntentService {

    /* constants */
    public static final String TAG =                                "POSTSERVICE";
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
//        String droplet = requestIntent.getStringExtra(JSON_EXTRA);      //droplet formatted as JSON

        switch (method) {

            /* send POST request to the server */
            case METHOD_POST:
                try {
                    Log.d(TAG, "starting POST request");

                    JSONObject json = new JSONObject();
//                    json.put(Droplet.OWNER, "Jared");
                    json.put(Droplet.LATITUDE, latitude);
                    json.put(Droplet.LONGITUDE, longitude);
                    json.put(Droplet.DATA, data);

                    String token = getAuthToken();

                    OkHttpClient client = new OkHttpClient();

                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, json.toString());
                    Request request = new Request.Builder()
                            .url("http://104.236.181.178:8000/droplets/all/")
                            .post(body)
                            .addHeader("authorization", "Token " + token)
                            .addHeader("content-type", "application/json")
                            .addHeader("cache-control", "no-cache")
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseBody = response.body().string();

                    Log.d(TAG, "response: " + response.message());
                    Log.d(TAG, "response body: " + responseBody);
                    
                    /* uncomment this loop to log the headers */
//            for (String header : response.headers().names()) {
//                Log.d(TAG, response.header(header));
//            }
                    if (response.code() < 400) {
                        // do stuff
                    }


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


    public String getAuthToken() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = settings.getString(User.AUTH_TOKEN, "");
        return token;
    }


}
