package edu.txstate.jared.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import edu.txstate.jared.menudemo.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class is responsible for logging in or registering to the server and retrieving the
 * Authorization token
 *
 * Created by jared on 3/29/16.
 */
public class AsyncAuth extends AsyncTask<JSONObject, Void, Boolean> {

    public static final String TAG =            "AUTH";
    public AsyncResponse delegate =              null;
    public Boolean authSuccess;
    public int authAction;
    public Context context;


    public interface AsyncResponse {
        void processResult(boolean success);
    }


    /**
     * Constructor for AsyncAuth.
     * @param context Activity this was called from
     * @param delegate object that has implemented AsyncResponse that will receive the results of
     *                 this task
     * @param authAction toggle between login/registration/logout; 1 if already signed up as a user,
     *                     0 if user has not been registered yet, 2 if user wants to log out.
     */
    public AsyncAuth(Context context, AsyncResponse delegate, int authAction) {
        this.context = context;
        this.delegate = delegate;
        this.authAction = authAction;
        authSuccess = false;
    }


    /**
     * This method is automatically called when AsyncAuth instance.execute() is called from an
     * Activity.
     * @param params request body with User info to be sent to the server for login/registration
     * @return auth success status
     */
    @Override
    protected Boolean doInBackground(JSONObject... params) {
        if (authAction == User.LOGIN) {
            return login(params[0]);
        }
        else if (authAction == User.REGISTER) {
            return register(params[0]);
        }
        else {
            return logout(params[0]);
        }
    }


    @Override
    protected void onPostExecute(Boolean result) {
        delegate.processResult(result);
    }


    public Boolean register(JSONObject params) {
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType, params.toString());
            Log.d(TAG, "params: " + String.valueOf(params));

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

            /* uncomment this loop to log the headers */
//            for (String header : response.headers().names()) {
//                Log.d(TAG, response.header(header));
//            }
            if (response.code() < 400) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                if (jsonResponse.has("key")) {
                    saveUserInfo(jsonResponse.getString("key"), params.getString("username"),
                            params.getString("email"));
                    authSuccess = true;
                }
                else {
                    Log.e(TAG, "Not 400, but 'key' not found in response.");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authSuccess;
    }


    /**
     * Send POST request with login parameters in request body. Save the token when successfully
     * authenticated
     * @param params username, email, password
     * @return auth success
     */
    public Boolean login(JSONObject params) {
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType, params.toString());
            Log.d(TAG, "params: " + String.valueOf(params));

            Request request = new Request.Builder()
                    .url("http://104.236.181.178:8000/rest-auth/login/")
                    .post(requestBody)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Connection", "keep-alive")
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            Log.d(TAG, "response: " + response.message());
            Log.d(TAG, "response body: " + responseBody);

            /* uncomment this loop to log the headers */
//            for (String header : response.headers().names()) {
//                Log.d(TAG, response.header(header));
//            }
            if (response.code() < 400) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                if (jsonResponse.has("key")) {
                    saveUserInfo(jsonResponse.getString("key"), params.getString("username"),
                            params.getString("email"));
                    authSuccess = true;
                }
                else {
                    Log.e(TAG, "Not 400, but 'key' not found in response.");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authSuccess;
    }

    /**
     * Send POST request with logout parameters in request body.
     * @param params token
     * @return auth success
     */
    public Boolean logout(JSONObject params) {
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType, params.toString());
            Log.d(TAG, "params: " + String.valueOf(params));

            Request request = new Request.Builder()
                    .url("http://104.236.181.178:8000/rest-auth/logout/")
                    .post(requestBody)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Connection", "keep-alive")
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            Log.d(TAG, "response: " + response.message());
            Log.d(TAG, "response body: " + responseBody);

            /* uncomment this loop to log the headers */
//            for (String header : response.headers().names()) {
//                Log.d(TAG, response.header(header));
//            }
            if (response.code() < 400) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                Log.d(TAG, "response: " + String.valueOf(jsonResponse));
                deleteUserInfo();
                authSuccess = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authSuccess;
    }

    /*
    Save user's auth token, username, and email in SharedPreferences.
     */
    public void saveUserInfo(String token, String username, String email) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(User.AUTH_TOKEN, token).apply();
        settings.edit().putString(User.USERNAME, username).apply();
        settings.edit().putString(User.EMAIL, email).apply();
    }

    public void deleteUserInfo() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (settings.contains(User.AUTH_TOKEN))
            settings.edit().remove(User.AUTH_TOKEN).apply();
        if (settings.contains(User.USERNAME))
            settings.edit().remove(User.USERNAME).apply();
        if (settings.contains(User.EMAIL))
            settings.edit().remove(User.EMAIL).apply();
    }

}
