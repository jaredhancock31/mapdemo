/**
 * Author: Jared Hancock
 *
 * Texas State University
 * Last Update: Feb 2016
 */

package edu.txstate.jared.artest2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainLog"; // for making filters in logcat
    private Editable username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText usernameBox = (EditText) findViewById(R.id.usernameBox);
        EditText pwdBox = (EditText) findViewById(R.id.pwdBox);
        Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameBox.getText();
                Intent intent1 = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent1); // transitions to a different activity
            }
        });

        Log.i(TAG, "onCreate");
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
