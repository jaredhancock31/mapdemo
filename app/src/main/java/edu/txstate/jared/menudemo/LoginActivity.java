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

import org.json.JSONException;
import org.json.JSONObject;

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

public class LoginActivity extends AppCompatActivity implements AsyncAuth.AsyncResponse{

    public static final String TAG = "LOGINACTIVITY";

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
                finish();
            }
        });
    }


    /**
     * After login button is pressed, this method attempts to login the user with the server.
     * @param view
     */
    private void attemptLogin(View view) throws IOException {
        Log.d(TAG, "logging in");

        // get username from settings
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String username = settings.getString(User.USERNAME, "jared");

        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("email", emailField.getText().toString());
            json.put("password", pwdField.getText().toString());

            AsyncAuth authTask = new AsyncAuth(this, this, true);
            authTask.execute(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    @Override
    public void processResult(boolean success) {
        if (success) {
            Log.d(TAG, "login success");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);          /* go to main menu */
            finish();                       // activity is finished and can be taken off stack
        }
        else {
            Log.d(TAG, "login failed");
            loginButton.setEnabled(true);
            pwdField.setText("");           // reset pwd field
        }
    }
}
