package edu.txstate.jared.menudemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "REGISTERACTIVITY";

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

    }


    private void attemptRegister(View view) {
        Log.d(TAG, "attempting to register");
        //TODO register logic
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
        registerButton.setEnabled(true);
    }

    private void onRegisterFailed() {
        Log.e(TAG, "registration failed");
        registerButton.setEnabled(true);
    }
}
