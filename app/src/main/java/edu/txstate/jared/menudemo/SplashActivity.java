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

    /**
     * Transitions to MainActivity if user is already logged in, else transitions to LoginActivity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.contains(User.AUTH_TOKEN) && settings.contains(User.USERNAME)
                && settings.contains(User.EMAIL)) { // User is logged in. Go to main activity.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        else { // User is not logged in. Go to login screen.
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
