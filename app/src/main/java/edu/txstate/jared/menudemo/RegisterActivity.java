package edu.txstate.jared.menudemo;

import android.content.Intent;
import android.os.AsyncTask;
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

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "REGISTERACTIVITY";
    public static final String AUTH_FAILED = "AUTH_FAILED";

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
            }
        });
    }


    private void attemptRegister(View view) {
        Log.d(TAG, "attempting to register");
        registerButton.setEnabled(false);
        try {

            JSONObject json = new JSONObject();
            json.put("username", usernameField.getText().toString());
            json.put("email", emailField.getText().toString());
            json.put("password1", pwdField.getText().toString());
            json.put("password2", pwdField.getText().toString());

            AsyncAuth auth = new AsyncAuth(this);
            auth.execute(json);
//            while(auth.getStatus().equals(AsyncTask.Status.RUNNING)) {
//                registerButton.setEnabled(false);
//            }

            // TODO get the result of the asyncAuth, react appropriately
            onRegisterSuccess();

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


    private void onRegisterSuccess() {
        Log.d(TAG, "registration success");
//        registerButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(intent);          /* go to main menu */
        finish();                       // activity is finished and can be taken off stack
    }

    private void onRegisterFailed() {
        Log.e(TAG, "registration failed");
        registerButton.setEnabled(true);
    }
}
