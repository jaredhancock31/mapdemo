package edu.txstate.jared.api;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by jared on 3/17/16.
 */
public class AsyncGet extends AsyncTask<String, Void, Boolean> {

    private String TAG = "ASYNCGET";
    // default constructor
    public AsyncGet() {}

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            String paramString = params[0];
            URL hostUrl = new URL("http://104.236.181.178");
            HttpURLConnection conn = (HttpURLConnection) hostUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(paramString.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.i(TAG, "Post response Code : " + responseCode);


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
