package edu.txstate.jared.menudemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.Date;

import static com.google.android.gms.location.LocationServices.API;

/**
 * Created by jared on 2/22/16.
 */
public class LocManager implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks,
                        GoogleApiClient.OnConnectionFailedListener {


    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /* Private attributes */
    private static final String TAG = "LocManager";
    private Location mCurrentLocation;
    private LatLng mCurrentLatLng;
    private boolean mRequestingLocationUpdates;
    private LocationManager manager;
    private Context context;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private String mProviderName;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    public GoogleApiClient mGoogleApiClient;


    /* def constructor */
    public LocManager(Context context, LocationManager man) {
        this.context = context;
        this.manager = man;
        Criteria criteria = new Criteria();
        this.mProviderName = manager.getBestProvider(criteria, true);
        mRequestingLocationUpdates = false;

        buildGoogleApiClient();
        createLocationRequest();
    }


    //LocationListener method
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    //GoogleApiClient method
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());
        if (ActivityCompat.checkSelfPermission(mGoogleApiClient.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mGoogleApiClient.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation != null) {
                 mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            }
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    //GoogleApiClient method
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "connection suspended");
    }

    //GoogleApiClient method
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "connection failed");

    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    public synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addApi(API)
                .addOnConnectionFailedListener(this)
                .build();
    }


    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mGoogleApiClient.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mGoogleApiClient.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }


    protected void createLocationRequest() {
        mRequestingLocationUpdates = true;
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public boolean ismRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public Location getMyCurrentLocation() {
        return mCurrentLocation;
    }

    public LatLng getMyCurrentLatLng() { return mCurrentLatLng; }
}
