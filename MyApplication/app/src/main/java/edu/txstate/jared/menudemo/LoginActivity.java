package edu.txstate.jared.menudemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import edu.txstate.jared.service.AsyncAuth;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LOGINACTIVITY";
    public static final String TOKEN_KEY = "TOKEN_KEY";

    private Button loginButton;
    private EditText emailField;
    private EditText pwdField;
    private TextView linktoRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = (EditText) findViewById(R.id.emailField);
        pwdField = (EditText) findViewById(R.id.pwdField1);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    attemptLogin(v);
                } catch (IOException e) {
                    Log.d(TAG, "IOexception on attempted login");
                    e.printStackTrace();
                }
            }
        });
        linktoRegister = (TextView) findViewById(R.id.linkToRegister);
        linktoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class );
                startActivity(intent);
            }
        });
    }


    /**
     * After login button is pressed, this method attempts to login the user with the server.
     * @param view
     */
    private void attemptLogin(View view) throws IOException {
        Log.d(TAG, "logging in");

        // Send POST request to server
//        AsyncAuth auth = new AsyncAuth();
//        auth.execute();

//        if (!validate()) {
//            onLoginFail();
//            return;
//        }

//
//
//        loginButton.setEnabled(true);
//
//        final ProgressDialog prog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_PopupOverlay);
//        prog.setIndeterminate(true);
//        prog.setMessage("Don't look behind you... ");
//        prog.show();
//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        onLoginSuccess();
//                        prog.dismiss();
//                    }
//                }, 3000);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }


    private boolean validate() {
        boolean valid = true;
        String email = emailField.getText().toString();
        String pwd = pwdField.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.e(TAG, "email is invalid.");
            valid = false;
        } else {
            Log.d(TAG, "email has been validated");
        }
        if (pwd.isEmpty() || pwd.length() < 5 || pwd.length() > 20) {
            Log.e(TAG, "password is invalid");
            valid = false;
        }
        return valid;
    }


    /**
     * Takes an Auth Token and saves it globally to the User's shared preferences
     * @param token auth token retrieved from backend.
     */
    private void saveAuthToken(String token) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(TOKEN_KEY, token);
        editor.commit();
    }


    private void onLoginFail() {
        Log.e(TAG, "Login failed");
        // show some kind of error message
        loginButton.setEnabled(true);
    }

    private void onLoginSuccess() {
        loginButton.setEnabled(true);
        finish();   // activity is finished and can be closed
    }

}
