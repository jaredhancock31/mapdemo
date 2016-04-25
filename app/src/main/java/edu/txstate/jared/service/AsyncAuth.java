package edu.txstate.jared.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import edu.txstate.jared.menudemo.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.framed.Header;

/**
 * This class is responsible for logging in or registering to the server and retrieving the
 * Authorization token
 *
 * Created by jared on 3/29/16.
 */
public class AsyncAuth extends AsyncTask<JSONObject, Void, Boolean> {

    public static final String TAG =            "AUTH";
    public static final String TOKEN =          "TOKEN";
    public Boolean authSuccess;
    public Context context;

    public AsyncAuth(Context context) {
        this.context = context;
        authSuccess = false;
    }

    @Override
    protected Boolean doInBackground(JSONObject... params) {
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType, params[0].toString());
            Log.d(TAG, "params: " + String.valueOf(params[0]));

            Request request = new Request.Builder()
                    .url("http://104.236.181.178:8000/rest-auth/registration/")
                    .post(requestBody)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Connection", "keep-alive")
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            Log.d(TAG, "response: " + response.message());
            Log.d(TAG, "response body: " + responseBody);
//            for (String header : response.headers().names()) {
//                Log.d(TAG, response.header(header));
//            }

            if (response.code() < 400) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                saveAuthToken(jsonResponse.getString("key"));
                authSuccess = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authSuccess;
    }


    public void saveAuthToken(String token) {
        String token_key = context.getResources().getString(R.string.auth_token_key);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(token_key, token).apply();
    }

//    @Override
//    protected void onPostExecute(Boolean aBoolean) {
//        super.onPostExecute(aBoolean);
//    }
}
