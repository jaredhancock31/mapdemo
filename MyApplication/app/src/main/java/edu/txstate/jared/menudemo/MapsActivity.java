package edu.txstate.jared.menudemo;

import android.Manifest;
import android.content.Context;
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


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;

import edu.txstate.jared.api.ReqManager;
import edu.txstate.jared.fragments.DropTypeFragment;
import edu.txstate.jared.fragments.TextDropFragment;

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
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        DropTypeFragment.OnFragmentInteractionListener,
        TextDropFragment.OnFragmentInteractionListener {

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
    private static final String TAG = "mapsLog";
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private static final String LOCATION_KEY = "location-key";
    private static final String LAST_UPDATE_TIME_KEY = "last-update-time-key";
    private LocManager locManager;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locManager = new LocManager(this, (LocationManager) getSystemService(Context.LOCATION_SERVICE));
        reqManager = ReqManager.getReqManager();

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
        onMyLocationButtonClick();

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
        return false;
    }


    protected void onStart() {
        locManager.mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        locManager.mGoogleApiClient.disconnect();
        super.onStop();
    }


    /* LocationListener method TODO stoplocationrequests in LocManager? */
    @Override
    public void onPause() {
        super.onPause();
//        locManager.stopLocationUpdates();
    }


    /* LocationListener method */
    @Override
    public void onResume() {
        super.onResume();
        if(locManager.mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            locManager.startLocationUpdates();
        }
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to ACCESS_FINE_LOCATION is missing.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to ACCESS_COARSE_LOCATION is missing
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

        }
    }


    /**
     * TODO fix grantResults[0] lines: refer to https://github.com/googlemaps/android-samples/blob/master/ApiDemos/app/src/main/java/com/example/mapdemo/PermissionUtils.java line 58
     * Permission request handler
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSION_ACCESS_COARSE_LOCATION || requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
            Log.i(TAG, "Received response for Location permission request.");
            switch (requestCode) {
                case MY_PERMISSION_ACCESS_COARSE_LOCATION: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "LOCATION permission granted");
                        // TODO run map task fragment?
                    } else {
                        Log.i(TAG, "LOCATION permission was NOT granted.");
                        // TODO give some message, return to main menu
                    }
                    break;
                }
                case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted
                        Log.i(TAG, "LOCATION permission granted");
                        // TODO run map task fragment?
                    } else {
                        // permission denied
                        Log.i(TAG, "LOCATION permission was NOT granted.");
                        // TODO give some message, return to main menu
                    }
                    break;
                }
            }
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

    @Override
    public void onDropTypeSubmit(String type) {
        FragmentManager fm = getSupportFragmentManager();
        TextDropFragment textDrop = TextDropFragment.newInstance(type);
        fm.beginTransaction().replace(fm.findFragmentById(R.id.fragment_container).getId(), textDrop).commit();
    }


    /**
     * When a new droplet is submitted, this method tells the ReqManager to start up a new asyncPost
     * and save the droplet to the server.
     * @param dropMessage message to leave at the droplet location
     * @param timestamp timestamp of the droplet submission
     */
    @Override
    public void onDropSubmit(String dropMessage, Timestamp timestamp) {
        LatLng currentLatLng = locManager.getMyCurrentLatLng();
        int userID = 1234; // TODO change this to a real id

        // TODO change 0 for drop_id to autoincrement
        DataDrop droplet = new DataDrop(userID, currentLatLng.latitude, currentLatLng.longitude,
                dropMessage, timestamp);

        reqManager.startPost(droplet);
    }

}