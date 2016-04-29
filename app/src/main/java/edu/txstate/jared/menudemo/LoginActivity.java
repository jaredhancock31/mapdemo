package edu.txstate.jared.menudemo;

import android.content.Intent;
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

import java.io.IOException;

import edu.txstate.jared.service.AsyncAuth;

/**
 * Activity for logging in.
 */
public class LoginActivity extends AppCompatActivity implements AsyncAuth.AsyncResponse{

    public static final String TAG = "LOGINACTIVITY";

    private Button loginButton;
    private EditText usernameField;
    private EditText emailField;
    private EditText pwdField;
    private TextView linktoRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = (EditText) findViewById(R.id.usernameField);
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

        try {
            JSONObject json = new JSONObject();
            json.put("username", usernameField.getText().toString());
            json.put("email", emailField.getText().toString());
            json.put("password", pwdField.getText().toString());

            AsyncAuth authTask = new AsyncAuth(this, this, AsyncAuth.LOGIN);
            authTask.execute(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Determines if user input is valid
     * @return True if valid, else false
     */
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
     * Called automatically after AsyncAuth completes. Processes the result of AsyncAuth.
     * @param success The result returned by AsyncAuth. True if login POST request to server
     *                was successful, else false.
     */
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
