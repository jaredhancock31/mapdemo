package edu.txstate.jared.menudemo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    public TextView usernameText;

    private String username;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get username and email
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = settings.getString(User.USERNAME, "no_username");

        usernameText = (TextView) findViewById(R.id.profileUsername);
        usernameText.setText(username);
    }
}
