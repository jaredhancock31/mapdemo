package edu.txstate.jared.menudemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.preference.PreferenceManager;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.txstate.jared.service.AsyncAuth;


public class MainActivity extends AppCompatActivity implements AsyncAuth.AsyncResponse {

    public Button gotoMapButton;
    public Button logoutButton;
    public Button gotoProfileButton;

    public static final String TAG = "MAINACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gotoMapButton = (Button) findViewById(R.id.mapButton);
        gotoMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        gotoProfileButton = (Button) findViewById(R.id.profileButton);
        gotoProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Profile button clicked.");
            }
        });

        logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Logout button clicked.");
                try {
                    attemptLogout(v);
                } catch (IOException e) {
                    Log.d(TAG, "IOexception on attempted logout");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void attemptLogout(View view) throws IOException {
        Log.d(TAG, "logging out");

        // get auth token from settings
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String token = settings.getString(User.AUTH_TOKEN, "no_token");

        try {
            JSONObject json = new JSONObject();
            json.put("token", token);

            AsyncAuth authTask = new AsyncAuth(this, this, User.LOGOUT);
            authTask.execute(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResult(boolean success) {
        if (success) {
            Log.d(TAG, "logout success");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);          /* go to login screen */
            finish();                       // activity is finished and can be taken off stack
        }
        else {
            Log.d(TAG, "logout failed");
        }
    }
}
