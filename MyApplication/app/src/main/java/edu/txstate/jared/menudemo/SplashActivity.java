package edu.txstate.jared.menudemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This activity is the first activity that starts when the App is started. It will check to see
 * whether or not the User is already logged in or not. If they are not, this activity will start
 * up the Login/Register activity. If they are, then the Main activity will be launched.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
