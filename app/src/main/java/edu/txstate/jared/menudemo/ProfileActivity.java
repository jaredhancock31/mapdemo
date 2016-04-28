package edu.txstate.jared.menudemo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    public TextView usernameText;
    public TextView emailText;

    public ListView dropList;

    public ArrayAdapter<String> adapter;

    private String username;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get username and email
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = settings.getString(User.USERNAME, "no_username");
        email = settings.getString(User.EMAIL, "no_email");

        // Set username and email fields
        usernameText = (TextView) findViewById(R.id.profileUsername);
        usernameText.setText(username);
        emailText = (TextView) findViewById(R.id.profileEmail);
        emailText.setText(email);

        // Populate profile with drops.

        ArrayList<String> testDrops = new ArrayList<>();
        testDrops.add("Drop1");
        testDrops.add("Drop2");
        testDrops.add("Drop3");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, testDrops);

        dropList = (ListView) findViewById(R.id.profileDropListView);
        dropList.setAdapter(adapter);
    }
}
