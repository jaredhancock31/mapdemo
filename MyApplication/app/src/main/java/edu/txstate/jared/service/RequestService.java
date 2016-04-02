package edu.txstate.jared.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;

import edu.txstate.jared.menudemo.DropletDiscoveryListener;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class RequestService extends IntentService {

    public static final String TOKEN_KEY = "TOKEN_KEY";
    public static final String TAG = "RESTSERVICEMANAGER";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_EXTRA = "METHOD_EXTRA";
    public static final String LATITUDE_EXTRA = "LATITUDE_EXTRA";
    public static final String LONGITUDE_EXTRA = "LONGITUDE_EXTRA";
    public static final String SERVICE_CALLBACK = "ORIGINAL_INTENT_EXTRA";
    public static final int RESOURCE_TYPE_PROFILE = 1;
    public static final int RESOURCE_TYPE_TIMELINE = 2;
    public static final String ORIGINAL_INTENT_EXTRA = "ORIGINAL_INTENT_EXTRA";
    private static final int REQUEST_INVALID = -1;

    private static final String HOST = "http://104.236.181.178";
    private DropletDiscoveryListener dropletListener;

    private ResultReceiver mCallback;
    private Intent mOriginalRequestIntent;

    public RequestService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent requestIntent) {
        mOriginalRequestIntent = requestIntent;

        // get request data from intent
        String method = requestIntent.getStringExtra(METHOD_EXTRA);
        String latitude = requestIntent.getStringExtra(LATITUDE_EXTRA);
        String longitude = requestIntent.getStringExtra(LATITUDE_EXTRA);
        mCallback = requestIntent.getParcelableExtra(SERVICE_CALLBACK);

        switch (method) {
            case METHOD_POST:
                // submit new post
        }

    }



    public String getAuthToken() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = settings.getString(RequestService.TOKEN_KEY, "");
        return token;
    }


}
