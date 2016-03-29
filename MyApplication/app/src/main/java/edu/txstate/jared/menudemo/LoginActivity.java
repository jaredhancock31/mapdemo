package edu.txstate.jared.menudemo;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGINACTIVITY";

    private Button loginButton;
    private EditText emailField;
    private EditText pwdField;


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
                attemptLogin(v);
            }
        });
    }


    /**
     * After login button is pressed, this method attempts to login the user with the server.
     * @param view
     */
    private void attemptLogin(View view) {
        Log.d(TAG, "logging in");

        if (!validate()) {
            onLoginFail();
            return;
        }

        loginButton.setEnabled(true);

        final ProgressDialog prog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_PopupOverlay);
        prog.setIndeterminate(true);
        prog.setMessage("Don't look behind you... ");
        prog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        onLoginSuccess();
                        prog.dismiss();
                    }
                }, 3000);
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


    private void onLoginFail() {
        Log.e(TAG, "Login failed");
        // show some kind of error message
        loginButton.setEnabled(true);
    }

    private void onLoginSuccess() {
        loginButton.setEnabled(true);
        finish();
    }

}
