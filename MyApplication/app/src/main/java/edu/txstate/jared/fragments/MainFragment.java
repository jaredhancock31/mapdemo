package edu.txstate.jared.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import edu.txstate.jared.menudemo.MapsActivity;
import edu.txstate.jared.menudemo.R;


public class MainFragment extends Fragment {

    public static String TAG = "MAINFRAG";

    @Nullable


    //TODO go to mapsActivity
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
//        return super.onCreateView(inflater, container, savedInstanceState);
        ImageButton gotoMapButton = (ImageButton) view.findViewById(R.id.earthButton);
        gotoMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
