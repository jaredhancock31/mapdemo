package edu.txstate.jared.menudemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import edu.txstate.jared.service.PostService;

public class CreateDropletActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText messageField;
    private Button submitButton;
    private Double latitude = 0.0;
    private Double longitude = 0.0;

    private Intent postServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_droplet);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra(MapsActivity.LATITUDE, 0);
        longitude = intent.getDoubleExtra(MapsActivity.LONGITUDE, 0);

        usernameField = (EditText) findViewById(R.id.usernameForm);
        messageField = (EditText) findViewById(R.id.messageForm);
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareDroplet(v);
            }
        });

    }


    /**
     *
     * @param v
     */
    private void prepareDroplet(View v) {
        String username = usernameField.getText().toString();
        String message = messageField.getText().toString();
        Droplet droplet = new Droplet(username, latitude, longitude, message);
        String paramString = droplet.getParamString();

        postServiceIntent = new Intent(getApplicationContext(), PostService.class);
        postServiceIntent.putExtra(PostService.PARAMS_EXTRA, paramString);


    }
}