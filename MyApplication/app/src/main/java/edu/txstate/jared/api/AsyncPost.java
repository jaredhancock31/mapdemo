package edu.txstate.jared.api;

import android.os.AsyncTask;
import android.util.Log;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

import edu.txstate.jared.menudemo.DataDrop;

/**
 * This class is a basic Http POST to the server
 */
public class AsyncPost extends AsyncTask {

    private String TAG = "ASYNCPOST";
    private HashMap<String, String> pMap;
    private String paramString;

    public AsyncPost(HashMap<String, String> params) {
        this.pMap = params;
    }

    public AsyncPost (String params) {
        this.paramString = params;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        try {
            URL hostUrl = new URL("http://104.236.181.178");
            HttpURLConnection conn = (HttpURLConnection) hostUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(paramString.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.i(TAG, "Post response Code : " + responseCode );


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
