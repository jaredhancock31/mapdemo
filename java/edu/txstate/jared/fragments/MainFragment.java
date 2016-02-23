package edu.txstate.jared.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.txstate.jared.menudemo.R;


public class MainFragment extends Fragment {
    @Nullable


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
//        return super.onCreateView(inflater, container, savedInstanceState);
        Button gotoMapButton = (Button) view.findViewById(R.id.gotoMapButton);
        gotoMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
            }
        });

        return view;
    }

}
