package edu.txstate.jared.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import edu.txstate.jared.menudemo.LocManager;
import edu.txstate.jared.menudemo.R;


public class MapFragment extends SupportMapFragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LocManager locManager;


    public MapFragment() {}


    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (container == null) {
//            return null;
//        }
//        View view = inflater.inflate(R.layout.activity_map_fragment, container, false);
//        locManager = new LocManager(getContext(), (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE));
//        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
//        mapFragment.getMapAsync(this);
//
//        return view;
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i("MapFragment", "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        locManager = new LocManager(getContext(), (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE));
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // center the camera, set markers, etc
        mGoogleMap = googleMap;
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(locManager.getMyCurrentLatLng()));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }
}
