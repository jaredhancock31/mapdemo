package edu.txstate.jared.menudemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This activity is the first activity that starts when the App is started. It will check to see
 * whether or not the User is already logged in or not. If they are not, this activity will start
 * up the Login/Register activity. If they are, then the Main activity will be launched.
 */
public class SplashActivity extends AppCompatActivity {

    public static final String TAG = "SPLASHACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = settings.getString(User.USERNAME, "jared");
        Log.d(TAG, username);


        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
