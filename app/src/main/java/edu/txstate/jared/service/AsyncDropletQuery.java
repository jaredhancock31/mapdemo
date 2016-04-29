package edu.txstate.jared.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;

import edu.txstate.jared.menudemo.Droplet;
import edu.txstate.jared.menudemo.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by John2014 on 4/28/16.
 *
 * Queries the server for a list of droplets created by the logged-in user.
 */
public class AsyncDropletQuery extends AsyncTask<Void, Void, ArrayList<Droplet>> {

    private static final String TAG = "ASYNCDROPLETQUERY";

    public AsyncResponse delegate;
    public Context context;

    public interface AsyncResponse {
        void processResult(ArrayList<Droplet> result);
    }

    /**
     * Constructor for AsyncDropletQuery.
     * @param context Activity this was called from
     * @param delegate object that has implemented AsyncResponse that will receive the results of
     *                 this task
     */
    public AsyncDropletQuery(Context context, AsyncResponse delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<Droplet> doInBackground(Void... Params) {
        ArrayList<Droplet> droplets = null;

        try {

            Log.d(TAG, "starting GET request");

            // Get logged-in user's auth token.
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            String token = settings.getString(User.AUTH_TOKEN, "no_token");

            OkHttpClient client = new OkHttpClient();

            // Build GET request
            Request request = new Request.Builder()
                    .url("http://104.236.181.178:8000/profile/")
                    .addHeader("authorization", "Token " + token)
                    .addHeader("cache-control", "no-cache")
                    .build();

            // Send GET request
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            Log.d(TAG, "response: " + response.message());
            Log.d(TAG, "response body: " + responseBody);

            // Parse response string into an ArrayList of Droplet objects
            droplets = parseJSON(responseBody);

            /* uncomment this loop to log the headers */
//            for (String header : response.headers().names()) {
//                Log.d(TAG, response.header(header));
//            }
            if (response.code() < 400) {
                Log.d(TAG, "GET request succeeded");
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return droplets;
    }

    @Override
    protected void onPostExecute(ArrayList<Droplet> result) {
        delegate.processResult(result);
    }

    /**
     * Parses JSON data from string into an ArrayList of Droplet object
     * @param response A string containing JSON data
     * @return ArrayList of Droplet objects
     * @throws JSONException
     */
    public ArrayList<Droplet> parseJSON(String response) throws JSONException {
        JSONArray jsonArray = new JSONArray(response);
        ArrayList<Droplet> dropList = new ArrayList<>();     // pass this to Map to be drawn

        for (int i = 0; i < jsonArray.length(); i++ ) {
            JSONObject json = jsonArray.getJSONObject(i);
            String owner = json.getString(Droplet.OWNER);
            Double latitude = json.getDouble(Droplet.LATITUDE);
            Double longitude = json.getDouble(Droplet.LONGITUDE);
            String data = json.getString(Droplet.DATA);

            /* create found droplet instance and add to list */
            Droplet droplet = new Droplet(owner, latitude, longitude, data);
            dropList.add(droplet);
        }
        return dropList;
    }

}
