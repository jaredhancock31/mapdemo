package edu.txstate.jared.menudemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import edu.txstate.jared.service.PostService;

/**
 * Activity for creating data drops. Gives the user a field to enter a message and a button to submit.
 */
public class CreateDropletActivity extends AppCompatActivity {

    public static final String TAG = "CREATE_DROPLET_ACTIVITY";

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
        latitude = intent.getDoubleExtra(Droplet.LATITUDE, 0);
        longitude = intent.getDoubleExtra(Droplet.LONGITUDE, 0);

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
     * Passes a droplet to PostService to be submitted to the server, then transitions to
     * MapsActivity.
     * @param v
     */
    private void prepareDroplet(View v) {
        String message = messageField.getText().toString();

        postServiceIntent = new Intent(getApplicationContext(), PostService.class);
        postServiceIntent.putExtra(PostService.METHOD_EXTRA, PostService.METHOD_POST);
        postServiceIntent.putExtra(PostService.LATITUDE_EXTRA, String.valueOf(latitude));
        postServiceIntent.putExtra(PostService.LONGITUDE_EXTRA, String.valueOf(longitude));
        postServiceIntent.putExtra(PostService.DATA_EXTRA, message);

        startService(postServiceIntent);        // start up the POST request service

        // TODO transition based on server response

        // Go back to map.
        Intent mainIntent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(mainIntent);


    }
}
