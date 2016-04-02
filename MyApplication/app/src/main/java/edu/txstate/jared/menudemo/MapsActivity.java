package edu.txstate.jared.menudemo;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;

import edu.txstate.jared.service.DropletQueryService;
import edu.txstate.jared.service.ReqManager;
import edu.txstate.jared.fragments.TextDropFragment;

import static com.google.android.gms.location.LocationServices.API;

/**
 * Activity responsible for housing the Google Maps object
 *
 * By extending the FragmentActivity, we don't use the traditional fragment loader used in this
 * example: https://developers.google.com/maps/documentation/android-api/map.
 *
 *
 */

public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        com.google.android.gms.location.LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        TextDropFragment.OnFragmentInteractionListener,
        DropletDiscoveryListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    private static final String TAG = "MAPSLOG";
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private static final String LOCATION_KEY = "location-key";
    private static final String LAST_UPDATE_TIME_KEY = "last-update-time-key";
//    private LocManager locManager;
    private LocationManager locationManager;
    private ReqManager reqManager;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private String mProviderName;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates;
    private PendingIntent mRequestLocationUpdatesPendingIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        locManager = new LocManager(this, (LocationManager) getSystemService(Context.LOCATION_SERVICE));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        reqManager = new ReqManager(this);
        buildGoogleApiClient();
        createLocationRequest();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button newDropButton = (Button) findViewById(R.id.newDropButton);
        newDropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().
                        replace(R.id.maps_frag_container, new TextDropFragment()).commit();
            }
        });
        // update vals using data stored in the Bundle
        updateValuesFromBundle(savedInstanceState);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
    }


    /**
     * When a new droplet is submitted, this method tells the ReqManager to start up a new asyncPost
     * and save the droplet to the server.
     * @param dropMessage message to leave at the droplet location
     * @param timestamp timestamp of the droplet submission
     */
    @Override
    public void onDropSubmit(String dropMessage, Timestamp timestamp) {
//        Location loc = locationManager.getLastKnownLocation(mProviderName);
        if (mCurrentLocation != null) {
            // this is going away
        }
    }

    /**
     * When a droplet appears in the user's proximity, this method unpacks the droplet and populates
     * it on the map
     * @param droplet droplet near user to draw on map
     */
    @Override
    public void onDropletFound(Droplet droplet) {
        LatLng dropletLocation = new LatLng(droplet.getLatitude(), droplet.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(dropletLocation)
                .title(String.valueOf(droplet.getUser_id()))
                .snippet(droplet.getMessage()));
    }


    // locationLister method
    @Override
    public void onLocationChanged(Location location) {
        if (location == null){
            Log.d(TAG, "inside onLocationChanged and location was null");
            return;
        }
        mCurrentLocation = location;
//        Log.d(TAG, "location is not null! ");
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApiClient");
        mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "permission to fine location is missing");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            Log.d(TAG, "best provider name is supposedly: "+ mProviderName);
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mCurrentLocation != null) {
                Log.d(TAG, "JARED currentlocation is NOT null" );
            }
            else {
                Log.d(TAG, "JARED currentlocation is null inside of onConnected.");
            }
        }
        else {
            startLocationUpdates();
        }
    }


    public void startLocationUpdates() {
        Log.d(TAG, "inside startlocationupdates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            startLocationUpdates();
        }
        else {
            // get updates for the map first
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

            // create intent to handle results
            Intent mRequestLocationUpdatesIntent = new Intent(this, DropletQueryService.class);

            // create a PendingIntent
            mRequestLocationUpdatesPendingIntent = PendingIntent.getService(getApplicationContext(), 0,
                    mRequestLocationUpdatesIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // send location updates to our background service
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, mRequestLocationUpdatesPendingIntent);


            Log.d(TAG, "starting location updates");
        }
    }


    public void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mRequestLocationUpdatesPendingIntent);
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended");
        mGoogleApiClient.connect();


    }

    // googleApiClient method
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "JARED googleApiClient connection failed");
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


    /**
     * Listener for the myLocation button. Returning false will center the camera on the user's
     * current location.
     * @return false if you want the default behavior, true if you want to explicitly define it
     */
    @SuppressWarnings("ResourceType")
    @Override
    public boolean onMyLocationButtonClick() {
        Log.i(TAG, "MyLocation button clicked.");
        //TODO tell reqManager to search for droplets near current location
        mRequestingLocationUpdates = true;
//        reqManager.lookForDropletsNearby(locManager.getMyCurrentLocation());
        return false;
    }


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
//        locManager.mGoogleApiClient.disconnect();
        super.onStop();
    }


    /* LocationListener method TODO stoplocationrequests in LocManager? */
    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    /* LocationListener method */
    @Override
    public void onResume() {
        super.onResume();
        if(mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }



    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    public synchronized void buildGoogleApiClient() {
        Log.d(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(API)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to ACCESS_FINE_LOCATION is missing.
            Log.d(TAG, "permission to fine location is missing");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        else if (mMap != null) {
            Log.d(TAG, "permission for fine location has been granted. setting the map to have location enabled");
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
        else {
            Log.e(TAG, "hit the else case in enableMyLocation");
        }
    }


    /**
     * TODO fix grantResults[0] lines: refer to https://github.com/googlemaps/android-samples/blob/master/ApiDemos/app/src/main/java/com/example/mapdemo/PermissionUtils.java line 58
     * Permission request handler
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                }
                return;
            default:
                Log.e(TAG, "location permission denied");
                return;

        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);

            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATE_TIME_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATE_TIME_KEY);
            }
        }

    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATE_TIME_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

}