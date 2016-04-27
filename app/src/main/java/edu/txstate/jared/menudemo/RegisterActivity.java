package edu.txstate.jared.menudemo;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import edu.txstate.jared.service.AsyncAuth;

public class RegisterActivity extends AppCompatActivity implements AsyncAuth.AsyncResponse {

    public static final String TAG =                    "REGISTERACTIVITY";
    public static final String AUTH_FAILED =            "AUTH_FAILED";


    private Button registerButton;
    private EditText emailField;
    private EditText usernameField;
    private EditText pwdField;
    private TextView linkToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailField = (EditText) findViewById(R.id.emailField);
        usernameField = (EditText) findViewById(R.id.usernameField);
        pwdField = (EditText) findViewById(R.id.pwdField1);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister(v);
            }
        });
        linkToLogin = (TextView) findViewById(R.id.linkToLogin);
        linkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //DEBUG
//        SharedPreferences settings = getApplicationContext().getSharedPreferences("geoPrefs", 0);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = settings.getString(User.USERNAME, "asdf");
        Log.d(TAG, username);
    }


    // TODO make 2nd pwd field and validate everything
    private void attemptRegister(View view) {
        Log.d(TAG, "starting authTask");
        registerButton.setEnabled(false);
        try {
            JSONObject json = new JSONObject();
            json.put("username", usernameField.getText().toString());
            json.put("email", emailField.getText().toString());
            json.put("password1", pwdField.getText().toString());
            json.put("password2", pwdField.getText().toString());

            AsyncAuth authTask = new AsyncAuth(this, this, false);
            authTask.execute(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private boolean validate() {
        Log.d(TAG, "validating entries");
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
     * Catches the result of the AsyncAuth instance. If registration was successful, advance the user
     * to the MainActivity view.
     * @param success result of authentication attempt
     */
    @Override
    public void processResult(boolean success) {
        if (success) {
            Log.d(TAG, "registration success");
            saveUserInfo();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);          /* go to main menu */
            finish();                       // activity is finished and can be taken off stack
        }
        else {
            Log.d(TAG, "registration failed");
            registerButton.setEnabled(true);
            pwdField.setText("");           // reset pwd field
        }
    }


    private void saveUserInfo() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences settings = getApplicationContext().getSharedPreferences("geoPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(User.USERNAME, usernameField.getText().toString());
//        editor.putString(User.EMAIL, emailField.getText().toString());
        editor.commit();

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = settings.getString(User.USERNAME, "asdf");
        Log.d(TAG, username);
    }
}
