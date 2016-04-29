package edu.txstate.jared.menudemo;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import edu.txstate.jared.service.DropletQueryService;
import static com.google.android.gms.location.LocationServices.API;

/**
 * Activity responsible for housing the Google Maps object
 *
 * By extending the FragmentActivity, we don't use the traditional fragment loader used in this
 * example: https://developers.google.com/maps/documentation/android-api/map.
 */

public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        com.google.android.gms.location.LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MAPSLOG";

    /* The desired interval for location updates. Inexact. Updates may be more or less frequent. */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;

    /* Updates will never be more frequent than this value. */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private static final String LOCATION_KEY = "location-key";
    private static final String LAST_UPDATE_TIME_KEY = "last-update-time-key";
    //    private LocManager locManager;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private String mProviderName;
    private LocationManager locationManager;
    private LocationListener mLocationListener;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates;
    private PendingIntent mRequestLocationUpdatesPendingIntent;
    private BroadcastReceiver mMessageReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        buildGoogleApiClient();
        createLocationRequest();
        setupBroadcasting();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button newDropButton = (Button) findViewById(R.id.newDropButton);
        newDropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateDropletActivity.class);
                intent.putExtra(Droplet.LATITUDE, mCurrentLocation.getLatitude());
                intent.putExtra(Droplet.LONGITUDE, mCurrentLocation.getLongitude());
                startActivity(intent);
            }
        });
        // update vals using data stored in the Bundle
        updateValuesFromBundle(savedInstanceState);
    }

    /**
     * Overrides the back button to always go to MainActivity.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void setupBroadcasting() {
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Got broadcast message.");
                Bundle bundle = intent.getBundleExtra("BUNDLE");
                ArrayList<Droplet> droplets = bundle.getParcelableArrayList(Droplet.TAG);
                if (droplets != null)
                    updateMap(droplets);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(DropletQueryService.DROPLETS_FOUND));
    }


    public void updateMap(ArrayList<Droplet> droplets) {
        Log.d(TAG, "updating map");
        if (!droplets.isEmpty()) {
            for (Droplet drop : droplets) {
                LatLng position = new LatLng(drop.getLatitude(), drop.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(drop.getOwner())
                        .snippet(drop.getData())
                        .draggable(false));
            }
        }
    }



    /**
     * Starts location update service as well as our own DropletQueryService
     */
    public void startLocationUpdates() {
        Log.d(TAG, "starting location updates.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            startLocationUpdates();
        } else {
            // get updates for the map first
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

            // create intent to handle results
            Intent mDropletQueryServiceIntent = new Intent(this, DropletQueryService.class);

            // create a PendingIntent
            mRequestLocationUpdatesPendingIntent = PendingIntent.getService(getApplicationContext(), 0,
                    mDropletQueryServiceIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // send location updates to our background service
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, mRequestLocationUpdatesPendingIntent);
        }
    }


    public void stopLocationUpdates() {
        if(mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mRequestLocationUpdatesPendingIntent);
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


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        stopLocationUpdates();
        super.onStop();
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
            Log.d(TAG, "permission to fine location is missing");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        } else if (mMap != null) {
            Log.d(TAG, "permission for fine location has been granted. setting the map to have location enabled");
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
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


    /* ---------------------------------- Overridden methods --------------------------------------*/

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


    // locationLister method
    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Log.d(TAG, "inside onLocationChanged and location was null");
            return;
        }
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApiClient");
        mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }


    /* LocationListener method TODO stoplocationrequests in LocManager? */
    @Override
    public void onPause() {
        super.onPause();

    }


    /* LocationListener method */
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Listener for the myLocation button. Returning false will center the camera on the user's
     * current location.
     *
     * @return false if you want the default behavior, true if you want to explicitly define it
     */
    @SuppressWarnings("ResourceType")
    @Override
    public boolean onMyLocationButtonClick() {
        Log.i(TAG, "MyLocation button clicked.");
        mRequestingLocationUpdates = true;
        return false;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        stopLocationUpdates();
        mGoogleApiClient.connect();
    }

    // googleApiClient method
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "JARED googleApiClient connection failed");
    }


    /**
     * Permission request handler
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                }
                return;
            default:
                Log.e(TAG, "location permission denied");
                return;
        }
    }
}