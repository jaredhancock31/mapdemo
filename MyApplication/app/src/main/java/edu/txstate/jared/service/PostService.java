package edu.txstate.jared.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import edu.txstate.jared.menudemo.DropletDiscoveryListener;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class PostService extends IntentService {

    public static final String TAG = "RESTSERVICEMANAGER";
    public static final String TOKEN_KEY = "TOKEN_KEY";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String PARAMS_EXTRA = "PARAMS";
    public static final String METHOD_EXTRA = "METHOD_EXTRA";
    public static final String LATITUDE_EXTRA = "LATITUDE_EXTRA";
    public static final String LONGITUDE_EXTRA = "LONGITUDE_EXTRA";
    public static final String SERVICE_CALLBACK = "ORIGINAL_INTENT_EXTRA";
    private static final int REQUEST_INVALID = -1;

    private static final String HOST = "http://104.236.181.178";
    private DropletDiscoveryListener dropletListener;

    private ResultReceiver mCallback;
    private Intent mOriginalRequestIntent;

    public PostService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent requestIntent) {
        mOriginalRequestIntent = requestIntent;

        // get request data from intent
        String method = requestIntent.getStringExtra(METHOD_EXTRA);
        String latitude = requestIntent.getStringExtra(LATITUDE_EXTRA);
        String longitude = requestIntent.getStringExtra(LATITUDE_EXTRA);
        String params = requestIntent.getStringExtra(PARAMS_EXTRA);
        mCallback = requestIntent.getParcelableExtra(SERVICE_CALLBACK);

        switch (method) {
            case METHOD_POST:
                // submit new post
                try {
                    String basicAuth = "Basic " + Base64.encodeToString("txstate:poopscoop".getBytes(), Base64.NO_WRAP);
                    URL hostUrl = new URL("http://104.236.181.178");
                    HttpURLConnection conn = (HttpURLConnection) hostUrl.openConnection();
                    conn.setRequestProperty("Authorization", basicAuth);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    os.write(params.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    Log.i(TAG, "Post response Code : " + responseCode);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }



    public String getAuthToken() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = settings.getString(PostService.TOKEN_KEY, "");
        return token;
    }


}
