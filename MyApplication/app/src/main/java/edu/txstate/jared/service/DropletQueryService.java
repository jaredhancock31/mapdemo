package edu.txstate.jared.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;


public class DropletQueryService extends IntentService {

    private static final String TAG = "LOCATIONSERVICE";



    public DropletQueryService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent requestIntent) {
        if (LocationResult.hasResult(requestIntent)) {
            LocationResult locationResult = LocationResult.extractResult(requestIntent);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Log.d(TAG, "accuracy: " + location.getAccuracy() + " lat: " + location.getLatitude() + " long: " + location.getLongitude());
                // send get and find droplets near user
            }
        }

    }


}
