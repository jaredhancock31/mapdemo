package edu.txstate.jared.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import edu.txstate.jared.menudemo.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TextDropFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TextDropFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextDropFragment extends Fragment {

    private static EditText textField;
    private static Button submitButton;
    private OnFragmentInteractionListener mListener;

    public TextDropFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type type of drop (text or media)
     * @return A new instance of fragment TextDropFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TextDropFragment newInstance(String type) {
        TextDropFragment fragment = new TextDropFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, type);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_drop, container, false);

        textField = (EditText) view.findViewById(R.id.dropMessageField);
        submitButton = (Button) view.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        onButtonPressed(v);
                    }
                }
        );
        return view;
    }


    public void onButtonPressed(View view) {
        if (mListener != null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String dropMessage = textField.getText().toString();
            mListener.onDropSubmit(dropMessage, timestamp);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDropSubmit(String dropMessage, Timestamp timestamp);
    }
}
