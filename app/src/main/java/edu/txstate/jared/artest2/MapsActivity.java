/**
 * Author: Jared Hancock
 *
 * Texas State University
 * Last Update: Feb 2016
 */

package edu.txstate.jared.artest2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
        GoogleMap.OnMyLocationButtonClickListener{

    private static final String TAG = "mapsLog";
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private GoogleMap mMap;

    private String mProviderName;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mProviderName = mLocationManager.getBestProvider(criteria, true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
     * Listener for the myLocation button. Returning false will center the camera on the user's
     * current location.
     *
     * Note: This won't work when running the app on an emulator. (I think)
     *
     * @return false if you want the default behavior, true if you want to explicitly define it
     */
    @SuppressWarnings("ResourceType")
    @Override
    public boolean onMyLocationButtonClick() {
        Log.i(TAG, "MyLocation button clicked.");
//        if(mMap.isMyLocationEnabled()) {
//            Location mLocation = mLocationManager.getLastKnownLocation(mProviderName);
//            mMap.moveCamera(CameraUpdateFactory.);
//        }

        return false;
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // Permission to ACCESS_FINE_LOCATION is missing.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to ACCESS_COARSE_LOCATION is missing
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }


    /**
     * TODO fix grantResults[0] lines: refer to https://github.com/googlemaps/android-samples/blob/master/ApiDemos/app/src/main/java/com/example/mapdemo/PermissionUtils.java line 58
     * Permission request handler
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
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
                    }
                    else {
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



}
